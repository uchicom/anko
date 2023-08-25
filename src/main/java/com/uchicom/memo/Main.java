// (C) 2019 uchicom
package com.uchicom.memo;

import static spark.Spark.exception;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.internalServerError;
import static spark.Spark.notFound;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uchicom.memo.annotation.Auth;
import com.uchicom.memo.annotation.Path;
import com.uchicom.memo.api.AbstractApi;
import com.uchicom.memo.api.AccountApi;
import com.uchicom.memo.api.MemoApi;
import com.uchicom.memo.dto.response.account.AuthDto;
import com.uchicom.memo.exception.ViolationException;
import com.uchicom.memo.module.MainModule;
import com.uchicom.memo.util.AuthUtil;
import com.uchicom.memo.util.ClassUtil;
import com.uchicom.memo.util.ValidateUtil;
import com.uchicom.util.Parameter;
import dagger.Component;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import spark.Request;
import spark.Response;

public class Main {

  private static Main main = DaggerMain_MainComponent.create().main();

  @Component(modules = MainModule.class)
  interface MainComponent {
    Main main();
  }

  public static void main(String[] args) {

    try {
      // SSL
      Parameter parameter = new Parameter(args);
      if (parameter.is("ssl")) {
        secure(parameter.get("keyStoreName"), parameter.get("keyStorePass"), null, null);
      }
      if (!parameter.is("port")) {
        parameter.put("port", "8080");
      }
      port(parameter.getInt("port"));

      exception(
          Exception.class,
          (exception, request, response) -> {
            // Handle the exception here
            main.logger.log(Level.SEVERE, exception.getMessage(), exception);
          });
      File file = new File("./www");
      main.logger.info(file.getCanonicalPath());
      externalStaticFileLocation(file.getCanonicalPath());
      initExceptionHandler((e) -> main.logger.warning(e.getMessage()));
      notFound(
          (req, res) -> {
            return "<html><body><h1>Custom 404 handling</h1></body></html>";
          });
      internalServerError("<html><body><h1>Custom 500 handling</h1></body></html>");

      get("/memo/js/validation.js", (req, res) -> createJs(res, createValidationPage()));
      apiPackage("com.uchicom.memo.api", "/memo/api");

      get(
          "/memo/user/*",
          (req, res) -> {
            main.logger.info(req.userAgent() + "@" + req.ip() + ":" + req.url());
            return readFile(file, "memo/user.htm");
          });
    } catch (Exception e) {
      main.logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  static void apiPackage(String packageName, String basePath) {
    path(
        basePath,
        () -> {
          var classSet = ClassUtil.<AbstractApi>listClasses(packageName);
          for (var clazz : classSet) {
            var path = clazz.getAnnotation(Path.class);
            if (path == null) continue;

            path(
                path.value(),
                () -> {
                  var methods = clazz.getMethods();
                  for (Method method : methods) {
                    var methodPath = method.getAnnotation(Path.class);
                    if (methodPath == null) continue;
                    post(
                        methodPath.value(),
                        (req, res) -> {
                          return main.execute(clazz, method, req, res);
                        });
                  }
                });
          }
        });
  }

  static String readFile(File baseFile, String path) throws IOException {
    return new String(
        Files.readAllBytes(new File(baseFile, path).toPath()), StandardCharsets.UTF_8);
  }

  public static void shutdown() {
    Context.close();
  }

  private final Map<Class<? extends AbstractApi>, AbstractApi> apiClassMap = new HashMap<>();

  private final ObjectMapper objectMapper;

  private final Validator validator;

  private final Logger logger;

  @Inject
  public Main(
      AccountApi accountApi,
      MemoApi memoApi,
      ObjectMapper objectMapper,
      Validator validator,
      Logger logger) {

    register(accountApi);
    register(memoApi);
    this.objectMapper = objectMapper;
    this.validator = validator;
    this.logger = logger;
  }

  void register(AbstractApi api) {
    apiClassMap.put(api.getClass(), api);
  }

  String execute(Class<AbstractApi> apiClass, Method method, Request req, Response res)
      throws Exception {

    logger.info(req.userAgent() + "@" + req.ip() + ":" + req.url());
    var api = apiClassMap.get(apiClass);

    try {
      String url = null;
      Auth auth = method.getAnnotation(Auth.class);
      if (auth != null && !AuthUtil.auth(req)) {
        res.type("application/json; charset=UTF-8");
        return objectMapper.writeValueAsString(new AuthDto("認証情報の有効期限が切れました。ログインしなおしてください。"));
      }

      var types = method.getGenericParameterTypes();
      var parameterTypes = method.getParameterTypes();

      if (String.class == method.getReturnType()) { // Stringの場合はurl
        switch (parameterTypes.length) {
          case 1:
            url = (String) method.invoke(api, readValue(req, parameterTypes[0], types[0]));
            break;
          case 2:
            url = (String) method.invoke(api, req, res);
            break;
          case 3:
            url =
                (String) method.invoke(api, readValue(req, parameterTypes[0], types[0]), req, res);
            break;
        }
      } else if (Object.class == method.getReturnType()) { // Objectの場合はJSON
        res.type("application/json; charset=UTF-8");
        switch (parameterTypes.length) {
          case 1:
            return objectMapper.writeValueAsString(
                method.invoke(api, readValue(req, parameterTypes[0], types[0])));
          case 2:
            return objectMapper.writeValueAsString(method.invoke(api, req, res));
          case 3:
            return objectMapper.writeValueAsString(
                method.invoke(api, readValue(req, parameterTypes[0], types[0]), req, res));
        }
      } else { // 戻りがない場合は内部処理で書き込み
        switch (parameterTypes.length) {
          case 1:
            method.invoke(api, readValue(req, parameterTypes[0], types[0]));
            break;
          case 2:
            method.invoke(api, req, res);
            break;
          case 3:
            method.invoke(api, readValue(req, parameterTypes[0], types[0]), req, res);
            break;
        }
      }
      return url;
    } catch (ViolationException e) {
      return objectMapper.writeValueAsString(e.getErrorDto());
    } catch (Exception e) {
      logger.log(Level.SEVERE, "エラー通知:" + e.getMessage(), e);
      throw new ServletException(e);
    }
  }

  <T, U> T readValue(Request req, Class<T> clazz, Type type) throws Exception {
    T requestDto =
        objectMapper.readValue(
            req.raw().getInputStream(),
            new TypeReference<T>() {
              @Override
              public Type getType() {
                return type;
              }
            });

    if (requestDto instanceof List<?> list) {
      validate(list);
    } else {
      validate(requestDto);
    }
    return requestDto;
  }

  <T> void validate(T requestDto) throws ViolationException {
    var violationSet = validator.validate(requestDto);
    if (!violationSet.isEmpty()) {
      throw ViolationException.create(violationSet);
    }
  }

  <T> void validate(List<T> dtoList) throws ViolationException {
    Map<Integer, Set<ConstraintViolation<T>>> violationMap = new HashMap<>();
    int length = dtoList.size();
    for (int index = 0; index < length; index++) {
      var dto = dtoList.get(index);
      var violationSet = validator.validate(dto);
      if (!violationSet.isEmpty()) {
        violationMap.put(index, violationSet);
      }
    }
    if (!violationMap.isEmpty()) {
      throw ViolationException.create(violationMap);
    }
  }

  static String createJs(Response res, String body) {
    res.header("Content-Type", "application/javascript; charset=UTF-8");
    res.header("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    return body;
  }

  static String createValidationPage() {
    var builder = new StringBuilder(1024);
    builder.append(
        """
        const validationHash='$hash';
        if (validationHash != localStorage.getItem('validationHash')) {
          localStorage.setItem('validationHash', validationHash);
          localStorage.setItem('validation', JSON.stringify($validation));
        }
        """);
    var json = ValidateUtil.createJsonForAllRequest();
    return builder
        .toString()
        .replace("$hash", ValidateUtil.createValidateHashCode(json))
        .replace("$validation", json);
  }
}
