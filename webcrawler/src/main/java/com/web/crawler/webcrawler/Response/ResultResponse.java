package com.web.crawler.webcrawler.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse {
    @JsonProperty("total_links")
    private int totalLinks;
    @JsonProperty("total_images")
    private int totalImages;
    @JsonProperty("details")
    private List<Details> details;

    private Error error;

    public int getTotalLinks() {
        return totalLinks;
    }

    public void setTotalLinks(int totalLinks) {
        this.totalLinks = totalLinks;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public void setTotalImages(int totalImages) {
        this.totalImages = totalImages;
    }

    public List<Details> getDetails() {
        return details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
