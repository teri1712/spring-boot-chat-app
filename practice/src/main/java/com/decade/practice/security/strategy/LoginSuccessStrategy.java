package com.decade.practice.security.strategy;

import com.decade.practice.core.ChatOperations;
import com.decade.practice.core.UserOperations;
import com.decade.practice.model.local.AccountEntry;
import com.decade.practice.utils.PlatformsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class LoginSuccessStrategy implements AuthenticationSuccessHandler {

      private final UserOperations userOperations;
      private final ChatOperations chatOperations;

      public LoginSuccessStrategy(UserOperations userOperations, ChatOperations chatOperations) {
            this.userOperations = userOperations;
            this.chatOperations = chatOperations;
      }

      @Override
      public void onAuthenticationSuccess(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse,
            Authentication authentication
      ) throws IOException {
            if (PlatformsUtils.isBrowserNavigation(httpRequest)) {
                  httpResponse.sendRedirect("/profile");
                  return;
            }

            Object principal = authentication.getPrincipal();
            if (!(principal instanceof UserDetails)) {
                  throw new AccessDeniedException("Operation not supported");
            }

            var account = userOperations.prepareAccount((UserDetails) principal);
            var user = account.getUser();
            var syncContext = user.getSyncContext();
            var chatList = chatOperations.listChat(user)
                  .stream()
                  .map(chat -> chatOperations.getSnapshot(chat, user, syncContext.getEventVersion()))
                  .collect(Collectors.toList());

            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.getWriter().write(
                  new ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(new AccountEntry(account, chatList))
            );
            httpResponse.getWriter().flush();
      }
}
