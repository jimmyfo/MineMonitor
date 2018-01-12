package com.jimmyfo.cyrpto.minemonitor;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jimmyfo.cyrpto.minemonitor.base.AppSettings;
import com.jimmyfo.cyrpto.minemonitor.base.CustomJSONObjectRequest;
import com.jimmyfo.cyrpto.minemonitor.base.Enums;
import com.jimmyfo.cyrpto.minemonitor.base.Formatting;
import com.jimmyfo.cyrpto.minemonitor.data.MinerResponse;
import com.jimmyfo.cyrpto.minemonitor.data.SiteResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.jimmyfo.cyrpto.minemonitor.base.Formatting.dateFormatLocal;
import static com.jimmyfo.cyrpto.minemonitor.base.Formatting.withSuffix;

public class DataCollection {

    private Context mContext;
    private View mRootView;
    LayoutInflater appInflater;

    RequestQueue requestQueue;
    static ObjectMapper mapper = new ObjectMapper();

    SiteResponse mSiteResponse;
    MinerResponse mMinerResponse;

    LineChart mHashrateChart;
    LineChart mPaymentsChart;

    AppSettings appSettings = new AppSettings();

    public DataCollection(Context context, View rootView, LineChart hashrateChart, LineChart paymentsChart){

        mContext = context;
        mRootView = rootView;
        requestQueue = Volley.newRequestQueue(mContext);

        appInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        mHashrateChart = hashrateChart;
        mPaymentsChart = paymentsChart;

        LoadSettings();
    }

    public void LoadSettings(){
        appSettings.Load(mContext);
    }

