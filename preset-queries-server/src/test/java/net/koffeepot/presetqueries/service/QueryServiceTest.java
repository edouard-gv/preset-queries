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

    private Configuration h2Configuration;

    @Before
    public void initH2Configuration() {
        h2Configuration = new Configuration(
                anId,
                "h2",
                "net.koffeepot.presetqueries.datasource.H2DataSourceFactory",
                "{\"db_close_delay\": -1}" //
        );
    }

    @Test
    public void postSimpleQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "desc", h2Configuration, "SELECT * FROM QUERY");
        given(queryRepository.findOne(new Long(1))).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        QueryResponse response = queryService.execQuery(postedQuery);
        assertThat(response.getJdbcTemplate()).isEqualTo("SELECT * FROM QUERY");
    }

    //Requête qui ne commence pas par un SELECT ou un sELecT ou ...
    @Test
    public void postStartingWithMixedCaseQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "", h2Configuration, "SeLECt * FROM QUERY");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        QueryResponse response = queryService.execQuery(postedQuery);
        assertThat(response.getJdbcTemplate()).isEqualTo("SeLECt * FROM QUERY");
    }

    @Test
    public void postNotStartingWithSelectQuery() throws Exception {
        Query storedQuery = new Query(anId, "simple", "", h2Configuration, "Update FOO FROM BAR");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query must start with SELECT: Update");
    }

    @Test
    public void postQueryWithNoTemplate() throws Exception {
        Query storedQuery = new Query(anId, "simple", "", h2Configuration, null);
        given(queryRepository.findOne(anId)).willReturn(storedQuery);
        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query has no template: 1");
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
    public void postNotADatasourceConfigurationQuery() throws Exception {
        Configuration badImpl = new Configuration(new Long(2),"badimpl", "java.lang.Object", "");
        Query storedQuery = new Query(anId, "simple", "desc", badImpl, "SELECT * FROM QUERY");
        given(queryRepository.findOne(anId)).willReturn(storedQuery);

        Query postedQuery = new Query(anId);
        assertThatThrownBy(() -> queryService.execQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Factory class must implement constructor with Configuration: java.lang.Object");
    }

    @Test
    public void getQueryById() {
        Query storedQuery = new Query(anId, "simple", "desc", h2Configuration, "SELECT * FROM QUERY");
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