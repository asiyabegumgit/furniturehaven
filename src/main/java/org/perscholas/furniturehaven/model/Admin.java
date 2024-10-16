package org.perscholas.furniturehaven.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Admin extends User{

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }
}
