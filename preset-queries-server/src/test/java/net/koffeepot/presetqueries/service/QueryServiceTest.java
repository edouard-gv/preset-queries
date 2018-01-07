package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.repository.QueryRepository;
import net.koffeepot.presetqueries.view.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


@RunWith(SpringRunner.class)
@SpringBootTest

public class QueryServiceTest {
    @Autowired
    private QueryService queryService;

    @MockBean
    private QueryRepository queryRepository;

    @Test
    public void postSimpleQuery() throws Exception {
        Query storedQuery = new Query("simple", "desc", "mysql-local", "SELECT FOO FROM BAR");
        given(queryRepository.findQueryByName("simple")).willReturn(storedQuery);
        Query postedQuery = new Query("simple");
        QueryResponse response = queryService.postQuery(postedQuery);
        assertThat(response.getJdbcTemplate()).isEqualTo("SELECT FOO FROM BAR");
    }

    //Requête qui ne commence pas par un SELECT ou un sELecT ou ...
    @Test
    public void postStartingWithMixedCaseQuery() throws Exception {
        Query storedQuery = new Query("simple", "", "mysql-local", "SeLECt FOO FROM BAR");
        given(queryRepository.findQueryByName("simple")).willReturn(storedQuery);
        Query postedQuery = new Query("simple");
        QueryResponse response = queryService.postQuery(postedQuery);
        assertThat(response.getJdbcTemplate()).isEqualTo("SeLECt FOO FROM BAR");
    }

    @Test
    public void postNotStartingWithSelectQuery() throws Exception {
        Query storedQuery = new Query("simple", "", "mysql-local", "Update FOO FROM BAR");
        given(queryRepository.findQueryByName("simple")).willReturn(storedQuery);
        Query postedQuery = new Query("simple");
        assertThatThrownBy(() -> queryService.postQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query must start with SELECT: Update");
    }

    @Test
    public void postNotFoundQuery() throws Exception {
        given(queryRepository.findByName("simple")).willReturn(new ArrayList<>());
        Query postedQuery = new Query("simple");
        assertThatThrownBy(() -> queryService.postQuery(postedQuery))
                .isInstanceOf(TechnicalRuntimeException.class)
                .hasMessageContaining("Query not found: simple");
    }


}