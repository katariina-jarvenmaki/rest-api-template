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
 * Makes every JSON response end with a newline. Terminal tools like curl do not add one
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
        String contentType = wrapped.getContentType();
        if (contentType != null
                && (contentType.contains("application/json")
                    || contentType.contains("+json"))
                && wrapped.getContentAsByteArray().length > 0) {
            wrapped.getOutputStream().write('\n');
        }

        // Sends the cached body and fixes Content-Length to the new size.
        wrapped.copyBodyToResponse();
    }

    // Skips / so the welcome page keeps its body. The forward to index.html
    // loses it inside the wrapper, which also wrote Content-Length: 0.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/".equals(request.getRequestURI());
    }

    // Error responses (404, 500) are written on a separate ERROR dispatch,
    // which this filter skips by default. Opting in keeps their JSON bodies
    // ending with a newline like every other response.
    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
