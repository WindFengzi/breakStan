package com.winso.break_law.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.winso.break_law.R;
import com.winso.break_law.activity.MainActivity;
import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.EasyLog;
import com.winso.comm_library.icedb.DownloadFileTask;

/**
 * 应用程序更新工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 */
public class UpdateManager
{

	private static final int DOWN_NOSDCARD = 0;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	private static final int DIALOG_TYPE_LATEST = 0;
	private static final int DIALOG_TYPE_FAIL = 1;

	private static UpdateManager updateManager;

	private boolean bProcessUpfile = false;
	private AppContext mContext;
	// 通知对话框
	private Dialog noticeDialog;
	// 下载对话框
	private Dialog downloadDialog;
	// '已经是最新' 或者 '无法获取最新版本' 的对话框
	private Dialog latestOrFailDialog;
	// 进度条
	private ProgressBar mProgress;
	// 显示下载数值
	private TextView mProgressText;
	// 查询动画
	private ProgressDialog mProDialog;
	// 进度值
	private int progress;
	// 下载线程
	private Thread downLoadThread;
	// 终止标记
	private boolean interceptFlag;
	// 提示语
	private String updateMsg = "有新的版本";
	// 返回的安装包url
	private String apkUrl = "";
	// 下载包保存路径
	private String savePath = "";
	// apk保存完整路径
	private String apkFilePath = "";
	// 临时下载文件路径
	private String tmpFilePath = "";
	// 下载文件大小
	private String apkFileSize;
	// 已下载文件大小
	private String tmpFileSize;
	
	private Context mActivity;

