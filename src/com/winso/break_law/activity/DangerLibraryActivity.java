package com.winso.break_law.activity;

import java.util.ArrayList;
import java.util.List;

import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.util.DangerSelectAdapter;
import com.winso.break_law.util.DangerSelectAdapter.ViewHolder;
import com.winso.comm_library.CItem;
import com.winso.comm_library.icedb.SelectHelp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class DangerLibraryActivity extends BaseActivity {
	// private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;

	AppContext app;
//	Button fbSave;
	private Spinner mSpDangerType;
	DangerSelectAdapter mAdapter;

	class DangerSpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// UIHelper.showMsg(ActionlibraryActivity.this, "",
			// ((CItem) mSpCheckItem.getSelectedItem()).GetID());
			// InitCheckItem(((CItem)
			// mSpSafeSubItem.getSelectedItem()).GetID());
			new GetDataTask().execute();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}

	}

	private void InitDangeType() {

		// 获取类型
		// mDangerType
		List<CItem> lst = new ArrayList<CItem>();

		for (int i = 0; i < app.mDangerType.size(); i++) {
			CItem ct = new CItem(app.mDangerType.valueStringByName(i,
					"hazard_type_id"), app.mDangerType.valueStringByName(i,
					"hazard_type_name"));
			lst.add(ct);
		}

		ArrayAdapter<CItem> Adapter = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);

		mSpDangerType.setAdapter(Adapter);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_danger_library);
		app = (AppContext) getApplication();

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("危险源辨识库");

		mSpDangerType = (Spinner) findViewById(R.id.sp_danger_type);
		mSpDangerType
				.setOnItemSelectedListener(new DangerSpinnerSelectedListener());
		InitDangeType();

		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		initListView();
		InitDangeType();

		// 设置保存信息
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SAVE);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ProcessSave();			
			}
		}

		);

	}

	private void ProcessSave() 
	{
		 //数据是使用Intent返回
        Intent it = getIntent();
     
        it.putExtra("detail_id", "xxx");
        it.putExtra("hazard_content", mAdapter.getNameStrings());
        //it.putExtra("hazard_id", mAdapter.getNames());
		//it.putStringArrayListExtra("hazard_id", info);  
		//it.putStringArrayListExtra("hazard_content", infoNames);  
		setResult(Activity.RESULT_OK, it);

		
		finish();

		
	}

	protected void reLoadView(SelectHelp help) {
		// actualListView.setAdapter(null);

		// get_break_last_view
		// Thread.sleep(2000);

		mAdapter.updateData(help);

	}

	protected void initListView() {
		actualListView = (ListView) findViewById(R.id.list_danger);

		// mPullRefreshListView.setPullLabel("刷新.....");

		// Set a listener to be invoked when the list should be refreshed.
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		mAdapter = new DangerSelectAdapter(this);
		actualListView.setAdapter(mAdapter);
		actualListView.setItemsCanFocus(false);
		actualListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("static-access")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder vHollder = (ViewHolder) view.getTag();
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
				SelectHelp help = app.getHazardContent(((CItem) mSpDangerType
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

			// Call onRefreshComplete when the list has been refreshed.
			
			super.onPostExecute(result);
		}
	}

}
