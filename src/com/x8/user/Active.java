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
			if(!NetworkDetector.isWifi(params[0]))					// ��ǰ���粻��WiFi
				return LoginCode.NOT_WIFI;
			if(!NetworkDetector.ping(AllUrl.pingUrl))				// ���粻����
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
			if(result.equals(LoginCode.NOT_WIFI)){					// �������Ӳ���WiFi
				updata.updataMainUI(LoginCode.NOT_WIFI);
			} else if(result.equals(LoginCode.CAN_NOT_CONNET)){		// ���粻����
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
				// �û�����
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.IS_ACTIVE);
			} else if(map.get("rescode") != null && map.get("rescode").equals("1")){
				// �û�������
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.NO_ACTIVE);
			} else if(map.get("rescode") != null && map.get("rescode").equals("2")){
				// У��ʧ��
				UpdataUI updata = UpdataUI.getUpdata();
				updata.updataMainUI(LoginCode.FAIL_ACTIVE);
			}
		} else{
			// ��ʾ��½ʧ��
			UpdataUI updata = UpdataUI.getUpdata();
			updata.updataMainUI(LoginCode.FAIL_ACTIVE);
		}
	}

}
