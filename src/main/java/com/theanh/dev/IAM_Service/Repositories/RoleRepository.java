package com.theanh.dev.IAM_Service.Repositories;

import com.theanh.dev.IAM_Service.Models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, String> {
    Optional<Roles> findByName(String name);
    boolean existsByName(String name);
}
