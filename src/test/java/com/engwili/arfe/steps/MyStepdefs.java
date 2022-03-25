package com.engwili.arfe.steps;

import com.engwili.arfe.AbstractSpringTest;
import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.ArticleDto;
import com.engwili.arfe.repository.WorkStatusRepository;
import com.google.gson.Gson;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static com.engwili.arfe.misc.factory.ConstantFactory.*;
import static com.engwili.arfe.misc.factory.ObjectFactory.getLocation;
import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
public class MyStepdefs extends AbstractSpringTest {

    @Autowired
    private WorkStatusRepository workStatusRepository;

    private static String workIdProof = "";

    @Given("1 unvisited location")
    public void unvisitedLocation() throws JsonProcessingException {

        var locations = getLocation();

        var post = RestAssured
                .given()
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

        var get = RestAssured
                .given()
                .header("Content-type", "application/json")
                .when()
                .post(scrap_trigger)
                .then()
                .extract()
                .response();

        assertThat(get.getStatusCode()).isEqualTo(200);
        var workId = get.body().jsonPath().getString("workId");
        workIdProof = workId;

        assertThat(workId).isNotNull();
        assertThat(workId).isNotEmpty();
    }

    @Then("{int} proof of work is returned with status {string}")
    public void proofOfWorkIsReturnedWithStatus(Integer integer, String expectedWorkStatus) {

        integer -= 1;

        var get = RestAssured
                .given()
                .header("Content-type", "application/json")
                .and()
                .body(new WorkProofDto(workIdProof))
                .when()
                .get(scrap_status)
                .then()
                .extract()
                .response();

        assertThat(get.getStatusCode()).isEqualTo(200);
        var workId = get.body().jsonPath().getString("workId");
        var status = get.body().jsonPath().getString("status");

        assertThat(status).isEqualToIgnoringCase(expectedWorkStatus);
        assertThat(workId).isEqualToIgnoringCase(workIdProof);
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
}
