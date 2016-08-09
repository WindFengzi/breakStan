package com.winso.break_law.activity;

import java.util.Calendar;

import com.winso.break_law.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class DateTimeDialogActivity extends Activity {
	
	private final int DATE_DIALOG = 1;
	
	private final int TIME_DIALOG = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_dialog_layout);
        
        View.OnClickListener dateBtnListener = 
        	new BtnOnClickListener(DATE_DIALOG);
      
        Button btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setOnClickListener(dateBtnListener);
        
        View.OnClickListener timeBtnListener = 
        	new BtnOnClickListener(TIME_DIALOG);
        Button btnTime = (Button) findViewById(R.id.btnTime);
        btnTime.setOnClickListener(timeBtnListener);
    }
    
    
    protected Dialog onCreateDialog(int id) {
    	//用来获取日期和时间的
    	Calendar calendar = Calendar.getInstance();	
    	
    	Dialog dialog = null;
    	switch(id) {
	    	case DATE_DIALOG:
	    		DatePickerDialog.OnDateSetListener dateListener = 
	    			new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker datePicker, 
								int year, int month, int dayOfMonth) {
							EditText editText = 
								(EditText) findViewById(R.id.editText);
							 //Calendar月份是从0开始,所以month要加1
							editText.setText("你选择了" + year + "年" + 
									(month+1) + "月" + dayOfMonth + "日");
						}
					};
	    		dialog = new DatePickerDialog(this,
	    				dateListener,
	    				calendar.get(Calendar.YEAR),
	    				calendar.get(Calendar.MONTH),
	    				calendar.get(Calendar.DAY_OF_MONTH));
	    		break;
	    	case TIME_DIALOG:
	    		TimePickerDialog.OnTimeSetListener timeListener = 
	    			new TimePickerDialog.OnTimeSetListener() {
						
						@Override
						public void onTimeSet(TimePicker timerPicker,
								int hourOfDay, int minute) {
							EditText editText = 
								(EditText) findViewById(R.id.editText);
							editText.setText("你选择了" + hourOfDay + "时" + 
									 minute + "分");
						}
					};
					dialog = new TimePickerDialog(this, timeListener,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							false);   //是否为二十四制
	    		break;
	    	default:
	    		break;
    	}
    	return dialog;
    }
    /*
     * 成员内部类,此处为提高可重用性，也可以换成匿名内部类
     */
    private class BtnOnClickListener implements View.OnClickListener {
    	
    	private int dialogId = 0;	//默认为0则不显示对话框

    	public BtnOnClickListener(int dialogId) {
    		this.dialogId = dialogId;
    	}
		@Override
		public void onClick(View view) {
			showDialog(dialogId);
		}
    	
    }
}