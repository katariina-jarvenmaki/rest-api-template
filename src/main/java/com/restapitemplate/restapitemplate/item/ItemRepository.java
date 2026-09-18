package com.restapitemplate.restapitemplate.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access for items. An interface extending JpaRepository.
 * 
 * @author KatariinaJ
 * @version 2026-09-17
 */

// Type params: which entity (Item)
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {}