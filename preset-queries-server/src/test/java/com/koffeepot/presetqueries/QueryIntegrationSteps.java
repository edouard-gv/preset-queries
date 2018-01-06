package com.koffeepot.presetqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koffeepot.presetqueries.entity.Query;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryIntegrationSteps {

    private static final String USER_KEY = "loggedUser";

    private ResponseEntity aResponse;

    private Map<String, Object> session;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp(){
    }

    @When("^I call \"([^\"]*)\"$")
    public void i_call_a_url(String url) throws Throwable {
        aResponse = restTemplate.getForEntity("/api/"+url, String.class);
    }

    @Then("^I am answered \"([^\"]*)\" with a (\\d+) http code$")
    public void i_am_answered_with_a_http_code(String message, int httpCode) throws Throwable {

        assertThat(aResponse.getStatusCode().value()).isEqualTo(httpCode);
        if (message != null && !message.equals("")) {
            assertThat(((ResponseEntity<String>)aResponse).getBody()).contains(message);
        }
    }

    @When("^I post \"([^\"]*)\" with body \"([^\"]*)\"$")
    public void i_post_with_body(String url, String body) throws Throwable {
        aResponse = restTemplate.postForEntity("/api/"+url, parseQuery(body), String.class);
    }

    public static Query parseQuery(String json)
    throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Query.class);
    }
}
