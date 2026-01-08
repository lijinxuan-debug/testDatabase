package org.example.entity;

public class Course {
    private String courseId;
    private String courseName;
    private Integer credits;
    private String teacher;

    public Course() {}

    public Course(String courseId, String courseName, Integer credits, String teacher) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.teacher = teacher;
    }

    // Getter 和 Setter
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }
}