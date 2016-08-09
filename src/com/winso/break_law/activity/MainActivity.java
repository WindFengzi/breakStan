package com.winso.break_law.activity;

import java.io.File;
import java.util.Iterator;
import com.kbeanie.imagechooser.api.ChooserType;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.provider.Settings;
import org.androidpn.client.ServiceManager;


public class MainActivity extends BaseActivity {

	private String updateMsg = "有新的版本";
	private Button mBtSetting, mBtReform, mBtViewBreak, mBtViewReform,
			mBtQuery;
	private Button mBtCathePhoto;
	AppContext app;
	private String sApkName = "";
	private int miServerVersion = -1;
	private static final String TAG = "GpsActivity";
	private String mSApkPath = ""; // APk保存目录
	MyCallBack myCallback;
	//private TextView txGPS;

	// new
	// DownloadFileTask(app.m_ice,mContext,"release/hello.txt","/mnt/sdcard/",myCallBack).execute();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);
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

		}
		
		//txGPS =  (TextView) findViewById(R.id.tx_gps);

		initGPS();
		
		// 设置信息
		mBtSetting = (Button) findViewById(R.id.btn_setting_2);
		mBtSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.openSetting(v.getContext());

			}
		});

		// 违规抓拍
		mBtCathePhoto = (Button) findViewById(R.id.main_footbar_func_cathe_2);
		mBtCathePhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!app.hasRight(AppContext.RIGHT_CATHE)) {
					UIHelper.showMsg(v.getContext(), "", "你没有抓拍的权限");
					return;
				}
				Intent intent = new Intent(v.getContext(),
						CathePhotoActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_CATHE_VIEW);
			}
		});

		//
		// 查询信息
		mBtQuery = (Button) findViewById(R.id.main_footbar_func_query_2);
		mBtQuery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

