package com.restapitemplate.restapitemplate.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One row in the items table, as a Java object. Placeholder object of the template for future entities the program may have.
 * 
 * @author KatariinaJ
 * @version 2026-09-17
 */

// Plain class, not a record: JPA loads rows empty-then-fill, records are immutable.
@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor // Required by JPA

public class Item {

    // IDENTITY = the database auto-increments this; never set it
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable=false becomes NOT NULL in the items table.
    @Column(nullable = false)
    private String name;

    private String description;
}