package com.decade.practice.models.domain;

import com.decade.practice.models.domain.embeddable.ImageSpec;

public class DefaultAvatar extends ImageSpec {
        private static final String AVATAR_URL =
                "https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg";

        private static final DefaultAvatar INSTANCE = new DefaultAvatar();

        private DefaultAvatar() {
                super(AVATAR_URL,
                        "",
                        ImageSpec.DEFAULT_WIDTH,
                        ImageSpec.DEFAULT_HEIGHT,
                        ImageSpec.DEFAULT_FORMAT);
        }

        public static DefaultAvatar getInstance() {
                return INSTANCE;
        }
}