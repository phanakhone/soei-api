package com.example.soeiapi.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.entities.UserEntity;

import com.example.soeiapi.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                getAuthorities(user.getRole()));
    }

    // private Collection<? extends GrantedAuthority> getAuthorities(RoleEntity
    // role) {
    // return role.getRolePermissions().stream()
    // .map(rolePermission -> new
    // SimpleGrantedAuthority(rolePermission.getPermission().getPermissionName()))
    // .collect(Collectors.toList());
    // }

    private Collection<? extends GrantedAuthority> getAuthorities(RoleEntity role) {
        // Create a SimpleGrantedAuthority using the role name
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +
                role.getRoleName()));
    }

    // private Collection<? extends GrantedAuthority> getAuthorities(RoleEntity
    // role) {
    // // Start with the role as an authority
    // List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    // // Add the role as a "ROLE_" authority (Spring Security convention)
    // authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
    // System.out.println(role.getRolePermissions());

    // // Add permissions associated with this role
    // role.getRolePermissions().forEach(rolePermission -> {
    // // Add the permission as a separate authority
    // authorities.add(new SimpleGrantedAuthority("PERM_" +
    // rolePermission.getPermission().getPermissionName()));
    // });

    // return authorities;
    // }

}
