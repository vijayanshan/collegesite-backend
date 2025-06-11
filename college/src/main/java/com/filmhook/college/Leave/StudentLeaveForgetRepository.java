package com.filmhook.college.Leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentLeaveForgetRepository extends JpaRepository<StudentLeaveForget, Long> {
    StudentLeaveForget findByUserId(String userId);
}
