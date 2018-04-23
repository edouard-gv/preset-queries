package net.koffeepot.presetqueries.controller;

import net.koffeepot.presetqueries.PresetQueriesConfiguration;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
import net.koffeepot.presetqueries.repository.QueryRepository;
import net.koffeepot.presetqueries.service.QueryService;
import net.koffeepot.presetqueries.service.RoleService;
import net.koffeepot.presetqueries.view.QueryResponse;
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

    @Autowired
    private RoleService roleService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @RequestMapping(path = "/api/queries", method= RequestMethod.GET)
    public @ResponseBody List<Query> getQueries(@RequestHeader(value = PresetQueriesConfiguration.KOFFE_POT_TOKEN, defaultValue = "none") String token) {
        roleService.checkTokenAtLeast(token, RoleService.RoleLevel.READ);

        //we iterate through the list at the last moment
        List<Query> queryList = new ArrayList<>();
        queryRepository.findAll().forEach(queryList::add);
        return queryList;
    }

    @RequestMapping(path = "/api/execute", method= RequestMethod.POST)
    public ResponseEntity<QueryResponse> exec(@RequestBody Query query, @RequestHeader(value = PresetQueriesConfiguration.KOFFE_POT_TOKEN, defaultValue = "none") String token) {
        roleService.checkTokenAtLeast(token, RoleService.RoleLevel.READ);
        return new ResponseEntity<>(queryService.execQuery(query), HttpStatus.OK);
    }

    @RequestMapping(path = "/api/query", method= RequestMethod.POST)
    public ResponseEntity<Query> update(@RequestBody Query query, @RequestHeader(value = PresetQueriesConfiguration.KOFFE_POT_TOKEN, defaultValue = "none") String token) {
        roleService.checkTokenAtLeast(token, RoleService.RoleLevel.WRITE);
        return new ResponseEntity<>(queryService.updateQuery(query), HttpStatus.OK);
    }

    @RequestMapping(path = "/api/query/{id}", method= RequestMethod.GET)
    public ResponseEntity<Query> get(@PathVariable("id") String id, @RequestHeader(value = PresetQueriesConfiguration.KOFFE_POT_TOKEN, defaultValue = "none") String token) {
        roleService.checkTokenAtLeast(token, RoleService.RoleLevel.READ);
        return new ResponseEntity<>(queryService.getQuery(id), HttpStatus.OK);
    }

    @RequestMapping(path = "/api/query/{id}", method= RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") String id, @RequestHeader(value = PresetQueriesConfiguration.KOFFE_POT_TOKEN, defaultValue = "none") String token) {
        roleService.checkTokenAtLeast(token, RoleService.RoleLevel.WRITE);
        queryService.deleteQuery(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
