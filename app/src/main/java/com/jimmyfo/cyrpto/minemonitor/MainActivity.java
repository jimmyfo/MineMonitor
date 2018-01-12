package com.jimmyfo.cyrpto.minemonitor;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import com.jimmyfo.cyrpto.minemonitor.base.AppSettings;
import com.jimmyfo.cyrpto.minemonitor.base.Enums;

import java.util.Date;

import static com.jimmyfo.cyrpto.minemonitor.base.Formatting.dateFormatLocal;
import static com.jimmyfo.cyrpto.minemonitor.base.Formatting.decimalFormat;

public class MainActivity extends AppCompatActivity
        implements UserPreferencesFragment.OnFragmentInteractionListener {

    View navBarView;

    private static final int MY_PERMISSIONS_INTERNET = 1;
    private static final int MY_PERMISSIONS_ACCESS_NETWORK_STATE = 2;

    AppSettings appSettings = new AppSettings();

    DataCollection dataCollection;

    long reenableRefresh;

    LineChart hashrateChart;
    LineChart paymentsChart;

    private Context appContext;

    Handler baseNotificationCheckHandler = new Handler();
    Runnable baseNotificationCheckRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                //Catch your exception
                paramThrowable.printStackTrace();
                Log.e("MainActivity", paramThrowable.getStackTrace().toString());

                //ErrorLogging.Log(getApplication().getApplicationContext(), "ScheduledServiceActivity", paramThrowable.getMessage(), paramThrowable.getStackTrace());

                // Temp
                Toast.makeText(appContext, paramThrowable.getMessage(), Toast.LENGTH_LONG).show();
                ((TextView)findViewById(R.id.refreshStatusText)).setText(paramThrowable.getMessage() + "\r\n" + paramThrowable.getStackTrace().toString());

                // Without System.exit() this will not work.
                System.exit(0);
                finish();
            }
        });

        RequestPermissions(android.Manifest.permission.INTERNET, MY_PERMISSIONS_INTERNET);
        RequestPermissions(android.Manifest.permission.ACCESS_NETWORK_STATE, MY_PERMISSIONS_ACCESS_NETWORK_STATE);

        decimalFormat.setMinimumFractionDigits( 12 );

        hashrateChart = (LineChart)findViewById(R.id.hashrateChart);
        paymentsChart = (LineChart)findViewById(R.id.paymentsChart);

        hashrateChart.setNoDataTextColor(R.color.aeonDarkBlue);
        paymentsChart.setNoDataTextColor(R.color.aeonDarkBlue);

        appContext = getApplicationContext();

        // Set action bar
        LayoutInflater inflater = (LayoutInflater) getSupportActionBar().getThemedContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        navBarView = inflater.inflate(R.layout.action_bar, null);
        navBarView.findViewById(R.id.settingsImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = UserPreferencesFragment.newInstance(appSettings.getUserStatsURL(), appSettings.getSiteStatsURL(), appSettings.getUserAddress(),
                        appSettings.getNotifyFound(), appSettings.getNotifyFoundInterval(), appSettings.getNotifyMatured(), appSettings.getNotifyMaturedInterval(),
                        appSettings.getNotifyPayment(), appSettings.getNotifyPaymentInterval());
                newFragment.show(ft, "dialog");
            }
        });

        /* Show the custom action bar view and hide the normal Home icon and title */
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setIcon(R.drawable.ic_ab_som);
        actionBar.setCustomView(navBarView);
        actionBar.setDisplayShowCustomEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.aeonDarkBlue));
        }

        // Restore preferences
        appSettings.Load(getApplicationContext());

        // Handle button refresh
        ((Button)findViewById(R.id.refreshButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    RefreshData(true);
                } catch (Exception ex){
                    Log.e("Refresh", ex.getStackTrace().toString());

                    Toast.makeText(appContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                    ((TextView)findViewById(R.id.refreshStatusText)).setText(ex.getMessage() + "\r\n" + ex.getStackTrace().toString());
                }
            }
        });

        (findViewById(R.id.DevDonationLayoutAEON)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyDevDonation(getString(R.string.DevDonationAEON));
            }
        });
        (findViewById(R.id.DevDonationLayoutXMR)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyDevDonation(getString(R.string.DevDonationXMR));
            }
        });

        // Show the warning
        if (!appSettings.getShowedWarning()){
            new AlertDialog.Builder(this)
                    //.setTitle("Note")
                    .setMessage(getString(R.string.WarningDialog))
                    .setPositiveButton("Good point!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences settings = getSharedPreferences(Enums.PreferencesFileName.toString(), 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean(AppSettings.ShowedWarningName, true);

                            appSettings.setShowedWarning(false);

                            editor.commit();
                        }
                    }).show();
        }

        // Create notification channel by checking every five minutes. This only runs when app has been opened - use alarm manager if not.
