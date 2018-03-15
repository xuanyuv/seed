package com.jadyer.seed.open.boot;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.JedisCluster;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://www.redis.cn/commands.html
 * http://www.runoob.com/redis/redis-commands.html
 */
public class RedisFilter extends OncePerRequestFilter {
    private static final int EXPIRE_TIME_SECONDS = 60 * 12;          //12分钟过期
    private static final String REDIS_DATA_KEY = "data-key";         //请求应答内容的RedisKey
    private static final String REDIS_DATA_CONTENT = "data-content"; //请求应答内容
    private static final String RESP_CONTENT_TYPE = "application/json; charset=UTF-8";
    private String filterURL;
    private JedisCluster jedisCluster;
    private List<String> filterMethodList = new ArrayList<>();

    /**
     * @param _filterURL        指定该Filter只拦截哪种请求URL，空表示都不拦截
     * @param _filterMethodList 指定该Filter只拦截的方法列表，空表示都不拦截
     * @param jedisCluster      redis集群对象
     */
    RedisFilter(String filterURL, List<String> filterMethodList, JedisCluster jedisCluster){
        this.filterURL = filterURL;
        this.jedisCluster = jedisCluster;
        this.filterMethodList.addAll(filterMethodList);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return StringUtils.isNotBlank(filterURL) || !request.getServletPath().startsWith(filterURL) || filterMethodList.isEmpty() || !filterMethodList.contains(request.getParameter("method"));
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //计算请求唯一性的标记
        String redisKey = null;
        if(Constants.OPEN_VERSION_21.equals(request.getParameter("version")) || Constants.OPEN_VERSION_22.equals(request.getParameter("version"))){
            redisKey = "open-" + request.getParameter("appid") + "-" + DigestUtils.md5Hex(request.getParameter("data"));
        }
        Long initResult = jedisCluster.hsetnx(redisKey, REDIS_DATA_KEY, REDIS_DATA_CONTENT);
        //返回1表示首次请求，即此时往redisKey指定的哈希集中成功添加了字段REDIS_DATA_KEY及其值
        //此时会缓存请求的应答内容
        if(initResult == 1){
            ResponseContentWrapper wrapperResponse = new ResponseContentWrapper(response);
            filterChain.doFilter(request, wrapperResponse);
            String content = wrapperResponse.getContent();
            Map<String, String> hash = new HashMap<>();
            hash.put(REDIS_DATA_KEY, content);
            jedisCluster.hmset(redisKey, hash);
            jedisCluster.expire(redisKey, EXPIRE_TIME_SECONDS);
            response.getOutputStream().write(content.getBytes(Constants.OPEN_CHARSET_UTF8));
            return;
        }
        //返回0表示非首次请求，此时会计算应答哪些内容
        Map<String, String> values = jedisCluster.hgetAll(redisKey);
        if(null!=values && values.containsKey(REDIS_DATA_KEY) && !REDIS_DATA_CONTENT.equals(values.get(REDIS_DATA_KEY))){
            response.setHeader("Content-Type", RESP_CONTENT_TYPE);
            response.getWriter().write(values.get(REDIS_DATA_KEY));
        }else{
            response.setHeader("Content-Type", RESP_CONTENT_TYPE);
            response.getOutputStream().write(("{\"code\":\"" + CodeEnum.SYSTEM_BUSY.getCode() + "\", \"msg\":\"处理中，请勿重复提交\"}").getBytes(Constants.OPEN_CHARSET_UTF8));
        }
    }


    /**
     * 可手工设置HttpServletResponse出参的Wrapper
     */
    private class ResponseContentWrapper extends HttpServletResponseWrapper {
        private ResponsePrintWriter writer;
        private OutputStreamWrapper outputWrapper;
        private ByteArrayOutputStream output;
        ResponseContentWrapper(HttpServletResponse httpServletResponse) {
            super(httpServletResponse);
            output = new ByteArrayOutputStream();
            outputWrapper = new OutputStreamWrapper(output);
            writer = new ResponsePrintWriter(output);
        }
        @Override
        public void finalize() throws Throwable {
            super.finalize();
            output.close();
            writer.close();
        }
        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputWrapper;
        }
        String getContent() {
            try {
                writer.flush();
                return writer.getByteArrayOutputStream().toString(Constants.OPEN_CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                return "UnsupportedEncoding";
            }
        }
        public void close() throws IOException {
            writer.close();
        }
        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }
        private class ResponsePrintWriter extends PrintWriter {
            ByteArrayOutputStream output;
            ResponsePrintWriter(ByteArrayOutputStream output) {
                super(output);
                this.output = output;
            }
            ByteArrayOutputStream getByteArrayOutputStream() {
                return output;
            }
        }
        private class OutputStreamWrapper extends ServletOutputStream {
            ByteArrayOutputStream output;
            OutputStreamWrapper(ByteArrayOutputStream output) {
                this.output = output;
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setWriteListener(WriteListener listener) {
                throw new UnsupportedOperationException("UnsupportedMethod setWriteListener.");
            }
            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }
        }
    }
}