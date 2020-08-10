package com.dbTool.service;

import java.util.Map;

/**
 * Created by leifeifei on 18-1-12.
 */
public interface TemplateService {

  String process(String key, Map<String, Object> data,String template);
}
