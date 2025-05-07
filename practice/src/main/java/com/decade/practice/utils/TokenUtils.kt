package com.decade.practice.utils

import jakarta.servlet.http.HttpServletRequest

object TokenUtils {
      const val REFRESH_PARAM: String = "refresh_token"
      const val HEADER_NAME: String = "Authorization"
      const val BEARER: String = "Bearer "

      fun extractToken(request: HttpServletRequest): String? {
            val headers = request.getHeaders(HEADER_NAME)
            for (header in headers) {
                  if (header.startsWith(BEARER)) {
                        return header.substring(BEARER.length)
                  }
            }

            return null
      }

      fun extractRefreshToken(request: HttpServletRequest): String? = request.getParameter(REFRESH_PARAM)
}