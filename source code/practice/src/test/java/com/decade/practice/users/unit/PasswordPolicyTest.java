package com.decade.practice.users.unit;

import com.decade.practice.users.domain.User;
import com.decade.practice.users.domain.UserPasswordPolicy;
import com.decade.practice.users.domain.WrongPasswordException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordPolicyTest {


      @Mock
      PasswordEncoder passwordEncoder;

      @InjectMocks
      UserPasswordPolicy passwordPolicy;


      @Test
      void givenCorrectMatchingPassword_whenChangingPassword_thenNewEncodedNewPasswordSetToUser() {
            UUID userId = UUID.randomUUID();

            User user = new User(
                      userId,
                      "teri",
                      "encoded old password",
                      "teri",
                      "teri.jpg",
                      new Date(),
                      1f
            );

            when(passwordEncoder.matches("old password", "encoded old password"))
                      .thenReturn(true);
            when(passwordEncoder.encode("new password"))
                      .thenReturn("encoded new password");


            passwordPolicy.change(user, "old password", "new password");

            assertThat(user)
                      .extracting(User::getPassword)
                      .isEqualTo("encoded new password");
      }

      @Test
      void givenInCorrectMatchingPassword_whenChangingPassword_thenWrongPasswordThrown() {
            UUID userId = UUID.randomUUID();

            User user = new User(
                      userId,
                      "teri",
                      "encoded old password",
                      "teri",
                      "teri.jpg",
                      new Date(),
                      1f
            );

            when(passwordEncoder.matches("wrong old password", "encoded old password"))
                      .thenReturn(false);

            assertThatThrownBy(() -> passwordPolicy.change(user, "wrong old password", "new password"))
                      .isInstanceOf(WrongPasswordException.class)
                      .hasMessage("Passwords do not match");
      }

}
