package com.decade.practice.controllers.rest;

import com.decade.practice.core.TokenCredentialService;
import com.decade.practice.core.UserOperations;
import com.decade.practice.image.ImageStore;
import com.decade.practice.model.TokenCredential;
import com.decade.practice.model.domain.DefaultAvatar;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.dto.SignUpRequest;
import com.decade.practice.security.model.DaoUser;
import com.decade.practice.security.model.UserClaims;
import com.decade.practice.utils.ImageUtils;
import com.decade.practice.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

      private final UserOperations userOperations;
      private final SecurityContextRepository contextRepo;
      private final ImageStore imageStore;
      private final TokenCredentialService credentialService;

      public AuthenticationController(
            UserOperations userOperations,
            SecurityContextRepository contextRepo,
            ImageStore imageStore,
            TokenCredentialService credentialService
      ) {
            this.userOperations = userOperations;
            this.contextRepo = contextRepo;
            this.imageStore = imageStore;
            this.credentialService = credentialService;
      }

      @PostMapping("/oauth2")
      public void exchange(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest request,
            HttpServletResponse response
      ) throws Exception {
            String username = jwt.getSubject();

            var claims = jwt.getClaims();
            String name = claims.get("name").toString();
            String picture = claims.get("picture").toString();
            try {
                  userOperations.createOauth2User(username, name, picture);
            } catch (DataIntegrityViolationException ignored) {
            }

            request.getRequestDispatcher("/account").forward(request, response);
      }

      @PostMapping("/refresh")
      public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
            String refreshToken = TokenUtils.extractRefreshToken(request);
            if (refreshToken == null) {
                  throw new AccessDeniedException("NO TOKEN REPRESENTED");
            }

            credentialService.validate(refreshToken);

            UserClaims claims = credentialService.decodeToken(refreshToken);
            TokenCredential credential = credentialService.create(claims, refreshToken);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(
                  new ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(credential)
            );
            response.getWriter().flush();
      }

      @PostMapping("/sign-up")
      @PreAuthorize("isAnonymous()")
      public ResponseEntity<String> signUp(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestPart("information") @Valid SignUpRequest information,
            @RequestPart(value = "file", required = false) MultipartFile file
      ) throws IOException {
            ImageSpec avatar;
            if (file != null) {
                  avatar = imageStore.save(ImageUtils.crop(file.getInputStream()));
            } else {
                  avatar = DefaultAvatar.INSTANCE;
            }

            try {
                  User user = userOperations.create(
                        information.getUsername(),
                        information.getPassword(),
                        information.getName(),
                        information.getDob(),
                        information.getGender(),
                        avatar,
                        true
                  );

                  SecurityContext context = SecurityContextHolder.createEmptyContext();
                  context.setAuthentication(new UsernamePasswordAuthenticationToken(
                        new DaoUser(user), information.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                  ));
                  contextRepo.saveContext(context, request, response);
                  SecurityContextHolder.setContext(context);

                  return ResponseEntity.status(HttpStatus.CREATED).build();
            } catch (Exception e) {
                  e.printStackTrace();
                  if (avatar != DefaultAvatar.INSTANCE) {
                        imageStore.remove(URI.create(avatar.getUri()));
                  }

                  if (e instanceof DataIntegrityViolationException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body("Username exists");
                  } else {
                        throw e;
                  }
            }
      }
}