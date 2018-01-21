package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
import net.koffeepot.presetqueries.repository.QueryRepository;
import net.koffeepot.presetqueries.view.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


@RunWith(SpringRunner.class)
@SpringBootTest

public class QueryServiceTest {
    Long anId = new Long(1);
    String sId = "1";

    @Autowired
    private QueryService queryService;

    @MockBean
    private QueryRepository queryRepository;

    @MockBean
    private ConfigurationRepository configurationRepository;

    @Before
    public void initH2Configuration() {
        Configuration h2Configuration = new Configuration(
                "h2",
                "net.koffeepot.presetqueries.datasource.H2DataSourceFactory",
                "{\"db_close_delay\": -1}" //
        );
        given(configurationRepository.findConfigurationByName("h2")).willReturn(h2Configuration);
    }

    @Test
    public void postSimpleQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "desc", "h2", "SELECT * FROM QUERY");
        given(queryRepository.findOne(new Long(1))).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        QueryResponse response = queryService.execQuery(postedQuery);
        assertThat(response.getJdbcTemplate()).isEqualTo("SELECT * FROM QUERY");
    }

    //Requête qui ne commence pas par un SELECT ou un sELecT ou ...
    @Test
    public void postStartingWithMixedCaseQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "", "h2", "SeLECt * FROM QUERY");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        QueryResponse response = queryService.execQuery(postedQuery);
        assertThat(response.getJdbcTemplate()).isEqualTo("SeLECt * FROM QUERY");
    }

    @Test
    public void postNotStartingWithSelectQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "", "h2", "Update FOO FROM BAR");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query must start with SELECT: Update");
    }

    @Test
    public void postNotFoundQuery() throws Exception {
        given(queryRepository.findOne(anId)).willReturn(null);
        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query not found: 1");
    }

    @Test
    public void postUnknownConfigurationQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "desc", "badconf", "SELECT * FROM QUERY");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        given(configurationRepository.findConfigurationByName("badconf")).willReturn(null);

        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Configuration not found: badconf");
    }

    @Test
    public void postNotADatasourceConfigurationQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "desc", "badimpl", "SELECT * FROM QUERY");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        Configuration badImpl = new Configuration("badimpl", "java.lang.Object", "");
        given(configurationRepository.findConfigurationByName("badimpl")).willReturn(badImpl);

        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Factory class must implement constructor with Configuration: java.lang.Object");
    }


    @Test
    public void getQueryById() {
        Query storedQuery = new Query(anId, "simple", "desc", "badimpl", "SELECT * FROM QUERY");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        assertThat(queryService.getQuery(sId).getName()).isEqualTo("simple");
    }

    @Test
    public void getUnknownIdQuery() {
        given(queryRepository.findOne(anId)).willReturn(null);
        assertThatThrownBy(() -> queryService.getQuery(sId))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query not found: 1 (1)");
    }

    @Test
    public void getNotAnNumIdQuery() {
        assertThatThrownBy(() -> queryService.getQuery("z"))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Not a correct id for a query: z");
    }



}