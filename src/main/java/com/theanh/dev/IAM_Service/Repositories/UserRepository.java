package com.theanh.dev.IAM_Service.Repositories;

import com.theanh.dev.IAM_Service.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.email LIKE %:email%")
    List<Users> findByEmailContaining(String email);


}
