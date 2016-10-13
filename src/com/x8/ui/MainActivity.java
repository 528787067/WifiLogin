package com.x8.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.x8.wifi_login.R;
import com.x8.params.AllParams;
import com.x8.params.LoginCode;
import com.x8.params.ParamsBean;
import com.x8.tools.NetworkDetector;
import com.x8.tools.UpdataMainUI;
import com.x8.tools.UserData;
import com.x8.user.Active;
import com.x8.user.HttpHeaders;
import com.x8.user.Logout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements UpdataMainUI {

	private Button loginButton;
	private EditText userName;
	private EditText password;
	private EditText mac;
	private LinearLayout inputView;
	private LinearLayout inputMac;
	private TextView runtime;
	private TextView title;
	private ImageButton wifiBn;
	private ImageView wifiImg;
	private TextView loginAbout;
	private View view;
	private ProgressDialog logining;
	private AlertDialog exitAlert;
	private ImageButton rightBn;

	private UpdataMainUI updataUI; // 更新 UI 的接口
	private UserData data; // 访问本地数据的对象
	private ParamsBean paramsBean; // 封装参数的对象

	private static Boolean isLogin; // 用于标记是否已经登陆
	private String phoneMac; // 手机本地 Mac 地址
	private String wifiMac; // 路由 Mac 地址

	private Timer timer;
	private Handler handler;
	private Message msg;
	private static long loginTime;
	private String hour;
	private String minute;
	private String second;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	@SuppressLint("HandlerLeak")
	private void init() {

		loginButton = (Button) this.findViewById(R.id.login_button);
		userName = (EditText) this.findViewById(R.id.username_edit);
		password = (EditText) this.findViewById(R.id.password_edit);
		mac = (EditText) this.findViewById(R.id.mac_edit);
		inputView = (LinearLayout) this.findViewById(R.id.input_view);
		inputMac = (LinearLayout) this.findViewById(R.id.input_mac);
		runtime = (TextView) this.findViewById(R.id.runtime);
		title = (TextView) this.findViewById(R.id.title);
		wifiBn = (ImageButton) this.findViewById(R.id.wifi_button);
		wifiImg = (ImageView) this.findViewById(R.id.wifi_image);
		loginAbout = (TextView) this.findViewById(R.id.login_about);
		rightBn = (ImageButton) this.findViewById(R.id.right_bn);
		view = this.findViewById(R.id.view);

		logining = new ProgressDialog(MainActivity.this);
		logining.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		logining.setTitle(getResources().getString(R.string.dialog_title));
		logining.setMessage(getResources().getString(R.string.dialog_msg));
		logining.setCancelable(false);

		exitAlert = new AlertDialog.Builder(MainActivity.this).create();
		exitAlert.setTitle(getResources().getString(R.string.dialog_title));
		exitAlert.setMessage(getResources().getString(R.string.exit_msg));

		updataUI = (UpdataMainUI) UpdataUI.getUpdata();
		updataUI.updataUI(MainActivity.this, null);
		data = UserData.getData(MainActivity.this);
		paramsBean = ParamsBean.getParamsBean();

		isLogin = false;
		phoneMac = NetworkDetector.getLocalMacAddress(MainActivity.this); // 获取手机mac地址
		wifiMac = data.get(AllParams.mac); // 获取本地保存的路由mac地址

		paramsBean.setUserName(data.get(AllParams.userName));
		paramsBean.setPassword(data.get(AllParams.password));
		paramsBean.setClientIp(data.get(AllParams.wlanUserIp));
		paramsBean.setNasIp(data.get(AllParams.wlanAcIp));
		/*
		if (NetworkDetector.getWifiName(MainActivity.this).equals(NetworkDetector.CHINANET_GDOU))
			paramsBean.setPhone(true);
		else
			paramsBean.setPhone(data.get(AllParams.isPhone).equals(ParamsBean.WIFI_CODE) ? false : true);
		*/
		paramsBean.setPhone(NetworkDetector.getWifiName(MainActivity.this).equals(NetworkDetector.CHINANET_GDOU));

		setLoginMode(paramsBean.isPhone()); // 设置登陆模式
		userName.setText(paramsBean.getUserName());
		password.setText(paramsBean.getPassword());
		mac.setText(wifiMac);

		ButtonClick buttonClick = new ButtonClick();
		wifiBn.setOnClickListener(buttonClick);
		loginButton.setOnClickListener(buttonClick);
		rightBn.setOnClickListener(buttonClick);

		loginTime = 0;
		timer = new Timer();
		hour = "00";
		minute = "00";
		second = "00";
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.arg1 == 1) {
					setWifiLevel();
					if (isLogin) {
						// WiFi已断开，跳转到登陆界面
						if(!NetworkDetector.isWifi(MainActivity.this)){
							Toast.makeText(MainActivity.this, LoginCode.OUT_OF_WIFI, Toast.LENGTH_LONG).show();
							switchState(false);
							return;
						}
						// 每隔 5 秒校验一次账号是否在线
						if(loginTime%5 == 0){
							new Active(ParamsBean.getParamsBean()).active(MainActivity.this);
						}
						// 更新运行时间
						hour = loginTime / (60 * 60) < 10 ? "0" + loginTime / (60 * 60)
								: loginTime / (60 * 60) + "";
						minute = (loginTime % (60 * 60) / 60) < 10 ? "0" + (loginTime % (60 * 60) / 60)
								: (loginTime % (60 * 60) / 60) + "";
						second = loginTime % 60 < 10 ? "0" + loginTime % 60 : loginTime % 60 + "";
						runtime.setText(
								getResources().getString(R.string.runtime) + hour + " : " + minute + " : " + second); // 显示运行时间
						loginTime++;
					}
				}
			}
		};

		timer.schedule(new TimerTask() {
			public void run() {
				msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);
			}
		}, 0, 1000);

		// 后台运行
		exitAlert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.exit_bn_background),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int which) {
						moveTaskToBack(false);
					}
				});
		// 退出程序
		exitAlert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.exit_bn_quit),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int which) {
						MainActivity.this.finish();
						System.exit(0);
					}
				});
	}

	/* 切换登陆模式 */
	private void setLoginMode(Boolean isPhone) {
		paramsBean.setPhone(isPhone);
		if (isPhone) {
			title.setText(getResources().getString(R.string.title_phone));
			inputMac.setVisibility(View.GONE);
			paramsBean.setMac(phoneMac);
		} else {
			title.setText(getResources().getString(R.string.title_wifi));
			inputMac.setVisibility(View.VISIBLE);
			paramsBean.setMac(wifiMac);
		}
	}

	private class ButtonClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (v.getId() == wifiBn.getId() && !isLogin) { // 切换登陆模式
				setLoginMode(!paramsBean.isPhone());
			} else if (v.getId() == loginButton.getId() && !isLogin) { // 登陆
				login();
			} else if (v.getId() == loginButton.getId() && isLogin) { // 下线
				logout();
			} else if(v.getId() == rightBn.getId()){
				MainActivity.this.startActivity(new Intent(MainActivity.this, AboutActivity.class));
			}
		}
	}

	/* 登陆 */
	private void login() {
		if (userName.getText().toString() != null && password.getText().toString() != null
				&& !userName.getText().toString().equals("") && !password.getText().toString().equals("")) {
			if (paramsBean.isPhone() || (mac.getText().toString() != null && !mac.getText().toString().equals(""))) {
				if (paramsBean.isPhone()
						&& !NetworkDetector.getWifiName(MainActivity.this).equals(NetworkDetector.CHINANET_GDOU)) {
					Toast.makeText(MainActivity.this, LoginCode.WIFI_NOT_CHINANET_MSG, Toast.LENGTH_LONG).show();
					return;
				}
				if(!paramsBean.isPhone()){
					paramsBean.setMac(mac.getText().toString());
				} else{
					paramsBean.setMac(phoneMac);
				}
				paramsBean.setUserName(userName.getText().toString());
				paramsBean.setPassword(password.getText().toString());
				logining.show(); // 显示登陆对话框
				// 验证账户是否在线，不在线则登陆，在线则进行界面跳转
				new Active(ParamsBean.getParamsBean()).active(MainActivity.this);
			} else { // 提示输入信息不完整
				Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_input_not_full),
						Toast.LENGTH_LONG).show();
			}
		} else { // 提示输入信息不完整
			Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_input_not_full),
					Toast.LENGTH_LONG).show();
		}
	}

	/* 下线 */
	private void logout() {
		logining.show();
		new Logout(paramsBean).logout(); // 下线
	}

	/* 根据返回的信息更新 UI 界面 */
	@Override
	public void updataUI(UpdataMainUI ui, String msg) {
		if (msg.equals(LoginCode.NO_NETWORK)) { // 网络不可用
			Toast.makeText(MainActivity.this, LoginCode.NO_NETWORK_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.NOT_WIFI)) { // 网络连接不是WiFi
			Toast.makeText(MainActivity.this, LoginCode.NOT_WIFI_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.CAN_NOT_CONNET)) { // 无法连接服务器
			if(!isLogin)
				Toast.makeText(MainActivity.this, LoginCode.CAN_NOT_CONNET_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.CONNETED)) { // 网络可以正常上网
			Toast.makeText(MainActivity.this, LoginCode.CONNETED_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.USER_ERROR)) { // 用户出错
			Toast.makeText(MainActivity.this, LoginCode.USER_ERROR_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGIN_FAIL)) { // 登陆失败
			Toast.makeText(MainActivity.this, LoginCode.LOGIN_FAIL_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGIN_SUCCESS)) { // 登陆成功
			switchState(true); // 切换登陆状态
			Toast.makeText(MainActivity.this, LoginCode.LOGIN_SUCCESS_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGOUT_SUCCESS)) { // 下线成功
			switchState(false); // 切换登陆状态
			Toast.makeText(MainActivity.this, LoginCode.LOGOUT_SUCCESS_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGOUT_FAIL)) { // 下线失败
			Toast.makeText(MainActivity.this, LoginCode.LOGOUT_SUCCESS_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.IS_ACTIVE)) { // 当前用户在线
			if (!isLogin) {
				switchState(true); // 切换登陆状态
				Toast.makeText(MainActivity.this, LoginCode.LOGIN_SUCCESS_MSG, Toast.LENGTH_LONG).show();
			} else {
				// 更新运行时间
			}
		} else if (msg.equals(LoginCode.NO_ACTIVE)) { // 当前用户不在线
			if (!isLogin) {
				new HttpHeaders().getHttpLocation();
			} else{
				switchState(false); // 切换登陆状态
				Toast.makeText(MainActivity.this, LoginCode.OUT_OF_CONNET, Toast.LENGTH_LONG).show();
			}
			return;
		} else if (msg.equals(LoginCode.FAIL_ACTIVE)) { // 校验失败
			if (!isLogin) {
				new HttpHeaders().getHttpLocation();
			} else{
				// 校验失败操作
			}
			return;
		}
		logining.dismiss(); // 隐藏对话框
	}

	/* 切换登陆状态 */
	private void switchState(Boolean login) {
		isLogin = login; // 标记登陆
		if (login) { // 登陆
			/* 保存登陆数据 */
			data.put(AllParams.userName, paramsBean.getUserName()); // 保存用户名到本地
			data.put(AllParams.password, paramsBean.getPassword()); // 保存密码到本地
			data.put(AllParams.wlanUserIp, paramsBean.getClientIp()); // 保存
																		// clientIp
																		// 到本地
			data.put(AllParams.wlanAcIp, paramsBean.getNasIp()); // 保存 nssip 到本地
			data.put(AllParams.isPhone, paramsBean.getPhone()); // 保存登陆模式到本地
			if(!paramsBean.isPhone())
				wifiMac = paramsBean.getMac();
			data.put(AllParams.mac, wifiMac); // 保存路由mac地址到本地
			data.commit(); // 提交保存数据

			wifiBn.setClickable(false); // 禁止切换登陆模式按钮
			loginButton.setText(getResources().getString(R.string.logout)); // 更登陆按钮为下线按钮
			inputView.setVisibility(View.GONE); // 隐藏全部文本输入框
			loginAbout.setVisibility(View.GONE);// 隐藏登陆说明文本
			view.setVisibility(View.VISIBLE);
			runtime.setVisibility(View.VISIBLE); // 显示运行时间文本

		} else { // 下线
			wifiBn.setClickable(true); // 启动切换登陆模式按钮
			loginButton.setText(getResources().getString(R.string.login)); // 更登陆按钮为登陆按钮
			inputView.setVisibility(View.VISIBLE); // 隐藏全部文本输入框
			loginAbout.setVisibility(View.VISIBLE);// 显示登陆说明文本
			view.setVisibility(View.GONE);
			runtime.setVisibility(View.GONE); // 显示运行时间文本
		}
		loginTime = 0;
	}

	@SuppressWarnings("deprecation")
	private void setWifiLevel() {
		if (!NetworkDetector.isWifi(MainActivity.this)) {
			if (isLogin) {
				switchState(false);
				Toast.makeText(MainActivity.this, getResources().getString(R.string.wifi_out), Toast.LENGTH_LONG)
						.show();
			}
			wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign0));
			return;
		}
		switch (NetworkDetector.getWifiLevel(MainActivity.this)) {
		case NetworkDetector.WIFI_LEVEL_0:
			wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign0));
			break;
		case NetworkDetector.WIFI_LEVEL_1:
			wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign1));
			break;
		case NetworkDetector.WIFI_LEVEL_2:
			wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign2));
			break;
		case NetworkDetector.WIFI_LEVEL_3:
			wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign3));
			break;
		case NetworkDetector.WIFI_LEVEL_4:
			wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign4));
			break;
		default:
			//wifiImg.setImageDrawable(getResources().getDrawable(R.drawable.sign0));
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitAlert.show();
		}
		return true;
	}
}
