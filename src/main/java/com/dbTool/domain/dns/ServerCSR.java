package com.dbTool.domain.dns;

import com.dbTool.util.StringUtil;

import org.apache.commons.lang.StringUtils;

/**
 * Created by leifeifei on 18-1-12.
 */
public class ServerCSR {

  private String countryName;

  private String provinceName;

  private String localityName;

  private String organizationName;

  private String organizadionUnitName = "Market";

  private String commonName;

  private String email;

  public String getCountryName() {
    return StringUtil.replceSpaceToStr(countryName.trim(),"_re_");
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getProvinceName() {
    return StringUtil.replceSpaceToStr(provinceName,"_re_");
  }

  public void setProvinceName(String provinceName) {
    this.provinceName = provinceName;
  }

  public String getLocalityName() {
    return StringUtil.replceSpaceToStr(localityName.trim(),"_re_");
  }

  public void setLocalityName(String localityName) {
    this.localityName = localityName;
  }

  public String getOrganizationName() {
    return StringUtil.replceSpaceToStr(organizationName.trim(),"_re_");
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getOrganizadionUnitName() {
    return StringUtil.replceSpaceToStr(organizadionUnitName.trim(),"_re_");
  }

  public void setOrganizadionUnitName(String organizadionUnitName) {
    this.organizadionUnitName = organizadionUnitName;
  }

  public String getCommonName() {
    return StringUtil.replceSpaceToStr(StringUtils.lowerCase(commonName.trim()),"_re_");
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getEmail() {
    return StringUtil.replceSpaceToStr(email.trim(),"_re_");
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isAttributeEmpty(){
    if(StringUtils.isBlank(countryName) || StringUtils.isBlank(provinceName) ||
        StringUtils.isBlank(localityName) || StringUtils.isBlank(organizationName) || StringUtils.isBlank(commonName) ||
        StringUtils.isBlank(email)){
      return true;
    }else{
      return false;
    }
  }
}
