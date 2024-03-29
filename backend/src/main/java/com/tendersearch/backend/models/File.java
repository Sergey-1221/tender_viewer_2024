package com.tendersearch.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class File {
    @Id
    @GeneratedValue
    public long id;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String url;
}
