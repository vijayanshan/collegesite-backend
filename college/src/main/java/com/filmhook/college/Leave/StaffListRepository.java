package com.filmhook.college.Leave;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffListRepository extends JpaRepository<StaffList, Long> {
    StaffList findByEmail(String email); // Fixed: parameter type should be String
}
