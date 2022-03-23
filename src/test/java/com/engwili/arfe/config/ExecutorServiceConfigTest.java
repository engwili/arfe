package com.engwili.arfe.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ExecutorServiceConfigTest {

    @Test
    void executorServiceNotNull() {
        ExecutorServiceConfig executorServiceConfig = new ExecutorServiceConfig();
        Assertions.assertThat(executorServiceConfig.controllerExecutorService()).isNotNull();
    }

}