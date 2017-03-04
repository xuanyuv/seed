package com.jadyer.seed.open.util;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了发送HTTP请求的工具类
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 本工具类中的部分方法用到了HttpComponents-Client-4.2.1
 * @see 本工具类完整版见https://github.com/jadyer/JadyerEngine/blob/master/JadyerEngine-common/src/main/java/com/jadyer/engine/common/util/HttpUtil.java
 * @see -----------------------------------------------------------------------------------------------------------
 * @version v2.4
 * @history v2.4-->重命名GET和POST方法名,全局定义通信报文编码和连接读取超时时间,通信发生异常时修改为直接抛出RuntimeException
 * @history v2.3-->增加<code>sendPostRequestWithUpload()</code><code>sendPostRequestWithDownload()</code>方法,用于上传和下载文件
 * @history v2.2-->增加<code>sendPostRequestBySocket()</code>方法,用于处理请求参数非字符串而是Map的情景
 * @history v2.1-->增加<code>sendTCPRequest()</code>方法,用于发送TCP请求
 * @history v2.0-->HttpClientUtil更名为HttpUtil,同时增加<code>sendPostRequestByJava()</code>和<code>sendPostRequestBySocket()</code>
 * @history v1.7-->修正<code>sendPostRequest()</code>请求的CONTENT_TYPE头信息,并优化各方法参数及内部处理细节
 * @history v1.6-->整理GET和POST请求方法,使之更为适用
 * @history v1.5-->重组各方法,并补充自动获取HTTP响应文本编码的方式,移除<code>sendPostRequestByJava()</code>
 * @history v1.4-->所有POST方法中增加连接超时限制和读取超时限制
 * @history v1.3-->新增<code>java.net.HttpURLConnection</code>实现的<code>sendPostRequestByJava()</code>
 * @history v1.2-->新增<code>sendPostRequest()</code>方法,用于发送HTTP协议报文体为任意字符串的POST请求
 * @history v1.1-->新增<code>sendPostSSLRequest()</code>方法,用于发送HTTPS的POST请求
 * @history v1.0-->新建<code>sendGetRequest()</code>和<code>sendPostRequest()</code>方法
 * @update Sep 16, 2015 3:59:14 PM
 * @create Feb 1, 2012 3:02:27 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
@SuppressWarnings("deprecation")
public final class HttpUtil {
	private static final String DEFAULT_CHARSET = "UTF-8"; // 设置默认通信报文编码为UTF-8
	private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 2; // 设置默认连接超时为2s
	private static final int DEFAULT_SO_TIMEOUT = 1000 * 60; // 设置默认读取超时为60s

	private HttpUtil() {}


