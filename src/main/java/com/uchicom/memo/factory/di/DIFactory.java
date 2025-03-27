// (C) 2025 uchicom
package com.uchicom.memo.factory.di;

import com.uchicom.memo.Main;
import com.uchicom.memo.factory.JsFactory;
import com.uchicom.memo.factory.ServerConnectorFactory;
import com.uchicom.memo.logging.DailyRollingFileHandler;
import com.uchicom.memo.servlet.ApiServlet;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class DIFactory {

  public static Main main() {
    return new Main(apiServlet(), serverConnectorFactory(), jsFactory(), logger());
  }

  static ApiServlet apiServlet() {
    return new ApiServlet(
        ApiFactory.accountApi(),
        ApiFactory.memoApi(),
        ServiceFactory.authService(),
        ServiceFactory.objectMapper(),
        validator(),
        logger());
  }

  static ServerConnectorFactory serverConnectorFactory() {
    return new ServerConnectorFactory();
  }

  static JsFactory jsFactory() {
    return new JsFactory();
  }

  static Validator validator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    return factory.getValidator();
  }

  public static Logger logger() {

    try {
      var PROJECT_NAME = "anko";
      var name =
          Stream.of(Thread.currentThread().getStackTrace())
              .map(StackTraceElement::getClassName)
              .filter(className -> className.endsWith("Main"))
              .findFirst()
              .orElse(PROJECT_NAME);
      Logger logger = Logger.getLogger(name);
      if (!PROJECT_NAME.equals(name)) {
        if (Arrays.stream(logger.getHandlers())
            .filter(handler -> handler instanceof DailyRollingFileHandler)
            .findFirst()
            .isEmpty()) {
          logger.addHandler(new DailyRollingFileHandler(name + "_%d.log"));
        }
      }
      return logger;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
