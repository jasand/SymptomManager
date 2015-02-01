package org.coursera.capstone.syman;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Callable;

import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.persistence.PatientPersistenceHelper;
import org.coursera.capstone.syman.util.CallableTask;
import org.coursera.capstone.syman.util.CheckInAlarmHelper;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.IntentFactory;
import org.coursera.capstone.syman.util.SymptomServiceMgr;
import org.coursera.capstone.syman.util.TaskCallback;
import org.coursera.capstone.syman.util.UserPhotoFileHandler;

import retrofit.client.Header;
import retrofit.client.Response;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PatientMainActivity extends ActionBarActivity {
	
	private final String TAG = "DEBUG_JAN";
	private Patient mPatient;
	private String mLoggedInUserId;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private int mFrequencyPref;
	private int mNightSettingPref;
	private boolean mNightPref;
	private boolean mAudioPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoggedInUserId = getIntent().getStringExtra(CommonDefs.INTENT_USERID_EXTRA);
		if (savedInstanceState != null) {
			String userId = savedInstanceState.getString(CommonDefs.INTENT_USERID_EXTRA);
			if (userId != null && !userId.equals("")) {
				//Probably coming from up-navigation, use this userid
				mLoggedInUserId = userId;
			}
		}
		setContentView(R.layout.activity_patient_main);
		getPreferencesOrSetDefault();
		scheduleAlarms();
		Log.d(TAG, "onCreate() - " + mLoggedInUserId);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart() - " + mLoggedInUserId);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume() - " + mLoggedInUserId);
