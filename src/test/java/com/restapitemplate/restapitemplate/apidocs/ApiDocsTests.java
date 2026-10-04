package com.restapitemplate.restapitemplate.apidocs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The OpenAPI is served for machines, and the Swagger UI page for browsers, both from the same running stack.
 *
 * @author KatariinaJ
 * @version 2026-10-02
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ApiDocsTests {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void apiDocsServesOpenApiJson() {

        ResponseEntity<String> response = rest.getForEntity(
            "/v3/api-docs", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().endsWith("\n"),
            "JSON body should end with a newline");
        assertTrue(response.getBody().contains("\"openapi\":\"3"),
            "body should be an OpenAPI document");
        assertTrue(response.getBody().contains("\"/items\""),
            "the item endpoints should be in the document");
    }

    @Test
    void swaggerUiPageIsServed() {

        ResponseEntity<String> response = rest.getForEntity(
            "/swagger-ui/index.html", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Swagger UI"),
            "the Swagger UI page should be served");
    }
}