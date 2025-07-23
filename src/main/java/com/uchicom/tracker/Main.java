// (C) 2019 uchicom
package com.uchicom.tracker;

import com.uchicom.tracker.factory.JsFactory;
import com.uchicom.tracker.factory.ServerConnectorFactory;
import com.uchicom.tracker.factory.di.DIFactory;
import com.uchicom.tracker.servlet.ApiServlet;
import com.uchicom.tracker.servlet.JsServlet;
import com.uchicom.tracker.servlet.RootServlet;
import com.uchicom.util.Parameter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;

public class Main {

  private static Main main = DIFactory.main();

  static Server server;

  public static void main(String[] args) {

    Parameter parameter = new Parameter(args);
    try {
      server = main.execute(parameter);
    } catch (Exception e) {
      main.logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  public static void shutdown() {
    try {
      server.stop();
    } catch (Exception e) {
      main.logger.log(Level.SEVERE, e.getMessage(), e);
    }
    Context.close();
  }

  private final ApiServlet apiServlet;
  private final ServerConnectorFactory serverConnectorFactory;
  private final JsFactory jsFactory;

  private final Logger logger;

  public Main(
      ApiServlet apiServlet,
      ServerConnectorFactory serverConnectorFactory,
      JsFactory jsFactory,
      Logger logger) {

    this.apiServlet = apiServlet;
    this.serverConnectorFactory = serverConnectorFactory;
    this.jsFactory = jsFactory;
    this.logger = logger;
  }

  Server execute(Parameter parameter) throws Exception {

    var server = new Server();
    var connector = serverConnectorFactory.createServerConnector(server, parameter);
    connector.setPort(parameter.getInt("port", 8080));
    server.addConnector(connector);

    var rootDir = "./www/";
    var handler = new ResourceHandler();
    handler.setBaseResource(ResourceFactory.of(handler).newResource(rootDir));
    handler.setDirAllowed(false);
    handler.setCacheControl("no-store");
    handler.setWelcomeFiles(List.of("index.html"));
    handler.setAcceptRanges(true);
    server.setHandler(handler);

    var context = new ServletContextHandler();
    context.setContextPath("/tracker");
    handler.setHandler(context);
    context.addServlet(apiServlet, "/api/*");
    context
        .addServlet(new JsServlet(jsFactory::createValidationPage), "/js/validation.js")
        .setAsyncSupported(true);
    context.addServlet(new RootServlet(logger, rootDir, "tracker/user.htm"), "/user/*");

    server.start();
    return server;
  }
}
