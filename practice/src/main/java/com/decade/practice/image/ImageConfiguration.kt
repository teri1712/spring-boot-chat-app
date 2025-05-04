package com.decade.practice.image

import com.decade.practice.model.domain.embeddable.ImageSpec
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

private const val fileSystemImageStore = "fileSystemImageStore"
private const val DIRECTORY = "./images/"
private const val FORMAT = "jpeg"
private const val PATH = "/image"
private const val QUERY = "filename="
private const val QUERY_PATH = "$PATH?$QUERY"

private class FileSystemImageStore : ImageStore {

      private fun URL.filename() =
            query.substring(QUERY.length)

      override fun support(uri: URI): Boolean {
            val scheme = uri.scheme

            if (scheme.equals("http", ignoreCase = true)
                  || scheme.equals("https", ignoreCase = true)
            )
                  return uri.toURL().path.startsWith(QUERY_PATH)
            return false
      }

      @Throws(IOException::class)
      override fun save(image: BufferedImage): ImageSpec {
            val httpRequest =
                  (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes)
                        .request
            val directory = File(DIRECTORY)
            if (!directory.exists())
                  directory.mkdir()

            val filename = UUID.randomUUID().toString() + "." + FORMAT
            val file = File(directory, filename)
            ImageIO.write(image, FORMAT, file)

            val scheme = httpRequest.scheme
            val server = httpRequest.serverName
            val port = httpRequest.serverPort

            val base = "$scheme://$server:$port"
            val uri = URL(base + QUERY_PATH + filename).toString()
            return ImageSpec(uri, filename, image.width, image.height, FORMAT)
      }

      @Throws(IOException::class)
      override fun read(uri: URI): Resource {
            val filename = uri.toURL().filename()
            val filePath = Paths.get(DIRECTORY).resolve(filename)
            val resource: Resource = UrlResource(filePath.toUri())
            if (resource.exists() || resource.isReadable) {
                  return resource
            }
            throw FileNotFoundException("File not found: $filename")
      }

      @Throws(FileNotFoundException::class)
      override fun remove(uri: URI) {
            val filename = uri.toURL().filename()
            val file = File(DIRECTORY + filename)
            if (!file.isFile || !file.delete()) {
                  throw FileNotFoundException("File not found: $filename")
            }
      }
}

@Configuration
class LocalImageConfiguration {
      @Bean(fileSystemImageStore)
      fun fileSystemImageStore(): ImageStore = FileSystemImageStore()
}

@Configuration
@RestController
@RequestMapping(PATH)
class ImageController(
      @Qualifier(fileSystemImageStore) private val store: ImageStore
) {

      @GetMapping
      fun get(request: HttpServletRequest): ResponseEntity<Resource> {
            try {
                  val requestURL = request.requestURL.toString()
                  val queryString = request.queryString
                  val uri = URL("$requestURL?$queryString").toURI()
                  val resource = store.read(uri)
                  val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
                        .cachePublic()

                  return ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource)
            } catch (e: IOException) {
                  return ResponseEntity.notFound().build()
            }
      }
}
