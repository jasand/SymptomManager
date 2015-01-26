package org.coursera.capstone.syman.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.model.PatientCompact;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DoctorPatientListAdapter extends BaseAdapter {
	private Activity mActivity;
	private List<PatientCompact> mPatients = new ArrayList<PatientCompact>();
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public DoctorPatientListAdapter(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return mPatients.size();
	}

	@Override
	public Object getItem(int pos) {
		return mPatients.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View view;
		if (convertView != null) {
			view = convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item_patient_list, null);
		}
		PatientCompact patient = mPatients.get(pos);
		TextView tv = (TextView) view.findViewById(R.id.patient_list_item_name);
		if (patient.getMiddleName() != null && !patient.getMiddleName().equals("")) {
			tv.setText(patient.getFirstName() + " " + patient.getMiddleName() + " " + patient.getLastName());
		} else {
			tv.setText(patient.getFirstName() + " " + patient.getLastName());
		}
		tv = (TextView) view.findViewById(R.id.patient_list_item_date_of_birth);
		tv.setText(mSimpleDateFormat.format(patient.getDateOfBirth()));
		ImageView iv = (ImageView) view.findViewById(R.id.patient_list_item_status);
		if (patient.getCurrentState().equalsIgnoreCase("GREEN")) {
			iv.setImageResource(R.drawable.status_green);
		} else if (patient.getCurrentState().equalsIgnoreCase("YELLOW")) {
			iv.setImageResource(R.drawable.status_yellow);
		} else if (patient.getCurrentState().equalsIgnoreCase("ORANGE")) {
			iv.setImageResource(R.drawable.status_orange);
		} else if (patient.getCurrentState().equalsIgnoreCase("RED")) {
			iv.setImageResource(R.drawable.status_red);
		}
		
		return view;
	}
	
	public void swapList(List<PatientCompact> patients) {
		this.mPatients = patients;
		Collections.sort(mPatients);
		notifyDataSetChanged();
	}
}
