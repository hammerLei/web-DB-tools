package com.dbTool;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class FreeMarkerConfig {
  @Bean
  public Configuration configuration() {
    Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
    configuration.setDefaultEncoding("utf-8");
    configuration.setClassicCompatible(true);
    configuration.setNumberFormat("#");
    return configuration;
  }

  @Bean
  public StringTemplateLoader templateLoader(Configuration configuration) {
    StringTemplateLoader templateLoader = new StringTemplateLoader();
    configuration.setTemplateLoader(templateLoader);
    return templateLoader;
  }
}
