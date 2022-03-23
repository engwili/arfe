package com.engwili.arfe.steps;

import com.engwili.arfe.AbstractSpringTest;
import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.ArticleDto;
import com.engwili.arfe.entity.WorkStatus;
import com.engwili.arfe.repository.WorkStatusRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.engwili.arfe.misc.ArfeTestContext.CONTEXT;
import static com.engwili.arfe.misc.factory.ConstantFactory.*;
import static com.engwili.arfe.misc.factory.ObjectFactory.getLocation;
import static com.engwili.arfe.misc.factory.ObjectFactory.getStandardLocation;
import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
public class MyStepdefs extends AbstractSpringTest {

    @Autowired
    private WorkStatusRepository workStatusRepository;

    @Given("1 unvisited location")
    public void unvisitedLocation() throws JsonProcessingException {

        var locations = getLocation();

        var post = RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(new Gson().toJson(locations))
                .when()
                .post(post_location)
                .then()
                .extract()
                .response();

        assertThat(post.getStatusCode()).isEqualTo(201);
    }

    @When("web client triggers scrap process")
    public void webClientTriggersScrapProcess() {

        var get = RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .post(scrap_trigger)
                .then()
                .extract()
                .response();

        assertThat(get.getStatusCode()).isEqualTo(200);
        var workId = get.body().jsonPath().getString("workId");

        assertThat(workId).isNotNull();
        assertThat(workId).isNotEmpty();

        if (CONTEXT.get("workId") == null) {
            CONTEXT.set("workId", new TreeSet<>(Arrays.asList(workId)));
        } else {
            TreeSet<String> temp = new Gson().fromJson(new Gson().toJson(CONTEXT.get("workId")), new TypeToken<TreeSet<String>>() {
            }.getType());
            temp.add(workId);
            CONTEXT.set("workId", temp);
        }
    }

    @Then("{int} proof of work is returned with status {string}")
    public void proofOfWorkIsReturnedWithStatus(Integer integer, String expectedWorkStatus) {

        integer -= 1;
        var workTemp = CONTEXT.get("workId").stream().toList();

        var get = RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(new WorkProofDto(workTemp.get(integer)))
                .when()
                .get(scrap_status)
                .then()
                .extract()
                .response();

        assertThat(get.getStatusCode()).isEqualTo(200);
        var workId = get.body().jsonPath().getString("workId");
        var status = get.body().jsonPath().getString("status");

        assertThat(status).isEqualToIgnoringCase(expectedWorkStatus);
        assertThat(CONTEXT.get("workId")).contains(workId);
    }

    @When("the scrap process finishes")
    public void theScrapProcessFinishes() throws InterruptedException {
        Thread.sleep(20000L);
    }

    @And("articles could be retrieved from DB")
    public void articlesCouldBeRetrievedFromDB() {
        var get = RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get(article_random)
                .then()
                .extract()
                .response();

        var article = new Gson().fromJson(get.getBody().prettyPrint(), ArticleDto.class);
        assertThat(article).isNotNull();
    }

    @When("add 1 unvisited location")
    public void addUnvisitedLocation() {

        var locations = getStandardLocation();

        var post = RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(new Gson().toJson(locations))
                .when()
                .post(post_location)
                .then()
                .extract()
                .response();

        assertThat(post.getStatusCode()).isEqualTo(201);
    }

    @And("{int} proof of work are stored in DB")
    public void proofOfWorksAreStoredInDB(int count) {

        var proofOfWorks = workStatusRepository.findAll().stream().map(WorkStatus::getWorkId).collect(Collectors.toSet());
        assertThat(proofOfWorks).hasSize(count);
    }
}
