package net.koffeepot.presetqueries.service;


import net.koffeepot.presetqueries.view.QueryResponse;
import net.koffeepot.presetqueries.entity.Query;

import java.util.List;

public interface QueryService {
    QueryResponse execQuery(Query query);

    Query updateQuery(Query query);

    Query getQuery(String sId);

    void deleteQuery(String sId);
}
