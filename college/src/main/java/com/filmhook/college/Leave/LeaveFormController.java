package com.filmhook.college.Leave;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaves")
public class LeaveFormController {

    @Autowired
    private LeaveFormRepository leaveFormRepository;

    @Autowired
    private StudentListRepository studentListRepository;

    @Autowired
    private StaffListRepository staffListRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgetLeaveRepository forgetLeaveRepository;

    @Autowired
    private StudentLeaveForgetRepository studentLeaveForgetRepository;

    @Autowired
    private JavaMailSender mailSender;
    
   

    
    
   

    // 1. Get all leave applications
    @GetMapping("/all")
    public List<LeaveForm> getAllLeaves() {
        return leaveFormRepository.findAll();
    }

    // 2. Get leave by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveForm> getLeaveById(@PathVariable Long id) {
        return leaveFormRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Leave Summary DTO
    @GetMapping("/summary")
    public List<LeaveSummary> getLeaveSummaries() {
        return leaveFormRepository.findAll().stream()
                .map(leave -> new LeaveSummary(
                        leave.getUserId(),
                        leave.getName(),
                        leave.getEmail(),
                        leave.getDepartment(),
                        leave.getLeaveDate(),
                        leave.getReason(),
                        leave.getStatus()))
                .collect(Collectors.toList());
    }

    // 4. Apply for Leave
    @PostMapping("/apply")
    public ResponseEntity<?> createLeave(@RequestBody LeaveForm leaveForm) {
        Optional<StudentList> student = studentListRepository.findById(leaveForm.getUserId());
        Optional<StaffList> staff = staffListRepository.findById(leaveForm.getStaffId());

        if (student.isEmpty() || staff.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid Student or Staff ID.");
        }

        StaffList staffMember = staff.get();
        leaveForm.setApprovedBy(staffMember.getName());
        LeaveForm savedLeave = leaveFormRepository.save(leaveForm);

        String subject = "New Leave Request from " + savedLeave.getName();
        String body = "Hi " + staffMember.getName() + ",\n\n" +
                "You have received a new leave request:\n\n" +
                "Student Name: " + savedLeave.getName() + "\n" +
                "Email: " + savedLeave.getEmail() + "\n" +
                "Department: " + savedLeave.getDepartment() + "\n" +
                "Leave Date: " + savedLeave.getLeaveDate() + "\n" +
                "Reason: " + savedLeave.getReason() + "\n\n" +
                "Please login to the system to approve or reject this leave.\n\n" +
                "Thanks,\nCollege Leave Management System";

        emailService.sendMail(staffMember.getEmail(), subject, body);

        return ResponseEntity.ok(savedLeave);
    }

