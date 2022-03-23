package com.engwili.arfe.mapper;

import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.WorkStatusDto;
import com.engwili.arfe.entity.WorkStatus;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkMapper {

    WorkProofDto toWorkProof(WorkStatus workStatus);

    WorkStatusDto toWorkStatus(WorkStatus workStatus);
}
