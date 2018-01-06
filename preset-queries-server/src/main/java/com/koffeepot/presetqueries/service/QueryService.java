package com.koffeepot.presetqueries.service;


import com.koffeepot.presetqueries.entity.QueryResponse;
import com.koffeepot.presetqueries.entity.Query;

import java.util.List;

public interface QueryService {
    List<Query> getQueries();

    QueryResponse postQuery(Query query);
}
