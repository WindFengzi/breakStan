package com.winso.break_law.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.comm_library.icedb.SelectHelp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 整改的主界面
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-01
 */
public class ReformMainActivity extends BaseActivity {
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	ArrayList<HashMap<String, Object>> mListItem;
	SimpleAdapter mListItemAdapter;

	public boolean bIsWorking;
	AppContext appContext;

	SelectHelp rtnValue  = new SelectHelp();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bIsWorking = false;
		appContext = (AppContext) getApplication();
		final View view = View.inflate(this, R.layout.activity_reform_main,
				null);
		setContentView(view);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("待整改拍照列表");

		// 修改保存为条件
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SEARCH);
		//bSave.setText("条件");
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(v.getContext(),
						QueryConditionActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_QUERY_CONDITION);

			}
		}

		);

		Button fbBack = (Button) findViewById(R.id.btn_back);
		fbBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				finish();

			}
		}

		);

		initListView();

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

			Intent intent = new Intent(view.getContext(), ReformActivity.class);
			intent.putExtra("break_rule_id", tv.getText());
			intent.putExtra("break_rule_content", tvContent.getText());

			startActivityForResult(intent, UIHelper.INTENT_CATHE_REFORM_MODIFY);

		}
	};

	private void updateUIView()
	{
		mListItem.clear();

		for (int i = 0; i < rtnValue.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("org_name", rtnValue.valueStringByName(i, "org_name"));
			map.put("break_rule_content",
					rtnValue.valueStringByName(i, "break_rule_content"));

			map.put("break_rule_id",
					rtnValue.valueStringByName(i, "break_rule_id"));

			map.put("node_name", rtnValue.valueStringByName(i, "node_name"));

			map.put("break_time", rtnValue.valueStringByName(i, "update_time").substring(5, 16));

			//2014-01-01 10:10:10
			// map.put("list_item_sub",
			// rtnValue.valueStringByName(i, "break_rule_type"));
			map.put("btn_save_sel", R.drawable.list_logo);
			mListItem.add(map);
		}
		
		
		// 生成适配器的Item和动态数组对应的元素
	}
	// 加载中间的视图
	protected void reLoadView() throws Exception {

		// SelectHelp rtnValue = appContext.getBreakLastView();

		rtnValue = appContext.getReformBreakMainView();
		
		
		

		//
	}

	protected void initListView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lt_reform_sub);
		actualListView = mPullRefreshListView.getRefreshableView();

		//mPullRefreshListView.setMode(PullToRefreshBase.MODE_BOTH);//两端刷新
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				if ( !bIsWorking ) {
					new GetDataTask().execute();
				}
			}
		});

		actualListView.setOnItemClickListener(listListener);

		mListItem = new ArrayList<HashMap<String, Object>>();
		mListItemAdapter = new SimpleAdapter(this, mListItem,// 数据源
				R.layout.list_view_main_activity,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "org_name", "break_rule_content",
						"break_rule_id", "node_name","break_time", "btn_save_sel" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.org_name, R.id.break_rule_content,
						R.id.break_rule_id, R.id.node_name,R.id.tx_check_status, R.id.btn_save_sel });

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

				
			updateUIView();
			
			
			bIsWorking = false;
			
			super.onPostExecute(result);
		}
	}

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == UIHelper.INTENT_QUERY_CONDITION) {

			new GetDataTask().execute();
			return;
		}

		// 选择Action
		if (requestCode == UIHelper.INTENT_CATHE_REFORM_MODIFY) {

			new GetDataTask().execute();
			return;
		}

	}
	
	
	
}
