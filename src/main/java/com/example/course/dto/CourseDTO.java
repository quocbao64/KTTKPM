package com.example.course.dto;

import java.util.List;

public record CourseDTO(String title, String description, double price, double discount, List<Integer> tags) {
    
}
