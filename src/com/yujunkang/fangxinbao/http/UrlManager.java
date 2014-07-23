package com.yujunkang.fangxinbao.http;



public class UrlManager {
	static final String TAG = "UrlManager";
	
	/**
	 * 3.8.3判断证件号是否有成功支付的纪录
	 */
	public final static int URL_IS_PAY_ORDER = 964; // 判断证件号是否有成功支付的纪录
	/**
	 *  获取国家数据
	 */
	public final static String URL_FETCH_COMMONDATA = "800"; // 获取国家数据

	
	/**
	 *  获取手机验证码
	 */
	public final static String URL_FETCH_VERIFYCODE = "300"; // 获取手机验证码

	/**
	 *  短信验证码验证
	 */
	public final static String URL_CONFIRM_VERIFYCODE = "1000"; // 短信验证码验证
	
	/**
	 *  用户注册
	 */
	public final static String URL_REGISTER = "200"; // 短信验证码验证
	/**
	 *  登录
	 */
	public final static String URL_LOGIN = "100"; // 登录
	/**
	 *  重置密码
	 */
	public final static String URL_RESET_PASSWORD = "500"; // 登录
	
	/**
	 *  修改宝宝数据
	 */
	public final static String URL_MODIFY_BABY_INFO = "1300"; // 修改宝宝数据
	
	/**
	 *  获取宝宝信息
	 */
	public final static String URL_FETCH_BABY_INFO = "1100"; // 获取宝宝信息
	/**
	 *  获取用户数据
	 */
	public final static String URL_FETCH_USER_INFO = "1200"; // 获取用户数据
	/**
	 *  修改密码 
	 */
	public final static String URL_MODIFY_PASSWORD = "600";
	/**
	 *  绑定邮箱
	 */
	public final static String URL_MODIFY_EMAIL = "1800";
	/**
	 *  添加宝宝
	 */
	public final static String URL_ADD_BABY = "400";
	
	/**
	 *  提交意见反馈
	 */
	public final static String URL_FEEDBACK = "2700";
	/**
	 *  获取最近24小时温度数据
	 */
	public final static String URL_FETCH_RECENT_TEMPERATURE_DATA = "1700";
	/**
	 *  添加备忘录
	 */
	public final static String URL_ADD_DAY_TIP = "2300";
	/**
	 *  获取备忘录
	 */
	public final static String URL_GET_DAY_TIP = "2400";
	/**
	 *  编辑备忘录
	 */
	public final static String URL_EDIT_DAY_TIP = "2500";
	/**
	 *  获取某天温度数据
	 */
	public final static String URL_FETCH_DAY_TEMPERATURE_DATA = "1600";
}
