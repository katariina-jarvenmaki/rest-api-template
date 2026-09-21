package com.restapitemplate.restapitemplate.item;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/**
 * Rules between controller and repository live here.
 * 
 * @author KatariinaJ
 * @version 2026-09-17
 */
// Same family as @Repository and @RestController: Spring creates the one instance and injects it where needed.
@Service
public class ItemService {

    // Variable that's used by ItemService only
    private final ItemRepository itemRepository;

    // Constructor: Field getting its value
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // ItemRepository handover for the list 
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Forward the call to the repository and return the result
    public Item getItem(Long id) {

        // findById gives an Optional
        return itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Saving: INSERT when the id is empty, UPDATE when the id is set
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    // Updating
    public Item updateItem(Long id, Item item) {

        // Same 404 rule as getItem: a missing row means 404, never a silent create.
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // The URL decides which row is updated; the body never gets to pick a row.
        item.setId(id);
        return itemRepository.save(item);
    }

    // Deleting
    public void deleteItem(Long id) {

        // deleteById on a missing row throws a raw exception, so we turn it into a 404 ourselves.
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        itemRepository.deleteById(id);
    }
}