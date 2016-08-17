package com.winso.break_law.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import com.winso.comm_library.AssetsDatabaseManager;
import com.winso.comm_library.EasyLog;
import com.winso.comm_library.SQLiteUtil;
import com.winso.comm_library.StringUtils;
import com.winso.comm_library.TimeZoneUtil;
import com.winso.comm_library.TinyDB;
import com.winso.comm_library.icedb.ICEDBUtil;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.icedb.SelectHelpParam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * 应用程序业务访问类，用于访问各类业务组件等
 * 
 * @author eric goo
 * @version 1.0
 * @created 2014-09-01
 */

public class AppContext extends Application {

	// 版本发布的目录
	public static String mSUpgradePath = "release"; // 版本发布的服务器目录

	// 所有的权限代码
	//
	public static final int RIGHT_CATHE = 1052; // 违规抓拍
	public static final int RIGHT_BREAK_MODIFY = 1063; // 违规批阅
	public static final int RIGHT_REFORM = 1057; // 整改抓拍
	public static final int RIGHT_MODIFY_REFORM = 1065; // 整改批阅
	// public static final int RIGHT_QUERY=1133; //查询
	// 权限定义------------------------------------------------
	// public static final int RIGHT_BREAK_TULE = "1052";//违规抓拍权限
	// public static final int RIGHT_REVIEW_BREAK_TULE = "1063";//批阅违规权限
	// public static final int RIGHT_RECTIFY = "1057";//整改抓拍权限
	// public static final int RIGHT_REVIEW_RECTIFY = "1065";//批阅整改权限
	public static final int RIGHT_QUERY = 1044;// 查询权限

	public static final int RIGHT_BR_REVIEW_1 = 1147;// 违规初级批阅权限
	public static final int RIGHT_BR_REVIEW_2 = 1148;// 违规中级批阅权限
	public static final int RIGHT_BR_REVIEW_3 = 1149;// 违规高级批阅权限
	public static final int RIGHT_BR_REVIEW_4 = 1150;// 违规最高级批阅权限

	public static final int RIGHT_RECTIFY_REVIEW_1 = 1147;// 整改初级批阅权限
	public static final int RIGHT_RECTIFY_REVIEW_2 = 1148;// 整改中级批阅权限
	public static final int RIGHT_RECTIFY_REVIEW_3 = 1149;// 整改高级批阅权限
	public static final int RIGHT_RECTIFY_REVIEW_4 = 1150;// 整改最高级批阅权限

	// iType=0时代表是
	public String getUserNodeRight(int iType) {
		String sDest = "";
		if (iType == 0) {
			if (hasRight(RIGHT_BR_REVIEW_1)) {
				sDest += "2,";
			}
			if (hasRight(RIGHT_BR_REVIEW_2)) {
				sDest += "3,";
			}
			if (hasRight(RIGHT_BR_REVIEW_3)) {
				sDest += "4,";
			}
			if (hasRight(RIGHT_BR_REVIEW_4)) {
				sDest += "5,";
			}

			if (sDest.length() <= 0)
				return "";

			if (sDest.substring(sDest.length() - 1, sDest.length()).equals(",")) {
				return sDest.substring(0, sDest.length() - 1);

			}
			return sDest;
		} else if (iType == 1) {
			if (hasRight(RIGHT_RECTIFY_REVIEW_1)) {
				sDest += "7,";
			}
			if (hasRight(RIGHT_RECTIFY_REVIEW_2)) {
				sDest += "8,";
			}
			if (hasRight(RIGHT_RECTIFY_REVIEW_3)) {
				sDest += "9,";
			}
			if (hasRight(RIGHT_RECTIFY_REVIEW_4)) {
				sDest += "10,";
			}

			if (sDest.length() <= 0)
				return "";

			if (sDest.substring(sDest.length() - 1, sDest.length()).equals(",")) {
				return sDest.substring(0, sDest.length() - 1);

			}
			return sDest;
		}

		return sDest;
	}

	// 流程节点定义
	public static final int FLOW_NODE_FINISH = 0;// 流程结束
	public static final int FLOW_NODE_CACHE = 1;// 违规视频抓拍
	// public static final int FLOW_NODE_VIEW = 1;// 违规视频抓拍
	public static final int FLOW_NODE_VIEW_1 = 2;// 违规初级批阅
	public static final int FLOW_NODE_VIEW_2 = 3;// 违规中级批阅
	public static final int FLOW_NODE_VIEW_3 = 4;// 违规高级批阅
	public static final int FLOW_NODE_VIEW_4 = 5;// 违规最高级批阅

