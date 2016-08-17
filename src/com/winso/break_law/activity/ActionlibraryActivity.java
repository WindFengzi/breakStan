package com.winso.break_law.activity;

import java.util.ArrayList;
import java.util.List;

import com.winso.comm_library.*;
import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;
import com.winso.break_law.util.ActionSelectAdapter;
import com.winso.break_law.util.ActionSelectAdapter.ViewHolderAction;
import com.winso.comm_library.icedb.SelectHelp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActionlibraryActivity extends BaseActivity {
	// private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	// ArrayList<HashMap<String, Object>> mListItem;
	// SimpleAdapter mListItemAdapter;
	AppContext app;
	ActionSelectAdapter mAdapter;
	boolean mbRefreshing = false; // 是否已经在加载
	ArrayAdapter<CItem> mAdapterCheckItem = null;
//	private Button fbSave;
	private Spinner mSpCheckItem;
	private Spinner mSpSafeSubItem;
	private Spinner mSpCheckItemType; // 固定值：0、全部1、一般项目；2、保证项目；3、其它项目

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_action_library);

		app = (AppContext) getApplication();
		actualListView = (ListView) findViewById(R.id.pl_list_action);

		// mPullRefreshListView.setPullLabel("刷新.....");

		// Set a listener to be invoked when the list should be refreshed.
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		// 设置标题
		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("行为辨识库");

		mSpSafeSubItem = (Spinner) findViewById(R.id.sp_safe_sub_item);
		mSpSafeSubItem
				.setOnItemSelectedListener(new ActionSpinnerSelectedListener());

		mSpCheckItem = (Spinner) findViewById(R.id.sp_check_item);
		mSpCheckItem
				.setOnItemSelectedListener(new ItemIdSpinnerSelectedListener());

		mSpCheckItemType = (Spinner) findViewById(R.id.sp_item_type);
		mSpCheckItemType
				.setOnItemSelectedListener(new ItemTypeSpinnerSelectedListener());

		InitActionSafeItem();
		InitCheckItemType();

		app = (AppContext) getApplication();

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
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SAVE);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String sValue = "";
				Intent it = getIntent();
				it.putExtra("content", mAdapter.getNameStrings());
				//it.putExtra("detail_id", mAdapter.getNameStrings());
				setResult(Activity.RESULT_OK, it);

				//UIHelper.showMsg(v.getContext(), "", sValue);
				finish();

			}
		}

		);

		initListView();
	}

	protected void reLoadView(SelectHelp help) throws Exception {
		// actualListView.setAdapter(null);

		mAdapter.updateData(help);
	}

	protected void initListView() {

		mAdapter = new ActionSelectAdapter(this);
		actualListView.setAdapter(mAdapter);
		actualListView.setItemsCanFocus(false);
		actualListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolderAction vHollder = (ViewHolderAction) view.getTag();
				// 在每次获取点击的item时将对于的checkbox状态改变，同时修改map的值。
				vHollder.cBox.toggle();
				mAdapter.isSelected.put(position, vHollder.cBox.isChecked());
			}
		});
		
		
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { "Abbaye de Belloc" };

		@Override
		protected String[] doInBackground(Void... params) {

			try {
				
					SelectHelp help = app.getItemDetail(((CItem) mSpCheckItem
							.getSelectedItem()).GetID());

					reLoadView(help);
				
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			mAdapter.notifyDataSetChanged();
			
			//setListViewHeightBasedOnChildren(actualListView);
			super.onPostExecute(result);
		}
	}

	private void InitActionSafeItem() {

		List<CItem> lst = new ArrayList<CItem>();

		for (int i = 0; i < app.mSafeItemHelp.size(); i++) {
			CItem ct = new CItem(app.mSafeItemHelp.valueStringByName(i,
					"safety_item_id"), app.mSafeItemHelp.valueStringByName(i,
					"safety_item_name"));
			lst.add(ct);
		}

		ArrayAdapter<CItem> Adapter = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);

		mSpSafeSubItem.setAdapter(Adapter);

	}

	class ActionSpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// UIHelper.showMsg(ActionlibraryActivity.this, "",
			// ((CItem) mSpCheckItem.getSelectedItem()).GetID());
			InitCheckItem(((CItem) mSpSafeSubItem.getSelectedItem()).GetID());

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	class ItemIdSpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// UIHelper.showMsg(ActionlibraryActivity.this, "",
			// ((CItem) mSpCheckItem.getSelectedItem()).GetID());
			new GetDataTask().execute();

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	private void InitCheckItem(String sID) {

		// 获取类型
		String sItemType = ((CItem) mSpCheckItemType.getSelectedItem()).GetID();

		SelectHelp help = app.getSubCheckItemHelp(sItemType, sID);
		List<CItem> lst = new ArrayList<CItem>();

		for (int i = 0; i < help.size(); i++) {
			CItem ct = new CItem(help.valueStringByName(i, "check_item_id"),
					help.valueStringByName(i, "check_item_name"));
			lst.add(ct);
		}

		mAdapterCheckItem = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);
		mSpCheckItem.setAdapter(mAdapterCheckItem);

		if ( mSpCheckItem.getCount() <= 0 )
		{
			SelectHelp h2 = new SelectHelp();
			mAdapter.updateData(h2);
		}
		
	}

	private void InitCheckItemType() {

		List<CItem> lst = new ArrayList<CItem>();

		CItem ct1 = new CItem("0", "全部");
		lst.add(ct1);

		CItem ct2 = new CItem("1", "一般项目");
		lst.add(ct2);

		CItem ct3 = new CItem("2", "保证项目");
		lst.add(ct3);

		CItem ct4 = new CItem("3", "其它项目");
		lst.add(ct4);

		ArrayAdapter<CItem> Adapter = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);

		mSpCheckItemType.setAdapter(Adapter);
	}

	class ItemTypeSpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// UIHelper.showMsg(ActionlibraryActivity.this, "",
			// ((CItem) mSpCheckItem.getSelectedItem()).GetID());
	
			InitCheckItem(((CItem) mSpSafeSubItem.getSelectedItem()).GetID());
	
			

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

}
