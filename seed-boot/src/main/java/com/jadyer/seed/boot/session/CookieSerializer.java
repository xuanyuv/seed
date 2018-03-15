package com.jadyer.seed.boot.session;

import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 参考https://github.com/spring-projects/spring-session/blob/master/spring-session/src/main/java/org/springframework/session/web/http/DefaultCookieSerializer.java
 * Created by 玄玉<http://jadyer.cn/> on 2016/6/19 20:22.
 */
class CookieSerializer {
    private Boolean useSecureCookie;
    private boolean useHttpOnlyCookie = isServlet3();
    private String cookiePath;
    @SuppressWarnings("FieldCanBeLocal")
    private int cookieMaxAge = -1;
    private String domainName;
    private Pattern domainNamePattern;

    String getCookie(HttpServletRequest request, String name){
        Assert.notNull(request, "HttpServletRequest must not be null");
        Cookie[] cookies = request.getCookies();
        if(null != cookies){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(this.isSecureCookie(request));
        cookie.setPath(this.getCookiePath(request));
        String domainName = this.getDomainName(request);
        if(null != domainName){
            cookie.setDomain(domainName);
        }
        if(this.useHttpOnlyCookie){
            cookie.setHttpOnly(true);
        }
        if("".equals(value)){
            cookie.setMaxAge(0);
        }else{
            cookie.setMaxAge(this.cookieMaxAge);
        }
        response.addCookie(cookie);
    }


    private String getDomainName(HttpServletRequest request){
        if(null != domainName){
            return domainName;
        }
        if(null != domainNamePattern){
            Matcher matcher = domainNamePattern.matcher(request.getServerName());
            if(matcher.matches()){
                return matcher.group(1);
            }
        }
        return null;
    }


    private String getCookiePath(HttpServletRequest request){
        if(null == cookiePath){
            return request.getContextPath() + "/";
        }
        return cookiePath;
    }


    private boolean isSecureCookie(HttpServletRequest request){
        if(null == useSecureCookie){
            return request.isSecure();
        }
        return useSecureCookie;
    }


    /**
     * Returns true if the Servlet 3 APIs are detected.
     * @return whether the Servlet 3 APIs are detected
     */
    private boolean isServlet3(){
        try{
            ServletRequest.class.getMethod("startAsync");
            return true;
        }catch(NoSuchMethodException e){
            // do nothing.
        }
        return false;
    }
}