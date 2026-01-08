package org.example.entity;

import java.util.Date;

public class Student {
    private Integer studentId;
    private String name;
    private String gender;
    private Date birthDate;
    private String className;

    // 无参构造
    public Student() {}

    // 全参构造
    public Student(Integer studentId, String name, String gender, Date birthDate, String className) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.className = className;
    }

    // Getter 和 Setter 方法
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}