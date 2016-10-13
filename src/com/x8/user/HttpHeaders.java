package com.x8.user;

import java.net.HttpURLConnection;
import java.net.URL;

import com.x8.params.AllUrl;
import com.x8.params.LoginCode;
import com.x8.params.ParamsBean;
import com.x8.tools.HttpUrlParams;
import com.x8.ui.UpdataUI;

import android.os.AsyncTask;

public class HttpHeaders {
	public void getHttpLocation(){
		new Task().execute(AllUrl.testUrl);
	}
	
	public void doNext(String data){
		if(data != null && data != "" && data != LoginCode.CAN_NOT_CONNET){
			ParamsBean paramsBean = new HttpUrlParams().getParams(data);
			new Challenge(paramsBean).challenge();
		} else{// if(data.equals("") || data == null)
			// 提示已经联网
			UpdataUI updata = UpdataUI.getUpdata();
			updata.updataMainUI(LoginCode.CONNETED);
		}
	}
	
	private class Task extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			String location = "";
			try{
				URL serverUrl = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setInstanceFollowRedirects(false);
				conn.connect();
				location = conn.getHeaderField("Location");
			} catch(Exception e){
				e.printStackTrace();
			}
			return location;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			doNext(result);
		}
	}
	
}
