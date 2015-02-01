package org.coursera.capstone.syman;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.coursera.capstone.syman.adapter.PrescriptionListAdapter;
import org.coursera.capstone.syman.api.SymptomServiceApi;
import org.coursera.capstone.syman.model.GraphSamplePoint;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.model.PatientCompact;
import org.coursera.capstone.syman.util.CallableTask;
import org.coursera.capstone.syman.util.CommonDefs;
import org.coursera.capstone.syman.util.SymptomServiceMgr;
import org.coursera.capstone.syman.util.TaskCallback;
import org.coursera.capstone.syman.util.UserPhotoFileHandler;

import retrofit.client.Header;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DoctorViewPatientActivity extends Activity {
	private final String TAG = "DEBUG_JAN";
	private SurfaceView mSurfaceView;
	private Patient mPatient;
	private PatientCompact mPatientCompact;
	private PrescriptionListAdapter mPrescriptionAdapter;
	private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private boolean mCanvasCreated = false;
	private Paint greenPaint = new Paint();
	private Paint yellowPaint = new Paint();
	private Paint redPaint = new Paint();
	private Paint grayPaint = new Paint();
	private Paint bluePaint = new Paint();
	private Paint blackPaint = new Paint();
	private Paint painPaint = new Paint();
	private Paint foodPaint = new Paint();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_view_patient);
		mPatientCompact = (PatientCompact) getIntent().getSerializableExtra(CommonDefs.INTENT_PATIENT_COMPACT_SERIALIZED_EXTRA);
		
		ListView lv = (ListView) findViewById(R.id.patient_prescriptions_list_view);
		mPrescriptionAdapter = new PrescriptionListAdapter(this);
		lv.setAdapter(mPrescriptionAdapter);
		
		refreshPatientPhoto();
		
		initializePaint();
		mSurfaceView = (SurfaceView) findViewById(R.id.patient_chart_surface_view);
		mSurfaceView.getHolder().addCallback(new Callback() {

	        @Override
	        public void surfaceCreated(SurfaceHolder holder) {
	            // Do some drawing when surface is ready
	        	mCanvasCreated = true;
	            Canvas canvas = holder.lockCanvas();
	            paintBackgroundGrid(canvas);
	            holder.unlockCanvasAndPost(canvas);
	        }

	        @Override
	        public void surfaceDestroyed(SurfaceHolder holder) {
	        }

	        @Override
	        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	        }
	    });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshPatientData();
		fetchPatientGraphData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.doctor_view_patient, menu);
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
	
	public void editPrescriptions(View view) {
		Intent editPrescriptionsIntent = new Intent(this, EditPrescriptionsActivity.class);
		// TODO: Fix need to send entire patient. Parcelable?
		editPrescriptionsIntent.putExtra(
				CommonDefs.INTENT_PATIENT_SERIALIZED_EXTRA,
				mPatient);
		startActivity(editPrescriptionsIntent);
	}
	
	private void updatePatientData(Patient patient) {
		mPatient = patient;
		TextView tv = (TextView) findViewById(R.id.patient_id_text);
		tv.setText(Long.toString(mPatient.getId()));
		tv = (TextView) findViewById(R.id.patient_view_date_of_birth_text);
		tv.setText(mSimpleDateFormat.format(mPatient.getDateOfBirth()));
		tv = (TextView) findViewById(R.id.patient_view_phone_text);
		tv.setText(mPatient.getPhone());
		tv = (TextView) findViewById(R.id.patient_view_email_text);
		tv.setText(mPatient.getEmail());
		tv = (TextView) findViewById(R.id.patient_view_diagnosis_text);
		tv.setText(mPatient.getDiagnosis());
		tv = (TextView) findViewById(R.id.patient_view_full_name_text);
		if (mPatient.getMiddleName() == null || mPatient.getMiddleName().equals("")) {
			tv.setText(mPatient.getFirstName() + " " + mPatient.getLastName());
		} else {
			tv.setText(mPatient.getFirstName() + " " + mPatient.getMiddleName() + " " + mPatient.getLastName());
		}
		mPrescriptionAdapter.swapList(mPatient.getPrescriptions());
	}
	
	
	private void refreshPatientData() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<Patient>() {

			@Override
			public Patient call() throws Exception {
				return symptomService.getDoctorsPatientById(mPatientCompact.getId());
			}
		}, new TaskCallback<Patient>() {

			@Override
			public void success(Patient patient) {
				updatePatientData(patient);
			}

			@Override
			public void error(Exception e) {
				Log.e(DoctorViewPatientActivity.class.getName(), "Error fetching patient data.", e);
				
				Toast.makeText(
						DoctorViewPatientActivity.this,
						"Error fetching patient data, check your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	
	private void refreshPatientPhoto() {
		Uri photoUri = UserPhotoFileHandler.findPhotoForUserId(mPatientCompact.getUserid());

        // Always load photo fro demo
//		if (photoUri != null) {
//			ImageView imageView = (ImageView) findViewById(R.id.patient_profile_image);
//			imageView.setImageURI(photoUri);
//			return;
//		} else {
			final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
			
			CallableTask.invoke(new Callable<Uri>() {

				@Override
				public Uri call() throws Exception {
					Response response = symptomService.getPhotoByUserId(mPatientCompact.getUserid());
					List<Header> headers = response.getHeaders();
					String contentType = "";
					for (Header header : headers) {
						if (header.getName().equals("Content-Type")) {
							contentType = header.getValue();
							Log.d(TAG,"Downloaded image with Content-Type: " + contentType);
							break;
						}
					}
					Uri fileUri = UserPhotoFileHandler.savePhotoForUserId(
							mPatientCompact.getUserid(), 
							contentType,
							response.getBody().in());
					if (fileUri == null) {
						throw new Exception("Could not save downloaded file.");
					}
					return fileUri;
				}
			}, new TaskCallback<Uri>() {

				@Override
				public void success(Uri photoUri) {
					ImageView imageView = (ImageView) findViewById(R.id.patient_profile_image);
					imageView.setImageURI(photoUri);
				}

				@Override
				public void error(Exception e) {
					Log.e(DoctorViewPatientActivity.class.getName(), "Error fetching patient photo.", e);
					
					Toast.makeText(
							DoctorViewPatientActivity.this,
							"Error fetching patient photo, check your Internet connection.",
							Toast.LENGTH_LONG).show();
				}
			});
//		}
	}
	
	
	private void fetchPatientGraphData() {
		final SymptomServiceApi symptomService = SymptomServiceMgr.getServiceOrShowLogin(this);
		
		CallableTask.invoke(new Callable<List<GraphSamplePoint>>() {

			@Override
			public List<GraphSamplePoint> call() throws Exception {
				return symptomService.getPatientsGraphSamples(mPatientCompact.getId());
			}
		}, new TaskCallback<List<GraphSamplePoint>>() {

			@Override
			public void success(List<GraphSamplePoint> samplePoints) {
				// TODO: Draw graph
//				updatePatientData(patient);
				mSurfaceView = (SurfaceView) findViewById(R.id.patient_chart_surface_view);
				SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
				Canvas canvas = surfaceHolder.lockCanvas();
				paintBackgroundGrid(canvas);
				drawGraphsFromSamplePoints(canvas, samplePoints);
				surfaceHolder.unlockCanvasAndPost(canvas);
			}

			@Override
			public void error(Exception e) {
				Log.e(DoctorViewPatientActivity.class.getName(), "Error fetching patient graph data.", e);
				// TODO: Draw empty graph
				Toast.makeText(
						DoctorViewPatientActivity.this,
						"Error fetching patient graph data,\ncheck your Internet connection.",
						Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void initializePaint() {
		greenPaint.setColor(Color.GREEN);
		yellowPaint.setColor(Color.YELLOW);
		redPaint.setColor(Color.RED);
		grayPaint.setColor(Color.LTGRAY);
		grayPaint.setStyle(Paint.Style.STROKE);
		bluePaint.setColor(Color.BLUE);
		blackPaint.setColor(Color.BLACK);
		blackPaint.setStyle(Paint.Style.STROKE);
		painPaint.setColor(Color.MAGENTA);
		foodPaint.setColor(Color.CYAN);
	}
	
	private void paintBackgroundGrid(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		float topLineY = height / 4;
		float midLineY = height / 2;
		float bottomLineY = height * 3 / 4;
		float lineStartX = width / 10;
		float lineEndX = width * 19 / 20;
		float markMinus12h = lineStartX + (lineEndX-lineStartX) *2 / 3;
		float markMinus24h = lineStartX + (lineEndX-lineStartX) / 3;
		float legendLength = width/20;
		// Background
		canvas.drawColor(Color.WHITE);
		// Chart lines
		grayPaint.setStrokeWidth(1f);
		canvas.drawLine(lineStartX, topLineY, lineEndX, topLineY, grayPaint);
		canvas.drawLine(lineStartX, midLineY, lineEndX, midLineY, grayPaint);
		canvas.drawLine(lineStartX, bottomLineY, lineEndX, bottomLineY, grayPaint);
		grayPaint.setStrokeWidth(2f);
		canvas.drawLine(lineStartX, bottomLineY-4, lineStartX, bottomLineY+6, grayPaint);
		canvas.drawLine(markMinus24h, bottomLineY-4, markMinus24h, bottomLineY+6, grayPaint);
		canvas.drawLine(markMinus12h, bottomLineY-4, markMinus12h, bottomLineY+6, grayPaint);
		canvas.drawLine(lineEndX, bottomLineY-4, lineEndX, bottomLineY+6, grayPaint);
		// Red/Yellow/Green severity marker dots on the left
		canvas.drawCircle(lineStartX/2, topLineY, lineStartX/6, redPaint);
		canvas.drawCircle(lineStartX/2, midLineY, lineStartX/6, yellowPaint);
		canvas.drawCircle(lineStartX/2, bottomLineY, lineStartX/6, greenPaint);
		// Legend
		painPaint.setStrokeWidth(4f);
		canvas.drawLine(lineStartX, topLineY/3, lineStartX+legendLength, topLineY/3, painPaint);
		blackPaint.setTextSize(topLineY/3);
		canvas.drawText("Pain status", lineStartX+legendLength+6, topLineY/2, blackPaint);
		foodPaint.setStrokeWidth(4f);
		canvas.drawLine(width/2, topLineY/3, width/2+legendLength, topLineY/3, foodPaint);
		canvas.drawText("Food status", width/2+legendLength+6, topLineY/2, blackPaint);
		// Scale
		float scaleTextY = bottomLineY + topLineY*3/5;
		canvas.drawText("-36h", lineStartX - getPixelWidthForText("-36h",blackPaint)/2, scaleTextY, blackPaint);
		canvas.drawText("-24h", markMinus24h - getPixelWidthForText("-24h",blackPaint)/2, scaleTextY, blackPaint);
		canvas.drawText("-12h", markMinus12h - getPixelWidthForText("-12h",blackPaint)/2, scaleTextY, blackPaint);
		canvas.drawText("Now", lineEndX - getPixelWidthForText("Now",blackPaint)/2, scaleTextY, blackPaint);
		
	}
	
	
	private void drawGraphsFromSamplePoints(Canvas canvas, List<GraphSamplePoint> samplePoints) {
		if (samplePoints == null || samplePoints.size() < 1) {
			drawNoDataForGraph(canvas);
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		float topLineY = height / 4;
		float midLineY = height / 2;
		float bottomLineY = height * 3 / 4;
		float baseTopYPain = topLineY + 2;
		float baseTopYFood = topLineY - 2;
		float stepY = midLineY - topLineY;
		float lineStartX = width / 10;
		float lineEndX = width * 19 / 20;
		float lengthX = lineEndX - lineStartX;
		Date dateNow = new Date();
		long now = dateNow.getTime();
		long millisec36Hours = 36 * 60 * 60 * 1000;
		long startTime = now - millisec36Hours;
		float[] painArray = new float[samplePoints.size()*4 - 2];
		float[] foodArray = new float[samplePoints.size()*4 - 2];
		// Make sure sorted
		Collections.sort(samplePoints);
		if (samplePoints.size() == 1) {
			// Draw two dots
			GraphSamplePoint sample = samplePoints.get(0);
			float scaleX = ((float)sample.getSampleTime().getTime() - (float)startTime) / (float)millisec36Hours;
			canvas.drawCircle(lineStartX + lengthX * scaleX - 2,
					baseTopYPain + stepY * mapPainStatusToStepMultiplier(sample.getPainStatus()),
					lineStartX/8,
					painPaint);
			canvas.drawCircle(lineStartX + lengthX * scaleX + 2,
					baseTopYFood + stepY * mapFoodStatusToStepMultiplier(sample.getFoodStatus()),
					lineStartX/8,
					foodPaint);
		} else {
			int index = 0;
			for (GraphSamplePoint sample : samplePoints) {
				float scaleX = ((float)sample.getSampleTime().getTime() - (float)startTime) / (float)millisec36Hours;
				painArray[index] = lineStartX + lengthX * scaleX ;
				foodArray[index] = lineStartX + lengthX * scaleX ;
				painArray[index+1] = baseTopYPain + stepY * mapPainStatusToStepMultiplier(sample.getPainStatus());
				foodArray[index+1] = baseTopYFood + stepY * mapFoodStatusToStepMultiplier(sample.getFoodStatus());
				if (index > 0) {
					// Write the first in the next pair
					index = index+2;
					painArray[index] = lineStartX + lengthX * scaleX;
					foodArray[index] = lineStartX + lengthX * scaleX;
					painArray[index+1] = baseTopYPain + stepY * mapPainStatusToStepMultiplier(sample.getPainStatus());
					foodArray[index+1] = baseTopYFood + stepY * mapFoodStatusToStepMultiplier(sample.getFoodStatus());
				}
				index = index+2;
			}
		}
		painPaint.setStrokeWidth(3);
		foodPaint.setStrokeWidth(3);
		canvas.drawLines(foodArray, foodPaint);
		canvas.drawLines(painArray, painPaint);
	}
	
	private void drawNoDataForGraph(Canvas canvas) {
		// TODO: DRAW!!
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		float midY = height / 2;
		float midX = width / 2;
		String noGraphText = "No graph data!";
		float textSize = height/7;
		bluePaint.setTextSize(textSize);
		canvas.drawText(noGraphText, midX - getPixelWidthForText(noGraphText,bluePaint)/2, midY+(textSize/3), bluePaint);
	}
	
	private float getPixelWidthForText(String text, Paint paint) {
		int strlen = text.length();
		float[] widths = new float[strlen];
		paint.getTextWidths(text, widths);
		float sum = 0.0f;
		for (int i=0; i<strlen; i++) {
			sum += widths[i];
		}
		return sum;
	}

	private int mapPainStatusToStepMultiplier(int status) {
		if (status == 3) {
			return 0;
		} else if (status == 2) {
			return 1;
		} else if (status == 1) {
			return 2;
		} else {
			return 2;
		}
	}
	
	private int mapFoodStatusToStepMultiplier(int status) {
		if (status == 3) {
			return 0;
		} else if (status == 2) {
			return 1;
		} else if (status == 1) {
			return 2;
		} else {
			return 2;
		}
	}
	
}
