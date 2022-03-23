package com.engwili.arfe.steps;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:reports/cucumber-report.html"},
        features = {"src/test/resources"},
        glue = {"com.engwili.arfe.steps"})
public class CucumberRunnerTest {

}