package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.entity.Parameter;
import net.koffeepot.presetqueries.view.QueryResponse;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryParsingTest {
    QueryServiceImpl queryService = new QueryServiceImpl();


    //un paramètre where simple obligatoire
    @Test
    public void parseWithMandatoryWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", ParameterType.WHERE);
        parameter.setUserValue("FOO");
        parameters.add(parameter);
        QueryResponse queryResponse = new QueryResponse();
        MapSqlParameterSource mergedParams = queryService.computeTemplateAndParams("SELECT FOO \n FROM BAR \n WHERE VALUE=:value", parameters, queryResponse);
        assertThat(queryResponse.getJdbcTemplate()).isEqualTo("SELECT FOO \n FROM BAR \n WHERE VALUE=:value");
        assertThat(mergedParams.getValue("value")).isEqualTo("FOO");
    }

    //un paramètre where simple facultatif, cas avec
    @Test
    public void parseWithGivenOptionalWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", ParameterType.WHERE_OPTIONAL, "AND FOO=:value");
        parameter.setUserValue("FOO");
        parameters.add(parameter);
        QueryResponse queryResponse = new QueryResponse();
        MapSqlParameterSource mergedParams = queryService.computeTemplateAndParams("SELECT FOO \n FROM BAR \n WHERE 1=1 :value", parameters, queryResponse);
        assertThat(queryResponse.getJdbcTemplate()).isEqualTo("SELECT FOO \n FROM BAR \n WHERE 1=1 AND FOO=:value");
        assertThat(mergedParams.getValue("value")).isEqualTo("FOO");
    }

    //ou sans
    @Test
    public void parseWithUnsetOptionalWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", ParameterType.WHERE_OPTIONAL, "AND VALUE=:value");
        //we don't set a user value;
        parameters.add(parameter);
        QueryResponse queryResponse = new QueryResponse();
        MapSqlParameterSource mergedParams = queryService.computeTemplateAndParams("SELECT FOO \n FROM BAR \n WHERE 1=1 :value", parameters, queryResponse);
        assertThat(queryResponse.getJdbcTemplate()).isEqualTo("SELECT FOO \n FROM BAR \n WHERE 1=1 ");
        assertThat(mergedParams.getValues()).isEmpty();
    }

    //ou virtuellement vide
    @Test
    public void parseWithEmptyOptionalWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", ParameterType.WHERE_OPTIONAL, "AND VALUE=:value");
        parameter.setUserValue("   ");
        parameters.add(parameter);
        QueryResponse queryResponse = new QueryResponse();
        MapSqlParameterSource mergedParams = queryService.computeTemplateAndParams("SELECT FOO \n FROM BAR \n WHERE 1=1 :value", parameters, queryResponse);
        assertThat(queryResponse.getJdbcTemplate()).isEqualTo("SELECT FOO \n FROM BAR \n WHERE 1=1 ");
        assertThat(mergedParams.getValues()).isEmpty();
    }

    //un paramètre from
    @Test
    public void parseWithSimpleFromParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("table", ParameterType.FROM);
        parameter.setUserValue("THETABLE");
        parameters.add(parameter);
        QueryResponse queryResponse = new QueryResponse();
        MapSqlParameterSource mergedParams = queryService.computeTemplateAndParams("SELECT FOO \n FROM :table.BAR", parameters, queryResponse);
        assertThat(queryResponse.getJdbcTemplate()).isEqualTo("SELECT FOO \n FROM THETABLE.BAR");
        assertThat(mergedParams.getValues()).isEmpty();

    }

    //un paramètre de type inconnu
    @Test
    public void parseWithNullParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("table", null);
        parameter.setUserValue("THETABLE");
        parameters.add(parameter);
        parameter = new Parameter("value", null);
        parameter.setUserValue("VALUE");
        parameters.add(parameter);
        QueryResponse queryResponse = new QueryResponse();
        MapSqlParameterSource mergedParams = queryService.computeTemplateAndParams("SELECT FOO FROM :table.BAR WHERE GO = :value", parameters, queryResponse);
        assertThat(queryResponse.getJdbcTemplate()).isEqualTo("SELECT FOO FROM :table.BAR WHERE GO = :value");
        assertThat(mergedParams.getValues()).isEmpty();
    }
}