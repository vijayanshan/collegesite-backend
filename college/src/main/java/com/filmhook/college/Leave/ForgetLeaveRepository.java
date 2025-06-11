package com.filmhook.college.Leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgetLeaveRepository extends JpaRepository<ForgetLeave, Long> {
    ForgetLeave findByUserId(String userId); // Corrected to match String type
}
