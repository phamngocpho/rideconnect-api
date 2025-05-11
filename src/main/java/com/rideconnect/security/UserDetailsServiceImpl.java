package com.rideconnect.security;

import com.rideconnect.entity.User;
import com.rideconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrId) throws UsernameNotFoundException {
        User user;

        try {
            UUID uuid = UUID.fromString(usernameOrId);
            user = userRepository.findById(uuid)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + usernameOrId));
        } catch (IllegalArgumentException e) {
            user = userRepository.findByPhoneNumber(usernameOrId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + usernameOrId));
        }

        return new CustomUserDetails(
                user.getUserId(),
                user.getPhoneNumber(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
