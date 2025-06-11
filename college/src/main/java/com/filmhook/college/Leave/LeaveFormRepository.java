package com.filmhook.college.Leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;  // <-- Add this import for List

@Repository
public interface LeaveFormRepository extends JpaRepository<LeaveForm, Long> {
    // Custom query methods can be added here
    
    List<LeaveForm> findByStaffIdAndStatus(Long staffId, String status);

	List<LeaveForm> findByStatusIn(List<String> statuses);
}
