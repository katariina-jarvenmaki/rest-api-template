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
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItem(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Item createItem(Item item) {

        // POST always creates a fresh row: the body's id, if any, is cleared so
        // the database assigns one. Same ownership rule as updateItem, mirrored.
        item.setId(null);
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item item) {

        // Same 404 rule as getItem: a missing row means 404, never a silent create.
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // The URL decides which row is updated; the body never gets to pick a row.
        item.setId(id);
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {

        // deleteById on a missing row throws a raw exception, so we turn it into a 404 ourselves.
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        itemRepository.deleteById(id);
    }
}
