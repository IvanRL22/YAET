package com.ivanrl.yaet.domain.category.persistence;

import com.ivanrl.yaet.domain.category.CategoryDO;
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

    public CategoryDO toDomainModel() {
        return new CategoryDO(this.getId(),
                              this.getName(),
                              this.getDescription());
    }

}