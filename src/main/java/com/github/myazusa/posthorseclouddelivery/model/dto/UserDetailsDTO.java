package com.github.myazusa.posthorseclouddelivery.model.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class UserDetailsDTO implements UserDetails {
    @Getter
    private final UUID uuid;
    private final String password;
    private final String username;
    @Getter
    private final String phone;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsDTO(UUID uuid, final String username, final String password, String phone, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.uuid = uuid;
        this.phone = phone;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}
