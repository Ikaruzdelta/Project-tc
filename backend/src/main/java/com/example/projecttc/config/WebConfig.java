package com.example.projecttc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
   public WebConfig() {
   }

   public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
      .allowedOrigins(new String[]{"*"})
      .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"})
      .allowedHeaders(new String[]{"*"});
   }
}
