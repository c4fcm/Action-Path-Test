package com.c4fcm.actionpath;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.location.Geofence;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

public class SynchronizeDataService extends IntentService {

	private static final String DEBUG_TAG = "SynchronizeDataService";
	String geofenceDataURL = "https://docs.google.com/spreadsheets/d/10FdA4gcHDOhjuoUq43LPOVROFgx0SpcMk51ntbTcQjs/export?format=csv";
    private SimpleGeofenceStore mPrefs;

	
	public SynchronizeDataService(){
		super("SynchronizeDataService");
		mPrefs = null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(mPrefs==null){
			mPrefs = new SimpleGeofenceStore(this.getApplicationContext());
		}
		//Bundle extras = intent.getExtras();
		Log.i("SynchronizeData","Intent Received");
		fetchGeofenceData();

	}
	/**
	 * 
	 * @param row is an array of string values purporting to be a geofence
	 * @return true if the row is valid, false if it is not
	 */
	protected boolean validateGeofenceRow(String[] row){
		//verify the right number of columns
		//then for each column, verify that they're not null
		// or empty string
		if(row.length > 3 &&
		   row[0]!=null && row[0].length() > 0 &&
		   row[1]!=null && row[1].length() > 0 &&
		   row[2]!=null && row[2].length() > 0 &&
		   row[3]!=null && row[2].length() > 0 &&
		   row[4]!=null && row[3].length() > 0){
			try{
				//verify that requisite rows parse into doubles or floats
				Double.valueOf(row[2]);
				Double.valueOf(row[3]);
				Float.valueOf(row[4]);
				return true;
			}catch(NumberFormatException n){
				Log.i("SynchronizeDataService","Invalid Geofence Data in Google Spreadsheet");
				return false;
			}
			
		}else{
			return false;
		}
	}
	
	protected void fetchGeofenceData(){		
		Log.i("fetchGeofenceData", "Fetching Data");
	    ConnectivityManager connMgr = (ConnectivityManager) 
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) {
	            try {
		        	String geofenceCSVData = downloadUrl(geofenceDataURL);
		        	
		        	//Log.i("SynchronizeData",geofenceCSVData);

		        	//explicitly skip the first line
		            CSVReader reader = new CSVReader(new StringReader(geofenceCSVData),',','\"',1);
		            List<String[]> myEntries = reader.readAll();
		            reader.close();
		            
		            //create a JSONArray object to send to MainActivity
		            JSONArray geofenceData = new JSONArray();
		            //Add any geofences included in the data
		            for (String[] s : myEntries) {
		            	//Log.i("Line",Arrays.toString(s));
		            	if(validateGeofenceRow(s)){
		            	      	  JSONObject row = new JSONObject();
		            	      	 // Log.i("SurveyKey", s[0]);
		            	      	  try{
		            	      		  row.put("uniqueID", s[0]);
			            	      	  row.put("surveyKey",s[1]);
			            	      	  row.put("lat", s[2]);
			            	      	  row.put("long",s[3]);
			            	      	  row.put("radius",s[4]);
		            	      	  }catch (Exception e) {
		            	      	      Log.i("JSONCreate",e.getStackTrace().toString());
		            	          }
		            	          geofenceData.put(row);
		            	}
		            }
		        	//Broadcast JSONArray object to MainActivity
		            if(geofenceData.length() > 0){
			    		Intent RTReturn = new Intent(GeofenceUtils.UPDATE_GEOFENCES);
			    		RTReturn.putExtra("json", geofenceData.toString());
			    		//Log.i("fetchGeofenceData","SendingBroadcast");
			    		LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);
		            }else{
		            	//TODO: Handle case where there are no valid rows
		            }
		        	
	            } catch (IOException e) {
	                Log.i("SynchronizeData","Unable to retrieve web page. URL may be invalid.");
	            }
	        } else {
	            Log.i("SynchronizeData", "Connection Not Available");
	        }
	        Log.i("SYNCmPrefs",Integer.toString(mPrefs.getGeofenceStoreKeys().size()));
	}
	
	private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is);
	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");      

	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    int b;
	    while((b = reader.read())!=-1){
	    	buffer.write(b);
	    }
	    return buffer.toString("UTF-8");
	}
}