package com.x8.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkDetector {

	/* 判断网络是否可用 */
	public static boolean detect(Activity act) {

		ConnectivityManager manager = (ConnectivityManager) act.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;
	}

	/* 判断当前网络是否是 WiFi */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if(info == null)											// 无网络连接
			return false;
		else if(info.getType() != ConnectivityManager.TYPE_WIFI)	// 网络连接不是 WiFi
			return false;
		else
			return true;
	}

	/* ping 某个地址 */
	public static boolean ping(String url) {
		String result = null;
		try {
			//Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + url);
			Process p = Runtime.getRuntime().exec("ping -c 1 " + url); 	// 读取
																				// ping
																				// 内容
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			StringBuffer buffer = new StringBuffer();
			String content = "";
			while ((content = in.readLine()) != null) {
				buffer.append(content);
			}
			Log.i("ping", "result content: " + buffer.toString());

			// ping 状态
			int status = p.waitFor();
			if (status == 0) {
				result = "successful";
				return true;
			} else {
				result = "fail";
			}
		} catch (IOException e)

		{
			e.printStackTrace();
		} catch (InterruptedException e)

		{
			e.printStackTrace();
		} finally

		{
			Log.i("ping", "result = " + result);
		}
		return false;
	}
	
	/* 获取mac地址  */
	public static String getLocalMacAddress(Context context) {
		if(getWifiInfo(context) == null || getWifiInfo(context).equals(""))
			return "";
        return getWifiInfo(context).getMacAddress(); 
    } 
	
	public static final int WIFI_LEVEL_0 = 0; 
	public static final int WIFI_LEVEL_1 = 1;
	public static final int WIFI_LEVEL_2 = 2;
	public static final int WIFI_LEVEL_3 = 3;
	public static final int WIFI_LEVEL_4 = 4;
	/* 获取WiFi强度 */
	public static int getWifiLevel(Context context){
		if(getWifiInfo(context) == null || getWifiInfo(context).equals(""))
			return WIFI_LEVEL_0;
        int level = getWifiInfo(context).getRssi(); 
      //根据获得的信号强度发送信息  
        if (level <= 0 && level >= -50) {  
        	return WIFI_LEVEL_4;
        } else if (level < -50 && level >= -70) {  
            return WIFI_LEVEL_3;
        } else if (level < -70 && level >= -80) {  
            return WIFI_LEVEL_2;  
        } else if (level < -80 && level >= -100) {  
            return WIFI_LEVEL_1; 
        } else {  
            return WIFI_LEVEL_0;		// 无信号
        }  
	}
	
	public static final String CHINANET_GDOU = "ChinaNet-gdou";
	public static String getWifiName(Context context){
		if(getWifiInfo(context) == null || getWifiInfo(context).equals(""))
			return "";
		return getWifiInfo(context).getSSID().split("\"")[1];		
	}
	
	private static WifiInfo getWifiInfo(Context context){
		if(!isWifi(context))
			return null;
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
        return wifi.getConnectionInfo(); 
	}
}
