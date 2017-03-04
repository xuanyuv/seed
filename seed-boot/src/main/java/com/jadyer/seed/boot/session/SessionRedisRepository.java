package com.jadyer.seed.boot.session;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.io.Serializable;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/6/19 19:56.
 */
class SessionRedisRepository {
    private JedisCluster jedisCluster;
    /**
     * Session数据储存到Redis3.x里面时的key的前缀（完整的key是前缀加上MapSession对象生成时构造的ID）
     */
    private static final String SESSION_PREFIX = "demo-boot-session-";
    private int expireSeconds = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;

    SessionRedisRepository(JedisCluster jedisCluster, int expireSeconds){
        this.jedisCluster = jedisCluster;
        this.expireSeconds = expireSeconds;
    }


    public int getExpireSeconds(){
        return expireSeconds;
    }


    public void setExpireSeconds(int expireSeconds){
        this.expireSeconds = expireSeconds;
    }


    /**
     * 获得byte[]型的key
     * <p>其将作为key储存到Redis3.x，其值为“SESSION_PREFIX + MapSession对象生成时构造的ID”</p>
     */
    private byte[] getByteKey(Serializable sessionId){
        return (SESSION_PREFIX + sessionId).getBytes();
    }


    /**
     * @return an integer greater than 0 if one or more keys were removed, or 0 if none of the specified key existed
     */
    long delete(String sessionId){
        if(StringUtils.isBlank(sessionId)){
            LogUtil.getLogger().warn("sessionId is blank");
            return 0;
        }
        return jedisCluster.del(this.getByteKey(sessionId));
    }


    /**
     * 创建一个全新的Session数据，并存储到Redis3.x
     * <p>注意：该方法创建的不是HttpSession对象，只是HttpSession数据存储的一个介质，不可直接操作该介质</p>
     * @return 本次创建的Session数据
     */
    public MapSession create(){
        MapSession session = new MapSession();
        this.save(session);
        return session;
    }


    /**
     * 根据sessionId获取储存在Redix3.x中的Session数据
     * <ul>
     *     <li>获取到的不是HttpSession对象，不可直接操作该介质</li>
     *     <li>若根据sessionId没有从Redis获取到数据，则本方法直接返回null</li>
     * </ul>
     */
    public MapSession get(String sessionId){
        if(StringUtils.isBlank(sessionId)){
            throw new NullPointerException("sessionId is blank");
        }
        LogUtil.getLogger().debug("get session-->[{}]", sessionId);
        byte[] sessionData = jedisCluster.get(this.getByteKey(sessionId));
        if(null == sessionData){
            LogUtil.getLogger().debug("get session-->[{}]-->session data not found", sessionId);
            return null;
        }
        return SerializationUtils.deserialize(sessionData);
    }


    /**
     * 存储Session数据到Redis3.x里面
     * <p>实际相当于更新Session数据</p>
     */
    void save(MapSession session){
        if(null==session || null==session.getId()){
            LogUtil.getLogger().warn("session or sessionId is null");
            return;
        }
        LogUtil.getLogger().debug("save session-->[{}]", session.getId());
        byte[] key = this.getByteKey(session.getId());
        byte[] value = SerializationUtils.serialize(session);
        if(this.expireSeconds > 0){
            jedisCluster.setex(key, this.expireSeconds, value);
        }else{
            jedisCluster.set(key, value);
        }
    }
}