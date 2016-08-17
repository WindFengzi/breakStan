package com.winso.break_law.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.winso.break_law.app.AppContext;
import com.winso.break_law.R;
import com.winso.comm_library.CItem;
import com.winso.comm_library.TimeZoneUtil;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 查询条件Activity的基类
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-01
 */
public class QueryConditionActivity extends BaseActivity {

	private Spinner mSpinnerProp;
	private Button mBtnStartTime;
	private Button mBtnEndTime;

	private int iStartYear, iStartMonth, iStartDay;
	private int iEndYear, iEndMonth, iEndDay;
	AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_query_condition);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("条件");

		appContext = (AppContext) getApplication();

		iStartYear = iStartMonth = iStartDay = 0;
		iEndYear = iEndMonth = iEndDay = 0;

		initProps();

		mBtnStartTime = (Button) findViewById(R.id.btn_start_time);
		mBtnStartTime.setText(appContext.getCookie("start_time"));

		mBtnEndTime = (Button) findViewById(R.id.btn_end_time);
		mBtnEndTime.setText(appContext.getCookie("end_time"));

		int ipos = 0;
		ipos = getSelPos(mSpinnerProp, appContext.getCookie("break_prop"));
		mSpinnerProp.setSelection(ipos);

		mBtnStartTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSysDates();

				DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year,
							int month, int dayOfMonth) {
						String sTmp = TimeZoneUtil.toDateString(year,
								month + 1, dayOfMonth);

						mBtnStartTime.setText(sTmp + " 00:00:00");
					}
				};

				DatePickerDialog dlg = new DatePickerDialog(
						QueryConditionActivity.this, dateListener, iStartYear,
						iStartMonth, iStartDay);

				dlg.show();
			}
		});

		mBtnEndTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getSysDates();

				DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year,
							int month, int dayOfMonth) {
						String sTmp = TimeZoneUtil.toDateString(year,
								month + 1, dayOfMonth);

						mBtnEndTime.setText(sTmp + " 23:59:59");
					}
				};

				DatePickerDialog dlg = new DatePickerDialog(
						QueryConditionActivity.this, dateListener, iStartYear,
						iStartMonth, iStartDay);

				dlg.show();
			}
		});

		//
		// 提交时间条件
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SUBMIT);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				appContext.setCookie("start_time", mBtnStartTime.getText()
						.toString());
				appContext.setCookie("end_time", mBtnEndTime.getText()
						.toString());
				appContext.setCookie("break_prop",
						((CItem) mSpinnerProp.getSelectedItem()).GetID());
				// ProcessSave();
				
				
				setResult(Activity.RESULT_OK, getIntent());
				finish();

			}
		}

		);

		// 抓图
		Button fbBack = (Button) findViewById(R.id.btn_back);
		fbBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			
				finish();

			}
		}

		);

	}

	public int getSelPos(Spinner sp, String sValue) {
		for (int i = 0; i < sp.getCount(); i++) {

			CItem item = (CItem) sp.getItemAtPosition(i);
			if (item.GetID().equals(sValue)) {
				return i;
			}

		}
		return -1;
	}

	public void initProps() {
		mSpinnerProp = (Spinner) findViewById(R.id.btn_prop);
		List<CItem> lst = new ArrayList<CItem>();

		CItem ct0 = new CItem("99", "全部");
		lst.add(ct0);

		CItem ct1 = new CItem("0", "一般违规");
		lst.add(ct1);

		CItem ct2 = new CItem("1", "严重违规");
		lst.add(ct2);

		CItem ct3 = new CItem("2", "重大违规");
		lst.add(ct3);

	
		ArrayAdapter<CItem> Adapter = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);

		mSpinnerProp.setAdapter(Adapter);

	}

	//
	private void getSysDates() {
		Calendar d = Calendar.getInstance(Locale.CHINA);
		// 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
		Date myDate = new Date();
		// 创建一个Date实例
		d.setTime(myDate);
		// 设置日历的时间，把一个新建Date实例myDate传入

		if (iStartYear <= 0) {
			iStartYear = d.get(Calendar.YEAR);
			iStartMonth = d.get(Calendar.MONTH);
			iStartDay = d.get(Calendar.DAY_OF_MONTH);

		}
	}

}
