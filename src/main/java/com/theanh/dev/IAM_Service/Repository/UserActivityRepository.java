package com.theanh.dev.IAM_Service.Repository;

import com.theanh.dev.IAM_Service.Model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, String> {
}
