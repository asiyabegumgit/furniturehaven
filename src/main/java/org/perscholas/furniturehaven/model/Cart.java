package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    private double subtotal;
    private double tax;
    private double totalPriceWithTax;

    private static final double TAX_RATE = 0.1; // Example tax rate of 10%
    public int getTotalQuantity()
    {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public void calculateTotalPrice() {
        subtotal = items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        tax = subtotal * TAX_RATE; // Calculate tax
        totalPriceWithTax = subtotal + tax; // Calculate total price including tax
    }



}
