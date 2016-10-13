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

	private UpdataMainUI updataUI; // ���� UI �Ľӿ�
	private UserData data; // ���ʱ������ݵĶ���
	private ParamsBean paramsBean; // ��װ�����Ķ���

	private static Boolean isLogin; // ���ڱ���Ƿ��Ѿ���½
	private String phoneMac; // �ֻ����� Mac ��ַ
	private String wifiMac; // ·�� Mac ��ַ

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
		phoneMac = NetworkDetector.getLocalMacAddress(MainActivity.this); // ��ȡ�ֻ�mac��ַ
		wifiMac = data.get(AllParams.mac); // ��ȡ���ر����·��mac��ַ

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

		setLoginMode(paramsBean.isPhone()); // ���õ�½ģʽ
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
						// WiFi�ѶϿ�����ת����½����
						if(!NetworkDetector.isWifi(MainActivity.this)){
							Toast.makeText(MainActivity.this, LoginCode.OUT_OF_WIFI, Toast.LENGTH_LONG).show();
							switchState(false);
							return;
						}
						// ÿ�� 5 ��У��һ���˺��Ƿ�����
						if(loginTime%5 == 0){
							new Active(ParamsBean.getParamsBean()).active(MainActivity.this);
						}
						// ��������ʱ��
						hour = loginTime / (60 * 60) < 10 ? "0" + loginTime / (60 * 60)
								: loginTime / (60 * 60) + "";
						minute = (loginTime % (60 * 60) / 60) < 10 ? "0" + (loginTime % (60 * 60) / 60)
								: (loginTime % (60 * 60) / 60) + "";
						second = loginTime % 60 < 10 ? "0" + loginTime % 60 : loginTime % 60 + "";
						runtime.setText(
								getResources().getString(R.string.runtime) + hour + " : " + minute + " : " + second); // ��ʾ����ʱ��
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

		// ��̨����
		exitAlert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.exit_bn_background),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int which) {
						moveTaskToBack(false);
					}
				});
		// �˳�����
		exitAlert.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.exit_bn_quit),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int which) {
						MainActivity.this.finish();
						System.exit(0);
					}
				});
	}

	/* �л���½ģʽ */
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
			if (v.getId() == wifiBn.getId() && !isLogin) { // �л���½ģʽ
				setLoginMode(!paramsBean.isPhone());
			} else if (v.getId() == loginButton.getId() && !isLogin) { // ��½
				login();
			} else if (v.getId() == loginButton.getId() && isLogin) { // ����
				logout();
			} else if(v.getId() == rightBn.getId()){
				MainActivity.this.startActivity(new Intent(MainActivity.this, AboutActivity.class));
			}
		}
	}

	/* ��½ */
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
				logining.show(); // ��ʾ��½�Ի���
				// ��֤�˻��Ƿ����ߣ����������½����������н�����ת
				new Active(ParamsBean.getParamsBean()).active(MainActivity.this);
			} else { // ��ʾ������Ϣ������
				Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_input_not_full),
						Toast.LENGTH_LONG).show();
			}
		} else { // ��ʾ������Ϣ������
			Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_input_not_full),
					Toast.LENGTH_LONG).show();
		}
	}

	/* ���� */
	private void logout() {
		logining.show();
		new Logout(paramsBean).logout(); // ����
	}

	/* ���ݷ��ص���Ϣ���� UI ���� */
	@Override
	public void updataUI(UpdataMainUI ui, String msg) {
		if (msg.equals(LoginCode.NO_NETWORK)) { // ���粻����
			Toast.makeText(MainActivity.this, LoginCode.NO_NETWORK_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.NOT_WIFI)) { // �������Ӳ���WiFi
			Toast.makeText(MainActivity.this, LoginCode.NOT_WIFI_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.CAN_NOT_CONNET)) { // �޷����ӷ�����
			if(!isLogin)
				Toast.makeText(MainActivity.this, LoginCode.CAN_NOT_CONNET_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.CONNETED)) { // ���������������
			Toast.makeText(MainActivity.this, LoginCode.CONNETED_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.USER_ERROR)) { // �û�����
			Toast.makeText(MainActivity.this, LoginCode.USER_ERROR_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGIN_FAIL)) { // ��½ʧ��
			Toast.makeText(MainActivity.this, LoginCode.LOGIN_FAIL_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGIN_SUCCESS)) { // ��½�ɹ�
			switchState(true); // �л���½״̬
			Toast.makeText(MainActivity.this, LoginCode.LOGIN_SUCCESS_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGOUT_SUCCESS)) { // ���߳ɹ�
			switchState(false); // �л���½״̬
			Toast.makeText(MainActivity.this, LoginCode.LOGOUT_SUCCESS_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.LOGOUT_FAIL)) { // ����ʧ��
			Toast.makeText(MainActivity.this, LoginCode.LOGOUT_SUCCESS_MSG, Toast.LENGTH_LONG).show();
		} else if (msg.equals(LoginCode.IS_ACTIVE)) { // ��ǰ�û�����
			if (!isLogin) {
				switchState(true); // �л���½״̬
				Toast.makeText(MainActivity.this, LoginCode.LOGIN_SUCCESS_MSG, Toast.LENGTH_LONG).show();
			} else {
				// ��������ʱ��
			}
		} else if (msg.equals(LoginCode.NO_ACTIVE)) { // ��ǰ�û�������
			if (!isLogin) {
				new HttpHeaders().getHttpLocation();
			} else{
				switchState(false); // �л���½״̬
				Toast.makeText(MainActivity.this, LoginCode.OUT_OF_CONNET, Toast.LENGTH_LONG).show();
			}
			return;
		} else if (msg.equals(LoginCode.FAIL_ACTIVE)) { // У��ʧ��
			if (!isLogin) {
				new HttpHeaders().getHttpLocation();
			} else{
				// У��ʧ�ܲ���
			}
			return;
		}
		logining.dismiss(); // ���ضԻ���
	}

	/* �л���½״̬ */
	private void switchState(Boolean login) {
		isLogin = login; // ��ǵ�½
		if (login) { // ��½
			/* �����½���� */
			data.put(AllParams.userName, paramsBean.getUserName()); // �����û���������
			data.put(AllParams.password, paramsBean.getPassword()); // �������뵽����
			data.put(AllParams.wlanUserIp, paramsBean.getClientIp()); // ����
																		// clientIp
																		// ������
			data.put(AllParams.wlanAcIp, paramsBean.getNasIp()); // ���� nssip ������
			data.put(AllParams.isPhone, paramsBean.getPhone()); // �����½ģʽ������
			if(!paramsBean.isPhone())
				wifiMac = paramsBean.getMac();
			data.put(AllParams.mac, wifiMac); // ����·��mac��ַ������
			data.commit(); // �ύ��������

			wifiBn.setClickable(false); // ��ֹ�л���½ģʽ��ť
			loginButton.setText(getResources().getString(R.string.logout)); // ����½��ťΪ���߰�ť
			inputView.setVisibility(View.GONE); // ����ȫ���ı������
			loginAbout.setVisibility(View.GONE);// ���ص�½˵���ı�
			view.setVisibility(View.VISIBLE);
			runtime.setVisibility(View.VISIBLE); // ��ʾ����ʱ���ı�

		} else { // ����
			wifiBn.setClickable(true); // �����л���½ģʽ��ť
			loginButton.setText(getResources().getString(R.string.login)); // ����½��ťΪ��½��ť
			inputView.setVisibility(View.VISIBLE); // ����ȫ���ı������
			loginAbout.setVisibility(View.VISIBLE);// ��ʾ��½˵���ı�
			view.setVisibility(View.GONE);
			runtime.setVisibility(View.GONE); // ��ʾ����ʱ���ı�
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
