package org.wallet.in.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * The `MainWebAppInitializer` class is responsible for initializing the main web application
 * context. It configures the Spring MVC DispatcherServlet and sets up the context for the
 * application.
 */
@Configuration
public class MainWebAppInitializer implements WebApplicationInitializer {
  /**
   * This method is called when the application starts up. It configures the Spring application
   * context, registers a listener, and configures the DispatcherServlet to handle web requests.
   *
   * @param container The ServletContext for the application.
   */
  @Override
  public void onStartup(ServletContext container) {
    AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
    context.scan("org.wallet");
    container.addListener(new ContextLoaderListener(context));
    ServletRegistration.Dynamic dispatcher =
        container.addServlet("mvc", new DispatcherServlet(context));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }
}
