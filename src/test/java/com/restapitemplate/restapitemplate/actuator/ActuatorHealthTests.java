package com.restapitemplate.restapitemplate.actuator;

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
 * The health endpoint answers while the whole stack boots for real:
 * the status is UP only when the datasource behind it is live.
 *
 * @author KatariinaJ
 * @version 2026-10-01
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ActuatorHealthTests {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void healthEndpointReportsUp() {

        ResponseEntity<String> response = rest.getForEntity(
            "/actuator/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().endsWith("\n"),
            "JSON body should end with a newline");
        assertTrue(response.getBody().contains("\"status\":\"UP\""),
            "health body should report UP");
    }

    // The discovery page stays off
    @Test
    void actuatorDiscoveryPageIsNotFound() {

        ResponseEntity<String> response = rest.getForEntity(
            "/actuator", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("\"title\":\"Route not found\""),
            "the discovery page should land on the problem detail 404");
    }
}