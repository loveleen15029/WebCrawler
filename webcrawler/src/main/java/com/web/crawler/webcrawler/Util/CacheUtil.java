package com.web.crawler.webcrawler.Util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class CacheUtil {

    @Value("#{'${jedis.cluster.host}'}")
    private List<String> jedisHost;

    private JedisCluster jedisCluster;

    @PostConstruct
    public void init() {
        Set<HostAndPort> hostAndPortNodeSet = new HashSet<HostAndPort>(jedisHost.size());
        for (int i = 0; i < jedisHost.size(); i++) {
            hostAndPortNodeSet.add(new HostAndPort(jedisHost.get(i).split(":")[0],
                    Integer.parseInt(jedisHost.get(i).split(":")[1])));
        }
        jedisCluster = new JedisCluster(hostAndPortNodeSet);
    }


    public void writeInCacheWithTTL(String key, String value,final int ttl) {
        jedisCluster.setex(key, ttl,value);
    }

    public String get(String key) {
        return jedisCluster.get(key);
    }

}
