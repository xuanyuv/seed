package com.jadyer.seed.comm.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 这是Redis3.0的集成方法
 * 另经测试：当应用正常运行过程中，RedisCluster突然宕掉后过一段时间又恢复，这里会自动重连
 * 注意：Jedis-2.9.1、2.10.0、3.0.0请勿使用，高并发环境中会出现资源泄露，可100%重现
 * 注意：目前能用的最新版还是2016年的2.9.0，详见https://v2ex.com/t/518987
 * Created by 玄玉<https://jadyer.cn/> on 2016/11/24 10:46.
 */
//@Configuration
@ConditionalOnClass({JedisCluster.class})
@ConfigurationProperties(prefix="redis")
public class RedisConfiguration {
    //各个getter应该是public的才可以，否则其它地方使用JedisCluster对象时会报告空指针
    private int connectionTimeout = 2000;
    private int soTimeout = 5000;
    private int maxRedirections = 5;
    private int maxTotal = 16;
    private int maxIdle = 8;
    private int minIdle = 0;
    private String password;
    private List<String> nodes = new ArrayList<>();

    @Bean
    public JedisCluster jedisCluster(){
        Set<HostAndPort> nodes = new HashSet<>();
        for(String node : this.getNodes()){
            try{
                String[] parts = StringUtils.split(node, ":");
                Assert.state(null!=parts&&parts.length==2, "redis node shoule be defined as 'host:port', not '" + Arrays.toString(parts) + "'");
                nodes.add(new HostAndPort(parts[0], Integer.parseInt(parts[1])));
            }catch(RuntimeException e){
                throw new IllegalStateException("Invalid redis cluster nodes property '" + node + "'", e);
            }
        }
        if(nodes.isEmpty()){
            return null;
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(this.getMaxTotal());
        config.setMaxIdle(this.getMaxIdle());
        config.setMinIdle(this.getMinIdle());
        return new JedisCluster(nodes, this.getConnectionTimeout(), this.getSoTimeout(), this.getMaxRedirections(), this.getPassword(), config);
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

    public int getMaxRedirections() {
        return maxRedirections;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
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