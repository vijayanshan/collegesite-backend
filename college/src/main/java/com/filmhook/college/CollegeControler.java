package com.filmhook.college;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/colleges")
public class CollegeControler {

    @Autowired
    private CollegeRepositary collegeRepository;

    // Create a new college
    @PostMapping("/add")
    public College addCollege(@RequestBody College college) {
        return collegeRepository.save(college);
    }

    // Get all colleges
    @GetMapping("/all")
    public List<College> getAllColleges() {
        return collegeRepository.findAll();
    }
 
    // Get a college by ID
    @GetMapping("/{id}")
    public College getCollegeById(@PathVariable Long id) {
        return collegeRepository.findById(id).orElse(null);
    }

    // Delete a college by ID
    @DeleteMapping("/{id}")
    public String deleteCollege(@PathVariable Long id) {
        collegeRepository.deleteById(id);
        return "College deleted with id: " + id;
    }

    // Update a college by ID
    @PutMapping("/update/{id}")
    public College updateCollege(@PathVariable Long id, @RequestBody College updatedCollege) {
        Optional<College> optionalCollege = collegeRepository.findById(id);

        if (optionalCollege.isPresent()) {
            College existingCollege = optionalCollege.get();
            existingCollege.setName(updatedCollege.getName());
            existingCollege.setEmail(updatedCollege.getEmail());
            existingCollege.setPhno(updatedCollege.getPhno());
            // Add other fields as necessary

            return collegeRepository.save(existingCollege);
        } else {
            return null; // or throw an exception
        }
    }
    
    
    
    
}
