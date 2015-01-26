package org.coursera.capstone.syman;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.coursera.capstone.syman.adapter.MedicationListAdapter;
import org.coursera.capstone.syman.adapter.PrescriptionListAdapter;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.Medication;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.model.Prescription;
import org.coursera.capstone.syman.util.CallableTask;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.SymptomServiceMgr;
import org.coursera.capstone.syman.util.TaskCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EditPrescriptionsActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private Patient mPatient;
	private List<Prescription> mPrescriptions;
	private List<Medication> mMedications;
	private PrescriptionListAdapter mPrescriptionsAdapter;
	private MedicationListAdapter mMedicationsAdapter;
	private PrescriptionTargetDragListener mDragEventListener = new PrescriptionTargetDragListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_prescriptions);
		mPatient = (Patient) getIntent().getSerializableExtra(CommonDefs.INTENT_PATIENT_SERIALIZED_EXTRA);
		mPrescriptionsAdapter = new PrescriptionListAdapter(this);
		mMedicationsAdapter = new MedicationListAdapter(this);
		setupPrescriptions();
		setupMedications();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_prescriptions, menu);
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
	
	
	private void setupPrescriptions() {
		ListView prescriptionListView = (ListView) findViewById(R.id.list_view_prescriptions);
		prescriptionListView.setAdapter(mPrescriptionsAdapter);
		prescriptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final Prescription prescription = (Prescription)mPrescriptionsAdapter.getItem(position);
				final EditText input = new EditText(EditPrescriptionsActivity.this);
				input.setText(prescription.getUsage());
				new AlertDialog.Builder(EditPrescriptionsActivity.this)
					.setTitle(prescription.getMedicine().getName())
					.setMessage(R.string.edit_prescriptions_usage_info_text)
					.setView(input)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String newUsage = input.getText().toString();
							if (!prescription.getUsage().equals(newUsage)) {
								prescription.setUsage(newUsage);
								updatePatientPrescription(prescription);
								mPrescriptionsAdapter.notifyDataSetChanged();
							}
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
						}
					}).show();
			}
		});
		
		prescriptionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Prescription prescription = (Prescription)mPrescriptionsAdapter.getItem(position);
				new AlertDialog.Builder(EditPrescriptionsActivity.this)
				.setTitle(prescription.getMedicine().getName())
				.setMessage(R.string.edit_prescriptions_delete_text)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						deletePrescription(prescription);
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
		            // Do nothing.
					}
				}).show();
				return true;
			}
		});
		
		RelativeLayout dropTargetLayout = (RelativeLayout) prescriptionListView.getParent();
		dropTargetLayout.setOnDragListener(mDragEventListener);
		
		mPrescriptions = mPatient.getPrescriptions();
		mPrescriptionsAdapter.swapList(mPrescriptions);
	}
	
	
	private void setupMedications() {
		ListView medicationListView = (ListView) findViewById(R.id.list_view_medications);
		medicationListView.setAdapter(mMedicationsAdapter);
		medicationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Medication medication = (Medication) mMedicationsAdapter.getItem(position);
				ClipData.Item item = new ClipData.Item(medication.getName());
				String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
				ClipData dragData = new ClipData(medication.getName(),
					     clipDescription,
					     item);
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//				DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
				view.startDrag(dragData, shadowBuilder, view, 0);
				return true;
			}
		});
		new FetchMedicationsAsyncTask().execute();
	}
	
	
	private void updatePatientPrescription(Prescription prescription) {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		final Prescription localPrescription = prescription;
		CallableTask.invoke(new Callable<Prescription>() {

			@Override
			public Prescription call() throws Exception {
				return symptomService.updatePatientPrescription(
						mPatient.getId(), localPrescription.getId(), localPrescription);
			}
		}, new TaskCallback<Prescription>() {

			@Override
			public void success(Prescription prescription) {
				// Already updated locally
				Toast.makeText(EditPrescriptionsActivity.this,
						"Prescription updated.",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void error(Exception e) {
				Log.e(EditPrescriptionsActivity.class.getName(), "Error fetching patient data.", e);
				
				Toast.makeText(
						EditPrescriptionsActivity.this,
						"Error fetching patient data, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	private void postNewPrescription(Prescription prescription) {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		final Prescription localPrescription = prescription;
		CallableTask.invoke(new Callable<Prescription>() {

			@Override
			public Prescription call() throws Exception {
				return symptomService.addPatientPrescription(mPatient.getId(), localPrescription);
			}
		}, new TaskCallback<Prescription>() {

			@Override
			public void success(Prescription prescription) {
				if (mPatient.getPrescriptions() == null) {
					mPatient.setPrescriptions(new ArrayList<Prescription>());
				}
				mPatient.getPrescriptions().add(prescription);
				mPrescriptionsAdapter.notifyDataSetChanged();
				mMedicationsAdapter.removeFromListByName(prescription.getMedicine().getName());
				Toast.makeText(EditPrescriptionsActivity.this,
						"Prescription added.",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void error(Exception e) {
				Log.e(EditPrescriptionsActivity.class.getName(), "Error posting prescription data.", e);
				
				Toast.makeText(
						EditPrescriptionsActivity.this,
						"Error posting prescription data,\ncheck your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	private void deletePrescription(Prescription prescription) {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		final Prescription localPrescription = prescription;
		CallableTask.invoke(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return symptomService.deletePatientPrescription(mPatient.getId(), localPrescription.getId());
			}
		}, new TaskCallback<Integer>() {

			@Override
			public void success(Integer result) {
				if (result == 1) {
					mMedicationsAdapter.addMedication(localPrescription.getMedicine());
					mPrescriptionsAdapter.removeFromListByMedicationName(
							localPrescription.getMedicine().getName());
					Toast.makeText(EditPrescriptionsActivity.this,
							"Prescription deleted.",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(EditPrescriptionsActivity.this,
							"Could not delete prescription.",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void error(Exception e) {
				Log.e(EditPrescriptionsActivity.class.getName(), "Error deleting prescription data.", e);
				
				Toast.makeText(
						EditPrescriptionsActivity.this,
						"Error deleting prescription data,\ncheck your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	private class PrescriptionTargetDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View view, DragEvent event) {
			int action = event.getAction();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				view.setBackgroundResource(R.drawable.background_drawable);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				view.setBackgroundResource(0);
				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				
				view.setBackgroundResource(0);
				ClipData.Item item = event.getClipData().getItemAt(0);
				final String medicationName = (String)item.getText();
				final Medication medication = mMedicationsAdapter.findMedicationByName(medicationName);
				Toast.makeText(EditPrescriptionsActivity.this, "Dropped: " + medicationName, Toast.LENGTH_SHORT).show();
				final Prescription prescription = new Prescription();
				prescription.setMedicine(medication);
				final EditText input = new EditText(EditPrescriptionsActivity.this);
				input.setText(prescription.getUsage());
				new AlertDialog.Builder(EditPrescriptionsActivity.this)
					.setTitle(prescription.getMedicine().getName())
					.setMessage(R.string.edit_prescriptions_add_text)
					.setView(input)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String newUsage = input.getText().toString();
							prescription.setUsage(newUsage);
							postNewPrescription(prescription);
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
						}
					}).show();
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				view.setBackgroundResource(0);
			default:
				break;
			}
			return true;
		}
	}
	
	
//	private static class MyDragShadowBuilder extends View.DragShadowBuilder {
//		private static Drawable shadow;
//		  
//		public MyDragShadowBuilder(View v) {
//			super(v);
//			shadow = new ColorDrawable(Color.LTGRAY);
//		}
//		  
//		@Override
//		public void onProvideShadowMetrics (Point size, Point touch){
//			int width = getView().getWidth();
//			int height = getView().getHeight();
//
//			shadow.setBounds(0, 0, width, height);
//			size.set(width, height);
//			touch.set(width / 2, height / 2);
//		}
//
//		@Override
//		public void onDrawShadow(Canvas canvas) {
//			shadow.draw(canvas);
//		}
//		  
//	}
	
	
	private class FetchMedicationsAsyncTask extends AsyncTask<Void, Void, List<Medication>> {
		protected List<Medication> doInBackground(Void... voids) {
			SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(EditPrescriptionsActivity.this);
			if (symptomService == null) {
				return null;
			}
			try {
				return symptomService.getMedications();
			} catch (Exception e) {
				Log.d(TAG, "Exception: " + e.toString());
				return null;
			}
		}
		protected void onPostExecute(List<Medication> medications) {
			if (medications != null) {
				mMedications = medications;
				mMedicationsAdapter.swapList(mMedications);
				for ( int i=0; i<mPrescriptionsAdapter.getCount(); i++) {
					Prescription p = (Prescription) mPrescriptionsAdapter.getItem(i);
					mMedicationsAdapter.removeFromListByName(p.getMedicine().getName());
				}
				mMedicationsAdapter.notifyDataSetChanged();
			} else if (medications.size() == 0) {
				Toast.makeText(EditPrescriptionsActivity.this, R.string.edit_prescriptions_empty_list_error, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(EditPrescriptionsActivity.this, R.string.edit_prescriptions_download_error, Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
