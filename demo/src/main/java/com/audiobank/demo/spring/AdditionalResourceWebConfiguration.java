package com.audiobank.demo.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/media/**").addResourceLocations("file:demo/src/main/media/");
    //System.out.println(fullPath);
    //registry.addResourceHandler("/media/**").addResourceLocations("file:" + System.getProperty("user.dir") + "demo/src/main/media/");
  }
}
