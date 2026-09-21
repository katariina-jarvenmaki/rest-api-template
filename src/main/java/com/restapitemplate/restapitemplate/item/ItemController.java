package com.restapitemplate.restapitemplate.item;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
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
}
