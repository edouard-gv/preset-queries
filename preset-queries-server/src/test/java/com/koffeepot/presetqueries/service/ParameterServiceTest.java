package com.koffeepot.presetqueries.service;

import com.koffeepot.presetqueries.entity.Query;
import com.koffeepot.presetqueries.repository.QueryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParameterServiceTest {

    @MockBean
    private
    QueryRepository queryRepository;

    @Before
    public void init() {
        Query query = new Query("login", "pwd");
        given(queryRepository.findQueryByName("name")).willReturn(query);
    }

    @Override
    protected void finalize() {
    }

    @Test
    public void testParamter() throws Exception {

    }
}