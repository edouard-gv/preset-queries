package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.entity.Configuration;
import net.koffeepot.presetqueries.entity.Parameter;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest


public class QueryParameterUpdateTest {

    @Autowired
    QueryService queryService;

    @MockBean
    private ConfigurationRepository configurationRepository;
    private Query queryPosted;
    private Query queryStored;

    @Test
    public void noParamTest() {


        given(configurationRepository.findOne(new Long(1))).willReturn(new Configuration(Long.valueOf(1), "confNameX", null, null));

        queryPosted.setDescription("descX");
        queryPosted.setName("nameX");
        queryPosted.setTemplate("templateX");

        ((QueryServiceImpl)queryService).updateQueryData(queryPosted, queryStored);

        assertThat(queryStored.getConfiguration().getName()).isEqualTo("confNameX");
        assertThat(queryStored.getDescription()).isEqualTo("descX");
        assertThat(queryStored.getName()).isEqualTo("nameX");
        assertThat(queryStored.getTemplate()).isEqualTo("templateX");

        assertThat(queryStored.getParameters()).isEmpty();
    }

    @Test
    public void twoParamTest() {

        queryPosted.getParameters().add(new Parameter("param1", ParameterType.FROM, null));
        queryPosted.getParameters().add(new Parameter("param2", ParameterType.WHERE, null));

        queryStored.getParameters().add(new Parameter("param3", ParameterType.WHERE_OPTIONAL, "optionalFragment"));
        queryStored.getParameters().add(new Parameter("param4", ParameterType.FROM, null));

        ((QueryServiceImpl)queryService).updateQueryData(queryPosted, queryStored);

        twoParametersCheck();
    }

    @Test
    public void addOneParameter() {

        queryPosted.getParameters().add(new Parameter("param1", ParameterType.FROM, null));
        queryPosted.getParameters().add(new Parameter("param2", ParameterType.WHERE, null));

        queryStored.getParameters().add(new Parameter("param3", ParameterType.WHERE_OPTIONAL, "optionalFragment"));
        //But not the second one

        ((QueryServiceImpl)queryService).updateQueryData(queryPosted, queryStored);

        twoParametersCheck();
    }

    @Test
    public void addTwoParameter() {

        queryPosted.getParameters().add(new Parameter("param1", ParameterType.FROM, null));
        queryPosted.getParameters().add(new Parameter("param2", ParameterType.WHERE, null));

        //no params in query stored in database

        ((QueryServiceImpl)queryService).updateQueryData(queryPosted, queryStored);

        twoParametersCheck();
    }

    @Test
    public void removeAParameter() {

        queryPosted.getParameters().add(new Parameter("param1", ParameterType.FROM, null));
        queryPosted.getParameters().add(new Parameter("param2", ParameterType.WHERE, null));

        queryStored.getParameters().add(new Parameter("param3", ParameterType.WHERE_OPTIONAL, "optionalFragment"));
        queryStored.getParameters().add(new Parameter("param4", ParameterType.FROM, null));
        queryStored.getParameters().add(new Parameter("param5", ParameterType.WHERE, null));

        ((QueryServiceImpl)queryService).updateQueryData(queryPosted, queryStored);

        twoParametersCheck();
    }

    @Test
    public void removeAllParameters() {

        queryStored.getParameters().add(new Parameter("param3", ParameterType.WHERE_OPTIONAL, "optionalFragment"));
        queryStored.getParameters().add(new Parameter("param4", ParameterType.FROM, null));

        ((QueryServiceImpl)queryService).updateQueryData(queryPosted, queryStored);

        assertThat(queryStored.getParameters().size()).isEqualTo(0);
    }

    private void twoParametersCheck() {
        assertThat(queryStored.getParameters().size()).isEqualTo(2);

        for (Parameter param: queryStored.getParameters()) {
            if ("param1".equals(param.getName())) {
                assertThat(param.getType()).isEqualTo(ParameterType.FROM);
            }
            else {
                assertThat(param.getName()).isEqualTo("param2");
                assertThat(param.getType()).isEqualTo(ParameterType.WHERE);
            }
        }
    }

    @Before
    public void setUp() {
        queryPosted = new Query();
        queryStored = new Query();

        queryPosted.setConfiguration(new Configuration(Long.valueOf(1), null, null, null));

        given(configurationRepository.findOne(new Long(1))).willReturn(new Configuration(Long.valueOf(1), "confNameX", null, null));
    }
}


