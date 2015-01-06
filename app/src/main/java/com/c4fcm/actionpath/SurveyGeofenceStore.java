package com.c4fcm.actionpath;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SurveyGeofenceStore extends SimpleGeofenceStore {

	public SurveyGeofenceStore(Context context) {
		super(context);
	}

	 /**
     * Save a geofence.
     * @param id The geofence id
     * @param geofence The {@link SurveyGeofence} containing the
     * values you want to save in SharedPreferences
     */
    public void setGeofence(String id, SurveyGeofence geofence) {
        /*
         * Get a SharedPreferences editor instance. 
         */
        Editor editor = mPrefs.edit();
        editor.putString(
        		getGeofenceFieldKey(id, GeofenceUtils.KEY_SURVEY_KEY),
                (String) geofence.getSurveyKey()
        		);
        editor.putString(
        		getGeofenceFieldKey(id, GeofenceUtils.KEY_UNIQUE_ID),
                (String) geofence.getUniqueID()
        		);
        editor.commit();
    	commitBasicGeofenceValues(id, geofence);
        if(!geofenceStoreKeys.contains(id)){
        	geofenceStoreKeys.add(id);
            commitGeofenceStoreKeys();
        }
    }
    
    /**
     *  Push a geofence into storage
	 * NOTE: THIS IS A TOTAL COPY FROM SimpleGeoFenceStore and should be refactored
	 *       SINCE THE ONLY DIFFERENCE is that we use SurveyGeofence
     * @param geofence The {@link SurveyGeofence} containing the
     * values you want to save in SharedPreferences
     */
    public void pushGeofence(SurveyGeofence geofence){
    	Editor editor = mPrefs.edit();
    	String maxFenceKey = GeofenceUtils.KEY_PREFIX + "_MAXFENCE";
        String maxFence = mPrefs.getString(maxFenceKey, GeofenceUtils.INVALID_STRING_VALUE);
             
        String newFenceID=Integer.toString(Integer.parseInt(maxFence)+1);
        
        geofence.setId(newFenceID);

        editor.putString(maxFenceKey, newFenceID);
        editor.commit();
   
        geofenceStoreKeys.add(newFenceID);
        commitGeofenceStoreKeys();
        setGeofence(newFenceID, geofence); 
        //TODO: VERIFY THAT THE GeofenceKey is being stored
        SurveyGeofence g = getGeofence(newFenceID);
        //Log.i("VerifySurveyKey",g.getSurveyKey());
    }
	
	/**
	 * NOTE: THIS IS A TOTAL COPY FROM SimpleGeoFenceStore and should be refactored
     * Returns a stored geofence by its id, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     * {@link SurveyGeofence}
     */
    public SurveyGeofence getGeofence(String id) {

    	String surveyKey = mPrefs.getString(
    			getGeofenceFieldKey(id,GeofenceUtils.KEY_SURVEY_KEY),
    			GeofenceUtils.INVALID_STRING_VALUE); 
    	
    	String uniqueID = mPrefs.getString(
    			getGeofenceFieldKey(id,GeofenceUtils.KEY_UNIQUE_ID),
    			GeofenceUtils.INVALID_STRING_VALUE); 
    	
        double lat = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        double lng = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        float radius = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        long expirationDuration = mPrefs.getLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                GeofenceUtils.INVALID_LONG_VALUE);

        int transitionType = mPrefs.getInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                GeofenceUtils.INVALID_INT_VALUE);

        // validate returned values
        if (validateGeofenceValues(lat,lng,radius,expirationDuration,transitionType)) {
            // Return a true Geofence object
            return new SurveyGeofence(uniqueID, surveyKey, id, lat, lng, 
            		radius, expirationDuration, transitionType);
        // Otherwise, return null.
        } else {
            return null;
        }
    }
	
    public boolean surveyExists(String uniqueID){
    	for (String geofenceStoreKey: geofenceStoreKeys){
        	String id = mPrefs.getString(
        			getGeofenceFieldKey(geofenceStoreKey,GeofenceUtils.KEY_UNIQUE_ID),
        			GeofenceUtils.INVALID_STRING_VALUE);
        	if(id.equals(uniqueID)){
        		return true;
        	}
    	}
    	return false;
    }

    // Remove a flattened geofence object from storage by removing all of its keys
    public void clearGeofence(String id) {
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_UNIQUE_ID));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_SURVEY_KEY));
        editor.commit();
        removeSimpleGeofence(id);
    }
    
    //remove all geofences from storage
    public void clearAllGeofences(){
    	Log.i("SurveyGeofenceStore", "Clearing all Geofences from storage");
    	ArrayList<String> l = new ArrayList<String>(geofenceStoreKeys);
    	for(String s: l){
    		clearGeofence(s);
    	}
    }
    
    /*
     * Get unique survey keys for a set of geofence IDs
     */
    public ArrayList<String> getUniqueSurveyKeys(String[] geofenceIds){
    	String[] ids = new HashSet<String>(Arrays.asList(geofenceIds)).toArray(new String[0]);
    	ArrayList<String> surveyKeys = new ArrayList<String>();
    	for(String id: ids){
    		SurveyGeofence sg = getGeofence(id);
    		if(sg!=null){
    			surveyKeys.add(sg.getSurveyKey());
    		}
    	}
    	return surveyKeys;
    }
	
}
