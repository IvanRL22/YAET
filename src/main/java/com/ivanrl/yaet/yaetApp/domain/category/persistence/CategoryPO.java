package com.ivanrl.yaet.yaetApp.domain.category.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "categories")
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
public class CategoryPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description")
    private String description;

}