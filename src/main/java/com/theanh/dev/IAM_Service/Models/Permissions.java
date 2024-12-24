package com.theanh.dev.IAM_Service.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String resourceCode;// (User, Book, ...)
    private String scope; // (write,
    private String description;
    private boolean isDeleted = false;
}
