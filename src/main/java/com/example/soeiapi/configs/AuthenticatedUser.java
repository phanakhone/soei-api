package com.example.soeiapi.configs;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.example.soeiapi.entities.UserEntity;

public class AuthenticatedUser extends User {
    private UserEntity userEntity;

    public AuthenticatedUser(UserEntity userEntity, Collection<? extends GrantedAuthority> authorities) {
        super(userEntity.getUsername(), userEntity.getPassword(), authorities);
        this.userEntity = userEntity;
    }

    public AuthenticatedUser(UserEntity userEntity, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(userEntity.getUsername(), userEntity.getPassword(), enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
        this.userEntity = userEntity;
    }

    public UserEntity getUser() {
        return userEntity;
    }
}
