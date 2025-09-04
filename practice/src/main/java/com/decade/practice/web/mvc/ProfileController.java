package com.decade.practice.web.mvc;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.media.ImageStore;
import com.decade.practice.models.domain.embeddable.ImageSpec;
import com.decade.practice.models.dto.Profile;
import com.decade.practice.usecases.UserOperations;
import com.decade.practice.utils.ImageUtils;
import com.decade.practice.web.validation.StrongPassword;
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

import static com.decade.practice.models.domain.embeddable.ImageSpec.DEFAULT_HEIGHT;
import static com.decade.practice.models.domain.embeddable.ImageSpec.DEFAULT_WIDTH;

@Controller
@RequestMapping("/profile")
@SessionAttributes({"profile"})
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
                @AuthenticationPrincipal(expression = "username") String username,
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

        @PreAuthorize("authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
        @PostMapping("/password")
        public String changePassword(
                Model model,
                @AuthenticationPrincipal(expression = "id") UUID id,
                @RequestParam(value = "password", required = false) String password,
                @StrongPassword @RequestParam("new_password") String newPassword
        ) {
                try {
                        userOperations.update(id, newPassword, password);
                        model.addAttribute("success", "Credentials updated");
                } catch (Exception e) {
                        e.printStackTrace();
                        model.addAttribute("error", "Wrong credential");
                }
                return "password";
        }
}
