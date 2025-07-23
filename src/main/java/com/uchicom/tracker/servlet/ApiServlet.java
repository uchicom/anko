// (C) 2024 uchicom
package com.uchicom.tracker.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uchicom.tracker.annotation.Auth;
import com.uchicom.tracker.annotation.Path;
import com.uchicom.tracker.api.AbstractApi;
import com.uchicom.tracker.api.AccountApi;
import com.uchicom.tracker.api.IssueApi;
import com.uchicom.tracker.dto.response.account.AuthDto;
import com.uchicom.tracker.exception.ViolationException;
import com.uchicom.tracker.service.AuthService;
import com.uchicom.tracker.util.ThrowingBiFunction;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiServlet extends HttpServlet {

  Map<String, ThrowingBiFunction<HttpServletRequest, HttpServletResponse, String, Exception>> map =
      new HashMap<>();

  private final AuthService authService;

  private final ObjectMapper objectMapper;

  private final Validator validator;

  private final Logger logger;

  public ApiServlet(
      AccountApi accountApi,
      IssueApi issueApi,
      AuthService authService,
      ObjectMapper objectMapper,
      Validator validator,
      Logger logger) {
    register(accountApi);
    register(issueApi);
    this.authService = authService;
    this.objectMapper = objectMapper;
    this.validator = validator;
    this.logger = logger;
  }

  void register(AbstractApi api) {
    var clazz = api.getClass();
    var path = clazz.getAnnotation(Path.class);
    if (path == null) return;
    var p = path.value();
    var methods = clazz.getMethods();
    for (Method method : methods) {
      var methodPath = method.getAnnotation(Path.class);
      if (methodPath == null) continue;
      map.put(
          p + methodPath.value(),
          (req, res) -> {
            return execute(api, method, req, res);
          });
    }
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      var function = map.get(req.getPathInfo());
      if (function == null) {
        res.setStatus(404);
        return;
      }
      var result = function.apply(req, res);
      if (result != null) {
        res.getOutputStream().write(result.getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception exception) {
      logger.log(Level.SEVERE, exception.getMessage(), exception);
    }
  }

  String execute(AbstractApi api, Method method, HttpServletRequest req, HttpServletResponse res)
      throws Exception {

    logger.info(req.getHeader("User-Agent") + "@" + req.getRemoteAddr() + ":" + req.getPathInfo());

    try {
      String url = null;
      Auth auth = method.getAnnotation(Auth.class);
      res.setHeader("Cache-Control", "no-store");
      if (auth != null && !authService.auth(req)) {
        res.setContentType("application/json; charset=UTF-8");
        return objectMapper.writeValueAsString(new AuthDto("認証情報の有効期限が切れました。ログインしなおしてください。"));
      }

      var types = method.getGenericParameterTypes();
      var parameterTypes = method.getParameterTypes();

      if (String.class == method.getReturnType()) { // Stringの場合はurl
        return switch (parameterTypes.length) {
          case 1 -> (String) method.invoke(api, readValue(req, parameterTypes[0], types[0]));
          case 2 -> (String) method.invoke(api, req, res);
          case 3 -> (String)
              method.invoke(api, readValue(req, parameterTypes[0], types[0]), req, res);
          default -> null;
        };
      } else if (Object.class == method.getReturnType()) { // Objectの場合はJSON
        res.setContentType("application/json; charset=UTF-8");
        return switch (parameterTypes.length) {
          case 1 -> objectMapper.writeValueAsString(
              method.invoke(api, readValue(req, parameterTypes[0], types[0])));
          case 2 -> objectMapper.writeValueAsString(method.invoke(api, req, res));
          case 3 -> objectMapper.writeValueAsString(
              method.invoke(api, readValue(req, parameterTypes[0], types[0]), req, res));
          default -> null;
        };
      } else { // 戻りがない場合は内部処理で書き込み
        switch (parameterTypes.length) {
          case 1 -> method.invoke(api, readValue(req, parameterTypes[0], types[0]));
          case 2 -> method.invoke(api, req, res);
          case 3 -> method.invoke(api, readValue(req, parameterTypes[0], types[0]), req, res);
        }
      }
      return url;
    } catch (ViolationException e) {
      return objectMapper.writeValueAsString(e.getErrorDto());
    } catch (Exception e) {
      logger.log(Level.SEVERE, "メモエラー通知:" + e.getMessage(), e);
      throw new ServletException(e);
    }
  }

  <T> T readValue(HttpServletRequest req, Class<T> clazz, Type type) throws Exception {
    var length = req.getContentLength();
    var bytes =
        length == -1
            ? req.getInputStream().readAllBytes()
            : readBytes(req.getInputStream(), length);
    T requestDto =
        objectMapper.readValue(
            bytes,
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

  byte[] readBytes(InputStream is, int length) throws IOException {
    var bytes = new byte[length];
    var offset = 0;
    var result = 0;
    while ((result = is.read(bytes, offset, length - offset)) > 0) {
      offset += result;
      if (offset == length) {
        break;
      }
    }
    return bytes;
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
