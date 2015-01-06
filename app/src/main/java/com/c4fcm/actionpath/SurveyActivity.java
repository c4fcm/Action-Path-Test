package com.c4fcm.actionpath;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.widget.ScrollView;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import org.json.*;

public class SurveyActivity extends FragmentActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Build Survey Layout based on current question, responses and images
		String surveyKey = this.getIntent().getExtras().getString("surveyKey");
		
		Intent loggerServiceIntent = new Intent(this,LoggerService.class);
        loggerServiceIntent.putExtra("logType", "actionLocation");
     	loggerServiceIntent.putExtra("action", "NotificationClick");
        loggerServiceIntent.putExtra("data", surveyKey);
    	startService(loggerServiceIntent);
		
		
		// ---------------- TEST DATA -------------- //
		
		// Instantiate text for questions and their responses
		String surveyKey1 = "\"Chuckie Harris Park\"";
		String question1 = "\"What\'s the best option for connecting the new Chuckie Harris Park to Broadway?\"";
		String responses1 = "\"Street paint at intersection\", \"Gate over Cross St\", \"Grass corridor by Senior Ctr\"";
		String images1 = "\"option1_streetpaint\", \"option2_gate\", \"option3_grass\"";
		
		String surveyKey2 = "\"MIT Media Lab\"";
		String question2 = "\"What addition to the 3rd Floor Cafe would you most like to see?\"";
		String responses2 = "\"Popcorn Machine\", \"Pizza Oven\", \"Kegerator\"";
		String images2 = ""; // empty array for testing		
		
		// ----------------------------------------- //
		
		try {	
			JSONObject surveys = new JSONObject(
					"{" + surveyKey1 + ": {"
						+ "\"question\": " + question1 + ","
						+ "\"responses\": [" + responses1 + "],"
						+ "\"images\": [" + images1 + "]},"
					+ surveyKey2 + ": {"
						+ "\"question\": " + question2 + ","
						+ "\"responses\": [" + responses2 + "],"
						+ "\"images\": [" + images2 + "]}"
					+ "}");
			
			
	

			JSONObject currentSurvey = surveys.getJSONObject(surveyKey);
			String currentQuestion = currentSurvey.getString("question");
			JSONArray responsesJSON = currentSurvey.getJSONArray("responses");
			JSONArray imagesJSON = currentSurvey.getJSONArray("images");
			String[] currentResponses = new String[responsesJSON.length()];
			String[] currentImages = new String[imagesJSON.length()];
		
			for (int i=0; i<responsesJSON.length(); i++) {
				currentResponses[i] = responsesJSON.get(i).toString();
				
				if (imagesJSON.length() == responsesJSON.length()) {
					currentImages[i] = imagesJSON.get(i).toString();
				}
			}
			
			ScrollView surveyLayout = buildSurveyLayout(currentQuestion, currentResponses, currentImages);
			// Make surveyLayout the ContentView
			setContentView(surveyLayout);
		}
		catch (JSONException e) {
			Log.e("SurveyActivity.java","JSONException");
		}
	}
	
	public ScrollView buildSurveyLayout(String question, String responses[], String images[]){
		
		// Get and scale dimensions from dps to pixels based on screen size
		int marginTiny = dpToPx(getResources().getDimension(R.dimen.margin_tiny));
		int marginSmall = dpToPx(getResources().getDimension(R.dimen.margin_small));
		int marginMedium = dpToPx(getResources().getDimension(R.dimen.margin_medium));	
		int imageHeight = dpToPx(200);
		
		// Get reusable colors
		int bgColor = getResources().getColor(R.color.SurveyBackgroundColor);
		int fontColor = getResources().getColor(R.color.SurveyFontColor);
		//int buttonColor = getResources().getColor(R.color.SurveyButtonColor);
		//int buttonFontColor = getResources().getColor(R.color.SurveyButtonFontColor);
		
		// Instantiate ScrollView layout for survey page
		ScrollView surveyLayout = new ScrollView(this);
		surveyLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		surveyLayout.setBackgroundColor(bgColor);
			
		// Create container LinearLayout
		LinearLayout containerLayout = new LinearLayout(this);
		containerLayout.setBackgroundColor(bgColor);
		containerLayout.setOrientation(LinearLayout.VERTICAL);
		containerLayout.setPadding(marginSmall,marginSmall,marginSmall,marginSmall);
		containerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		// Create LinearLayout for question text
		LinearLayout questionLayout = new LinearLayout(this);
		questionLayout.setOrientation(LinearLayout.VERTICAL);
	   	LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        qlp.setMargins(0, marginMedium, 0, marginMedium); // rlp.setMargins(left, top, right, bottom);
        questionLayout.setLayoutParams(qlp);
        
		TextView questionText = new TextView(this);
		questionText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		questionText.setText(question);
		questionText.setTextAppearance(this, android.R.style.TextAppearance_Large);
		questionText.setTextColor(fontColor);
		questionText.setTypeface(Typeface.DEFAULT_BOLD);
				
		questionLayout.addView(questionText);
		containerLayout.addView(questionLayout);
		
		char alphabet = 'A'; // response letters to iterate through
		
		// Create LinearLayouts for responses with images
	    for (int i=0; i<responses.length; i++) {
	    	
	    	LinearLayout responseLayout = new LinearLayout(this);
	    	responseLayout.setOrientation(LinearLayout.VERTICAL);
    	   	LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        rlp.setMargins(0, 0, 0, marginSmall); // rlp.setMargins(left, top, right, bottom);
	        responseLayout.setLayoutParams(rlp);
	      
	        // Create response text
	    	TextView responseText = new TextView(this);
    	   	LinearLayout.LayoutParams rtp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	        rtp.setMargins(0, marginSmall, 0, marginTiny); // rtp.setMargins(left, top, right, bottom);
	        responseText.setLayoutParams(rtp);
	        responseText.setText(alphabet + ". " + responses[i]);
			responseText.setTextAppearance(this, android.R.style.TextAppearance_Large);
			responseText.setTextColor(fontColor);
			responseLayout.addView(responseText);
			
			// Create images to click on for each response if used
			if (images.length != 0){
				ImageButton responseImage = new ImageButton(this);
				responseImage.setId(i);
				responseImage.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, imageHeight));
				responseImage.setAdjustViewBounds(true); // corrects for padding added by scaling
				responseImage.setScaleType(ScaleType.FIT_START);
				responseImage.setImageResource(getResources().getIdentifier(images[i], "drawable", getPackageName()));
				responseImage.setOnClickListener(new View.OnClickListener() {
					@Override // make image clickable
					public void onClick(View arg0) {
						//Log the fact that this was clicked
						Intent loggerServiceIntent = new Intent(arg0.getContext(),LoggerService.class);
				        loggerServiceIntent.putExtra("logType", "actionLocation");
				     	loggerServiceIntent.putExtra("action", "SurveyResponse");
				        loggerServiceIntent.putExtra("data", "response_" + (arg0.getId()+1)); //eventually include the survey data
				    	startService(loggerServiceIntent);
						
						
						//Load the Thanks page
						Intent i = new Intent(arg0.getContext(), SurveyThanksActivity.class);
						startActivity(i); 
						finish();
					}
				});
				responseLayout.addView(responseImage);
			} else {

				// Create plain buttons if no images included
				Button responseButton = new Button(this);
				responseButton.setId(i);
				responseButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				responseButton.setTextColor(fontColor);
				responseButton.setText("vote " + alphabet);
				responseButton.setOnClickListener(new View.OnClickListener() {
					@Override // make button clickable
					public void onClick(View arg0) {
						//Log the fact that this was clicked
						Intent loggerServiceIntent = new Intent(arg0.getContext(),LoggerService.class);
				        loggerServiceIntent.putExtra("logType", "actionLocation");
				     	loggerServiceIntent.putExtra("action", "SurveyResponse");
				        loggerServiceIntent.putExtra("data", "response_" + (arg0.getId()+1)); //eventually include the survey data
				    	startService(loggerServiceIntent);
						
						// Load the Thanks page
						Intent i = new Intent(arg0.getContext(), SurveyThanksActivity.class);
						startActivity(i); 
						finish();
					}
				});
				responseLayout.addView(responseButton);
			}
			
			containerLayout.addView(responseLayout); // add response text and button to layout
			
			alphabet++; // iterate response letters
	    }			
		
		// Add LinearLayouts to ScrollView
		surveyLayout.addView(containerLayout);
		
		return surveyLayout;
	}
	
	private int dpToPx(float dp){
		//return (int)Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
		return (int)(dp*getResources().getDisplayMetrics().density);
	}
	
	public void onResume(){
    	super.onResume();
    	Log.i("SurveyActivity","OnResumeCalled");
    	NotificationManager notificationManager =(NotificationManager) 
    			  getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.cancel(0);
	}
	
	  //creates a PendingIntent
    public PendingIntent getPendingIntent() {
    	Log.v("INTENT","returning an intent for SurveyActivity.class");
    	return PendingIntent.getActivity(this, 0, new Intent(this,
    			SurveyThanksActivity.class), 0);
    }

}
