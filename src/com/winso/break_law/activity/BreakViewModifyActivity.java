package com.winso.break_law.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.winso.break_law.R;
import com.winso.break_law.activity.MainActivity.MyCallBack;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.CItem;
import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.DownloadFileTask;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.widget.RemoteImageHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 用于批阅修改类
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-01
 */
public class BreakViewModifyActivity extends BaseActivity {
	private AppContext appContext;
	private String sBreakRuleID;
	private Spinner mSpinnerCheck;

	private RemoteImageHelper rImageHelp = new RemoteImageHelper();

	private TextView tvProjectDept, txBreakOption, txBreakDate, txBreakContent;
	private EditText edViewLevel1, edViewLevel2, edViewLevel3, edViewLevel4;
	private String sLevel1 = "", sLevel2 = "", sLevel3 = "", sLevel4 = ""; // 记录进来时的值，未修改则不保存
	private String sReviewID1 = "", sReviewID2 = "", sReviewID3 = "",
			sReviewID4 = ""; // 原有review编号

	private int iRight = 2; // 权限
	private int iCurNode = -1; // 进来时的节点
	SelectHelp rtnBreakHelp = new SelectHelp();
	SelectHelp rtnBreakViewHelp = new SelectHelp(); // 当前保存的breakreview信息

	private boolean bProcessSaveing = false;
	private boolean bProcessSaveOK = false; // 保存是否成功
	private String sFilePath = ""; // 加载的本地文件图片
	private ImageView fViewPhoto;
	private Button fbSave;
	boolean  mDownloadOK= false;
	MyCallBack myCallback; //
	String msDownloadFilePath="";
	// private String sImageURL = ""; //远程的ＵＲＬ，确认是否需要加载

