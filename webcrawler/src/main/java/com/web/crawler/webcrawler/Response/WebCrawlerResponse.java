package com.web.crawler.webcrawler.Response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebCrawlerResponse {

    private String ackToken;
    private Error error;

    public String getAckToken() {
        return ackToken;
    }

    public void setAckToken(String ackToken) {
        this.ackToken = ackToken;
    }


    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
