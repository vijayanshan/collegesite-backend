package com.filmhook.college.Response;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveResponseRepository extends JpaRepository<LeaveResponse, Long> {
    // Future custom queries here
}

