package com.x8.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserData {
	
	private static final String spName = "userData";

	private static UserData saveData;
	private static SharedPreferences sp;
	private static Editor editor;
	
	@SuppressWarnings("static-access")
	private UserData(Context context){
		sp = context.getSharedPreferences(spName, context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public synchronized static UserData getData(Context context){
		if(saveData == null){
			saveData = new UserData(context);
		}	
		return saveData;
	}
	public void commit(){
		editor.commit();
	}
	public void put(String key, String value){
		editor.putString(key, value);
		//editor.commit();
	}
	public String get(String key){
		return sp.getString(key, "");
	}
}
