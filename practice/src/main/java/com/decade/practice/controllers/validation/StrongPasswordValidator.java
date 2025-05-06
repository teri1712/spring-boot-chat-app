package com.decade.practice.controllers.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
      @Override
      public boolean isValid(String value, ConstraintValidatorContext context) {
            return value != null && value.length() >= 8;
      }
}
