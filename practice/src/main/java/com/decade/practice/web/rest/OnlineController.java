package com.decade.practice.web.rest;

import com.decade.practice.model.OnlineStatus;
import com.decade.practice.presence.UserPresenceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/onlines")
public class OnlineController {

        private final UserPresenceService stat;

        public OnlineController(UserPresenceService stat) {
                this.stat = stat;
        }

        @GetMapping
        public List<OnlineStatus> listOnline(Principal principal) {
                return stat.getOnlineList(principal.getName());
        }

        @GetMapping("/{username}")
        public OnlineStatus get(@PathVariable String username) {
                return stat.get(username);
        }
}