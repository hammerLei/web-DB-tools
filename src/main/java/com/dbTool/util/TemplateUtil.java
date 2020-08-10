package com.dbTool.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by leifeifei on 18-6-27.
 */
public class TemplateUtil {

  public static String getTemplateByName(String templateName,Map<String, Object> data){


    StringWriter writer = new StringWriter();
    Configuration configuration = new Configuration();
    try {
      configuration.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir")+"/email_module"));
      Template processor = configuration.getTemplate(templateName);
      processor.toString();//.getSource()
      processor.process(data, writer);
    } catch (IOException | TemplateException e) {
//      throw new RuntimeException("Ftl key=" + key + " error:" + e.getMessage(), e);
      return e.getMessage();
    }
    return writer.toString();
  }
}
