package com.dbTool.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.net.URI;
import org.apache.http.client.methods.HttpPost;

public class HttpGetWithEntity extends HttpPost {
  public static final String METHOD_NAME = "GET";

  public HttpGetWithEntity(URI url) {
    super(url);
  }

  public HttpGetWithEntity(String url) {
    super(url);
  }

  public String getMethod() {
    return "GET";
  }
}
