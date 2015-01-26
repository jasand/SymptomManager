package org.coursera.capstone.syman;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Doctor;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.SymptomServiceMgr;
import org.coursera.capstone.syman.util.UserPhotoFileHandler;

import retrofit.mime.TypedFile;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorEditActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private Doctor mDoctor;
	private Uri mTmpImageFileUri;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_edit);
		mDoctor = (Doctor) getIntent().getSerializableExtra(CommonDefs.INTENT_DOCTOR_SERIALIZED_EXTRA);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		populateUi();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doctor_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CommonDefs.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	        	Log.i(TAG, "mTmpImageFileUri: " + mTmpImageFileUri);
	        	// Upload photo file
	        	new UploadPhotoAsyncTask().execute();
	        	ImageView iv = (ImageView) findViewById(R.id.doctor_edit_profile_img);
	        	// The iv.invalidate() didn't refres imageview on following selfies.
	        	// Had to set default and back to captured image to refresh imageview...
	        	iv.setImageResource(R.drawable.user_icon);
	        	iv.setImageURI(mTmpImageFileUri);
	        	//iv.invalidate();
	        	
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture, do nothing
	        } else {
	        	Toast.makeText(this, R.string.common_edit_img_capture_fail_text, Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	public void launchPhotoCapture(View view) {
		mTmpImageFileUri = UserPhotoFileHandler.getTempFileForCameraCapture();
		Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mTmpImageFileUri);
		startActivityForResult(cameraIntent, CommonDefs.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	
	public void setEditable(View view) {
		setEditTextActive();
	}
	
	public void cancelEdit(View view) {
		populateUi();
	}
	
	public void submitDoctorData(View view) {
		setEditTextInactive();
		copyEditTextToDoctor();
		new UploadDoctorDataAsyncTask().execute();
	}
	
	private void populateUi() {
		TextView tv = (TextView) findViewById(R.id.doctor_edit_first_name_text);
		tv.setText(mDoctor.getFirstName());
		tv = (TextView) findViewById(R.id.doctor_edit_mid_name_text);
		tv.setText(mDoctor.getMiddleName());
		tv = (TextView) findViewById(R.id.doctor_edit_last_name_text);
		tv.setText(mDoctor.getLastName());
		
		EditText et = (EditText) findViewById(R.id.doctor_phone_edit_text);
		et.setText(mDoctor.getPhone());
		et = (EditText) findViewById(R.id.doctor_email_edit_text);
		et.setText(mDoctor.getEmail());
		et = (EditText) findViewById(R.id.doctor_address_edit_text);
		et.setText(mDoctor.getAddress());
		et = (EditText) findViewById(R.id.doctor_zip_edit_text);
		et.setText(mDoctor.getZip());
		et = (EditText) findViewById(R.id.doctor_state_edit_text);
		et.setText(mDoctor.getState());
		et = (EditText) findViewById(R.id.doctor_country_edit_text);
		et.setText(mDoctor.getCountry());
		setEditTextInactive();
		// Set user photo if stored on device
		Uri photoUri = UserPhotoFileHandler.findPhotoForUserId(mDoctor.getUserid());
		if (photoUri != null) {
			ImageView iv = (ImageView) findViewById(R.id.doctor_edit_profile_img);
			iv.setImageURI(photoUri);
		}
	}
	
	
	private void setEditTextInactive() {
		EditText et = (EditText) findViewById(R.id.doctor_phone_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.doctor_email_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.doctor_address_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.doctor_zip_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.doctor_state_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.doctor_country_edit_text);
		et.setFocusable(false);
		Button button = (Button) findViewById(R.id.doctor_edit_enable_btn);
		button.setClickable(true);
		button = (Button) findViewById(R.id.doctor_edit_submit_btn);
		button.setClickable(false);
	}

	
	private void setEditTextActive() {
		EditText et = (EditText) findViewById(R.id.doctor_phone_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.doctor_email_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.doctor_address_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.doctor_zip_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.doctor_state_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.doctor_country_edit_text);
		et.setFocusableInTouchMode(true);
		Button button = (Button) findViewById(R.id.doctor_edit_enable_btn);
		button.setClickable(false);
		button = (Button) findViewById(R.id.doctor_edit_submit_btn);
		button.setClickable(true);
	}
	
	private void copyEditTextToDoctor() {
		EditText et = (EditText) findViewById(R.id.doctor_phone_edit_text);
		mDoctor.setPhone(et.getText().toString());
		et = (EditText) findViewById(R.id.doctor_email_edit_text);
		mDoctor.setEmail(et.getText().toString());
		et = (EditText) findViewById(R.id.doctor_address_edit_text);
		mDoctor.setAddress(et.getText().toString());
		et = (EditText) findViewById(R.id.doctor_zip_edit_text);
		mDoctor.setZip(et.getText().toString());
		et = (EditText) findViewById(R.id.doctor_state_edit_text);
		mDoctor.setState(et.getText().toString());
		et = (EditText) findViewById(R.id.doctor_country_edit_text);
		mDoctor.setCountry(et.getText().toString());
	}
	
	
	private class UploadPhotoAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(DoctorEditActivity.this);
			if (symptomService == null) {
				return false;
			}
			try {
				File file = new File(mTmpImageFileUri.getPath());
				TypedFile typedFile = new TypedFile("image/jpeg", file);
				String result = symptomService.setUserPhoto(typedFile);
				if (result.equals("OK")) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				return false;
			}
		}

		protected void onPostExecute(Boolean success) {
			if (success) {
				Toast.makeText(DoctorEditActivity.this, R.string.common_edit_img_upload_success_text, Toast.LENGTH_SHORT).show();
				// Copy tmp file to user photo file only when saved to server.
				// Otherwise patient will have local copy and never capture new, while doctor see default picture
				// Important to upload picture to personalize...
				try {
					Uri userPhotoUri = UserPhotoFileHandler.savePhotoForUserId(
							mDoctor.getUserid(),
							"image/jpeg",
							new FileInputStream(new File(mTmpImageFileUri.getPath())));
					ImageView iv = (ImageView) findViewById(R.id.doctor_edit_profile_img);
					// Point imageview to final file when uploaded, set default image first to force reload
					iv.setImageResource(R.drawable.user_icon);
					iv.setImageURI(userPhotoUri);
				} catch (Exception e) {
					Toast.makeText(DoctorEditActivity.this, R.string.common_edit_img_copy_fail_text, Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(DoctorEditActivity.this, R.string.common_edit_img_upload_fail_text, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	
	private class UploadDoctorDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(DoctorEditActivity.this);
			if (symptomService == null) {
				return false;
			}
			try {
				symptomService.updateDoctor(mDoctor);
				return true;
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				return false;
			}
		}

		protected void onPostExecute(Boolean success) {
			if (success) {
				Toast.makeText(DoctorEditActivity.this, R.string.common_edit_upload_success_text, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(DoctorEditActivity.this, R.string.common_edit_upload_fail_text, Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
