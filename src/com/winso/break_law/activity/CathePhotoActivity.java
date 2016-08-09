package com.winso.break_law.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.winso.break_law.R;
import com.winso.break_law.app.AppContext;
import com.winso.break_law.app.UIHelper;

import com.winso.comm_library.*;
import com.winso.comm_library.icedb.*;
import com.winso.comm_library.widget.*;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

/**
 * 应用程序Activity的基类
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-9-01
 */
public class CathePhotoActivity extends BaseActivity implements
		ImageChooserListener {

	private Spinner mSpinnerProp;
	private Button mBtnOption;

	private Button mBtnAction;
	private Button mBtnUserDefine;
	private Button mBtnDanger;

	private RadioButton fbCathePhoto;
	private RadioButton fbImport;
	private Button fbSave;
	private Button fbReturn;
	private ImageView fViewPhoto;

	// private ImageView imageViewThumbnail;
	// private ImageView imageViewThumbSmall;
	// private VideoChooserManager videoChooserManager;
	// private MediaChooserManager chooserManager;
	private ImageChooserManager imageChooserManager;
	// private VideoView videoView;
	private int chooserType;
	private String filePath = new String("");

	
	private EditText mEDContent;
	private boolean bProcessUpfile = false;

	AppContext appContext;

	boolean bIsImport = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bProcessUpfile = false;

		myCallBack = new MyCallBack(this);
		final View view = View.inflate(this, R.layout.activity_cathe_photo,
				null);
		setContentView(view);

		TextView vTitle = (TextView) findViewById(R.id.tx_header_title);
		vTitle.setText("违规抓拍");

		appContext = (AppContext) getApplication();
		fViewPhoto = (ImageView) findViewById(R.id.photo_cathe_save_img);

		mEDContent = (EditText) findViewById(R.id.edit_fpr_opinion);

		// 危险库
		mBtnDanger = (Button) findViewById(R.id.photo_btn_danger);
		mBtnDanger.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// DangerLibraryActivity
				Intent intent = new Intent(v.getContext(),
						DangerLibraryActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_DANGER_VIEW);

			}
		}

		);

		// 行为库
		mBtnAction = (Button) findViewById(R.id.photo_btn_action);
		mBtnAction.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// DangerLibraryActivity
				Intent intent = new Intent(v.getContext(),
						ActionlibraryActivity.class);
				startActivityForResult(intent, UIHelper.INTENT_ACTION_VIEW);
			}
		}

		);

		// 行为库
		mBtnUserDefine = (Button) findViewById(R.id.photo_btn_user_define);
		mBtnUserDefine.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		}

		);

		// 抓图
		fbCathePhoto = (RadioButton) findViewById(R.id.photo_picture);
		fbCathePhoto.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				takePicture();

			}
		}

		);

		//
		// 抓图
		fbSave = (Button) findViewById(R.id.btn_save);
		fbSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				//
				if (filePath.length() <= 0) {
					UIHelper.showMsg(v.getContext(), "", "请先拍照或者选择");
					return;
				}
				if (mEDContent.getText().toString().length() <= 0) {
					UIHelper.showMsg(v.getContext(), "", "请输入违规内容");
					return;
				}

				if ( appContext.getCookie("org_id").length() <= 0 )
				{
					UIHelper.showMsg(v.getContext(), "", "请先在设置界面选择当前的项目");
					return ;
				}
				
				
				upload();
				// ProcessSave();
				// finish();

			}
		}

		);

		// 抓图
		fbCathePhoto = (RadioButton) findViewById(R.id.photo_picture);
		fbCathePhoto.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				bIsImport = false;
				takePicture();

			}
		}

		);

		//

		fViewPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//
				if (filePath.length() <= 0)
					return;
				UIHelper.openViewPic(v.getContext(), filePath);

			}
		}

		);

		

		// 返回
		fbReturn = (Button) findViewById(R.id.btn_back);
		fbReturn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		}

		);
		

		// 导入
		fbImport = (RadioButton) findViewById(R.id.photo_import);
		fbImport.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				bIsImport = true;

				// String sDestFile = appContext.getPictureDirectory();
				// UIHelper.showMsg(v.getContext(), "", sDestFile);

				pickMedia();
			}
		}

		);

		initProps();

		// reinitializeImageChooser();
		// reinitializeVideoChooser();

		// videoView.setVisibility(View.INVISIBLE);
		fViewPhoto.setVisibility(View.VISIBLE);
		
		
		
		//查找是否存在项
		if ( appContext.getCookie("org_id").length() <= 0 )
		{
			UIHelper.showMsg(this, "", "请先在设置界面选择当前的项目");
		}
	}

	public void pickMedia() {
		// chooserManager = new MediaChooserManager(this,
		// ChooserType.REQUEST_PICK_PICTURE_OR_VIDEO);
		// chooserManager.setMediaChooserListener(this);
		//
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_PICK_PICTURE, AppContext.STORE_PIC_PATH,
				false);
		imageChooserManager.setImageChooserListener(this);
		try {
			filePath = imageChooserManager.choose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initProps() {
		mSpinnerProp = (Spinner) findViewById(R.id.photo_spin_prop);
		List<CItem> lst = new ArrayList<CItem>();

		CItem ct1 = new CItem("0", "一般违规");
		lst.add(ct1);

		CItem ct2 = new CItem("1", "严重违规");
		lst.add(ct2);

		CItem ct3 = new CItem("2", "重大违规");
		lst.add(ct3);

	
		ArrayAdapter<CItem> Adapter = new ArrayAdapter<CItem>(this,
				android.R.layout.simple_spinner_item, lst);

		mSpinnerProp.setAdapter(Adapter);

		mBtnOption = (Button) findViewById(R.id.photo_btn_user_define);
		mBtnOption.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DorisSideBar.loadDorisSideBar(DorisSideBar.getDefalutItems(),
						CathePhotoActivity.this,
						new DorisSideBar.SideItemClickEvent() {

							@Override
							public void navigateTo(String tagName) {
								// TODO Auto-generated method stub

							}
						});
			}
		}

		);

	}

	// Should be called if for some reason the ImageChooserManager is null (Due
	// to destroying of activity for low memory situations)
	private void reinitializeImageChooser() {
		imageChooserManager = new ImageChooserManager(this, chooserType,
				AppContext.STORE_PIC_PATH, true);
		imageChooserManager.setImageChooserListener(this);
		imageChooserManager.reinitialize(filePath);
	}

	// Should be called if for some reason the VideoChooserManager is null (Due
	// to destroying of activity for low memory situations)
	// private void reinitializeVideoChooser() {
	// videoChooserManager = new VideoChooserManager(this, chooserType,
	// AppContext.STORE_PIC_PATH, true);
	// videoChooserManager.setVideoChooserListener(this);
	// videoChooserManager.reinitialize(filePath);
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK)
			return;

		// if (requestCode == ChooserType.REQUEST_PICK_PICTURE_OR_VIDEO) {
		// chooserManager.submit(requestCode, data);
		// return;
		// }

		// 选择Danger
		if (requestCode == UIHelper.INTENT_DANGER_VIEW) {
			super.onActivityResult(requestCode, resultCode, data);
			String sConent = data.getStringExtra("hazard_content");
			mEDContent.setText(sConent);
			return;
		}
		// 选择Action
		if (requestCode == UIHelper.INTENT_ACTION_VIEW) {
			super.onActivityResult(requestCode, resultCode, data);
			String sConent = data.getStringExtra("content");
			mEDContent.setText(sConent);
			return;
		}
		// 选择图片
		if (requestCode == ChooserType.REQUEST_PICK_PICTURE
				|| requestCode == ChooserType.REQUEST_CAPTURE_PICTURE) {
			if (imageChooserManager == null) {
				reinitializeImageChooser();
			}
			imageChooserManager.submit(requestCode, data);
			return;
		}

	}


	private void takePicture() {
		chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_CAPTURE_PICTURE, AppContext.STORE_PIC_PATH,
				true);
		imageChooserManager.setImageChooserListener(this);
		try {
			// pbar.setVisibility(View.VISIBLE);
			filePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onImageChosen(final ChosenImage image) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// pbar.setVisibility(View.GONE);
				if (image != null) {
					
					filePath = image.getFileThumbnail().toString();
					// textViewFile.setText(image.getFilePathOriginal());
					// imageViewThumbnail.setImageURI(Uri.parse(new File(image
					// .getFileThumbnail()).toString()));
					// imageViewThumbSmall.setImageURI(Uri.parse(new File(image
					// .getFileThumbnailSmall()).toString()));

					// fViewPhoto.setImageURI(Uri.parse(appContext
					// .getPictureDirectory()
					// + FileUtils.getFileName(filePath)));

					fViewPhoto.setImageURI(
							Uri.parse(new File(filePath).toString())
							);

				}
			}
		});
	}

	// @Override
	// public void onImageChosen(final ChosenImage image) {
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// filePath = image.getFilePathOriginal().toString();
	// // pbar.setVisibility(View.GONE);
	// if (image != null) {
	//
	// fViewPhoto.setVisibility(View.VISIBLE);
	//
	// fViewPhoto.setImageURI(Uri.parse(appContext
	// .getPictureDirectory()
	// + FileUtils.getFileName(filePath)));
	// // fViewPhoto.setImageURI(Uri.parse(new File(image
	// // .getFileThumbnailSmall()).toString()));
	// }
	// }
	// });
	// }

	@Override
	public void onError(final String reason) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// pbar.setVisibility(View.GONE);
				Toast.makeText(CathePhotoActivity.this, reason,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	/**
	 * 上传文件操作。
	 */
	private void upload() {

		if (bProcessUpfile)
			return;

		bProcessUpfile = true;
		//public UploadFileTask(ICEDBUtil db,Activity context, String sSrcFile,String sRemote, CallbackInterface bc) 
		new UploadFileTask(appContext.m_ice,this,filePath,"",myCallBack).execute();
		
		// new UploadTask(this, uploadFile).execute(); // 使用异步任务上传文件。

	}

	private boolean ProcessSave(String sPic) {
		// getDBID
		//查找是否存在项
		//确定是否存在项目
		if ( appContext.getCookie("org_id").length() <= 0 )
		{
			UIHelper.showMsg(this, "", "请先在系统设计中选择项目");
			return false;
		}
		

		if (!appContext.putBreakLawInfo(mEDContent.getText().toString(), sPic,
				appContext.getTime(),
				((CItem) mSpinnerProp.getSelectedItem()).GetID())) {
			UIHelper.showMsg(this, "", "保存失败");
			return false;
		}

		return true;
		// 传输结果
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("chooser_type", chooserType);
		outState.putString("media_path", filePath);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("chooser_type")) {
				chooserType = savedInstanceState.getInt("chooser_type");
			}

			if (savedInstanceState.containsKey("media_path")) {
				filePath = savedInstanceState.getString("media_path");
			}
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	private MyCallBack myCallBack;

	public class MyCallBack implements CallbackInterface {
		CathePhotoActivity mAc;

		public void func(String responseText) {

			bProcessUpfile = false;

			if (responseText == null)
				return;

			if (responseText.length() <= 1) {
				UIHelper.showMsg(CathePhotoActivity.this, "", "上传文件异常");
				return;
			}

			// Android 必须在 UI 线程中才能修改 UI。

			mAc.bProcessUpfile = false;

			if (responseText.length() <= 14)
				return;

			//String[] vs = responseText.split("\\/");

			//String sRealFileName = "";
			//if (vs.length > 0)
			//	sRealFileName = vs[vs.length - 1];

			if (!ProcessSave(responseText)) {
				EasyLog.error("save error");
				return;
			}

			// 修改本地文名
			filePath = appContext.getPictureDirectory() + "/";
			filePath += responseText;

			//FileUtils.reNamePath(filePath, sNewName);

			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			finish();

		}

		public void cancel(String responseText) {

			bProcessUpfile = false;
			
		}

		MyCallBack(CathePhotoActivity ac) {
			mAc = ac;
		}
	}

	
	
	
	//
}
