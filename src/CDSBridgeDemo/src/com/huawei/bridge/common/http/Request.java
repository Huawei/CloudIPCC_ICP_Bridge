package com.huawei.bridge.common.http;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.LineIterator;
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
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.util.encoders.Base64;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.bridge.common.global.CommonConstant;
import com.huawei.bridge.common.global.GlobalObject;
import com.huawei.bridge.common.global.HeaderType;
import com.huawei.bridge.common.util.LogUtils;
import com.huawei.bridge.common.util.Md5Util;
import com.huawei.bridge.common.util.StringUtils;




public class Request
{
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

	
	
	private static PoolingHttpClientConnectionManager connManager = null;
	
	private static CloseableHttpClient client = null;
	
	
    public static void init()
    {
        SSLContext sslContext;
        try
        {
            sslContext = SSLContext.getInstance("TLSv1.2");
            
            sslContext.init(null, new TrustManager[] {new CdsTrustManager()}, null);
            
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier()
            {
                public boolean verify(String arg0, SSLSession arg1)
                {
                    return true;
                }
                
                public void verify(String host, SSLSocket ssl)
                    throws IOException
                {
                }
                
                public void verify(String host, X509Certificate cert)
                    throws SSLException
                {
                }
                
                public void verify(String host, String[] cns, String[] subjectAlts)
                    throws SSLException
                {
                }
            });
            
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
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
     * Send login request
     * @param userId: the account
     * @param password: the password of the user
     * @param url:the address of the request
     * @return
     */
    public static Map<String, Object> loginPost(String userId, String password, String url)
    {
     
        Map<String, Object> result = null;
        HttpPost post = null;
        CloseableHttpResponse response = null;
        try
        {
            url = Normalizer.normalize(url, Form.NFKC);
            post = new HttpPost(url);
            
            setHeaders(userId, post, HeaderType.USER_ID);                
            post.setHeader("Content-Type", "application/json;charset=UTF-8");   
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
            {
                Header[] authH = response.getHeaders("WWW-Authenticate");
                String authHeader = authH[0].getValue();
                Matcher matcher = Pattern.compile("auth-name=\"(.+?)\"").matcher(authHeader);
                String auth_name = match(matcher);
                matcher = Pattern.compile("realm=\"(.+?)\"").matcher(authHeader);
                String realm = match(matcher);
                matcher = Pattern.compile("nonce=\"(.+?)\"").matcher(authHeader);
                String nonce = match(matcher);
                String challenge = authHeader.indexOf("Digest") < 0 ? "Basic" : "Digest";
                String namePastr = new StringBuffer().append(auth_name).append(":").append(password).toString();
                String namRealmPasStr = new StringBuffer().append(auth_name).append(":").append(realm).append(":").append(password).toString();
                String methodUri = new StringBuffer().append("POST").append(":").append("/login/sc").toString();
                String HA1 = Md5Util.caculateStringMd5Value(namRealmPasStr);
                String HA2 = Md5Util.caculateStringMd5Value(methodUri);
                String secondRequest = Md5Util.caculateStringMd5Value(HA1 + ":" + nonce + ":" + HA2);
                String credentials = new String(Base64.encode(namePastr.getBytes(CommonConstant.UTF_8)), CommonConstant.UTF_8);
                StringBuffer digestInfo = new StringBuffer();
                digestInfo.append("Digest username=").append(auth_name).append(", realm=").append(realm).append(", nonce=")
                    .append(nonce).append(", uri=/login/sc, response=").append(secondRequest);
                String authorizationStr = challenge.equals("Digest") ? digestInfo.toString() : "Basic " + credentials;
                        
                post = new HttpPost(url);  
                setHeaders(authorizationStr, post, HeaderType.AUTHORIZATION_STR);  
                post.setHeader("Content-Type", "application/json");               
                response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    InputStream is = response.getEntity().getContent();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is, CommonConstant.UTF_8));
                    StringBuffer buffer = new StringBuffer();

                    LineIterator lineItr = new LineIterator(in);
                    while (lineItr.hasNext())
                    {
                        buffer.append(lineItr.next());
                    }
                    String strs = buffer.toString();
                    int start = strs.indexOf("<AccessToken>");
                    int end = strs.indexOf("</AccessToken>");
                    String token = strs.substring(start + 13, end);
                    GlobalObject.setAuthInfo(userId, token);
                    result = new HashMap<>();
                    result.put("data", token);
                    result.put("returnCode", 0);
                    result.put("returnDesc", "Successful operation.");
                }
                else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN)
                {
                    String resultStr = "{\"returnDesc\":\"Authentication failure.\",\"returnCode\":\"300088\"}";
                    result = jsonToMap(resultStr);
                }
                else
                {
                    HttpEntity entity = response.getEntity();
                    result = new HashMap<String, Object>();
                    String entityContent = "unkown error";
                    if (null != entity)
                    {
                        entityContent = EntityUtils.toString(entity, "UTF-8");
                    }
                    result.put("returnCode", 1);
                    result.put("returnDesc", entityContent);
                }
            }
            else
            {
                HttpEntity entity = response.getEntity();
                result = new HashMap<String, Object>();
                String entityContent = "unkown error";
                if (null != entity)
                {
                    entityContent = EntityUtils.toString(entity, "UTF-8");
                }
                result.put("returnCode", 1);
                result.put("returnDesc", entityContent);
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
            if (response != null)
            {
                try
                {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e)
                {
                    LOG.error("release connection failed, the exception is \r\n {}",
                            e.getMessage());
                }
            }
        }
        return result;
    }
    
    
    /**
     * Send login request
     * @param userId: the account
     * @param token: the authInfo
     * @param url:the address of the request
     * @return
     */
    public static Map<String, Object> tokenPost(String userId, String token, String url)
    {
     
        Map<String, Object> result = null;
        HttpPost post = null;
        CloseableHttpResponse response = null;
        try
        {
            url = Normalizer.normalize(url, Form.NFKC);
            post = new HttpPost(url);
            
            setHeaders(token, post, HeaderType.AUTHORIZATION_STR);               
            post.setHeader("Content-Type", "application/json;charset=UTF-8");   
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                InputStream is = response.getEntity().getContent();
                BufferedReader in = new BufferedReader(new InputStreamReader(is, CommonConstant.UTF_8));
                StringBuffer buffer = new StringBuffer();

                LineIterator lineItr = new LineIterator(in);
                while (lineItr.hasNext())
                {
                    buffer.append(lineItr.next());
                }
                String strs = buffer.toString();
                int start = strs.indexOf("<AccessToken>");
                int end = strs.indexOf("</AccessToken>");
                token = strs.substring(start + 13, end);
                GlobalObject.setAuthInfo(userId, token);
                result = new HashMap<>();
                result.put("data", token);
                result.put("returnCode", 0);
                result.put("returnDesc", "Successful operation.");
            }
            else
            {
                HttpEntity entity = response.getEntity();
                result = new HashMap<String, Object>();
                String entityContent = "unkown error";
                if (null != entity)
                {
                    entityContent = EntityUtils.toString(entity, "UTF-8");
                }
                result.put("returnCode", 1);
                result.put("returnDesc", entityContent);
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
            if (response != null)
            {
                try
                {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e)
                {
                    LOG.error("release connection failed, the exception is \r\n {}", e.getMessage());
                }
            }
        }
        return result;
    }


    /**
     * Send http's PUT request
     * @param  token: it's used for auth
     * @param url: the address of the request
     * @param entityParams: the paramters of entity 
     * @return
     */
    public static Map<String, Object> put(String token, String url, Object entityParams)
    {
        CloseableHttpResponse response = null;
        HttpPut put = null;
        Map<String, Object> result = null;
        try
        {                      
            url = Normalizer.normalize(url, Form.NFKC);
            put = new HttpPut(url);
            
            setHeaders(token, put, HeaderType.TOKEN);
            put.setHeader("Content-Type", "application/json");                       
            if (null != entityParams)
            {
                String jsonString = beanToJson(entityParams);
                HttpEntity entity = new StringEntity(jsonString);
                put.setEntity(entity);
            }
            
            response = client.execute(put);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (null != entity)
                {
                    String entityContent = EntityUtils.toString(entity,"UTF-8");
                    result = jsonToMap(entityContent);
                }
                               
            }
            else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN)
            {
                String resultStr = "{\"returnDesc\":\"Authentication failure.\",\"returnCode\":\"300088\"}";
                result = jsonToMap(resultStr);
            }
            else
            {
                HttpEntity entity = response.getEntity();
                result = new HashMap<String, Object>();
                String entityContent = "unkown error";
                if (null != entity)
                {
                    entityContent = EntityUtils.toString(entity, "UTF-8");
                }
                result.put("returnCode", 1);
                result.put("returnDesc", entityContent);
            }
        }
        catch (UnsupportedEncodingException e)
        {
             result =  returnConnectError(e);
        } 
        catch (ClientProtocolException e)
        {
             result =  returnConnectError(e);
        } 
        catch (IOException e)
        {
             result =  returnConnectError(e);
        }
        finally
        {
            if (response != null)
            {
                try
                {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e)
                {
                    LOG.error("release connection failed, the exception is \r\n {}", e.getMessage());
                }
            }
        }
        return result;
    }
    
    /**
     * Send http's POST request
     * @param token: it's used for auth
     * @param url:the address of the request
     * @param entityParams:the paramters of entity
     * @return
     */
    public static Map<String, Object> post(String token, String url, Object entityParams)
    {       
     
        Map<String, Object> result = null;
        HttpPost post = null;
        CloseableHttpResponse response = null;
        try
        {            
            url = Normalizer.normalize(url, Form.NFKC);
            post = new HttpPost(url);
            setHeaders(token, post, HeaderType.TOKEN);
            post.setHeader("Content-Type", "application/json;charset=UTF-8");                       
            if (null != entityParams)
            {
                String jsonString = beanToJson(entityParams);
                HttpEntity entity = new StringEntity(jsonString);
                post.setEntity(entity);
            }
            
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = response.getEntity();
                if (null != entity)
                {
                    String entityContent = EntityUtils.toString(entity,"UTF-8");
                    result = jsonToMap(entityContent);
                }  
            }
            else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN)
            {
                String resultStr = "{\"returnDesc\":\"Authentication failure.\",\"returnCode\":\"300088\"}";
                result = jsonToMap(resultStr);
            }
            else
            {
                HttpEntity entity = response.getEntity();
                result = new HashMap<String, Object>();
                String entityContent = "unkown error";
                if (null != entity)
                {
                    entityContent = EntityUtils.toString(entity, "UTF-8");
                }
                result.put("returnCode", 1);
                result.put("returnDesc", entityContent);
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
            if (response != null)
            {
                try
                {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException e)
                {
                    LOG.error("release connection failed, the exception is \r\n {}",
                            e.getMessage());
                }
            }
        }
        return result;
    }
    
	
    /**
     * Change object to json-string
     * @param object
     * @return
     * @throws IOException
     */
    public static String beanToJson(Object object)
    {
              
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        JsonGenerator gen = null;
        String json = "";
        try 
        {
            gen = new JsonFactory().createJsonGenerator(writer);
            mapper.writeValue(gen, object);
            json = writer.toString();
        } 
        catch (IOException e) 
        {
            LOG.error("object to json string failed, the exception is \r\n {}", e.getMessage());
        }
        finally
        {
            if(gen != null)
            {
                try 
                {
                    gen.close();
                } 
                catch (IOException e) 
                {
                    LOG.error("close Json generator failed, the exception is \r\n {}", e.getMessage());
                }
            }
            try 
            {
                writer.close();
            } catch (IOException e)
            {
                LOG.error("close StringWriter failed, the exception is \r\n {}", e.getMessage());
            }
            
        }
        return json;
    }
    
    /**
     * json string to map
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> jsonToMap(String json)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> result;
        try
        {
            result = objectMapper.readValue(json, HashMap.class);
            return result;
        }
        catch (JsonParseException e)
        {
            LOG.error("catch JsonParseException, the exception is \r\n {}", e.getMessage());
            return null;
        }
        catch (JsonMappingException e)
        {
            LOG.error("catch JsonMappingException, the exception is \r\n {}", e.getMessage());
            return null;
        }
        catch (IOException e)
        {
            LOG.error("catch IOException, the exception is \r\n {}", e.getMessage());
            return null;
        }
    }
    
    public static String match(Matcher matcher)
    {
        while (matcher.find())
        {
            return matcher.group(1);
        }
        return "";
    }
    
    /**
     * header set mothed abstract 
     * 
     * @param header : the content of the header
     * @param httpMethod
     * @param headerType : the type of the header
     */
    private static void setHeaders(String header, HttpRequestBase httpMethod, int headerType)
    {
        if (!StringUtils.isNullOrBlank(header))
        {
            String str = checkHeader(header);
            if (HeaderType.TOKEN == headerType)
            {
                httpMethod.setHeader("Authorization", "Basic " + str);
            }
            else if(HeaderType.USER_ID == headerType)
            {
                httpMethod.setHeader("Authorization", "Digest username=" + str + ", algorithm=MD5");
            }
            else if(HeaderType.AUTHORIZATION_STR == headerType)
            {
                httpMethod.setHeader("Authorization", str);
            }
            else
            {
                 LOG.error("headerType error");
            }
        }
    }
      
    /**
     * replace \r \n
     * @param header
     * @return
     */
    public static String checkHeader(String header)
    {
       header = Normalizer.normalize(header, Form.NFKC);
       String replaceAll = header.replaceAll("\r", "")
             .replaceAll("\n", "");
       return replaceAll;
    }
    
    /**
     * Reteurn ConnectotError
     * @param e
     * @return
     */
    private static HashMap<String, Object> returnConnectError(Exception e)
    {
        LOG.error("request to server failed: \r\n {}", LogUtils.encodeForLog(e.getMessage()));
        String resultStr = "{\"returnDesc\":\"Connect Error failed.\",\"returnCode\":\"NETWORKERROR\"}";
        return jsonToMap(resultStr);
    }    
    
}