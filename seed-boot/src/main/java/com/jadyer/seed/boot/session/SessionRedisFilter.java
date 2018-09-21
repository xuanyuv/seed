package com.jadyer.seed.boot.session;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 参考http://www.infoq.com/cn/articles/Next-Generation-Session-Management-with-Spring-Session
 * 参考https://github.com/spring-projects/spring-session/blob/master/spring-session/src/main/java/org/springframework/session/web/http/SessionRepositoryFilter.java
 * Created by 玄玉<https://jadyer.cn/> on 2016/6/19 18:21.
 */
class SessionRedisFilter extends OncePerRequestFilter {
    private static final String DEFAULT_SESSION_ALIAS_PARAM_NAME = "_s";
    /**
     * SessionID储存到Cookie里面时的key完整名称
     */
    private String sessionKey = DEFAULT_SESSION_ALIAS_PARAM_NAME;
    private final SessionRedisRepository sessionRedisRepository;
    private CookieSerializer cookieSerializer = new CookieSerializer();

    SessionRedisFilter(SessionRedisRepository sessionRedisRepository, String sessionKey){
        this.sessionRedisRepository = sessionRedisRepository;
        this.sessionKey = sessionKey;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SessionRepositoryRequestWrapper requestWrapper = new SessionRepositoryRequestWrapper(request, response, this.getServletContext());
        try{
            filterChain.doFilter(requestWrapper, response);
        }finally{
            requestWrapper.commitSession();
        }

    }


