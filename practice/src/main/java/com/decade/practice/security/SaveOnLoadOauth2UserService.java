package com.decade.practice.security;

import com.decade.practice.usecases.UserOperations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

class SaveOnLoadOauth2UserService<R extends OAuth2UserRequest, U extends OAuth2User> implements OAuth2UserService<R, U> {

        private final UserOperations userOperations;
        private final OAuth2UserService<R, U> delegate;

        public SaveOnLoadOauth2UserService(UserOperations userOperations, OAuth2UserService<R, U> delegate) {
                this.userOperations = userOperations;
                this.delegate = delegate;
        }

        @Override
        public U loadUser(R userRequest) {
                U oAuth2User = delegate.loadUser(userRequest);
                String username = oAuth2User.getName();
                String name = oAuth2User.getAttributes().get("name").toString();
                String picture = oAuth2User.getAttributes().get("picture").toString();
                try {
                        userOperations.createOauth2User(username, name, picture);
                } catch (DataIntegrityViolationException ignored) {
                        // Ignore if user already exists
                }
                return oAuth2User;
        }
}