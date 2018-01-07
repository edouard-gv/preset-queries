package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.entity.Parameter;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryParsingTest {
    QueryServiceImpl queryService = new QueryServiceImpl();


    //un paramètre where simple obligatoire
    @Test
    public void parseWithMandatoryWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", "where");
        parameter.setUserValue("FOO");
        parameters.add(parameter);
        String sql = queryService.mergeTemplate("SELECT FOO \n FROM BAR \n WHERE VALUE=:value", parameters);
        assertThat(sql).isEqualTo("SELECT FOO \n FROM BAR \n WHERE VALUE=:value");
    }

    //un paramètre where simple facultatif, cas avec
    @Test
    public void parseWithGivenOptionalWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", "where-optional", "AND FOO=:value");
        parameter.setUserValue("FOO");
        parameters.add(parameter);
        String sql = queryService.mergeTemplate("SELECT FOO \n FROM BAR \n WHERE 1=1 :value", parameters);
        assertThat(sql).isEqualTo("SELECT FOO \n FROM BAR \n WHERE 1=1 AND FOO=:value");
    }

    //ou sans
    @Test
    public void parseWithUnsetOptionalWhereParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("value", "where-optional", "AND VALUE=:value");
        //we don't set a user value;
        parameters.add(parameter);
        String sql = queryService.mergeTemplate("SELECT FOO \n FROM BAR \n WHERE 1=1 :value", parameters);
        assertThat(sql).isEqualTo("SELECT FOO \n FROM BAR \n WHERE 1=1 ");
    }

    //un paramètre from
    @Test
    public void parseWithSimpleFromParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("table", "from");
        parameter.setUserValue("THETABLE");
        parameters.add(parameter);
        String sql = queryService.mergeTemplate("SELECT FOO \n FROM :table.BAR", parameters);
        assertThat(sql).isEqualTo("SELECT FOO \n FROM THETABLE.BAR");
    }

    //un paramètre de type inconnu
    @Test
    public void parseWithUnknownTypeParameterQuery() throws Exception {
        Set<Parameter> parameters = new HashSet<>();
        Parameter parameter = new Parameter("table", "krinch");
        parameter.setUserValue("THETABLE");
        parameters.add(parameter);
        parameter = new Parameter("value", "krinch");
        parameter.setUserValue("VALUE");
        parameters.add(parameter);
        String sql = queryService.mergeTemplate("SELECT FOO FROM :table.BAR WHERE GO = :value", parameters);
        assertThat(sql).isEqualTo("SELECT FOO FROM :table.BAR WHERE GO = :value");
    }
}