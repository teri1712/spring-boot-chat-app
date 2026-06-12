package com.decade.practice.presence.adapter;

import com.decade.practice.presence.application.query.PresenceService;
import com.decade.practice.presence.dto.BuddyResponse;
import com.decade.practice.presence.dto.RoomPresenceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping
public class PresenceController {

    private final PresenceService presenceService;


    @Operation(summary = "My buddies",
        responses = {
            @ApiResponse(responseCode = "200", description = "My buddies"),
        }
    )
    @GetMapping("/buddy-presences")
    public List<BuddyResponse> listBuddies(
        @AuthenticationPrincipal(expression = "id") UUID caller
    ) {
        return presenceService.findMyBuddies(caller);
    }


    @Operation(summary = "Find presences of conversation by batch",
        responses = {
            @ApiResponse(responseCode = "200", description = "Presence map"),
            @ApiResponse(responseCode = "400", description = "Expect chatId size from 1-50", content = @Content(
                examples = {@ExampleObject(ref = "#/components/examples/Validation")}
            ))
        }
    )
    @GetMapping("/presences")
    public Map<String, RoomPresenceResponse> find(
        @AuthenticationPrincipal(expression = "id") UUID caller,
        @Size(min = 1, max = 50)
        @Parameter(description = "List of chat ids, size must be between 1 to 50")
        @RequestParam(name = "chatId")
        Set<String> chatIds) {
        return presenceService.find(caller, chatIds);
    }
}