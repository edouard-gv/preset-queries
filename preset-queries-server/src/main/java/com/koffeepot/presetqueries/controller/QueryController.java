package com.koffeepot.presetqueries.controller;

import com.koffeepot.presetqueries.entity.Query;
import com.koffeepot.presetqueries.entity.QueryResponse;
import com.koffeepot.presetqueries.repository.QueryRepository;
import com.koffeepot.presetqueries.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QueryController {

    @Autowired
    private QueryService queryService;

    @Autowired
    private QueryRepository queryRepository;

    @RequestMapping(path = "/api/queries", method= RequestMethod.GET)
    public @ResponseBody List<Query> getQueries() {
        return queryService.getQueries();
    }

    @RequestMapping(path = "/api/query", method= RequestMethod.POST)
    public ResponseEntity<QueryResponse> auth(@RequestBody Query query) {
        try {
            return new ResponseEntity<>(queryService.postQuery(query), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<QueryResponse>(new QueryResponse(query, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
