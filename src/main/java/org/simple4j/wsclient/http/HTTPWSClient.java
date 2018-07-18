package org.simple4j.wsclient.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.simple4j.wsclient.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the HTTP connection manager. Server host, port, http/https,
 * keystore/truststore, SSL protocol, proxy timeouts are configured using this
 * class.
 * 
 * @author jsrinivas108
 */
public class HTTPWSClient
{

	private static Logger logger = LoggerFactory.getLogger(HTTPWSClient.class);

	private HttpClient httpClient;

	private int socketTimeOutMillis;

	private int connectionTimeOutMillis;

	private int numberOfRetries;

	private int maxConnectionsPerHost;

	private String serviceProtocol;

	private String serviceHostName;

	private int servicePortNumber = -1;

	private String keyStorePath;

	private String keyStorePassword;

	private String trustStorePath;

	private String trustStorePassword;

	private String keyStoreType = "jks";

	private String trustStoreType = "jks";

	private String[] supportedProtocols = new String[] { "TLSv1" };

	private String[] supportedCipherSuites = null;

	private String keyPassword = null;

	private String proxyHostName = null;

	private int proxyPortNumber = -1;

	private HttpClient getHttpClient() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, KeyManagementException, UnrecoverableKeyException
	{

		if (httpClient == null)
		{
			HttpClientBuilder hCBuilder = HttpClients.custom();
			Registry<ConnectionSocketFactory> socketFactoryRegistry = null;
			if (this.getKeyStorePath() != null && this.getKeyStorePath().trim().length() > 0)
			{
				KeyStore trustStore = KeyStore.getInstance(this.getTrustStoreType());
				FileInputStream trustStream = new FileInputStream(this.getTrustStorePath());
				trustStore.load(trustStream, this.getTrustStorePassword().toCharArray());

				KeyStore keyStore = KeyStore.getInstance(this.getKeyStoreType());
				FileInputStream keyStream = new FileInputStream(this.getKeyStorePath());
				keyStore.load(keyStream, this.getKeyStorePassword().toCharArray());

				SSLContext sslcontext = SSLContexts.custom()
						.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
						.loadKeyMaterial(keyStore, this.getKeyPassword().toCharArray()).build();

				SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslcontext,
						this.getSupportedProtocols(), this.getSupportedCipherSuites(),
						SSLConnectionSocketFactory.getDefaultHostnameVerifier());

				socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register(this.getServiceProtocol(), socketFactory).build();
			} else
			{
				socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register(this.getServiceProtocol(), PlainConnectionSocketFactory.INSTANCE).build();
			}

			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			connManager.setMaxPerRoute(
					new HttpRoute(new HttpHost(this.getServiceHostName(), this.getServicePortNumber())),
					this.getMaxConnectionsPerHost());
			hCBuilder.setConnectionManager(connManager);

			if (this.getProxyHostName() != null && this.getProxyPortNumber() != -1)
			{
				hCBuilder.setProxy(new HttpHost(this.getProxyHostName(), this.getProxyPortNumber()));

				// TODO : implement proxy authentication
				// HttpHost targetHost = new HttpHost("localhost", 80, "http");
				// CredentialsProvider credsProvider = new BasicCredentialsProvider();
				// credsProvider.setCredentials(
				// new AuthScope(this.getProxyHostName(), this.getProxyPortNumber()),
				// new UsernamePasswordCredentials("username", "password"));
				// AuthCache authCache = new BasicAuthCache();
				// // Generate BASIC scheme object and add it to the local auth cache
				// BasicScheme basicAuth = new BasicScheme();
				// authCache.put(targetHost, basicAuth);
				//
				// hCBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}

			CloseableHttpClient httpClient = hCBuilder.setRetryHandler(new HttpRequestRetryHandler()
			{

				@Override
				public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
				{

					if (executionCount >= getNumberOfRetries())
					{
						// Do not retry if over max retry count
						return false;
					}
					if (exception instanceof UnknownHostException)
					{
						// Unknown host
						return false;
					}
					if (exception instanceof SSLException)
					{
						// SSL handshake exception
						return false;
					}
					return true;
				}
			}).build();
			Runtime.getRuntime().addShutdownHook(new HttpClientCleaner(httpClient));
			this.httpClient = httpClient;
		}
		return httpClient;
	}

	private class HttpClientCleaner extends Thread
	{
		private CloseableHttpClient httpClient = null;

		HttpClientCleaner(CloseableHttpClient httpClient)
		{
			this.httpClient = httpClient;
		}

		public void run()
		{
			if (this.httpClient != null)
				try
				{
					this.httpClient.close();
				} catch (IOException e)
				{
					logger.warn("Error while closing http client", e);
				}
		}
	}

	public int getSocketTimeOutMillis()
	{
		return socketTimeOutMillis;
	}

	public void setSocketTimeOutMillis(int socketTimeOutMillis)
	{
		this.socketTimeOutMillis = socketTimeOutMillis;
	}

	public int getConnectionTimeOutMillis()
	{
		return connectionTimeOutMillis;
	}

	public void setConnectionTimeOutMillis(int connectionTimeOutMillis)
	{
		this.connectionTimeOutMillis = connectionTimeOutMillis;
	}

	public int getNumberOfRetries()
	{
		return numberOfRetries;
	}

	public void setNumberOfRetries(int numberOfRetries)
	{
		this.numberOfRetries = numberOfRetries;
	}

	public int getMaxConnectionsPerHost()
	{
		return maxConnectionsPerHost;
	}

