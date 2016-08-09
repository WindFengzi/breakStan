package com.winso.break_law.activity;

import java.io.File;
import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用于整改批阅
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-20
 */
public class QueryViewActivity extends BaseActivity {
	private AppContext appContext;
	private String sBreakRuleID;
	// private Spinner mSpinnerCheck;
	private ImageView imgBreakLaw, imgReform;

	private RemoteImageHelper rImageHelp = new RemoteImageHelper();

	private TextView tvProjectDept, txBreakOption, txBreakDate, txBreakContent,
			txBreakContentReform, txCheckStatus,txRectTime;
	private String sBreakImagePath = "", sReformImagePath = "";
	private int iRight = 2; // 权限
	private int iCurNode = -1; // 进来时的节点
	SelectHelp rtnBreakHelp = new SelectHelp();
	// SelectHelp rtnBRViewHelp = new SelectHelp(); // 当前保存的breakreview信息
	// SelectHelp rtnRectifyHelp = new SelectHelp(); // 整改的信息
	private boolean bProcessSaveing = false;
	private boolean bProcessSaveOK = false; // 保存是否成功
	private String sFilePath = ""; // 加载的本地文件图片
	private String sRectifyID = ""; // 整改编号

	// private ImageView fViewPhoto;

	// private String sImageURL = ""; //远程的ＵＲＬ，确认是否需要加载

	// 图片下载
	String msDownloadFilePath = "";
	boolean mDownloadOK = false,mDownloadOK_reform=false;
	MyCallBack myCallback = null;
	MyCallBack_reform myCallbackReform = null;
	boolean mDownloadReform = true;

