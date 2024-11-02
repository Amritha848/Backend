package com.riverside.tamarind.entityimplements;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.riverside.tamarind.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class UserDetailsInfo implements UserDetails {


    private String userId;
    private String password;
    private List<GrantedAuthority> grantedAuthorities;

    public UserDetailsInfo(User user){
        userId=user.getUserId();
        password=user.getPassword();
        grantedAuthorities=Stream.of(user.getRole().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
   }
   
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
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
        return true;
    }
    
}



