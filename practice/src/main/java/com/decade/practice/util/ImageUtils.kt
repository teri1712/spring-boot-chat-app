package com.decade.practice.util

import com.decade.practice.model.embeddable.ImageSpec.Companion.DEFAULT_HEIGHT
import com.decade.practice.model.embeddable.ImageSpec.Companion.DEFAULT_WIDTH
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.min

object ImageUtils {

      @Throws(IOException::class)
      fun cropCenter(image: BufferedImage): BufferedImage {
            val h = image.height
            val w = image.width
            val d = min(w.toDouble(), h.toDouble()).toInt()
            return image.getSubimage((w - d) / 2, (h - d) / 2, d, d)
      }

      @Throws(IOException::class)
      fun crop(
            image: BufferedImage,
            w: Int = DEFAULT_WIDTH,
            h: Int = DEFAULT_HEIGHT
      ): BufferedImage {
            val cropped = cropCenter(image)
            return scaleImage(cropped, w, h)
      }

      @Throws(IOException::class)
      fun crop(
            inputStream: InputStream,
            w: Int = DEFAULT_WIDTH,
            h: Int = DEFAULT_HEIGHT
      ): BufferedImage {
            val cropped = cropCenter(ImageIO.read(inputStream))
            return scaleImage(cropped, w, h)
      }


      @Throws(IOException::class)
      fun scaleImage(image: BufferedImage, w: Int, h: Int): BufferedImage {
            var w = w
            var h = h
            val imageWidth = image.width
            val imageHeight = image.height
            if (imageWidth <= w && imageHeight <= h) {
                  return image
            }
            val scaleWidth = (w.toFloat()) / imageWidth
            val scaleHeight = (h.toFloat()) / imageHeight
            if (scaleWidth > scaleHeight) {
                  w = (imageWidth * scaleHeight).toInt()
                  h = (imageHeight * scaleHeight).toInt()
            } else {
                  w = (imageWidth * scaleWidth).toInt()
                  h = (imageHeight * scaleWidth).toInt()
            }
            val scaled = BufferedImage(w, h, image.type)
            val g2 = scaled.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2.drawImage(image, 0, 0, w, h, null)
            g2.dispose()

            return scaled
      }
}
