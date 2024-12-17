package com.theanh.dev.IAM_Service.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    private String username;
    private String password;
    private String fullname;
    private String address;
    private int phone;
    private Date doB;
}
