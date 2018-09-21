package com.jadyer.seed.boot.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 参考https://github.com/spring-projects/spring-session/blob/master/spring-session/src/main/java/org/springframework/session/web/http/ExpiringSessionHttpSession.java
 * Created by 玄玉<https://jadyer.cn/> on 2016/6/19 20:47.
 */
class MapSessionWrapper implements HttpSession {
    private MapSession session;
    private ServletContext servletContext;
    private boolean invalidated;
    private boolean old;

    MapSessionWrapper(MapSession session, ServletContext servletContext){
        this.session = session;
        this.servletContext = servletContext;
    }


    public MapSession getSession() {
        return this.session;
    }


    public void setSession(MapSession session) {
        this.session = session;
    }


    @Override
    public long getCreationTime() {
        this.checkState();
        return this.session.getCreationTime();
    }


    @Override
    public String getId() {
        return this.session.getId();
    }


    @Override
    public long getLastAccessedTime() {
        this.checkState();
        return this.session.getLastAccessedTime();
    }


    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }


    @Override
    public void setMaxInactiveInterval(int interval) {
        this.session.setMaxInactiveIntervalInSeconds(interval);
    }


    @Override
    public int getMaxInactiveInterval() {
        return this.session.getMaxInactiveIntervalInSeconds();
    }


    @Override
    @SuppressWarnings("deprecation")
    public HttpSessionContext getSessionContext() {
        return NOOP_SESSION_CONTEXT;
    }


    @Override
    public Object getAttribute(String name) {
        this.checkState();
        return this.session.getAttribute(name);
    }


    @Override
    public Object getValue(String name) {
        return this.getAttribute(name);
    }


    @Override
    public Enumeration<String> getAttributeNames() {
        this.checkState();
        return Collections.enumeration(this.session.getAttributeNames());
    }


    @Override
    public String[] getValueNames() {
        this.checkState();
        Set<String> attrs = this.session.getAttributeNames();
        //return attrs.toArray(new String[0]);
        return attrs.toArray(new String[attrs.size()]);
    }


    @Override
    public void setAttribute(String name, Object value) {
        this.checkState();
        this.session.setAttribute(name, value);
    }


    @Override
    public void putValue(String name, Object value) {
        this.setAttribute(name, value);
    }


    @Override
    public void removeAttribute(String name) {
        this.checkState();
        this.session.removeAttribute(name);
    }


    @Override
    public void removeValue(String name) {
        this.removeAttribute(name);
    }


    @Override
    public void invalidate() {
        this.checkState();
        this.invalidated = true;
    }


    @Override
    public boolean isNew() {
        this.checkState();
        return !this.old;
    }


    public void setNew(boolean isNew){
        this.old = !isNew;
    }


    private void checkState(){
        //if(this.session.isExpired()){
        //    throw new IllegalStateException("The HttpSession has already be invalidated.");
        //}
        //不用上面的判断，是有一种考虑：当应用容器发现HttpSession要过期了，会调用下面的方法清除HttpSession
        //会调用com.jadyer.seed.boot.session.SessionRedisFilter.SessionRepositoryRequestWrapper.MapSessionRedisWrapper.invalidate()
        //此时super.invalidate()先执行，当执行到this.session.isExpired()时，会发现HttpSession正好已经过期了，就会抛异常出去
        //导致MapSessionRedisWrapper.invalidate()无法继续执行，造成无法移除绑定到请求上的HttpSession以及删除Redis3.x中的session数据
        if(this.invalidated){
            throw new IllegalStateException("The HttpSession has already be invalidated.");
        }
    }


    @SuppressWarnings("deprecation")
    private static final HttpSessionContext NOOP_SESSION_CONTEXT = new HttpSessionContext(){
        @Override
        public HttpSession getSession(String sessionId) {
            return null;
        }
        @Override
        public Enumeration<String> getIds() {
            return EMPTY_ENUMERATION;
        }
    };


    private static final Enumeration<String> EMPTY_ENUMERATION = new Enumeration<String>(){
        @Override
        public boolean hasMoreElements() {
            return false;
        }
        @Override
        public String nextElement() {
            throw new NoSuchElementException("a");
        }
    };
}
