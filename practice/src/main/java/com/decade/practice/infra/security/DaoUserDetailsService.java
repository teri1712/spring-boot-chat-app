package com.decade.practice.infra.security;

import com.decade.practice.infra.security.models.DaoUser;
import com.decade.practice.persistence.jpa.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
public class DaoUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public DaoUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new DaoUser(userRepo.findByUsername(username).orElseThrow());
        } catch (NoSuchElementException ex) {
            log.error("User not found {}", username);
            throw new UsernameNotFoundException("Credential with Username: " + username + " does not exist.");
        }
    }
}
