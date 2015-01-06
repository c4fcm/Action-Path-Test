package com.c4fcm.actionpath;

import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class SampleAlertDialogFragment extends DialogFragment{
		public String location;
		
		public String setLocation(String location){
			this.location=location;
			return location;
		}
		
	   @Override	   
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(this.location)
	               .setPositiveButton("Yess", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // If Yes, Do This
	                   }
	               })
	               .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // If No, Do This
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
}