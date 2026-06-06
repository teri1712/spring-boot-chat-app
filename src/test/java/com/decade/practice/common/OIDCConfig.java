package com.decade.practice.common;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@TestConfiguration
public class OIDCConfig {

    public static final RSAKey TEST_RSA_KEY;

    static {
        try {
            TEST_RSA_KEY = new RSAKeyGenerator(2048).keyID("test-key").generate();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() throws Exception {
        return NimbusJwtDecoder
            .withPublicKey(TEST_RSA_KEY.toRSAPublicKey())
            .build();
    }
}
