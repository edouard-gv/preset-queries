package net.koffeepot.presetqueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import net.koffeepot.presetqueries.entity.Configuration;
import net.koffeepot.presetqueries.entity.Query;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
import net.koffeepot.presetqueries.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryIntegrationSteps {

    private ResponseEntity aResponse;

    private Map<String, Object> session;

    @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp(){
    }

    @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private QueryRepository queryRepository;


    @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    private ConfigurationRepository configurationRepository;

    @When("^I call \"([^\"]*)\"$")
    public void i_call_a_url(String url) throws Throwable {
        setHeader("KoffePot-Token", "ro-pjha00ippK");
        aResponse = restTemplate.getForEntity("/api/"+url, String.class);
    }

    @Then("^I am answered \"([^\"]*)\" with a (\\d+) http code$")
    public void i_am_answered_with_a_http_code(String message, int httpCode) throws Throwable {

        assertThat(aResponse.getStatusCode().value()).isEqualTo(httpCode);
        if (message != null && !message.equals("")) {
            assertThat(((ResponseEntity<String>)aResponse).getBody()).contains(message);
        }
    }

    @And("^I am answered \"([^\"]*)\"$")
    public void i_am_answered(String message) throws Throwable {
        if (message != null && !message.equals("")) {
            assertThat(((ResponseEntity<String>)aResponse).getBody()).contains(message);
        }
    }

    @When("^I post \"([^\"]*)\" with body \"([^\"]*)\" and token \"([^\"]*)\" in header$")
    public void i_post_with_body(String url, String body, String header) throws Throwable {
        if ("null".equals(header)) {
            header = null;
        }

        setHeader("KoffePot-Token", header);

        aResponse = restTemplate.postForEntity("/api/"+url, parseQuery(body), String.class);
    }

    @Given("^I initiate a mock database with a query named \"([^\"]*)\"$")
    public void iInitiateAMockDatabaseWithAQueryNamed(String name) throws Throwable {
        Configuration h2Configuration = new Configuration(
                new Long(1),
                "h2",
                "net.koffeepot.presetqueries.datasource.H2DataSourceFactory",
                "{\"db_close_delay\": -1}" //
        );
        configurationRepository.save(h2Configuration);
        queryRepository.save(new Query(new Long(1), name, "description", h2Configuration, "template"));
    }

    public static Query parseQuery(String json)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json.replaceAll("'","\""), Query.class);
    }

    private void setHeader(String headerName, String headerValue) {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, aBody, execution) -> {
                    request.getHeaders()
                            .add(headerName, headerValue);
                    return execution.execute(request, aBody);
                }));
    }
}
