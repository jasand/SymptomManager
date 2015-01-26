package org.coursera.capstone.syman.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.model.Medication;
import org.coursera.capstone.syman.model.Prescription;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MedicationListAdapter extends BaseAdapter {
	private List<Medication> mMedications = new ArrayList<Medication>();
	private Activity mActivity;
	
	public MedicationListAdapter(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMedications.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mMedications.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void swapList(List<Medication> medications) {
		mMedications = medications;
		notifyDataSetChanged();
	}
	
	public void addMedication(Medication medication) {
		mMedications.add(medication);
		notifyDataSetChanged();
	}
	
	public Medication findMedicationByName(String name) {
		for (Medication medication : mMedications) {
			if (medication.getName().equals(name)) {
				return medication;
			}
		}
		return null;
	}
	
	public void removeFromListByName(String medicationName) {
		Iterator<Medication> iter = mMedications.iterator();
		while (iter.hasNext()) {
			Medication med = iter.next();
			if (med.getName().equals(medicationName)) {
				iter.remove();
				notifyDataSetChanged();
				return;
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item_medication_list, null);
		} else {
			view = convertView;
		}
		Medication medication = mMedications.get(position);
		TextView tv = (TextView) view.findViewById(R.id.medication_name_text);
		tv.setText(medication.getName());
		tv = (TextView) view.findViewById(R.id.medication_description_text);
		tv.setText(medication.getDescription());
		return view;
	}

}