    // 5. Update Leave
    @PutMapping("/{id}")
    public ResponseEntity<LeaveForm> updateLeave(@PathVariable Long id, @RequestBody LeaveForm updatedLeave) {
        return leaveFormRepository.findById(id).map(leave -> {
            leave.setUserId(updatedLeave.getUserId());
            leave.setName(updatedLeave.getName());
            leave.setEmail(updatedLeave.getEmail());
            leave.setDepartment(updatedLeave.getDepartment());
            leave.setLeaveDate(updatedLeave.getLeaveDate());
            leave.setReason(updatedLeave.getReason());
            leave.setStatus(updatedLeave.getStatus());
            leave.setApprovedBy(updatedLeave.getApprovedBy());
            leave.setRejectedBy(updatedLeave.getRejectedBy());
            leave.setDecisionDate(updatedLeave.getDecisionDate());
            return ResponseEntity.ok(leaveFormRepository.save(leave));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6. Delete Leave
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeave(@PathVariable Long id) {
        if (leaveFormRepository.existsById(id)) {
            leaveFormRepository.deleteById(id);
            return ResponseEntity.ok("✅ Leave deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    // 7. Approve Leave
    @GetMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id,
                                          @RequestParam Long staffId,
                                          @RequestParam String decisionDate) {
        Optional<LeaveForm> leaveOpt = leaveFormRepository.findById(id);
        Optional<StaffList> staffOpt = staffListRepository.findById(staffId);

        if (leaveOpt.isEmpty() || staffOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid Leave or Staff ID.");
        }

        LeaveForm leave = leaveOpt.get();
        StaffList staff = staffOpt.get();

        leave.setStatus("APPROVED");
        leave.setApprovedBy(staff.getName());
        leave.setRejectedBy(null);
        leave.setDecisionDate(decisionDate);
        leaveFormRepository.save(leave);

        String subject = "✅ Leave Approved";
        String body = "Hi " + leave.getName() + ",\n\nYour leave request for " + leave.getLeaveDate() +
                " has been APPROVED.\n\nApproved By: " + staff.getName() + "\nEmail: " + staff.getEmail() +
                "\n\nThanks,\nCollege Leave Management System";

        emailService.sendMail(leave.getEmail(), subject, body);

        return ResponseEntity.ok(leave);
    }

    // 8. Reject Leave
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id,
                                         @RequestParam Long staffId,
                                         @RequestParam String decisionDate) {
        Optional<LeaveForm> leaveOpt = leaveFormRepository.findById(id);
        Optional<StaffList> staffOpt = staffListRepository.findById(staffId);

        if (leaveOpt.isEmpty() || staffOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Invalid Leave or Staff ID.");
        }

        LeaveForm leave = leaveOpt.get();
        StaffList staff = staffOpt.get();

        leave.setStatus("REJECTED");
        leave.setApprovedBy(null);
        leave.setRejectedBy(staff.getName());
        leave.setDecisionDate(decisionDate);
        leaveFormRepository.save(leave);

        String subject = "❌ Leave Rejected";
        String body = "Hi " + leave.getName() + ",\n\nYour leave request for " + leave.getLeaveDate() +
                " has been REJECTED.\n\nRejected By: " + staff.getName() + "\nEmail: " + staff.getEmail() +
                "\n\nThanks,\nCollege Leave Management System";

        emailService.sendMail(leave.getEmail(), subject, body);

        return ResponseEntity.ok(leave);
    }

 // Student Login
    
    @CrossOrigin(origins = "*")
    @PutMapping("/login/student")
    public ResponseEntity<?> studentLogin(@RequestParam Long id, @RequestParam String password) {
        Optional<StudentList> student = studentListRepository.findById(id);

        if (student.isPresent() && student.get().getPassword().equals(password)) {
            return ResponseEntity.ok("✅ Student login successful. Welcome, " + student.get().getUserName() + "!");
        }

        return ResponseEntity.status(401).body("❌ Invalid Student ID or Password.");
    }

    // Staff Login
    @PutMapping("/login/staff")
    public ResponseEntity<?> staffLogin(@RequestParam Long id, @RequestParam String password) {
        Optional<StaffList> staff = staffListRepository.findById(id);

        if (staff.isPresent() && staff.get().getPassword().equals(password)) {
            return ResponseEntity.ok("✅ Staff login successful. Welcome, " + staff.get().getName() + "!");
        }

        return ResponseEntity.status(401).body("❌ Invalid Staff ID or Password.");
    }


    // 10. Send OTP to Staff
    @PostMapping("/send-otp")
    public String sendStaffOtp(@RequestParam Long userId) {
        Optional<StaffList> staffOpt = staffListRepository.findById(userId);
        if (staffOpt.isEmpty()) return "❌ User ID not found";

        StaffList staff = staffOpt.get();
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        ForgetLeave forget = forgetLeaveRepository.findByUserId(String.valueOf(userId));
        if (forget == null) forget = new ForgetLeave();

        forget.setUserId(String.valueOf(userId));
        forget.setOtp(otp);
        forgetLeaveRepository.save(forget);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(staff.getEmail());
        message.setSubject("Staff OTP for Password Reset");
        message.setText("Dear " + staff.getName() + ",\n\nYour OTP is: " + otp + "\n\n- Leave Management System");
        mailSender.send(message);

        return "✅ OTP sent to staff email.";
    }

    // 11. Verify OTP for Staff
    @PostMapping("/verify-otp")
    public String verifyStaffOtp(@RequestParam Long userId, @RequestParam String otp) {
        ForgetLeave forget = forgetLeaveRepository.findByUserId(String.valueOf(userId));
        return (forget != null && forget.getOtp().equals(otp))
                ? "✅ OTP Verified. You can now reset your password."
                : "❌ Invalid OTP.";
    }
    
    @PostMapping("/reset-password")
    public String resetStaffPassword(@RequestParam Long userId, @RequestParam String newPassword) {
        // Find staff by ID
        StaffList staff = staffListRepository.findById(userId).orElse(null);
        if (staff == null) return "❌ Staff not found";
// Update the staff password
        staff.setPassword(newPassword);
        staffListRepository.save(staff);

        // Clear the OTP after use


        return "✅ Password successfully updated!";
    }



    // 12. Send OTP to Student
    @PostMapping("/student/send-otp")
    public String sendStudentOtp(@RequestParam Long userId) {
        Optional<StudentList> studentOpt = studentListRepository.findById(userId);
        if (studentOpt.isEmpty()) return "❌ User ID not found";

        StudentList student = studentOpt.get();
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        StudentLeaveForget forget = studentLeaveForgetRepository.findByUserId(String.valueOf(userId));
        if (forget == null) forget = new StudentLeaveForget();

        forget.setUserId(String.valueOf(userId));
        forget.setOtp(otp);
        studentLeaveForgetRepository.save(forget);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Student OTP for Password Reset");
        message.setText("Dear " + student.getUserName() + ",\n\nYour OTP is: " + otp + "\n\n- Leave Management System");
        mailSender.send(message);

        return "✅ OTP sent to student email.";
    }

    // 13. Verify OTP for Student
    @PostMapping("/student/verify-otp")
    public String verifyStudentOtp(@RequestParam Long userId, @RequestParam String otp) {
        StudentLeaveForget forget = studentLeaveForgetRepository.findByUserId(String.valueOf(userId));
        return (forget != null && forget.getOtp().equals(otp))
                ? "✅ OTP Verified. You can now reset your password."
                : "❌ Invalid OTP.";
    }
    
    @PostMapping("/student/reset-password")
    public String resetStudentPassword(@RequestParam Long userId, @RequestParam String newPassword) {
        // Find student by ID
        StudentList student = studentListRepository.findById(userId).orElse(null);
        if (student == null) return "❌ Student not found";

        // Update the password
        student.setPassword(newPassword);  // Consider encoding this!
        studentListRepository.save(student);

        return "✅ Student password reset successfully!";
    }

    
    @GetMapping("/pending-by-staff/{staffId}")
    public ResponseEntity<?> getPendingLeavesByStaff(@PathVariable Long staffId) {
        List<LeaveForm> pendingLeaves = leaveFormRepository.findByStaffIdAndStatus(staffId, "PENDING");

        if (pendingLeaves.isEmpty()) {
            return ResponseEntity.ok("❌ No pending leaves found for staff ID: " + staffId);
        }

        return ResponseEntity.ok(pendingLeaves);
    }


    
    @GetMapping("/all-approved-rejected")
    public ResponseEntity<?> getAllApprovedAndRejectedLeaves() {
        List<String> statuses = List.of("APPROVED", "REJECTED");
        List<LeaveForm> result = leaveFormRepository.findByStatusIn(statuses);

        if (result.isEmpty()) {
            return ResponseEntity.ok("❌ No approved or rejected leave entries found.");
        }

        return ResponseEntity.ok(result);
    }


    



    // ✅ DTO Class
    static class LeaveSummary {
        private Long userId;
        private String name;
        private String email;
        private String department;
        private String leaveDate;
        private String reason;
        private String status;

        public LeaveSummary(Long userId, String name, String email, String department,
                            String leaveDate, String reason, String status) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.department = department;
            this.leaveDate = leaveDate;
            this.reason = reason;
            this.status = status;
        }

        public Long getUserId() { return userId; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getLeaveDate() { return leaveDate; }
        public String getReason() { return reason; }
        public String getStatus() { return status; }
    }
}

