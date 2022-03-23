package com.engwili.arfe.service;

import com.engwili.arfe.dto.request.ScrapLocationDto;
import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.exception.ArfeException;
import com.engwili.arfe.mapper.ScrapLocationMapper;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class ScrappingClientServiceTest {

    @Autowired
    private ScrappingLocationRepository scrappingLocationRepository;

    @Autowired
    private ScrapLocationMapper scrapLocationMapper;

    @Test
    void given_scrapLocationDto_when_addLocation_then_locationIsSavedInDB() {

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        Long id = scrappingClientService.addLocation(new ScrapLocationDto("testUrl", "1-d", "testNickname", 1));

        Assertions.assertThat(id).isNotNull();

    }

    @Test
    void given_locationIdNotInDB_when_getLocation_then_emptyListReturned() {

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        var result = scrappingClientService.getLocation(1L);

        Assertions.assertThat(result).isEmpty();

    }

    @Test
    void given_locationInInDB_when_getLocation_then_locationReturned() {
        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .nickname("arxiv-ai")
                .frequency("day")
                .url("http://localhost.com")
                .build();

        scrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        var result = scrappingClientService.getLocation(scrappingLocation.id());

        Assertions.assertThat(result).hasSize(1);

    }

    @Test
    void given_locationInInDB_when_getLocationByPage_then_locationReturned() {
        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .nickname("arxiv-ai")
                .frequency("1-d")
                .url("http://localhost.com")
                .build();

        scrappingLocationRepository.save(scrappingLocation);

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        var result = scrappingClientService.getLocationByPage(0, 5);

        Assertions.assertThat(result).hasSize(1);

    }

    @Test
    void given_locationInInDB_when_updateLocation_then_locationReturned() {
        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .nickname("arxiv-ai")
                .frequency("1-d")
                .url("http://localhost.com")
                .build();

        scrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        var updateDto = new ScrapLocationDto("testUrl", "testFreq", "testNickname", 1);
        var result = scrappingClientService.updateLocation(scrappingLocation.id(), updateDto);

        Assertions.assertThat(result).isTrue();

        Optional<ScrappingLocation> updatedLocationFromDb = scrappingLocationRepository.findById(scrappingLocation.id());

        Assertions.assertThat(updatedLocationFromDb).isNotEmpty();
        Assertions.assertThat(updatedLocationFromDb.get().maxArticleScraped()).isOne();
        Assertions.assertThat(updatedLocationFromDb.get().url()).isEqualTo("testUrl");
        Assertions.assertThat(updatedLocationFromDb.get().frequency()).isEqualTo("testFreq");
        Assertions.assertThat(updatedLocationFromDb.get().nickname()).isEqualTo("testNickname");

    }

    @Test
    void given_locationNotInInDB_when_updateLocation_then_returnFalseAndNoUpdate() {

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        var updateDto = new ScrapLocationDto("testUrl", "testFreq", "testNickname", 1);

        var result = scrappingClientService.updateLocation(100L, updateDto);

        Assertions.assertThat(result).isFalse();
    }

    @Test
    void given_locationNotInInDB_when_deleteLocations_then_exceptionThrown() {

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        ArfeException exception = assertThrows(ArfeException.class, () -> {
            scrappingClientService.deleteLocations(List.of(1L, 2L));
        });

        Assertions.assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void given_locationInInDB_when_deleteLocations_then_deleteLocations() {

        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .nickname("arxiv-ai")
                .frequency("1-d")
                .url("http://localhost.com")
                .build();

        scrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var scrappingClientService = new ScrappingClientService(scrappingLocationRepository, scrapLocationMapper);

        scrappingClientService.deleteLocations(List.of(scrappingLocation.id()));

        Assertions.assertThat(scrappingLocationRepository.findAll()).isEmpty();
    }

}