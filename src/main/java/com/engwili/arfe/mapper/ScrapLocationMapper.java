package com.engwili.arfe.mapper;

import com.engwili.arfe.dto.request.ScrapLocationDto;
import com.engwili.arfe.entity.ScrappingLocation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ScrapLocationMapper {

    ScrappingLocation dtoToModel(ScrapLocationDto scrapLocationDto);
}
