package org.perscholas.furniturehaven.model;

import lombok.Data;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Getter
public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;

    private String name; // Additional field for the user's name
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, String name, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.authorities = authorities;
    }

}