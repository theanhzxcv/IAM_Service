package com.theanh.dev.IAM_Service.Repositories;

import com.theanh.dev.IAM_Service.Models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Roles, String> {
}
