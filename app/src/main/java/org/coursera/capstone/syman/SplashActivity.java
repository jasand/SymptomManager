package org.coursera.capstone.syman;

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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends ActionBarActivity {
	private String TAG = "DEBUG_JAN";
	
	private boolean mAnimationDone = false;
	private boolean mGetUserInfoDone = false;
	private UserInfo mUserInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ImageView mediCloudImg = (ImageView) findViewById(R.id.splash_medicloud_img);
		ImageView starOfLifeImg = (ImageView) findViewById(R.id.start_of_life_img);
		ImageView symptomMgrImg = (ImageView) findViewById(R.id.splash_symptom_img);
//		Animation fade1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation rotate1 = AnimationUtils.loadAnimation(this, R.anim.rotate1);
		Animation rotate2 = AnimationUtils.loadAnimation(this, R.anim.rotate2);
		mediCloudImg.startAnimation(rotate1);
//		starOfLifeImg.startAnimation(fade1);
		symptomMgrImg.startAnimation(rotate2);
		new GetUserInfoAsyncTask().execute();
		
		rotate2.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if (mGetUserInfoDone) {
					if (mUserInfo != null && mUserInfo.getGrants().contains("PATIENT")) {
						Intent patientIntent = new Intent(SplashActivity.this, PatientMainActivity.class);
						patientIntent.putExtra(CommonDefs.INTENT_USERID_EXTRA, mUserInfo.getUserName());
						startActivity(patientIntent);
						SplashActivity.this.finish();
						return;
					} else if (mUserInfo != null && mUserInfo.getGrants().contains("DOCTOR")) {
						Intent doctorIntent = new Intent(SplashActivity.this, DoctorMainActivity.class);
						doctorIntent.putExtra(CommonDefs.INTENT_USERID_EXTRA, mUserInfo.getUserName());
						startActivity(doctorIntent);
						SplashActivity.this.finish();
						return;
					}
					else {
						startActivity(new Intent(SplashActivity.this,
								LoginActivity.class));
						SplashActivity.this.finish();
						return;
					}
				} else {
					mAnimationDone = true;
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ImageView mediCloudImg = (ImageView) findViewById(R.id.splash_medicloud_img);
//		ImageView starOfLifeImg = (ImageView) findViewById(R.id.start_of_life_img);
		ImageView symptomMgrImg = (ImageView) findViewById(R.id.splash_symptom_img);
		mediCloudImg.clearAnimation();
//		starOfLifeImg.clearAnimation();
		symptomMgrImg.clearAnimation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class GetUserInfoAsyncTask extends AsyncTask<Void, Void, UserInfo> {

		protected UserInfo doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceIfAvailable(SplashActivity.this);
			if (symptomService == null) {
				return null;
			}
			try {
				UserInfo info = symptomService.getUserInfo();
				return info;
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(UserInfo userInfo) {
			if (mAnimationDone) {
				if (userInfo != null && userInfo.getGrants().contains("PATIENT")) {
					Intent patientIntent = new Intent(SplashActivity.this, PatientMainActivity.class);
					patientIntent.putExtra(CommonDefs.INTENT_USERID_EXTRA, userInfo.getUserName());
					startActivity(patientIntent);
					SplashActivity.this.finish();
					return;
				} else if (userInfo != null && userInfo.getGrants().contains("DOCTOR")) {
					Intent doctorIntent = new Intent(SplashActivity.this, DoctorMainActivity.class);
					doctorIntent.putExtra(CommonDefs.INTENT_USERID_EXTRA, userInfo.getUserName());
					startActivity(doctorIntent);
					SplashActivity.this.finish();
					return;
				}
				else {
					startActivity(new Intent(SplashActivity.this,
							LoginActivity.class));
					SplashActivity.this.finish();
					return;
				}
			} else {
				mUserInfo = userInfo;
				mGetUserInfoDone = true;
			}
		}
	}
}
