package com.koffeepot.presetqueries.entity;

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

    private List<List<String>> data;

    public List<String> getHeader() {
        return header;
    }

    private List<String> header;

    public Query getQuery() {
        return query;
    }

    private Query query;

    public QueryResponse(Query query, List<String> header, List<List<String>> data) {
        this.query = query;
        this.header = header;
        this.data = data;
    }

    public QueryResponse(Query query, String message) {
        this.query = query;
        this.message = message;
    }
}
