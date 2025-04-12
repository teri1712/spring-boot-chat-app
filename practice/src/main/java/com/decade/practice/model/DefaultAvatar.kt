package com.decade.practice.model

import com.decade.practice.model.embeddable.ImageSpec

private const val AVATAR_URL =
    "https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg"
private const val WIDTH = 512
private const val HEIGHT = 512

object DefaultAvatar : ImageSpec(
    uri = AVATAR_URL,
    width = WIDTH,
    height = HEIGHT,
)