//		mLoggedInUserId = getIntent().getStringExtra(CommonDefs.INTENT_USERID_EXTRA);
		getPreferencesOrSetDefault();
		refreshPatientData();
		refreshPatientPhoto();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause() - " + mLoggedInUserId);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop() - " + mLoggedInUserId);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy() - " + mLoggedInUserId);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CommonDefs.INTENT_USERID_EXTRA, mLoggedInUserId);
		Log.d(TAG, "Saving instance state: " + mLoggedInUserId);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mLoggedInUserId = savedInstanceState.getString(CommonDefs.INTENT_USERID_EXTRA);
		Log.d(TAG, "Restoring instance state: " + mLoggedInUserId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_main, menu);
		Log.i(TAG, "PatientMainActivity.onCreateOptionsMenu()");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			startActivity(new Intent(this,PatientSettingsActivity.class));
			return true;
		} else if (id == R.id.action_logout) {
			SymptomServiceMgr.logout(this);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			PendingIntent pendingIntent = IntentFactory.createReminderServicePendingIntent(this);
			alarmManager.cancel(pendingIntent);
			startActivity(new Intent(this, LoginActivity.class));
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void scheduleAlarms() {
		CheckInAlarmHelper.setNewAlarmSeries(this, mFrequencyPref, mNightSettingPref, mNightPref);
	}
	
	public void navigateEditPatient(View view) {
		Intent editPatientIntent = new Intent(this, PatientEditActivity.class);
		//editPatientIntent.putExtra(CommonDefs.INTENT_PATIENT_SERIALIZED_EXTRA, mPatient);
		startActivity(editPatientIntent);
	}
	
	public void navigateViewDoctors(View view) {
		startActivity(new Intent(this, PatientViewDoctorActivity.class));
	}
	
	public void navigateViewCheckins(View view) {
		startActivity(new Intent(this, PatientViewCheckinsActivity.class));
	}
	
	public void navigateCheckin(View view) {
		Intent checkInIntent = new Intent(this, PatientCheckinActivity.class);
		//checkInIntent.putExtra(CommonDefs.INTENT_PATIENT_SERIALIZED_EXTRA, mPatient);
		startActivity(checkInIntent);
	}
	
	private void getPreferencesOrSetDefault() {
		SharedPreferences prefs = getSharedPreferences(PatientSettingsActivity.PATIENT_PREFERENCES, Activity.MODE_PRIVATE);
		mFrequencyPref = prefs.getInt(PatientSettingsActivity.REMINDER_FREQUENCY_SETTING, -1);
		mNightSettingPref = prefs.getInt(PatientSettingsActivity.NIGHT_TIME_INTERVAL_SETTING, -1);
		mNightPref = prefs.getBoolean(PatientSettingsActivity.NIGHT_ENABLE_SETTING, false);
		mAudioPref = prefs.getBoolean(PatientSettingsActivity.AUDIO_ENABLE_SETTING, false);
		if (mFrequencyPref == -1 || mNightSettingPref == -1) {
			// Not set, set defaults
			mFrequencyPref = 6;
			mNightSettingPref = 0;
			mNightPref = false;
			mAudioPref = true;
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(PatientSettingsActivity.REMINDER_FREQUENCY_SETTING, mFrequencyPref);
			editor.putInt(PatientSettingsActivity.NIGHT_TIME_INTERVAL_SETTING, mNightSettingPref);
			editor.putBoolean(PatientSettingsActivity.NIGHT_ENABLE_SETTING, mNightPref);
			editor.putBoolean(PatientSettingsActivity.AUDIO_ENABLE_SETTING, mAudioPref);
			editor.apply();
			Log.i(TAG, "Satt nye preferences");
		}
	}
	
	private void updatePatientData(Patient patient) {
		this.mPatient = patient;
		TextView tv = (TextView) findViewById(R.id.patient_id_text);
		tv.setText(Long.toString(mPatient.getId()));
		tv = (TextView) findViewById(R.id.patient_first_name_text);
		tv.setText(mPatient.getFirstName());
		tv = (TextView) findViewById(R.id.patient_middle_name_text);
		tv.setText(mPatient.getMiddleName());
		tv = (TextView) findViewById(R.id.patient_last_name_text);
		tv.setText(mPatient.getLastName());
		tv = (TextView) findViewById(R.id.patient_date_of_birth_text);
		tv.setText(mSimpleDateFormat.format(mPatient.getDateOfBirth()));
		
		tv = (TextView) findViewById(R.id.patient_phone_text);
		tv.setText(mPatient.getPhone());
		tv = (TextView) findViewById(R.id.patient_email_text);
		tv.setText(mPatient.getEmail());
		tv = (TextView) findViewById(R.id.patient_address_text);
		tv.setText(mPatient.getAddress());
		tv = (TextView) findViewById(R.id.patient_zip_state_text);
		tv.setText(mPatient.getZip() + "/" + mPatient.getState());
		// Country removed from GUI due to space issues, available in edit screen
//		tv = (TextView) findViewById(R.id.patient_country_text);
//		tv.setText(mPatient.getCountry());
		tv = (TextView) findViewById(R.id.patient_diagnosis_text);
		tv.setText(mPatient.getDiagnosis());
		
		tv = (TextView) findViewById(R.id.patient_reminder_freq_text);
		if (mFrequencyPref == 6) {
			tv.setText("Every 6 hours");
		} else if (mFrequencyPref == 3) {
			tv.setText("Every 3 hours");
		} else if (mFrequencyPref == 2) {
			tv.setText("Every 2 hours");
		}
		
		tv = (TextView) findViewById(R.id.patient_reminder_night_text);
		if (!mNightPref) {
			tv.setText("Not set");
		} else if (mNightSettingPref == 22) {
			tv.setText("22:00-04:00");
		} else if (mNightSettingPref == 23) {
			tv.setText("23:00-05:00");
		} else if (mNightSettingPref == 0) {
			tv.setText("00:00-06:00");
		} else if (mNightSettingPref == 1) {
			tv.setText("01:00-07:00");
		}
		
		tv = (TextView) findViewById(R.id.patient_reminder_audio_text);
		if (mAudioPref) {
			tv.setText("Audio on");
		} else {
			tv.setText("Audio off");
		}
		
		ImageView iv = (ImageView) findViewById(R.id.patient_status_icon);
		if (mPatient.getCurrentState().equalsIgnoreCase("GREEN")) {
			iv.setImageResource(R.drawable.status_green);
		} else if (mPatient.getCurrentState().equalsIgnoreCase("YELLOW")) {
			iv.setImageResource(R.drawable.status_yellow);
		} else if (mPatient.getCurrentState().equalsIgnoreCase("ORANGE")) {
			iv.setImageResource(R.drawable.status_orange);
		} else if (mPatient.getCurrentState().equalsIgnoreCase("RED")) {
			iv.setImageResource(R.drawable.status_red);
		}
	}
	
	private void refreshPatientData() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<Patient>() {

			@Override
			public Patient call() throws Exception {
				return symptomService.getPatient();
			}
		}, new TaskCallback<Patient>() {

			@Override
			public void success(Patient result) {
				PatientPersistenceHelper.storeOrUpdatePatient(PatientMainActivity.this, result);
				updatePatientData(result);
			}

			@Override
			public void error(Exception e) {
				Log.e(PatientMainActivity.class.getName(), "Error fetching patient data.", e);
				
				Toast.makeText(
						PatientMainActivity.this,
						"Error fetching patient data, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void refreshPatientPhoto() {
		if (mLoggedInUserId == null) {
			//
			// TODO: This may possible if woken from alarm.
			//       Implement when alarm handling is implemented.
			Toast.makeText(
					PatientMainActivity.this,
					"InnloggetBruker er null...",
					Toast.LENGTH_LONG).show();
//			return;
		}

        // Always fetch photo for demo
//		Uri photoUri = UserPhotoFileHandler.findPhotoForUserId(mLoggedInUserId);
//		if (photoUri != null) {
//			ImageView imageView = (ImageView) findViewById(R.id.patient_profile_image);
//			imageView.setImageResource(R.drawable.user_icon);
//			imageView.setImageURI(photoUri);
//			return;
//		} else {
			
			final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
			
			CallableTask.invoke(new Callable<Uri>() {

				@Override
				public Uri call() throws Exception {
					Response response = symptomService.getUserPhoto();
					List<Header> headers = response.getHeaders();
					String contentType = "";
					for (Header header : headers) {
						if (header.getName().equals("Content-Type")) {
							contentType = header.getValue();
							Log.d(TAG,"Downloaded image with Content-Type: " + contentType);
							break;
						}
					}
					Uri fileUri = UserPhotoFileHandler.savePhotoForUserId(
							mLoggedInUserId, 
							contentType,
							response.getBody().in());
					if (fileUri == null) {
						throw new Exception("Could not save downloaded file.");
					}
					return fileUri;
				}
			}, new TaskCallback<Uri>() {

				@Override
				public void success(Uri photoUri) {
					ImageView imageView = (ImageView) findViewById(R.id.patient_profile_image);
					imageView.setImageResource(R.drawable.user_icon);
					imageView.setImageURI(photoUri);
				}

				@Override
				public void error(Exception e) {
					Log.e(PatientMainActivity.class.getName(), "Error fetching patient photo.", e);
					
					Toast.makeText(
							PatientMainActivity.this,
							"Error fetching patient photo, check your Internet connection.",
							Toast.LENGTH_LONG).show();
				}
			});
			
//		}
	}
}