	/**
	 * 发送HTTP_POST请求
	 * @see 1)该方法允许自定义任何格式和内容的HTTP请求报文体
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 4)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 5)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html; charset=GBK]的charset值
	 * @see   若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @param reqURL  请求地址
	 * @param reqData 请求报文,无参数时传null即可,多个参数则应拼接为param11=value11&22=value22&33=value33的形式
	 * @return 远程主机响应正文
	 */
	public static String post(String reqURL, String reqData, String contentType) {
		LogUtil.getAppLogger().info("请求{}的报文为-->>[{}]", reqURL, reqData);
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {}
			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {}
			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}
			@Override
			public boolean verify(String arg0, SSLSession arg1) {return true;}
		};
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// 由于下面使用的是new
			// StringEntity(....),所以默认发出去的请求报文头中CONTENT_TYPE值为text/plain;
			// charset=ISO-8859-1
			// 这就有可能会导致服务端接收不到POST过去的参数,比如运行在Tomcat6.0.36中的Servlet,所以我们手工指定CONTENT_TYPE头消息
			if (StringUtils.isBlank(contentType)) {
				httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET);
			} else {
				httpPost.setHeader(HTTP.CONTENT_TYPE, contentType);
			}
			httpPost.setEntity(new StringEntity(null == reqData ? "" : reqData, DEFAULT_CHARSET));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			LogUtil.getAppLogger().info("请求{}得到应答<<--[{}]", reqURL, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}


	/**
	 * 发送HTTPS_POST请求
	 * @see 1)该方法亦可处理HTTP_POST请求
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内自动注册443作为HTTPS端口,即处理HTTPS请求时,默认请求对方443端口
	 * @see 4)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 5)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 6)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html; charset=GBK]的charset值
	 * @see   若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @param reqURL 请求地址
	 * @param params 请求参数,无参数时传null即可
	 * @return 远程主机响应正文
	 */
	public static String post(String reqURL, Map<String, String> params) {
		LogUtil.getAppLogger().info("请求{}的报文为-->>{}", reqURL, OpenUtil.buildStringFromMap(params));
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {}
			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {}
			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}
			@Override
			public boolean verify(String arg0, SSLSession arg1) {return true;}
		};
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// 由于下面使用的是new
			// UrlEncodedFormEntity(....),所以这里不需要手工指定CONTENT_TYPE为application/x-www-form-urlencoded
			// 因为在查看了HttpClient的源码后发现,UrlEncodedFormEntity所采用的默认CONTENT_TYPE就是application/x-www-form-urlencoded
			// httpPost.setHeader(HTTP.CONTENT_TYPE,
			// "application/x-www-form-urlencoded; charset=" + encodeCharset);
			if (null != params) {
				List<NameValuePair> formParams = new ArrayList<>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
			}
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			LogUtil.getAppLogger().info("请求{}得到应答<<--[{}]", reqURL, respData);
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}


	/**
	 * 发送上传文件的HTTP_POST请求
	 * @see 1)该方法用来上传文件
	 * @see 2)该方法会自动关闭连接,释放资源
	 * @see 3)方法内设置了连接和读取超时(时间由本工具类全局变量限定),超时或发生其它异常将抛出RuntimeException
	 * @see 4)请求参数含中文等特殊字符时,可直接传入本方法,方法内部会使用本工具类设置的全局DEFAULT_CHARSET对其转码
	 * @see 5)该方法在解码响应报文时所采用的编码,取自响应消息头中的[Content-Type:text/html; charset=GBK]的charset值
	 * @see 若响应消息头中未指定Content-Type属性,则会使用HttpClient内部默认的ISO-8859-1
	 * @param reqURL       请求地址
	 * @param filename     待上传的文件名
	 * @param is           待上传的文件流
	 * @param fileBodyName 远程主机接收文件域的名字,相当于前台表单中的文件域名称<input type="file" name="fileBodyName">
	 * @param params       请求参数,无参数时传null即可
	 * @return 远程主机响应正文
	 */
	public static String postWithUpload(String reqURL, String filename, InputStream is, String fileBodyName, Map<String, String> params) {
		LogUtil.getAppLogger().info("请求"+reqURL+"的报文为-->>"+OpenUtil.buildStringFromMap(params));
		String respData = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
		// 创建TrustManager(),用于解决javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			@Override
			public X509Certificate[] getAcceptedIssuers() {return null;}
		};
		// 创建HostnameVerifier,用于解决javax.net.ssl.SSLException: hostname in certificate didn't match: <123.125.97.66> != <123.125.97.241>
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			@Override
			public void verify(String host, SSLSocket ssl) throws IOException {}
			@Override
			public void verify(String host, X509Certificate cert) throws SSLException {}
			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}
			@Override
			public boolean verify(String arg0, SSLSession arg1) {return true;}
		};
		File tmpFile = new File(filename);
		try {
			// TLS1.0是SSL3.0的升级版(网上已有人发现SSL3.0的致命BUG了),它们使用的是相同的SSLContext
			SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
			// 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
			sslContext.init(null, new TrustManager[] { trustManager }, null);
			// 创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
			// 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
			HttpPost httpPost = new HttpPost(reqURL);
			// Charset用来保证文件域中文名不乱码,非文件域中文不乱码的话还要像下面StringBody中再设置一次Charset
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(DEFAULT_CHARSET));
			FileUtils.copyInputStreamToFile(is, tmpFile);
			reqEntity.addPart(fileBodyName, new FileBody(tmpFile));
			if (null != params) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName(DEFAULT_CHARSET)));
				}
			}
			httpPost.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				respData = EntityUtils.toString(entity, DEFAULT_CHARSET);
			}
			LogUtil.getAppLogger().info("请求"+reqURL+"得到应答<<--["+respData+"]");
			return respData;
		} catch (ConnectTimeoutException cte) {
			throw new RuntimeException("请求通信[" + reqURL + "]时连接超时", cte);
		} catch (SocketTimeoutException ste) {
			throw new RuntimeException("请求通信[" + reqURL + "]时读取超时", ste);
		} catch (Exception e) {
			throw new RuntimeException("请求通信[" + reqURL + "]时遇到异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
			//noinspection ResultOfMethodCallIgnored
			tmpFile.delete();
		}
	}
}