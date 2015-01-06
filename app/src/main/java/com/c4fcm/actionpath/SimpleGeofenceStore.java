/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.c4fcm.actionpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Storage for geofence values, implemented in SharedPreferences.
 * For a production app, use a content provider that's synced to the
 * web or loads geofence data based on current location.
 */
public class SimpleGeofenceStore {

    // The SharedPreferences object in which geofences are stored
    protected final SharedPreferences mPrefs;

    // The name of the resulting SharedPreferences
    protected static final String SHARED_PREFERENCE_NAME = "ActionPathPreferences";
                    //MainActivity.class.getSimpleName();
    
    protected ArrayList<String> geofenceStoreKeys;

    // Create the SharedPreferences storage with private access only
    public SimpleGeofenceStore(Context context) {
    	
    	
    	mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        /*mPrefs =
                context.getSharedPreferences(
                        SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);*/
    	
    	//TODO: store keys in a sharedPreference
        geofenceStoreKeys = retrieveGeofenceStoreKeys();
    }
    
    protected void commitGeofenceStoreKeys(){
    	JSONArray geofenceStoreKeysJSON = new JSONArray(geofenceStoreKeys);
        Editor editor = mPrefs.edit();

        // Write the Geofence values to SharedPreferences
        editor.putString("GEOFENCE_STORE_KEYS",geofenceStoreKeysJSON.toString());
        editor.commit();
    }
    
    protected ArrayList<String> retrieveGeofenceStoreKeys(){
        JSONArray geofenceStoreKeysJSON;
        ArrayList<String> gfsk = new ArrayList<String>();
    	try {
			geofenceStoreKeysJSON = new JSONArray(mPrefs.getString("GEOFENCE_STORE_KEYS", GeofenceUtils.INVALID_STRING_VALUE));
    		//Log.i("GeofenceStoreKeysLength",Integer.toString(geofenceStoreKeysJSON.length()));
			for(int i = 0; i < geofenceStoreKeysJSON.length(); i++){
	    		gfsk.add(geofenceStoreKeysJSON.getString(i));
	    	}
		} catch (JSONException e) {
			geofenceStoreKeysJSON = new JSONArray();
		}
    	return gfsk;
    }
    
    /**
     * Returns a stored geofence by its id, or returns {@code null}
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     * {@link SimpleGeofence}
     */
    public SimpleGeofence getGeofence(String id) {

        /*
         * Get the latitude for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
         * if it doesn't exist
         */
        double lat = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the longitude for the geofence identified by id, or
         * -999 if it doesn't exist
         */
        double lng = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the radius for the geofence identified by id, or GeofenceUtils.INVALID_VALUE
         * if it doesn't exist
         */
        float radius = mPrefs.getFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                GeofenceUtils.INVALID_FLOAT_VALUE);

        /*
         * Get the expiration duration for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        long expirationDuration = mPrefs.getLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                GeofenceUtils.INVALID_LONG_VALUE);

        /*
         * Get the transition type for the geofence identified by
         * id, or GeofenceUtils.INVALID_VALUE if it doesn't exist
         */
        int transitionType = mPrefs.getInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                GeofenceUtils.INVALID_INT_VALUE);

        // validate values from shared preferences
        if (validateGeofenceValues(lat,lng,radius,expirationDuration,transitionType)) {
        	
            // Return a true Geofence object
            return new SimpleGeofence(id, lat, lng, radius, expirationDuration, transitionType);

        // Otherwise, return null.
        } else {
            return null;
        }
    }
    
    
    public boolean validateGeofenceValues(double lat, double lng, float radius, long expirationDuration, int transitionType){
        // If none of the values is incorrect, return the object
        if (
            lat != GeofenceUtils.INVALID_DOUBLE_VALUE &&
            lng != GeofenceUtils.INVALID_DOUBLE_VALUE &&
            radius != GeofenceUtils.INVALID_FLOAT_VALUE &&
            expirationDuration != GeofenceUtils.INVALID_LONG_VALUE &&
            transitionType != GeofenceUtils.INVALID_INT_VALUE) {

            // Return a true Geofence object
            return true;

        // Otherwise, return null.
        } else {
            return false;
        }
    }
    
    /**
     *  Push a geofence into storage
     * @param geofence The {@link SimpleGeofence} containing the
     * values you want to save in SharedPreferences
     */
    public void pushGeofence(SimpleGeofence geofence){
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
    }
    
    /**
     * Get a list of geofence keys
     */
    public ArrayList<String> getGeofenceStoreKeys(){
    	return geofenceStoreKeys;
    }
    
    /**
     * Save a geofence.

     * @param geofence The {@link SimpleGeofence} containing the
     * values you want to save in SharedPreferences
     */
    public void setGeofence(String id, SimpleGeofence geofence) {

    	commitBasicGeofenceValues(id, geofence);
        if(!geofenceStoreKeys.contains(id)){
        	geofenceStoreKeys.add(id);
        }
    }
    
    public void commitBasicGeofenceValues(String id, SimpleGeofence geofence){
        /*
         * Get a SharedPreferences editor instance. Among other
         * things, SharedPreferences ensures that updates are atomic
         * and non-concurrent
         */
        Editor editor = mPrefs.edit();

        // Write the Geofence values to SharedPreferences
        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE),
                (float) geofence.getLatitude());

        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE),
                (float) geofence.getLongitude());

        editor.putFloat(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS),
                geofence.getRadius());

        editor.putLong(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION),
                geofence.getExpirationDuration());

        editor.putInt(
                getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE),
                geofence.getTransitionType());

        // Commit the changes
        editor.commit();
    }

    public void clearGeofence(String id) {
    	removeSimpleGeofence(id);
    }
    
    public void removeSimpleGeofence(String id){
        // Remove a flattened geofence object from storage by removing all of its keys
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, GeofenceUtils.KEY_TRANSITION_TYPE));
        editor.commit();
        geofenceStoreKeys.remove(id);
    }

    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, GeofenceUtils.KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    protected String getGeofenceFieldKey(String id, String fieldName) {

        return
                GeofenceUtils.KEY_PREFIX +
                id +
                "_" +
                fieldName;
    }

}
