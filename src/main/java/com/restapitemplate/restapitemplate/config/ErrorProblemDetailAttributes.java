package com.restapitemplate.restapitemplate.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Reformats the legacy /error body into problem detail
 *
 * @author KatariinaJ
 * @version 2026-10-02
 */
@Component
public class ErrorProblemDetailAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest,
        ErrorAttributeOptions options) {

        Map<String, Object> legacy = super.getErrorAttributes(webRequest, options);

        String title;
        Object status = legacy.get("status");
        if (Integer.valueOf(400).equals(status)) {
            title = "Bad request";
        } else if (Integer.valueOf(404).equals(status)) {
            title = "Route not found";
        } else if (Integer.valueOf(405).equals(status)) {
            title = "Method not allowed";
        } else if (Integer.valueOf(406).equals(status)) {
            title = "Not acceptable";
        } else if (Integer.valueOf(415).equals(status)) {
            title = "Unsupported media type";
        } else {
            title = String.valueOf(legacy.get("error"));
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("status", status);
        if (legacy.get("path") instanceof String path) {
            body.put("instance", path);
        }
        return body;
    }
}