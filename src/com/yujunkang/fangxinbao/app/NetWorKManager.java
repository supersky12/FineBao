package com.yujunkang.fangxinbao.app;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;

import com.yujunkang.fangxinbao.http.FangXinBaoHttpApi;
import com.yujunkang.fangxinbao.model.Baby;
import com.yujunkang.fangxinbao.model.BaseData;
import com.yujunkang.fangxinbao.model.BaseModel;
import com.yujunkang.fangxinbao.model.CommonData;
import com.yujunkang.fangxinbao.model.User;
import com.yujunkang.fangxinbao.parser.Parser;

/**
 * 
 * @date 2014-5-17
 * @author xieb
 * 
 */
public class NetWorKManager {
	private static final String TAG = "NetWorKManager";
	public static final boolean DEBUG = true;
	public static final boolean PARSER_DEBUG = false;

	public static final String FOURSQUARE_API_DOMAIN = "api.foursquare.com";

	public static final String MALE = "male";
	public static final String FEMALE = "female";

	private String mAccount;
	private String mPassword;
	private FangXinBaoHttpApi mFangXinBaoHttpApi;

	@V1
	public NetWorKManager(FangXinBaoHttpApi httpApi) {
		mFangXinBaoHttpApi = httpApi;
	}

	public void setCredentials(String account, String password) {
		mAccount = account;
		mPassword = password;
		mFangXinBaoHttpApi.setCredentials(account, password);
	}

	@V1
	public void setOAuthToken(String token, String secret) {
		mFangXinBaoHttpApi.setOAuthTokenWithSecret(token, secret);
	}

	@V1
	public void setOAuthConsumerCredentials(String oAuthConsumerKey,
			String oAuthConsumerSecret) {
		mFangXinBaoHttpApi.setOAuthConsumerCredentials(oAuthConsumerKey,
				oAuthConsumerSecret);
	}

	public void clearAllCredentials() {
		setCredentials(null, null);
		setOAuthToken(null, null);
	}

	@V1
	public boolean hasCredentials() {
		return mFangXinBaoHttpApi.hasCredentials()
				&& mFangXinBaoHttpApi.hasOAuthTokenWithSecret();
	}

	@V1
	public boolean hasLoginAndPassword() {
		return mFangXinBaoHttpApi.hasCredentials();
	}

	
	
	// public User user()
	// {
	//
	// }
	/**
	 * This api is supported in the V1 API documented at:
	 * http://groups.google.com/group/foursquare-api/web/api-documentation
	 */
	@interface V1 {
	}

	public CommonData fetchCommonData() {
		return mFangXinBaoHttpApi.fetchCommonData();
	}

	public BaseData fetchVerifyCode(String phone, String countrycode,
			String type) {
		return mFangXinBaoHttpApi.fetchVerifyCode(phone, countrycode, type);
	}

	public BaseData confirmVerifyCode(String verifycode, String phone,
			String countrycode) {
		return mFangXinBaoHttpApi.confirmVerifyCode(verifycode, phone,
				countrycode);
	}

	public User register(String phone, String email, String password,
			String nationality, String nickname, String born, String filePath,
			String sex) {
		return mFangXinBaoHttpApi.register(phone, email, password, nationality,
				nickname, born, filePath, sex);
	}

	public User Login(String phone, String email, String password,
			String nationality) {
		return mFangXinBaoHttpApi.Login(phone, email, password, nationality);
	}

	public User user() {
		return mFangXinBaoHttpApi.user();
	}

	public Baby modifyBabyPhoto(String filePath, String babyid) {
		return mFangXinBaoHttpApi.modifyBabyPhoto(filePath,babyid);
	}

	
	public Baby addBabyInfo(String nickname, String born, String sex,
			String filePath) {
		return mFangXinBaoHttpApi.addBabyInfo(nickname,born,sex,filePath);
	}
	
	public InputStream fetchStream(String url) {
		return mFangXinBaoHttpApi.fetchStream(url);
	}
	
	
	/**
	 * 请求
	 * 
	 * @return
	 */
	public <T extends BaseModel> T fetchModel(Map<String, String> query,
			Parser<T> parser) {
		return mFangXinBaoHttpApi.fetchModel(query, parser);
	}

	/**
	 * This api call requires a location.
	 */
	@interface LocationRequired {
	}

}
