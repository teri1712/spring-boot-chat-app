package com.decade.practice.model.domain;

import com.decade.practice.model.domain.embeddable.ImageSpec;

public class DefaultAvatar {
    private static final String AVATAR_URL =
            "https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg";

    public static final ImageSpec INSTANCE = new ImageSpec(
            AVATAR_URL,
            "",
            ImageSpec.DEFAULT_WIDTH,
            ImageSpec.DEFAULT_HEIGHT,
            ImageSpec.DEFAULT_FORMAT
    );
}