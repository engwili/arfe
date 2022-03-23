package com.engwili.arfe.client.web;

import com.engwili.arfe.dto.request.ScrapLocationDto;
import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.exception.ArfeException;
import com.engwili.arfe.service.ScrappingClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class ScrappingLocationController {

    private final ScrappingClientService scrappingClientService;

    @PostMapping(path = "/scrap-location", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createNewScrappingLocation(@RequestBody @Valid ScrapLocationDto scrapLocationDto) {
        var id = scrappingClientService.addLocation(scrapLocationDto);
        return ResponseEntity.created(URI.create(String.format("/scrap-location/%s", id))).build();
    }

    @GetMapping(path = "/scrap-location/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScrappingLocation>> getScrappingLocation(@PathVariable("id") @Min(1) Long id) {
        return ResponseEntity.ok().body(scrappingClientService.getLocation(id));
    }

    @GetMapping(path = "/scrap-location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ScrappingLocation>> getAllScrappingLocation(@RequestParam("page") @Min(0) Integer page, @RequestParam("size") @Min(1) Integer size) {
        return ResponseEntity.ok().body(scrappingClientService.getLocationByPage(page, size));
    }

    @PutMapping(path = "/scrap-location/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity editScrappingLocation(@PathVariable("id") @Min(1) Long id, @RequestBody @Valid ScrapLocationDto scrapLocationDto) {

        return scrappingClientService.updateLocation(id, scrapLocationDto) ? ResponseEntity.created(URI.create(String.format("/scrap-location/%s", id))).build() :
                ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/scrap-location", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteScrappingLocation(@RequestBody List<Long> locationIds) throws ArfeException {
        scrappingClientService.deleteLocations(locationIds);
        return ResponseEntity.accepted().build();
    }
}