	private String curVersionName = "";
	public int curVersionCode, iServerVersion = -1;
	// private String mUpgradeVersion="1.0"; //版本号

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				mProgressText.setText(tmpFileSize + "/" + apkFileSize);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			case DOWN_NOSDCARD:
				downloadDialog.dismiss();
				Toast.makeText(mContext, "无法下载安装文件，请检查SD卡是否挂载", 3000).show();
				break;
			}
		};
	};

	public static UpdateManager getUpdateManager()
	{
		if (updateManager == null)
		{
			updateManager = new UpdateManager();
		}
		updateManager.interceptFlag = false;
		return updateManager;
	}

	/**
	 * 检查App更新
	 * 
	 * @param context
	 * @param isShowMsg
	 *            是否显示提示消息
	 * @return
	 */
	public boolean checkAppUpdate(AppContext context,Activity ac,
			final boolean isShowMsg,CallbackInterface myCallBack)
	{
		mActivity = ac;
		
		this.mContext = context;
		getCurrentVersion();
	
		iServerVersion = context.getServerVersion();
		if (iServerVersion > 0)
		{
			if (curVersionCode <= iServerVersion)
			{
				//
				//showNoticeDialog(context,ac,isShowMsg,myCallBack);
				
				//final boolean bUpgrade =false;
				AlertDialog.Builder builder = new Builder(mActivity);
				builder.setTitle("软件版本更新");
				builder.setMessage(updateMsg+" " + iServerVersion);
				builder.setPositiveButton("立即更新", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
						
						
						//new DownloadFileTask(context.m_ice,ac,"release/hello.txt","/mnt/sdcard/",myCallBack).execute();
						
						
					}
				});
				builder.setNegativeButton("以后再说", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
				noticeDialog = builder.create();
				noticeDialog.show();
				
				
			}
			else
			{
					//showLatestOrFailDialog(DIALOG_TYPE_LATEST);
			}
		}
		else
		{
			showLatestOrFailDialog(DIALOG_TYPE_FAIL);
		}

		return true;

	}

	/**
	 * 显示'已经是最新'或者'无法获取版本信息'对话框
	 */
	private void showLatestOrFailDialog(int dialogType)
	{
		if (latestOrFailDialog != null)
		{
			// 关闭并释放之前的对话框
			latestOrFailDialog.dismiss();
			latestOrFailDialog = null;
		}
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setTitle("系统提示");
		if (dialogType == DIALOG_TYPE_LATEST)
		{
			builder.setMessage("您当前已经是最新版本");
		} else if (dialogType == DIALOG_TYPE_FAIL)
		{
			builder.setMessage("无法获取版本更新信息");
		}
		builder.setPositiveButton("确定", null);
		latestOrFailDialog = builder.create();
		latestOrFailDialog.show();
	}

	/**
	 * 获取当前客户端版本信息
	 */
	private void getCurrentVersion()
	{
		try
		{
			PackageInfo info = mActivity.getPackageManager().getPackageInfo(
					mActivity.getPackageName(), 0);
			curVersionName = info.versionName;
			curVersionCode = info.versionCode;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace(System.err);
		}
	}

	
	/**
	 * 显示版本更新通知对话框
	 */
	private void showNoticeDialog()
	{
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg+" " + iServerVersion);
		builder.setPositiveButton("立即更新", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	/**
	 * 显示下载对话框
	 */
	private void showDownloadDialog()
	{
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setTitle("正在下载新版本");

		final LayoutInflater inflater = LayoutInflater.from(mActivity);
		View v = inflater.inflate(R.layout.update_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		mProgressText = (TextView) v.findViewById(R.id.update_progress_text);

		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		builder.setOnCancelListener(new OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();

		downloadApk();
	}

	@SuppressLint("NewApi")
	public final static String DEFAULT_DB_PATH = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ "break-law"
			+ File.separator;
	
	private Runnable mdownInitDataRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				String apkName = "breaklaw_init.db";
				String tmpApk = "breaklaw_init.tmp";
				// 判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED))
				{
					savePath = DEFAULT_DB_PATH;
					File file = new File(savePath);
					if (!file.exists())
					{
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}

				// 没有挂载SD卡，无法下载文件
				if (apkFilePath == null || apkFilePath == "")
				{
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File ApkFile = new File(apkFilePath);

				// 是否已下载更新文件
				

				// 输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);

				apkUrl = "http://";
				apkUrl += mContext.getCookie("http_server");
				apkUrl += ":";
				apkUrl += mContext.getCookie("http_port");
				apkUrl += "/upfile/release/";
				apkUrl += apkName;

				EasyLog.debug(apkUrl);

				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				// 显示文件大小格式：2个小数点显示
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

				int count = 0;
				byte buf[] = new byte[1024];

				do
				{
					int numread = is.read(buf);
					count += numread;
					// 进度条下面显示的当前下载文件大小
					tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
					// 当前进度值
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0)
					{
						// 下载完成 - 将临时下载文件转成APK文件
						if (tmpFile.renameTo(ApkFile))
						{
							// 通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载

				fos.close();
				is.close();
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	};
	private Runnable mdownApkRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				String apkName = "BreakLawApp_" + iServerVersion + ".apk";
				String tmpApk = "BreakLawApp_" + iServerVersion + ".tmp";
				// 判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();
				if (storageState.equals(Environment.MEDIA_MOUNTED))
				{
					savePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/BreakLawApp/Update/";
					File file = new File(savePath);
					if (!file.exists())
					{
						file.mkdirs();
					}
					apkFilePath = savePath + apkName;
					tmpFilePath = savePath + tmpApk;
				}

				// 没有挂载SD卡，无法下载文件
				if (apkFilePath == null || apkFilePath == "")
				{
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}

				File ApkFile = new File(apkFilePath);

				// 是否已下载更新文件
				if (ApkFile.exists())
				{
					if ( downloadDialog != null )
						downloadDialog.dismiss();
					installApk();
					return;
				}

				// 输出临时下载文件
				File tmpFile = new File(tmpFilePath);
				FileOutputStream fos = new FileOutputStream(tmpFile);

				apkUrl = "http://";
				apkUrl += mContext.getCookie("http_server");
				apkUrl += ":";
				apkUrl += mContext.getCookie("http_port");
				apkUrl += "/upfile/release/";
				apkUrl += apkName;

				EasyLog.debug(apkUrl);

				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				// 显示文件大小格式：2个小数点显示
				DecimalFormat df = new DecimalFormat("0.00");
				// 进度条下面显示的总文件大小
				apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

				int count = 0;
				byte buf[] = new byte[1024];

				do
				{
					int numread = is.read(buf);
					count += numread;
					// 进度条下面显示的当前下载文件大小
					tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
					// 当前进度值
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0)
					{
						// 下载完成 - 将临时下载文件转成APK文件
						if (tmpFile.renameTo(ApkFile))
						{
							// 通知安装
							mHandler.sendEmptyMessage(DOWN_OVER);
						}
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载

				fos.close();
				is.close();
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
	};
	
	/**
	 * 下载apk
	 * 
	 * @param url
	 */
	private void downloadInitData()
	{
		downLoadThread = new Thread(mdownInitDataRunnable);
		downLoadThread.start();
	}
	/**
	 * 下载apk
	 * 
	 * @param url
	 */
	private void downloadApk()
	{
		//AppContext app = (AppContext)(mContext.getApplicationContext());
		//new DownloadFileTask(app.m_ice,mContext,"release/hello.txt","/mnt/sdcard/",myCallBack).execute();
		String apkName = "BreakLawApp_" + iServerVersion + ".apk";
		String tmpApk = "BreakLawApp_" + iServerVersion + ".tmp";
		// 判断是否挂载了SD卡
		String storageState = Environment.getExternalStorageState();
		if (storageState.equals(Environment.MEDIA_MOUNTED))
		{
			savePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/BreakLawApp/Update/";
			File file = new File(savePath);
			if (!file.exists())
			{
				file.mkdirs();
			}
			apkFilePath = savePath + apkName;
			tmpFilePath = savePath + tmpApk;
		}

		// 没有挂载SD卡，无法下载文件
		if (apkFilePath == null || apkFilePath == "")
		{
			mHandler.sendEmptyMessage(DOWN_NOSDCARD);
			return ;
		}

		File ApkFile = new File(apkFilePath);

		// 是否已下载更新文件
		if (ApkFile.exists())
		{
			if ( downloadDialog != null )
				downloadDialog.dismiss();
			installApk();
			return ;
		}
		
		
		//new DownloadFileTask(context.m_ice,ac,"release/"+apkName,apkFilePath,myCallBack).execute();
	}
	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private void installApk()
	{
		File apkfile = new File(apkFilePath);
		if (!apkfile.exists())
		{
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mActivity.startActivity(i);
	}
	
	
	
	
}
