package com.x8.http;

import com.x8.user.ChangeState;

import android.os.AsyncTask;

public class ConnetToService extends AsyncTask<String, Void, String>{
	
	private ChangeState objcet;
	public ConnetToService(ChangeState objcet){
		this.objcet = objcet;
	}

	@Override
	protected String doInBackground(String... params) {
		
		ConnetInterface connet = null;
		String result = "";
		
		String type = params[2];
		
		if(type.equals(ConnetPramsBean.POST)){
			connet = new ConnetByPost();
		} else if(type.equals(ConnetPramsBean.GET)){
			connet = new ConnetByGet();
		}
		result = connet.connet(params[0], params[1]);
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		objcet.doNext(result);
	}
	
}
