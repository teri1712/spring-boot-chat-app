package com.decade.practice.engagement.adapter;


import com.decade.practice.engagement.application.ports.out.Caller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;

@Component
public class RequestCaller implements Caller {

      private static HttpServletRequest getCurrentRequest() {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
      }

      @Override
      public Principal get() {
            return getCurrentRequest().getUserPrincipal();
      }
}
