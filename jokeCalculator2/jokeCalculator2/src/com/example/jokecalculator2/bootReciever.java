package com.example.jokecalculator2;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/* Author: Jon
   11/15/2014
 */

// class designed to start the app on boot
public class bootReciever extends BroadcastReceiver {

	

	@Override
	public void onReceive(Context context, Intent intent) {
		// un hide the app on boot so the phone can find the app to start
		unHideApp(context);
        // start the up the app on boot
		Startup(context);
        // hide the app right after the app starts
		hideApp(context);

	}

	private void Startup(Context context) {
        // just for test to let the developer(me) know the boot reciever has started
		Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
        // intent to start the app
		Intent startApp = new Intent(context, MainActivity.class);
		startApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startApp);
        // intent to take the phone user back to the home screen so they never know the app was started.
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(startMain);

	}
    // method used to hide the app
	private void hideApp(Context context) {
        // hide the app with out killing the appp
		PackageManager p = context.getPackageManager();
		ComponentName componentName = new ComponentName(context,
				com.example.jokecalculator2.MainActivity.class);
		p.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}
    // method used to un-hide the app
	public void unHideApp(Context context) {
        // un-hide the app with out killing the app
		PackageManager p = context.getPackageManager();
		ComponentName componentName = new ComponentName(context,
				com.example.jokecalculator2.MainActivity.class);
		p.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

	}

}
