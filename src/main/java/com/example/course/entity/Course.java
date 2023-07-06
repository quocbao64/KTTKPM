package com.example.course.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private double price;
    private double discount;
    private String image;
    @ManyToMany
    @JoinTable(name = "course_tag",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Tag> tags;

    public Course(String title, String description, double price, double discount, String image, List<Tag> tags) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.image = image;
        this.tags = tags;
    }
}
