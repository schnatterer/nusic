package org.musicbrainz.webservice.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParamBean;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.musicbrainz.webservice.AuthorizationException;
import org.musicbrainz.webservice.DefaultWebServiceWs2;
import org.musicbrainz.webservice.RequestException;
import org.musicbrainz.webservice.ResourceNotFoundException;
import org.musicbrainz.webservice.WebServiceException;
import org.musicbrainz.wsxml.MbXMLException;
import org.musicbrainz.wsxml.element.Metadata;

/**
 * A simple http client using Apache Commons HttpClient.
 * 
 */
public class HttpClientWebServiceWs2 extends DefaultWebServiceWs2 {
	/**
	 * A logger
	 */
	private Log log = LogFactory.getLog(HttpClientWebServiceWs2.class);

	/**
	 * A {@link HttpClient} instance
	 */
	private DefaultHttpClient httpClient;

	/**
	 * Default constructor creates a httpClient with default properties.
	 */
	public HttpClientWebServiceWs2() {
		this.httpClient = new DefaultHttpClient();
	}

	/**
	 * Use this constructor to inject a configured {@link DefaultHttpClient}.
	 * 
	 * @param httpClient
	 *            A configured {@link DefaultHttpClient}.
	 */
	public HttpClientWebServiceWs2(DefaultHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	private void setConnectionParam() {

		HttpParams connectionParams = httpClient.getParams();

		HttpConnectionParams.setConnectionTimeout(connectionParams, 60000);
		HttpConnectionParams.setSoTimeout(connectionParams, 60000);
		connectionParams.setParameter("http.useragent", USERAGENT);
		connectionParams.setParameter("http.protocol.content-charset", "UTF-8");

		if (getUsername() != null && !getUsername().isEmpty()) {

			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
					getUsername(), getPassword());

			AuthScope authScope = new AuthScope(getHost(), AuthScope.ANY_PORT,
					AuthScope.ANY_REALM, AuthScope.ANY_SCHEME);

			httpClient.getCredentialsProvider()
					.setCredentials(authScope, creds);
		}

	}

