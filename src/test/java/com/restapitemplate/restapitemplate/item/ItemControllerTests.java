package com.restapitemplate.restapitemplate.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;    
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void createdItemIsStoredAndReturns201() {

        // Arranging...
        Item sent = new Item();
        sent.setName("Test item");
        sent.setDescription("Created by a write-path test");

        // Act: Test posting
        ResponseEntity<Item> response =
            rest.postForEntity("/items", sent, Item.class);

        // Assert: 201 and the row came back with a generated id.
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test item", response.getBody().getName());

        // Cleanup: delete what we created.
        rest.delete("/items/" + response.getBody().getId());
    }

    @Test
    void updatedItemReturnsUpdatedValues() {

        // Arrange: create one row to update.
        Item sent = new Item();
        sent.setName("Original name");
        ResponseEntity<Item> created =
            rest.postForEntity("/items", sent, Item.class);

        // Arrange: the replacement values.
        Item changed = new Item();
        changed.setName("Updated name");
        changed.setDescription("Updated by a write-path test");

        // Act: PUT to the row's own URL.
        ResponseEntity<Item> response = rest.exchange(
            "/items/" + created.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(changed),
            Item.class);

        // Assert: 200 and the new values.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated name", response.getBody().getName());

        // Cleanup.
        rest.delete("/items/" + created.getBody().getId());
    }

    @Test
    void updateMissingItemReturns404() {

        Item changed = new Item();
        changed.setName("No such row");

        ResponseEntity<String> response = rest.exchange(
            "/items/999",
            HttpMethod.PUT,
            new HttpEntity<>(changed),
            String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteMissingItemReturns404() {

        ResponseEntity<String> response = rest.exchange(
            "/items/999",
            HttpMethod.DELETE,
            null,
            String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteExistingItemReturns204AndRowIsGone() {

        // Arrange: create a row, then delete it.
        Item sent = new Item();
        sent.setName("Doomed row");
        ResponseEntity<Item> created =
            rest.postForEntity("/items", sent, Item.class);

        ResponseEntity<String> deleted = rest.exchange(
            "/items/" + created.getBody().getId(),
            HttpMethod.DELETE,
            null,
            String.class);

        // Assert: 204 and no body.
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        assertNull(deleted.getBody());

        // Assert: the row is gone.
        ResponseEntity<String> after =
            rest.getForEntity("/items/" + created.getBody().getId(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, after.getStatusCode());
    }
}