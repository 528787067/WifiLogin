package com.x8.user;

import java.util.HashMap;

import com.x8.http.ConnetPramsBean;
import com.x8.http.ConnetToService;
import com.x8.params.AllParams;
import com.x8.params.AllUrl;
import com.x8.params.LoginCode;
import com.x8.params.ParamsBean;
import com.x8.ui.UpdataUI;

public class Logout implements ChangeState{
	
	private static ParamsBean paramsBean;	
	public Logout(ParamsBean params){
		paramsBean = params;
	}
	
	public void logout() {
		
		String timeStamp = paramsBean.getTime();
		String md5 = paramsBean.getMD5(paramsBean.getClientIp() 
										+ paramsBean.getNasIp() 
										+ paramsBean.getMac() 
										+ timeStamp
									);
		String params = "{"
				+ "\"" + AllParams.wlanUserIp + "\":\"" + paramsBean.getClientIp() + "\","
				+ "\"" + AllParams.wlanAcIp   + "\":\"" + paramsBean.getNasIp()    + "\","
				+ "\"" + AllParams.mac        + "\":\"" + paramsBean.getMac()      + "\","
				+ "\"" + AllParams.timeStamp  + "\":\"" + timeStamp                + "\","
				+ "\"" + AllParams.md5        + "\":\"" + md5                      + "\""
				+ "}";
		
		new ConnetToService(this).execute(AllUrl.logout, params, ConnetPramsBean.POST);
	}
	
	@Override
	public void doNext(String data) {
		if(data != null){
			String params[] = data.split(",");
			
			HashMap<String, String> map = new HashMap<String, String>();
			
			for(String param : params){
				String key = param.split(":")[0].split("\"")[1];
				String value = param.split(":")[1].split("\"")[1];
				map.put(key, value);
			}
			
			if(map.get("rescode") != null && map.get("rescode").equals("0")){
				// 提示登陆成功
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.LOGOUT_SUCCESS);
			} else{
				// 提示登陆失败
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.LOGOUT_FAIL);
			}
		} else{
			// 提示登陆失败
			UpdataUI updata = UpdataUI.getUpdata();
			updata.updataMainUI(LoginCode.LOGOUT_FAIL);
		}
	}

}
