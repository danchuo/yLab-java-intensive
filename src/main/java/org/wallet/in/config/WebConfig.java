package org.wallet.in.config;

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The `WebConfig` class is a configuration class for setting up the web environment in a Spring
 * application. It is responsible for configuring web-related settings, including message
 * converters.
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "org.wallet")
@EnableAspectJAutoProxy
@EnableSpringConfigured
public class WebConfig implements WebMvcConfigurer {
  /**
   * Configures custom message converters for handling HTTP request and response payloads. In this
   * method, a Jackson2ObjectMapperBuilder is used to create an ObjectMapper with an indented
   * output. This ObjectMapper is then added to the list of HTTP message converters.
   *
   * @param converters The list of HttpMessageConverter instances to be configured.
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMvcConfigurer.super.configureMessageConverters(converters);
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().indentOutput(true);
    converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
  }
}
