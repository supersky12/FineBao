package com.yujunkang.fangxinbao.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.Parser;
import com.yujunkang.fangxinbao.preferences.Preferences;
import com.yujunkang.fangxinbao.utility.DataConstants;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
abstract public class AbstractHttpApi implements HttpApi {
	protected static final Logger LOG = Logger.getLogger(AbstractHttpApi.class
			.getCanonicalName());
	protected static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	private static final String TAG = "AbstractHttpApi";
	private static final String ERROR_RESPONSE = "{\"code\":0,\"desc\":\"%s\"}";

	private static final String CLIENT_VERSION_HEADER = "User-Agent";
	private static final int TIMEOUT = 60;
	private static HttpContext _httpContext;
	private final DefaultHttpClient mHttpClient;
	private final String mClientVersion = "";
	private Context _context;

	public AbstractHttpApi(DefaultHttpClient httpClient, Context context) {
		mHttpClient = httpClient;
		_context = context;
	}

	@Override
	public <T extends BaseModel> T fetchModel(Parser<T> parser,
			HttpRequestBase request) {
		// TODO Auto-generated method stub
		return executeHttpRequest(request, parser);
	}

	public <T extends BaseModel> T executeHttpRequest(
			HttpRequestBase httpRequest, Parser<T> parser) {
		LoggerTool.d(TAG, "doHttpRequest: " + httpRequest.getURI());
		try {
			HttpResponse response = executeHttpRequest(httpRequest);

			LoggerTool.d(TAG, "executed HttpRequest for: "
					+ httpRequest.getURI().toString());

			int statusCode = response.getStatusLine().getStatusCode();
			String content;
			switch (statusCode) {
			case 200:

				content = EntityUtils.toString(response.getEntity());

				return parser.consume(content, _context);

			case 400:
				LoggerTool.d(TAG, "HTTP Code: 400");

				response.getEntity().consumeContent();
				content = String.format(ERROR_RESPONSE, _context.getResources()
						.getString(R.string.ServerExceptional));
				break;

			case 401:
				response.getEntity().consumeContent();
				LoggerTool.d(TAG, "HTTP Code: 401");
				content = String.format(ERROR_RESPONSE, _context.getResources()
						.getString(R.string.network_authentication_failed));
				break;

			case 404:
				response.getEntity().consumeContent();
				LoggerTool.d(TAG, "HTTP Code: 404");

				content = String.format(ERROR_RESPONSE, _context.getResources()
						.getString(R.string.ServerExceptional));
			case 500:
				response.getEntity().consumeContent();

				LoggerTool.d(TAG, "HTTP Code: 500");
				content = String.format(ERROR_RESPONSE, _context.getResources()
						.getString(R.string.ServerExceptional));

			default:
				LoggerTool.d(TAG, "Default case for status code reached: "
						+ response.getStatusLine().toString());
				response.getEntity().consumeContent();
				content = String.format(ERROR_RESPONSE, _context.getResources()
						.getString(R.string.NetworkExceptional));
			}
			return parser.consume(content, _context);

		} catch (SocketTimeoutException ex) {
			try {
				return parser.consume(String
						.format(ERROR_RESPONSE, _context.getResources()
								.getString(R.string.network_unavailable)),
						_context);
			} catch (NotFoundException e) {

			} catch (JSONException e) {

			}
		} catch (UnknownHostException ex) {
			try {
				return parser.consume(String
						.format(ERROR_RESPONSE, _context.getResources()
								.getString(R.string.network_unavailable)),
						_context);
			} catch (NotFoundException e) {

			} catch (JSONException e) {

			}
		} catch (Exception ex) {
			try {
				return parser.consume(
						String.format(ERROR_RESPONSE, _context.getResources()
								.getString(R.string.NetworkExceptional)),
						_context);
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {

			}

		}
		return null;
	}

	public String doHttpPost(String url, NameValuePair... nameValuePairs) {
		LoggerTool.d(TAG, "doHttpPost: " + url);
		HttpPost httpPost = createHttpPost(url, nameValuePairs);
		try {
			HttpResponse response = executeHttpRequest(httpPost);
			LoggerTool.d(TAG, "executed HttpRequest for: "
					+ httpPost.getURI().toString());

			switch (response.getStatusLine().getStatusCode()) {
			case 200:

				return EntityUtils.toString(response.getEntity());

			case 401:
				response.getEntity().consumeContent();
				return null;
			case 404:
				response.getEntity().consumeContent();
				return null;
			default:
				response.getEntity().consumeContent();
				return null;
			}
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * execute() an httpRequest catching exceptions and returning null instead.
	 * 
	 * @param httpRequest
	 * @return
	 * @throws IOException
	 */
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest)
			throws Exception {
		LoggerTool.d(TAG, "executing HttpRequest for: "
				+ httpRequest.getURI().toString());
		try {

			if (_httpContext == null) {
				_httpContext = new BasicHttpContext();
				_httpContext.setAttribute(ClientContext.COOKIE_STORE,
						PersistentCookieStore.getInstance(_context));
			}
			httpRequest.addHeader("Accept-Encoding", "gzip");
			mHttpClient.getConnectionManager().closeExpiredConnections();
			return mHttpClient.execute(httpRequest);
		} catch (Exception e) {
			httpRequest.abort();
			throw e;
		}
	}

	public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
		LoggerTool.d(TAG, "creating HttpGet for: " + url);
		String query = URLEncodedUtils.format(buildParams(nameValuePairs),
				HTTP.UTF_8);
		HttpGet httpGet = new HttpGet(url + "?" + query);

		LoggerTool.d(TAG, "Created: " + httpGet.getURI());
		return httpGet;
	}

	public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
		LoggerTool.d(TAG, "creating HttpPost for: " + url);
		HttpPost httpPost = new HttpPost(url);

		try {

			httpPost.setEntity(new UrlEncodedFormEntity(
					buildParams(nameValuePairs), HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalArgumentException(
					"Unable to encode http parameters.");
		}
		LoggerTool.d(TAG, "Created: " + httpPost);
		return httpPost;
	}

	public HttpPost createHttpPost(String url, Map<String, String> params) {
		LoggerTool.d(TAG, "creating HttpPost for: " + url);
		HttpPost httpPost = new HttpPost(url);

		try {

			httpPost.setEntity(new UrlEncodedFormEntity(buildParams(params),
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalArgumentException(
					"Unable to encode http parameters.");
		}
		LoggerTool.d(TAG, "Created: " + httpPost);
		return httpPost;
	}

	public HttpURLConnection createHttpURLConnectionPost(URL url,
			String boundary) {
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url.openConnection();

			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(TIMEOUT * 1000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty(CLIENT_VERSION_HEADER, mClientVersion);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			return conn;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	@Override
	public HttpPost createHttpPost(String url, String filePath, String filekey,
			NameValuePair... nameValuePairs) {
		LoggerTool.d(TAG, "creating HttpPost for: " + url);
		HttpPost httpPost = new HttpPost(url);
		try {
			List<NameValuePair> params = buildParams(nameValuePairs);
			MultipartEntity mutiEntity = new MultipartEntity();
			if (!TextUtils.isEmpty(filePath)) {
				File file = new File(filePath);
				if (file != null && file.exists()) {
					mutiEntity.addPart(filekey, new FileBody(file));
				}
			}
			for (NameValuePair parma : params) {
				mutiEntity.addPart(
						parma.getName(),
						new StringBody(parma.getValue(), Charset
								.forName("UTF-8")));
			}
			httpPost.setEntity(mutiEntity);
		} catch (Exception e1) {
			throw new IllegalArgumentException(
					"Unable to encode http parameters.");
		}
		LoggerTool.d(TAG, "Created: " + httpPost);
		return httpPost;

	}

	public InputStream fetchStream(HttpRequestBase httpRequest) {

		LoggerTool.d(TAG, "doHttpRequest: " + httpRequest.getURI());
		int statusCode = 0;
		HttpResponse response = null;
		try {
			response = executeHttpRequest(httpRequest);

			LoggerTool.d(TAG, "executed HttpRequest for: "
					+ httpRequest.getURI().toString());

			statusCode = response.getStatusLine().getStatusCode();

			LoggerTool.d(TAG, "HTTP Code: " + String.valueOf(statusCode));

			if (statusCode != HttpStatus.SC_OK) {
				// 释放
				if (response != null) {
					response.getEntity().consumeContent();
				}

				return null;
			}
			return response.getEntity().getContent();
		} catch (Exception e) {
			// e.printStackTrace();
			LoggerTool.e(TAG, "fetchStream occur Exception:" + e.toString());
			return null;
		}

	}

	private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (int i = 0; i < nameValuePairs.length; i++) {
			NameValuePair param = nameValuePairs[i];
			if (param.getValue() != null) {
				LoggerTool.d(TAG, "Param name: " + param.getName()
						+ " value : " + param.getValue());
				params.add(param);
			}
		}
		return params;
	}

	private List<NameValuePair> stripNulls(Map<String, String> extraQuery) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Entry<String, String> queryItem : extraQuery.entrySet()) {
			String key = queryItem.getKey();
			String value = queryItem.getValue();
			if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(key)) {
				BasicNameValuePair param = new BasicNameValuePair(key, value);
				LoggerTool.d(TAG, String.format("%s=%s", key, value));
				params.add(param);
			}
		}
		return params;
	}

	private List<NameValuePair> buildParams(NameValuePair... nameValuePairs) {
		List<NameValuePair> commonParams = buildCommonParams();
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		for (int index = 0; index < commonParams.size(); index++) {
			NameValuePair param = commonParams.get(index);
			if (!TextUtils.isEmpty(param.getValue())) {
				LoggerTool.d(
						TAG,
						String.format("%s=%s", param.getName(),
								param.getValue()));
				requestParams.add(param);
			}
		}
		requestParams.addAll(stripNulls(nameValuePairs));

		String sid = Method.getSid(_context, requestParams);
		LoggerTool.d(TAG, "sid :" + sid);
		requestParams.add(new BasicNameValuePair(
				DataConstants.HTTP_PARAM_NAME_SID, sid));
		return requestParams;
	}

	private List<NameValuePair> buildParams(Map<String, String> extraQuery) {
		List<NameValuePair> commonParams = buildCommonParams();
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
		for (int index = 0; index < commonParams.size(); index++) {
			NameValuePair param = commonParams.get(index);
			if (!TextUtils.isEmpty(param.getValue())) {
				LoggerTool.d(
						TAG,
						String.format("%s=%s", param.getName(),
								param.getValue()));
				requestParams.add(param);
			}
		}
		requestParams.addAll(stripNulls(extraQuery));

		String sid = Method.getSid(_context, requestParams);
		LoggerTool.d(TAG, "sid :" + sid);
		requestParams.add(new BasicNameValuePair(
				DataConstants.HTTP_PARAM_NAME_SID, sid));
		return requestParams;
	}

	private List<NameValuePair> buildCommonParams() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_UID, Method
				.getUid(_context)));
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_CVER,
				DataConstants.CVER));
		try {
			params.add(new BasicNameValuePair(
					DataConstants.HTTP_PARAM_PLATFORM, java.net.URLEncoder
							.encode(android.os.Build.MODEL + "", "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_DVERC,
				Method.getDverc(_context)));
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_DVERT,
				Method.getDvert(_context)));
		User user = Preferences.getUserCommonInfo(_context);
		if (user != null && !TextUtils.isEmpty(user.getId())) {
			params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_USERID,
					user.getId()));
		}
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_IMEI,
				Method.getImei(_context)));
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_SOURCE,
				Method.getSource(_context)));
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_TIMESTAMP,
				Method.getTimeStamp(_context)));
		params.add(new BasicNameValuePair(DataConstants.HTTP_PARAM_ISZH, Method
				.getIszh(_context)));
		return params;
	}

	/**
	 * Create a thread-safe client. This client does not do redirecting, to
	 * allow us to capture correct "error" codes.
	 * 
	 * @return HttpClient
	 */
	public static final DefaultHttpClient createHttpClient(Context context) {
		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		// Set some client http client parameter defaults.
		final HttpParams httpParams = createHttpParams(context);
		HttpClientParams.setRedirecting(httpParams, false);

		final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
				httpParams, supportedSchemes);
		return new DefaultHttpClient(ccm, httpParams);
	}

	/**
	 * Create the default HTTP protocol parameters.
	 */
	private static final HttpParams createHttpParams(Context context) {
		final HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connMgr != null) {
			NetworkInfo info = connMgr.getActiveNetworkInfo();
			if (info != null) {
				try {
					if (info.getType() != ConnectivityManager.TYPE_WIFI) {
						LoggerTool.v("AbstractHttpApi", "info.getType():"
								+ info.getType());
						String proxyHost = android.net.Proxy.getDefaultHost();
						int proxyPort = android.net.Proxy.getDefaultPort();
						if (!TextUtils.isEmpty(proxyHost) && proxyPort != 0
								&& proxyPort != -1) {
							LoggerTool.v("AbstractHttpApi",
									"proxyHost proxyPort" + proxyHost + " "
											+ proxyPort);
							final HttpHost proxy = new HttpHost(proxyHost,
									proxyPort, "http");
							params.setParameter(ConnRoutePNames.DEFAULT_PROXY,
									proxy);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}

	protected static HttpResponseInterceptor gzipResponseIntercepter = new HttpResponseInterceptor() {

		@Override
		public void process(HttpResponse response, HttpContext context)
				throws org.apache.http.HttpException, IOException {
			HttpEntity entity = response.getEntity();
			Header ceheader = entity.getContentEncoding();
			if (ceheader != null) {
				HeaderElement[] codecs = ceheader.getElements();
				for (int i = 0; i < codecs.length; i++) {
					if (codecs[i].getName().equalsIgnoreCase("gzip")) {
						response.setEntity(new GzipDecompressingEntity(response
								.getEntity()));
						return;
					}
				}
			}

		}
	};

	static class GzipDecompressingEntity extends HttpEntityWrapper {

		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		@Override
		public InputStream getContent() throws IOException,
				IllegalStateException {

			// the wrapped entity's getContent() decides about repeatability
			InputStream wrappedin = wrappedEntity.getContent();
			return new GZIPInputStream(wrappedin);
		}

		@Override
		public long getContentLength() {
			// length of ungzipped content is not known
			return -1;
		}

	}

}
