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
		
		//Log the fact that this was clicked
		Intent loggerServiceIntent = new Intent(view.getContext(),LoggerService.class);
        loggerServiceIntent.putExtra("logType", "actionLocation");
     	loggerServiceIntent.putExtra("action", "thanksDismissed");
        loggerServiceIntent.putExtra("data", ""); //eventually include the survey data
    	startService(loggerServiceIntent);
		
		Intent homeIntent= new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory(Intent.CATEGORY_HOME);
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(homeIntent);
		finish();
	}
	
}
