package com.filmhook.college;


import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepositary extends JpaRepository<College, Long> {
    // Additional query methods can go here if needed
}
