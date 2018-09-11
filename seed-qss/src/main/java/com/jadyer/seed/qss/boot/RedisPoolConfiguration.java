package com.jadyer.seed.qss.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * --------------------------------------------------------------------------------------------
 * 目标Redis为单机：可使用Jedis或JedisPool对象
 * 目标Redis为分布式的（多端口）：可使用ShardedJedisPool对象
 * --------------------------------------------------------------------------------------------
 * 阿里云Redis客户端连接：https://help.aliyun.com/document_detail/43848.html
 * 阿里云Redis消息发布订阅：https://help.aliyun.com/document_detail/26368.html
 * --------------------------------------------------------------------------------------------
 * 使用举例：
 * @Resource
 * private JedisPool jedisPool;
 * try (Jedis jedis = jedisPool.getResource()) {
 *     jedis.publish("seed_channel", jsonData);
 * }
 * --------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/24 10:46.
 */
@Configuration
@SuppressWarnings("WeakerAccess")
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
        //pool中最大连接数（若赋值为-1，则表示不限制）
        //如果pool已分配完所有jedis实例，则此时池状态为exhausted（耗尽）
        config.setMaxTotal(this.maxTotal);
        //pool允许最大空闲的连接数
        config.setMaxIdle(this.maxIdle);
        //pool确保最少空闲的连接数
        config.setMinIdle(this.minIdle);
        //pool用尽后，调用者是否要等待（默认值为true，只有true时下面的maxWaitMillis才会生效）
        config.setBlockWhenExhausted(true);
        //pool连接用尽后，调用者的最大等待时间，超过等待时间则直接抛出JedisConnectionException（单位为毫秒，默认值为-1，标识永不超时）
        config.setMaxWaitMillis(this.maxWaitMillis);
        //借用连接从pool时是否检查连接可用性（默认值为false），业务量很大时建议设为false（多一次ping的开销）
        config.setTestOnBorrow(false);
        //归还连接给pool时是否检查连接可用性（默认值为false），业务量很大时建议设为false（多一次ping的开销）
        config.setTestOnReturn(false);
        //List<JedisShardInfo> nodes = new ArrayList<>();
        //for(String node : this.getNodes()){
        //    try{
        //        String[] parts = StringUtils.split(node, ":");
        //        Assert.state(parts.length==2, "redis node shoule be defined as 'host:port', not '" + Arrays.toString(parts) + "'");
        //        nodes.add(new JedisShardInfo(parts[0], Integer.parseInt(parts[1]), this.connectionTimeout));
        //    }catch(RuntimeException e){
        //        throw new IllegalStateException("Invalid redis cluster nodes property '" + node + "'", e);
        //    }
        //}
        //return new ShardedJedisPool(config, nodes);
        //这是传URI的方式，会更简洁一些
        //URI uri = URI.create("redis://redis:password@redis.dev1.ctstest.com");
        //JedisPool pool = new JedisPool(config, uri, this.connectionTimeout);
        String[] parts = StringUtils.split(this.nodes.get(0), ":");
        JedisPool pool = new JedisPool(config, parts[0], Integer.parseInt(parts[1]),  this.connectionTimeout, this.password);
        //预热
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