package com.decade.practice.adapter.web.mvc;

import com.decade.practice.adapter.web.validation.StrongPassword;
import com.decade.practice.application.dto.Profile;
import com.decade.practice.application.usecases.ImageStore;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.embeddables.ImageSpec;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.utils.ImageUtils;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
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

import static com.decade.practice.domain.embeddables.ImageSpec.DEFAULT_HEIGHT;
import static com.decade.practice.domain.embeddables.ImageSpec.DEFAULT_WIDTH;

@Controller
@RequestMapping("/profile")
@SessionAttributes({"profile"})
public class ProfileController {

        private final ImageStore imageStore;
        private final UserRepository userRepository;
        private final UserService userService;

        ProfileController(ImageStore imageStore, UserService userService, UserRepository userRepository) {
                this.imageStore = imageStore;
                this.userService = userService;
                this.userRepository = userRepository;
        }


        @ModelAttribute("availableGenders")
        public String[] prepareAvailableGenders() {
                return new String[]{"Male", "Female", "Other"};
        }

        @ModelAttribute("profile")
        public Profile prepareProfile(
                @AuthenticationPrincipal(expression = "username") String username,
                @AuthenticationPrincipal(expression = "id") UUID id
        ) {
                Profile profile = new Profile(userRepository.findByUsername(username));
                profile.setAllowToUpdate(id != null);
                return profile;
        }

        @GetMapping
        public String getProfilePage() {
                return "profile";
        }

        @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
        @PostMapping("/information")
        public String updateProfile(
                @ModelAttribute("profile") @Valid Profile profile,
                @AuthenticationPrincipal(expression = "id") UUID id,
                @RequestPart(name = "file", required = false) MultipartFile file
        ) throws IOException, OptimisticLockException, URISyntaxException {

                String name = profile.getName();
                String gender = profile.getGender();
                Date birthday = profile.getBirthday();


                ImageSpec avatar = null;
                if (file != null && !file.isEmpty()) {
                        InputStream inputStream = file.getInputStream();
                        BufferedImage cropped = ImageUtils
                                .crop(inputStream, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                        avatar = imageStore.save(cropped);
                }

                try {
                        if (avatar == null) {
                                userService.update(id, name, birthday, gender);
                        } else {
                                userService.update(id, name, birthday, gender, avatar);
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

        @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
        @PostMapping("/password")
        public String changePassword(
                Model model,
                @AuthenticationPrincipal(expression = "id") UUID id,
                @RequestParam(value = "password", required = false) String password,
                @StrongPassword @RequestParam("new_password") String newPassword
        ) {
                try {
                        userService.update(id, newPassword, password);
                        model.addAttribute("success", "Credentials updated");
                } catch (Exception e) {
                        e.printStackTrace();
                        model.addAttribute("error", "Wrong credential");
                }
                return "password";
        }
}
