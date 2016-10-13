package com.x8.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnetByGet implements ConnetInterface{
	@Override
	public String connet(String url, String params){
		
		String result = "";

		try {
			URL conUrl = new URL(url + "?" + params);
			
			HttpURLConnection urlCon = (HttpURLConnection) conUrl.openConnection();
			InputStreamReader in = new InputStreamReader(urlCon.getInputStream());
			BufferedReader buff = new BufferedReader(in);
			String inputLine = null;
			while((inputLine = buff.readLine()) != null){
				result += inputLine + "\n";
			}
			in.close();
			urlCon.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
