package com.decade.practice.model.local

import com.decade.practice.model.domain.embeddable.ImageSpec


data class TextEvent(val content: String)
data class SeenEvent(val at: Long)
data class IconEvent(val resourceId: Int)
data class ImageEvent(val imageSpec: ImageSpec)