// (C) 2019 uchicom
package com.uchicom.tracker;

import com.uchicom.tracker.factory.JsFactory;
import com.uchicom.tracker.factory.di.DIFactory;
import com.uchicom.tracker.servlet.ApiServlet;
import com.uchicom.zouni.ZouniParameter;
import com.uchicom.zouni.ZouniProcess;
import com.uchicom.zouni.dto.IpErrorMessageKey;
import com.uchicom.zouni.servlet.JsServlet;
import com.uchicom.zouni.servlet.RootServlet;
import jakarta.servlet.Servlet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static Main main = DIFactory.main();
  private static ConcurrentHashMap<IpErrorMessageKey, AtomicInteger> ipErrorMessageCountMap =
      new ConcurrentHashMap<>();

  public static void main(String[] args) {

    var zouniParameter = new ZouniParameter(args);
    try {
      main.execute(zouniParameter);
    } catch (Exception e) {
      main.logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  public static void shutdown() {
    Context.close();
  }

  private final ApiServlet apiServlet;
  private final JsFactory jsFactory;

  private final Logger logger;

  public Main(ApiServlet apiServlet, JsFactory jsFactory, Logger logger) {

    this.apiServlet = apiServlet;
    this.jsFactory = jsFactory;
    this.logger = logger;
  }

  void execute(ZouniParameter zouniParameter) throws Exception {
    var servlet = new RootServlet(logger, "./www/", "tracker/user.htm");
    var map = new HashMap<String, Servlet>();
    map.put("pub./tracker/user/", servlet);
    map.put("pub./tracker/js/validation.js", new JsServlet(jsFactory::createValidationPage));
    var startWithMap = new HashMap<String, Servlet>();
    startWithMap.put("pub./tracker/user/", servlet);
    startWithMap.put("pub./tracker/api/", apiServlet);
    zouniParameter.put("public", "./www");
    zouniParameter.put("type", "multi");
    zouniParameter
        .createServer(
            (parameter, socket) -> {
              return new ZouniProcess(
                  parameter, socket, map, startWithMap, logger, ipErrorMessageCountMap);
            })
        .execute();
  }
}
