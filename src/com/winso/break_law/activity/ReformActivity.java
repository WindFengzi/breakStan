package com.winso.break_law.activity;

import java.io.File;

import android.app.Activity;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.winso.break_law.R;
import com.winso.break_law.R.id;
import com.winso.break_law.R.layout;
import com.winso.break_law.R.menu;

import com.winso.break_law.activity.CathePhotoActivity.MyCallBack;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.CItem;
import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.EasyLog;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.DownloadFileTask;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.icedb.UploadFileTask;
import com.winso.comm_library.upload.UploadTask;
import com.winso.comm_library.widget.RemoteImageHelper;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenVideo;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.kbeanie.imagechooser.api.MediaChooserListener;

import android.view.View.OnClickListener;

public class ReformActivity extends BaseActivity implements
		ImageChooserListener {
	private File uploadFile;
	private RemoteImageHelper rImageHelp = new RemoteImageHelper();
	private int chooserType;
	private String sBreakRuleID;
	private TextView mTxContent, mTxReformText;
	private ImageView imgBreakLaw, imgReform;
	private String sBreakType;

	private boolean bProcessUpfile = false;
	private ImageChooserManager imageChooserManager;

	private String sBreakImagePath = "", sReformImagePath = "";
	private SelectHelp rtBreakInfoHelp;
	private int iBreakRuleType;
	private Button fbReturn, fbSave;

	private RadioButton fbCathePhoto, fbImport;

	private int iNodeID;

	AppContext appContext;

	MyCallBack myCallback;
	// 图片下载
	String msDownloadFilePath = "";
	boolean mDownloadOK = false;
	MyCallBack_download myCallbackDownload = null;
	boolean mDownloadReform = true;

	/*
	 * 下载功能
	 */
	public class MyCallBack_download implements CallbackInterface {
		ReformActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) 
			{

				mDownloadOK = true;
				// 存在
				imgBreakLaw.setImageURI(Uri.parse(sBreakImagePath));
				
				return;
			}
			mDownloadOK = false;
		}

		public void cancel(String responseText) {
			UIHelper.showMsg(ReformActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack_download(ReformActivity ac) {
			mAc = ac;
		}
	}

	/*
	 * 下载文件
	 */
	public boolean download(String sRemote, String sLocal) {
		new DownloadFileTask(appContext.m_ice, this, sRemote, sLocal,
				myCallbackDownload).execute();
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reform);
		myCallback = new MyCallBack(this);
		myCallbackDownload = new MyCallBack_download(this);
		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("整改抓拍");

		appContext = (AppContext) getApplication();
		bProcessUpfile = false;
		myCallBack = new MyCallBack(this);
		Intent it = getIntent();

		sBreakRuleID = it.getStringExtra("break_rule_id");

		mTxContent = (TextView) findViewById(R.id.break_rule_content_reform);
		mTxContent.setText(it.getStringExtra("break_rule_content"));
		mTxReformText = (TextView) findViewById(R.id.ed_reform_text);

		imgBreakLaw = (ImageView) findViewById(R.id.img_break_law_1);
		imgReform = (ImageView) findViewById(R.id.img_break_law_reform);

		// 返回
		fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);

		// 抓图
		fbCathePhoto = (RadioButton) findViewById(R.id.photo_picture);
		fbCathePhoto.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				takePicture();

			}
		}

		);
		// 导入
		fbImport = (RadioButton) findViewById(R.id.photo_import);
		fbImport.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				chooseImage();

			}
		}

		);

		//
		// 放大图片
		imgReform.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (sReformImagePath.length() <= 0)
					return;
				UIHelper.openViewPic(v.getContext(), sReformImagePath);
			}
		}

		);
		// 放大图片
		imgBreakLaw.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
				if (sBreakImagePath.length() <= 0)
					return;
				UIHelper.openViewPic(v.getContext(), sBreakImagePath);

			}
		}

		);

		// 保存
		fbSave = (Button) findViewById(R.id.btn_save);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (sReformImagePath.length() <= 0) {
					UIHelper.showMsg(v.getContext(), "", "请先拍照或者选择");
					return;
				}
				if (mTxReformText.getText().toString().length() <= 0) {
					UIHelper.showMsg(v.getContext(), "", "请输入整改内容");
					return;
				}

				upload();

				// ProcessSave("");
			}
		}

		);

		// 加载数据
		new GetDataTask().execute();
	}

	private void chooseImage() {

		chooserType = ChooserType.REQUEST_PICK_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_PICK_PICTURE, AppContext.STORE_PIC_PATH,
				true);
		imageChooserManager.setImageChooserListener(this);
		try {

			sReformImagePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void takePicture() {

		chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_CAPTURE_PICTURE, AppContext.STORE_PIC_PATH,
				true);
		imageChooserManager.setImageChooserListener(this);
		try {

			sReformImagePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Should be called if for some reason the ImageChooserManager is null (Due
	// to destroying of activity for low memory situations)
	private void reinitializeImageChooser() {

		imageChooserManager = new ImageChooserManager(this, chooserType,
				"myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		imageChooserManager.reinitialize(sReformImagePath);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK
				&& (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			if (imageChooserManager == null) {
				reinitializeImageChooser();
			}
			imageChooserManager.submit(requestCode, data);
		} else {

		}
	}

	private boolean ProcessSave(String sPic) {
		// getDBID
		if (appContext.getCookie("org_id").length() <= 0) {
			UIHelper.showMsg(this, "", "请先在系统设计中选择项目");
			return false;
		}

		if (!appContext.putReformInfo(sBreakRuleID, mTxReformText.getText()
				.toString(), sPic, appContext.getTime())) {
			UIHelper.showMsg(this, "", "保存失败");
			return false;
		}

		//

		if (!appContext.updateReformNode(sBreakRuleID,
				AppContext.FLOW_NODE_REFORM_1, sBreakRuleID)) {
			UIHelper.showMsg(this, "", "更新流程失败");
			return false;
		}
		//

		// 更新当前所有br_view的值

		setResult(Activity.RESULT_OK, getIntent());
		finish();
		return true;
		// 传输结果
	}

	private void getRemoteData() {
		rtBreakInfoHelp = appContext.getBreakInfo(sBreakRuleID);
	}

	// 加载历史数据
	private void ProcessLoadData() {

		if (rtBreakInfoHelp.mReturnCode < 0) {
			UIHelper.showMsg(this, "错误", rtBreakInfoHelp.mReturnError);
			return;
		}
		if (rtBreakInfoHelp.mReturnCode == 0) {
			UIHelper.showMsg(this, "错误", "没有找到此编号,可能已经删除");
			return;
		}

		String sUpURL = appContext.RemoteFileURL();

		iBreakRuleType = Integer.valueOf(rtBreakInfoHelp.valueStringByName(0,
				"break_rule_type"));
		iNodeID = Integer.valueOf(rtBreakInfoHelp.valueStringByName(0,
				"node_id"));
		String sFile = FileUtils.getFileName(rtBreakInfoHelp.valueStringByName(
				0, "pic_name"));

		sUpURL += sFile;
		String sLoacalFile = appContext.getPictureDirectory()
				+ rtBreakInfoHelp.valueStringByName(0, "pic_name");
		// rImageHelp.loadImage(imgBreakLaw, sUpURL, false);
		sBreakImagePath = sLoacalFile;

		if (FileUtils.checkFileExists(sLoacalFile)) {
			imgBreakLaw.setImageURI(Uri.parse(sLoacalFile));
			// sFilePath = sLoacalFile;
		} else {
			
			
			mDownloadOK = false;
			download(rtBreakInfoHelp.valueStringByName(0, "pic_name"),appContext.getPictureDirectory());
		
			
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

	/*
	 * 图片选择相关
	 */

	@Override
	public void onImageChosen(final ChosenImage image) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				sReformImagePath = image.getFilePathOriginal().toString();
				// pbar.setVisibility(View.GONE);
				if (image != null) {

					imgReform.setImageURI(Uri.parse(appContext
							.getPictureDirectory()
							+ FileUtils.getFileName(sReformImagePath)));
					// fViewPhoto.setImageURI(Uri.parse(new File(image
					// .getFileThumbnailSmall()).toString()));
				}
			}
		});
	}

	@Override
	public void onError(final String reason) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// pbar.setVisibility(View.GONE);
				// Toast.makeText(MainActivity.this, reason,
				// Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	private MyCallBack myCallBack;

	public class MyCallBack implements CallbackInterface {
		ReformActivity mAc;

		public void func(String responseText) {

			bProcessUpfile = false;

			if (responseText == null)
				return;

			if (responseText.length() <= 1) {
				UIHelper.showMsg(ReformActivity.this, "", "上传文件异常");
				return;
			}

			// Android 必须在 UI 线程中才能修改 UI。

			mAc.bProcessUpfile = false;

			if (responseText.length() <= 14)
				return;

			if (!ProcessSave(responseText)) {
				EasyLog.error("save error");
				return;
			}

			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			finish();

		}

		public void cancel(String responseText) {

			bProcessUpfile = false;
		}

		MyCallBack(ReformActivity ac) {
			mAc = ac;
		}
	}

	/**
	 * 上传文件操作。
	 */
	private void upload() {

		if (bProcessUpfile)
			return;

		bProcessUpfile = true;
		new UploadFileTask(appContext.m_ice, this, sReformImagePath, "",
				myCallBack).execute();
	}

}
