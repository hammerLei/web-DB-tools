package com.dbTool.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

public final class HttpApiClientBuilder {
  private static final int DEFAULT_TIMEOUT = 2000;
  private static final int DEFAULT_CONNECTION_NUMBER = 1;
  private int connectionNumber = 1;
  private int timeout = 2000;

  public HttpApiClientBuilder() {
  }

  public HttpApiClientBuilder setConnectionNumber(int connectionNumber) {
    this.connectionNumber = connectionNumber;
    return this;
  }

  public HttpApiClientBuilder setTimeout(int timeout) {
    this.timeout = timeout;
    return this;
  }

  public HttpApiClient build() {
    return new HttpApiClient(this.connectionNumber, this.timeout);
  }
}

