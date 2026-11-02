package com.example.expensetracker.security;

import com.example.expensetracker.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Reads the user id that JwtAuthFilter stored on the SecurityContext. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long userId)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please log in");
        }
        return userId;
    }
}
