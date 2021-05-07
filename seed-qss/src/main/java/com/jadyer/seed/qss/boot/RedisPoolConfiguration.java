package com.jadyer.seed.qss.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnClass({JedisPool.class})
@ConfigurationProperties(prefix="redis")
public class RedisPoolConfiguration {
    private int connectionTimeout = 2000;
    private int soTimeout = 2000;
    private int maxWaitMillis = 100*1000;
    private int maxTotal = 5;
    private int maxIdle = 2;
    private int minIdle = 1;
    private String password;
    private List<String> nodes = new ArrayList<>();

    @Bean
    public JedisPool getPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(this.maxTotal);
        config.setMaxIdle(this.maxIdle);
        config.setMinIdle(this.minIdle);
        config.setBlockWhenExhausted(true);
        config.setMaxWaitMillis(this.maxWaitMillis);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        String[] parts = StringUtils.split(this.nodes.get(0), ":");
        Assert.state(null!=parts&&parts.length==2, "redis node shoule be defined as 'host:port', not '" + Arrays.toString(parts) + "'");
        JedisPool pool = new JedisPool(config, parts[0], Integer.parseInt(parts[1]),  this.connectionTimeout, this.password);
        for(int i=0; i<this.minIdle; i++){
            Jedis jedis = pool.getResource();
            jedis.ping();
            jedis.close();
        }
        return pool;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getNodes() {
        return nodes;
    }
}