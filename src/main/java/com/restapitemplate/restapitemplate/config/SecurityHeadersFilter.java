package com.restapitemplate.restapitemplate.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Adds the security headers to every response. The API and the browser UI
 * share one origin, so one filter covers both.
 *
 * @author KatariinaJ
 * @version 2026-10-02
 */
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        // Browsers trust Content-Type only.
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Limiting loads to from this origin only
        response.setHeader("Content-Security-Policy",
            "default-src 'self'; frame-ancestors 'none'");

        // Keeping refferrer out of other sites' logs.
        response.setHeader("Referrer-Policy", "no-referrer");

        // No cache storage
        response.setHeader("Cache-Control", "no-store");

        filterChain.doFilter(request, response);
    }

    // Error responses are written on a ERROR dispatch
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}