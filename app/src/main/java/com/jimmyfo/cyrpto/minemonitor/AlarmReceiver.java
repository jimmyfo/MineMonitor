package com.jimmyfo.cyrpto.minemonitor;

import android.app.IntentService;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class AlarmReceiver extends IntentService {
    public AlarmReceiver(){
        super("AlarmReceiver");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                //Catch your exception
                paramThrowable.printStackTrace();
                Log.e("AlarmReceiver", paramThrowable.getStackTrace().toString());

                //ErrorLogging.Log(getApplication().getApplicationContext(), "AlarmReceiver", paramThrowable.getMessage(), paramThrowable.getStackTrace());

                // Without System.exit() this will not work.
                System.exit(0);
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Alarm", "Received");

        DataCollection dataCollection = new DataCollection(getApplicationContext(), null, null, null);

        try {
            dataCollection.UpdateWebStats(false);
        } catch (Exception ex){
            Log.e("AlarmReceiver", ex.getMessage());
        } finally {
            dataCollection.StartAlarm();
        }
    }
}