	public static final int FLOW_NODE_REFORM = 6;// 整改视频抓拍
	public static final int FLOW_NODE_REFORM_1 = 7;// 整改初级批阅
	public static final int FLOW_NODE_REFORM_2 = 8;// 整改中级批阅
	public static final int FLOW_NODE_REFORM_3 = 9;// 整改高级批阅
	public static final int FLOW_NODE_REFORM_4 = 10;// 整改最高级批阅

	public static final int FLOW_CHECK_PASS = 0;// 整改中级批阅
	public static final int FLOW_CHECK_CANNOT = 1;// 整改高级批阅
	public static final int FLOW_CHECK_NO_NEED_REFORM = 2;// 整改最高级批阅

	public static final int FLOW_CHECK_REOFRM_PASS = 0;// 审核通过
	public static final int FLOW_CHECK_REFORM_CANNOT = 1;// 审核未通过
	public static final int FLOW_CHECK_REFORM_NO_PASS = 2;// 不通过

	// SEQUENCE值
	public static final String SEQ_break_rule_id = "break_rule_id";
	public static final String SEQ_rectify_id = "rectify_id";
	public static final String SEQ_review_id = "review_id";
	public static final String SEQ_pic_id = "pic_id";

	//

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	public static final String STORE_PIC_PATH = "break_law_pic";

	public static final String S_REMOTE_PIC_PATH = "/upfile/upload/";
	public static final int CENT_PORT = 8840;

	// 配置的数据据表结构
	public static final String[] sSyncTables = { "BR_HAZARD", "BR_CHECK_ITEM",
			"BR_CHECK_ITEM_DETAIL", "BR_HAZARD_TYPE" };
	public static final boolean[] bSyncDatas = { true, true, true, false };
	//
	public String mUserName = "admin";
	public String mUserRight;

	public SelectHelp mHelpRight = new SelectHelp(); // 用户权限列表

	private Map<String, String> mSQLMap = new HashMap<String, String>();
	// 用于存储key value的对应值
	private Map<String, String> mMap = new HashMap<String, String>();
	// login_user,login_pass,cent_ip,cent_port

	// 用于访问本地的数据库
	public SQLiteUtil mSQLiteDB = new SQLiteUtil();
	public String mLongitude = ""; // 经度
	public String mLatitude = ""; // 纬度

	public String DEFAULT_DB_SAVE_PATH() {
		// return getApplicationContext().getFilesDir().getPath() +
		// File.separator;
		return getPictureDirectory();
	}

	public LocationManager mGPS = null;

	public String getGPS() {

		return "";

	}

	public String RemoteFileURL() {
		String sUpURL = "http://";
		sUpURL += getCookie("http_server");
		sUpURL += ":";
		sUpURL += getCookie("http_port");
		sUpURL += S_REMOTE_PIC_PATH;

		return sUpURL;
	}

	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	// 通过ＩＤ查找违规类型
	public String getBreakRuleType(int iType) {
		if (iType == 0)
			return "一般违规";

		if (iType == 1)
			return "严重违规";

		if (iType == 1)
			return "重大违规";

		return "一般违规";

	}

	// 安全分项
	public SelectHelp mSafeItemHelp = new SelectHelp();;

	// 检查项目
	public SelectHelp mCheckItemHelp = new SelectHelp();;

	// 危险源类型
	public SelectHelp mDangerType = new SelectHelp();;

	// 用于保存配置信息
	TinyDB mTinydb = null;

	public ICEDBUtil m_ice = new ICEDBUtil();

	public AppContext() {
		super();
		setProp("cent_ip", "www.myxui.com");
	}

	public void initApp() {
		mTinydb = new TinyDB(this);

		AssetsDatabaseManager.initManager(this);

		if (getCookie("cent_ip").length() <= 1) {

			setCookie("cent_ip", "www.zjtouchnet.com");

		}

		if (getCookie("http_port").length() <= 0) {
			setCookie("http_port", "8080");
		}

		if (getCookie("is_check_up").length() <= 0) {
			setCookie("is_check_up", "1");
		}

		// 设置默认查询时间
		// getCurDateTime

		setCookie("start_time", TimeZoneUtil.getDateByDay(10) + " 00:01:01");
		setCookie("end_time", TimeZoneUtil.getCurDate() + " 23:59:59");
		setCookie("break_prop", "99");

	}

	public void convertSQLMap(String sContent) {
		if (sContent == null)
			return;

		mSQLMap.clear();
		String[] vsAll = sContent.split(";");

		for (int i = 0; i < vsAll.length; i++) {
			String[] vSub = vsAll[i].split("@");

			if (vSub.length >= 2) {

				mSQLMap.put(vSub[0].trim(), vSub[1]);
			}
		}
	}

	public String getSQL(String sType) {
		return mSQLMap.get(sType);
	}