//        baseNotificationCheckRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                Log.i("MainActivity", "Runnable");
//                try {
//                    RefreshData(false);
//                } catch (Exception ex) {
//                    Log.e(getClass().getSimpleName(), ex.getMessage());
//                } finally {
//                    baseNotificationCheckHandler.postDelayed(this, Integer.parseInt(Enums.BaseNotificationInterval.toString()));
//                }
//            }
//        };
//
//        baseNotificationCheckHandler.postDelayed(baseNotificationCheckRunnable, Integer.parseInt(Enums.BaseNotificationInterval.toString()));

        // Set up data collection class
        dataCollection = new DataCollection(getApplicationContext(), findViewById(R.id.rootView), hashrateChart, paymentsChart);

        // Technically, we should only start the alarm when onPause/onStop/onDestroy, and stop it onResume.
        // This is because we already have a handler running to update the UI.
        dataCollection.StartAlarm();
    }

    private void RefreshData(final boolean calledManually){
        dataCollection.UpdateWebStats(appSettings.getSiteStatsURL(), appSettings.getUserStatsURL(), appSettings.getUserAddress(), calledManually);

        // Clear old network data
        ((TextView)findViewById(R.id.networkHashRateText)).setText("N/A");
        ((TextView)findViewById(R.id.networkFoundText)).setText("N/A");
        ((TextView)findViewById(R.id.difficultyText)).setText("N/A");
        ((TextView)findViewById(R.id.heightText)).setText("N/A");
        ((TextView)findViewById(R.id.lastRewardText)).setText("N/A");
        ((TextView)findViewById(R.id.lastHashText)).setText("N/A");

        // Clear old pool data
        ((TextView)findViewById(R.id.poolHashRateText)).setText("N/A");
        ((TextView)findViewById(R.id.poolFoundText)).setText("N/A");
        ((TextView)findViewById(R.id.minersText)).setText("N/A");
        ((TextView)findViewById(R.id.poolFeeText)).setText("N/A");
        ((TextView)findViewById(R.id.blockEveryText)).setText("N/A");
        ((TextView)findViewById(R.id.currentDifficultyText)).setText("N/A");
        ((TextView)findViewById(R.id.hashesSubmittedText)).setText("N/A");

        // Clear network charts
        // TODO

        // Clear old user data
        ((LinearLayout)findViewById(R.id.paymentsLinearLayout)).removeAllViews();

        ((TextView)findViewById(R.id.hashesText)).setText("N/A");
        ((TextView)findViewById(R.id.lastShareText)).setText("N/A");
        ((TextView)findViewById(R.id.balanceText)).setText("N/A");
        ((TextView)findViewById(R.id.paidText)).setText("N/A");
        ((TextView)findViewById(R.id.hashrateText)).setText("N/A");

        hashrateChart.clear();
        paymentsChart.clear();

        // Write status to screen
        ((TextView)findViewById(R.id.refreshStatusText)).setText("Refreshing...");
        ((TextView)findViewById(R.id.lastRefreshedText)).setText("Refreshing...");

        // Write last updated time to screen
        Date date = new Date();
        String timestamp = "Last Refreshed: " + dateFormatLocal.format(date);
        ((TextView)findViewById(R.id.lastRefreshedText)).setText(timestamp);

        // Disable button for period of time
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                int secondsRemaining = (int)((reenableRefresh - new Date().getTime()) / 1000);

                try {

                    if (secondsRemaining > 0){
                        ((Button)findViewById(R.id.refreshButton)).setText("Wait to refresh (" + secondsRemaining + ")...");
                    }
                    else{
                        ((Button)findViewById(R.id.refreshButton)).setText("Refresh");
                        ((Button)findViewById(R.id.refreshButton)).setEnabled(true);
                    }
                } catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.getMessage());
                } finally {
                    if (secondsRemaining > 0) {
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        };

        if (calledManually) {
            reenableRefresh = new Date().getTime() + 60000;
        }
        else{
            reenableRefresh = new Date().getTime() + 5000;
        }
        handler.postDelayed(runnable, 1000);

        int secondsRemaining = (int)((reenableRefresh - new Date().getTime()) / 1000);

        ((Button)findViewById(R.id.refreshButton)).setText("Wait to refresh (" + secondsRemaining + ")...");
        ((Button)findViewById(R.id.refreshButton)).setEnabled(false);
    }

    private void RequestPermissions(String androidPermission, int userDefinedPermision) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, androidPermission);

        if (ContextCompat.checkSelfPermission(this, androidPermission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, androidPermission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{androidPermission},
                        userDefinedPermision);


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{androidPermission},
                        userDefinedPermision);

                // userDefinedPermision is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_INTERNET:
            case MY_PERMISSIONS_ACCESS_NETWORK_STATE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted

                } else {

                    // permission denied
                    finish();
                }
                return;
            }
        }

    @Override
    public void onFragmentInteraction(String userStatsURL, String siteStatsURL, String userAddress,
                                      boolean notifyFound, String notifyFoundInterval, boolean notifyMatured, String notifyMaturedInterval,
                                      boolean notifyPayment, String notifyPaymentInterval) {
        SharedPreferences settings = getSharedPreferences(Enums.PreferencesFileName.toString(), 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(AppSettings.UserStatsURLName, userStatsURL);
        editor.putString(AppSettings.SiteStatsURLName, siteStatsURL);
        editor.putString(AppSettings.UserAddressName, userAddress);

        editor.putBoolean(AppSettings.NotifyFoundName, notifyFound);
        editor.putString(AppSettings.NotifyFoundIntervalName, notifyFoundInterval);
        editor.putBoolean(AppSettings.NotifyMaturedName, notifyMatured);
        editor.putString(AppSettings.NotifyMaturedIntervalName, notifyMaturedInterval);
        editor.putBoolean(AppSettings.NotifyPaymentName, notifyPayment);
        editor.putString(AppSettings.NotifyPaymentIntervalName, notifyPaymentInterval);

        editor.commit();

        // Refresh items reliant on settings
        appSettings.Load(getApplicationContext());
        dataCollection.LoadSettings();
    }

    private void CopyDevDonation(String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("DevDonationText", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Address Copied...", Toast.LENGTH_SHORT).show();
    }
}
