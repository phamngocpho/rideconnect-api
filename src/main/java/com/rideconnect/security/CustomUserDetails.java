package com.rideconnect.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    @Getter
    private final UUID userId;
    private final String phoneNumber;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public CustomUserDetails(UUID userId, String phoneNumber, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this(userId, phoneNumber, password, authorities, true);
    }

    public CustomUserDetails(UUID userId, String phoneNumber, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             boolean enabled) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
