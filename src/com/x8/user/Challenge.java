package com.x8.user;

import java.util.HashMap;

import com.x8.http.ConnetPramsBean;
import com.x8.http.ConnetToService;
import com.x8.params.AllParams;
import com.x8.params.AllUrl;
import com.x8.params.LoginCode;
import com.x8.params.ParamsBean;
import com.x8.ui.UpdataUI;

public class Challenge implements ChangeState {

	private static ParamsBean paramsBean;

	@SuppressWarnings("static-access")
	public Challenge(ParamsBean params) {
		this.paramsBean = params;
	}

	/* 测试用是否合法 */
	public void challenge() {
		String timeStamp = paramsBean.getTime();
		String md5 = paramsBean.getMD5(paramsBean.getClientIp() 
										+ paramsBean.getNasIp() 
										+ paramsBean.getMac() 
										+ timeStamp
									);
		String params = "{"
				+ "\"" + AllParams.userName   + "\":\"" + paramsBean.getUserName() + "\","
				+ "\"" + AllParams.wlanUserIp + "\":\"" + paramsBean.getClientIp() + "\","
				+ "\"" + AllParams.wlanAcIp   + "\":\"" + paramsBean.getNasIp()    + "\","
				+ "\"" + AllParams.mac        + "\":\"" + paramsBean.getMac()      + "\","
				+ "\"" + AllParams.timeStamp  + "\":\"" + timeStamp                + "\","
				+ "\"" + AllParams.md5        + "\":\"" + md5                      + "\""
				+ "}";
	
		new ConnetToService(this).execute(AllUrl.challenge, params, ConnetPramsBean.POST);
	}

	

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
				paramsBean.setChallengeCode(map.get("challenge"));
				new Login(paramsBean).login();
			} else{
				// 提示用户出错
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.USER_ERROR);
			}
		} else{
			// 提示用户出错
			UpdataUI updata = UpdataUI.getUpdata();
			updata.updataMainUI(LoginCode.USER_ERROR);
		}
	}

}
