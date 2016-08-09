package com.winso.break_law.activity;

import com.winso.break_law.R;
import com.winso.break_law.R.layout;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.EasyLog;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.icedb.SelectHelpParam;
import com.winso.comm_library.widget.*;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ReformActivity_old extends BaseActivity
{
	private RemoteImageHelper rImageHelp = new RemoteImageHelper();
	private TextView mTxSpinProp;
	private TextView mTxContent;
	private String sBreakRuleID;
	private String sContent;
	private ImageView fViewPhoto;
	
	private AppContext appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reform_old);
		appContext = (AppContext)getApplication();
		
		mTxSpinProp = (TextView)findViewById(R.id.tx_spin_prop);
		mTxContent = (TextView)findViewById(R.id.edit_fpr_opinion);
		fViewPhoto = (ImageView)findViewById(R.id.photo_cathe_save_img);
		//mTxContent.setEnabled(false);
		
		Intent it = getIntent();
		
		sBreakRuleID = it.getStringExtra("break_rule_id");
		
		mTxSpinProp.setText(sBreakRuleID);
		mTxContent.setText(it.getStringExtra("break_rule_content"));
		
		
		
		
		InitButton();
		
		LoadData();
		
	}

	//加载历史数据
	private void LoadData()
	{
	

		SelectHelp rv = appContext.getBreakInfo(sBreakRuleID);

		if (rv.mReturnCode < 0) {
			UIHelper.showMsg(this, "错误", rv.mReturnError);
			return;
		}
		if (rv.mReturnCode == 0) {
			UIHelper.showMsg(this, "错误", "没有找到此编号,可能已经删除");
			return;
		}

		String sUpURL = appContext.RemoteFileURL();
		
		
		String sFile = FileUtils.getFileName(rv.valueStringByName(0, "pic_name"));
		
		sUpURL += sFile;


		//rImageHelp.loadImage(fViewPhoto, sUpURL, false);
		
	}
	
	private void InitButton()
	{
		//
		// 抓图
		Button fbSave = (Button) findViewById(R.id.btn_save);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				

			}
		}

		);
		
		//
		// 抓图
		Button fbBack = (Button) findViewById(R.id.btn_back);
		fbBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				finish();

			}
		}

		);
		
	}
	private void loadRemoteImage(final String sUpURL) {

		new Thread(new Runnable() {

			public void run() {

				try {
					//rImageHelp.loadImage(fViewPhoto, sUpURL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	
}
