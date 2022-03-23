package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.entity.VisitedLocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class PostgresLocationRetrievalUtilTest {

    @ParameterizedTest
    @MethodSource(value = "generateFrequency")
    void isVisitedLocationReadyForANewVisit(String frequency, LocalDate date, boolean expected) {
        var visitedLocation = new VisitedLocation()
                .visited(true)
                .accessedAt(date)
                .scrappingLocation(ScrappingLocation.builder().frequency(frequency).build());

        boolean result = PostgresLocationRetrievalUtil.isVisitedLocationReadyForANewVisit(visitedLocation);

        Assertions.assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> generateFrequency() {
        return Stream.of(
                Arguments.of("1-m", LocalDate.now().minusDays(1), false),
                Arguments.of("1-m", LocalDate.now().minusMonths(1), true),
                Arguments.of("1-d", LocalDate.now(), false),
                Arguments.of("1-d", LocalDate.now().minusDays(1), true),
                Arguments.of("1-w", LocalDate.now(), false),
                Arguments.of("1-w", LocalDate.now().minusWeeks(1), true)
        );
    }

}