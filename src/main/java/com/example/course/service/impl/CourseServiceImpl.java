package com.example.course.service.impl;

import com.example.course.dto.CourseDTO;
import com.example.course.entity.Course;
import com.example.course.entity.Tag;
import com.example.course.repository.CourseRepository;
import com.example.course.repository.TagRepository;
import com.example.course.service.CourseService;
import com.example.course.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> getCourseByID(int id) {
        return courseRepository.findById(id);

    }

    @Override
    public Course addCourse(CourseDTO courseDTO, MultipartFile file) throws IOException {
        List<Tag> tags = new ArrayList<>();
        courseDTO.tags().forEach(tagID -> tags.add(tagRepository.findById(tagID).orElse(null)));

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Course newCourse = new Course(
                courseDTO.title(),
                courseDTO.description(),
                courseDTO.price(),
                courseDTO.discount(),
                fileName,
                tags
        );

        newCourse.setImage(fileName);
        Course course = courseRepository.save(newCourse);
        String uploadDir = "file/course-images/" + course.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, file);
        return course;
    }

    @Override
    public Course updateCourse(CourseDTO courseDTO, MultipartFile image, int id) throws IOException {
        List<Tag> tags = new ArrayList<>();
        Course course = courseRepository.findById(id).get();
        courseDTO.tags().forEach(tagID -> tags.add(tagRepository.findById(tagID).orElse(null)));

        if (image != null) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            course.setImage(fileName);
            String uploadDir = "file/course-images/" + course.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }
        course.setTitle(courseDTO.title());
        course.setDescription(courseDTO.description());
        course.setPrice(courseDTO.price());
        course.setDiscount(courseDTO.discount());
        course.setTags(tags);

        return courseRepository.saveAndFlush(course);
    }

    @Override
    public void removeCourseByID(int id) {
        courseRepository.deleteById(id);
    }
}
