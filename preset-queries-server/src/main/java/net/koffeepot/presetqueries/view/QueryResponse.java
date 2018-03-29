package net.koffeepot.presetqueries.view;

import net.koffeepot.presetqueries.entity.Query;

import java.util.List;

public class QueryResponse {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public List<List<String>>getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    private List<List<String>> data;

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    private List<String> header;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    private Query query;

    public String getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(String jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String jdbcTemplate;

}
