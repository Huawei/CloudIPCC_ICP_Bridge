package com.huawei.bridge.common.http;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.huawei.bridge.bean.AgentAuthInfoBean;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.util.JsonUtils;
import com.huawei.bridge.common.util.LogUtils;




public class Request {
    /**
     * log
     */
    private static final Logger LOG = LoggerFactory.getLogger(Request.class);
    
	/**
	 * Max connections of connection pool,unit:millisecond
	 */
	private static final int MAXCONNECTION = 500;
	
	/**
	 * Connections of every route,unit:millisecond
	 */
	private static final int MAXPERROUTE = 500;
	
	/**
	 * Max request time of getting a connection from connection pool,unit:millisecond
	 */
	private static final int REQUESTTIMEOUT = 2000;
	
	/**
	 * Max time of a request,unit:millisecond
	 */
	private static final int CONNECTIMEOUT = 2000;
	
	/**
	 * Max time of waiting for response message,unit:millisecond
	 */
	private static final int SOCKETIMEOUT = 12000;

	
	private static final String CONNECT_ERROR = "{\"message\":\"request to server failed\", \"retcode\":\"NETWORKERROR\"}";
	
	private static PoolingHttpClientConnectionManager connManager = null;
	
	private static CloseableHttpClient client = null;
	
	public static void init()
	{
		SSLContext sslContext;
		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				
				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();
		
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext
					, new X509HostnameVerifier(){
				public boolean verify(String arg0, SSLSession arg1) {
			        return true;
			    }
			    public void verify(String host, SSLSocket ssl)
			            throws IOException {
			    }
			    public void verify(String host, X509Certificate cert)
			            throws SSLException {
			    }
			    public void verify(String host, String[] cns,
			            String[] subjectAlts) throws SSLException {
			    }
			});
			
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", sslsf)
					.build();
			
			connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			connManager.setMaxTotal(MAXCONNECTION);
			connManager.setDefaultMaxPerRoute(MAXPERROUTE);
			
		} 
		catch (RuntimeException e) 
		{
            throw e;
		}
		catch (Exception e)
        {
            LOG.error("init connection pool failed \r\n {}: ", LogUtils.encodeForLog(e.getMessage()));
            return;
        }
		
		client = getConnection();
	}
	

    private static CloseableHttpClient getConnection()
    {
    	RequestConfig restConfig = RequestConfig.custom().setConnectionRequestTimeout(REQUESTTIMEOUT)
    			.setConnectTimeout(CONNECTIMEOUT)
    			.setSocketTimeout(SOCKETIMEOUT).build();
    	HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler()
        {
            public boolean retryRequest(IOException exception, int executionCount,
                    HttpContext context)
            {
                if (executionCount >= 3)
                {
                   return false; 
                }
                if (exception instanceof NoHttpResponseException) 
                {
                    return true;  
                } 
                if (exception instanceof InterruptedIOException) 
                {
                    return false;
                }
                if (exception instanceof SSLHandshakeException) 
                {
                    return false;  
                }  
                if (exception instanceof UnknownHostException) 
                {
                    return false;  
                }  
                if (exception instanceof ConnectTimeoutException) 
                {
                    return false;  
                }  
                if (exception instanceof SSLException) 
                {
                    return false;  
                }
                
                HttpClientContext clientContext = HttpClientContext.adapt(context);  
                HttpRequest request = clientContext.getRequest();  
                if (!(request instanceof HttpEntityEnclosingRequest)) 
                {  
                    return true;  
                }  
                return false;  
            }
        };
    	CloseableHttpClient httpClient = HttpClients.custom()
    			.setConnectionManager(connManager).setDefaultRequestConfig(restConfig).setRetryHandler(retryHandler).build();
    	return httpClient;
    }
    
	
	
    /**
     * Send http's GET request
     * @param workNo: : the agent id
     * @param url:the address of the request
     * @return
     */
    public static Map<String, Object> get(String workNo, String url)
    {
    	CloseableHttpResponse response = null;
    	HttpGet get = null;
    	Map<String, Object> result  = new HashMap<String, Object>();
    	try 
		{
    		url = Normalizer.normalize(url, Form.NFKC);
			get = new HttpGet(url);
			
			setHeaders(workNo, get);
            
			get.setHeader("Content-Type", "application/json;charset=UTF-8");
			response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	    	{
			    HttpEntity entity = response.getEntity();
                if (null != entity)
                {
                    String entityContent = EntityUtils.toString(entity,"UTF-8");
                    result = JsonUtils.jsonToMap(entityContent);                   
                }
                
                try 
                {
                    EntityUtils.consume(entity);
                } 
                catch (IOException e) 
                {
                    LOG.error("release entity failed \r\n {}", LogUtils.encodeForLog(e.getMessage()));
                }
	    	}
		}
    	catch (UnsupportedEncodingException e) 
        {
            result = returnConnectError(e);
        } catch (ClientProtocolException e)
        {
            result = returnConnectError(e);
        }
        catch (IOException e) 
        {
            result =  returnConnectError(e);
        }
    	finally
		{
    		if (null != response)
    		{
				try
				{
					EntityUtils.consume(response.getEntity());
					response.close();
				} 
				catch (IOException e) 
				{
					LOG.error("release response failed \r\n {}", LogUtils.encodeForLog(e.getMessage()));
				}
			}
		}
		
    	return result;
    }

    
    /**
     * Send http's POST request
     * @param workNo: the agent id
     * @param url:the address of the request
     * @param entityParams:the paramters of entity
     * @return
     */
    public static Map<String, Object> post(String workNo, String url, Object entityParams)
    {
    	Map<String, Object> result = new HashMap<String, Object>();
    	HttpPost post = null;
    	CloseableHttpResponse response = null;
    	try 
    	{
    		url = Normalizer.normalize(url, Form.NFKC);
    		post = new HttpPost(url);
	    	if (null != entityParams)
	    	{	    		
				String jsonString = JsonUtils.beanToJson(entityParams);
				HttpEntity entity = new StringEntity(jsonString);
				post.setEntity(entity);
	    	}
	    	setHeaders(workNo, post);
	    	post.setHeader("Content-Type", "application/json;charset=UTF-8");
	    	response = client.execute(post);
	    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	    	{
	    	    HttpEntity entity = response.getEntity();
                if (null != entity)
                {
                    String entityContent = EntityUtils.toString(entity,"UTF-8");
                    result = JsonUtils.jsonToMap(entityContent);
                }
              
                try 
                {
                    EntityUtils.consume(entity);
                } 
                catch (IOException e) 
                {
                    LOG.error("release entity failed \r\n {}", LogUtils.encodeForLog(e.getMessage()));
                }
	    	}
    	}
    	catch (UnsupportedEncodingException e) 
    	{
            result = returnConnectError(e);
        } catch (ClientProtocolException e)
    	{
            result = returnConnectError(e);
        }
    	catch (IOException e) 
    	{
    	    result =  returnConnectError(e);
        }
    	finally
    	{
    		if (null != response)
    		{
				try
				{
					EntityUtils.consume(response.getEntity());
					response.close();
				} 
				catch (IOException e) 
				{
					LOG.error("release response failed \r\n {}", LogUtils.encodeForLog(e.getMessage()));
				}
			}
    	}
    	return result;
    }
    
    /**
     * Send http's PUT request
     * @param workNo: the agent id
     * @param url:the address of the request
     * @param entityParams:the paramters of entity
     * @return
     */
    public static Map<String, Object> put(String workNo, String url, Object entityParams)
    {
    	CloseableHttpResponse response = null;
    	HttpPut put = null;
    	Map<String, Object> result = new HashMap<String, Object>();
    	try 
    	{
    		url = Normalizer.normalize(url, Form.NFKC);
	    	put = new HttpPut(url);
	    	if (null != entityParams) {
	    		String jsonString = JsonUtils.beanToJson(entityParams);
                HttpEntity entity = new StringEntity(jsonString);
                put.setEntity(entity);
            }
	    	
	    	if (!url.contains("/forcelogin"))
	    	{
	    	    setHeaders(workNo, put);
	    	}
				    	
	    	put.setHeader("Content-Type", "application/json;charset=UTF-8");
	    	response = client.execute(put);	    	
	    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	    	{
	    	    HttpEntity entity = response.getEntity();
	    	    if (null != entity)
	    	    {
	    	        String entityContent = EntityUtils.toString(entity,"UTF-8");
	    	        result = JsonUtils.jsonToMap(entityContent);
	    	    }
                if (url.contains("/forcelogin"))
                {
                    getGuidAndCookie(workNo, response);
                }
                
                try 
    	        {
    	            EntityUtils.consume(entity);
    	        } 
    	        catch (IOException e) 
    	        {
    	            LOG.error("release entity failed \r\n{} ", LogUtils.encodeForLog(e.getMessage()));
    	        }
	    	}
    	}
    	catch (UnsupportedEncodingException e)
    	{
    	    result = returnConnectError(e);
        } 
    	catch (ClientProtocolException e)
    	{
    	    result =  returnConnectError(e);
        } 
    	catch (IOException e)
    	{
    	    result = returnConnectError(e);
        }
    	finally
    	{
    		if (null != response)
    		{
				try
				{
					EntityUtils.consume(response.getEntity());
					response.close();
				} 
				catch (IOException e) 
				{
					LOG.error("release response failed \r\n {}", LogUtils.encodeForLog(e.getMessage()));
				}
			}
    	}
    	return result;
    }    
    
    
    /**
     * Get the guid and cookie after login
     * @param workNo
     * @param response
     */
    private static void getGuidAndCookie(String workNo, CloseableHttpResponse response)
    {
        Header[] allHeaders = response.getAllHeaders();
        if (allHeaders == null || allHeaders.length == 0)
        {
            return;
        }
        String guid = null;
        StringBuffer cookieBuffer = new StringBuffer();
        for (Header header : allHeaders)
        {
            if (header.getName().equals("Set-GUID"))
            {
                String setGuid = header.getValue();
                if (setGuid != null)
                {
                    guid = setGuid.replace("JSESSIONID=", "");
                }
            }  
            else if (header.getName().equals("Set-Cookie"))
            {
                String setCookie = header.getValue();
                if (setCookie != null)
                {
                    cookieBuffer.append(setCookie).append(";");
                }
            }
        }
        GlobalObject.addAgentAuthInfo(workNo, new AgentAuthInfoBean(guid, cookieBuffer.toString()));
        
    }
  

	
    /**
     * header set mothed abstract 
     * 
     * @param workNo
     * @param httpMethod
     */
    private static void setHeaders(String workNo, HttpRequestBase httpMethod)
    {
        AgentAuthInfoBean authInfo = GlobalObject.getAgentAuthInfo(workNo);
        if (null != authInfo)
        {
            httpMethod.setHeader("guid", formatHeader(authInfo.getGuid()));
            httpMethod.setHeader("Cookie", formatHeader(authInfo.getCookie()));
        }
        
    }
  
    
    /**
     * 返回ConnectotError信息 
     * @param e
     * @return
     */
    private static HashMap<String, Object> returnConnectError(Exception e)
    {
        LOG.error("request to server failed: \r\n {}", LogUtils.encodeForLog(e.getMessage()));
        String returnString = CONNECT_ERROR;
        return JsonUtils.jsonToMap(returnString);
    }
    
    
    /**
     * 格式化头域值；format header
     * @param header
     * @return
     */
    private static String formatHeader(String header)
    {
        if (null == header) 
        {
            return "";
        }
        header = Normalizer.normalize(header, Form.NFKC);
        String replaceAll = header.replaceAll("\r", "")
             .replaceAll("\n", "")
             .replaceAll(":", "")
             .replaceAll("=", "");
        return replaceAll;
    }
  

}