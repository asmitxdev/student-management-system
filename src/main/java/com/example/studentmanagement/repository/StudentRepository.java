package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // 4. Search by email or course
    Optional<Student> findByEmail(String email);
    List<Student> findByCourseNameIgnoreCase(String courseName);

    // 5. Generate merit list sorted by average score descending
    List<Student> findAllByOrderByAverageScoreDesc();

    // 6. Identify students who have a specific technical skill
    List<Student> findByTechnicalSkillsContainingIgnoreCase(String skill);

    // 7. Needs improvement in programming (< 50) AND mathematics score >= 75
    List<Student> findByProgrammingScoreLessThanAndMathematicsScoreGreaterThanEqual(Double progLimit, Double mathMin);
}