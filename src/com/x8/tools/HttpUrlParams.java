package com.x8.tools;

import com.x8.params.ParamsBean;

public class HttpUrlParams {
	public ParamsBean getParams(String url){

		ParamsBean paramBean = ParamsBean.getParamsBean();
		String params[] = url.split("\\?")[1].split("&");
		for(String param : params){
			if(param.split("=")[0].equals("wlanuserip")){
				paramBean.setClientIp(param.split("=")[1]);
			} else if(param.split("=")[0].equals("wlanacip")){
				paramBean.setNasIp(param.split("=")[1]);
			}
		}
		
		return paramBean;
	}
}