	/*
	 * 下载功能
	 */
	public class MyCallBack_reform implements CallbackInterface {
		QueryViewActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) {

				mDownloadOK_reform = true;
				// 存在
				imgReform.setImageURI(Uri.parse(sReformImagePath));
				
				return;
			}
			mDownloadOK_reform = false;
		}

		public void cancel(String responseText) {
			UIHelper.showMsg(QueryViewActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack_reform(QueryViewActivity ac) {
			mAc = ac;
		}
	}
	/*
	 * 下载功能
	 */
	public class MyCallBack implements CallbackInterface {
		QueryViewActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) {

				mDownloadOK = true;
				// 存在
				
				imgBreakLaw.setImageURI(Uri.parse(sBreakImagePath));
				
				return;
			}
			mDownloadOK = false;
		}

		public void cancel(String responseText) {
			UIHelper.showMsg(QueryViewActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack(QueryViewActivity ac) {
			mAc = ac;
		}
	}

	/*
	 * 下载文件
	 */
	public boolean download(String sRemote, String sLocal) {
		new DownloadFileTask(appContext.m_ice, this, sRemote, sLocal,
				myCallback).execute();
		return true;
	}
	/*
	 * 下载文件
	 */
	public boolean download_reform(String sRemote, String sLocal) {
		new DownloadFileTask(appContext.m_ice, this, sRemote, sLocal,
				myCallbackReform).execute();
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View
				.inflate(this, R.layout.activity_query_view, null);
		setContentView(view);
		myCallback = new MyCallBack(this);
		myCallbackReform = new MyCallBack_reform(this);

		appContext = (AppContext) getApplication();
		imgReform = (ImageView) findViewById(R.id.img_break_law_reform);
		imgBreakLaw = (ImageView) findViewById(R.id.img_break_law_1);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("违规信息");

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
		Button fbSave = (Button) findViewById(R.id.btn_save);
		// fbSave = (Button) findViewById(R.id.btn_save);
		fbSave.setVisibility(View.INVISIBLE);

		// fbSave.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		//
		//
		// new ProcessSaveTask().execute();
		// }
		// }
		//
		// );

		imgBreakLaw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//
				//
				if (sBreakImagePath.length() <= 0)
					return;
				UIHelper.openViewPic(v.getContext(), sBreakImagePath);

			}
		}

		);
		imgReform.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sReformImagePath.length() <= 0)
					return;
				UIHelper.openViewPic(v.getContext(), sReformImagePath);

			}
		}

		);
		new GetDataTask().execute();
	}

	// private int mRowCount = 0;
	private void InitControl() {

		tvProjectDept = (TextView) findViewById(R.id.tx_project_dept);
		txBreakOption = (TextView) findViewById(R.id.tx_break_option);

		txBreakDate = (TextView) findViewById(R.id.tx_break_date);
		txBreakContent = (TextView) findViewById(R.id.tx_break_content);
		txBreakContentReform = (TextView) findViewById(R.id.tx_break_content_reform);

		txCheckStatus = (TextView) findViewById(R.id.tx_check_status);
		
		txRectTime = (TextView) findViewById(R.id.q_rect_time);
	}

	private void getRemoteData() {
		rtnBreakHelp = appContext.getQueryBreakInfo(sBreakRuleID);

		// rtnRectifyHelp = appContext.getReformViewInfo(sBreakRuleID);
		// rtnBRViewHelp = appContext.getBRViewReformInfo(sBreakRuleID);
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
		txBreakDate.setText(rtnBreakHelp.valueStringByName(0, "pic_time"));
		txBreakOption
				.setText(appContext.getBreakRuleType(Integer
						.valueOf(rtnBreakHelp.valueStringByName(0,
								"break_rule_type"))));
		txBreakContent.setText(rtnBreakHelp.valueStringByName(0,
				"break_rule_content"));

		// if ( rtnBRViewHelp.mReturnCode <= 0 ) return;

		if (rtnBreakHelp.mReturnCode <= 0)
			return;

		sRectifyID = rtnBreakHelp.valueStringByName(0, "rectify_id");

		txRectTime.setText(rtnBreakHelp.valueStringByName(0, "pic_time"));
		iCurNode = Integer
				.valueOf(rtnBreakHelp.valueStringByName(0, "node_id"));

		//

		txBreakContentReform.setText(rtnBreakHelp.valueStringByName(0,
				"rectify_content"));

		txCheckStatus.setText(rtnBreakHelp.valueStringByName(0, "node_name"));

		// 违规图片
		String sUpURL = appContext.RemoteFileURL();

		String sLoacalFile = appContext.getPictureDirectory()
				+ rtnBreakHelp.valueStringByName(0, "pic_name");
		sUpURL += rtnBreakHelp.valueStringByName(0, "pic_name");

		if (FileUtils.checkFileExists(sLoacalFile)) {
			imgBreakLaw.setImageURI(Uri.parse(sLoacalFile));
			sBreakImagePath = sLoacalFile;
		} else {
			// sImageURL = sUpURL;
			//rImageHelp.loadImage(imgBreakLaw, sUpURL, sLoacalFile, false);
			mDownloadOK = false;
			mDownloadReform = false;
			download(rtnBreakHelp.valueStringByName(0, "pic_name"),appContext.getPictureDirectory());
			sBreakImagePath = sLoacalFile;
		}

		// 整改图片
		sUpURL = appContext.RemoteFileURL();
		sUpURL += rtnBreakHelp.valueStringByName(0, "rectify_pic_name");
		String sLoacalFileReform = appContext.getPictureDirectory()
				+ rtnBreakHelp.valueStringByName(0, "rectify_pic_name");
		// rImageHelp.loadImage(imgBreakLaw, sUpURL, false);
		sReformImagePath = sLoacalFileReform;

		if (FileUtils.checkFileExists(sLoacalFileReform)) {
			imgReform.setImageURI(Uri.parse(sLoacalFileReform));
			// sFilePath = sLoacalFile;
		} else {
			// sImageURL = sUpURL;
			mDownloadReform = true;
			mDownloadOK_reform = false;
			download_reform(rtnBreakHelp.valueStringByName(0, "rectify_pic_name"),appContext.getPictureDirectory());
			sReformImagePath = sLoacalFileReform;
			//rImageHelp.loadImage(imgReform, sUpURL, sLoacalFileReform, false);
		}

		// 如果没有指定节点的权限，则返回
		if (!appContext.hasNodeRight(iRight)) {
			return;
		}
		iRight = iCurNode;

		// 判断当前流程节点

		//

	}

	public void initProps() {

	}

	private void ProcessSave() {

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
