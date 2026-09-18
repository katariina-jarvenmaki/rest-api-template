package com.restapitemplate.restapitemplate.item;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    
    private final ItemRepository itemRepository;

    // Constructor injection: Spring hands the repository in; we never call new.
    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
    
    // findAll = Spring Data runs SELECT * FROM items; no SQL visible in our code.
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
