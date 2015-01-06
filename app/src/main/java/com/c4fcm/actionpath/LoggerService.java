package com.c4fcm.actionpath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class LoggerService extends IntentService implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	Location mCurrentLocation;
	LocationClient mLocationClient;
	
	Stack<ArrayList<String>> queuedLocationLogs;
	Stack<ArrayList<String>> queuedActionLogs;
	
	String storagePath = "/Android/data/action_path";
	String storageFile = "geodata.txt"; 

	public LoggerService(){
		super("LoggerService");
	}

	@Override
	public void onCreate(){
		super.onCreate();
        mLocationClient = new LocationClient(this, this, this);
        queuedLocationLogs = new Stack<ArrayList<String>>();
        queuedActionLogs = new Stack<ArrayList<String>>();
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		Bundle extras = intent.getExtras();
		String logType = intent.getStringExtra("logType");
    	Log.i("LoggerService", logType);
		if(logType.equals("location")){
			String transitionType = intent.getStringExtra("transitionType");
			String[] geofenceIds = extras.getStringArray("ids");
			for(String id: geofenceIds){
				queueLocation(transitionType, id);
			}
			mLocationClient.connect();
		}else if(logType.equals("action")){
			String action = intent.getStringExtra("action");
			String data = intent.getStringExtra("data");
	    	Log.i("LoggerAction", action);
			queueAction(action, data);
		}else if(logType.equals("actionLocation")){
			String action = intent.getStringExtra("action");
			String data = intent.getStringExtra("data");
			queueLocation(action, data);
			mLocationClient.connect();
		}
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		//TODO: determine if we need to do this or not

		if (result.hasResolution()) {
			// if there's a way to resolve the result
		} else {
			// otherwise consider showing an error
		}	
	}


	@Override
	public void onConnected(Bundle connectionHint) {
		// when connected, log queued locations
		logQueuedLocations();
	}

	@Override
	public void onDisconnected() {	
	}

    public void queueLocation(String action, String id){
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	ArrayList<String> a = new ArrayList<String>();
    	a.add(0, now.toString());
    	a.add(1,action);
    	a.add(2,id);
		Log.i("QUEUEING LOCATION", action);
    	queuedLocationLogs.push(a);
    }
    
    public void queueAction(String action, String data){
    	Timestamp now = new Timestamp(System.currentTimeMillis());
    	ArrayList<String> a = new ArrayList<String>();
    	a.add(0,now.toString());
    	a.add(1,action);
    	a.add(2,data);
    	Log.i("QUEUEING ACTION", action);
    	queuedActionLogs.push(a);
		logQueuedActions();//TODO: FIX THIS
    }
	
	public void logQueuedLocations(){
		this.mCurrentLocation = this.mLocationClient.getLastLocation();
		String longitude = String.valueOf(this.mCurrentLocation.getLongitude());
		String latitude = String.valueOf(this.mCurrentLocation.getLatitude());
		Iterator<ArrayList<String>> it = queuedLocationLogs.iterator();
		while(it.hasNext())
		{
			ArrayList<String> locationLog = it.next();
			logCurrentLocation(locationLog.get(0), locationLog.get(1), locationLog.get(2), latitude, longitude);
		}
		queuedLocationLogs.clear(); // TODO: could be a garbage collection issue
	}
	
	//LOG ACTIONS TO A FILE
	//QUESTION: Which actions need location data?
	public void logQueuedActions(){
		Iterator<ArrayList<String>> it = queuedActionLogs.iterator();
		try{
			String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
			File dir = new File(root + storagePath);    
			Log.i("LogQueuedAction",root);
			if(dir.mkdirs() || dir.isDirectory()){
				FileWriter write = new FileWriter(root + storagePath + File.separator + storageFile, true);
				while(it.hasNext())
				{
					ArrayList<String> action = it.next();
					String line = action.get(0) + "," + action.get(1) + "," + action.get(2) + "\n";
					Log.i("LogQueuedAction",line);
					write.append(line);
				}
				write.flush();
				write.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/// LOG CURRENT LOCATION TO A FILE
	public void logCurrentLocation(String timestamp, String action, String data, String latitude, String longitude){
		try{
			String root = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
			File dir = new File(root + storagePath);    
			Log.i("LogCurrentLocation",root);
			if(dir.mkdirs() || dir.isDirectory()){

				FileWriter write = new FileWriter(root + storagePath + File.separator + storageFile, true);
				String line = timestamp + "," + action + "," + data + 
						"," + latitude + "," + longitude+"\n";
				Log.i("LogCurrentLocation",line);
				write.append(line);
				write.flush();
				write.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}


}
