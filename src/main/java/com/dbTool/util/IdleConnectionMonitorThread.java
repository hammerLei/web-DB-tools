package com.dbTool.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.concurrent.TimeUnit;
import org.apache.http.conn.HttpClientConnectionManager;

public final class IdleConnectionMonitorThread extends Thread {
  private final HttpClientConnectionManager connMgr;
  private volatile boolean shutdown = false;

  protected IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
    this.connMgr = connMgr;
    this.setDaemon(true);
  }

  public void run() {
    try {
      synchronized(this) {
        while(true) {
          if (this.shutdown) {
            break;
          }

          this.wait(5000L);
          this.connMgr.closeExpiredConnections();
          this.connMgr.closeIdleConnections(30L, TimeUnit.SECONDS);
        }
      }
    } catch (InterruptedException var4) {
    }

  }

  public void shutdown() {
    synchronized(this) {
      this.shutdown = true;
      this.notifyAll();
    }
  }
}
