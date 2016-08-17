package com.winso.break_law.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.icedb.SelectHelp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class SelectProjectActivity extends BaseActivity {
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	ArrayList<HashMap<String, Object>> mListItem;
	SimpleAdapter mListItemAdapter;
	AppContext app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_project);
		app = (AppContext) getApplication();
		
		TextView vt= (TextView)findViewById(R.id.tx_header_title);
		
		vt.setText("请选择项目");
		
		initListView();
	}

	protected void reLoadView() throws Exception {
		// actualListView.setAdapter(null);

		// get_break_last_view
		// Thread.sleep(2000);

		if (!app.m_ice.isLogin()) {
			if (!app.m_ice
					.login(app.getCookie("cent_ip"), AppContext.CENT_PORT)) {
				UIHelper.showMsg(this, "", "连接中心失败:" + app.getCookie("cent_ip"));
				return;
			}
		}

		SelectHelp rtnValue = app.getProject();

		// 生成动态数组，加入数据
		// ArrayList<HashMap<String, Object>> mListItem = new
		// ArrayList<HashMap<String, Object>>();

		mListItem.clear();

		for (int i = 0; i < rtnValue.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("list_item_title",
					rtnValue.valueStringByName(i, "org_name"));
			map.put("org_id", rtnValue.valueStringByName(i, "org_id"));

			map.put("btn_save_sel", R.drawable.btn_click_pro);
			mListItem.add(map);
		}

	}

	protected void initListView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list_project);

		// mPullRefreshListView.setPullLabel("刷新.....");

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				new GetDataTask().execute();
			}
		});

		actualListView = mPullRefreshListView.getRefreshableView();

		actualListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				TextView tv = (TextView) view.findViewById(R.id.detail_id);
				TextView orgName = (TextView) view
						.findViewById(R.id.org_name);

				Intent it = getIntent();
				it.putExtra("org_id", tv.getText().toString());
				setResult(Activity.RESULT_OK, it);

				app.setCookie("org_id", tv.getText().toString());
				app.setCookie("org_name", orgName.getText().toString());
				finish();

			}
		});

		mListItem = new ArrayList<HashMap<String, Object>>();
		mListItemAdapter = new SimpleAdapter(this, mListItem,// 数据源
				R.layout.list_view_select_project,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "list_item_title", "org_id", "btn_save_sel" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.org_name, R.id.detail_id,
						R.id.btn_save_sel });

		// 添加并且显示
		actualListView.setAdapter(mListItemAdapter);

		try {
			reLoadView();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { "Abbaye de Belloc",
				"Abbaye du Mont des Cats" };

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				reLoadView();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			mListItemAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

}
