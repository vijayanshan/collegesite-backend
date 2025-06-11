package com.filmhook.college.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.filmhook.college.Leave.EmailService;

@RestController
@RequestMapping("/response")
public class LeaveResponseController {

    @Autowired
    private LeaveResponseRepository leaveResponseRepository;  // <-- add this

    @Autowired
    private EmailService emailService;

    // ✅ PUT - Approve
    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id, @RequestParam String staffName) {
        LeaveResponse res = leaveResponseRepository.findById(id).orElse(null);
        if (res != null) {
            res.setApprovedBy(staffName);
            res.setRejectedBy(null);
            leaveResponseRepository.save(res);

            // 📨 Send Email
            String subject = "Leave Request Approved";
            String body = "Hi " + res.getName() + ",\n\nYour leave has been approved by " + staffName + ".\n\nThanks!";
            emailService.sendMail(res.getEmail(), subject, body);

            return ResponseEntity.ok("Approved and mail sent.");
        }
        return ResponseEntity.notFound().build();
    }

    // ✅ PUT - Reject
    @PutMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id, @RequestParam String staffName) {
        LeaveResponse res = leaveResponseRepository.findById(id).orElse(null);
        if (res != null) {
            res.setRejectedBy(staffName);
            res.setApprovedBy(null);
            leaveResponseRepository.save(res);

            // 📨 Send Email
            String subject = "Leave Request Rejected";
            String body = "Hi " + res.getName() + ",\n\nYour leave has been rejected by " + staffName + ".\n\nPlease contact for clarification.";
            emailService.sendMail(res.getEmail(), subject, body);

            return ResponseEntity.ok("Rejected and mail sent.");
        }
        return ResponseEntity.notFound().build();
    }
}
