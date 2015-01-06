package com.c4fcm.actionpath;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.Notification.Builder;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class ReceiveTransitionsIntentService extends IntentService {
	
    /**
     * Sets an identifier for this class' background thread
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
    }

    /**
     * Handles incoming intents
     * @param intent The Intent sent by Location Services. This Intent is provided
     * to Location Services (inside a PendingIntent) when you call addGeofences()
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a local broadcast Intent
        Intent broadcastIntent = new Intent();

        // Give it the category for all intents sent by the Intent Service
        broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

        // First check for errors
        if (LocationClient.hasError(intent)) {

            // Get the error code
            int errorCode = LocationClient.getErrorCode(intent);

            // Get the error message
            String errorMessage = LocationServiceErrorMessages.getErrorString(this, errorCode);

            // Log the error
            Log.e(GeofenceUtils.APPTAG,
                    getString(R.string.geofence_transition_error_detail, errorMessage)
            );

            // Set the action and error message for the broadcast intent
            broadcastIntent.setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
                           .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, errorMessage);

            // Broadcast the error *locally* to other components in this app
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        // If there's no error, get the transition type and create a notification
        } else {

            // Get the type of transition (entry or exit)
            int transition = LocationClient.getGeofenceTransition(intent);

            // Test that a valid transition was reported
            if (
                    (transition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    ||
                    (transition == Geofence.GEOFENCE_TRANSITION_EXIT)
               ) {

                // Post a notification
                List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
                String[] geofenceIds = new String[geofences.size()];
                for (int index = 0; index < geofences.size() ; index++) {
                    geofenceIds[index] = geofences.get(index).getRequestId();
                }
                // log the first transition

                String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,geofenceIds);
                
                String transitionType = getTransitionString(transition);

                //activate the logging system
            	Intent loggerServiceIntent = new Intent(this,LoggerService.class);
            	loggerServiceIntent.putExtra("logType", "location");
            	loggerServiceIntent.putExtra("transitionType", transitionType);
                loggerServiceIntent.putExtra("ids", geofenceIds);
            	startService(loggerServiceIntent);
                         
            	//TODO: retrieve SimpleGeofence objects, work out
            	//      which surveys are relevant, and 
            	
            	//create the notification
                sendNotifications(transitionType, geofenceIds);

                // Log the transition type and a message to adb debug
                Log.d(GeofenceUtils.APPTAG,
                        getString(
                                R.string.geofence_transition_notification_title,
                                transitionType,
                                ids));
                Log.d(GeofenceUtils.APPTAG,
                        getString(R.string.geofence_transition_notification_text));

            // An invalid transition was reported
            } else {
                // Always log as an error
                Log.e(GeofenceUtils.APPTAG,
                        getString(R.string.geofence_transition_invalid_type, transition));
            }
        }
    }
        
    private void sendNotifications(String transitionType, String[] ids){
    	
    	//retrieve the relevant survey keys and print them
    	Context ctx = this.getApplicationContext();
    	SurveyGeofenceStore mPrefs = new SurveyGeofenceStore(ctx);
    	ArrayList<String> surveyKeys = mPrefs.getUniqueSurveyKeys(ids);
    	ArrayList<String> y = mPrefs.getGeofenceStoreKeys();
    	Log.i("NotificationContext", ctx.toString());
    	for (String surveyKey : surveyKeys) {
    		sendNotification(surveyKey);
    	}
    }
    
    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the main Activity.
     * @param transitionType The type of transition that occurred.
     * For now, ActionPath only handles enter transitionTypes
     *
     */
    private void sendNotification(String surveyKey) {

    	Log.d("sendNotification","sending notification build thing in ReceiveTransitionsIntentService");
    	Log.i("sendNotification", surveyKey);
    	
    	//surveyKey="Chuckie Harris Park";
    	    	
    	// create "surveyIntent" to be triggered when user clicks on notification
    	PendingIntent pi = getPendingIntent(surveyKey);
    	
    	// create the notification
    	Builder notificationBuilder = new Notification.Builder(this);
    	notificationBuilder.setContentTitle("Action: " + getString(R.string.active_action))
    	//notificationBuilder.setContentTitle("ActionPath " + transitionType + " " + TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,ids))
    	// Notification title
    	// not sure how to make this appear, or where it does appear
    	//.setContentText("You have " + transitionType + " " + ids.length + "ActionPaths")
    	// you can put subject line.
    	.setSmallIcon(R.drawable.ic_launcher)
    	// Set your notification icon here.
    	
    	 //TODO: ADD THESE BACK IN WHEN NEEDED
    	.addAction(R.drawable.ic_notification, "Go There", pi)
    	.addAction(
    			R.drawable.ic_stat_snooze,
    			"Snooze", pi); // TODO: Make this an actual snooze button*/
    	
    	//notificationBuilder.setContentIntent(pi);

    	
    	// Now create the Big picture notification.
    	Notification notification = new Notification.BigTextStyle(notificationBuilder)
    		.bigText("Fill in this awesome form!").build();
    //	Notification notification = new Notification.BigPictureStyle(notificationBuilder).build();
    	/*.bigPicture(
    			BitmapFactory.decodeResource(getResources(),
    					R.drawable.ic_notification_placeholder)).build();*/
    	// Put the auto cancel notification flag
    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
    	NotificationManager notificationManager = getNotificationManager();
    	notificationManager.notify(0, notification);
    	
    	// TODO: Create a way to clear the notification once it has been clicked

    }
    
    //creates a PendingIntent for bigPicture notifications
    public PendingIntent getPendingIntent(String surveyKey) {
    	Log.v("INTENT","returning an intent for SurveyActivity.class");
    	
    	Intent surveyIntent = new Intent(this, SurveyActivity.class)
    		.putExtra("surveyKey", surveyKey);
	
    	return PendingIntent.getActivity(this, 0, surveyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    public NotificationManager getNotificationManager() {
    	return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    /**

     * Maps geofence transition types to their human-readable equivalents.
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);

            default:
                return getString(R.string.geofence_transition_unknown);
        }
    }
    
  
}
