package tw.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class HttpUtil {
    public static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;

    static {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ex) {
            LOG.info("Exception", ex);
        }
    }
//	static {
//		// 设置连接池
//		connMgr = new PoolingHttpClientConnectionManager();
//		// 设置连接池大小
//		connMgr.setMaxTotal(100);
//		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
//
//		RequestConfig.Builder configBuilder = RequestConfig.custom();
//		// 设置连接超时
//		configBuilder.setConnectTimeout(MAX_TIMEOUT);
//		// 设置读取超时
//		configBuilder.setSocketTimeout(MAX_TIMEOUT);
//		// 设置从连接池获取连接实例的超时
//		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
//		// 在提交请求之前 测试连接是否可用
//		configBuilder.setStaleConnectionCheckEnabled(true);
//		requestConfig = configBuilder.build();
//	}

    public static void config(HttpURLConnection con) throws Exception {

        con.setRequestMethod("GET");

        // con.setInstanceFollowRedirects(true);

        // con.setDoInput( true );
        // con.setDoOutput( false );

        con.setConnectTimeout(10 * 60 * 1000);
        con.setReadTimeout(10 * 60 * 1000);
        // con.setUseCaches(false);
        // con.setRequestProperty("User-Agent","Mozilla/3.5.7 (compatible; MSIE
        // 5.0; Windows NT; DigExt)");
        // con.setRequestProperty("Connection","keep-alive");
        // con.setRequestProperty("Accept-Encoding", "identity");

    }

    public static String getHttpContentNio(String URL) throws MalformedURLException {
        URL url = new URL(URL);
        HttpURLConnection con = null;
        InputStream is = null;
        int contentLength = 0;
        try {
            int code;
            for (int redirect = 0; redirect <= 5; redirect++) {
                con = (HttpURLConnection) url.openConnection();
                config(con);
                con.setRequestProperty("Accept-Encoding", "gzip");
                // con.connect();
                code = con.getResponseCode();
                contentLength = con.getContentLength();
                LOG.info(" ResponseCode " + code + " 文件大小" + contentLength + " " + URL);
                /* 只记录第一次返回的code */
                boolean needBreak = false;
                switch (code) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        if (redirect == 5) {
                            throw new Exception("redirect to much time");
                        }
                        String location = con.getHeaderField("Location");
                        if (location == null) {
                            throw new Exception("redirect with no location");
                        }
                        url = new URL(url, location);
                        continue;
                    default:
                        needBreak = true;
                        break;
                }
                if (needBreak) {
                    break;
                }

            }

            is = con.getInputStream();
            String contentEncoding = con.getContentEncoding();
            if (contentEncoding != null && contentEncoding.equals("gzip")) {
                is = new GZIPInputStream(is);
                System.out.println("is gzip");
            }
            byte[] buf = new byte[10 * 1024];
            int read;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int retry = 0;
            int readAlready = 0;
            int k = 1;
            while (true) {
                if (retry > 1) {
                    break;
                }
                try {
                    if ((read = is.read(buf)) != -1) {
                        bos.write(buf, 0, read);
                        readAlready += read;
                        if (readAlready > k * 1024 * 1000) {
                            k++;
                            System.out.print("已下载" + (readAlready / 1000) + "K.." + URL);
                            System.out.println();
                        }
                        // System.out.println(new String (buf));
                    } else {
                        System.out.println();
                        System.out.println("下载完成" + contentLength / 1000 + "K..");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("读取错误！ 重试" + retry++ + URL);
                    System.out.print("已下载 " + (readAlready / 1000) + "K.." + URL);
                    Thread.sleep(5 * 1000);
                    if (bos.size() == contentLength) {
                        break;
                    }
                }
            }
            return new String(bos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
        return null;

    }

    public static String getHttpContent(String URL) throws MalformedURLException {
        URL url = new URL(URL);
        HttpURLConnection con = null;
        InputStream is = null;
        int contentLength = 0;
        try {
            int code;
            for (int redirect = 0; redirect <= 5; redirect++) {
                con = (HttpURLConnection) url.openConnection();
                config(con);
                con.setRequestProperty("Accept-Encoding", "gzip");
                // con.connect();
                code = con.getResponseCode();
                contentLength = con.getContentLength();
                LOG.info(" ResponseCode " + code + " 文件大小" + contentLength + " " + URL);
				/* 只记录第一次返回的code */
                boolean needBreak = false;
                switch (code) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        if (redirect == 5) {
                            throw new Exception("redirect to much time");
                        }
                        String location = con.getHeaderField("Location");
                        if (location == null) {
                            throw new Exception("redirect with no location");
                        }
                        url = new URL(url, location);
                        continue;
                    default:
                        needBreak = true;
                        break;
                }
                if (needBreak) {
                    break;
                }

            }

            is = con.getInputStream();
            String contentEncoding = con.getContentEncoding();
            if (contentEncoding != null && contentEncoding.equals("gzip")) {
                is = new GZIPInputStream(is);
                System.out.println("is gzip");
            }
            byte[] buf = new byte[10 * 1024];
            int read;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int retry = 0;
            int readAlready = 0;
            int k = 1;
            while (true) {
                if (retry > 1) {
                    break;
                }
                try {
                    if ((read = is.read(buf)) != -1) {
                        bos.write(buf, 0, read);
                        readAlready += read;
                        if (readAlready > k * 1024 * 1000) {
                            k++;
                            System.out.print("已下载" + (readAlready / 1000) + "K.." + URL);
                            System.out.println();
                        }
                        // System.out.println(new String (buf));
                    } else {
                        System.out.println();
                        System.out.println("下载完成" + contentLength / 1000 + "K..");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("读取错误！ 重试" + retry++ + URL);
                    System.out.print("已下载 " + (readAlready / 1000) + "K.." + URL);
                    Thread.sleep(5 * 1000);
                    if (bos.size() == contentLength) {
                        break;
                    }
                }
            }
            return new String(bos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
        return null;

    }

    public static String doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpPost = new HttpGet(apiUrl);
            HttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();

            System.out.println(url + "执行状态码 : " + statusCode);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param apiUrl
     * @return
     */
    public static String doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            System.out.println(response.toString());
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl
     * @param json   json对象
     * @return
     */
    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            System.out.println(response.getStatusLine().getStatusCode());
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     *
     * @param apiUrl API接口URL
     * @param json   JSON对象
     * @return
     */
    public static String doPostSSL(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
                .setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

}
