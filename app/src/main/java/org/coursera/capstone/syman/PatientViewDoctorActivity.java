package org.coursera.capstone.syman;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.coursera.capstone.syman.adapter.PatientDoctorListAdapter;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Doctor;
import org.coursera.capstone.syman.util.CallableTask;
import org.coursera.capstone.syman.util.SymptomServiceMgr;
import org.coursera.capstone.syman.util.TaskCallback;
import org.coursera.capstone.syman.util.UserIdPhotoHolder;
import org.coursera.capstone.syman.util.UserPhotoFileHandler;
import org.coursera.capstone.syman.wrapper.DoctorWrapper;

import retrofit.client.Header;
import retrofit.client.Response;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PatientViewDoctorActivity extends ActionBarActivity {
	private final String TAG = "DEBUG_JAN";
	
	private PatientDoctorListAdapter mDoctorsAdapter = new PatientDoctorListAdapter(this);;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_view_doctor);
		fetchDoctors();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_view_doctor, menu);
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
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public PatientDoctorListAdapter getDoctorsAdapter() {
		return mDoctorsAdapter;
	}


	private void fetchDoctors() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<List<Doctor>>() {

			@Override
			public List<Doctor> call() throws Exception {
				return symptomService.getPatientsDoctors();
			}
		}, new TaskCallback<List<Doctor>>() {

			@Override
			public void success(List<Doctor> result) {
				Log.d(PatientViewDoctorActivity.class.getName(), "Doctors received: " + result.size());
				mDoctorsAdapter.createFromDoctorList(result);
				mDoctorsAdapter.notifyDataSetChanged();
				fetchImagesForDoctors();
			}

			@Override
			public void error(Exception e) {
				Log.e(PatientViewDoctorActivity.class.getName(), "Error fetching doctors data for patient.", e);
				
				Toast.makeText(
						PatientViewDoctorActivity.this,
						"Error fetching doctors for patient, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void fetchImagesForDoctors() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<List<UserIdPhotoHolder>>() {

			@Override
			public List<UserIdPhotoHolder> call() throws Exception {
				// Only read off UI Thread
				List<DoctorWrapper> wrapperList = mDoctorsAdapter.getDoctorWrapperList();
				List<UserIdPhotoHolder> holders = new ArrayList<UserIdPhotoHolder>();
				for (DoctorWrapper wrapper : wrapperList) {
					Uri uri;
					if ((uri = UserPhotoFileHandler.findPhotoForUserId(wrapper.getDoctor().getUserid())) != null) {
						// Found on file
						UserIdPhotoHolder holder = new UserIdPhotoHolder();
						holder.setPhotoUri(uri);
						holder.setUserId(wrapper.getDoctor().getUserid());
						holders.add(holder);
					} else {
						// Have to download photo
						Response response = symptomService.getPhotoByUserId(wrapper.getDoctor().getUserid());
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
								wrapper.getDoctor().getUserid(), 
								contentType,
								response.getBody().in());
						if (fileUri != null) {
							UserIdPhotoHolder holder = new UserIdPhotoHolder();
							holder.setPhotoUri(fileUri);
							holder.setUserId(wrapper.getDoctor().getUserid());
							holders.add(holder);
						}
					}
				}
				wrapperList = null;
				return holders;
			}
		}, new TaskCallback<List<UserIdPhotoHolder>>() {

			@Override
			public void success(List<UserIdPhotoHolder> photoHolders) {
				Log.d(PatientViewDoctorActivity.class.getName(), "Received photos for doctors: " + photoHolders.size());
				List<DoctorWrapper> wrapperList = mDoctorsAdapter.getDoctorWrapperList();
				for (DoctorWrapper wrapper : wrapperList) {
					for (UserIdPhotoHolder photoHolder : photoHolders) {
						if (wrapper.getDoctor().getUserid().equals(photoHolder.getUserId())) {
							wrapper.setPhotoFileUri(photoHolder.getPhotoUri());
							break;
						}
					}
				}
				mDoctorsAdapter.notifyDataSetChanged();
			}

			@Override
			public void error(Exception e) {
				Log.e(PatientViewDoctorActivity.class.getName(), "Error fetching images for doctors.", e);
				
				Toast.makeText(
						PatientViewDoctorActivity.this,
						"Error fetching doctors for patient, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
