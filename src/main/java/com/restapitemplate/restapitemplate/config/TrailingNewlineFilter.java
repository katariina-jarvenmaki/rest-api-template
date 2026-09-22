package com.restapitemplate.restapitemplate.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Makes every JSON response end with a newline. Terminal tools like curl do
 * not add one, so without this the response body glues to the next prompt.
 *
 * @author KatariinaJ
 * @version 2026-09-22
 */
@Component
public class TrailingNewlineFilter extends OncePerRequestFilter {

    // Spring's own wrapper caches the body, so it can be finished after the chain.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingResponseWrapper wrapped =
            new ContentCachingResponseWrapper(response);

        filterChain.doFilter(request, wrapped);

        // Only an existing JSON body gets the newline; a 204 has no body,
        // so it stays untouched.
        String contentType = wrapped.getContentType();
        if (contentType != null && contentType.contains("application/json")
                && wrapped.getContentAsByteArray().length > 0) {
            wrapped.getOutputStream().write('\n');
        }

        // Sends the cached body and fixes Content-Length to the new size.
        wrapped.copyBodyToResponse();
    }
}