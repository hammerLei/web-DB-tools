package com.dbTool.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public final class ApiConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {
  private static final Log LOGGER = LogFactory.getLog(ApiConnectionKeepAliveStrategy.class);
  private static final long DEFAULT_KEEP_ALIVE_TIME = 30000L;
  public static final ApiConnectionKeepAliveStrategy INSTANCE = new ApiConnectionKeepAliveStrategy();

  private ApiConnectionKeepAliveStrategy() {
  }

  public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
    Args.notNull(response, "HTTP response");
    HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
    long duration = 30000L;

    while(it.hasNext()) {
      HeaderElement he = it.nextElement();
      String param = he.getName();
      String value = he.getValue();
      if (value != null && param.equalsIgnoreCase("timeout")) {
        try {
          duration = Long.parseLong(value) * 1000L;
        } catch (NumberFormatException var10) {
          LOGGER.error("parse timeout failed", var10);
        }
      }
    }

    return duration;
  }
}
