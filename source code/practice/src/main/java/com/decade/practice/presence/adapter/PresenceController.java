package com.decade.practice.presence.adapter;

import com.decade.practice.presence.application.query.PresenceService;
import com.decade.practice.presence.dto.PresenceResponse;
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

    private final PresenceService presenceService;


    @GetMapping
    public List<PresenceResponse> listOnline(Principal principal) {
        return presenceService.getOnlineList(principal.getName());
    }

    @GetMapping("/{username}")
    public PresenceResponse get(@PathVariable String username) {
        return presenceService.get(username);
    }
}