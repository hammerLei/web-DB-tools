package com.dbTool.domain.dns;

/**
 * Created by leifeifei on 17-11-30.
 */
public class DNSRecord {
  private String name;
  private String type;
  private String value;
  private long ttl;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public long getTtl() {
    return ttl;
  }

  public void setTtl(long ttl) {
    this.ttl = ttl;
  }
}
