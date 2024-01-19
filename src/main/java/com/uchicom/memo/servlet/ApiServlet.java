// (C) 2024 uchicom
package com.uchicom.memo.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uchicom.memo.annotation.Auth;
import com.uchicom.memo.annotation.Path;
import com.uchicom.memo.api.AbstractApi;
import com.uchicom.memo.api.AccountApi;
import com.uchicom.memo.api.MemoApi;
import com.uchicom.memo.dto.response.account.AuthDto;
import com.uchicom.memo.exception.ViolationException;
import com.uchicom.memo.util.AuthUtil;
import com.uchicom.memo.util.ClassUtil;
import com.uchicom.memo.util.ThrowingBiFunction;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class ApiServlet extends HttpServlet {

  Map<String, ThrowingBiFunction<HttpServletRequest, HttpServletResponse, String, Exception>> map =
      new HashMap<>();

  private final Map<Class<? extends AbstractApi>, AbstractApi> apiClassMap = new HashMap<>();

  private final ObjectMapper objectMapper;

  private final Validator validator;

  private final Logger logger;

  @Inject
  public ApiServlet(
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
    apiPackage("com.uchicom.memo.api");
  }

  void register(AbstractApi api) {
    apiClassMap.put(api.getClass(), api);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      var function = map.get(req.getPathInfo());
      var result = function.apply(req, res);
      if (result != null) {
        res.getOutputStream().write(result.toString().getBytes());
      }
    } catch (Exception exception) {
      logger.log(Level.SEVERE, exception.getMessage(), exception);
    }
  }

  void apiPackage(String packageName) {
    var classSet = ClassUtil.<AbstractApi>listClasses(packageName);
    for (var clazz : classSet) {
      var path = clazz.getAnnotation(Path.class);
      if (path == null) continue;
      var p = path.value();
      var methods = clazz.getMethods();
      for (Method method : methods) {
        var methodPath = method.getAnnotation(Path.class);
        if (methodPath == null) continue;
        map.put(
            p + methodPath.value(),
            (req, res) -> {
              return execute(clazz, method, req, res);
            });
      }
    }
  }

  String execute(
      Class<AbstractApi> apiClass, Method method, HttpServletRequest req, HttpServletResponse res)
      throws Exception {

    logger.info(req.getHeader("User-Agent") + "@" + req.getRemoteAddr() + ":" + req.getPathInfo());
    var api = apiClassMap.get(apiClass);

    try {
      String url = null;
      Auth auth = method.getAnnotation(Auth.class);
      res.setHeader("Cache-Control", "no-store");
      if (auth != null && !AuthUtil.auth(req)) {
        res.setContentType("application/json; charset=UTF-8");
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
        res.setContentType("application/json; charset=UTF-8");
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
      logger.log(Level.SEVERE, "シェアオフィスエラー通知:" + e.getMessage(), e);
      throw new ServletException(e);
    }
  }

  <T, U> T readValue(HttpServletRequest req, Class<T> clazz, Type type) throws Exception {
    T requestDto =
        objectMapper.readValue(
            req.getInputStream(),
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
}
