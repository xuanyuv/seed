package com.jadyer.seed.boot;

import com.jadyer.seed.comm.util.JadyerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/28 11:28.
 */
@Configuration
public class XSSConfiguration {
    @Bean
    public Filter xssFilter(){
        return new XSSFilter();
    }


    private class XSSFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if(StringUtils.endsWithAny(request.getServletPath(), "js", "css", "ico", "png", "jpg", "jpeg")){
                filterChain.doFilter(request, response);
            }else{
                filterChain.doFilter(new XSSRequestParameterWrapper(request), response);
            }
        }
    }


    /**
     * 这是基于seed-open.OpenFilter.RequestParameterWrapper.java做的修改（修改了取值时的实现）
     */
    private class XSSRequestParameterWrapper extends HttpServletRequestWrapper {
        private Map<String, String[]> paramMap = new HashMap<>();
        private Map<String, String> headerMap = new HashMap<>();
        XSSRequestParameterWrapper(HttpServletRequest request) {
            super(request);
            this.paramMap.putAll(request.getParameterMap());
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement();
                headerMap.put(headerName, request.getHeader(headerName));
            }
        }
        @Override
        public String getParameter(String name) {
            String[] values = this.paramMap.get(name);
            if(null==values || values.length==0){
                return "";
            }
            return JadyerUtil.escapeXSS(values[0]);
        }
        @Override
        public String[] getParameterValues(String name) {
            String[] values = this.paramMap.get(name);
            if(null==values || values.length==0){
                return new String[0];
            }
            String[] escapeValues = new String[values.length];
            for(int i=0; i<values.length; i++){
                escapeValues[i] = JadyerUtil.escapeXSS(values[i]);
            }
            return escapeValues;
        }
        @Override
        public Enumeration<String> getParameterNames() {
            return new Vector<>(this.paramMap.keySet()).elements();
        }
        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> escapeParamMap = new HashMap<>();
            for(Map.Entry<String,String[]> entry : this.paramMap.entrySet()){
                escapeParamMap.put(entry.getKey(), this.getParameterValues(entry.getKey()));
            }
            return escapeParamMap;
        }
        void addParameter(String name, Object value){
            if(null != value){
                if(value instanceof String[]){
                    this.paramMap.put(name, (String[])value);
                }else if(value instanceof String){
                    this.paramMap.put(name, new String[]{(String)value});
                }else{
                    this.paramMap.put(name, new String[]{String.valueOf(value)});
                }
            }
        }
        void addAllParameters(Map<String, Object> allParams){
            for(Map.Entry<String,Object> entry : allParams.entrySet()){
                this.addParameter(entry.getKey(), entry.getValue());
            }
        }
        @Override
        public String getHeader(String name) {
            String value = this.headerMap.get(name);
            if(null == value){
                return "";
            }
            return JadyerUtil.escapeXSS(value);
        }
        @Override
        public Enumeration<String> getHeaders(String name) {
            String value = this.headerMap.get(name);
            if(StringUtils.isEmpty(value)){
                return new Vector<String>().elements();
            }
            Vector<String> values = new Vector<>();
            values.add(this.getHeader(name));
            return values.elements();
        }
        @Override
        public Enumeration<String> getHeaderNames() {
            return new Vector<>(this.headerMap.keySet()).elements();
        }
        void addHeader(String name, String value){
            if(StringUtils.isNotBlank(value)){
                this.headerMap.put(name, value);
            }
        }
        void addAllHeaders(Map<String, String> allHeaders){
            for(Map.Entry<String,String> entry : allHeaders.entrySet()){
                this.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }
}