	private void setRetryHandler() {

		// retry 3 times, do not retry if we got a response, because we
		// may only query the web service once a second

		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {

				if (executionCount >= 3) {
					// Do not retry if over max retry count
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// Retry if the server dropped connection on us
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// Do not retry on SSL handshake exception
					return false;
				}
				HttpRequest request = (HttpRequest) context
						.getAttribute(ExecutionContext.HTTP_REQUEST);
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// Retry if the request is considered idempotent
					return true;
				}
				return false;
			}
		};

		httpClient.setHttpRequestRetryHandler(myRetryHandler);
	}

	@Override
	protected Metadata doGet(String url) throws WebServiceException,
			MbXMLException {
		setConnectionParam();
		setRetryHandler();// inside the call

		// retry with new calls if the error is 503 Service unavaillable.
		boolean repeat = true;
		int trial = 0;
		int maxtrial = 5;

		while (repeat) {

			trial++;
			HttpGet method = new HttpGet(url);
			Metadata md = executeMethod(method);
			if (md == null && trial > maxtrial) {
				String em = "ABORTED: web service returned an error "
						+ maxtrial + " time consecutively";
				log.error(em);
				throw new WebServiceException(em);
			} else if (md != null) {
				return md;
			}

		} // end wile
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.musicbrainz.webservice.DefaultWebServiceWs1#doGet(java.lang.String,
	 * java.io.InputStream)
	 */
	@Override
	protected Metadata doPost(String url, Metadata md)
			throws WebServiceException, MbXMLException {

		setConnectionParam();
		setRetryHandler();// inside the call

		HttpPost method = new HttpPost(url);

		try {

			StringEntity httpentity = new StringEntity(getWriter()
					.getXmlString(md));
			httpentity.setContentType(new BasicHeader("Content-Type",
					"application/xml; charset=UTF-8"));

			method.setEntity(httpentity);
			return executeMethod(method);

		} catch (IOException ex) {
			Logger.getLogger(HttpClientWebServiceWs2.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return null;
	}

	@Override
	protected Metadata doPut(String url) throws WebServiceException,
			MbXMLException {
		setConnectionParam();

		HttpPut method = new HttpPut(url);
		return executeMethod(method);

	}

	@Override
	protected Metadata doDelete(String url) throws WebServiceException,
			MbXMLException {

		setConnectionParam();
		HttpDelete method = new HttpDelete(url);
		return executeMethod(method);
	}

	private Metadata executeMethod(HttpUriRequest method)
			throws MbXMLException, WebServiceException {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
		paramsBean.setUserAgent(USERAGENT);
		method.setParams(params);

		// Try using compression
		method.setHeader("Accept-Encoding", "gzip");
		
		try {
			// Execute the method.
			System.out.println("Hitting url: " + method.getURI().toString());
			HttpResponse response = this.httpClient.execute(method);

			lastHitTime = System.currentTimeMillis();

			int statusCode = response.getStatusLine().getStatusCode();

			switch (statusCode) {
			case HttpStatus.SC_SERVICE_UNAVAILABLE: {
				// Maybe the server is too busy, let's try again.
				log.warn(buildMessage(response, "Service unavaillable"));
				method.abort();
				lastHitTime = System.currentTimeMillis();
				wait(1);
				return null;
			}
			case HttpStatus.SC_BAD_GATEWAY: {
				// Maybe the server is too busy, let's try again.
				log.warn(buildMessage(response, "Bad Gateway"));
				method.abort();
				lastHitTime = System.currentTimeMillis();
				wait(1);
				return null;
			}
			case HttpStatus.SC_OK:
				InputStream instream = response.getEntity().getContent();
				// Check if content is compressed
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}
				Metadata mtd = getParser().parse(instream);
				// Closing the input stream will trigger connection release
				try {
					instream.close();
				} catch (Exception ignore) {
				}
				lastHitTime = System.currentTimeMillis();
				return mtd;

			case HttpStatus.SC_NOT_FOUND:
				throw new ResourceNotFoundException(buildMessage(response,
						"Not found"));

			case HttpStatus.SC_BAD_REQUEST:
				throw new RequestException(
						buildMessage(response, "Bad Request"));

			case HttpStatus.SC_FORBIDDEN:
				throw new AuthorizationException(buildMessage(response,
						"Forbidden"));

			case HttpStatus.SC_UNAUTHORIZED:
				throw new AuthorizationException(buildMessage(response,
						"Unauthorized"));

				// This is the actual response code for invalid username o
				// password
			case HttpStatus.SC_INTERNAL_SERVER_ERROR: {
				throw new AuthorizationException(buildMessage(response,
						"Internal server error"));
			}
			default: {

				String em = buildMessage(response, "");
				log.error("Fatal web service error: " + em);
				throw new WebServiceException(em);
			}

			}
		} catch (IOException e) {
			log.error("Fatal transport error: " + e.getMessage());
			throw new WebServiceException(e.getMessage(), e);
		}
	}

	private String buildMessage(HttpResponse response, String status) {
		String msg = "";
		InputStream instream;
		int statusCode = response.getStatusLine().getStatusCode();
		String reasonPhrase = response.getStatusLine().getReasonPhrase();

		if (reasonPhrase == null || reasonPhrase.isEmpty())
			reasonPhrase = status;

		msg = "Server response was: " + statusCode + " " + reasonPhrase;

		try {
			instream = response.getEntity().getContent();
			Metadata mtd;
			try {
				mtd = getParser().parse(instream);
				msg = msg + " MESSAGE: " + mtd.getMessage();
				instream.close();
			} catch (MbXMLException ex) {
				Logger.getLogger(HttpClientWebServiceWs2.class.getName()).log(
						Level.SEVERE, convertInputStreamToString(instream), ex);
			}
		} catch (IOException ignore) {
		} catch (IllegalStateException ignore) {
		}

		finally {
			try {
				consume(response.getEntity());
			} catch (IOException ex) {
			}
		}
		return msg;
	}

	/**
	 * Ensures that the entity content is fully consumed and the content stream,
	 * if exists, is closed.
	 * 
	 * @param entity
	 * @throws IOException
	 *             if an error occurs reading the input stream
	 * 
	 */
	private void consume(final HttpEntity entity) throws IOException {
		if (entity == null) {
			return;
		}
		if (entity.isStreaming()) {
			InputStream instream = entity.getContent();
			if (instream != null) {
				instream.close();
			}
		}
	}

	private static long lastHitTime = 0;

	private static void wait(int seconds) {
		long t1;
		if (lastHitTime > 0) {
			do {
				t1 = System.currentTimeMillis();
			} while (t1 - lastHitTime < seconds * 1000);
		}
	}

	/**
	 * method to convert an InputStream to a string using the
	 * BufferedReader.readLine() method this methods reads the InputStream line
	 * by line until the null line is encountered it appends each line to a
	 * StringBuilder object for optimal performance
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		if (inputStream != null) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}
			} finally {
				inputStream.close();
			}

			return stringBuilder.toString();
		} else {
			return null;
		}
	}
}
