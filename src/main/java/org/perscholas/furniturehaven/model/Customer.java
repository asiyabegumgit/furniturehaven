package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Customer extends User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String password;
    private String preferredContact;
    private boolean subscribedToNewsletter;
    @Enumerated
    private Role role;
    @OneToMany(mappedBy = "customer" , cascade = CascadeType.ALL)
    private List<Cart> carts;
}
