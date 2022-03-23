package com.engwili.arfe.repository;

import com.engwili.arfe.entity.ScrappingLocation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ScrappingLocationRepository extends PagingAndSortingRepository<ScrappingLocation, Long> {

    Optional<ScrappingLocation> findByUrl(String url);

    List<ScrappingLocation> findAll();
}