package org.coursera.capstone.syman;

import java.util.List;

import org.coursera.capstone.syman.adapter.CheckinListAdapter;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.Prescription;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.SymptomServiceMgr;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class DoctorSearchCheckInsActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private String mSearchText;
	private CheckinListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListAdapter = new CheckinListAdapter(this);
		setContentView(R.layout.activity_doctor_search_check_ins);
		ListView lv = (ListView) findViewById(R.id.search_check_in_list_view);
		lv.setAdapter(mListAdapter);
		mSearchText = getIntent().getStringExtra(CommonDefs.INTENT_PATIENT_FULL_NAME_EXTRA);
		if (mSearchText != null && !mSearchText.equals("")) {
			EditText et = (EditText) findViewById(R.id.search_name_edit_text);
			et.setText(mSearchText);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doctor_search_check_ins, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void searchCheckIns(View view) {
		EditText et = (EditText) findViewById(R.id.search_name_edit_text);
		mSearchText = et.getText().toString();
		postSearchToServer();
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	
	private void postSearchToServer() {
		new PostSearchAsyncTask().execute(mSearchText);
	}
	
	
	private class PostSearchAsyncTask extends AsyncTask<String, Void, List<Checkin>> {
		@Override
		protected List<Checkin> doInBackground(String... name) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(DoctorSearchCheckInsActivity.this);
			if (symptomService == null) {
				return null;
			}
			try {
				return symptomService.searchCheckinsByPatientName(name[0]);
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				return null;
			}
		}
		@Override
		protected void onPostExecute(List<Checkin> checkins) {
			if (checkins != null && checkins.size() > 0) {
				Log.d(TAG, "Before swapList");
				mListAdapter.swapList(checkins);
				Log.d(TAG, "After swapList");
			} else if (checkins != null && checkins.size() == 0) {
				Toast.makeText(DoctorSearchCheckInsActivity.this, "No exact match found", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(DoctorSearchCheckInsActivity.this,
						"Error searching Check-Ins.\nPlease check network status.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