    public void StartAlarm(){
        // Set timer
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getService(mContext, 0, i, 0);

        // Must be 60 seconds or above
        //mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Integer.parseInt(Enums.BaseNotificationInterval.toString()), Integer.parseInt(Enums.BaseNotificationInterval.toString()), pi);

        // Must be 5 seconds or above
        //mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Integer.parseInt(Enums.BaseNotificationInterval.toString()), pi);
        mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5000, pi);
    }

    public void UpdateWebStats(final boolean calledManually){
        UpdateSiteStats(appSettings.getSiteStatsURL(), appSettings.getUserStatsURL(), appSettings.getUserAddress(), calledManually);
    }

    public void UpdateWebStats(String siteStatsURL, final String userStatsURL, final String userAddress, final boolean calledManually){
        UpdateSiteStats(siteStatsURL, userStatsURL, userAddress, calledManually);
    }

    private void UpdateSiteStats(String siteStatsURL, final String userStatsURL, final String userAddress, final boolean calledManually){

        final CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(siteStatsURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mSiteResponse = mapper.readValue(response.toString(), SiteResponse.class);

                        if (mSiteResponse != null &&
                                mSiteResponse.getConfig() != null) {

                            // TODO what is value if pool has never been found?
                            Date lastNetworkFoundDate = new Date(mSiteResponse.getNetwork().getTimeStamp() * 1000);
                            Date lastPoolFoundDate = new Date(mSiteResponse.getPool().getLastBlockFound());

                            long networkHashRate = mSiteResponse.getNetwork().getDifficulty() / mSiteResponse.getConfig().getCoinDifficultyTarget();

                            long blockEvery = mSiteResponse.getNetwork().getDifficulty() / mSiteResponse.getPool().getHashRate();
                            blockEvery *= 1000;
                            int seconds = (int) (blockEvery / 1000) % 60;
                            int minutes = (int) ((blockEvery / (1000 * 60)) % 60);
                            //int hours   = (int) ((blockEvery / (1000*60*60)) % 24); // If getting days
                            float hours = ((blockEvery / (1000 * 60 * 60f)));

                            long timeSince = new Date().getTime() - lastPoolFoundDate.getTime();
                            float currentDiff = timeSince / BigDecimal.valueOf(blockEvery).floatValue();

                            if (mRootView != null) {
                                // Set network data
                                ((TextView) mRootView.findViewById(R.id.networkHashRateText)).setText(withSuffix(networkHashRate));
                                ((TextView) mRootView.findViewById(R.id.networkFoundText)).setText(Formatting.approxTimeDifference(lastNetworkFoundDate, new Date()));
                                ((TextView) mRootView.findViewById(R.id.difficultyText)).setText(NumberFormat.getNumberInstance().format(mSiteResponse.getNetwork().getDifficulty()));
                                ((TextView) mRootView.findViewById(R.id.heightText)).setText(NumberFormat.getNumberInstance().format(mSiteResponse.getNetwork().getHeight()));
                                ((TextView) mRootView.findViewById(R.id.lastRewardText)).setText(BigDecimal.valueOf(mSiteResponse.getNetwork().getReward() / 1000000000000d).setScale(6, BigDecimal.ROUND_HALF_DOWN).toString());

                                String dynamicUrl = "https://chainradar.com/aeon/block/" + mSiteResponse.getNetwork().getHash();
                                String linkedText = String.format("<a href=\"%s\">" + mSiteResponse.getNetwork().getHash().substring(0, 10) + "...</a> ", dynamicUrl);

                                ((TextView) mRootView.findViewById(R.id.lastHashText)).setText(Html.fromHtml(linkedText));
                                ((TextView) mRootView.findViewById(R.id.lastHashText)).setMovementMethod(LinkMovementMethod.getInstance());

                                // Set pool data
                                ((TextView) mRootView.findViewById(R.id.poolHashRateText)).setText(withSuffix(mSiteResponse.getPool().getHashRate()));
                                ((TextView) mRootView.findViewById(R.id.poolFoundText)).setText(Formatting.approxTimeDifference(lastPoolFoundDate, new Date()));
                                ((TextView) mRootView.findViewById(R.id.minersText)).setText(NumberFormat.getNumberInstance().format(mSiteResponse.getPool().getMiners()));
                                ((TextView) mRootView.findViewById(R.id.poolFeeText)).setText(Float.toString(mSiteResponse.getConfig().getFee()) + "%");
                                if (hours > 0) {
                                    ((TextView) mRootView.findViewById(R.id.blockEveryText)).setText(BigDecimal.valueOf(hours).setScale(1, BigDecimal.ROUND_HALF_DOWN).toString() + " Hours");
                                } else {
                                    ((TextView) mRootView.findViewById(R.id.blockEveryText)).setText(BigDecimal.valueOf(minutes).setScale(1, BigDecimal.ROUND_HALF_DOWN).toString() + " Minutes");
                                }
                                ((TextView) mRootView.findViewById(R.id.currentDifficultyText)).setText(BigDecimal.valueOf(currentDiff * 100f).setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%");
                                if (currentDiff > 1) {
                                    ((TextView) mRootView.findViewById(R.id.currentDifficultyText)).setTextColor(ContextCompat.getColor(mContext, R.color.red));
                                } else {
                                    ((TextView) mRootView.findViewById(R.id.currentDifficultyText)).setTextColor(ContextCompat.getColor(mContext, R.color.green));
                                }
                                ((TextView) mRootView.findViewById(R.id.hashesSubmittedText)).setText(NumberFormat.getNumberInstance().format(mSiteResponse.getPool().getRoundHashes()));

                            }
                        } else {
                            if (mRootView != null) {
                                ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Empty response received.");
                            }
                        }
                    } catch (IOException ex){
                        Log.e("LOG", ex.toString());

                        if (mRootView != null) {
                            ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                        }
                    } catch (Exception ex){
                        Log.e("LOG", ex.toString());

                        if (mRootView != null) {
                            ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                        }
                    } finally {
                        UpdateUserStats(userStatsURL, userAddress, calledManually);

                        UpdatePoolFound(mSiteResponse, calledManually);

                        UpdatePoolMatured(mSiteResponse, calledManually);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Log.e("LOG", ex.toString());

                if (mRootView != null) {
                    ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                }
            }
        });

        // Site data is really slow so modify
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void UpdatePoolFound(SiteResponse siteResponse, boolean calledManually) {
        // Track our changing pool stats
        if (siteResponse != null &&
                siteResponse.getPool() != null) {

            // Check if a new block has been found
            if (siteResponse.getPool().getLastBlockFound() != appSettings.getTrackLastPoolFound()) {

                Log.d("Found", new Date(siteResponse.getPool().getLastBlockFound()) + " found.");

                // If done by the service in the background, then show a notification.
                if (!calledManually) {

                    // Check if a new block has been found
                    if (siteResponse.getPool().getLastBlockFound() > appSettings.getTrackLastPoolFound()) {

                        Log.d("Found", new Date(siteResponse.getPool().getLastBlockFound()) + " found automatically.");

                        appSettings.setPendingFoundNotification(true);

                        Log.d("Found", "Showing found at " + new Date(appSettings.getNextFoundNotification()));
                    }
                } else {
                    // Reset our to-be-shown as manual refresh will clear it
                    appSettings.setPendingFoundNotification(false);
                }

                // Update our last found block
                appSettings.setTrackLastPoolFound(siteResponse.getPool().getLastBlockFound());

                SharedPreferences settings = mContext.getSharedPreferences(Enums.PreferencesFileName.toString(), 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putLong(AppSettings.TrackPoolLastFoundName, appSettings.getTrackLastPoolFound());
                editor.putBoolean(AppSettings.PendingFoundNotificationName, appSettings.getPendingFoundNotification());
                editor.putLong(AppSettings.NextFoundNotificationName, appSettings.getNextFoundNotification());

                editor.commit();
            }
        }

        ShowNotification(appSettings.getPendingFoundNotification(), appSettings.getNotifyFound(), appSettings.getNextFoundNotification(), "found",
                1, mContext.getString(R.string.NewBlockTitle), mContext.getString(R.string.NewBlockText));
    }

    private void UpdatePoolMatured(SiteResponse siteResponse, boolean calledManually){
        // if refresh called at all, then we reset our "track" times to whatever we found in that call.
        //      any auto refresh will be compared against that last refresh data, whether manual or not
        // if refresh called automatically, we can show notifications
        //      we track whether or not to show a change at the next defined interval if we find an item that changes automatically, but it isn't time to show yet

        // Track our changing pool stats
        if (siteResponse != null &&
                siteResponse.getPool() != null) {

            // Track our latest blocks to see if any have matured
            if (siteResponse.getPool().getBlocks().length > 0) {

                long lowestHeightWaitingOn = Long.MAX_VALUE;

                // Blocks are in order of recency, most recent at top
                for (int i = 0; i < Math.min(siteResponse.getPool().getBlocks().length, siteResponse.getConfig().getDepth()); i++) {
                    // First is most recent
                    String[] blockData = siteResponse.getPool().getBlocks()[0].split(":");
                    String hash = blockData[0];
                    long found = Long.parseLong(blockData[1]) * 1000;
                    long difficulty = Long.parseLong(blockData[2]);
                    // Rest are unknown

                    long height = Long.parseLong(siteResponse.getPool().getBlocks()[1]);

                    long toGo = siteResponse.getConfig().getDepth() - (siteResponse.getNetwork().getHeight() - height);
                    if (toGo > 0){
                        lowestHeightWaitingOn = Math.min(height, lowestHeightWaitingOn);
                    } else{
                        break;
                    }
                }

                //current track
                //  M       M 	ok, no notification
                //  1 	    M 	ok, no notification
                //  M 	    1	ok, notification - only block waiting on has matured
                //  1  	    2 	ok - should not happen
                //  2    	1 	ok, notification - older/lower height has matured, waiting on other.
                if (lowestHeightWaitingOn != appSettings.getTrackLowestHeightWaitingOn()){

                    // If our waiting to validate block is max and used to not be max, new has matured.
                    // If our waiting to validate block is higher than old waiting to mature, new has matured.
                    // Can't get here without != to each other.
                    if (lowestHeightWaitingOn == Long.MAX_VALUE ||  // Heights are different. Block list not waiting on anything, but we were before
                            lowestHeightWaitingOn > appSettings.getTrackLowestHeightWaitingOn()){    // Heights are different. Newest low height is greater than previous low height.

                        Log.d("Matured", appSettings.getTrackLowestHeightWaitingOn() + " > " + lowestHeightWaitingOn + " matured");

                        // If done by the service in the background, then show a notification
                        if (!calledManually) {

                            Log.d("Matured", appSettings.getTrackLowestHeightWaitingOn() + " > " + lowestHeightWaitingOn + " matured automatically");

                            appSettings.setPendingMaturedNotification(true);

                            Log.d("Matured", "Showing matured at " + new Date(appSettings.getNextMaturedNotification()));
                        } else {
                            // Reset our to-be-shown as manual refresh will clear it
                            appSettings.setPendingMaturedNotification(false);
                        }
                    }

                    // Write to settings
                    appSettings.setTrackLowestHeightWaitingOn(lowestHeightWaitingOn);

                    SharedPreferences settings = mContext.getSharedPreferences(Enums.PreferencesFileName.toString(), 0);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putLong(AppSettings.TrackLowestHeightWaitingOnName, appSettings.getTrackLowestHeightWaitingOn());
                    editor.putBoolean(AppSettings.PendingMaturedNotificationName, appSettings.getPendingMaturedNotification());
                    editor.putLong(AppSettings.NextMaturedNotificationName, appSettings.getNextMaturedNotification());

                    editor.commit();
                }
            }
        }

        ShowNotification(appSettings.getPendingMaturedNotification(), appSettings.getNotifyMatured(), appSettings.getNextMaturedNotification(), "matured",
                2, mContext.getString(R.string.MaturedBlockTitle), mContext.getString(R.string.MaturedBlockText));
    }

    private void UpdatePaymentSent(MinerResponse minerResponse, boolean calledManually){
        // Track our changing miner stats
        if (minerResponse != null &&
                minerResponse.getPayments() != null &&
                minerResponse.getPayments().length > 1) {

            // Most recent payment at the top
            long paymentTime = (Long.parseLong(mMinerResponse.getPayments()[1]) * 1000);

            // Check if a new payment
            if (paymentTime > appSettings.getTrackLastPaymentMade()) {

                Log.d("Payment", appSettings.getTrackLastPaymentMade() + " > " + paymentTime + " paid");

                // If done by the service in the background, then show a notification.
                if (!calledManually) {

                    Log.d("Payment", appSettings.getTrackLastPaymentMade() + " > " + paymentTime + " paid automatically");

                    appSettings.setPendingPaymentNotification(true);

                    Log.d("Payment", "Showing payment at " + new Date(appSettings.getNextPaymentNotification()));
                } else {
                    // Reset our to-be-shown as manual refresh will clear it
                    appSettings.setPendingPaymentNotification(false);
                }

                // Update our last payment
                appSettings.setTrackLastPaymentMade(paymentTime);

                SharedPreferences settings = mContext.getSharedPreferences(Enums.PreferencesFileName.toString(), 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putLong(AppSettings.TrackLastPaymentMadeName, appSettings.getTrackLastPaymentMade());
                editor.putBoolean(AppSettings.PendingPaymentNotificationName, appSettings.getPendingPaymentNotification());
                editor.putLong(AppSettings.NextPaymentNotificationName, appSettings.getNextPaymentNotification());

                editor.commit();
            }
        }

        ShowNotification(appSettings.getPendingPaymentNotification(), appSettings.getNotifyPayment(),appSettings.getNextPaymentNotification(), "paid",
                3, mContext.getString(R.string.PaidTitle), mContext.getString(R.string.PaidText));
    }

    private void ShowNotification(boolean pendingNotification, boolean notify, long nextNotificationTime, String type,
                                  int notificationChannel, String title, String text){

        // Log for debugging
        Log.d("Notification", "Checking " + type + ": Pending: " + pendingNotification + " Notify: " + notify + " NextTime: " + new Date(nextNotificationTime));

        // If we have allow notifications
        // And we have a pending notification
        // And we have waited the specified duration since the last notification (NOTE: it isn't every x period, it's x time BETWEEN notifications)
        //                                                                          Otherwise, we'd want to set the next notification timestamp every time we check, not only every time we show.
        if (notify &&
                pendingNotification &&
                new Date().getTime() >= nextNotificationTime){

            Log.d("Notification", "In show notification");

            // Update the app settings
            //      Set that we no longer have a pending notfication
            //      Set the next time we can use a notification of this type to be now + interval
            try {
                SharedPreferences settings = mContext.getSharedPreferences(Enums.PreferencesFileName.toString(), 0);
                SharedPreferences.Editor editor = settings.edit();

                switch (type){
                    case "found":
                        Log.d("Notification", "Found was " + new Date(appSettings.getNextFoundNotification()));
                        appSettings.setPendingFoundNotification(false);
                        appSettings.setNextFoundNotification(DataCollection.GetNextNotificationTime(new Date(), appSettings.getNotifyFoundInterval()));

                        editor.putBoolean(AppSettings.PendingFoundNotificationName, appSettings.getPendingFoundNotification());
                        editor.putLong(AppSettings.NextFoundNotificationName, appSettings.getNextFoundNotification());
                        Log.d("Notification", "Found is " + new Date(appSettings.getNextFoundNotification()));
                        break;
                    case "matured":
                        Log.d("Notification", "Matured was " + new Date(appSettings.getNextMaturedNotification()));
                        appSettings.setPendingMaturedNotification(false);
                        appSettings.setNextMaturedNotification(DataCollection.GetNextNotificationTime(new Date(), appSettings.getNotifyMaturedInterval()));

                        editor.putBoolean(AppSettings.PendingMaturedNotificationName, appSettings.getPendingMaturedNotification());
                        editor.putLong(AppSettings.NextMaturedNotificationName, appSettings.getNextMaturedNotification());
                        Log.d("Notification", "Matured is " + new Date(appSettings.getNextMaturedNotification()));
                        break;
                    case "paid":
                        Log.d("Notification", "Paid was " + new Date(appSettings.getNextPaymentNotification()));
                        appSettings.setPendingPaymentNotification(false);
                        appSettings.setNextPaymentNotification(DataCollection.GetNextNotificationTime(new Date(), appSettings.getNotifyPaymentInterval()));

                        editor.putBoolean(AppSettings.PendingPaymentNotificationName, appSettings.getPendingPaymentNotification());
                        editor.putLong(AppSettings.NextPaymentNotificationName, appSettings.getNextPaymentNotification());
                        Log.d("Notification", "Paid is " + new Date(appSettings.getNextPaymentNotification()));
                        break;
                }

                editor.commit();

                CreateNotificationChannel(notificationChannel, title, text);
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.getMessage());
            }
        }
    }

    private void UpdateUserStats(String userStatsURL, String userAddress, final boolean calledManually){
        String url = userStatsURL + userAddress;

        final CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mMinerResponse = mapper.readValue(response.toString(), MinerResponse.class);

                        if (mMinerResponse != null &&
                                mMinerResponse.getMinerStats() != null){

                            if (mMinerResponse.getError() != null){
                                if (mRootView != null) {
                                    ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + mMinerResponse.getError());
                                }
                            } else {
                                if (mRootView != null) {
                                    ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Response received.");

                                    // Write data to screen
                                    // TODO get divisibility from SiteConfig.java
                                    ((TextView) mRootView.findViewById(R.id.hashesText)).setText(NumberFormat.getInstance().format(mMinerResponse.getMinerStats().getHashes()));
                                    ((TextView) mRootView.findViewById(R.id.lastShareText)).setText(dateFormatLocal.format(new Date(mMinerResponse.getMinerStats().getLastShare() * 1000)));
                                    ((TextView) mRootView.findViewById(R.id.balanceText)).setText(BigDecimal.valueOf(mMinerResponse.getMinerStats().getBalance() / 1000000000000d).setScale(12, BigDecimal.ROUND_HALF_DOWN).toString());
                                    ((TextView) mRootView.findViewById(R.id.paidText)).setText(BigDecimal.valueOf(mMinerResponse.getMinerStats().getPaid() / 1000000000000d).setScale(12, BigDecimal.ROUND_HALF_DOWN).toString());
                                    ((TextView) mRootView.findViewById(R.id.hashrateText)).setText(mMinerResponse.getMinerStats().getHashRate());

                                    // Payments
                                    for (int i = 0; i < mMinerResponse.getPayments().length; i++) {
                                        View paymentView = appInflater.inflate(R.layout.payment, null);

                                        String[] paymentInfo = mMinerResponse.getPayments()[i].split(":");
                                        i++;
                                        String paymentTime = mMinerResponse.getPayments()[i];

                                        ((TextView) paymentView.findViewById(R.id.timeSentTextView)).setText(dateFormatLocal.format(new Date(Long.parseLong(paymentTime) * 1000)));

                                        String dynamicUrl = "https://chainradar.com/aeon/transaction/" + paymentInfo[0];
                                        String linkedText = String.format("<a href=\"%s\">" + paymentInfo[0].substring(0, 10) + "...</a> ", dynamicUrl);

                                        ((TextView) paymentView.findViewById(R.id.transactionHashTextView)).setText(Html.fromHtml(linkedText));
                                        ((TextView) paymentView.findViewById(R.id.transactionHashTextView)).setMovementMethod(LinkMovementMethod.getInstance());

                                        ((TextView) paymentView.findViewById(R.id.amountTextView)).setText(BigDecimal.valueOf(Long.parseLong(paymentInfo[1]) / 1000000000000d).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString());
                                        ((TextView) paymentView.findViewById(R.id.mixinTextView)).setText(paymentInfo[3]);

                                        ((LinearLayout) mRootView.findViewById(R.id.paymentsLinearLayout)).addView(paymentView);
                                    }

                                    // Update charts
                                    if (mHashrateChart != null) {
                                        if (mMinerResponse.getCharts() == null ||
                                                mMinerResponse.getCharts().getHashRate() == null ||
                                                mMinerResponse.getCharts().getHashRate().length == 0) {

                                            mHashrateChart.clear();
                                        } else {
                                            List<Entry> hashRateEntries = new ArrayList<Entry>();

                                            // Step up from 0
                                            int startIndex = 0;
                                            if (mMinerResponse.getCharts().getHashRate().length < 2) {
                                                startIndex = 1;
                                                hashRateEntries.add(new Entry(0, 0, 0));
                                            }

                                            for (int i = 0; i < mMinerResponse.getCharts().getHashRate().length; i++) {
                                                // turn your data into Entry objects
                                                hashRateEntries.add(new Entry(i + startIndex, Float.parseFloat(mMinerResponse.getCharts().getHashRate()[i][1])));
                                            }

                                            LineDataSet hashRateDataSet = new LineDataSet(hashRateEntries, "Hash Rate");
                                            hashRateDataSet.setDrawValues(false);

                                            LineData hashRateLineData = new LineData(hashRateDataSet);
                                            mHashrateChart.setData(hashRateLineData);
                                            mHashrateChart.setDescription(null);
                                            mHashrateChart.getXAxis().setDrawLabels(false);
                                            mHashrateChart.getAxisRight().setDrawLabels(false);
                                            mHashrateChart.setNoDataTextColor(R.color.aeonDarkBlue);
                                            mHashrateChart.invalidate(); // refresh
                                        }
                                    }

                                    if (mPaymentsChart != null) {
                                        if (mMinerResponse.getCharts() == null ||
                                                mMinerResponse.getCharts().getPayments() == null ||
                                                mMinerResponse.getCharts().getPayments().length == 0) {

                                            mPaymentsChart.clear();
                                        } else {

                                            List<Entry> paymentEntries = new ArrayList<Entry>();

                                            // Step up from 0
                                            int startIndex = 0;
                                            if (mMinerResponse.getCharts().getPayments().length < 2) {
                                                startIndex = 1;
                                                paymentEntries.add(new Entry(0, 0, 0));
                                            }

                                            for (int i = 0; i < mMinerResponse.getCharts().getPayments().length; i++) {
                                                // turn your data into Entry objects
                                                paymentEntries.add(new Entry(i + startIndex, Float.parseFloat(BigDecimal.valueOf(Long.parseLong(mMinerResponse.getCharts().getPayments()[i][1]) / 1000000000000d).setScale(4).toString())));
                                            }

                                            LineDataSet paymentDataSet = new LineDataSet(paymentEntries, "Payments");
                                            paymentDataSet.setDrawValues(false);

                                            LineData paymentLineData = new LineData(paymentDataSet);
                                            mPaymentsChart.setData(paymentLineData);
                                            mPaymentsChart.setDescription(null);
                                            mPaymentsChart.getXAxis().setDrawLabels(false);
                                            mPaymentsChart.getAxisRight().setDrawLabels(false);
                                            mPaymentsChart.setNoDataTextColor(R.color.aeonDarkBlue);
                                            mPaymentsChart.invalidate(); // refresh
                                        }
                                    }
                                }
                            }
                        } else{
                            if (mRootView != null) {
                                ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Empty response received.");
                            }
                        }
                    } catch (IOException ex){
                        Log.e("LOG", ex.toString());

                        if (mRootView != null) {
                            ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                        }
                    } catch (Exception ex){
                        Log.e("LOG", ex.toString());

                        if (mRootView != null) {
                            ((TextView) mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                        }
                    } finally {
                        UpdatePaymentSent(mMinerResponse, calledManually);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Log.e("LOG", ex.toString());

                if (mRootView != null) {
                    ((TextView)mRootView.findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public static long GetNextNotificationTime(Date lastNotification, String interval){
        Calendar c = Calendar.getInstance();
        c.setTime(lastNotification);

        switch (interval){
            case "5 min":
                c.add(Calendar.MINUTE, 5);
                lastNotification = c.getTime();

                return lastNotification.getTime();
            case "10 min":
                c.add(Calendar.MINUTE, 10);
                lastNotification = c.getTime();

                return lastNotification.getTime();
            case "15 min":
                c.add(Calendar.MINUTE, 15);
                lastNotification = c.getTime();

                return lastNotification.getTime();
            case "30 min":
                c.add(Calendar.MINUTE, 30);
                lastNotification = c.getTime();

                return lastNotification.getTime();
            case "1 hour":
                c.add(Calendar.HOUR, 1);
                lastNotification = c.getTime();

                return lastNotification.getTime();
            case "1 day":
            default:
                c.add(Calendar.DATE, 1);
                lastNotification = c.getTime();

                return lastNotification.getTime();
        }
    }

    private void CreateNotificationChannel(int notificationID, String title, String text){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setAutoCancel(true);

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
