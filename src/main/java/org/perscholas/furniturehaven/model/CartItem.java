package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="cart_id",nullable=false)
    private Cart cart;
    @ManyToOne
    @JoinColumn(name="product_id",nullable=false)
    private Product product;
    private int quantity;
}
