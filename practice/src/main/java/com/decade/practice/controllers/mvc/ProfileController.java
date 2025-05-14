package com.decade.practice.controllers.mvc;

import com.decade.practice.controllers.validation.StrongPassword;
import com.decade.practice.core.UserOperations;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.image.ImageStore;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.dto.Profile;
import com.decade.practice.utils.ImageUtils;
import com.decade.practice.utils.TokenUtils;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

import static com.decade.practice.model.domain.embeddable.ImageSpec.DEFAULT_HEIGHT;
import static com.decade.practice.model.domain.embeddable.ImageSpec.DEFAULT_WIDTH;

@Controller
@RequestMapping("/profile")
@SessionAttributes({"profile", "availableGenders"})
public class ProfileController {

      private final ImageStore imageStore;
      private final UserRepository userRepository;
      private final UserOperations userOperations;

      ProfileController(ImageStore imageStore, UserOperations userOperations, UserRepository userRepository) {
            this.imageStore = imageStore;
            this.userOperations = userOperations;
            this.userRepository = userRepository;
      }


      @ModelAttribute("availableGenders")
      public String[] prepareAvailableGenders() {
            return new String[]{"Male", "Female", "Other"};
      }

      @ModelAttribute("profile")
      public Profile prepareProfile(
            @AuthenticationPrincipal(expression = "name") String username,
            @AuthenticationPrincipal(expression = "id") UUID id
      ) {
            Profile profile = new Profile(userRepository.getByUsername(username));
            profile.setAllowToUpdate(id != null);
            return profile;
      }

      @GetMapping
      public String getProfilePage() {
            return "profile";
      }

      /**
       * Updates the user's profile information including name, birthday, gender and avatar
       *
       * @param profile Profile object containing updated user information
       * @param id      Authenticated user's ID
       * @param file    Optional avatar image file to update
       * @return View name for profile page
       * @throws IOException             If there are issues processing the image file
       * @throws OptimisticLockException If there is a concurrent modification conflict
       * @throws URISyntaxException      If the avatar URI is invalid
       */
      @PostMapping("/information")
      public String updateProfile(
            @ModelAttribute("profile") @Valid Profile profile,
            @AuthenticationPrincipal(expression = "id") UUID id,
            @RequestPart(name = "file", required = false) MultipartFile file
      ) throws IOException, OptimisticLockException, URISyntaxException {
            if (id == null) {
                  // Only application's principals can modify the information
                  throw new AccessDeniedException("Operation not supported");
            }

            String name = profile.getName();
            String gender = profile.getGender();
            Date birthday = profile.getBirthday();


            // Process avatar image if submitted
            ImageSpec avatar = null;
            if (file != null && !file.isEmpty()) {
                  InputStream inputStream = file.getInputStream();
                  BufferedImage cropped = ImageUtils.INSTANCE
                        .crop(inputStream, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                  avatar = imageStore.save(cropped);
            }

            try {
                  // Update user profile with or without new submitted avatar
                  if (avatar == null) {
                        userOperations.update(id, name, birthday, gender);
                  } else {
                        userOperations.update(id, name, birthday, gender, avatar);
                        profile.setAvatar(avatar);
                  }
                  return "profile";
            } catch (Exception e) {
                  // Clean up saved avatar if update fails
                  if (avatar != null)
                        imageStore.remove(new URI(avatar.getUri()));
                  throw e;
            }
      }

      @GetMapping("/password")
      public String getPasswordPage() {
            return "password";
      }

      /**
       * Changes the current user's password.
       * Requires a valid modifier token (refreshToken) and user must be authenticated.
       */
      @PostMapping("/password")
      public String changePassword(
            Model model,
            HttpServletRequest request,
            @AuthenticationPrincipal(expression = "id") UUID id,
            @RequestParam(value = "password", required = false) String password,
            @StrongPassword @RequestParam("new_password") String newPassword
      ) {
            if (id == null) {
                  // Only application's principals can modify the information
                  throw new AccessDeniedException("Operation not supported");
            }

            // Must have a valid refresh_token/password to update the password
            String modiferToken = password;
            if (modiferToken == null) {
                  modiferToken = TokenUtils.INSTANCE.extractRefreshToken(request);
                  if (modiferToken == null) {
                        throw new AccessDeniedException("Missing credentials");
                  }
            }

            // Update the user's password
            try {
                  userOperations.update(id, newPassword, modiferToken);
                  model.addAttribute("success", "Credentials updated");
            } catch (Exception e) {
                  e.printStackTrace();
                  model.addAttribute("error", "Wrong credential");
            }
            return "password";
      }
}
