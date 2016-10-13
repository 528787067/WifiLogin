package com.x8.http;

import com.x8.user.ChangeState;

public class ConnetPramsBean {
	
	public static final String POST = "post";
	public static final String GET = "get";
	
	private ChangeState object;
	private String url;
	private String type;
	
	public ConnetPramsBean(ChangeState object, String url, String type){
		this.object = object;
		this.url = url;
		this.type = type;
	}
	
	public void setObject(ChangeState object){
		this.object = object;
	}
	public void setUrl(String url){
		this.url = url;
	}
	public void setType(String type){
		this.type = type;
	}
	
	public ChangeState getObject(){
		return object;
	}
	public String getUrl(){
		return url;
	}
	public String getType(){
		return type;
	}
}
