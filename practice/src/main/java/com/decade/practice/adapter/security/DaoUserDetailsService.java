package com.decade.practice.adapter.security;

import com.decade.practice.adapter.security.models.DaoUser;
import com.decade.practice.domain.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DaoUserDetailsService implements UserDetailsService {

        private final UserRepository userRepo;

        public DaoUserDetailsService(UserRepository userRepo) {
                this.userRepo = userRepo;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                try {
                        return new DaoUser(userRepo.findByUsername(username));
                } catch (Exception e) {
                        throw new UsernameNotFoundException("Credential with Username: " + username + " does not exist.");
                }
        }
}
