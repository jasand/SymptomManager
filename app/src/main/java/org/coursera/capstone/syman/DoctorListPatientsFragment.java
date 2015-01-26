package org.coursera.capstone.syman;

import java.util.ArrayList;
import java.util.List;

import org.coursera.capstone.syman.adapter.DoctorPatientListAdapter;
import org.coursera.capstone.syman.model.PatientCompact;
import org.coursera.capstone.syman.util.CommonDefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;

public class DoctorListPatientsFragment extends ListFragment {
	
	private Activity mActivity;
	private EditText mFilterEditText;
	private List<PatientCompact> mFilteredPatients = new ArrayList<PatientCompact>();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		setListAdapter(((DoctorMainActivity)mActivity).getPatientAdapter());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_doctors_patients, container, false);
		mFilterEditText = (EditText) view.findViewById(R.id.doctor_filter_patients_edit_text);
		mFilterEditText.setText("");
		
		mFilterEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterPatients();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		mFilterEditText.setText("");
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view,
					int pos, long id) {
				DoctorPatientListAdapter patientsCompacts = (DoctorPatientListAdapter) getListAdapter();
				Intent searchCheckinsIntent = 
						new Intent(DoctorListPatientsFragment.this.mActivity, DoctorSearchCheckInsActivity.class);
				String firstName = ((PatientCompact)patientsCompacts.getItem(pos)).getFirstName();
				String middleName = ((PatientCompact)patientsCompacts.getItem(pos)).getMiddleName();
				String lastName = ((PatientCompact)patientsCompacts.getItem(pos)).getLastName();
				String fullName;
				if (middleName != null && !middleName.equals("")) {
					fullName = firstName + " " + middleName + " " + lastName;
				} else {
					fullName = firstName + " " + lastName;
				}
				searchCheckinsIntent.putExtra(CommonDefs.INTENT_PATIENT_FULL_NAME_EXTRA, fullName);
				startActivity(searchCheckinsIntent);
				return true;
			}
			
		});
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		PatientCompact patient = (PatientCompact) getListAdapter().getItem(position);
		Intent viewPatientIntent = new Intent(mActivity, DoctorViewPatientActivity.class);
		viewPatientIntent.putExtra(CommonDefs.INTENT_PATIENT_COMPACT_SERIALIZED_EXTRA, patient);
		startActivity(viewPatientIntent);
	}
	
	public void notifyPatientListRefreshed() {
		filterPatients();
	}
	
	private void filterPatients() {
		String filterText = mFilterEditText.getText().toString();
		mFilteredPatients.clear();
		if (filterText == null || filterText.equals("")) {
			mFilteredPatients.addAll(((DoctorMainActivity)mActivity).getPatientList());
		} else {
			List<PatientCompact> fullList = ((DoctorMainActivity)mActivity).getPatientList();
			for (PatientCompact patient : fullList) {
				if (patient.getFullName().toUpperCase().contains(filterText.toUpperCase())) {
					mFilteredPatients.add(patient);
				}
			}
		}
		((DoctorMainActivity)mActivity).getPatientAdapter().swapList(mFilteredPatients);
	}
}
