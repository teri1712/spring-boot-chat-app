package com.decade.practice.model.domain

import com.decade.practice.model.domain.embeddable.ImageSpec

private const val AVATAR_URL =
      "https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg"

object DefaultAvatar : ImageSpec(
      uri = AVATAR_URL,
      filename = ""
)
