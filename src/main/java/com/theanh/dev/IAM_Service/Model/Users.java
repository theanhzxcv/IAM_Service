package com.theanh.dev.IAM_Service.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String firstname;
    private String lastname;

    private String email;
    private String password;

    private String address;
    private int phone;
    private Date doB;

    private String secret;
    private String imageUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
