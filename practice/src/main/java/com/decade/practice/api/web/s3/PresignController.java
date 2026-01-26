package com.decade.practice.api.web.s3;

import com.decade.practice.api.dto.S3PresignedDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class PresignController {

    private final PresignedUrlService service;

    @GetMapping("/upload-urls")
    public S3PresignedDto uploadUrl(@RequestParam String filename, Principal principal) {
        return service.generateUploadUrl(filename, principal.getName());
    }
}
