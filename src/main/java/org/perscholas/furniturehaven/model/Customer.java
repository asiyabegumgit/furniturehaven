package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@DiscriminatorValue("CUSTOMER")

public class Customer extends User{
   /* @Id
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
    private Role role;*/

    @OneToOne(mappedBy = "customer" , cascade = CascadeType.ALL)
    private Cart cart;

    public Customer() {
        super();
        setRole(Role.CUSTOMER);
    }
}
