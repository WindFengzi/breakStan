package com.winso.break_law.activity;

import com.winso.break_law.R;
import com.winso.comm_library.widget.TouchImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewPicActivity extends BaseActivity {

	private TouchImageView tViewImage;
	private String sPic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pic);

		Intent it = getIntent();

		sPic = it.getStringExtra("pic_path");

		tViewImage = (TouchImageView) findViewById(R.id.t_view_pic);

		Bitmap bmpDefaultPic = BitmapFactory.decodeFile(sPic, null);

		tViewImage.setImageBitmap(bmpDefaultPic);

		// 设置返回按扭
		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
