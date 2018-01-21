package net.koffeepot.presetqueries.service;

import net.koffeepot.presetqueries.common.TechnicalRuntimeException;
import net.koffeepot.presetqueries.entity.Configuration;
import net.koffeepot.presetqueries.entity.Parameter;
import net.koffeepot.presetqueries.entity.Query;
import net.koffeepot.presetqueries.datasource.DataSourceFactory;
import net.koffeepot.presetqueries.repository.ConfigurationRepository;
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
    ConfigurationRepository configurationRepository;

    @Override
    public QueryResponse execQuery(Query postedQuery) {
        //Checks
        if (postedQuery == null || postedQuery.getId() == null) {
            throw new TechnicalRuntimeException("No query in the body or id is null");
        }

        Query storedQuery = queryRepository.findOne(postedQuery.getId());

        if (storedQuery == null) {
            throw new TechnicalRuntimeException("Query not found: "+postedQuery.getId());
        }

        if (storedQuery.getTemplate() == null) {
            throw new TechnicalRuntimeException("Query has no template: "+postedQuery.getId());
        }

        //Storing temporary values given by the user in the query parameters so that the user cannot force anything
        mergePostedParametersInStoredQuery(postedQuery.getParameters(), storedQuery.getParameters());

        if (!storedQuery.getTemplate().toUpperCase().startsWith("SELECT")) {
            throw new TechnicalRuntimeException("Query must start with SELECT: "+storedQuery.getTemplate());
        }

        //Computing the prepared statement
        String jdbcTemplateString = mergeTemplate(storedQuery.getTemplate(), storedQuery.getParameters());

        try {
            Configuration configuration = storedQuery.getConfiguration();
            if (configuration == null) {
                throw new TechnicalRuntimeException("Query has no source configuration: "+storedQuery.getName());
            }
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(
                    DataSourceFactory.getDataSource(configuration)
            );

            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            //TODO: should be in the convert Template method to be nearby the similar logic
            //Filtering parameters that should be given to the sqltemplate
            for (Parameter param: storedQuery.getParameters()) {
                if (param.getType().startsWith("where")) {
                    paramSource.addValue(param.getName(), param.getUserValue());
                }
            }
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(jdbcTemplateString, paramSource);

            //Getting data and header from rowset
            SqlRowSetMetaData md = rowSet.getMetaData();
            List<String> header = Arrays.asList(md.getColumnNames());
            List<List<String>> data = new ArrayList<>();
            while (rowSet.next()) {
                List<String> row = new ArrayList(header.size());
                for (String col: header) {
                    row.add(rowSet.getObject(col)==null ? null : rowSet.getObject(col).toString());
                }
                data.add(row);
            }

            return new QueryResponse(postedQuery, header, data, jdbcTemplateString);
        }
        catch (SQLException ex) {
            throw new TechnicalRuntimeException(ex);
        }

    }

    @Override
    public Query updateQuery(Query postedQuery) {
        //Checks
        if (postedQuery == null || postedQuery.getId() == null) {
            throw new TechnicalRuntimeException("No query in the body or id is null");
        }

        Query storedQuery = queryRepository.findOne(postedQuery.getId());

        if (storedQuery == null) {
            throw new TechnicalRuntimeException("Query not found: "+postedQuery.getId());
        }

        storedQuery.setDescription(postedQuery.getDescription());
        storedQuery.setConfiguration(configurationRepository.findOne(postedQuery.getConfigurationId()));
        storedQuery.setName(postedQuery.getName());
        storedQuery.setTemplate(postedQuery.getTemplate());
        Iterator<Parameter> postedParams = postedQuery.getParameters().iterator();
        //No possibility to add parameters for the moment, and parameters are not shared by queries
        for (Parameter storedParam : storedQuery.getParameters()) {
            Parameter postedParam = postedParams.next();
            storedParam.setName(postedParam.getName());
            storedParam.setType(postedParam.getType());
            storedParam.setOptionalFragment(postedParam.getOptionalFragment());
        }


        this.queryRepository.save(storedQuery);

        return storedQuery;
    }

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

    String mergeTemplate(String template, Set<Parameter> parameters) {
        String jdbcTemplateString = template;
        for (Parameter param: parameters) {
            switch (param.getType()) {
                case "from":
                    jdbcTemplateString = jdbcTemplateString.replaceAll(":"+param.getName(), param.getUserValue());
                    break;
                case "where":
                    break;
                case "where-optional":
                    if (null != param.getUserValue() && !param.getUserValue().trim().isEmpty()) {
                        jdbcTemplateString = jdbcTemplateString.replaceAll(":"+param.getName(), param.getOptionalFragment());
                    }
                    else {
                        jdbcTemplateString = jdbcTemplateString.replaceAll(":"+param.getName(), "");
                    }
            }
        }
        return jdbcTemplateString;
    }

    void mergePostedParametersInStoredQuery(Set<Parameter> postedParameters, Set<Parameter> storedParameters) {
        Map<String, String> postedMap = new HashMap<>();
        postedParameters.forEach(param -> postedMap.put(param.getName(), param.getUserValue()));
        for (Parameter storedParam: storedParameters) {
            if (postedMap.containsKey(storedParam.getName())) {
                storedParam.setUserValue(postedMap.get(storedParam.getName()));
            }
        }
    }
}
