package com.x8.user;

import java.util.HashMap;

import com.x8.http.ConnetPramsBean;
import com.x8.http.ConnetToService;
import com.x8.params.AllParams;
import com.x8.params.AllUrl;
import com.x8.params.LoginCode;
import com.x8.params.ParamsBean;
import com.x8.tools.NetworkDetector;
import com.x8.ui.UpdataUI;

import android.content.Context;
import android.os.AsyncTask;

public class Active implements ChangeState{

	
	private static ParamsBean paramsBean;
	Context context;
	
	public Active(ParamsBean params){
		paramsBean = params;
	}
	
	public void active(Context context){
		this.context = context;
		new Task().execute(context);
	}
	
	private class Task extends AsyncTask<Context, Void, String>{

		@Override
		protected String doInBackground(Context... params) {
			if(!NetworkDetector.isWifi(params[0]))					// 当前网络不是WiFi
				return LoginCode.NOT_WIFI;
			if(!NetworkDetector.ping(AllUrl.pingUrl))				// 网络不可用
				return LoginCode.CAN_NOT_CONNET;
			testActive();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(result == null || result.equals(""))
				return;
			UpdataUI updata = UpdataUI.getUpdata();
			if(result.equals(LoginCode.NOT_WIFI)){					// 网络连接不是WiFi
				updata.updataMainUI(LoginCode.NOT_WIFI);
			} else if(result.equals(LoginCode.CAN_NOT_CONNET)){		// 网络不可用
				updata.updataMainUI(LoginCode.CAN_NOT_CONNET);
			}
		}
		
	}
	
	private void testActive() {
		String timeStamp = paramsBean.getTime();
		String md5 = paramsBean.getMD5(paramsBean.getClientIp() 
										+ paramsBean.getNasIp() 
										+ paramsBean.getMac() 
										+ timeStamp
										+ paramsBean.getGhallengeCode()
									);
		String params = AllParams.userName   + "=" + paramsBean.getUserName() + "&"
					  + AllParams.wlanUserIp + "=" + paramsBean.getClientIp() + "&"
					  + AllParams.wlanAcIp   + "=" + paramsBean.getNasIp()    + "&"
					  + AllParams.mac        + "=" + paramsBean.getMac()      + "&"
					  + AllParams.timeStamp  + "=" + timeStamp                + "&"
					  + AllParams.md5        + "=" + md5;
		new ConnetToService(this).execute(AllUrl.active, params, ConnetPramsBean.GET);
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
				// 用户在线
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.IS_ACTIVE);
			} else if(map.get("rescode") != null && map.get("rescode").equals("1")){
				// 用户不在线
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.NO_ACTIVE);
			} else if(map.get("rescode") != null && map.get("rescode").equals("2")){
				// 校验失败
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.FAIL_ACTIVE);
			}
		} else{
			// 提示登陆失败
			UpdataUI updata = UpdataUI.getUpdata();
			updata.updataMainUI(LoginCode.FAIL_ACTIVE);
		}
	}

}
