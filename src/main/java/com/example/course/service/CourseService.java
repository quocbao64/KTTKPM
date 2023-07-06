package com.example.course.service;

import com.example.course.dto.CourseDTO;
import com.example.course.entity.Course;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    public List<Course> getAll();
    public Optional<Course> getCourseByID(int id);
    public Course addCourse(CourseDTO course, MultipartFile image) throws IOException;
    public Course updateCourse(CourseDTO courseDTO, MultipartFile image, int id) throws IOException;
    public void removeCourseByID(int id);
}
