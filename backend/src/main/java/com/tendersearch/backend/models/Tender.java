package com.tendersearch.backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(
        uniqueConstraints =
                @UniqueConstraint(columnNames = {"v_key"})

)
public class Tender {
    @Id
    @GeneratedValue
    public Long id;
    @Column(nullable = false)
    public String name;
    @Column(nullable = false)
    public String vKey;
    // Search
    @Column(columnDefinition = "TEXT")
    public String customer;
    public String price;
    public String currency;
    // Extend
    public String url;
    @Column(columnDefinition = "TEXT")
    public String description;
    @Column(columnDefinition = "TEXT")
    public String otherInfo;
    @Column(columnDefinition = "TEXT")
    public String customerData;
    @Column(columnDefinition = "TEXT")
    public String customerContacts;
    public LocalDateTime createdAt;
    public LocalDateTime closedAt;
    @OneToMany(cascade = CascadeType.ALL)
    public List<File> files;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Lot> lots;
}
