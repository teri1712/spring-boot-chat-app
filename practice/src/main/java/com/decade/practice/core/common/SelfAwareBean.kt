package com.decade.practice.core.common

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

// FOR AOP's drawback
open class SelfAwareBean : ApplicationContextAware {
      private lateinit var appCtx: ApplicationContext

      @Throws(BeansException::class)
      override fun setApplicationContext(applicationContext: ApplicationContext) {
            appCtx = applicationContext
      }

      @Volatile
      protected var self: SelfAwareBean? = null
            get() {
                  if (field == null) {
                        synchronized(this) {
                              if (field == null)
                                    field = appCtx.getBean(this::class.java)
                        }
                  }
                  return field
            }
            private set
}