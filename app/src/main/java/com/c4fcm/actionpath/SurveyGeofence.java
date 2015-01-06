package com.c4fcm.actionpath;

public class SurveyGeofence extends SimpleGeofence {

	private String surveyKey;
	private String uniqueID;
	
	public SurveyGeofence(double latitude, double longitude, float radius,
			long expiration, int transition) {
		super(latitude, longitude, radius, expiration, transition);
	}

	public SurveyGeofence(String uniqueID, String surveyKey, double latitude, double longitude, float radius,
			long expiration, int transition) {
		super(latitude, longitude, radius, expiration, transition);
		setSurveyKey(surveyKey);
		setUniqueID(uniqueID);
	}
	
	public SurveyGeofence(String uniqueID, String surveyKey, String geofenceId, double latitude, double longitude, float radius,
			long expiration, int transition) {
		super(geofenceId, latitude, longitude, radius, expiration, transition);
		setSurveyKey(surveyKey);
		setUniqueID(uniqueID);
	}
	
	public void setSurveyKey(String sk){
		this.surveyKey = sk;
	}
	
	public String getSurveyKey(){
		return this.surveyKey;
	}
	
	public void setUniqueID(String id){
		this.uniqueID = id;
	}
	
	public String getUniqueID(){
		return this.uniqueID;
	}
}
