package com.decade.practice.api.web.rest;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.persistence.redis.OnlineStatus;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/presences")
public class PresenceController {

    private final UserPresenceService presenceService;


    @GetMapping
    public List<OnlineStatus> listOnline(Principal principal) {
        return presenceService.getOnlineList(principal.getName());
    }

    @GetMapping("/{username}")
    public OnlineStatus get(@PathVariable String username) {
        return presenceService.get(username);
    }
}