	public void setMaxConnectionsPerHost(int maxConnectionsPerHost)
	{
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	public String getServiceProtocol()
	{
		return serviceProtocol;
	}

	public void setServiceProtocol(String serviceProtocol)
	{
		this.serviceProtocol = serviceProtocol;
	}

	public String getServiceHostName()
	{
		return serviceHostName;
	}

	public void setServiceHostName(String serviceHostName)
	{
		this.serviceHostName = serviceHostName;
	}

	public int getServicePortNumber()
	{
		return servicePortNumber;
	}

	public void setServicePortNumber(int servicePortNumber)
	{
		this.servicePortNumber = servicePortNumber;
	}

	public String getKeyStorePath()
	{
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath)
	{
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePassword()
	{
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword)
	{
		this.keyStorePassword = keyStorePassword;
	}

	public String getTrustStorePath()
	{
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath)
	{
		this.trustStorePath = trustStorePath;
	}

	public String getTrustStorePassword()
	{
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword)
	{
		this.trustStorePassword = trustStorePassword;
	}

	public String getKeyStoreType()
	{
		return keyStoreType;
	}

	public void setKeyStoreType(String keyStoreType)
	{
		this.keyStoreType = keyStoreType;
	}

	public String getTrustStoreType()
	{
		return trustStoreType;
	}

	public void setTrustStoreType(String trustStoreType)
	{
		this.trustStoreType = trustStoreType;
	}

	public String[] getSupportedProtocols()
	{
		return supportedProtocols;
	}

	public void setSupportedProtocols(String[] supportedProtocols)
	{
		this.supportedProtocols = supportedProtocols;
	}

	public String[] getSupportedCipherSuites()
	{
		return supportedCipherSuites;
	}

	public void setSupportedCipherSuites(String[] supportedCipherSuites)
	{
		this.supportedCipherSuites = supportedCipherSuites;
	}

	public String getKeyPassword()
	{
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword)
	{
		this.keyPassword = keyPassword;
	}

	public String getProxyHostName()
	{
		return proxyHostName;
	}

	public void setProxyHostName(String proxyHostName)
	{
		this.proxyHostName = proxyHostName;
	}

	public int getProxyPortNumber()
	{
		return proxyPortNumber;
	}

	public void setProxyPortNumber(int proxyPortNumber)
	{
		this.proxyPortNumber = proxyPortNumber;
	}

	public HTTPWSResponse get(String url, Map<String, List<String>> requestHeaders)
	{
		HttpGet httpGet = new HttpGet();
		return processMethod(url, null, requestHeaders, httpGet);
	}

	public HTTPWSResponse delete(String url, Map<String, List<String>> requestHeaders)
	{
		HttpDelete postMethod = new HttpDelete();
		return processMethod(url, null, requestHeaders, postMethod);
	}

	public HTTPWSResponse post(String url, String bodyStr, Map<String, List<String>> requestHeaders)
	{
		HttpPost postMethod = new HttpPost();
		return processMethod(url, bodyStr, requestHeaders, postMethod);
	}

	private HTTPWSResponse processMethod(String url, String bodyStr, Map<String, List<String>> requestHeaders,
			HttpRequestBase httpRequest)
	{

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.getSocketTimeOutMillis())
				.setConnectTimeout(this.getConnectionTimeOutMillis())
				.setConnectionRequestTimeout(this.getConnectionTimeOutMillis()).build();

		httpRequest.setConfig(requestConfig);
		String hostHeaderValue = getHostHeaderValue();
		if (!url.startsWith(this.getServiceProtocol()))
		{
			if (url.startsWith("/"))
			{
				url = this.getServiceProtocol() + "://" + hostHeaderValue + url;
			} else
			{
				url = this.getServiceProtocol() + "://" + hostHeaderValue + "/" + url;
			}
		}
		httpRequest.setHeader("Host", hostHeaderValue);
		HttpClient httpClient = null;
		try
		{
			URI uri = new URI(url);
			httpRequest.setURI(uri);
			httpClient = this.getHttpClient();
			if (requestHeaders != null && requestHeaders.size() > 0)
			{
				for (Entry<String, List<String>> entry : requestHeaders.entrySet())
				{
					List<String> values = entry.getValue();
					for (String value : values)
					{
						httpRequest.addHeader(entry.getKey(), value);
					}
				}
			}
			if (bodyStr != null && bodyStr.length() > 0 && httpRequest instanceof HttpEntityEnclosingRequestBase)
			{
				HttpEntityEnclosingRequestBase entityEnclosingMethod = (HttpEntityEnclosingRequestBase) httpRequest;
				entityEnclosingMethod.setEntity(new StringEntity(bodyStr));
				// entityEnclosingMethod.setHeader("Content-Length",
				// String.valueOf(bodyStr != null ? bodyStr.length() : 0));
			}

			ResponseHandler<HTTPWSResponse> responseHandler = new ResponseHandler<HTTPWSResponse>()
			{

				@Override
				public HTTPWSResponse handleResponse(final HttpResponse response) throws IOException
				{
					return new HTTPWSResponse(response);
				}
			};
			return httpClient.execute(httpRequest, responseHandler);
		} catch (Exception e)
		{
			throw new SystemException("", "Error while calling service: " + httpRequest, e);
		} finally
		{
		}
	}

	private String getHostHeaderValue()
	{
		String hostHeaderValue = this.getServiceHostName() + ":" + this.getServicePortNumber();
		return hostHeaderValue;
	}

	public HTTPWSResponse put(String url, String bodyStr, Map<String, List<String>> requestHeaders)
	{
		HttpPut postMethod = new HttpPut();
		return processMethod(url, bodyStr, requestHeaders, postMethod);
	}

}
