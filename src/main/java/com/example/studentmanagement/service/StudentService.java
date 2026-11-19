package com.example.studentmanagement.service;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    // Helper method to apply rules 1, 2, 3, and 8
    private void applyBusinessRules(Student student) {
        // 8. Validate scores between 0 and 100
        if (student.getProgrammingScore() == null || student.getProgrammingScore() < 0 || student.getProgrammingScore() > 100 ||
            student.getMathematicsScore() == null || student.getMathematicsScore() < 0 || student.getMathematicsScore() > 100) {
            throw new IllegalArgumentException("Scores must be between 0 and 100.");
        }

        // 1. Calculate average score
        double avg = (student.getProgrammingScore() + student.getMathematicsScore()) / 2.0;
        student.setAverageScore(avg);

        // 2. Assign performance category
        if (avg >= 85) {
            student.setPerformanceCategory("Excellent");
        } else if (avg >= 70) {
            student.setPerformanceCategory("Good");
        } else if (avg >= 50) {
            student.setPerformanceCategory("Satisfactory");
        } else {
            student.setPerformanceCategory("Needs Improvement");
        }

        // 3. Identify stronger subject
        if (student.getProgrammingScore() > student.getMathematicsScore()) {
            student.setStrongerSubject("Programming");
        } else if (student.getMathematicsScore() > student.getProgrammingScore()) {
            student.setStrongerSubject("Mathematics");
        } else {
            student.setStrongerSubject("Equal in Both");
        }
    }

    // Add student (with duplicate ID prevention)
    public Student addStudent(Student student) {
        if (student.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        // 8. Prevent duplicate student IDs
        if (repository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException("Duplicate Student ID: " + student.getStudentId() + " already exists.");
        }
        applyBusinessRules(student);
        return repository.save(student);
    }

    // Retrieve all students
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    // Search by ID
    public Student getStudentById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + id));
    }

    // 4. Search by email
    public Student getStudentByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Student not found with email: " + email));
    }

    // 4. Search by course
    public List<Student> getStudentsByCourse(String course) {
        return repository.findByCourseNameIgnoreCase(course);
    }

    // Update student scores and skills
    public Student updateScoresAndSkills(Integer id, Double programmingScore, Double mathematicsScore, List<String> skills) {
        Student existing = getStudentById(id);
        if (programmingScore != null) existing.setProgrammingScore(programmingScore);
        if (mathematicsScore != null) existing.setMathematicsScore(mathematicsScore);
        if (skills != null) existing.setTechnicalSkills(skills);

        applyBusinessRules(existing);
        return repository.save(existing);
    }

    // Delete student record
    public void deleteStudent(Integer id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Student not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    // 5. Merit list sorted by average score descending
    public List<Student> getMeritList() {
        return repository.findAllByOrderByAverageScoreDesc();
    }

    // 6. Group students who share the same technical skills
    public Map<String, List<String>> getStudentsGroupedBySkill() {
        List<Student> allStudents = repository.findAll();
        Map<String, List<String>> skillMap = new HashMap<>();

        for (Student s : allStudents) {
            if (s.getTechnicalSkills() != null) {
                for (String skill : s.getTechnicalSkills()) {
                    String normalizedSkill = skill.trim();
                    skillMap.computeIfAbsent(normalizedSkill, k -> new ArrayList<>())
                            .add(s.getStudentId() + " - " + s.getStudentName());
                }
            }
        }
        // Keep only skills shared by more than 1 student
        return skillMap.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 7. Needs improvement in programming (< 50) but Mathematics >= 75
    public List<Student> getWeakProgrammingStrongMathStudents() {
        return repository.findByProgrammingScoreLessThanAndMathematicsScoreGreaterThanEqual(50.0, 75.0);
    }
}