package net.koffeepot.presetqueries.controller;

import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.view.QueryResponse;
import net.koffeepot.presetqueries.repository.QueryRepository;
import net.koffeepot.presetqueries.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QueryController {

    @Autowired
    private QueryService queryService;

    @Autowired
    private QueryRepository queryRepository;

    @RequestMapping(path = "/api/queries", method= RequestMethod.GET)
    public @ResponseBody List<Query> getQueries() {
        //we iterate through the list at the last moment
        List<Query> queryList = new ArrayList<>();
        queryService.getQueries().forEach(queryList::add);
        return queryList;
    }

    @RequestMapping(path = "/api/execute", method= RequestMethod.POST)
    public ResponseEntity<QueryResponse> exec(@RequestBody Query query) {
        return new ResponseEntity<>(queryService.execQuery(query), HttpStatus.OK);
    }

    @RequestMapping(path = "/api/query", method= RequestMethod.POST)
    public ResponseEntity<Query> update(@RequestBody Query query) {
        return new ResponseEntity<>(queryService.updateQuery(query), HttpStatus.OK);
    }

    @RequestMapping(path = "/api/query/{name}", method= RequestMethod.GET)
    public ResponseEntity<Query> get(@PathVariable("name") String name) {
        return new ResponseEntity<>(queryService.getQuery(name), HttpStatus.OK);
    }
}
