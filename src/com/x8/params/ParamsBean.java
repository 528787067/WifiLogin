package com.x8.params;

import com.x8.tools.MD5;

public class ParamsBean {
	
	private String username;
	private String password;
	private String mac;
	private String clientip;	// wlan user ip
	private String nasip;		// wlan ac   ip
	private String iswifi;
	private String challengeCode;
	
	private boolean isPhoneMode;
	
	private static ParamsBean params;
	
	public static String PHONE_CODE = "4060";
	public static String WIFI_CODE = "1050";
	//public static String WIFI_CODE = "4060";
	
	private ParamsBean(){
		username = "zjlanhdxs000000kk";
		password = "00000000";
		mac = "00-00-00-00-00-00";
		clientip = "0.0.0.0";
		nasip = "0.0.0.0";
		iswifi = WIFI_CODE;
		challengeCode = "0";
		
		isPhoneMode = false;
	}
	
	public static synchronized ParamsBean getParamsBean(){		
		if(params == null)
			params = new ParamsBean();
		return params;
	}
	
	public void setUserName(String userName){
		this.username = userName;
	}
	public void setPassword(String password){
		this.password = password;
	}
	public void setMac(String mac){
		this.mac = mac;
	}
	public void setClientIp(String clientip){
		this.clientip = clientip;
	}
	public void setNasIp(String nasip){
		this.nasip = nasip;
	}
	public void setPhone(Boolean isPhone){		
		if(isPhone)
			this.iswifi = PHONE_CODE;
		else
			this.iswifi = WIFI_CODE;
		
		isPhoneMode = isPhone;
	}
	public void setChallengeCode(String challengeCode){
		this.challengeCode = challengeCode;
	}
	
	public String getUserName(){
		return this.username;
	}
	public String getPassword(){
		return this.password;
	}
	public String getMac(){
		return this.mac;
	}
	public String getClientIp(){
		return this.clientip;
	}
	public String getNasIp(){
		return this.nasip;
	}
	public String getPhone(){
		return this.iswifi;
	}
	public Boolean isPhone(){
		/*
		if(this.iswifi.equals(WIFI_CODE))
			return false;
		else
			return true;
		*/
		return isPhoneMode;
	}
	public String getMD5(String str){
		return new MD5().MD5_32(str + "Eshore!@#");
	}
	public String getTime(){
		return System.currentTimeMillis()+"";
	}
	public String getGhallengeCode(){
		return this.challengeCode;
	}
}

















