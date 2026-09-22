package com.restapitemplate.restapitemplate.item;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import java.util.List;

/**
 * HTTP layer for Items that answers requests to /items and returns JSON. The only layer that talks to the outside world.
 * 
 * @author KatariinaJ
 * @version 2026-09-17
 */

@RestController
@RequestMapping("/items")

public class ItemController {
    
    private final ItemService itemService;
    
    // Constructor injection: Spring hands the service in; we never call new.
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    // Spring Data runs SELECT * FROM items; no SQL visible in our code.
    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // {id} in the path maps to the method argument so /items/5 means id = 5.
    @GetMapping("/{id}")
    public Item getItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    // Bound POST JSON into an Item so service can save it
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 is new resource created and default would be 200
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    // Replacing whole rows with URL names. Service 404s if missing.
    @PutMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody Item item) {
        return itemService.updateItem(id, item);
    }

    // Deleting the row so there is nothing to return. Code for success is 204 = success
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
