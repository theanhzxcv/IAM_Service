package com.theanh.dev.IAM_Service.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
public class Permissions {

    @Id
    @Column(name = "permission_name")
    private String name;
    @Column(name = "permission_resource")
    private String resource;
    @Column(name = "permission_scope")
    private String scope;

    private boolean isDeleted = false;
}
