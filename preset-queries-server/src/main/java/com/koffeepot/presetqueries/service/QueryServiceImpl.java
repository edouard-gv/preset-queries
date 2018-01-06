package com.koffeepot.presetqueries.service;

import com.koffeepot.presetqueries.common.TechnicalRuntimeException;
import com.koffeepot.presetqueries.entity.Parameter;
import com.koffeepot.presetqueries.entity.Query;
import com.koffeepot.presetqueries.entity.QueryResponse;
import com.koffeepot.presetqueries.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class QueryServiceImpl implements QueryService {

    @Autowired
    private
    QueryRepository queryRepository;

    @Override
    public List<Query> getQueries() {
        List<Query> queries = new ArrayList<>();
        queries.add(new Query("Requête", "Une description"));
        queries.add(new Query("Coupons", "Pour Yvonne"));
        queries.add(new Query("Requête sans params", "Pour test"));

        queries.get(0).getParameters().add(new Parameter("param1", "String"));
        queries.get(0).getParameters().add(new Parameter("param2", "Int"));
        queries.get(1).getParameters().add(new Parameter("param3", "String"));

        return queries;
    }

    @Override
    public QueryResponse postQuery(Query query) {
        if (query == null || query.getName() == null) {
            throw new TechnicalRuntimeException("No query in the body or name is null");
        }

        String[] headerArray = {"nom", "val"};
        String[][] dataArray = {{"debug", "1"}, {"info", "2"}};

        List<List<String>> data = new ArrayList<>();
        for(String[] array : dataArray){
            data.add( Arrays.asList(array) );
        }

        return new QueryResponse(query, Arrays.asList(headerArray), data);
    }
}
