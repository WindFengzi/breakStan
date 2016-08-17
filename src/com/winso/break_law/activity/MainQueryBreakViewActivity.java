package com.winso.break_law.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.winso.break_law.R;
import com.winso.break_law.activity.MainActivity.MyCallBack;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.icedb.SelectHelp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainQueryBreakViewActivity extends BaseActivity {
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	ArrayList<HashMap<String, Object>> mListItem;
	SimpleAdapter mListItemAdapter;
	public boolean bIsWorking;
	AppContext appContext;
	MyCallBack myCallback; //

	private Button fbReturn;
	SelectHelp rtnValue = new SelectHelp();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_break_view);
		appContext = (AppContext) getApplication();
		
		
		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("综合查询");

		initListView();

		// 返回
		fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);

		// 查询记录
		fbSave = (Button) findViewById(R.id.btn_save);
		//fbSave.setText("条件");
//		Drawable drawable= getResources().getDrawable(R.drawable.cond);
//		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//		fbSave.setCompoundDrawables(drawable,null,null,null);
		getRightChangeBtn(RIGHT_SEARCH);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// ProcessSave("");
				Intent intent = new Intent(v.getContext(),
						QueryConditionActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_QUERY_CONDITION);
			}
		}

		);

		new GetDataTask().execute();
	}

	
	
	

	
	/*
	 * 处理函数
	 */
	private OnItemClickListener listListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// When clicked, show a toast with the TextView text
			TextView tv = (TextView) view.findViewById(R.id.break_rule_id);
			TextView tvContent = (TextView) view
					.findViewById(R.id.break_rule_content);
			TextView txOrgName = (TextView) view.findViewById(R.id.org_name);

			Intent intent = new Intent(view.getContext(),
					QueryViewActivity.class);
			intent.putExtra("break_rule_id", tv.getText().toString());
			intent.putExtra("break_rule_content", tvContent.getText()
					.toString());

			intent.putExtra("org_name", txOrgName.getText().toString());
			// intent.putExtra("break_type_name",
			// appContext.getBreakRuleType(Integer.valueOf(tv.getText().toString())));

			startActivityForResult(intent, UIHelper.INTENT_CATHE_REFORM_MODIFY);

		}
	};

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		
		new GetDataTask().execute();
		
	}

	// 加载中间的视图
	protected void reLoadView() throws Exception {

		// SelectHelp rtnValue = appContext.getBreakLastView();

		rtnValue = appContext.getAllBreakView();

		// 生成适配器的Item和动态数组对应的元素

		//
	}

	private void updateUIView() {
		mListItem.clear();

		for (int i = 0; i < rtnValue.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("org_name", rtnValue.valueStringByName(i, "org_name"));
			map.put("break_rule_content",
					rtnValue.valueStringByName(i, "break_rule_content"));

			map.put("break_rule_id",
					rtnValue.valueStringByName(i, "break_rule_id"));

			map.put("node_name", rtnValue.valueStringByName(i, "node_name"));

			map.put("break_time", rtnValue.valueStringByName(i, "update_time")
					.substring(5, 16));

			// map.put("list_item_sub",
			// rtnValue.valueStringByName(i, "break_rule_type"));
			map.put("btn_save_sel", R.drawable.list_logo);
			mListItem.add(map);
		}
	}

	protected void initListView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lt_reform_sub);
		actualListView = mPullRefreshListView.getRefreshableView();

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				if (!bIsWorking) {
					new GetDataTask().execute();
				}
			}
		});

		actualListView.setOnItemClickListener(listListener);

		mListItem = new ArrayList<HashMap<String, Object>>();
		mListItemAdapter = new SimpleAdapter(this, mListItem,// 数据源
				R.layout.list_view_break_view_main,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "org_name", "break_rule_content",
						"break_rule_id", "node_name", "break_time",
						"btn_save_sel" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.org_name, R.id.break_rule_content,
						R.id.break_rule_id, R.id.node_name,
						R.id.tx_check_status, R.id.btn_save_sel });

		// 添加并且显示
		actualListView.setAdapter(mListItemAdapter);

	}

	// 执行刷新任务
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { "Abbaye de Belloc",
				"Abbaye du Mont des Cats" };

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				bIsWorking = true;
				reLoadView();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			bIsWorking = false;
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			mListItemAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			bIsWorking = false;
			updateUIView();
			super.onPostExecute(result);
		}
	}
}
