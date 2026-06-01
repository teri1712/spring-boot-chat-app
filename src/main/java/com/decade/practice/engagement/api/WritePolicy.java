package com.decade.practice.engagement.api;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@PreAuthorize("@engagementApi.canWrite(#p0,#p1)")
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WritePolicy {
}
