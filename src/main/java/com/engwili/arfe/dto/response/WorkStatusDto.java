package com.engwili.arfe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WorkStatusDto {

    private String status;
    private String triggeredAt;
    private String workId;
}
