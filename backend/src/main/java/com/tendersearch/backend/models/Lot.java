package com.tendersearch.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Lot {
    @Id
    @GeneratedValue
    public long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    public String name;
    @Column(nullable = false, columnDefinition = "TEXT")
    public String info;
}
