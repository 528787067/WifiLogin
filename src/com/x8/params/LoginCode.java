package com.x8.params;

public class LoginCode {
	public static final String NOT_WIFI = "notWifi";			// 网络连接不是WiFi
	public static final String NO_NETWORK = "noNetwork";		// 网络不可用
	public static final String CONNETED = "conneted";			// 已经连接
	public static final String CAN_NOT_CONNET = "canNotConnet";	// 无法连接服务器
	public static final String USER_ERROR = "userError";		// 用户出错
	public static final String LOGIN_FAIL = "loginFail";		// 登陆失败
	public static final String LOGIN_SUCCESS = "loginSuccess";	// 登陆成功
	
	public static final String LOGOUT_SUCCESS = "logoutSuccess";// 下线成功
	public static final String LOGOUT_FAIL = "logoutFail";		// 下线失败
	public static final String IS_ACTIVE = "isActive";			// 当前用户在线
	public static final String NO_ACTIVE = "noActive";			// 当前用户不在线
	public static final String FAIL_ACTIVE = "failActive";		// 当前用户不在线
	
	public static final String NOT_WIFI_MSG = "当前网络不是 WIFI，请连上需要登录的 WIFI 后重试";
	public static final String NO_NETWORK_MSG = "当前网络不可用，请连接需要登陆的路由后重试";
	public static final String CONNETED_MSG = "当前的网络无需登陆，请切换到需要登录的 WIFI";
	public static final String CAN_NOT_CONNET_MSG = "无法访问服务器，请确定 WIFI 动态 IP 是否已更新";
	public static final String USER_ERROR_MSG = "登陆失败，该账号不可用或 MAC 输入错误，请检测是否输入错误";
	public static final String LOGIN_FAIL_MSG = "登陆失败，请检查用户密码是否出错";
	public static final String LOGIN_SUCCESS_MSG = "登陆成功";
	public static final String OUT_OF_CONNET = "您的账户已近掉线，请重新登陆";
	public static final String OUT_OF_WIFI = "您的 WIFI 已断开，请重新连接";
	
	public static final String LOGOUT_SUCCESS_MSG = "下线成功";
	public static final String LOGOUT_FAIL_MSG = "下线失败，请稍后再试";
	
	public static final String WIFI_NOT_CHINANET_MSG = "手机登陆模式，请手机连接上 ChinaNet-goud 后再试";
}
