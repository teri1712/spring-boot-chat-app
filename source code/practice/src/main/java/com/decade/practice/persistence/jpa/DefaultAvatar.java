package com.decade.practice.persistence.jpa;

import com.decade.practice.persistence.jpa.embeddables.ImageSpecEmbeddable;

public class DefaultAvatar extends ImageSpecEmbeddable {
    private static final String AVATAR_URL =
            "https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg";

    private static final DefaultAvatar INSTANCE = new DefaultAvatar();

    private DefaultAvatar() {
        super(AVATAR_URL,
                "",
                ImageSpecEmbeddable.DEFAULT_WIDTH,
                ImageSpecEmbeddable.DEFAULT_HEIGHT,
                ImageSpecEmbeddable.DEFAULT_FORMAT);
    }

    public static DefaultAvatar getInstance() {
        return INSTANCE;
    }
}