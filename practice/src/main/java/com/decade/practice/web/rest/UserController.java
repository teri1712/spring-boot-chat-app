package com.decade.practice.web.rest;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.media.ImageStore;
import com.decade.practice.model.domain.DefaultAvatar;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.model.dto.SignUpRequest;
import com.decade.practice.security.model.DaoUser;
import com.decade.practice.usecases.UserOperations;
import com.decade.practice.utils.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

        private final UserRepository userRepository;
        private final SecurityContextRepository contextRepo;
        private final ImageStore imageStore;
        private final UserOperations userOperations;

        public UserController(UserRepository userRepository,
                              SecurityContextRepository contextRepo,
                              ImageStore imageStore,
                              UserOperations userOperations
        ) {
                this.userOperations = userOperations;
                this.userRepository = userRepository;
                this.contextRepo = contextRepo;
                this.imageStore = imageStore;
        }

        @GetMapping
        public List<User> findConversations(
                @AuthenticationPrincipal(expression = "name") String username,
                @RequestParam(required = true) String query
        ) {
                return userRepository.findByNameContainingAndRole(query, "ROLE_USER");
        }

        @PostMapping
        @PreAuthorize("isAnonymous()")
        public ResponseEntity<String> registerUser(
                HttpServletRequest request,
                HttpServletResponse response,
                @RequestPart("information") @Valid SignUpRequest information,
                @RequestPart(value = "file", required = false) MultipartFile file
        ) throws IOException {
                ImageSpec avatar;
                if (file != null) {
                        avatar = imageStore.save(ImageUtils.crop(file.getInputStream()));
                } else {
                        avatar = DefaultAvatar.getInstance();
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
                        if (avatar != DefaultAvatar.getInstance()) {
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