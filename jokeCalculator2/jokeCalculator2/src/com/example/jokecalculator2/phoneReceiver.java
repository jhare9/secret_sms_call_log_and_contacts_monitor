package com.example.jokecalculator2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

public class phoneReceiver extends BroadcastReceiver {
    /*
        Author: Jon
        date: 11/16/2014
        Receiver to start the custom receiver for incoming text messages.
     */
	@Override
	public void onReceive(Context context, Intent intent) {

        // get the bundle and the data package within the bundle
		Bundle bundle = intent.getExtras();

        // create an Intent object
		Intent custom = new Intent();
        //set the action of the custom receiver
		custom.setAction("customText");
        // send the bundle of data to the custom receiver.
		custom.putExtras(bundle);
        // start the custom broadcast receiver
		context.sendBroadcast(custom);
        // terminate this broadcast;
		abortBroadcast();

	}

}
