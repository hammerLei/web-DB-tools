package com.dbTool.service.impl;

import com.dbTool.service.TemplateService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by leifeifei on 18-1-12.
 */
@Service
public class TemplateServiceImpl implements TemplateService {

  @Autowired
  private Configuration configuration;

  @Autowired
  private StringTemplateLoader templateLoader;

  @Override
  public String process(String key, Map<String, Object> data,
                        String template) {
    StringWriter writer = new StringWriter();
    try {
      templateLoader.putTemplate(key, template);
      Template processor = configuration.getTemplate(key);
      processor.toString();//.getSource()
      processor.process(data, writer);
    } catch (IOException | TemplateException e) {
//      throw new RuntimeException("Ftl key=" + key + " error:" + e.getMessage(), e);
      return e.getMessage();
    }
    return writer.toString();
  }
}
