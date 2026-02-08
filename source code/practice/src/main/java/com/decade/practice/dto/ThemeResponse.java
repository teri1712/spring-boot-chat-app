package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ImageSpecEmbeddable;

public record ThemeResponse(Integer id, ImageSpecEmbeddable background) {
}
