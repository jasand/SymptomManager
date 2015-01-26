package org.coursera.capstone.syman.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.coursera.capstone.syman.R;
import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.PrescriptionCheckin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheckinListAdapter extends BaseAdapter {
	private Activity mActivity;
	private List<Checkin> mCheckins = new ArrayList<Checkin>();
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public CheckinListAdapter(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public int getCount() {
		return mCheckins.size();
	}

	@Override
	public Object getItem(int position) {
		return mCheckins.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void swapList(List<Checkin> checkins) {
		this.mCheckins = checkins;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
		View view;
		LayoutInflater inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView != null) {
			view = convertView;
			Log.d("DEBUG_JAN", "  ==> Reuse view");
		} else {
			view = inflater.inflate(R.layout.list_item_view_check_in, null);
			Log.d("DEBUG_JAN", "  ==> New view");
		}
		Checkin checkin = mCheckins.get(position);
		TextView tv = (TextView) view.findViewById(R.id.list_item_check_in_time_text);
		tv.setText(mSimpleDateFormat.format(checkin.getTimestamp()));
		tv = (TextView) view.findViewById(R.id.list_item_check_in_pain_text);
		tv.setText(mapPainStatusToDisplayable(checkin.getPainStatus()));
		tv.setTextColor(mapPainStatusTextColor(checkin.getPainStatus()));
		tv = (TextView) view.findViewById(R.id.list_item_check_in_food_text);
		tv.setText(mapFoodStatusToDisplayable(checkin.getFoodStatus()));
		tv.setTextColor(mapFoodStatusTextColor(checkin.getFoodStatus()));
		if (checkin.getPrescriptionCheckins() != null) {
			LinearLayout ll = (LinearLayout) view.findViewById(R.id.prescription_checkins_linear_layout);
			ll.removeAllViews();
			for (PrescriptionCheckin pc : checkin.getPrescriptionCheckins()) {
				RelativeLayout subView = (RelativeLayout) inflater.inflate(R.layout.sub_list_item_prescription, null);
				tv = (TextView) subView.findViewById(R.id.sub_list_prescription_name);
				tv.setText(pc.getMedicine().getName());
				tv = (TextView) subView.findViewById(R.id.sub_list_prescription_time);
				tv.setText(mSimpleDateFormat.format(pc.getTimeTaken()));
				Log.d("DEBUG_JAN", "  ==> Adding: " + pc.getMedicine() + " of " + checkin.getPrescriptionCheckins().size());
				ll.addView(subView);
			}
		}
		return view;
		} catch (RuntimeException e) {
			e.printStackTrace();
			Log.d("DEBUG_JAN", "  ==> Exception: " + e);
			throw e;
		}
	}
	
	
	private String mapPainStatusToDisplayable(String status) {
		if (status.equals("PAIN_CONTROLLED")) {
			return "well-controlled";
		} else if (status.equals("PAIN_MODERATE")) {
			return "moderate";
		} else if (status.equals("PAIN_SEVERE")) {
			return "severe";
		} else {
			return "unknown status";
		}
	}
	
	private String mapFoodStatusToDisplayable(String status) {
		if (status.equals("PROBLEM_EATING_NO")) {
			return "no";
		} else if (status.equals("PROBLEM_EATING_SOME")) {
			return "some";
		} else if (status.equals("PROBLEM_EATING_YES")) {
			return "can't eat";
		} else {
			return "unknown status";
		}
	}
	
	private int mapPainStatusTextColor(String status) {
		if (status.equals("PAIN_CONTROLLED")) {
			return Color.GREEN;
		} else if (status.equals("PAIN_MODERATE")) {
			return Color.YELLOW;
		} else if (status.equals("PAIN_SEVERE")) {
			return Color.RED;
		} else {
			return Color.BLACK;
		}
	}

	private int mapFoodStatusTextColor(String status) {
		if (status.equals("PROBLEM_EATING_NO")) {
			return Color.GREEN;
		} else if (status.equals("PROBLEM_EATING_SOME")) {
			return Color.YELLOW;
		} else if (status.equals("PROBLEM_EATING_YES")) {
			return Color.RED;
		} else {
			return Color.BLACK;
		}
	}
}
