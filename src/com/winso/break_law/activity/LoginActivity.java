package com.winso.break_law.activity;

import java.io.InputStream;

import com.winso.break_law.app.*;
import com.winso.break_law.R;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.icedb.SelectHelpParam;

import com.winso.comm_library.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;

public class LoginActivity extends BaseActivity {

	private AppContext appContext;
	private Button mBtnLogin;
	private Button mBtnSetting;
	private EditText mEditUser;
	private EditText mEditUserPasswd;
	private CheckBox mCheckAutoLogin;
	private CheckBox mCheckRemInfo;
	private boolean mLoginOK = false;

	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_v2);

		
		// 检测网络是否已经连接
		appContext = (AppContext) getApplication();
		mBtnLogin = (Button) findViewById(R.id.login_btn_login);

		mBtnSetting = (Button) findViewById(R.id.login_btn_setting_2);

		// 用户名和密码
		mEditUser = (EditText) findViewById(R.id.login_account);
		mEditUserPasswd = (EditText) findViewById(R.id.login_password);

		// 记住信息
		mCheckAutoLogin = (CheckBox) findViewById(R.id.login_check_box_autologin);
		mCheckRemInfo = (CheckBox) findViewById(R.id.login_checkbox_rememberMe);

		// 加载以前的配置信息
		loadOldUser();

		// 绑定登录按钮点击事件。
		// mBtnLogin.setOnClickListener(new Listener());

		// 登录
		mBtnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				EasyLog.debug("login");
				new LoginTask().execute(); // 提交登录操作。
			}
		});

		// 设置按扭
		mBtnSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				UIHelper.openSetting(v.getContext());
			}
		});
		//
		mCheckAutoLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				appContext.setCookieBoolean("login_auto",
						mCheckAutoLogin.isChecked());
			}
		});

		mCheckRemInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				appContext.setCookieBoolean("login_rem",
						mCheckRemInfo.isChecked());
			}
		});

		if (mCheckAutoLogin.isChecked()) {
			new LoginTask().execute(); // 提交登录操作。
		}
	}

	private boolean loadOldUser() {

		mCheckAutoLogin.setChecked(appContext.getCookieBoolean("login_auto"));
		mCheckRemInfo.setChecked(appContext.getCookieBoolean("login_rem"));

		mEditUser.setText(appContext.getCookie("login_user"));

		if (mCheckRemInfo.isChecked())
			mEditUserPasswd.setText(appContext.getCookie("login_pass"));

		return true;
	}

	/**
	 * 显示登录提示对话框。
	 */
	protected void showDialog() {

		progressDialog = ProgressDialog
				.show(this, "正在登陆验证", "请稍后", true, false);
		progressDialog.setCancelable(true);
	}

	/**
	 * 隐藏登录提示对话框。
	 */
	protected void dismissDialog() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	///
	private String Login()
	{
		try {
			
			mLoginOK = false;
			boolean bNetConnected = appContext.isNetworkConnected();
			if (!bNetConnected) {
				mLoginOK = false;
				return "connect_fail";
			}

			appContext.m_ice.login(appContext.getCookie("cent_ip"), 8840);

			if (!appContext.m_ice.isLogin()) {
				Toast.makeText(getApplicationContext(), "网络未连接",
						Toast.LENGTH_SHORT).show();
				mLoginOK = false;
				return "connect_fail" ;
			}
			// test uploadfile

			SelectHelpParam helpParam = new SelectHelpParam();
			helpParam.add(mEditUser.getText().toString());
			helpParam.add(mEditUserPasswd.getText().toString());
			
//			System.out.println("helpParam.get()--->" + helpParam.get());
			SelectHelp v = appContext.m_ice.select("login_check",
					helpParam.get());

			EasyLog.debug("return=" + v.mReturnCode);
			if (v.mReturnCode > 0) {

				appContext.setCookie("login_user", mEditUser.getText()
						.toString());
				appContext.setCookie("login_pass", mEditUserPasswd.getText()
						.toString());

				InputStream is = getResources().openRawResource(
						R.raw.break_law_local_exec);

				appContext.convertSQLMap(FileUtils.readInStream(is));

				appContext.loadInitConfigure();
				
				mLoginOK = true;
				return "success";
			} else if (v.mReturnCode < 0) {

				return "connect_fail";
			}
			
			mLoginOK = false;
			return "fail";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
		mLoginOK = false;
		return "connect_fail";

	}
	/**
	 * 登录管理。
	 * 
	 */
	private class LoginTask extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}

		protected String doInBackground(String... arg0) {

			
			return Login();

		}

		protected void onPostExecute(String result) {

			if (result != null) {
				if (result.equals("success")) {

					startActivity(new Intent(LoginActivity.this,
							MainActivity.class)); // 启动 主界面活动类。

					finish(); // 终止当前活动界面。
				} else if (result.equals("connect_fail")) {
					Toast.makeText(getApplicationContext(), "连接失败",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "帐号或密码错误",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "连接失败",
						Toast.LENGTH_SHORT).show();
			}

			dismissDialog();
		}
	}

}
