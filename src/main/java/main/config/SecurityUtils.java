package main.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper for accessing the current authenticated user. Prefer this over
 * directly using SecurityContextHolder so auth access is consistent and testable.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getCurrentUsername() {
        Authentication auth = getCurrentUser();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())
                ? auth.getName()
                : null;
    }
}