    private final class SessionRepositoryRequestWrapper extends HttpServletRequestWrapper {
        private final String CURRENT_SESSION_ATTR = HttpServletRequestWrapper.class.getName();
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final ServletContext servletContext;
        SessionRepositoryRequestWrapper(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext){
            super(request);
            this.request = request;
            this.response = response;
            this.servletContext = servletContext;
        }
        /**
         * Uses the HttpSessionStrategy to write the session id tot he response and persist the Session.
         */
        private void commitSession(){
            //从HttpServletRequest中获取HttpSession对象，如果取到了，那么就更新到Redis3.x
            MapSessionRedisWrapper wrappedSession = this.getCurrentSession();
            if(null != wrappedSession){
                MapSession session = wrappedSession.getSession();
                sessionRedisRepository.save(session);
                //注释的这段，本意是在当前请求绑定的新会话与Cookie记录的原会话对象不同时，就更新Cookie的为新会话对象
                //但实际上下面的this.getSession()在创建新会话时已经更新了Cookie记录的SessionID，所以这里就没必要了
                //if(!session.getId().equals(this.getRequestedSessionId())){
                //    cookieSerializer.setCookie(request, response, sessionKey, session.getId());
                //}
            }
        }
        /**
         * 从HttpServletRequest中获取HttpSession对象
         */
        private MapSessionRedisWrapper getCurrentSession(){
            return (MapSessionRedisWrapper)this.getAttribute(this.CURRENT_SESSION_ATTR);
        }
        /**
         * 将HttpSession对象放到当前HttpServletRequest属性中
         */
        private void setCurrentSession(MapSessionRedisWrapper currentSession){
            if(null == currentSession){
                this.removeAttribute(this.CURRENT_SESSION_ATTR);
            }else{
                this.setAttribute(this.CURRENT_SESSION_ATTR, currentSession);
            }
        }
        /**
         * 根据sessionId从Redis3.x中获取对应的Session数据
         * <p>注意：不是HttpSession对象，不可直接操作该Session数据</p>
         */
        private MapSession getSession(String sessionId){
            MapSession session = sessionRedisRepository.get(sessionId);
            if(null == session){
                return null;
            }
            session.setLastAccessedTime(System.currentTimeMillis());
            return session;
        }
        @Override
        public ServletContext getServletContext() {
            if(null != this.servletContext){
                return this.servletContext;
            }
            // Servlet 3.0+
            return super.getServletContext();
        }
        @Override
        public String changeSessionId() {
            //先获取当前HttpServletRequest绑定的HttpSession
            HttpSession session = this.getSession(false);
            if(null == session){
                throw new IllegalStateException("Cannot change session ID. There is no session associated with this request.");
            }
            //eagerly get session attributes in case implementation lazily loads them
            //得到现有HttpSession中存储的所有属性
            Map<String, Object> attrs = new HashMap<>();
            Enumeration<String> iAttrNames = session.getAttributeNames();
            while(iAttrNames.hasMoreElements()){
                String attrName = iAttrNames.nextElement();
                Object value = session.getAttribute(attrName);
                attrs.put(attrName, value);
            }
            //删除储存在Redis3.x中的原Session数据
            sessionRedisRepository.delete(session.getId());
            //取到当前HttpServletRequest中的HttpSession对象
            //经过第一行的this.getSession(false)已经保证能够从HttpServletRequest得到已绑定的HttpSession
            MapSessionRedisWrapper original = this.getCurrentSession();
            //清掉当前HttpServletRequest中的HttpSession对象
            this.setCurrentSession(null);
            //创建新的HttpSession对象，同时绑定到当前HttpServletRequest，并储存到Redis3.x，以及更新Cookie中的sessionId
            MapSessionRedisWrapper newSession = this.getSession();
            /**
             * 将原HttpSession对象指向到新创建的Session数据上（这里有个疑问：）
             * // TODO 这里有个疑问：original对象应该没啥用啊，此时绑定到请求上的已经是新的session了啊，有时间测试一下
             */
            original.setSession(newSession.getSession());
            //让新的HttpSession对象和原HttpSession对象拥有相同的有效期
            newSession.setMaxInactiveInterval(session.getMaxInactiveInterval());
            //让新的HttpSession对象和原HttpSession对象拥有相同的属性
            //由于HttpSession对象的实现是一个Map，故此时绑定到HttpServletRequest上的HttpSession对象中已经含有原会话的所有数据
            for(Map.Entry<String, Object> attr : attrs.entrySet()){
                String attrName = attr.getKey();
                Object attrValue = attr.getValue();
                newSession.setAttribute(attrName, attrValue);
            }
            //返回新的SessionID
            //此时Redis3.x中的session数据还只是一个不含有任何属性空架子
            //之所以这里没有立即更新session数据到Redis，是因为请求结束之前会自动调用commitSession()，它会把所有属性更新到Redis
            return newSession.getId();
        }
        @Override
        public MapSessionRedisWrapper getSession(boolean create) {
            //先从HttpServletRequest中获取HttpSession对象，取到就直接返回
            MapSessionRedisWrapper currentSession = this.getCurrentSession();
            if(null != currentSession){
                return currentSession;
            }
            //取不到HttpSession对象的话，就再从Cookie中取一下sessionId
            String requestedSessionId = this.getRequestedSessionId();
            if(null != requestedSessionId){
                //再根据sessionId，从Redis3.x里面拿到session数据，最后构建成HttpSession，并将之绑定到HttpServletRequest后返回
                MapSession session = this.getSession(requestedSessionId);
                if(null != session){
                    currentSession = new MapSessionRedisWrapper(session, this.getServletContext());
                    currentSession.setNew(false);
                    this.setCurrentSession(currentSession);
                    return currentSession;
                }
            }
            //经过以上步骤若未得到HttpSession对象，那就看看是否需要强制创建一个HttpSession
            if(!create){
                return null;
            }
            //创建一个新的空属性session数据并放到Redis3.x，然后将本次创建的session数据构建成HttpSession对象
            MapSession session = sessionRedisRepository.create();
            session.setLastAccessedTime(System.currentTimeMillis());
            currentSession = new MapSessionRedisWrapper(session, this.getServletContext());
            //再把HttpSession绑定到当前HttpServletRequest，同时把sessionId储存到Cookie，最后返回HttpSession
            this.setCurrentSession(currentSession);
            cookieSerializer.setCookie(request, response, sessionKey, session.getId());
            return currentSession;
        }
        /**
         * 获取当前请求绑定的会话，如果没有会话，就新建一个
         */
        @Override
        public MapSessionRedisWrapper getSession() {
            return this.getSession(true);
        }
        /**
         * 从Cookie中获取SessionID
         */
        @Override
        public String getRequestedSessionId() {
            return cookieSerializer.getCookie(request, sessionKey);
        }
        /**
         * Allows creating an HttpSession from a Session instance.
         * 该类用于实际绑定到HttpServletRequest里面HttpSession
         * 它重写了invalidate()可以保证过期的session数据能够从Redis3.x中删除
         * 不止是过期的HttpSession，当应用容器停止时，也会到invalidate()方法中处理不再使用的session数据
         */
        private final class MapSessionRedisWrapper extends MapSessionWrapper{
            MapSessionRedisWrapper(MapSession session, ServletContext servletContext) {
                super(session, servletContext);
            }
            @Override
            public void invalidate() {
                super.invalidate();
                setCurrentSession(null);
                sessionRedisRepository.delete(this.getId());
            }
        }
    }
}