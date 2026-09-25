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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * First endpoint test: boots the real application on a random port and asks
 * /items for its list. Passes only when controller, repository and H2 all
 * answer together.
 *
 * @author KatariinaJ
 * @version 2026-09-18
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ItemControllerTests {

    @Autowired
    private TestRestTemplate rest;

    // The response body should end with a newline
    private void bodyEndsWithNewline(ResponseEntity<String> response) {

        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.endsWith("\n"),
            "JSON body should end with a newline");
    }

    @Test
    void listIsEmptyBeforeAnythingIsStored() {

        ResponseEntity<String> response = rest.getForEntity("/items", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        bodyEndsWithNewline(response);
    }

    @Test
    void missingItemReturns404() {

        ResponseEntity<String> response = rest.getForEntity("/items/999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        bodyEndsWithNewline(response);
    }

    @Test
    void createdItemIsStoredAndReturns201() {

        Item sent = new Item();
        sent.setName("Test item");
        sent.setDescription("Created by a write-path test");

        ResponseEntity<Item> response =
            rest.postForEntity("/items", sent, Item.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test item", response.getBody().getName());

        rest.delete("/items/" + response.getBody().getId());
    }

    @Test
    void createdItemIgnoresIdFromRequestBody() {

        Item sent = new Item();
        sent.setId(999L);
        sent.setName("Body-sent id");

        ResponseEntity<Item> response =
            rest.postForEntity("/items", sent, Item.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotEquals(999L, response.getBody().getId());

        rest.delete("/items/" + response.getBody().getId());
    }

    @Test
    void blankItemNameReturns400() {

        Item sent = new Item();
        sent.setName("   ");

        ResponseEntity<String> response =
            rest.postForEntity("/items", sent, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        bodyEndsWithNewline(response);
    }

    @Test
    void missingItemNameReturns400() {

        Item sent = new Item();

        ResponseEntity<String> response =
            rest.postForEntity("/items", sent, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        bodyEndsWithNewline(response);
    }

    @Test
    void updatedItemReturnsUpdatedValues() {

        Item sent = new Item();
        sent.setName("Original name");
        ResponseEntity<Item> created =
            rest.postForEntity("/items", sent, Item.class);

        Item changed = new Item();
        changed.setName("Updated name");
        changed.setDescription("Updated by a write-path test");

        ResponseEntity<Item> response = rest.exchange(
            "/items/" + created.getBody().getId(),
            HttpMethod.PUT,
            new HttpEntity<>(changed),
            Item.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated name", response.getBody().getName());

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

        Item sent = new Item();
        sent.setName("Doomed row");
        ResponseEntity<Item> created =
            rest.postForEntity("/items", sent, Item.class);

        ResponseEntity<String> deleted = rest.exchange(
            "/items/" + created.getBody().getId(),
            HttpMethod.DELETE,
            null,
            String.class);

        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
        assertNull(deleted.getBody());

        ResponseEntity<String> after =
            rest.getForEntity("/items/" + created.getBody().getId(), String.class);
        assertEquals(HttpStatus.NOT_FOUND, after.getStatusCode());
    }
}
