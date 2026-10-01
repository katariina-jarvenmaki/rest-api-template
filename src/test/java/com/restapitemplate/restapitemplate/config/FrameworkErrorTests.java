package com.restapitemplate.restapitemplate.config;

import com.restapitemplate.restapitemplate.item.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pair for the framework-level error paths: exceptions raised around the controllers.
 *
 * @author KatariinaJ
 * @version 2026-10-01
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class FrameworkErrorTests {

    @Autowired
    private TestRestTemplate rest;

    // The response body should be problem detail with the given title
    private void assertProblemDetail(ResponseEntity<String> response,
                                     HttpStatus status, String title) {

        assertEquals(status, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"title\":\"" + title + "\""),
            status + " body should be problem detail titled '" + title + "'");
    }

    @Test
    void unknownRouteReturnsProblemDetail404() {

        ResponseEntity<String> response =
            rest.getForEntity("/no/such/route", String.class);

        assertProblemDetail(response, HttpStatus.NOT_FOUND, "Route not found");
    }

    @Test
    void malformedJsonReturnsProblemDetail400() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = rest.exchange(
            "/items",
            HttpMethod.POST,
            new HttpEntity<>("{not valid json", headers),
            String.class);

        assertProblemDetail(response, HttpStatus.BAD_REQUEST, "Malformed request body");
    }

    @Test
    void nonNumericIdReturnsProblemDetail400() {

        ResponseEntity<String> response =
            rest.getForEntity("/items/abc", String.class);

        assertProblemDetail(response, HttpStatus.BAD_REQUEST, "Invalid path variable");
    }

    @Test
    void unsupportedMethodReturnsProblemDetail405() {

        ResponseEntity<String> response = rest.exchange(
            "/items",
            HttpMethod.PATCH,
            null,
            String.class);

        assertProblemDetail(response, HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed");
    }

    @Test
    void oversizedNameReturnsValidation400Not500() {

        Item sent = new Item();
        sent.setName("x".repeat(300));

        ResponseEntity<String> response =
            rest.postForEntity("/items", sent, String.class);

        assertProblemDetail(response, HttpStatus.BAD_REQUEST, "Validation failed");
        assertTrue(response.getBody().contains("\"invalidField\":\"name\""),
            "400 body should point at the name field");
    }
}