package io.college.cms.core.application;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ConnectionService {
	public static String HTTPS_PROXY_HOST = "https.proxyHost";
	public static String HTTPS_PROXY_PORT = "https.proxyPort";
	public static String HTTP_PROXY_HOST = "http.proxyHost";
	public static String HTTP_PROXY_PORT = "http.proxyPort";
	static {
		System.setProperty(HTTPS_PROXY_HOST, "PITC-Zscaler-Americas-Cincinnati3PR.proxy.corporate.ge.com");
		System.setProperty(HTTPS_PROXY_PORT, "80");
		System.setProperty(HTTP_PROXY_HOST, "PITC-Zscaler-Americas-Cincinnati3PR.proxy.corporate.ge.com");
		System.setProperty(HTTP_PROXY_PORT, "80");
		processSSLTrust();
	}

	private static void processSSLTrust() {

		try {

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { getTrustManager() }, new SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostName, SSLSession session) {

					return true;
				}

			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private static X509TrustManager getTrustManager() {
		return new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {

				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {

			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {

			}

		};
	}

	public String httpsPost(String url, Map<String, String> headers, String body) {
		StringBuilder json = new StringBuilder();
		try {
			HttpURLConnection post = (HttpURLConnection) new URL(url).openConnection();
			for (String key : headers.keySet()) {
				post.setRequestProperty(key, headers.get(key));
			}
			post.setDoOutput(true);
			post.getOutputStream().write(body.toString().getBytes());
			json = new StringBuilder().append(org.apache.commons.io.IOUtils.toString(post.getInputStream(), "UTF-8"));
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return json.toString();
	}
}
