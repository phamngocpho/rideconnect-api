package com.rideconnect.service.impl;

import com.rideconnect.entity.User;
import com.rideconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user: {}", username);

        User user = userRepository.findByEmailOrPhoneNumber(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
                });

        log.debug("User found: {} with role: {}", user.getFullName(), user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhoneNumber())
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(user.getRole().getRoleName()) // Sẽ trả về ROLE_ADMIN
                ))
                .accountExpired(false)
                .accountLocked(user.getStatus() != User.UserStatus.ACTIVE)
                .credentialsExpired(false)
                .disabled(user.getStatus() == User.UserStatus.INACTIVE)
                .build();
    }
}
