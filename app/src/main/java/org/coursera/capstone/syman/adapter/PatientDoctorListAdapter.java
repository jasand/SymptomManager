package org.coursera.capstone.syman.adapter;

import java.util.ArrayList;
import java.util.List;

import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.model.Doctor;
import org.coursera.capstone.syman.wrapper.DoctorWrapper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PatientDoctorListAdapter extends BaseAdapter {

	private Activity mActivity;
	private List<DoctorWrapper> mDoctors;
	
	public PatientDoctorListAdapter(Activity activity) {
		this.mActivity = activity;
		mDoctors = new ArrayList<DoctorWrapper>();
	}

	@Override
	public int getCount() {
		return mDoctors.size();
	}

	@Override
	public Object getItem(int pos) {
		return mDoctors.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	public List<DoctorWrapper> getDoctorWrapperList() {
		// TODO: Probably not a good solution, refactor later.
		return mDoctors;
	}
	
	public void createFromDoctorList(List<Doctor> doctors) {
		List<DoctorWrapper> doctorWrappers = new ArrayList<DoctorWrapper>();
		for (Doctor doctor : doctors) {
			DoctorWrapper wrapper = new DoctorWrapper();
			wrapper.setDoctor(doctor);
			doctorWrappers.add(wrapper);
		}
		this.mDoctors = doctorWrappers;
		Log.d(PatientDoctorListAdapter.class.getName(), "Created list with size: " + doctorWrappers.size());
		this.notifyDataSetChanged();
	}
	
	public void swapList(List<DoctorWrapper> doctorWrappers) {
		this.mDoctors = doctorWrappers;
		Log.d(PatientDoctorListAdapter.class.getName(), "Swapped list with size: " + doctorWrappers.size());
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
//			LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			view = inflater.inflate(R.layout.list_item_patients_doctor, null);
		}
		Log.d(PatientDoctorListAdapter.class.getName(), "getView() called...");
		LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.list_item_patients_doctor, null);
		Doctor doctor = mDoctors.get(pos).getDoctor();
		TextView tv = (TextView) view.findViewById(R.id.doctor_first_name_text);
		tv.setText(doctor.getFirstName() + " " + doctor.getMiddleName());
		tv = (TextView) view.findViewById(R.id.doctor_last_name_text);
		tv.setText(doctor.getLastName());
		tv = (TextView) view.findViewById(R.id.doctor_list_phone_text);
		tv.setText(doctor.getPhone());
		tv = (TextView) view.findViewById(R.id.doctor_list_email_text);
		tv.setText(doctor.getEmail());
		tv = (TextView) view.findViewById(R.id.doctor_list_hospital_text);
		tv.setText(doctor.getHospital());
		tv = (TextView) view.findViewById(R.id.doctor_list_speciality_text);
		tv.setText(doctor.getSpeciality());
		if (mDoctors.get(pos).getPhotoFileUri() != null) {
			ImageView photo = (ImageView) view.findViewById(R.id.patient_doctor_profile_image);
			photo.setImageURI(mDoctors.get(pos).getPhotoFileUri());
		}
		return view;
	}
	
	
}
