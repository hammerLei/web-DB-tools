package com.dbTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author lanxu
 * @email xu.lan@renren-inc.com
 * @create 2017年07月19 13:21
 **/
@Component
public class WebServerConfig implements
  ApplicationListener<EmbeddedServletContainerInitializedEvent> {

  private static Logger logger = LoggerFactory.getLogger(WebServerConfig.class);

  @Override
  public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
    int port = event.getEmbeddedServletContainer().getPort();
    System.setProperty("serverAddress", getHost() + ":" + port);
    System.setProperty("serverHost", getHost());
    System.setProperty("serverPort", port + "");
  }

  private String getHost() {
    String host = null;
    try {
      host = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      logger.error("get server host Exception e:", e);
    }
    return host;
  }
}