package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import net.koffeepot.presetqueries.entity.Parameter;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.datasource.DataSourceFactory;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
import net.koffeepot.presetqueries.repository.ParameterRepository;
import net.koffeepot.presetqueries.repository.QueryRepository;
import net.koffeepot.presetqueries.view.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;

@Component
public class QueryServiceImpl implements QueryService {

    @Autowired
    private
    QueryRepository queryRepository;

    @Autowired
    private
    ParameterRepository parameterRepository;

    @Autowired
    private
    ConfigurationRepository configurationRepository;

    @Override
    public Query getQuery(String sId) {
        long id;
        try {
            id = Long.parseLong(sId);
        }
        catch (NumberFormatException nfe) {
            throw new TechnicalRuntimeException("Not a correct id for a query: "+sId);
        }

        Query storedQuery = queryRepository.findOne(id);

        if (storedQuery == null) {
            throw new TechnicalRuntimeException("Query not found: "+sId+" ("+id+")");
        }

        return storedQuery;

    }

    @Override
    public QueryResponse execQuery(Query postedQuery) {
        Query storedQuery = checkAndGetQueryForExec(postedQuery);
        QueryResponse queryResponse = new QueryResponse();
        //queryResponse will act as a collecting parameter except for mergedParams which I don't want to send back to the client
        Configuration queryConfiguration = storedQuery.getConfiguration();

        mergePostedParametersInStoredQuery(postedQuery.getParameters(), storedQuery.getParameters());
        MapSqlParameterSource mergedParams  = computeTemplateAndParams(storedQuery.getTemplate(), storedQuery.getParameters(), queryResponse);

        queryResponse.setQuery(postedQuery);

        computeHeaderAndData(queryConfiguration, mergedParams, queryResponse);
        return queryResponse;
    }

    @Override
    public Query updateQuery(Query postedQuery) {
        Query storedQuery = checkAndGetQueryForUpdate(postedQuery);
        updateQueryData(postedQuery, storedQuery);
        this.parameterRepository.save(storedQuery.getParameters());
        this.queryRepository.save(storedQuery);
        return storedQuery;
    }

    @Override
    public void deleteQuery(String sId) {
        long id;
        try {
            id = Long.parseLong(sId);
        }
        catch (NumberFormatException nfe) {
            throw new TechnicalRuntimeException("Not a correct id for a query: "+sId);
        }

        queryRepository.delete(id);

    }

    private Query checkAndGetQueryForExec(Query postedQuery) {
        //Checks
        if (postedQuery == null || postedQuery.getId() == null) {
            throw new TechnicalRuntimeException("No query in the body or id is null");
        }

        Query storedQuery = queryRepository.findOne(postedQuery.getId());

        if (storedQuery == null) {
            throw new TechnicalRuntimeException("Query not found: "+ postedQuery.getId());
        }

        if (storedQuery.getTemplate() == null) {
            throw new TechnicalRuntimeException("Query has no template: "+ postedQuery.getId());
        }

        if (!storedQuery.getTemplate().toUpperCase().startsWith("SELECT")) {
            throw new TechnicalRuntimeException("Query must start with SELECT: "+ storedQuery.getTemplate());
        }

        if (storedQuery.getConfiguration() == null) {
            throw new TechnicalRuntimeException("Query has no source configuration: "+ storedQuery.getName());
        }

        return storedQuery;
    }

    /***
     * Stores values given by the user in the query parameters
     * in order to use the stored parameter and not the posted parameter that the client could have changed
     * @param postedParameters
     * @param storedParameters
     */
    void mergePostedParametersInStoredQuery(Set<Parameter> postedParameters, Set<Parameter> storedParameters) {
        Map<String, String> postedMap = new HashMap<>();
        postedParameters.forEach(param -> postedMap.put(param.getName(), param.getUserValue()));
        for (Parameter storedParam: storedParameters) {
            if (postedMap.containsKey(storedParam.getName())) {
                storedParam.setUserValue(postedMap.get(storedParam.getName()));
            }
        }
    }

    MapSqlParameterSource computeTemplateAndParams(String template, Set<Parameter> storedParams, QueryResponse queryResponse) {
        MapSqlParameterSource mergedParams = new MapSqlParameterSource();
        String jdbcTemplateString = template;
        for (Parameter param : storedParams) {
            if (param.getType() != null){
                // Set null to "" in order 'replace all' not to fail if no user value set
                String userValue = param.getUserValue()==null?"":param.getUserValue();
                switch (param.getType()) {
                    case FROM:
                    case FROM_LIST:
                        jdbcTemplateString = jdbcTemplateString.replaceAll(":"+ param.getName(), userValue);
                        break;
                    case WHERE:
                    case WHERE_LIST:
                        mergedParams.addValue(param.getName(), userValue);
                        break;
                    case WHERE_OPTIONAL:
                        if (null != userValue && !userValue.trim().isEmpty()) {
                            jdbcTemplateString = jdbcTemplateString.replaceAll(":"+ param.getName(), param.getOptions());
                            mergedParams.addValue(param.getName(), userValue);
                        }
                        else {
                            jdbcTemplateString = jdbcTemplateString.replaceAll(":"+ param.getName(), "");
                        }
                        break;
                    case DRILLING_QUERY:
                        break;
                }
            }
        }
        queryResponse.setJdbcTemplate(jdbcTemplateString);
        return mergedParams;
    }

    private void computeHeaderAndData(Configuration queryConfiguration, MapSqlParameterSource mergedParams, QueryResponse queryResponse) {
        try {
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(
                    DataSourceFactory.getDataSource(queryConfiguration)
            );

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(queryResponse.getJdbcTemplate(), mergedParams);

            //Getting data and header from rowset
            SqlRowSetMetaData md = rowSet.getMetaData();
            List<String> header = Arrays.asList(md.getColumnNames());
            List<List<String>> data = new ArrayList<>();
            while (rowSet.next()) {
                List<String> row = new ArrayList<>(header.size());
                for (String col: header) {
                    row.add(rowSet.getObject(col)==null ? null : rowSet.getObject(col).toString());
                }
                data.add(row);
            }
            queryResponse.setData(data);
            queryResponse.setHeader(header);
        }
        catch (SQLException ex) {
            throw new TechnicalRuntimeException(ex);
        }
    }

    private Query checkAndGetQueryForUpdate(Query postedQuery) {
        //Checks
        if (postedQuery == null) {
            throw new TechnicalRuntimeException("No query in the body");
        }

        Query storedQuery;
        if (postedQuery.getId() == null) { //new query, let's do some checks
            if (postedQuery.getConfigurationId() == null) {
                throw new TechnicalRuntimeException("No configuration found in the query");
            }
            storedQuery = new Query();
        }
        else {
            storedQuery = queryRepository.findOne(postedQuery.getId());
            if (storedQuery == null) {
                throw new TechnicalRuntimeException("Query not found: "+postedQuery.getId());
            }
        }
        return storedQuery;
    }

    void updateQueryData(Query postedQuery, Query storedQuery) {
        Configuration configuration = configurationRepository.findOne(postedQuery.getConfigurationId());
        Set<Parameter> parametersToRemove = storedQuery.update(postedQuery, configuration);
        for (Parameter parameter: parametersToRemove) {
            if (parameter.getQueries().size()==1) {
                //the parameter still has the query in its queries, but only the query. We can thus garbage it.
                parameterRepository.delete(parametersToRemove);
            }
        }
    }

}
