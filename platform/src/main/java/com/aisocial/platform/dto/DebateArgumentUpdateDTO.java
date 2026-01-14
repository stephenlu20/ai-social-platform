package com.aisocial.platform.dto;

public class DebateArgumentUpdateDTO {
    private String content;
    private Integer roundNumber;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }
}