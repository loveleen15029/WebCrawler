package com.web.crawler.webcrawler.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class ApplicationConfig {

    @Value("${crawler.core.pool.size}")
    private int crawlerTaskExecutorCorePool;
    @Value("${crawler.max.pool.size}")
    private int crawlerTaskExecutorMaxPool;
    @Value("${crawler.queue.size}")
    private int crawlerTaskExecutorQueue;
    @Value("${crawler.keepAliveTimeInMillis}")
    private int crawlerTaskExecutorKeepAliveTime;

    @Bean(name = "webcrawlerTaskExecutor")
    public ThreadPoolTaskExecutor dimensionTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(crawlerTaskExecutorCorePool);
        pool.setMaxPoolSize(crawlerTaskExecutorMaxPool);
        pool.setQueueCapacity(crawlerTaskExecutorQueue);
        pool.setKeepAliveSeconds(crawlerTaskExecutorKeepAliveTime);
        pool.setThreadNamePrefix("crawlerTaskExecutor");
        return pool;
    }

}
