package org.wallet.in.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The `WebConfig` class is a configuration class for setting up the web environment in a Spring
 * application. It is responsible for configuring web-related settings.
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"org.wallet", "com.danchuo.starterannotations.aop"})
@EnableAspectJAutoProxy
@EnableSpringConfigured
public class WebConfig implements WebMvcConfigurer {}
