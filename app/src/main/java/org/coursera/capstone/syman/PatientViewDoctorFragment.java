package org.coursera.capstone.syman;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class PatientViewDoctorFragment extends ListFragment {

	private Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setListAdapter(((PatientViewDoctorActivity)mActivity).getDoctorsAdapter());
		RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_patient_doctors, container, false);
		return layout;
	}
}