	public boolean loadInitConfigure() {

		// 加载 http_server
		String sTmp = m_ice.getConfigure("common", "http_port");
		if (sTmp.length() > 0)
			setCookie("http_port", sTmp);

		sTmp = "";
		sTmp = m_ice.getConfigure("common", "http_server");
		if (sTmp.length() > 0)
			setCookie("http_server", sTmp);

		// 加载数据库文件
		EasyLog.debug(DEFAULT_DB_SAVE_PATH() + "break_law.db");

		if (mSQLiteDB.login(DEFAULT_DB_SAVE_PATH() + "break_law.db") == false) {
			EasyLog.debug(mSQLiteDB.getMsg());
		}
		// if (!syncTables(false)) {

		// }

		// if (mSQLiteDB.loginAsset("break_law_init.db") == false) {
		// EasyLog.debug(mSQLiteDB.getMsg());

		// }

		// 加载配置信息
		// mSafeItemHelp = mSQLiteDB.select("Select * from BR_SAFETY_ITEM");
		// mCheckItemHelp = mSQLiteDB.select("Select * from BR_CHECK_ITEM");
		// mDangerType = mSQLiteDB.select("select * from BR_HAZARD_TYPE");

		mSafeItemHelp = m_ice.select("get_br_safety_item", "");
		mCheckItemHelp = m_ice.select("get_br_check_item", "");

		mDangerType = m_ice.select("get_br_hazard_type", "");

		// EasyLog.debug(mSafeItemHelp.toString());

		// 加载用户权限
		mHelpRight = m_ice.select("get_right", getCookie("login_user"));

		return loadUserInfo();

	}

	/*
	 * 创建初始化的数据库
	 */
	public boolean createInitDB() {

		return true;
	}

	public boolean hasRight(int iRight) {
		String sv = String.valueOf(iRight);

		for (int i = 0; i < mHelpRight.size(); i++) {
			if (mHelpRight.valueStringByName(i, "privilege_code").equals(sv)) {
				return true;
			}
		}

		return false;
	}

	public boolean loadUserInfo() {
		SelectHelp help = m_ice
				.select("get_user_info", getCookie("login_user"));

		if (help.size() > 0) {
			setCookie("user_id", help.valueStringByName(0, "user_id"));
			setCookie("user_org_id", help.valueStringByName(0, "org_id"));
			setCookie("user_real_name", help.valueStringByName(0, "real_name"));
		}
		return true;
	}

	// 获取服务器上Android App的版本号
	public int getServerVersion() {
//		return Integer.parseInt(m_ice.getConfigure("common",
//				"app_android_version"));
		String str = m_ice.getConfigure("common","app_android_version");
//		System.out.println("Appcontext--->" + str + "---");
		return  str == null || str.equals("") ? 0 : Integer.parseInt(m_ice.getConfigure("common",
						"app_android_version"));
	}

	public boolean isCheckUp() {
		String strCookie = getCookie("is_check_up");
		if (strCookie == null || strCookie.equals("") || Integer.parseInt(strCookie) == 0) {
			return false;
		}
		return true;
	}

	public String getDBID(String sType) {

		return m_ice.getID(sType);
	}

	public String getTime() {

		return m_ice.getTime();
	}

	public SelectHelp getItemDetail(String sId) {

		// 加载配置信息
		SelectHelp help = m_ice.select("get_br_check_item_detail", sId);

		// help.changeFiledName("hazard_id", "detail_id");
		// help.changeFiledName("hazard_content", "content");

		return help;
	}

	public SelectHelp getHazardContent(String sType) {

		// 加载配置信息
		return m_ice.select("get_hazard_content", sType);

	}

	public SelectHelp getSubCheckItemHelp(String sItemType, String sID) {
		SelectHelp help = new SelectHelp();
		help.addField("check_item_id");
		help.addField("check_item_name");
		help.addField("check_item_type");

		for (int i = 0; i < mCheckItemHelp.size(); i++) {
			if (sItemType.equals("0")
					&& mCheckItemHelp.valueStringByName(i, "safety_item_id")
							.equals(sID)) {
				Vector<String> vs = new Vector<String>();
				vs.add(mCheckItemHelp.valueStringByName(i, "check_item_id"));
				vs.add(mCheckItemHelp.valueStringByName(i, "check_item_name"));
				vs.add(mCheckItemHelp.valueStringByName(i, "check_item_type"));

				help.addValue(vs);
			} else if (mCheckItemHelp.valueStringByName(i, "safety_item_id")
					.equals(sID)
					&& mCheckItemHelp.valueStringByName(i, "check_item_type")
							.equals(sItemType)) {
				Vector<String> vs = new Vector<String>();
				vs.add(mCheckItemHelp.valueStringByName(i, "check_item_id"));
				vs.add(mCheckItemHelp.valueStringByName(i, "check_item_name"));
				vs.add(mCheckItemHelp.valueStringByName(i, "check_item_type"));

				help.addValue(vs);
			}

		}

		return help;
	}

