package com.decade.practice.core.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SelfAwareBean implements ApplicationContextAware {
      private ApplicationContext appCtx;
      private volatile SelfAwareBean self;

      @Override
      public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.appCtx = applicationContext;
      }

      protected SelfAwareBean getSelf() {
            if (self == null) {
                  synchronized (this) {
                        if (self == null) {
                              self = appCtx.getBean(this.getClass());
                        }
                  }
            }
            return self;
      }
}