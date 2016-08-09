package com.winso.break_law.activity;

import java.io.File;

import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.DownloadFileTask;
import com.winso.comm_library.icedb.SelectHelp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 应用程序Activity的基类
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-01
 */
public class AboutActivity extends BaseActivity {
	private String mSApkPath = ""; // APk保存目录
	private AppContext app;
	private Button mBtCheckVersion;
	private String sApkName = "";
	private int miServerVersion = -1;
	private String updateMsg = "有新的版本";
	MyCallBack myCallback;
	public class MyCallBack implements CallbackInterface {
		AboutActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) {

				installApk(responseText);
				return;
			}

		}

		public void cancel(String responseText) {
			UIHelper.showMsg(AboutActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack(AboutActivity ac) {
			mAc = ac;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		final View view = View.inflate(this, R.layout.activity_about, null);
		setContentView(view);
		app = (AppContext) getApplication();
		myCallback = new MyCallBack(this);

		mSApkPath = Environment.getExternalStorageDirectory().getAbsolutePath();

		// 判断是否挂载了SD卡
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
			mSApkPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/winso/Update/";
			File file = new File(mSApkPath);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		
		
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);

			String curVersionName = info.versionName;

			String sFullText = "版本：";
			sFullText += curVersionName;

			TextView fV = (TextView) findViewById(R.id.about_version);

			fV.setText(sFullText);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 抓图
		mBtCheckVersion = (Button) findViewById(R.id.break_rule_content_reform);
		mBtCheckVersion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				check();
			}
		});
	}

	public boolean check() {
		// 检查新版本
		// 网络连接判断
		if (!app.isNetworkConnected())
			UIHelper.showMsg(this, "", "网络未连接");

		if (app.isCheckUp()) {
			// UpdateManager mgr = UpdateManager.getUpdateManager();

			// mgr.checkAppUpdate(app, this, true,myCallback);
			Dialog noticeDialog;

			int iC = getNeedUpgrade();
			if (iC == 1) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("软件版本更新");
				builder.setMessage(updateMsg + " " + miServerVersion);
				builder.setPositiveButton("立即更新",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

								// new
								// DownloadFileTask(app.m_ice,mContext,"release/hello.txt","/mnt/sdcard/",myCallBack).execute();
								downloadApk();
							}
						});
				builder.setNegativeButton("以后再说",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				noticeDialog = builder.create();
				noticeDialog.show();
			} else if (iC == 2) {

				installApk(mSApkPath + "/" + sApkName);
			}
			else
			{
				UIHelper.showMsg(this, "", "没有新的版本");
			}

		}
		return true;
	}

	void downloadApk() {
		sApkName = "Break-law" + ".apk";
		

		// UIHelper.showMsg(this, "", app.DEFAULT_DB_SAVE_PATH());

		new DownloadFileTask(app.m_ice, this, "release/" + sApkName, mSApkPath,
				myCallback).execute();
	}
	/*
	 * 用于下载Apk 返回０代表没有新版本，１代表有新版本则没有下载，２代表有新版本但已经下载
	 */

	private int getNeedUpgrade() {
		try {

			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			// curVersionCode = info.versionCode;
			miServerVersion = app.getServerVersion();

			sApkName = "Break-law" + ".apk";

			if (miServerVersion <= 0)
				return 0;

			if (miServerVersion > info.versionCode) {
				File ApkFile = new File(mSApkPath + "/" + sApkName);
				if (ApkFile.exists()) {
					SelectHelp help = app.m_ice
							.getFileInfo(AppContext.mSUpgradePath + "/"
									+ sApkName);
					if (help.size() > 0) {
						if (FileUtils.getFileSize(mSApkPath + "/" + sApkName) == Integer
								.valueOf(help.valueStringByName(0, "size"))) {
							return 2;
						}
					}

					return 1;
				}
				return 1;
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}

		return 0;
	}
	
	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk(String sFile) {
		File apkfile = new File(sFile);
		if (!apkfile.exists()) {
			return;
		}

		// UIHelper.showMsg(MainActivity.this, "", apkfile.toString());

		Intent i = new Intent(Intent.ACTION_VIEW);

		// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		startActivity(i);
	}
}