//				if ( app.hasRight(AppContext.RIGHT_QUERY) )
//				{
//					UIHelper.showMsg(v.getContext(), "", "你没有查询的权限");
//					return;
//				}
				Intent intent = new Intent(v.getContext(),
						MainQueryBreakViewActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_VIEW_QUERY);
			}
		});

		initButtonFunction();
		
		
		///启动消息服务
	     // Start the service
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
	}

	void updateView(Location location) {

		if(location!=null){
			app.mLongitude = String.valueOf(location.getLongitude());
			app.mLatitude = String.valueOf(location.getLatitude());
			
			//txGPS.setText("设备位置信息\n\n经度：");
			//txGPS.append(String.valueOf(location.getLongitude()));
			//txGPS.append("\n纬度：");
			///txGPS.append(String.valueOf(location.getLatitude()));
        }else{
            //清空EditText对象
        	//txGPS.setText("");
        }
	}

	////////////////////////////////////////////////////////////////////////////
	//GPS 相关信息
	// 位置监听
	private LocationListener locationListener = new LocationListener() {

		/**
		 * 位置信息变化时触发
		 */
		public void onLocationChanged(Location location) {
			updateView(location);
			Log.i(TAG, "时间：" + location.getTime());
			Log.i(TAG, "经度：" + location.getLongitude());
			Log.i(TAG, "纬度：" + location.getLatitude());
			Log.i(TAG, "海拔：" + location.getAltitude());
		}

		/**
		 * GPS状态变化时触发
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			// GPS状态为可见时
			case LocationProvider.AVAILABLE:
				Log.i(TAG, "当前GPS状态为可见状态");
				break;
			// GPS状态为服务区外时
			case LocationProvider.OUT_OF_SERVICE:
				Log.i(TAG, "当前GPS状态为服务区外状态");
				break;
			// GPS状态为暂停服务时
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Log.i(TAG, "当前GPS状态为暂停服务状态");
				break;
			}
		}

		/**
		 * GPS开启时触发
		 */
		public void onProviderEnabled(String provider) {
			Location location = app.mGPS.getLastKnownLocation(provider);
			updateView(location);
		}

		/**
		 * GPS禁用时触发
		 */
		public void onProviderDisabled(String provider) {
			updateView(null);
		}

	};
	 /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
	public boolean initGPS() {
		app.mGPS = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// 判断GPS是否正常启动
		if (!app.mGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			return false;
		}

		// 为获取地理位置信息时设置查询条件
		String bestProvider = app.mGPS.getBestProvider(getCriteria(), true);
		// 获取位置信息
		// 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
		Location location = app.mGPS.getLastKnownLocation(bestProvider);
		updateView(location);
		// 监听状态
		app.mGPS.addGpsStatusListener(listener);
		// 绑定监听，有4个参数
		// 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
		// 参数2，位置信息更新周期，单位毫秒
		// 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
		// 参数4，监听
		// 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

		// 1秒更新一次，或最小位移变化超过1米更新一次；
		// 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
		app.mGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1,
				locationListener);

		return true;
	}
	
	//状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
            //第一次定位
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.i(TAG, "第一次定位");
                break;
            //卫星状态改变
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.i(TAG, "卫星状态改变");
                //获取当前状态
                GpsStatus gpsStatus=app.mGPS.getGpsStatus(null);
                //获取卫星颗数的默认最大值
                int maxSatellites = gpsStatus.getMaxSatellites();
                //创建一个迭代器保存所有卫星 
                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                int count = 0;     
                while (iters.hasNext() && count <= maxSatellites) {     
                    iters.next();     
                    count++;     
                }   
                System.out.println("搜索到："+count+"颗卫星");
                break;
            //定位启动
            case GpsStatus.GPS_EVENT_STARTED:
                Log.i(TAG, "定位启动");
                break;
            //定位结束
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.i(TAG, "定位结束");
                break;
            }
        };
    };
    
	
	/////////////////////////////////////////////////////////////////////////////
	
	void downloadApk() {
		sApkName = "Break-law" + ".apk";
		

		// UIHelper.showMsg(this, "", app.DEFAULT_DB_SAVE_PATH());

		new DownloadFileTask(app.m_ice, this, "release/" + sApkName, mSApkPath,
				myCallback).execute();
	}

	@Override
	protected void onResume() {
		/**
		 * 设置为横屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	void initButtonFunction() {
		// 草搞
		Button fbSaveDraft = (Button) findViewById(R.id.main_footbar_func_draft_2);
		fbSaveDraft.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		}

		);

		// 整改抓拍
		mBtReform = (Button) findViewById(R.id.main_footbar_func_reform_2);
		mBtReform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!app.hasRight(AppContext.RIGHT_CATHE)) {
					UIHelper.showMsg(v.getContext(), "", "你没有整改抓拍的权限");
					return;
				}

				Intent intent = new Intent(v.getContext(),
						ReformMainActivity.class);
				startActivityForResult(intent,
						UIHelper.INTENT_CATHE_MAIN_REFORM);
			}
		});

		// 批阅违规
		mBtViewBreak = (Button) findViewById(R.id.main_footbar_func_read_2);
		mBtViewBreak.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!app.hasRight(AppContext.RIGHT_BREAK_MODIFY)) {
					UIHelper.showMsg(v.getContext(), "", "你没有批阅违规权限");
					return;
				}

				Intent intent = new Intent(v.getContext(),
						MainBreakViewActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_VIEW_BREAK);
			}
		});

		// 批阅整改
		mBtViewReform = (Button) findViewById(R.id.main_footbar_func_draft_2);
		mBtViewReform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (!app.hasRight(AppContext.RIGHT_MODIFY_REFORM)) {
					UIHelper.showMsg(v.getContext(), "", "你没有批阅整改的权限");
					return;
				}

				Intent intent = new Intent(v.getContext(),
						MainReformViewActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_VIEW_REFORM);
			}
		});
	}

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == ChooserType.REQUEST_PICK_PICTURE_OR_VIDEO) {

			return;
		}

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

	public class MyCallBack implements CallbackInterface {
		MainActivity mAc;

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
			UIHelper.showMsg(MainActivity.this, "", "文件不存在或者下载失败");
		}

		MyCallBack(MainActivity ac) {
			mAc = ac;
		}
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
