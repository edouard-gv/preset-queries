package com.koffeepot.presetqueries.repository;

import com.koffeepot.presetqueries.common.TechnicalRuntimeException;
import com.koffeepot.presetqueries.entity.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QueryRepository extends CrudRepository<Query, Long> {

    public List<Query> findByName(String name);

    public default Query findQueryByName(String name) {
        List<Query> queries = this.findByName(name);
        if (queries.isEmpty())
            return null;

        if (queries.size()>1)
            throw new TechnicalRuntimeException("Found "+ queries.size()+" queries for name "+name);

        return queries.get(0);
    }

}
