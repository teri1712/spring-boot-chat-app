package com.decade.practice.common.security.jwt;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithJwtUserSecurityContextFactory.class)
public @interface WithJwtUser {

    @AliasFor("username")
    String value() default "alice";

    @AliasFor("value")
    String username() default "alice";

    String id() default "11111111-1111-1111-1111-111111111111";

    String name() default "Alice";

    String avatar() default "https://avatar.com/alice";
}
