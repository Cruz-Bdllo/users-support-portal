package com.code.supportportal.principal;

import com.code.supportportal.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UserDetailsPrincipal implements UserDetails {

    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;
    private boolean isActive;
    private boolean isNotLocked;

    public UserDetailsPrincipal(User user) {
        password = user.getPassword();
        username = user.getUsername();
        isActive = user.isActive();
        isNotLocked = user.isNotLocked();
        authorities = stream(user.getAuthorities()).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    } // end constructor

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
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isNotLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
