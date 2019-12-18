package com.web.crawler.webcrawler.Util;

import com.web.crawler.webcrawler.Request.WebCrawlerRequest;
import org.springframework.util.StringUtils;
import com.web.crawler.webcrawler.Response.Error;

public class ValidateUtil {
    public static Error validateWebCrawlRequest(WebCrawlerRequest webCrawlerRequest){
        Error errors = new Error();
        if(StringUtils.isEmpty(webCrawlerRequest.getUrl())){
            errors.getAdditionalProperties().put("101","Url Is Empty or Nulll");
            return errors;
        }
        if(webCrawlerRequest.getDepth() <= 0){
            errors.getAdditionalProperties().put("102","Invalid depth");
            return errors;
        }
        return errors;
    }

    public static Error validateResultRequest(String id){
        Error errors = new Error();
        if(StringUtils.isEmpty(id)){
            errors.getAdditionalProperties().put("103","Acknowlegemet Token is not present");
            return errors;
        }
        return errors;
    }

    public static Error validateStatusRequest(String id){
        Error errors = new Error();
        if(StringUtils.isEmpty(id)){
            errors.getAdditionalProperties().put("103","Acknowlegemet Token is not present");
            return errors;
        }
        return errors;
    }
}
