package org.coursera.capstone.syman;

import java.util.List;

import org.coursera.capstone.syman.adapter.CheckinListAdapter;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.util.SymptomServiceMgr;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class PatientViewCheckinsActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private CheckinListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_view_checkins);
		mListAdapter = new CheckinListAdapter(this);
		ListView lv = (ListView) findViewById(R.id.patient_check_in_list_view);
		lv.setAdapter(mListAdapter);
		new GetCheckInsAsyncTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_view_checkins, menu);
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
	
	private class GetCheckInsAsyncTask extends AsyncTask<Void, Void, List<Checkin>> {
		@Override
		protected List<Checkin> doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(PatientViewCheckinsActivity.this);
			if (symptomService == null) {
				return null;
			}
			try {
				return symptomService.getPatientsCheckins();
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				return null;
			}
		}
		@Override
		protected void onPostExecute(List<Checkin> checkins) {
			if (checkins != null && checkins.size() > 0) {
				mListAdapter.swapList(checkins);
			} else if (checkins != null && checkins.size() == 0) {
				Toast.makeText(PatientViewCheckinsActivity.this, "No Check-Ins found", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PatientViewCheckinsActivity.this,
						"Error fetching Check-Ins.\nPlease check network status.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
