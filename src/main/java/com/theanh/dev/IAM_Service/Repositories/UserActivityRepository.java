package com.theanh.dev.IAM_Service.Repositories;

import com.theanh.dev.IAM_Service.Models.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, String> {
}
