package org.coursera.capstone.syman;

import org.coursera.capstone.syman.api.LoginInfo;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.UserInfo;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.SymptomServiceMgr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void login(View view) {
		LoginInfo loginInfo = new LoginInfo();
		EditText et = (EditText)findViewById(R.id.login_username);
		loginInfo.setUsername(et.getText().toString());
		et = (EditText)findViewById(R.id.login_password);
		loginInfo.setPassword(et.getText().toString());
		
		new LoginAsyncTask().execute(loginInfo);
		
	}
	
	private class LoginAsyncTask extends AsyncTask<LoginInfo, Void, UserInfo> {

		protected UserInfo doInBackground(LoginInfo... login) {
			String user = login[0].getUsername();
			String pass = login[0].getPassword();
			SymptomServiceApi symptomService = SymptomServiceMgr.init(LoginActivity.this, user, pass);
			Log.i("DEBUG_JAN", login[0].toString());
			try {
				UserInfo info = symptomService.getUserInfo();
				return info;
			} catch (Exception e) {
				Log.i("DEBUG_JAN", "Exception: " + e.toString());
				e.printStackTrace();
				return new UserInfo();
			}
		}

		protected void onPostExecute(UserInfo userInfo) { 
			Log.i("DEBUG_JAN", userInfo.toString());
			if (userInfo.getGrants().contains("PATIENT")) {
				Intent patientIntent = new Intent(LoginActivity.this, PatientMainActivity.class);
				patientIntent.putExtra(CommonDefs.INTENT_USERID_EXTRA, userInfo.getUserName());
				startActivity(patientIntent);
				LoginActivity.this.finish();
				return;
			} else if (userInfo.getGrants().contains("DOCTOR")) {
				Intent doctorIntent = new Intent(LoginActivity.this, DoctorMainActivity.class);
				doctorIntent.putExtra(CommonDefs.INTENT_USERID_EXTRA, userInfo.getUserName());
				startActivity(doctorIntent);
				LoginActivity.this.finish();
				return;
			}
			Toast.makeText(LoginActivity.this, "Could not connect to host", Toast.LENGTH_LONG).show();
		}
	}
}
