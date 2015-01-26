package org.coursera.capstone.syman;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.persistence.PatientPersistenceHelper;
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

public class PatientEditActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private Patient mPatient;
	private Uri mTmpImageFileUri;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_edit);
		mPatient = PatientPersistenceHelper.getPatient(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		populateUi();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_edit, menu);
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
	        	ImageView iv = (ImageView) findViewById(R.id.patient_edit_profile_img);
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
	
	public void submitPatientData(View view) {
		setEditTextInactive();
		copyEditTextToPatient();
		new UploadPatientDataAsyncTask().execute();
	}
	
	private void populateUi() {
		TextView tv = (TextView) findViewById(R.id.patient_edit_first_name_text);
		tv.setText(mPatient.getFirstName());
		tv = (TextView) findViewById(R.id.patient_edit_mid_name_text);
		tv.setText(mPatient.getMiddleName());
		tv = (TextView) findViewById(R.id.patient_edit_last_name_text);
		tv.setText(mPatient.getLastName());
		tv = (TextView) findViewById(R.id.patient_edit_date_of_birth_text);
		tv.setText(mSimpleDateFormat.format(mPatient.getDateOfBirth()));
		
		EditText et = (EditText) findViewById(R.id.patient_phone_edit_text);
		et.setText(mPatient.getPhone());
		et = (EditText) findViewById(R.id.patient_email_edit_text);
		et.setText(mPatient.getEmail());
		et = (EditText) findViewById(R.id.patient_address_edit_text);
		et.setText(mPatient.getAddress());
		et = (EditText) findViewById(R.id.patient_zip_edit_text);
		et.setText(mPatient.getZip());
		et = (EditText) findViewById(R.id.patient_state_edit_text);
		et.setText(mPatient.getState());
		et = (EditText) findViewById(R.id.patient_country_edit_text);
		et.setText(mPatient.getCountry());
		setEditTextInactive();
		// Set user photo if stored on device
		Uri photoUri = UserPhotoFileHandler.findPhotoForUserId(mPatient.getUserid());
		if (photoUri != null) {
			ImageView iv = (ImageView) findViewById(R.id.patient_edit_profile_img);
			iv.setImageURI(photoUri);
		}
	}
	
	private void setEditTextInactive() {
		EditText et = (EditText) findViewById(R.id.patient_phone_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.patient_email_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.patient_address_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.patient_zip_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.patient_state_edit_text);
		et.setFocusable(false);
		et = (EditText) findViewById(R.id.patient_country_edit_text);
		et.setFocusable(false);
		Button button = (Button) findViewById(R.id.patient_edit_enable_btn);
		button.setClickable(true);
		button = (Button) findViewById(R.id.patient_edit_submit_btn);
		button.setClickable(false);
	}

	
	private void setEditTextActive() {
		EditText et = (EditText) findViewById(R.id.patient_phone_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.patient_email_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.patient_address_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.patient_zip_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.patient_state_edit_text);
		et.setFocusableInTouchMode(true);
		et = (EditText) findViewById(R.id.patient_country_edit_text);
		et.setFocusableInTouchMode(true);
		Button button = (Button) findViewById(R.id.patient_edit_enable_btn);
		button.setClickable(false);
		button = (Button) findViewById(R.id.patient_edit_submit_btn);
		button.setClickable(true);
	}
	
	private void copyEditTextToPatient() {
		EditText et = (EditText) findViewById(R.id.patient_phone_edit_text);
		mPatient.setPhone(et.getText().toString());
		et = (EditText) findViewById(R.id.patient_email_edit_text);
		mPatient.setEmail(et.getText().toString());
		et = (EditText) findViewById(R.id.patient_address_edit_text);
		mPatient.setAddress(et.getText().toString());
		et = (EditText) findViewById(R.id.patient_zip_edit_text);
		mPatient.setZip(et.getText().toString());
		et = (EditText) findViewById(R.id.patient_state_edit_text);
		mPatient.setState(et.getText().toString());
		et = (EditText) findViewById(R.id.patient_country_edit_text);
		mPatient.setCountry(et.getText().toString());
	}

	
	private class UploadPhotoAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(PatientEditActivity.this);
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
				Toast.makeText(PatientEditActivity.this, R.string.common_edit_img_upload_success_text, Toast.LENGTH_SHORT).show();
				// Copy tmp file to user photo file only when saved to server.
				// Otherwise patient will have local copy and never capture new, while doctor see default picture
				// Important to upload picture to personalize...
				try {
					Uri userPhotoUri = UserPhotoFileHandler.savePhotoForUserId(
							mPatient.getUserid(),
							"image/jpeg",
							new FileInputStream(new File(mTmpImageFileUri.getPath())));
					ImageView iv = (ImageView) findViewById(R.id.patient_edit_profile_img);
					// Point imageview to final file when uploaded, set default image first to force reload
					iv.setImageResource(R.drawable.user_icon);
					iv.setImageURI(userPhotoUri);
				} catch (Exception e) {
					Toast.makeText(PatientEditActivity.this, R.string.common_edit_img_copy_fail_text, Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(PatientEditActivity.this, R.string.common_edit_img_upload_fail_text, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private class UploadPatientDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(PatientEditActivity.this);
			if (symptomService == null) {
				return false;
			}
			try {
				symptomService.updatePatient(mPatient);
				return true;
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				return false;
			}
		}

		protected void onPostExecute(Boolean success) {
			if (success) {
				Toast.makeText(PatientEditActivity.this, R.string.common_edit_upload_success_text, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PatientEditActivity.this, R.string.common_edit_upload_fail_text, Toast.LENGTH_LONG).show();
			}
		}
	}
}
