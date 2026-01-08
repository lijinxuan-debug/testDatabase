package org.example.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Grade {
    private Integer gradeId;   // 自增主键
    private Integer studentId; // 外键
    private String courseId;   // 外键
    private BigDecimal score;  // 对应 decimal(5,2)
    private Date examDate;

    public Grade() {}

    public Grade(Integer gradeId, Integer studentId, String courseId, BigDecimal score, Date examDate) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.score = score;
        this.examDate = examDate;
    }

    // Getter 和 Setter
    public Integer getGradeId() { return gradeId; }
    public void setGradeId(Integer gradeId) { this.gradeId = gradeId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public Date getExamDate() { return examDate; }
    public void setExamDate(Date examDate) { this.examDate = examDate; }
}