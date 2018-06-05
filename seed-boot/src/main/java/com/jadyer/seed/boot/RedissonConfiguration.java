package com.jadyer.seed.boot;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 【待测试】
 */
//@Configuration
@ConditionalOnClass({RedissonClient.class})
@ConfigurationProperties(prefix="redisson")
public class RedissonConfiguration {
    private static final List<RedissonClient> redissonClientList = new ArrayList<>();
    private List<String> nodes = new ArrayList<>();

    public List<String> getNodes() {
        return nodes;
    }

    public static List<RedissonClient> getRedissonClientList() {
        return redissonClientList;
    }

    @PostConstruct
    public void initRedissonClientList(){
        for (String node : nodes) {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://" + node);
            redissonClientList.add(Redisson.create(config));
        }
    }
}