	/**
	 * 设置Cookie，可以持续保存值
	 * 
	 * @return
	 */
	public void setCookie(String k, String v) {
		mTinydb.putString(k, v);
	}

	public void setCookieInt(String k, int v) {
		mTinydb.putInt(k, v);
	}

	public void setCookieBoolean(String k, boolean v) {
		mTinydb.putBoolean(k, v);
	}

	/**
	 * 查询 Cookie
	 * 
	 * @return 返回查到的Cookie
	 */
	public String getCookie(String k) {
		return mTinydb.getString(k);
	}

	public int getCookieInt(String k) {
		return mTinydb.getInt(k);
	}

	public boolean getCookieBoolean(String k) {
		return mTinydb.getBoolean(k);
	}

	/**
	 * 查询属性值
	 * 
	 * @return
	 */
	public String getProp(String k) {
		return mMap.get(k);
	}

	/**
	 * 设置属性值
	 * 
	 * @return
	 */
	public String setProp(String k, String v) {
		return mMap.put(k, v);
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	// ///////////////////////////////////////////////////////////////////////
	// 应用的数据库查询相关
	// 查询任意的违规，查询界面中使用
	//

	public SelectHelp getQueryBreakInfo(String sID) {
		return m_ice.select("view_break_rect_info", sID);

	}

	public SelectHelp getBreakInfo(String sID) {
		return m_ice.select("get_br_break_rule_info", sID);

	}

	public SelectHelp getBreakViewInfo(String sID) {
		return m_ice.select("get_br_review", sID);

	}

	public SelectHelp getBRViewReformInfo(String sID) {
		return m_ice.select("get_br_review_reform", sID);

	}

	public SelectHelp getReformViewInfo(String sID) {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(sID);
		// helpParam.add(sID);
		return m_ice.select("get_br_rectify_info", helpParam.get());

	}

	// 获取违规信息
	public SelectHelp getBreakLastView(int iNodeID) {
		// if (iType == 0)
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(Integer.toString(iNodeID));

		if (!getCookie("break_prop").equals("99")) {
			helpParam.add(getCookie("break_prop"));
			helpParam.add(getCookie("start_time"));
			helpParam.add(getCookie("end_time"));
			return m_ice.select("get_break_last_view", helpParam.get());
		}

		helpParam.add(getCookie("start_time"));
		helpParam.add(getCookie("end_time"));
		return m_ice.select("get_break_last_view_all", helpParam.get());

	}

	// 获取整改页的列表
	public SelectHelp getReformBreakMainView() {
		// if (iType == 0)
		SelectHelpParam helpParam = new SelectHelpParam();

		// helpParam.add(Integer.toString(iNodeID));

		if (!getCookie("break_prop").equals("99")) {
			helpParam.add(getCookie("break_prop"));
			helpParam.add(getCookie("start_time"));
			helpParam.add(getCookie("end_time"));
			helpParam.add(getCookie("org_id"));
			return m_ice.select("get_break_main_reform_notfull",
					helpParam.get());
		}

		helpParam.add(getCookie("start_time"));
		helpParam.add(getCookie("end_time"));
		helpParam.add(getCookie("org_id"));
		//getCookie("")
		
		return m_ice.select("get_break_main_reform", helpParam.get());

	}

	// 获取违规信息
	public SelectHelp getAllBreakView() {
		// if (iType == 0)
		SelectHelpParam helpParam = new SelectHelpParam();

		if (!getCookie("break_prop").equals("99")) {
			helpParam.add(getCookie("break_prop"));
			helpParam.add(getCookie("start_time"));
			helpParam.add(getCookie("end_time"));
			return m_ice.select("get_all_break_view", helpParam.get());
		}

		helpParam.add(getCookie("start_time"));
		helpParam.add(getCookie("end_time"));
		return m_ice.select("get_all_break_view_all", helpParam.get());

	}

	// 获取违规信息
	public SelectHelp getBreakViewView() {
		// if (iType == 0)
		SelectHelpParam helpParam = new SelectHelpParam();

		String sRight = getUserNodeRight(0);

		// sRight = "1,2,3,4,5";
		if (sRight.length() <= 0)
			return new SelectHelp();

		if (!getCookie("break_prop").equals("99")) {
			helpParam.add(getCookie("break_prop"));
			helpParam.add(getCookie("start_time"));
			helpParam.add(getCookie("end_time"));
			helpParam.add(sRight);

			String sql = " SELECT top 20 a.pic_time,a.node_id,a.pic_name,a.update_time,c.node_name,b.org_name,a.break_rule_id,";
			sql += "a.break_rule_type,substring(break_rule_content,0,100)  as  break_rule_content ";
			sql += " FROM BR_BREAK_RULE_INFO a,T_ORGANIZATION b,FLOW_NODE c ";
			sql += " where a.org_id =b.org_id  and c.node_id=a.node_id ";
			sql += " and a.break_rule_type=:break_rule_type<int> ";
			sql += "and a.update_time>=:start_time<timestamp> and a.update_time<=:end_time<timestamp>  ";
			sql += " and a.node_id in( ";
			sql += sRight;
			sql += ") order by pic_time desc";

			return m_ice.selectByParam(sql, helpParam.get());
		}

		helpParam.add(getCookie("start_time"));
		helpParam.add(getCookie("end_time"));
		helpParam.add(sRight);

		String sql = "";
		sql += "SELECT top 20  a.pic_time,a.node_id,a.pic_name,a.update_time,c.node_name,b.org_name,a.break_rule_id,";
		sql += "	a.break_rule_type,substring(break_rule_content,0,100)  as  break_rule_content ";
		sql += " FROM BR_BREAK_RULE_INFO a,T_ORGANIZATION b,FLOW_NODE c ";
		sql += "where a.org_id =b.org_id  and c.node_id=a.node_id  ";
		sql += "	and a.update_time>=:start_time<timestamp> and a.update_time<=:end_time<timestamp> ";
		sql += " and a.node_id in( ";
		sql += sRight;
		sql += ") order by pic_time desc ";

		return m_ice.selectByParam(sql, helpParam.get());

	}

	// 获取整改批阅信息
	public SelectHelp getBreakRefromView() {
		// if (iType == 0)

		String sRight = getUserNodeRight(1);
		// sRight = "1,2,3,4,5";
		if (sRight.length() <= 0)
			return new SelectHelp();

		SelectHelpParam helpParam = new SelectHelpParam();

		String sql = "";

		if (!getCookie("break_prop").equals("99")) {
			helpParam.add(getCookie("break_prop"));
			helpParam.add(getCookie("start_time"));
			helpParam.add(getCookie("end_time"));

			sql += "SELECT top 20 a.pic_time,a.node_id,a.pic_name,a.update_time,c.node_name,b.org_name,a.break_rule_id,a.break_rule_type,";
			sql += "	substring(break_rule_content,0,100)  as  break_rule_content ";
			sql += " FROM BR_BREAK_RULE_INFO a,T_ORGANIZATION b,FLOW_NODE c  ";
			sql += " where a.org_id =b.org_id  and c.node_id=a.node_id  ";
			sql += " and  a.node_id in ( ";
			sql += sRight;
			sql += ")  and a.break_rule_type=:break_rule_type<int>  ";
			sql += "and a.update_time>=:start_time<timestamp> and a.update_time<=:end_time<timestamp>  ";
			sql += "and a.break_rule_id  in ( select break_rule_id from BR_RECTIFY_INFO  )  ";
			sql += "order by pic_time desc ";

			return m_ice.selectByParam(sql, helpParam.get());
		}

		sql += " SELECT top 20 a.pic_time,a.node_id,a.pic_name,a.update_time,c.node_name,b.org_name,a.break_rule_id,";
		sql += " a.break_rule_type,substring(break_rule_content,0,100)  as  break_rule_content ";
		sql += "   FROM BR_BREAK_RULE_INFO a,T_ORGANIZATION b,FLOW_NODE c  ";
		sql += "   where a.org_id =b.org_id  and c.node_id=a.node_id  ";
		sql += " 	and  a.node_id in ( ";
		sql += sRight;
		sql += " ) and a.update_time>=:start_time<timestamp> and a.update_time<=:end_time<timestamp>  ";
		sql += " and a.break_rule_id  in ( select break_rule_id from BR_RECTIFY_INFO  ) ";
		sql += " order by pic_time desc ";

		helpParam.add(getCookie("start_time"));
		helpParam.add(getCookie("end_time"));
		return m_ice.selectByParam(sql, helpParam.get());

	}

	public SelectHelp getBreakLastView() {
		// if (iType == 0)
		return m_ice.select("get_break_last_view", "");

		// String sql =
		// "SELECT   c.node_name,b.org_name,a.break_rule_id,a.break_rule_type,break_rule_content   as  break_rule_content ";
		// sql += " FROM BR_BREAK_RULE_INFO a,T_ORGANIZATION b,FLOW_NODE c ";
		// sql +=
		// "where a.org_id =b.org_id  and c.node_id=a.node_id order by pic_time desc";
		//
		// return mSQLiteDB.select(sql);

	}

	public SelectHelp getProject() {

		String sSQL = " select * from T_ORGANIZATION a,T_ORG_TYPE b where org_id in (";
		sSQL += " SELECT org_id FROM func_query_project('";
		sSQL += mUserName;
		sSQL += "') ";
		sSQL += ")  and a.org_type_id=b.org_type_id and b.org_type='3'";

		return m_ice.selectByParam(sSQL, "");
	}

	// 增加break rule
	public boolean putBreakLawInfo(String sContent, String sPicName,
			String sPicTime, String sRuleType) {
		//

		// break_rule_id, node_id,
		// org_id, user_id, break_rule_content, pic_name,
		// pic_time, break_rule_type, update_time,
		// Longitude, Latitude
		//
		String sBreakRuldID = m_ice.getID(SEQ_break_rule_id);
		if (sBreakRuldID.length() <= 1)
			return false;

		SelectHelpParam param = new SelectHelpParam();
		param.add(sBreakRuldID);
		param.add(String.valueOf(FLOW_NODE_VIEW_1));

		param.add(getCookie("org_id"));
		param.add(getCookie("user_id"));
		param.add(sContent);
		param.add(sPicName);
		param.add(sPicTime);
		param.add(sRuleType);
		param.add(sPicTime);
		param.add(mLongitude);
		param.add(mLatitude);

		SelectHelp help = m_ice.execCmd("", "put_break_law_info", param.get());

		if (help.mReturnCode < 0)
			return false;

		return true;
	}

	// 增加break rule 至本地
	public boolean putBreakLawInfo_local(String sContent, String sPicName,
			String sPicTime, String sRuleType) {
		//

		// break_rule_id, node_id,
		// org_id, user_id, break_rule_content, pic_name,
		// pic_time, break_rule_type, update_time,
		// Longitude, Latitude,submit_time
		//
		String sBreakRuldID = m_ice.getID(SEQ_break_rule_id);
		if (sBreakRuldID.length() <= 1)
			return false;

		// ArrayList<String> vs = new ArrayList<String>();

		String[] vs = new String[14];// 必须初始化

		vs[0] = sBreakRuldID;
		vs[0] = String.valueOf(FLOW_NODE_CACHE);

		vs[0] = getCookie("org_id");
		vs[0] = getCookie("user_id");
		vs[0] = sContent;
		vs[0] = sPicName;
		vs[0] = sPicTime;
		vs[0] = sRuleType;
		vs[0] = sPicTime;
		vs[0] = "0";
		vs[0] = "0";
		vs[0] = "sPicTime";

		return mSQLiteDB.execSQL(getSQL("put_break_rule_info"), vs);

	}

	/*
	 * 增加整改批阅
	 */
	public boolean insertReformBRView(String sBreakRuleID, String sContent,
			int iViewGrade, int iNodeID, int iPassState, String sRectifyID) {
		String sReviewID = m_ice.getID(SEQ_review_id);

		SelectHelpParam param = new SelectHelpParam();
		param.add(sReviewID);
		param.add(sBreakRuleID);
		param.add(getCookie("user_id"));
		param.add(sContent);
		param.add(String.valueOf(iViewGrade));

		param.add(sRectifyID);

		SelectHelp help = m_ice.execCmd("", "put_br_review", param.get());

		if (help.mReturnCode < 0)
			return false;

		// 更新节点状态
		if (iPassState == AppContext.FLOW_CHECK_REFORM_CANNOT)
			updateReformNode(sBreakRuleID, iNodeID, sRectifyID);

		// 无法判断
		if (iPassState == AppContext.FLOW_CHECK_REFORM_NO_PASS) {
			updateReformNode(sBreakRuleID, AppContext.FLOW_NODE_REFORM,
					sRectifyID);
		}

		// 通过
		if (iPassState == AppContext.FLOW_CHECK_REOFRM_PASS) {
			updateReformNode(sBreakRuleID, 0, sRectifyID);
		}

		// 更新当前插入br_reiw的rectify_id
		return updateReviewID(sReviewID, sRectifyID);
	}

	/*
	 * 增加违规批阅
	 */
	public boolean insertBRView(String sBreakRuleID, String sContent,
			int iViewGrade, int iNodeID, int iPassState) {
		String sReviewID = m_ice.getID(SEQ_review_id);

		SelectHelpParam param = new SelectHelpParam();
		param.add(sReviewID);
		param.add(sBreakRuleID);
		param.add(getCookie("user_id"));
		param.add(sContent);
		param.add(String.valueOf(iViewGrade));
		param.add("0");

		SelectHelp help = m_ice.execCmd("", "put_br_review", param.get());

		if (help.mReturnCode < 0)
			return false;

		// 更新节点状态
		if (iPassState == AppContext.FLOW_CHECK_CANNOT)
			return updateReformNode(sBreakRuleID, iNodeID, "0");

		if (iPassState == AppContext.FLOW_CHECK_NO_NEED_REFORM) {
			return updateReformNode(sBreakRuleID, AppContext.FLOW_NODE_FINISH,
					"0");
		}
		if (iPassState == AppContext.FLOW_CHECK_PASS) {
			return updateReformNode(sBreakRuleID, AppContext.FLOW_NODE_REFORM,
					"0");
		}

		return true;
	}

	/*
	 * 更新违规批阅
	 */
	public boolean updateBRView(String sReviewID, String sContent) {

		SelectHelpParam param = new SelectHelpParam();

		param.add(sContent);
		param.add(getCookie("user_id"));
		param.add(sReviewID);

		SelectHelp help = m_ice.execCmd("", "update_br_review", param.get());

		if (help.mReturnCode < 0)
			return false;

		return true;

	}

	/*
	 * 更新整改信息
	 */
	public boolean updateReformNode(String sBreakRuleID, int iNodeID,
			String sRecifityID) {

		// Update BR_BREAK_RULE_INFO set node_id=:node_id<int>,
		// update_time=:update_time<timestamp> where
		// break_rule_id=:break_rule_id<char[40]>
		//

		SelectHelpParam param = new SelectHelpParam();
		param.add(String.valueOf(iNodeID));
		// param.add(sRecifityID);
		param.add(sBreakRuleID);

		SelectHelp help = m_ice.execCmd("", "update_break_law_node",
				param.get());

		if (help.mReturnCode < 0)
			return false;

		return true;

	}

	/*
	 * 更新整当前br view 的整改编号
	 */
	public boolean updateReviewID(String sReviewID, String sRectifyID) {

		// Update BR_BREAK_RULE_INFO set node_id=:node_id<int>,
		// update_time=:update_time<timestamp> where
		// break_rule_id=:break_rule_id<char[40]>
		//

		SelectHelpParam param = new SelectHelpParam();
		param.add(sRectifyID);
		param.add(sReviewID);

		SelectHelp help = m_ice.execCmd("", "update_br_review_rectify_id",
				param.get());

		if (help.mReturnCode < 0)
			return false;

		return true;

	}

	public boolean putReformInfo(String sBreakRuleID, String sContent,
			String sPicName, String sPicTime) {
		//
		// insert into BR_RECTIFY_INFO (rectify_id,break_rule_id, user_id,
		// rectify_content,
		// pic_name, pic_time, break_rule_type,
		// update_time,Longitude, Latitude, submit_time)
		//

		String sReformID = m_ice.getID(SEQ_rectify_id);
		if (sReformID.length() <= 1)
			return false;

		SelectHelpParam param = new SelectHelpParam();
		param.add(sReformID);
		param.add(sBreakRuleID);
		param.add(getCookie("user_id"));
		param.add(sContent);
		param.add(sPicName);
		param.add(sPicTime);
		// param.add(String.valueOf(iRuleType));
		param.add(sPicTime);
		param.add(mLongitude);
		param.add(mLatitude);

		// param.add(String.valueOf(FLOW_NODE_CACHE));

		// param.add(getCookie("org_id"));

		SelectHelp help = m_ice.execCmd("", "put_br_recify_info", param.get());

		if (help.mReturnCode < 0)
			return false;

		return true;

		// 还需要更新节点状态
		// 更新节点状态

		// return updateReformNode(sBreakRuleID,
		// AppContext.FLOW_NODE_REFORM_1,sReformID);
		// String sql="update BR_REVIEW set rectify_id='";
		// sql+= sReformID +"'";
		// sql+= " where review_id='";
		// sql+= sReformID;
		// sql+= "'";
		//
		//
		// return m_ice.execSQL(sql);

	}

	/*
	 * 更新br_view中字段rectify_id的值，在整改批阅中使用
	 */
	public boolean updateBRViewRectifyID(String sRectifyID, String sReviewID) {
		String sql = "update BR_REVIEW set rectify_id='";
		sql += sRectifyID + "'";
		sql += " where review_id='";
		sql += sReviewID;
		sql += "'";

		return m_ice.execSQL(sql);
	}

	/*
	 * 判断是否是有效的数据库
	 */
	public boolean isValidDatabase() {
		// 加载配置信息
		// mSafeItemHelp = mSQLiteDB.select("Select * from BR_SAFETY_ITEM");
		// mCheckItemHelp = mSQLiteDB.select("Select * from BR_CHECK_ITEM");
		// mDangerType = mSQLiteDB.select("select * from BR_HAZARD_TYPE");
		//
		// String[] sTables =
		// {"BR_SAFETY_ITEM","BR_CHECK_ITEM","BR_CHECK_ITEM_DETAIL","BR_BREAK_RULE_INFO"};

		for (int i = 0; i < sSyncTables.length; i++) {
			String sql = "Select count(1) from ";
			sql += sSyncTables[i];

			SelectHelp help = mSQLiteDB.select(sql);
			if (help.size() <= 0)
				return false;
		}

		return true;

	}

	public boolean syncTables(boolean bForce) {
		// if (!bForce)
		// {
		// if (isValidDatabase())
		// return true;
		// }

		mSQLiteDB.begin();
		// syncTable("BR_BREAK_RULE_INFO");
		for (int i = 0; i < sSyncTables.length; i++) {

			if (!syncTable(sSyncTables[i])) {
				mSQLiteDB.rollback();
				return false;
			}

			if (bSyncDatas[i]) {
				if (!syncTableData(sSyncTables[i])) {
					mSQLiteDB.rollback();
					return false;
				}
			}
		}
		mSQLiteDB.commit();
		//

		// syncTable("BR_CHECK_ITEM");
		// syncTable("BR_CHECK_ITEM_DETAIL");
		// syncTable("BR_HAZARD");
		// BR_HAZARD_TYPE,BR_SAFETY_ITEM,FLOW_NODE,T_ORG_TYPE,T_ORGANIZATION

		return true;
	}

	public boolean syncTableData(String sTable) {

		String sql;
		sql = "select * from " + sTable;
		SelectHelp help = m_ice.selectByParam(sql, "");

		sql = "delete from ";
		sql += sTable;

		if (!mSQLiteDB.execSQL(sql)) {
			EasyLog.error("delete table data error: " + mSQLiteDB.getMsg());
			return false;
		}
		mSQLiteDB.begin();
		sql = "";
		for (int iRow = 0; iRow < help.size(); iRow++) {
			sql = "insert into  " + sTable + "(";
			int i = 0;

			for (i = 0; i < help._fields.size(); i++) {
				sql += help._fields.get(i);

				if (i != (help._fields.size() - 1))
					sql += ",";
				else
					sql += ")";
			}

			sql += " values(";

			for (i = 0; i < help._fields.size(); i++) {
				sql += "'";
				sql += help.valueString(iRow, i);

				if (i != (help._fields.size() - 1))
					sql += "',";
				else
					sql += "')";

			}

			if (!mSQLiteDB.execSQL(sql)) {
				EasyLog.error(mSQLiteDB.getMsg());

			}
		}
		mSQLiteDB.commit();
		return true;
	}

	public boolean syncTable(String sTable) {

		SelectHelp help = m_ice.select("get_remote_sql_struct", sTable);

		if (help.mReturnCode < 0) {
			// UIHelper.showMsg(, title, msg);
			EasyLog.error(help.mReturnError);
			return false;
		}
		String sql = "drop table " + sTable;
		mSQLiteDB.execSQL(sql);

		sql = "create table  " + sTable + " (";

		for (int i = 0; i < help.size(); i++) {
			sql += help.valueStringByName(i, "name");

			sql += "   ";

			if (help.valueStringByName(i, "dtype").length() <= 1) {
				sql += "varchar(100) ";
			} else {
				sql += help.valueStringByName(i, "dtype");
			}

			if (i != (help.size() - 1))
				sql += ",\n";
			else
				sql += ")";

		}

		return mSQLiteDB.execSQL(sql);

	}

	public static boolean isSDAviable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// sd card 可用
			return true;

		}

		return false;
	}

	public String getPictureDirectory() {

		if (isSDAviable()) {
			File directory = null;
			directory = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ AppContext.STORE_PIC_PATH);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			return directory.getAbsolutePath() + "/";
		}

		// 只能存在本地文件夹
		String sDest = getApplicationContext().getFilesDir().getPath()
				+ File.separator + AppContext.STORE_PIC_PATH + File.separator;

		File directory = new File(sDest);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		return sDest;

	}

	// 判断用户是否有指定流程节点某项权限
	public boolean hasNodeRight(int iRightID) {

		return true;
	}

	// ///////////////////////////////////////////////////////////////////////
	
	/**
	 * 添加Activity,退出Activity，和单例模式
	 * @author Hman
	 * @date 2016/8/15
	 * */
	private List<Activity> actList;
	
	public void addActivity(Activity activity) {
		if (null == actList) {
			actList = new ArrayList<Activity>();
		}
		actList.add(activity);
	}

	public void exit() {
		for (Activity activity : actList) {
			activity.finish();
		}
	}
	private static AppContext appContext;
	public static AppContext getInstance() {
		if (appContext == null) {
			appContext = new AppContext();
		}
		return appContext;
	}
}
