package org.coursera.capstone.syman.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.model.Prescription;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PrescriptionListAdapter extends BaseAdapter {
	private final String TAG = "DEBUG_JAN";
	private List<Prescription> mPrescriptions = new ArrayList<Prescription>();
	private Activity mActivity;
	
	public PrescriptionListAdapter(Activity activity) {
		mActivity = activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPrescriptions.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPrescriptions.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void swapList(List<Prescription> prescriptions) {
		mPrescriptions = prescriptions;
		this.notifyDataSetChanged();
	}
	
	public void addPrescription(Prescription prescription) {
		Log.i(TAG, "addPrescription: " + prescription.getMedicine().getName());
		mPrescriptions.add(prescription);
		notifyDataSetChanged();
	}
	
	public void removeFromListByMedicationName(String medicationName) {
		Iterator<Prescription> iter = mPrescriptions.iterator();
		while (iter.hasNext()) {
			Prescription item = iter.next();
			if (item.getMedicine().getName().equals(medicationName)) {
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
			view = inflater.inflate(R.layout.list_item_prescription_list, null);
		} else {
			view = convertView;
		}
		Prescription prescription = mPrescriptions.get(position);
		TextView tv = (TextView) view.findViewById(R.id.prescription_text);
		tv.setText(prescription.getMedicine().getName());
		tv = (TextView) view.findViewById(R.id.prescription_usage_text);
		tv.setText(prescription.getUsage());
		return view;
	}

}
