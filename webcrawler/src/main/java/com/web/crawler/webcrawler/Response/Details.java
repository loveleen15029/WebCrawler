package com.web.crawler.webcrawler.Response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {
    @JsonProperty("page_title")
    private String pageTitle;
    @JsonProperty("page_link")
    private String pageLink;
    @JsonProperty("image_count")
    private int imageCount;

    @JsonIgnore
    private int linkCount;

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public int getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(int linkCount) {
        this.linkCount = linkCount;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (obj instanceof Details) {
            Details temp = (Details) obj;
            if (this.pageTitle.equals(temp.pageTitle))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.pageTitle.hashCode());
    }
}
