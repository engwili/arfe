package com.engwili.arfe.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Accessors(fluent = true)
public class VisitedLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean visited;

    @Column(length = 512)
    private LocalDate accessedAt;

    @OneToOne
    private ScrappingLocation scrappingLocation;
}
