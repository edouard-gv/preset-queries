package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.entity.Parameter;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ParameterMergingTest {
    QueryServiceImpl queryService = new QueryServiceImpl();


    @Test
    public void mergeNothing() throws Exception {
        Set<Parameter> postedParameters = new HashSet<>();
        Set<Parameter> storedParameters = new HashSet<>();
        Parameter parameter = new Parameter("name");
        parameter.setUserValue("value");
        postedParameters.add(parameter);
        storedParameters.add(new Parameter("other"));
        queryService.mergePostedParametersInStoredQuery(postedParameters, storedParameters);
        Map<String, String> mergedMap = new HashMap<>();
        storedParameters.forEach(param -> mergedMap.put(param.getName(), param.getUserValue()));
        assertThat(mergedMap.get("name")).isNullOrEmpty();
    }

    @Test
    public void mergeSimple() throws Exception {
        Set<Parameter> postedParameters = new HashSet<>();
        Set<Parameter> storedParameters = new HashSet<>();
        Parameter parameter = new Parameter("name");
        parameter.setUserValue("value");
        postedParameters.add(parameter);
        storedParameters.add(new Parameter("name"));
        queryService.mergePostedParametersInStoredQuery(postedParameters, storedParameters);
        Map<String, String> mergedMap = new HashMap<>();
        storedParameters.forEach(param -> mergedMap.put(param.getName(), param.getUserValue()));
        assertThat(mergedMap.get("name")).isEqualTo("value");
    }

    @Test
    public void mergeMixed() throws Exception {
        Set<Parameter> postedParameters = new HashSet<>();
        Set<Parameter> storedParameters = new HashSet<>();
        Parameter parameter = new Parameter("name1");
        parameter.setUserValue("value1");
        postedParameters.add(parameter);
        parameter = new Parameter("name2");
        parameter.setUserValue("value2");
        postedParameters.add(parameter);
        parameter = new Parameter("name3");
        parameter.setUserValue("value3");
        postedParameters.add(parameter);

        storedParameters.add(new Parameter("name5"));
        storedParameters.add(new Parameter("name2"));
        storedParameters.add(new Parameter("name1"));

        queryService.mergePostedParametersInStoredQuery(postedParameters, storedParameters);

        Map<String, String> mergedMap = new HashMap<>();
        storedParameters.forEach(param -> mergedMap.put(param.getName(), param.getUserValue()));

        assertThat(mergedMap.get("name5")).isNullOrEmpty();
        assertThat(mergedMap.get("name2")).isEqualTo("value2");
        assertThat(mergedMap.get("name1")).isEqualTo("value1");}
}