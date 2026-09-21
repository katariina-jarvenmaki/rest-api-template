package com.restapitemplate.restapitemplate.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;    
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * First endpoint test: boots the real application on a random port and asks
 * /items for its list. Passes only when controller, repository and H2 all
 * answer together.
 *
 * @author KatariinaJ
 * @version 2026-09-18
 */

// Compiled only for test runs, never packed into the shipped jar.
// Makes sure that test boots on a free port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Points TestRestTemplate at the chosen port
@AutoConfigureTestRestTemplate

class ItemControllerTests {

    // TestRestTemplate auto-aims at the random port, so a "/items" is enough.
    @Autowired
    private TestRestTemplate rest;

    @Test
    void listIsEmptyBeforeAnythingIsStored() {

        ResponseEntity<String> response = rest.getForEntity("/items", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }

    @Test
    // The 404 body is Spring's default, so we only assert the status.
    void missingItemReturns404() {

        // 999 never exists, so H2 is fresh at every boot.
        ResponseEntity<String> response = rest.getForEntity("/items/999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}