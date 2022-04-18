package com.engwili.arfe.service;

import com.engwili.arfe.dto.request.ScrapLocationDto;
import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.exception.ArfeException;
import com.engwili.arfe.mapper.ScrapLocationMapper;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ScrappingClientService {

    private final ScrappingLocationRepository scrappingLocationRepository;
    private final ScrapLocationMapper scrapLocationMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long addLocation(ScrapLocationDto scrapLocationDto) {
        return scrappingLocationRepository
                .findByUrl(scrapLocationDto.getUrl())
                .map(ScrappingLocation::id)
                .orElseGet(() -> scrappingLocationRepository.save(scrapLocationMapper.dtoToModel(scrapLocationDto)).id());
    }

    public List<ScrappingLocation> getLocation(Long id) {
        return scrappingLocationRepository.findById(id)
                .stream()
                .collect(Collectors.toList());
    }

    public List<ScrappingLocation> getLocationByPage(Integer page, Integer size) {
        return scrappingLocationRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean updateLocation(Long id, ScrapLocationDto scrapLocationDto) {
        return scrappingLocationRepository
                .findById(id)
                .map(el -> scrapLocationMapper.dtoToModel(scrapLocationDto).id(id))
                .map(scrappingLocationRepository::save)
                .isPresent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    public void deleteLocations(List<Long> locationIds) throws ArfeException {
        try {
            scrappingLocationRepository.deleteAllById(locationIds);
        } catch (EmptyResultDataAccessException exception) {
            log.error("Provided Ids could not be found in DB.");
            throw new ArfeException(HttpStatus.CONFLICT,
                    String.format("Some of the provided ids %s could not be found in DB.", locationIds));
        }
    }
}