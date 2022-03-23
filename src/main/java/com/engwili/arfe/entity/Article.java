package com.engwili.arfe.entity;

//import com.engwili.arfe.convertors.LocalDateAttributeConverter;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String title;

    @Lob
    private String authors;

    @Lob
    private String summary;

    @Lob
    private String categories;

    private String createdAt;

    private String downloadableContentUrl;

    @Column(length = 512)
    private LocalDate accessedAt;
}
