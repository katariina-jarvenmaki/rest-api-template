package com.restapitemplate.restapitemplate.config;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.HtmlUtils;
import tools.jackson.databind.ObjectMapper;

/**
 * Owns the /error endpoint that every error dispatch lands on. It writes the problem detail body straight to the response.
 *
 * @author KatariinaJ
 * @version 2026-10-02
 */
@Controller
public class ProblemDetailErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;
    private final ObjectMapper objectMapper;

    public ProblemDetailErrorController(ErrorAttributes errorAttributes,
        ObjectMapper objectMapper) {

        this.errorAttributes = errorAttributes;
        this.objectMapper = objectMapper;
    }

    // Browsers asking for HTML still get a page, matching the old
    @RequestMapping(path = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public void errorHtml(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        Map<String, Object> attributes = readAttributes(request);
        int status = statusOf(attributes);

        response.setStatus(status);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(errorPage(status, String.valueOf(attributes.get("title"))));
    }

    // Every other response is problem detail
    @RequestMapping("/error")
    public void error(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        Map<String, Object> attributes = readAttributes(request);

        response.setStatus(statusOf(attributes));
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(attributes));
    }

    private Map<String, Object> readAttributes(HttpServletRequest request) {

        return errorAttributes.getErrorAttributes(
            new ServletWebRequest(request), ErrorAttributeOptions.defaults());
    }

    private int statusOf(Map<String, Object> attributes) {

        if (attributes.get("status") instanceof Integer status) {
            return status;
        }
        // No status attribute means the request never reached a handler
        return 500;
    }

    // Title becomes markup on HTML pages
    static String errorPage(int status, String title) {

        return "<html><body><h1>" + status + "</h1><p>"
            + HtmlUtils.htmlEscape(title) + "</p></body></html>\n";
    }
}