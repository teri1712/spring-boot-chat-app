package com.decade.practice.model.domain.embeddable

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import java.io.Serializable

@Embeddable
@JsonDeserialize
open class ImageSpec(
      val uri: String,
      val filename: String,
      @field:NotNull var width: Int = DEFAULT_WIDTH,
      @field:NotNull var height: Int = DEFAULT_HEIGHT,
      var format: String = DEFAULT_FORMAT,
) : Serializable {
      companion object {
            const val DEFAULT_FORMAT = "jpg"
            const val DEFAULT_HEIGHT: Int = 512
            const val DEFAULT_WIDTH: Int = 512
      }
}
