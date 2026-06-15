package com.decade.practice.common;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

public class DatasetImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            metadata.getAnnotationAttributes(ComponentTest.class.getName()));
        if (attributes != null && attributes.containsKey("datasets")) {
            return Arrays.stream(attributes.getClassArray("datasets"))
                .map(Class::getName).toArray(String[]::new);
        }
        return new String[0];
    }
}
