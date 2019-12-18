package com.web.crawler.webcrawler.Controller;

import com.web.crawler.webcrawler.Request.WebCrawlerRequest;
import com.web.crawler.webcrawler.Response.Error;
import com.web.crawler.webcrawler.Response.ResultResponse;
import com.web.crawler.webcrawler.Response.StatusResponse;
import com.web.crawler.webcrawler.Response.WebCrawlerResponse;
import com.web.crawler.webcrawler.Service.WebCrawlerService;
import com.web.crawler.webcrawler.Util.Constants;
import com.web.crawler.webcrawler.Util.ValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class WebController {

    @Autowired
    private WebCrawlerService webCrawlerService;

    @RequestMapping(value = "/webCrawl", method = RequestMethod.POST)
    public @ResponseBody
    WebCrawlerResponse webCrawl(@RequestBody WebCrawlerRequest webCrawlerRequest) {
        WebCrawlerResponse webCrawlerResponse = new WebCrawlerResponse();
        int depth = webCrawlerRequest.getDepth();
        String url = webCrawlerRequest.getUrl();
        String ackToken = UUID.randomUUID().toString();
        try {
            webCrawlerService.persistState(ackToken, Constants.SUBMITTED);
            Error error = ValidateUtil.validateWebCrawlRequest(webCrawlerRequest);
            if (!error.getAdditionalProperties().isEmpty()) {
                error.setDependency("Crawler");
                error.setErrorCode("701");
                webCrawlerResponse.setError(error);
                return webCrawlerResponse;
            } else {
                webCrawlerResponse.setAckToken(ackToken);
                webCrawlerService.crawl(ackToken, url, depth);
            }
        } catch (Exception ex) {
            webCrawlerService.persistState(ackToken,Constants.FAILED);
            ex.printStackTrace();
        }
        return webCrawlerResponse;
    }

    @RequestMapping(value = "/result",method = RequestMethod.GET)
    public @ResponseBody
    ResultResponse result(@RequestParam("id") String id) {
        String ackToken = id;
        ResultResponse resultResponse = null;
        try {
            Error error = ValidateUtil.validateResultRequest(id);
            if (!error.getAdditionalProperties().isEmpty()) {
                error.setDependency("Result");
                error.setErrorCode("702");
                resultResponse.setError(error);
                return resultResponse;
            } else {
                resultResponse = webCrawlerService.getPersistedResult(ackToken);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultResponse;
    }

    @RequestMapping("/status")
    public @ResponseBody  StatusResponse status(@RequestParam("id") String id){
        String ackToken = id;
        StatusResponse statusResponse = new StatusResponse();
        try{
            Error error = ValidateUtil.validateStatusRequest(id);
            if (!error.getAdditionalProperties().isEmpty()) {
                error.setDependency("Status");
                error.setErrorCode("703");
                statusResponse.setError(error);
                return statusResponse;
            } else {
                String status = webCrawlerService.getPersistedStatus(ackToken);
                statusResponse.setStatus(status);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return statusResponse;
    }

    @RequestMapping("/healthCheck")
    public String healthCheck() {
        return "Success!";
    }

}
