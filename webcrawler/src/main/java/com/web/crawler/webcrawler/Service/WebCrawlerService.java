package com.web.crawler.webcrawler.Service;

import com.google.gson.Gson;
import com.web.crawler.webcrawler.Response.Details;
import com.web.crawler.webcrawler.Response.ResultResponse;
import com.web.crawler.webcrawler.Util.CacheUtil;
import com.web.crawler.webcrawler.Util.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@EnableAsync
@Component
public class WebCrawlerService {

    private Gson gson;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private ThreadPoolTaskExecutor webcrawlerTaskExecutor;

    @PostConstruct
    public void init() {
        gson = new Gson();
    }

    @Value("${crawler.thread.timeout}")
    private int crawlerTimeout;

    /*Using BFS to make sure of the depth. Basically is being handeled by introducing new character # in tree.*/


    @Async
    public void crawl(String ackToken, String url, int depth) {
        persistState(ackToken, Constants.INPROCESS);
        ResultResponse resultResponse = new ResultResponse();
        Document doc;
        int totalLinks = 0;
        int totalImages = 0;
        ArrayList<String> urls = new ArrayList<>();
        try {
            urls.add(url);
            urls.add("#");
            int count = 0;
            List<Future<Details>> details = new ArrayList<>();
            HashSet<Details> detailsHashSet = new HashSet<>();
            List<Details> detailsList = new ArrayList<>();
            for (int i = 0; i < urls.size(); i++) {
                String newUrl = urls.get(i);
                if (newUrl.equalsIgnoreCase("#")) {
                    count++;
                    if (depth == count) {
                        break;
                    }
                    urls.add("#");
                    continue;
                }
                if (!newUrl.contains("://")) {
                    continue;
                }
                doc = Jsoup.connect(newUrl).get();
                Elements links = doc.select("a[href]");
                int thCount = 0;
                for (Element link : links) {
                    thCount++;
                    details.add(webcrawlerTaskExecutor.submit(() -> {
                        return innerDocParsing(urls, link);
                    }));
                }

                for (int j = 0; j < thCount; j++) {
                    Future<Details> detailsFuture = details.get(j);
                    try {
                        Details detail = detailsFuture.get(crawlerTimeout, TimeUnit.MILLISECONDS);
                        if (detail != null) {
                            detailsList.add(detail);
                        }
                    } catch (TimeoutException ex) {
                        persistState(ackToken,Constants.FAILED);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                detailsHashSet.addAll(detailsList);
                detailsList.clear();
                detailsList.addAll(detailsHashSet);
                for(Details detailsEle : detailsList){
                    totalLinks++;
                    totalImages += detailsEle.getImageCount();
                }

            }

//          Main Url Images addition. First Page Images Addition
            Document imgDoc = Jsoup.connect(urls.get(0)).get();
            Elements imgLinks = imgDoc.select("img[src]");
            int imgCount = 0;
            for (Element innerLink : imgLinks) {
                imgCount++;
            }
            totalImages += imgCount;
            totalLinks += 1;
            resultResponse.setTotalLinks(totalLinks);
            resultResponse.setTotalImages(totalImages);
            resultResponse.setDetails(detailsList);
            persistResult(ackToken, resultResponse);
            persistState(ackToken, Constants.FINISHED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Details innerDocParsing(ArrayList<String> urls, Element link) throws IOException {
        Details detail = new Details();
        String innerurl = link.attr("abs:href").toString();
        if (!innerurl.contains("://")) {
            return null;
        }
        urls.add(innerurl);
        detail.setPageLink(innerurl);

        Document innerDoc = Jsoup.connect(innerurl).get();
        Elements title = innerDoc.select("title");
        detail.setPageTitle(title.text());

        Elements imgLinks = innerDoc.select("img[src]");
        int imgCount = 0;
        for (Element innerLink : imgLinks) {
            imgCount++;
        }
        detail.setImageCount(imgCount);
        return detail;
    }

    private void persistResult(String ackToken, ResultResponse resultResponse) {
        String result = gson.toJson(resultResponse);
        cacheUtil.writeInCacheWithTTL(Constants.CRAWL + ackToken, result, 7200);
    }

    public ResultResponse getPersistedResult(String ackToken) {
        String result = cacheUtil.get(Constants.CRAWL + ackToken);
        ResultResponse resultResponse = gson.fromJson(result, ResultResponse.class);
        return resultResponse;
    }

    public void persistState(String ackToken, String status) {
        cacheUtil.writeInCacheWithTTL(ackToken, status, 7200);
    }

    public String getPersistedStatus(String ackToken) {
        return cacheUtil.get(ackToken);
    }
}
