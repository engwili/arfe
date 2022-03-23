package com.engwili.arfe.repository;

import com.engwili.arfe.entity.VisitedLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VisitedLocationRepository extends JpaRepository<VisitedLocation, Long> {
    List<VisitedLocation> findByVisitedAndAccessedAt(boolean visited, LocalDate today);
}
