package com.decade.practice.inbox.infras;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AOTMessageMapperConfig {

    @Bean
    public HibernatePropertiesCustomizer jsonFormatMapperCustomizer(ObjectMapper objectMapper) {
        final JacksonJsonFormatMapper delegate = new JacksonJsonFormatMapper(objectMapper);
        return properties -> properties.put(
            AvailableSettings.JSON_FORMAT_MAPPER,
            new FormatMapper() {
                @Override
                public <T> T fromString(CharSequence charSequence, JavaType<T> javaType, WrapperOptions options) {
                    try {
                        return delegate.fromString(charSequence, javaType, options);
                    } catch (Exception e) {
                        return null;
                    }
                }

                @Override
                public <T> String toString(T value, JavaType<T> javaType, WrapperOptions options) {
                    return delegate.toString(value, javaType, options);
                }
            }
        );
    }
}
