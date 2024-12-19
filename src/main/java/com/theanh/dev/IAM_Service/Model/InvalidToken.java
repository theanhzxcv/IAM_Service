package com.theanh.dev.IAM_Service.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@Table(name = "invalidTokens")
@NoArgsConstructor
@AllArgsConstructor
public class InvalidToken {

    @Id
    String id;

    Date expiredTime;
}
