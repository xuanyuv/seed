package com.jadyer.seed.boot.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 参考https://github.com/spring-projects/spring-session/blob/master/spring-session/src/main/java/org/springframework/session/MapSession.java
 * Created by 玄玉<https://jadyer.cn/> on 2016/6/19 18:36.
 */
class MapSession implements Serializable {
    private static final long serialVersionUID = -8704705569479113790L;

    /**
     * Default {@link #setMaxInactiveIntervalInSeconds(int)} (30 minutes).
     */
    static final int DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS = 1800;

    private String id;
    private Map<String, Object> sessionAttrs = new HashMap<>();
    private long creationTime = System.currentTimeMillis();
    private long lastAccessedTime = creationTime;

    /**
     * Defaults to 30 minutes.
     */
    private int maxInactiveInterval = DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;

    /**
     * Creates a new instance with a secure randomly generated identifier.
     */
    MapSession(){
        this(UUID.randomUUID().toString());
    }


    /**
     * Creates a new instance with the specified id. This is preferred to the default
     * constructor when the id is known to prevent unnecessary consumption on entropy
     * which can be slow.
     * @param id the identifier to use
     */
    private MapSession(String id){
        this.id = id;
    }


    ///**
    // * Creates a new instance from the provided {@link MapSession}.
    // * @param session the {@link MapSession} to initialize this {@link MapSession} with. Cannot be null.
    // */
    //public MapSession(MapSession session){
    //    if(null == session){
    //        throw new IllegalArgumentException("session cannot be null");
    //    }
    //    this.id = session.getId();
    //    this.sessionAttrs = new HashMap<>(session.getAttributeNames().size());
    //    for(String attrName : session.getAttributeNames()){
    //        Object attrValue = session.getAttribute(attrName);
    //        this.sessionAttrs.put(attrName, attrValue);
    //    }
    //    this.lastAccessedTime = session.getLastAccessedTime();
    //    this.creationTime = session.getCreationTime();
    //    this.maxInactiveInterval = session.getMaxInactiveIntervalInSeconds();
    //}


    void setLastAccessedTime(long lastAccessedTime){
        this.lastAccessedTime = lastAccessedTime;
    }


    long getCreationTime(){
        return this.creationTime;
    }


    String getId() {
        return id;
    }


    long getLastAccessedTime(){
        return this.lastAccessedTime;
    }


    void setMaxInactiveIntervalInSeconds(int interval){
        this.maxInactiveInterval = interval;
    }


    int getMaxInactiveIntervalInSeconds(){
        return this.maxInactiveInterval;
    }


    public boolean isExpired(){
        return isExpired(System.currentTimeMillis());
    }


    private boolean isExpired(long now) {
        return this.maxInactiveInterval >= 0 && now - TimeUnit.SECONDS.toMillis(this.maxInactiveInterval) >= this.lastAccessedTime;
    }


    Object getAttribute(String attributeName){
        return sessionAttrs.get(attributeName);
    }


    Set<String> getAttributeNames(){
        return sessionAttrs.keySet();
    }


    void setAttribute(String attributeName, Object attributeValue){
        if(null == attributeValue){
            this.removeAttribute(attributeName);
        }else{
            sessionAttrs.put(attributeName, attributeValue);
        }
    }


    /**
     * @return 本次移除的属性值
     */
    Object removeAttribute(String attributeName){
        return sessionAttrs.remove(attributeName);
    }


    ///**
    // * Sets the time that this {@link MapSession} was created in milliseconds since midnight
    // * of 1/1/1970 GMT. The default is when the {@link MapSession} was instantiated.
    // * @param creationTime the time that this {@link MapSession} was created in milliseconds since midnight of 1/1/1970 GMT.
    // */
    //public void setCreationTime(long creationTime){
    //    this.creationTime = creationTime;
    //}
    //
    //
    ///**
    // * Sets the identifier for this {@link MapSession}. The id should be a secure random
    // * generated value to prevent malicious users from guessing this value. The default is
    // * a secure random generated identifier.
    // * @param id the identifier for this session.
    // */
    //public void setId(String id){
    //    this.id = id;
    //}


    @Override
    public boolean equals(Object obj) {
        return obj instanceof MapSession && this.id.equals(((MapSession)obj).getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}