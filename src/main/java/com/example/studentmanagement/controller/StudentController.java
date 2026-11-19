package com.example.studentmanagement.controller;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService service;

    // 1. Add a student
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student saved = service.addStudent(student);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Retrieve all students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    // 3. Search for a student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.getStudentById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // Q2.4 Search by email or course
    @GetMapping("/search")
    public ResponseEntity<?> searchStudents(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String course) {
        try {
            if (email != null) {
                return ResponseEntity.ok(service.getStudentByEmail(email));
            } else if (course != null) {
                return ResponseEntity.ok(service.getStudentsByCourse(course));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Provide 'email' or 'course' parameter."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Update student scores and skills
    @PutMapping("/{id}")
    public ResponseEntity<?> updateScoresAndSkills(@PathVariable Integer id, @RequestBody Student updatedData) {
        try {
            Student updated = service.updateScoresAndSkills(
                    id,
                    updatedData.getProgrammingScore(),
                    updatedData.getMathematicsScore(),
                    updatedData.getTechnicalSkills()
            );
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Delete a student record
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
        try {
            service.deleteStudent(id);
            return ResponseEntity.ok(Map.of("message", "Student with ID " + id + " deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // 6. Display the merit list (sorted by average score descending)
    @GetMapping("/merit-list")
    public ResponseEntity<List<Student>> getMeritList() {
        return ResponseEntity.ok(service.getMeritList());
    }

    // Q2.6 Identify students with the same technical skills
    @GetMapping("/common-skills")
    public ResponseEntity<?> getCommonSkills() {
        return ResponseEntity.ok(service.getStudentsGroupedBySkill());
    }

    // Q2.7 Weak in programming (<50) but Mathematics >= 75
    @GetMapping("/math-strong-prog-weak")
    public ResponseEntity<List<Student>> getMathStrongProgWeak() {
        return ResponseEntity.ok(service.getWeakProgrammingStrongMathStudents());
    }
}