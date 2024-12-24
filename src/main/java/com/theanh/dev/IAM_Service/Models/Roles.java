package com.theanh.dev.IAM_Service.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Roles implements GrantedAuthority {

    @Id
    @Column(name = "role_name")
    private String name;
    private String description;
    private boolean isDeleted = false;

    @ManyToMany
    private Set<Permissions> permissions;

    @Override
    public String getAuthority() {
        return name;
    }
}
