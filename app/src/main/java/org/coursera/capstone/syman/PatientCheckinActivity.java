package org.coursera.capstone.syman;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.Medication;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.model.Prescription;
import org.coursera.capstone.syman.model.PrescriptionCheckin;
import org.coursera.capstone.syman.persistence.PatientPersistenceHelper;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.SymptomServiceMgr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PatientCheckinActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private Patient mPatient;
	private Checkin mCheckin = new Checkin();
	private Hashtable<String, PrescriptionCheckin> mPrescriptionHash = 
			new Hashtable<String, PrescriptionCheckin>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPatient = PatientPersistenceHelper.getPatient(this);
		mCheckin.setPatient(mPatient);
		setContentView(R.layout.activity_patient_checkin);
		buildGUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.patient_checkin, menu);
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
	
	
	public void submitCheckin() {
		if (mCheckin.getPainStatus() == null || mCheckin.getFoodStatus() == null) {
			Toast.makeText(this, "Must answer at least\npain status and food status.", Toast.LENGTH_LONG).show();
			return;
		}
		postCheckinToServer();
	}
	
	
	private void buildGUI() {
		final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout rootLayout = (LinearLayout) findViewById(R.id.LinearLayoutCheckIn);
		
		ScrollView scrollView = new ScrollView(this);
		
		LinearLayout lLayout = new LinearLayout(this);
		lLayout.setOrientation(LinearLayout.VERTICAL);
		
		// Creating Pain Check-In item
		View view = inflater.inflate(R.layout.list_item_pain_check_in, null);
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.pain_status_radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d(TAG, "Pain status checkedId: " + checkedId);
				switch (checkedId) {
				case R.id.pain_status_well:
					Log.d(TAG, "Pain status is 'well-controlled'");
					mCheckin.setPainStatus(CommonDefs.CHECK_IN_PAIN_CONTROLLED);
					break;
				case R.id.pain_status_moderate:
					Log.d(TAG, "Pain status is 'moderate'");
					mCheckin.setPainStatus(CommonDefs.CHECK_IN_PAIN_MODERATE);
					break;
				case R.id.pain_status_severe:
					Log.d(TAG, "Pain status is 'severe'");
					mCheckin.setPainStatus(CommonDefs.CHECK_IN_PAIN_SEVERE);
					break;
				default:
					Log.d(TAG, "Pain status is unknown");
				}
			}
		});
		lLayout.addView(view);
		
		// Creating Medication Check-In(s)
		if (mPatient.getPrescriptions() == null || mPatient.getPrescriptions().size() == 0) {
			view = inflater.inflate(R.layout.list_item_prescriptions_empty, null);
			lLayout.addView(view);
		} else if (mPatient.getPrescriptions().size() == 1) {
			view = inflater.inflate(R.layout.list_item_prescription_check_in, null);
			TextView tv = (TextView) view.findViewById(R.id.check_in_prescription_question);
			final Medication medicine = mPatient.getPrescriptions().get(0).getMedicine();
			tv.setText(R.string.list_item_one_prescription_text);
			radioGroup = (RadioGroup) view.findViewById(R.id.prescription_status_radio_group);
			radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				private Medication medication = medicine;
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					Log.d(TAG, "Prescription (multi) status checkedId: " + checkedId);
					Log.d(TAG, "Medication: " + medication.getName());
					
					switch (checkedId) {
					case R.id.prescription_status_yes:
						Log.d(TAG, "Prescription status is 'yes'");
						// ---- Start
						new AlertDialog.Builder(PatientCheckinActivity.this)
						.setTitle("Date & Time taken")
						.setMessage("Sett dato/tid")
						.setView(inflater.inflate(R.layout.date_time_picker, null))
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								Log.d(TAG, "Prescription datetime 'Ok'");
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								Log.d(TAG, "Prescription datetime 'Cancel'");
							}
						}).show();
						// ---- Stopp
						if (mCheckin.getPrescriptionCheckins() == null) {
							mCheckin.setPrescriptionCheckins(new ArrayList<PrescriptionCheckin>());
						}
						if (mCheckin.getPrescriptionCheckins().size() == 0) {
							PrescriptionCheckin prescriptionCI = new PrescriptionCheckin();
							prescriptionCI.setMedicine(medication);
							// TODO: Set date from input
							prescriptionCI.setTimeTaken(new Date());
							mCheckin.getPrescriptionCheckins().add(prescriptionCI);
						}
						break;
					case R.id.prescription_status_no:
						Log.d(TAG, "Prescription status is 'no'");
						if (mCheckin.getPrescriptionCheckins() != null) {
							mCheckin.getPrescriptionCheckins().clear();
						}
						break;
					default:
						Log.d(TAG, "Food status is unknown");
					}
				}
			});
			lLayout.addView(view);
		} else {
			for (Prescription prescription : mPatient.getPrescriptions()) {
				view = inflater.inflate(R.layout.list_item_prescription_check_in, null);
				final Medication medicine = prescription.getMedicine();
				TextView tv = (TextView) view.findViewById(R.id.check_in_prescription_question);
				tv.setText(R.string.list_item_multi_prescription_text);
				tv.setText(tv.getText() + "\n" + medicine.getName() + "?");
				radioGroup = (RadioGroup) view.findViewById(R.id.prescription_status_radio_group);
				radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					private Medication medication = medicine;
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Log.d(TAG, "Prescription (multi) status checkedId: " + checkedId);
						Log.d(TAG, "Medication: " + medication.getName());
						
						switch (checkedId) {
						case R.id.prescription_status_yes:
							Log.d(TAG, "Prescription status is 'yes'");
							// ---- Start
							new AlertDialog.Builder(PatientCheckinActivity.this)
							.setTitle("Date & Time taken")
							.setMessage("Sett dato/tid")
							.setView(inflater.inflate(R.layout.date_time_picker, null))
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									Log.d(TAG, "Prescription datetime 'Ok'");
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									Log.d(TAG, "Prescription datetime 'Cancel'");
								}
							}).show();
							// ---- Stopp
							if (mPrescriptionHash.get(medication.getName()) == null) {
								PrescriptionCheckin prescriptionCI = new PrescriptionCheckin();
								prescriptionCI.setMedicine(medication);
								// TODO: Set date from input
								prescriptionCI.setTimeTaken(new Date());
								mPrescriptionHash.put(medication.getName(), prescriptionCI);
							}
							break;
						case R.id.prescription_status_no:
							Log.d(TAG, "Prescription status is 'no'");
							if (mPrescriptionHash.get(medication.getName()) != null) {
								mPrescriptionHash.remove(medication.getName());
							}
							break;
						default:
							Log.d(TAG, "Food status is unknown");
						}
					}
				});
				lLayout.addView(view);
			}
		}
		
		// Creating food Check-In item
		view = inflater.inflate(R.layout.list_item_food_check_in, null);
		radioGroup = (RadioGroup) view.findViewById(R.id.food_status_radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d(TAG, "Food status checkedId: " + checkedId);
				switch (checkedId) {
				case R.id.food_status_no:
					Log.d(TAG, "Food status is 'no'");
					mCheckin.setFoodStatus(CommonDefs.CHECK_IN_PROBLEM_EATING_NO);
					break;
				case R.id.food_status_some:
					Log.d(TAG, "Food status is 'some'");
					mCheckin.setFoodStatus(CommonDefs.CHECK_IN_PROBLEM_EATING_SOME);
					break;
				case R.id.food_status_cant_eat:
					Log.d(TAG, "Food status is 'can't eat'");
					mCheckin.setFoodStatus(CommonDefs.CHECK_IN_PROBLEM_EATING_YES);
					break;
				default:
					Log.d(TAG, "Food status is unknown");
				}
			}
		});
		lLayout.addView(view);
		
		// Add submit button
		Button button = new Button(this);
		button.setText("Submit Check-In");
		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(buttonLayoutParams);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCheckin.setTimestamp(new Date());
				if (!mPrescriptionHash.isEmpty()) {
					mCheckin.setPrescriptionCheckins(new ArrayList<PrescriptionCheckin>());
					mCheckin.getPrescriptionCheckins().addAll(mPrescriptionHash.values());
				}
				submitCheckin();
			}
		});
		lLayout.addView(button);
		
		scrollView.addView(lLayout);
		rootLayout.addView(scrollView);
	}
	
	
	private void postCheckinToServer() {
		// TODO: Store Check-In for guaranteed delivery when network
		new PostCheckInDataAsyncTask().execute();
	}
	
	
	private class PostCheckInDataAsyncTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(PatientCheckinActivity.this);
			if (symptomService == null) {
				return false;
			}
			try {
				symptomService.postPatientCheckin(mCheckin);
				return true;
			} catch (Exception e) {
				Log.e(TAG, "Exception: " + e.toString());
				PatientPersistenceHelper.storeCheckInForLaterSubmission(PatientCheckinActivity.this, mCheckin);
				return false;
			}
		}

		protected void onPostExecute(Boolean success) {
			if (success) {
				Toast.makeText(PatientCheckinActivity.this, R.string.common_edit_upload_success_text, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(PatientCheckinActivity.this, R.string.common_edit_upload_fail_text, Toast.LENGTH_LONG).show();
			}
			finish();
		}
	}
}
