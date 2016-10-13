package com.x8.ui;

import com.x8.tools.UpdataMainUI;

public class UpdataUI implements UpdataMainUI{

	private static UpdataUI updataUI;
	private UpdataUI(){};	
	private UpdataMainUI mainUI;
	
	public static synchronized UpdataUI getUpdata(){
		if(updataUI == null){
			updataUI = new UpdataUI();
		}
		return updataUI;
	}
	
	public void updataMainUI(String loginMessage) {
		updataUI(null, loginMessage);
	}

	@Override
	public void updataUI(UpdataMainUI ui, String msg) {
		if(ui != null && msg == null)
			this.mainUI = ui;
		else if(ui == null && msg != null)
			mainUI.updataUI(null, msg);
	}
	
}
