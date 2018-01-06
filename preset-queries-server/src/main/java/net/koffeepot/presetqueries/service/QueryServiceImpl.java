package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Parameter;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.entity.QueryResponse;
import net.koffeepot.presetqueries.repository.QueryRepository;
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
        queries.get(0).getParameters().add(new Parameter("param3", "Int"));
        queries.get(1).getParameters().add(new Parameter("param6", "String"));

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
