package com.theanh.dev.IAM_Service.Repository;

import com.theanh.dev.IAM_Service.Model.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
}
