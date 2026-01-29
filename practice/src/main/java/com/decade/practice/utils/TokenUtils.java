package com.decade.practice.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;

public class TokenUtils {
    public static final String REFRESH_PARAM = "refresh_token";
    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER = "Bearer ";

    public static String extractToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(HEADER_NAME);
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            String token = extractToken(header);
            if (token != null) {
                return token;
            }
        }
        return null;
    }

    public static String extractToken(String headerValue) {
        if (headerValue != null && headerValue.startsWith(BEARER)) {
            return headerValue.substring(BEARER.length());
        }
        return null;
    }

    public static boolean hasToken(HttpServletRequest request) {
        return extractToken(request) != null || extractRefreshToken(request) != null;
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        return request.getParameter(REFRESH_PARAM);
        // Commented out in original:
        // ?: request.cookies?.find { cookie -> cookie.name == REFRESH_PARAM }?.value
    }
}