	/*
	 * 下载功能
	 */
	public class MyCallBack implements CallbackInterface {
		BreakViewModifyActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) {

				mDownloadOK = true;
				//存在
				fViewPhoto.setImageURI(Uri.parse(msDownloadFilePath));
				return;
			}
			mDownloadOK = false;
		}

		public void cancel(String responseText) {
			UIHelper.showMsg(BreakViewModifyActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack(BreakViewModifyActivity ac) {
			mAc = ac;
		}
	}
	/*
	 * 下载文件
	 */
	public boolean download(String sRemote,String sLocal)
	{
		new DownloadFileTask(appContext.m_ice, this, sRemote, sLocal,myCallback).execute();
		return true;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this,
				R.layout.activity_break_view_modify, null);
		
		myCallback = new MyCallBack(this);
		setContentView(view);
		
		appContext = (AppContext) getApplication();

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("违规批阅");

		InitControl();
		initProps();

		Intent it = getIntent();
		sBreakRuleID = it.getStringExtra("break_rule_id");
		tvProjectDept.setText(it.getStringExtra("org_name"));

		// 设置返回按扭
		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		//
		// 设置保存信息
		fbSave = (Button) findViewById(R.id.btn_save);
		fbSave.setVisibility(View.INVISIBLE);
		
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				new ProcessSaveTask().execute();
			}
		}

		);

		fViewPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//
				if (sFilePath.length() <= 0)
					return;
				UIHelper.openViewPic(v.getContext(), sFilePath);

			}
		}

		);

		new GetDataTask().execute();
	}

	// private int mRowCount = 0;
	private void InitControl() {

		fViewPhoto = (ImageView) findViewById(R.id.img_break);

		// mSpinnerCheck = (Spinner)findViewById(R.id.sp_check_status);
		// private TextView
		// tvProjectDept,txBreakOption,txBreakDate,txBreakContent;
		// private EditText edViewLevel1,edViewLevel2,edViewLevel3,edViewLevel4;

		tvProjectDept = (TextView) findViewById(R.id.tx_project_dept);
		txBreakOption = (TextView) findViewById(R.id.tx_break_option);

		txBreakDate = (TextView) findViewById(R.id.tx_break_date);
		txBreakContent = (TextView) findViewById(R.id.tx_break_content);

		edViewLevel1 = (EditText) findViewById(R.id.ed_view_level_1);
		edViewLevel2 = (EditText) findViewById(R.id.ed_view_level_2);
		edViewLevel3 = (EditText) findViewById(R.id.ed_view_level_3);
		edViewLevel4 = (EditText) findViewById(R.id.ed_view_level_4);
		edViewLevel1.setEnabled(false);
		edViewLevel2.setEnabled(false);
		edViewLevel3.setEnabled(false);
		edViewLevel4.setEnabled(false);

	}

	private void getRemoteData() {
		rtnBreakHelp = appContext.getBreakInfo(sBreakRuleID);
		rtnBreakViewHelp = appContext.getBreakViewInfo(sBreakRuleID);
	}

	// 加载历史数据
	private void ProcessLoadData() {

		if (rtnBreakHelp.mReturnCode < 0) {
			UIHelper.showMsg(this, "错误", rtnBreakHelp.mReturnError);
			return;
		}
		if (rtnBreakHelp.mReturnCode == 0) {
			UIHelper.showMsg(this, "错误", "没有找到此编号,可能已经删除");
			return;
		}


		iCurNode = Integer
				.valueOf(rtnBreakHelp.valueStringByName(0, "node_id"));

		
		if ( iCurNode ==  AppContext.FLOW_NODE_CACHE )
		{
			if ( appContext.hasRight(AppContext.RIGHT_BR_REVIEW_1) )
			{
				fbSave.setVisibility(View.VISIBLE);
			}			
		}
		
		//判断权限是否存在
		if ( iCurNode ==  AppContext.FLOW_NODE_VIEW_1 )
		{
			if ( appContext.hasRight(AppContext.RIGHT_BR_REVIEW_1) )
			{
				fbSave.setVisibility(View.VISIBLE);
			}			
		}
		//判断权限是否存在
		if ( iCurNode ==  AppContext.FLOW_NODE_VIEW_2 )
		{
			if ( appContext.hasRight(AppContext.RIGHT_BR_REVIEW_2) )
			{
				fbSave.setVisibility(View.VISIBLE);
			}			
		}
		//判断权限是否存在
		if ( iCurNode ==  AppContext.FLOW_NODE_VIEW_3 )
		{
			if ( appContext.hasRight(AppContext.RIGHT_BR_REVIEW_3) )
			{
				fbSave.setVisibility(View.VISIBLE);
			}			
		}
		//判断权限是否存在
		if ( iCurNode ==  AppContext.FLOW_NODE_VIEW_4 )
		{
			if ( appContext.hasRight(AppContext.RIGHT_BR_REVIEW_4) )
			{
				fbSave.setVisibility(View.VISIBLE);
			}			
		}
		
		//
		txBreakDate.setText(rtnBreakHelp.valueStringByName(0, "pic_time"));
		txBreakOption
				.setText(appContext.getBreakRuleType(Integer
						.valueOf(rtnBreakHelp.valueStringByName(0,
								"break_rule_type"))));
		txBreakContent.setText(rtnBreakHelp.valueStringByName(0,
				"break_rule_content"));

		String sUpURL = appContext.RemoteFileURL();

		String sLoacalFile = appContext.getPictureDirectory()
				+ rtnBreakHelp.valueStringByName(0, "pic_name");
		sUpURL += rtnBreakHelp.valueStringByName(0, "pic_name");

		if (FileUtils.checkFileExists(sLoacalFile)) {
			fViewPhoto.setImageURI(Uri.parse(sLoacalFile));
			sFilePath = sLoacalFile;
		} else {
			// sImageURL = sUpURL;
			mDownloadOK = false;
			msDownloadFilePath =  sLoacalFile;
			sFilePath = sLoacalFile;
			download(rtnBreakHelp.valueStringByName(0, "pic_name"),appContext.getPictureDirectory());
			
		}

		// 如果没有指定节点的权限，则返回
		if (!appContext.hasNodeRight(iRight)) {
			return;
		}
		iRight =  iCurNode;
		
		if (iCurNode <= 0) {
			edViewLevel1.setEnabled(false);
			edViewLevel2.setEnabled(false);
			edViewLevel3.setEnabled(false);
			edViewLevel4.setEnabled(false);

		} else if (iCurNode == 2) {
			edViewLevel1.setEnabled(true);
			edViewLevel2.setEnabled(false);
			edViewLevel3.setEnabled(false);
			edViewLevel4.setEnabled(false);
			edViewLevel1.requestFocus();
		} else if (iCurNode == 3) {
			edViewLevel1.setEnabled(false);
			edViewLevel2.setEnabled(true);
			edViewLevel3.setEnabled(false);
			edViewLevel4.setEnabled(false);
			edViewLevel2.requestFocus();
		} else if (iCurNode == 4) {
			edViewLevel1.setEnabled(false);
			edViewLevel2.setEnabled(false);
			edViewLevel3.setEnabled(true);
			edViewLevel4.setEnabled(false);
			edViewLevel3.requestFocus();
		} else if (iCurNode == 5) {
			edViewLevel1.setEnabled(false);
			edViewLevel2.setEnabled(false);
			edViewLevel3.setEnabled(false);
			edViewLevel4.setEnabled(true);
			edViewLevel4.requestFocus();
		}

		// 更新rBV的信息
		if (rtnBreakViewHelp == null)
			return;

		for (int i = 0; i < rtnBreakViewHelp.size(); i++) {
			String sValue = rtnBreakViewHelp.valueStringByName(i,
					"review_content");
			String sReviewID = rtnBreakViewHelp.valueStringByName(i,
					"review_id");

			if (rtnBreakViewHelp.valueStringByName(i, "review_grade").equals(
					"1")) {
				sLevel1 = sValue;
				sReviewID1 = sReviewID;

				edViewLevel1.setText(sValue);
				continue;
			}
			if (rtnBreakViewHelp.valueStringByName(i, "review_grade").equals(
					"2")) {
				sLevel2 = sValue;
				sReviewID2 = sReviewID;
				edViewLevel2.setText(sValue);
				continue;
			}
			if (rtnBreakViewHelp.valueStringByName(i, "review_grade").equals(
					"3")) {
				sLevel3 = sValue;
				sReviewID3 = sReviewID;
				edViewLevel3.setText(sValue);
				continue;
			}
			if (rtnBreakViewHelp.valueStringByName(i, "review_grade").equals(
					"4")) {
				sLevel4 = sValue;
				sReviewID4 = sReviewID;
				edViewLevel4.setText(sValue);
				continue;
			}

		}

		// 判断当前流程节点

		//

	}

	public void initProps() {
		mSpinnerCheck = (Spinner) findViewById(R.id.sp_check_status);
		List<CItem> lst = new ArrayList<CItem>();

		CItem ct1 = new CItem(String.valueOf(AppContext.FLOW_CHECK_PASS),
				"审核通过");
		lst.add(ct1);
		CItem ct3 = new CItem(
				String.valueOf(AppContext.FLOW_CHECK_NO_NEED_REFORM), "无需整改");
		lst.add(ct3);

		CItem ct2 = new CItem(String.valueOf(AppContext.FLOW_CHECK_CANNOT),
				"无法判定");
		lst.add(ct2);

		ArrayAdapter<CItem> Adapter = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);

		mSpinnerCheck.setAdapter(Adapter);

	}

	private void ProcessSave() {
		bProcessSaveOK = true;

		int iPassState = Integer.valueOf(((CItem) mSpinnerCheck
				.getSelectedItem()).GetID());

		if (!sLevel1.equals(edViewLevel1.getText().toString())) {
			if (sLevel1.length() <= 0) {
				bProcessSaveOK = appContext.insertBRView(sBreakRuleID,
						edViewLevel1.getText().toString(), 1,
						AppContext.FLOW_NODE_VIEW_2, iPassState);
			} else // 更新
			{
				bProcessSaveOK = appContext.updateBRView(sReviewID1,
						edViewLevel1.getText().toString());
			}
		}
		if (bProcessSaveOK == false)
			return;

		if (!sLevel2.equals(edViewLevel2.getText().toString())) {
			if (sLevel2.length() <= 0) {
				appContext
						.insertBRView(sBreakRuleID, edViewLevel2.getText()
								.toString(), 2, AppContext.FLOW_NODE_VIEW_3,
								iPassState);
			} else // 更新
			{
				appContext.updateBRView(sReviewID2, edViewLevel2.getText()
						.toString());
			}
		}
		if (bProcessSaveOK == false)
			return;

		if (!sLevel3.equals(edViewLevel3.getText().toString())) {
			if (sLevel3.length() <= 0) {
				appContext
						.insertBRView(sBreakRuleID, edViewLevel3.getText()
								.toString(), 3, AppContext.FLOW_NODE_VIEW_4,
								iPassState);
			} else // 更新
			{
				appContext.updateBRView(sReviewID3, edViewLevel3.getText()
						.toString());
			}
		}

		if (bProcessSaveOK == false)
			return;
		if (!sLevel4.equals(edViewLevel4.getText().toString())) {
			if (sLevel4.length() <= 0) {
				appContext
						.insertBRView(sBreakRuleID, edViewLevel4.getText()
								.toString(), 4, AppContext.FLOW_NODE_REFORM,
								iPassState);
			} else // 更新
			{
				appContext.updateBRView(sReviewID4, edViewLevel4.getText()
						.toString());
			}
		}

	}

	// 执行刷新任务
	private class ProcessSaveTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { "" };

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				if (bProcessSaveing)
					return mStrings;

				bProcessSaveing = true;
				ProcessSave();
				bProcessSaveing = false;

			} catch (Exception e) {
				e.printStackTrace();

			}

			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			super.onPostExecute(result);

			if (bProcessSaveOK)
				finish();
		}
	}

	// 执行获取远程数据任务
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { "" };

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				getRemoteData();

			} catch (Exception e) {
				e.printStackTrace();

			}

			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			super.onPostExecute(result);

			ProcessLoadData();
		}
	}



}
