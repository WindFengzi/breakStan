package com.winso.break_law.activity;

import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.widget.SwitchButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingActivity extends BaseActivity {

	private SwitchButton mSt2G3G;
	private SwitchButton mStMessage;
	private SwitchButton mStMessageV,mCheckAutoLogin;
	private SwitchButton mStMessageNight;
	private EditText mEdCentIP, mEdProject;
	private Button mBtBack;
	private RadioButton mBtAbout,mBtSync;
	private AppContext appContext;
	private TextView tvAbout;
	private Button exitLogin;
//	private DownloadFile download = null;
	private boolean bProcessUpfile = false;
	private MyCallBack myCallBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		myCallBack = new MyCallBack(this);
		
		appContext = (AppContext) getApplication();
		TextView vt= (TextView)findViewById(R.id.tx_header_title);
		
		vt.setText("系统设置");
		mCheckAutoLogin = (SwitchButton) findViewById(R.id.st_auto_login);
		mSt2G3G = (SwitchButton) findViewById(R.id.st_enable_2g3g);
		mStMessage = (SwitchButton) findViewById(R.id.st_enable_message);
		mStMessageV = (SwitchButton) findViewById(R.id.st_enable_message_v);
		mStMessageNight = (SwitchButton) findViewById(R.id.st_enable_message_night);
		mEdCentIP = (EditText) findViewById(R.id.ed_cent_ip);

		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SAVE);
		fbSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		
		mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
//		mBtSync = (RadioButton) findViewById(R.id.main_footbar_func_draft);
//		mBtSync.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(v.getContext(),
//						SyncActivity.class);
//				startActivityForResult(intent, UIHelper.INTENT_SYNC_ACTIONS);
//			}
//		});

		tvAbout = (TextView) findViewById(R.id.about_tv);
		tvAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.openAbout(v.getContext());
			}
		});
		
		mEdProject = (EditText) findViewById(R.id.ed_project);
		mEdProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						SelectProjectActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_SELECT_PROJECT);

			}
		});

		load();
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == UIHelper.INTENT_SELECT_PROJECT) {
			mEdProject.setText(appContext.getCookie("org_name"));
		}

	}

	private void save() {
		appContext.setCookieBoolean("enable_2g3g", mSt2G3G.isChecked());
		appContext.setCookieBoolean("enable_message", mStMessage.isChecked());
		appContext
				.setCookieBoolean("enable_message_v", mStMessageV.isChecked());
		appContext.setCookieBoolean("enable_message_night",
				mStMessageNight.isChecked());

		appContext.setCookieBoolean("login_auto",
				mCheckAutoLogin.isChecked());
		
		appContext.setCookie("cent_ip", mEdCentIP.getText().toString());
		finish();
	}

	private void load() {
		mCheckAutoLogin.setChecked(appContext.getCookieBoolean("login_auto"));
		mSt2G3G.setChecked(appContext.getCookieBoolean("enable_2g3g"));
		mStMessage.setChecked(appContext.getCookieBoolean("enable_message"));
		mStMessageV.setChecked(appContext.getCookieBoolean("enable_message_v"));
		mStMessageNight.setChecked(appContext
				.getCookieBoolean("enable_message_night"));

		mEdProject.setText(appContext.getCookie("org_name"));

		mEdCentIP.setText(appContext.getCookie("cent_ip"));
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		exitLogin = (Button) findViewById(R.id.exit_login);
		String username = appContext.getCookie("login_exit");
		System.out.println("username--->" + username +"---");
		if (username == "login" || ("login").equals(username)) {
			exitLogin.setVisibility(View.VISIBLE);
		} else {
			exitLogin.setVisibility(View.INVISIBLE);
		}
		
		exitLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				appContext.setCookie("login_exit", "exit");
				UIHelper.exitLogin(v.getContext());
				finish();
//				System.exit(0);
			}
			
		});
	}
	

	public class MyCallBack implements CallbackInterface {
		SettingActivity mAc;

		public void func(String responseText) {

			bProcessUpfile = false;

			if (responseText == null)
				return;

		

		}

		public void cancel(String responseText) {

			bProcessUpfile = false;
			
		}

		MyCallBack(SettingActivity ac) {
			mAc = ac;
		}
	}
}
