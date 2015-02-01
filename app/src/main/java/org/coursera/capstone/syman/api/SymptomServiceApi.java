package org.coursera.capstone.syman.api;

import java.util.List;

import org.coursera.capstone.syman.model.Checkin;
import org.coursera.capstone.syman.model.Doctor;
import org.coursera.capstone.syman.model.GraphSamplePoint;
import org.coursera.capstone.syman.model.Medication;
import org.coursera.capstone.syman.model.Patient;
import org.coursera.capstone.syman.model.PatientCompact;
import org.coursera.capstone.syman.model.Prescription;
import org.coursera.capstone.syman.model.UserInfo;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface SymptomServiceApi {
    public final static String SYMPTOM_SERVICE_BASE_URL = "https://192.168.1.4:8443";
	//public final static String SYMPTOM_SERVICE_BASE_URL = "https://cloud9.dnsalias.com:8443";
	public final static String CLIENT_ID = "mobile";
	
	public final static String TOKEN_PATH = "/oauth/token";
	public final static String PATIENT_ID_PATHVAR = "patientId";
	public final static String PRESCRIPTION_ID_PATHVAR = "prescriptionId";
	public final static String USER_ID_PATHVAR = "userId";
	public final static String PATIENT_NAME_REQUEST_PARAM = "patient_name";
	public final static String USER_INFO_PATH = "/userinfo";	
	public final static String PATIENT_BASE_PATH = "/patient";
	public final static String PHOTO_BASE_PATH = "/photos";
	public final static String PHOTO_USER_ID_PATH = PHOTO_BASE_PATH + "/{" + USER_ID_PATHVAR + "}";
	public final static String MEDICATIONS_BASE_PATH = "/medications";
	public final static String PATIENT_DOCTORS_PATH = PATIENT_BASE_PATH + "/doctors";
	public final static String PATIENT_CHECKINS_PATH = PATIENT_BASE_PATH + "/checkins";
	public final static String DOCTOR_BASE_PATH = "/doctor";
	public final static String DOCTOR_PATIENTS_PATH = DOCTOR_BASE_PATH + "/patients";
	public final static String DOCTOR_PATIENTS_COMPACT_PATH = DOCTOR_PATIENTS_PATH + "/compact";
	public final static String DOCTOR_PATIENT_PATH = DOCTOR_PATIENTS_PATH + "/{" + PATIENT_ID_PATHVAR + "}";
	public final static String DOCTOR_PATIENTS_CHECKIN_PATH = DOCTOR_PATIENTS_PATH + "/{"
			+ PATIENT_ID_PATHVAR + "}/checkins";
	public final static String DOCTOR_PATIENTS_PRESCRIPTIONS_PATH = DOCTOR_PATIENTS_PATH + "/{"
			+ PATIENT_ID_PATHVAR + "}/prescriptions";
	public final static String DOCTOR_PATIENTS_PRESCRIPTION_PATH = DOCTOR_PATIENTS_PATH + "/{"
			+ PATIENT_ID_PATHVAR + "}/prescriptions/{" + PRESCRIPTION_ID_PATHVAR + "}";
	public final static String DOCTOR_PATIENTS_GRAPH_PATH = DOCTOR_PATIENTS_PATH + "/{"
			+ PATIENT_ID_PATHVAR + "}/graphsamples";
	public final static String DOCTOR_CHECKIN_SEARCH_PATH = DOCTOR_BASE_PATH + "/checkinsearch";
	public final static String ALARMS_BASE_PATH = DOCTOR_BASE_PATH + "/alarms";

	//-----------------------------------------------------------
	// Common
	//-----------------------------------------------------------
	@GET(USER_INFO_PATH)
	public UserInfo getUserInfo();
	
	@Multipart
	@POST(PHOTO_BASE_PATH)
	public String setUserPhoto(@Part("data") TypedFile photo);
	
	@GET(PHOTO_BASE_PATH)
	public Response getUserPhoto();
	
	@GET(PHOTO_USER_ID_PATH)
	public Response getPhotoByUserId(@Path(USER_ID_PATHVAR) String userId);
	
	@GET(MEDICATIONS_BASE_PATH)
	public List<Medication> getMedications();
	
	//-----------------------------------------------------------
	// Patient
	//-----------------------------------------------------------
	@GET(PATIENT_BASE_PATH)
	public Patient getPatient();
	
	@GET(PATIENT_DOCTORS_PATH)
	public List<Doctor> getPatientsDoctors();
	
	@GET(PATIENT_CHECKINS_PATH)
	public List<Checkin> getPatientsCheckins();

	@POST(PATIENT_CHECKINS_PATH)
	public Checkin postPatientCheckin(@Body Checkin checkin);

	@PUT(PATIENT_BASE_PATH)
	public Patient updatePatient(@Body Patient patient);
	
	//-----------------------------------------------------------
	// Doctor
	//-----------------------------------------------------------
	@GET(DOCTOR_BASE_PATH)
	public Doctor getDoctor();
	
	@GET(DOCTOR_PATIENTS_PATH)
	public List<Patient> getDoctorsPatients();
	
	@GET(DOCTOR_PATIENTS_COMPACT_PATH)
	public List<PatientCompact> getDoctorsPatientsCompact();
	
	@GET(DOCTOR_PATIENT_PATH)
	public Patient getDoctorsPatientById(@Path(PATIENT_ID_PATHVAR) long patientId);
	
	@PUT(DOCTOR_BASE_PATH)
	public Doctor updateDoctor(@Body Doctor doctor);
	
	@GET(DOCTOR_PATIENTS_CHECKIN_PATH)
	public List<Checkin> getPatientsCheckinsById(@Path(PATIENT_ID_PATHVAR) long patientId);
	
	@GET(DOCTOR_PATIENTS_PRESCRIPTIONS_PATH)
	public List<Prescription> getPatientsPrescriptions(@Path(PATIENT_ID_PATHVAR) long patientId);
	
	@POST(DOCTOR_PATIENTS_PRESCRIPTIONS_PATH)
	public Prescription addPatientPrescription(
			@Path(PATIENT_ID_PATHVAR) long patientId,
			@Body Prescription prescription);

	@PUT(DOCTOR_PATIENTS_PRESCRIPTION_PATH)
	public Prescription updatePatientPrescription(
			@Path(PATIENT_ID_PATHVAR) long patientId,
			@Path(PRESCRIPTION_ID_PATHVAR) long prescriptionId,
			@Body Prescription prescription);
	
	@DELETE(DOCTOR_PATIENTS_PRESCRIPTION_PATH)
	public int deletePatientPrescription(
			@Path(PATIENT_ID_PATHVAR) long patientId,
			@Path(PRESCRIPTION_ID_PATHVAR) long prescriptionId);
	
	@GET(DOCTOR_PATIENTS_GRAPH_PATH)
	public List<GraphSamplePoint> getPatientsGraphSamples(
			@Path(PATIENT_ID_PATHVAR) long patientId);
	
	@GET(DOCTOR_CHECKIN_SEARCH_PATH)
	public List<Checkin> searchCheckinsByPatientName(
			@Query(PATIENT_NAME_REQUEST_PARAM) String patientName);
	
	@GET(ALARMS_BASE_PATH)
	public List<Patient> getPatientsWithAlarms();

    //
    // Endring for test-commit
}
