package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Guest extends User{
    @OneToOne(mappedBy = "customer" , cascade = CascadeType.ALL)
    private Cart cart;
    public Guest() {
        super();
        setRole(Role.GUEST);
    }
}
