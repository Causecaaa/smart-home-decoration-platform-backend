package org.homedecoration.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Book {
    @Id
    private Integer id;
    private String name;
    private String author;
}
