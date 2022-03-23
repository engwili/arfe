package com.engwili.arfe.entity;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Accessors(fluent = true)
@EqualsAndHashCode
@Log4j2
public class ScrappingLocation {

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String nickname;
    private String frequency;

    private Integer maxArticleScraped;

    public Pair<Integer, ChronoUnit> convertFrequencyToChronUnit() {
        var split = frequency.split("-");

        var number = 1;
        try {
            number = Integer.parseInt(split[0]);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        var resolution = switch (split[1].toLowerCase(Locale.ROOT)) {
            case "d" -> ChronoUnit.DAYS;
            case "w" -> ChronoUnit.WEEKS;
            case "m" -> ChronoUnit.MONTHS;
            default -> ChronoUnit.YEARS;
        };
        return Pair.of(number, resolution);
    }
}