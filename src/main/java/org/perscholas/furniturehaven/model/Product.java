package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    @Lob
    private String description;
    private String image;
    private String category;
    private String brand;
    private String rating;
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
