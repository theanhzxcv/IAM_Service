package com.theanh.dev.IAM_Service.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@Table(name = "user_activity")
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String ip;

    private String email;

    private String activity;

    private Date timestamp;

}
