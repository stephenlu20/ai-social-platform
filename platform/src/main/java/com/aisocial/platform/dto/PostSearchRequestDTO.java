package com.aisocial.platform.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.UUID;

public class PostSearchRequestDTO {

    private String query;
    private UUID authorId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant end;

    private int page = 0;
    private int size = 20;

    public PostSearchRequestDTO() {}

    // ---------------------
    // Getters and Setters
    // ---------------------
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }

    public Instant getStart() { return start; }
    public void setStart(Instant start) { this.start = start; }

    public Instant getEnd() { return end; }
    public void setEnd(Instant end) { this.end = end; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
