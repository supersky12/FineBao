package com.yujunkang.fangxinbao.http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.content.FileBody;

import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.parser.Parser;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public interface HttpApi {
	abstract public BaseModel doHttpRequest(HttpRequestBase httpRequest,
			Parser<? extends BaseModel> parser);

	abstract public String doHttpPost(String url,
			NameValuePair... nameValuePairs);

	abstract public HttpGet createHttpGet(String url,
			NameValuePair... nameValuePairs);

	abstract public HttpPost createHttpPost(String url,
			NameValuePair... nameValuePairs);

	abstract public HttpPost createHttpPost(String url,
			Map<String, String> params);

	abstract public <T extends BaseModel> T fetchModel(Parser<T> parser,
			HttpRequestBase request);

	abstract public HttpURLConnection createHttpURLConnectionPost(URL url,
			String boundary);

	abstract public HttpPost createHttpPost(String url, String filePath,
			String filekey, NameValuePair... nameValuePairs);

	 abstract public InputStream fetchStream(HttpRequestBase httpRequest);
}
