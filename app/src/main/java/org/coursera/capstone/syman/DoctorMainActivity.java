package org.coursera.capstone.syman;

import java.util.List;
import java.util.concurrent.Callable;

import org.coursera.capstone.syman.adapter.DoctorPatientListAdapter;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Doctor;
import org.coursera.capstone.syman.model.PatientCompact;
import org.coursera.capstone.syman.persistence.DoctorPersistenceHelper;
import org.coursera.capstone.syman.util.CallableTask;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.IntentFactory;
import org.coursera.capstone.syman.util.SymptomServiceMgr;
import org.coursera.capstone.syman.util.TaskCallback;
import org.coursera.capstone.syman.util.UserPhotoFileHandler;

import retrofit.client.Header;
import retrofit.client.Response;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorMainActivity extends ActionBarActivity {
	private final String TAG = "DEBUG_JAN";
	private String mLoggedInUserId;
	private Doctor mDoctor;
	private List<PatientCompact> mPatientCompacts;
	private DoctorPatientListAdapter mPatientAdapter = new DoctorPatientListAdapter(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoggedInUserId = getIntent().getStringExtra(CommonDefs.INTENT_USERID_EXTRA);
		setContentView(R.layout.activity_doctor_main);
		// Set up alarms for polling
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent pendingIntent = IntentFactory.createAlarmPollerServicePendingIntent(this);
		// Just to make sure we don't add a new alarm when one exist
		alarmManager.cancel(pendingIntent);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 900000l,
				900000l, pendingIntent);
//		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//				SystemClock.elapsedRealtime() + 90000l,
//				90000l, pendingIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mLoggedInUserId == null) {
			// Probably coming from wakeup/alarm pending intent.
			// Find userId from stored doctor.
			Doctor doc = DoctorPersistenceHelper.getDoctor(this);
			mLoggedInUserId = doc.getUserid();
		}
		refreshDoctorData();
		refreshDoctorPhoto();
		refreshDoctorsPatients();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.doctor_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_logout) {
			SymptomServiceMgr.logout(this);
			// Stop alarms
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			PendingIntent pendingIntent = IntentFactory.createAlarmPollerServicePendingIntent(this);
			alarmManager.cancel(pendingIntent);
			startActivity(new Intent(this, LoginActivity.class));
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public DoctorPatientListAdapter getPatientAdapter() {
		return mPatientAdapter;
	}
	
	public List<PatientCompact> getPatientList() {
		return mPatientCompacts;
	}
	
	public void onEditClicked(View view) {
		Intent editDoctorIntent = new Intent(this, DoctorEditActivity.class);
		editDoctorIntent.putExtra(CommonDefs.INTENT_DOCTOR_SERIALIZED_EXTRA, mDoctor);
		startActivity(editDoctorIntent);
	}
	
	public void onSearchCheckInsClicked(View view) {
		startActivity(new Intent(this, DoctorSearchCheckInsActivity.class));
	}
	
	private void updateDoctorData(Doctor doctor) {
		this.mDoctor = doctor;
		TextView tv = (TextView) findViewById(R.id.doctor_id_text);
		tv.setText(Long.toString(mDoctor.getId()));
		tv = (TextView) findViewById(R.id.doctor_name_text);
		tv.setText(mDoctor.getFirstName() + " " + mDoctor.getMiddleName() + " " + mDoctor.getLastName());
		tv = (TextView) findViewById(R.id.doctor_phone_text);
		tv.setText(mDoctor.getPhone());
		tv = (TextView) findViewById(R.id.doctor_email_text);
		tv.setText(mDoctor.getEmail());
	}
	
	
	private void refreshDoctorData() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<Doctor>() {

			@Override
			public Doctor call() throws Exception {
				return symptomService.getDoctor();
			}
		}, new TaskCallback<Doctor>() {

			@Override
			public void success(Doctor doctor) {
				DoctorPersistenceHelper.storeOrUpdateDoctor(DoctorMainActivity.this, doctor);
				updateDoctorData(doctor);
			}

			@Override
			public void error(Exception e) {
				Log.e(DoctorMainActivity.class.getName(), "Error fetching doctor data.", e);
				
				Toast.makeText(
						DoctorMainActivity.this,
						"Error fetching doctors data, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	private void refreshDoctorPhoto() {
		Uri photoUri = UserPhotoFileHandler.findPhotoForUserId(mLoggedInUserId);
		if (photoUri != null) {
			ImageView imageView = (ImageView) findViewById(R.id.doctor_profile_photo);
			imageView.setImageResource(R.drawable.user_icon);
			imageView.setImageURI(photoUri);
			return;
		} else {
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
					ImageView imageView = (ImageView) findViewById(R.id.doctor_profile_photo);
					imageView.setImageResource(R.drawable.user_icon);
					imageView.setImageURI(photoUri);
				}

				@Override
				public void error(Exception e) {
					Log.e(DoctorMainActivity.class.getName(), "Error fetching doctor photo.", e);
					
					Toast.makeText(
							DoctorMainActivity.this,
							"Error fetching photo for doctor,\n check your Internet connection.",
							Toast.LENGTH_LONG).show();
				}
			});
		}
	}
	
	
	private void refreshDoctorsPatients() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<List<PatientCompact>>() {

			@Override
			public List<PatientCompact> call() throws Exception {
				return symptomService.getDoctorsPatientsCompact();
			}
		}, new TaskCallback<List<PatientCompact>>() {

			@Override
			public void success(List<PatientCompact> compactPatients) {
				mPatientCompacts = compactPatients;
				DoctorListPatientsFragment listFragment = (DoctorListPatientsFragment)
						DoctorMainActivity.this.getSupportFragmentManager().findFragmentById(R.id.fragment_doctor_list_patients);
				if (listFragment != null) {
					listFragment.notifyPatientListRefreshed();
				}
//				mPatientAdapter.swapList(mPatientCompacts);
			}

			@Override
			public void error(Exception e) {
				Log.e(DoctorMainActivity.class.getName(), "Error fetching doctors patients data.", e);
				
				Toast.makeText(
						DoctorMainActivity.this,
						"Error fetching doctors data, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
}
