package net.koffeepot.presetqueries.service;


import net.koffeepot.presetqueries.view.QueryResponse;
import net.koffeepot.presetqueries.entity.Query;

import java.util.List;

public interface QueryService {
    Iterable<Query> getQueries();

    QueryResponse execQuery(Query query);

    Query updateQuery(Query query);

    Query getQuery(String sId);
}
