// (C) 2024 uchicom
package com.uchicom.tracker.factory;

import com.uchicom.util.Parameter;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class ServerConnectorFactory {

  public ServerConnectorFactory() {}

  public ServerConnector createServerConnector(Server server, Parameter parameter) {
    if (parameter.is("ssl")) {
      return createSslServerConnector(
          server, parameter.get("keyStoreName"), parameter.get("keyStorePass"));
    } else {
      return creaServerConnector(server);
    }
  }

  ServerConnector createSslServerConnector(
      Server server, String keyStorePath, String keyStorePassword) {
    var httpConfig = createHttpConfiguration();
    httpConfig.addCustomizer(new SecureRequestCustomizer());

    var http11 = new HttpConnectionFactory(httpConfig);

    var sslContextFactory = new SslContextFactory.Server();
    sslContextFactory.setKeyStorePath(keyStorePath);
    sslContextFactory.setKeyStorePassword(keyStorePassword);

    var tls = new SslConnectionFactory(sslContextFactory, http11.getProtocol());

    return new ServerConnector(server, 1, 1, tls, http11);
  }

  ServerConnector creaServerConnector(Server server) {
    var httpConfig = createHttpConfiguration();
    return new ServerConnector(server, 1, 1, new HttpConnectionFactory(httpConfig));
  }

  HttpConfiguration createHttpConfiguration() {
    var httpConfig = new HttpConfiguration();
    httpConfig.setSendServerVersion(false);
    return httpConfig;
  }
}
