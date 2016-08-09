package com.winso.break_law.app;

import com.winso.break_law.activity.AboutActivity;
import com.winso.break_law.activity.SelectProjectActivity;
import com.winso.break_law.activity.SettingActivity;
import com.winso.break_law.activity.SyncActivity;
import com.winso.break_law.activity.ViewPicActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.winso.break_law.R;

public class UIHelper {
	//
	public static final int INTENT_SELECT_PROJECT = 1000;
	public static final int INTENT_CATHE_VIEW = 1001;
	public static final int INTENT_DANGER_VIEW= 1002;
	public static final int INTENT_ACTION_VIEW= 1003;
	public static final int INTENT_SELECT_DATE = 1004;
	public static final int INTENT_CATHE_MANAGER_VIEW = 1005;
	public static final int INTENT_CATHE_MAIN_REFORM = 1006;   //整改主页
	public static final int INTENT_CATHE_REFORM_MODIFY = 1007;  //抓拍整改
	public static final int INTENT_QUERY_CONDITION = 1008;  //查询条件
	
	public static final int INTENT_VIEW_BREAK = 1009;  //批阅违规
	public static final int INTENT_VIEW_BREAK_MODITY = 1010;  //批阅违规修改
	
	public static final int INTENT_VIEW_REFORM = 1011;  //批阅整改
	public static final int INTENT_VIEW_REFORM_MODIFY = 1012;  //批阅整改修改
	public static final int INTENT_VIEW_QUERY = 1013; //查询界面　
	
	public static final int INTENT_SYNC_ACTIONS = 1014; //同步行为库　
	
	public static void showMsg(Context ac, String title, String msg) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ac);

		alertDialog.setTitle(title);
		alertDialog.setIcon(R.drawable.ic_launcher);

		alertDialog.setMessage(msg);

		alertDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});

		alertDialog.show();

	}

	/**
	 * 加载设置对话框.
	 */
	public static void openSetting(Context ct) {
		Intent intent = new Intent(ct, SettingActivity.class);
		ct.startActivity(intent);
	}

	/**
	 * 加载选择项目.
	 */
	public static void openSelectProject(Context ct) {
		Intent intent = new Intent(ct, SelectProjectActivity.class);
		ct.startActivity(intent);
	}

	/**
	 * 加载关于对话框.
	 */
	public static void openAbout(Context ct) {
		Intent intent = new Intent(ct, AboutActivity.class);
		ct.startActivity(intent);
	}
	/**
	 * 加载关于同步对话框.
	 */
	public static void openViewPic(Context ct,String sPicPath) {
		Intent intent = new Intent(ct, ViewPicActivity.class);
		
		intent.putExtra("pic_path", sPicPath);
		
		ct.startActivity(intent);
	}
}
