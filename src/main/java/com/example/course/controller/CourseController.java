package com.example.course.controller;

import com.example.course.dto.CourseDTO;
import com.example.course.entity.Course;
import com.example.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("")
    public ResponseEntity<List<Course>> getAllCourse() {
        return ResponseEntity.status(200).body(courseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseByID(@PathVariable("id") int id) {
        Optional<Course> course = courseService.getCourseByID(id);
        return course.map(value -> ResponseEntity.status(200).body(value))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    public ResponseEntity<Course> addNewCourse(
            @RequestPart CourseDTO course,
            @RequestParam("image")MultipartFile image
            ) throws IOException {
        return ResponseEntity.status(200).body(courseService.addCourse(course, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @RequestPart CourseDTO course,
            @RequestParam(value = "image", required = false)MultipartFile image,
            @PathVariable int id) throws IOException {
        return ResponseEntity.status(200).body(courseService.updateCourse(course, image, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable("id") int id) {
        courseService.removeCourseByID(id);
        return ResponseEntity.status(201).body("Delete course successfully");
    }
}
