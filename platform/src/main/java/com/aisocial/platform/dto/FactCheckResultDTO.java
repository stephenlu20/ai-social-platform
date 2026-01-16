package com.aisocial.platform.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FactCheckResultDTO {

    private String verdict;
    private Integer confidence;
    private String summary;
    private List<String> reasoning;
    private List<Source> sources;
    private String error;

    public FactCheckResultDTO() {}

    public static FactCheckResultDTO error(String errorMessage) {
        FactCheckResultDTO result = new FactCheckResultDTO();
        result.setVerdict("ERROR");
        result.setConfidence(0);
        result.setSummary(errorMessage);
        result.setError(errorMessage);
        return result;
    }

    // Getters and Setters
    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public Integer getConfidence() { return confidence; }
    public void setConfidence(Integer confidence) { this.confidence = confidence; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getReasoning() { return reasoning; }
    public void setReasoning(List<String> reasoning) { this.reasoning = reasoning; }

    public List<Source> getSources() { return sources; }
    public void setSources(List<Source> sources) { this.sources = sources; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        private String title;
        private String url;
        private String relevance;

        public Source() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getRelevance() { return relevance; }
        public void setRelevance(String relevance) { this.relevance = relevance; }
    }
}
