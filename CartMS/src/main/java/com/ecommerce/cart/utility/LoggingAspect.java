package com.ecommerce.cart.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.sterotype.Component;

@Component
@Aspect
public class LoggingAspect {
  private static Log Logger = LogFactory.getLog(LoggingAspect.class);

  @AfterThrowing(pointcut = "execution(* com.ecommerce.service.*Impl.*(..))", throwing = "exception")
  public void logExceptionFromService(Exception exception) {
    Logger.error(exception.getMessage(), exception);
  }
}
