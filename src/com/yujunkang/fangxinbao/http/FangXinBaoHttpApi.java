package com.yujunkang.fangxinbao.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.TextUtils;

import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.execption.FangXinBaoCredentialsException;
import com.yujunkang.fangxinbao.execption.FangXinBaoException;
import com.yujunkang.fangxinbao.execption.FangXinBaoParseException;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.BabyParser;
import com.yujunkang.fangxinbao.parser.BaseDataParser;
import com.yujunkang.fangxinbao.parser.CommonDataParser;
import com.yujunkang.fangxinbao.parser.Parser;
import com.yujunkang.fangxinbao.parser.UserParser;
import com.yujunkang.fangxinbao.utility.DataConstants;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class FangXinBaoHttpApi {
	private static final Logger LOG = Logger.getLogger(FangXinBaoHttpApi.class
			.getCanonicalName());
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;

	private static String mDomain = null;
	private DefaultHttpClient mHttpClient = null;
	private HttpApi mHttpApi;

	private UrlManager mUrlManager;
	private final AuthScope mAuthScope;

	public FangXinBaoHttpApi(String domain, Context context, boolean useOAuth) {

		mDomain = domain;
		mAuthScope = new AuthScope(domain, 80);
		mHttpClient = AbstractHttpApi.createHttpClient(context);
		if (useOAuth) {
			mHttpApi = new HttpApiWithOAuth(mHttpClient, context);
		} else {
			mHttpApi = new HttpApiWithBasicAuth(mHttpClient, context);
		}
	}

	public void setCredentials(String phone, String password) {
		if (phone == null || phone.length() == 0 || password == null
				|| password.length() == 0) {
			if (DEBUG)
				LOG.log(Level.FINE, "Clearing Credentials");
			mHttpClient.getCredentialsProvider().clear();
		} else {
			if (DEBUG)
				LOG.log(Level.FINE, "Setting Phone/Password: " + phone
						+ "/******");
			mHttpClient.getCredentialsProvider().setCredentials(mAuthScope,
					new UsernamePasswordCredentials(phone, password));
		}
	}

	public boolean hasLoginAndPassword() {
		return hasCredentials();
	}

	public boolean hasCredentials() {
		return mHttpClient.getCredentialsProvider().getCredentials(mAuthScope) != null;
	}

	public static final FangXinBaoHttpApi createHttpApi(String domain,
			Context context, boolean useOAuth) {
		return new FangXinBaoHttpApi(domain, context, useOAuth);
	}

	public void setOAuthConsumerCredentials(String oAuthConsumerKey,
			String oAuthConsumerSecret) {
		if (DEBUG) {
			LOG.log(Level.FINE, "Setting consumer key/secret: "
					+ oAuthConsumerKey + " " + oAuthConsumerSecret);
		}
		((HttpApiWithOAuth) mHttpApi).setOAuthConsumerCredentials(
				oAuthConsumerKey, oAuthConsumerSecret);
	}

	public void setOAuthTokenWithSecret(String token, String secret) {
		if (DEBUG)
			LOG.log(Level.FINE, "Setting oauth token/secret: " + token + " "
					+ secret);
		((HttpApiWithOAuth) mHttpApi).setOAuthTokenWithSecret(token, secret);
	}

	public boolean hasOAuthTokenWithSecret() {
		return ((HttpApiWithOAuth) mHttpApi).hasOAuthTokenWithSecret();
	}

	/**
	 * 获取国家基础数据
	 * 
	 * @return
	 */
	public CommonData fetchCommonData() {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(),
				new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_FETCH_COMMONDATA));
		return (CommonData) mHttpApi.doHttpRequest(httpPost,
				new CommonDataParser());
	}

	/**
	 * 获取验证码
	 * 
	 * @return
	 */
	public BaseData fetchVerifyCode(String phone, String countrycode,
			String type) {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(),
				new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_FETCH_VERIFYCODE),
				new BasicNameValuePair("phone", phone), new BasicNameValuePair(
						"type", type), new BasicNameValuePair("nationality",
						countrycode));
		return (BaseData) mHttpApi
				.doHttpRequest(httpPost, new BaseDataParser());
	}

	/**
	 * 短信验证
	 * 
	 * @return
	 */
	public BaseData confirmVerifyCode(String verifycode, String phone,
			String countrycode) {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(),
				new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_CONFIRM_VERIFYCODE),
				new BasicNameValuePair("phone", phone), new BasicNameValuePair(
						"nationality", countrycode), new BasicNameValuePair(
						"verifycode", verifycode));
		return (BaseData) mHttpApi
				.doHttpRequest(httpPost, new BaseDataParser());
	}

	/**
	 * 注册
	 * 
	 * @return
	 */
	public User register(String phone, String email, String password,
			String nationality, String nickname, String born, String filePath,
			String sex) {

		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(), filePath,
				"photo", new BasicNameValuePair(
						DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_REGISTER), new BasicNameValuePair(
						"phone", phone),
				new BasicNameValuePair("email", email), new BasicNameValuePair(
						"password", password), new BasicNameValuePair(
						"nationality", nationality), new BasicNameValuePair(
						"nickname", nickname), new BasicNameValuePair("born",
						born), new BasicNameValuePair("sex", sex));

		return (User) mHttpApi.doHttpRequest(httpPost, new UserParser());
	}

	/**
	 * 修改宝宝图像
	 * 
	 * @return
	 */
	public Baby modifyBabyPhoto(String filePath, String babyid) {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(), filePath,
				"photo", new BasicNameValuePair(
						DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_MODIFY_BABY_INFO),
				new BasicNameValuePair("bbid", babyid));
		return (Baby) mHttpApi.doHttpRequest(httpPost, new BabyParser());
	}

	/**
	 * 添加宝宝信息
	 * 
	 * @return
	 */
	public Baby addBabyInfo(String nickname, String born, String sex,
			String filePath) {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(), filePath,
				"photo", 
				new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_PID,UrlManager.URL_ADD_BABY), 
				new BasicNameValuePair("nickname", nickname),
				new BasicNameValuePair("born", born),
				new BasicNameValuePair("sex", sex));
		return (Baby) mHttpApi.doHttpRequest(httpPost, new BabyParser());
	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	public User Login(String phone, String email, String password,
			String nationality) {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(),
				new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_LOGIN), new BasicNameValuePair("phone",
						phone), new BasicNameValuePair("email", email),
				new BasicNameValuePair("password", password),
				new BasicNameValuePair("nationality", nationality));
		return (User) mHttpApi.doHttpRequest(httpPost, new UserParser());
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public User user() {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(),
				new BasicNameValuePair(DataConstants.HTTP_PARAM_NAME_PID,
						UrlManager.URL_FETCH_USER_INFO));
		return (User) mHttpApi.doHttpRequest(httpPost, new UserParser());
	}

	/**
	 * 通用协议
	 * 
	 * @return
	 */
	public <T extends BaseModel> T fetchModel(Map<String, String> query,
			Parser<T> parser) {
		HttpPost httpPost = mHttpApi.createHttpPost(getNormalUrl(), query);
		return mHttpApi.fetchModel(parser, httpPost);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public InputStream fetchStream(String url) {
		HttpGet httpget = new HttpGet(url);
		return mHttpApi.fetchStream(httpget);
	}

	/**
	 * 防止2级域名有改变
	 * 
	 * @param path
	 * @return
	 */
	private String fullUrl(String path) {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(mDomain);
		sbUrl.append(path);
		return sbUrl.toString();
	}

	private String getNormalUrl() {
		return fullUrl("");
	}

}
