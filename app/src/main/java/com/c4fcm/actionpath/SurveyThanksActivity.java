package com.c4fcm.actionpath;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class SurveyThanksActivity extends FragmentActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_survey_thanks);
	}
	
	
	public void dismissThanks(View view){
		
		//Log the fact that the user simply dismissed the thanks
		Intent loggerServiceIntent = new Intent(view.getContext(),LoggerService.class);
        loggerServiceIntent.putExtra("logType", "action");
     	loggerServiceIntent.putExtra("action", "ThanksDismissed");
        loggerServiceIntent.putExtra("data", ""); //eventually include the survey data (need this?)
    	startService(loggerServiceIntent);
		
		Intent homeIntent= new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
		finish();
	}
	
	public void unfollowIssue(View view){
		
		//Log the fact that the user unfollowed the issue
		Intent loggerServiceIntent = new Intent(view.getContext(),LoggerService.class);
        loggerServiceIntent.putExtra("logType", "action");
     	loggerServiceIntent.putExtra("action", "UnfollowedIssue");
        loggerServiceIntent.putExtra("data", ""); //eventually include the survey data (need this?)
    	startService(loggerServiceIntent);
		
    	Toast.makeText(this, "Issue has been unfollowed.",
                Toast.LENGTH_SHORT).show();
    	
		Intent homeIntent= new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
		finish();
	}
	
}
