package net.koffeepot.presetqueries.service;


import net.koffeepot.presetqueries.entity.QueryResponse;
import net.koffeepot.presetqueries.entity.Query;

import java.util.List;

public interface QueryService {
    List<Query> getQueries();

    QueryResponse postQuery(Query query);
}
