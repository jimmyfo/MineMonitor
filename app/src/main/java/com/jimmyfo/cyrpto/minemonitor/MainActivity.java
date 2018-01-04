package com.jimmyfo.cyrpto.minemonitor;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.jimmyfo.cyrpto.minemonitor.base.Enums;
import com.jimmyfo.cyrpto.minemonitor.data.MinerResponse;
import com.jimmyfo.cyrpto.minemonitor.data.SiteResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jimmyfo.cyrpto.minemonitor.base.Formatting.dateFormatLocal;
import static com.jimmyfo.cyrpto.minemonitor.base.Formatting.decimalFormat;

public class MainActivity extends AppCompatActivity
        implements UserPreferencesFragment.OnFragmentInteractionListener {

    View navBarView;

    RequestQueue requestQueue;
    static ObjectMapper mapper = new ObjectMapper();

    private static final int MY_PERMISSIONS_INTERNET = 1;
    private static final int MY_PERMISSIONS_ACCESS_NETWORK_STATE = 2;

    public static final String PREFS_NAME = "MyPrefsFile";

    private String userAddress = "";
    private String userStatsURL = "";
    private String siteStatsURL = "";

    private boolean showedWarning = false;

    LayoutInflater appInflater;

    LineChart hashrateChart;
    LineChart paymentsChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestPermissions(android.Manifest.permission.INTERNET, MY_PERMISSIONS_INTERNET);
        RequestPermissions(android.Manifest.permission.ACCESS_NETWORK_STATE, MY_PERMISSIONS_ACCESS_NETWORK_STATE);

        requestQueue = Volley.newRequestQueue(this);

        decimalFormat.setMinimumFractionDigits( 12 );

        appInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        hashrateChart = (LineChart)findViewById(R.id.hashrateChart);
        paymentsChart = (LineChart)findViewById(R.id.paymentsChart);

        hashrateChart.setNoDataTextColor(R.color.aeonDarkBlue);
        paymentsChart.setNoDataTextColor(R.color.aeonDarkBlue);

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
                DialogFragment newFragment = UserPreferencesFragment.newInstance(userStatsURL, siteStatsURL, userAddress);
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
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userAddress = settings.getString("userAddress", "");
        userStatsURL = settings.getString("userStatsURL", Enums.UserWebAddress.toString());
        siteStatsURL = settings.getString("siteStatsURL", Enums.SiteWebAddress.toString());
        showedWarning = settings.getBoolean("showedWarning", false);

        // Handle button refresh
        ((Button)findViewById(R.id.refreshButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateWebStats();

                // Clear old data
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
                        try {
                            ((Button)findViewById(R.id.refreshButton)).setText("Refresh");
                            ((Button)findViewById(R.id.refreshButton)).setEnabled(true);
                        } catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.getMessage());
                        } finally {
                  //          handler.postDelayed(this, 60000);
                        }
                    }
                };

                // TODO make one minute
                handler.postDelayed(runnable, 1000);

                //runnable.run();

                ((Button)findViewById(R.id.refreshButton)).setText("Wait one minute to refresh...");
                ((Button)findViewById(R.id.refreshButton)).setEnabled(false);
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
        if (!showedWarning){
            new AlertDialog.Builder(this)
                    //.setTitle("Note")
                    .setMessage(getString(R.string.WarningDialog))
                    .setPositiveButton("Good point!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("showedWarning", true);

                            showedWarning = true;

                            editor.commit();
                        }
                    }).show();
        }
    }

    private void UpdateWebStats(){
        //UpdateSiteStats();
        UpdateUserStats();
    }

    private void UpdateSiteStats(){
        final String url = siteStatsURL;
//        final Context context = getApplicationContext();
//
//        // Volley shits the bed with site stats, so custom object
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    JSONObject jsonObj = getJSONObjectFromURL(context, url);
//
//                    SiteResponse siteResponse = mapper.readValue(jsonObj.toString(), SiteResponse.class);
//
//                    if (siteResponse != null &&
//                            siteResponse.getConfig() != null){
//
//                        //((TextView) findViewById(R.id.refreshStatusText)).setText("Response received.");
//                        UpdateUserStats();
//                    } else{
//                        ((TextView)findViewById(R.id.refreshStatusText)).setText("Empty response received.");
//                    }
//
//                } catch (JSONException ex) {
//                    Log.e("JSON", ex.getMessage());
//
//                    // TODO can't access views from other thread
//                    //((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
//                } catch (IOException ex) {
//                    Log.e("JSON", ex.getMessage());
//
//                    // TODO can't access views from other thread
//                    //((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
//                } catch (Exception ex){
//                    Log.e("JSON", ex.getMessage());
//
//                    // TODO can't access views from other thread
//                    //((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
//                }
//            }
//        }.start();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        SiteResponse siteResponse = mapper.readValue(response.toString(), SiteResponse.class);

                        if (siteResponse != null &&
                                siteResponse.getConfig() != null){

                            //((TextView) findViewById(R.id.refreshStatusText)).setText("Response received.");
                             UpdateUserStats();
                        } else{
                            ((TextView)findViewById(R.id.refreshStatusText)).setText("Empty response received.");
                        }
                    } catch (IOException ex){
                        Log.e("LOG", ex.toString());

                        ((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                    } catch (Exception ex){
                        Log.e("LOG", ex.toString());

                        ((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Log.e("LOG", ex.toString());

                ((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
            }
        });

        // Site data is really slow so modify
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private void UpdateUserStats(){
        String url = userStatsURL + userAddress;
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        MinerResponse minerResponse = mapper.readValue(response.toString(), MinerResponse.class);

                        if (minerResponse != null &&
                                minerResponse.getMinerStats() != null){

                            if (minerResponse.getError() != null){
                                ((TextView) findViewById(R.id.refreshStatusText)).setText("Error: " + minerResponse.getError());
                            } else {
                                ((TextView) findViewById(R.id.refreshStatusText)).setText("Response received.");

                                // Write data to screen
                                // TODO get divisibility from SiteConfig.java
                                ((TextView)findViewById(R.id.hashesText)).setText(NumberFormat.getInstance().format(minerResponse.getMinerStats().getHashes()));
                                ((TextView)findViewById(R.id.lastShareText)).setText(dateFormatLocal.format(new Date(minerResponse.getMinerStats().getLastShare() * 1000)));
                                ((TextView)findViewById(R.id.balanceText)).setText(BigDecimal.valueOf(minerResponse.getMinerStats().getBalance() / 1000000000000d).setScale(12).toString());
                                ((TextView)findViewById(R.id.paidText)).setText(BigDecimal.valueOf(minerResponse.getMinerStats().getPaid() / 1000000000000d).setScale(12).toString());
                                ((TextView)findViewById(R.id.hashrateText)).setText(minerResponse.getMinerStats().getHashRate());

                                // Payments
                                for (int i = 0; i < minerResponse.getPayments().length; i++)
                                {
                                    View paymentView = appInflater.inflate(R.layout.payment, null);

                                    String[] paymentInfo = minerResponse.getPayments()[i].split(":");
                                    i++;
                                    String paymentTime = minerResponse.getPayments()[i];

                                    ((TextView)paymentView.findViewById(R.id.timeSentTextView)).setText(dateFormatLocal.format(new Date(Long.parseLong(paymentTime) * 1000)));

                                    String dynamicUrl = "https://chainradar.com/aeon/transaction/" + paymentInfo[0];
                                    String linkedText = String.format("<a href=\"%s\">" + paymentInfo[0].substring(0, 10) + "...</a> ", dynamicUrl);

                                    ((TextView)paymentView.findViewById(R.id.transactionHashTextView)).setText(Html.fromHtml(linkedText));
                                    ((TextView)paymentView.findViewById(R.id.transactionHashTextView)).setMovementMethod(LinkMovementMethod.getInstance());

                                    ((TextView)paymentView.findViewById(R.id.amountTextView)).setText(BigDecimal.valueOf(Long.parseLong(paymentInfo[1]) / 1000000000000d).setScale(4).toString());
                                    ((TextView)paymentView.findViewById(R.id.mixinTextView)).setText(paymentInfo[3]);

                                    ((LinearLayout)findViewById(R.id.paymentsLinearLayout)).addView(paymentView);
                                }

                                // Update charts
                                List<Entry> hashRateEntries = new ArrayList<Entry>();

                                for (int i = 0; i < minerResponse.getCharts().getHashRate().length; i++){
                                    // turn your data into Entry objects
                                    hashRateEntries.add(new Entry(i, Float.parseFloat(minerResponse.getCharts().getHashRate()[i][1])));
                                }

                                LineDataSet hashRateDataSet = new LineDataSet(hashRateEntries, "Hash Rate");
                                hashRateDataSet.setDrawValues(false);

                                LineData hashRateLineData = new LineData(hashRateDataSet);
                                hashrateChart.setData(hashRateLineData);
                                hashrateChart.setDescription(null);
                                hashrateChart.getXAxis().setDrawLabels(false);
                                hashrateChart.getAxisRight().setDrawLabels(false);
                                hashrateChart.setNoDataTextColor(R.color.aeonDarkBlue);
                                hashrateChart.invalidate(); // refresh

                                List<Entry> paymentEntries = new ArrayList<Entry>();

                                for (int i = 0; i < minerResponse.getCharts().getPayments().length; i++){
                                    // turn your data into Entry objects
                                    paymentEntries.add(new Entry(i, Float.parseFloat(BigDecimal.valueOf(Long.parseLong(minerResponse.getCharts().getPayments()[i][1]) / 1000000000000d).setScale(4).toString())));
                                }

                                LineDataSet paymentDataSet = new LineDataSet(paymentEntries, "Payments");
                                paymentDataSet.setDrawValues(false);

                                LineData paymentLineData = new LineData(paymentDataSet);
                                paymentsChart.setData(paymentLineData);
                                paymentsChart.setDescription(null);
                                paymentsChart.getXAxis().setDrawLabels(false);
                                paymentsChart.getAxisRight().setDrawLabels(false);
                                paymentsChart.setNoDataTextColor(R.color.aeonDarkBlue);
                                paymentsChart.invalidate(); // refresh
                            }
                        } else{
                            ((TextView)findViewById(R.id.refreshStatusText)).setText("Empty response received.");
                        }
                    } catch (IOException ex){
                        Log.e("LOG", ex.toString());

                        ((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                    } catch (Exception ex){
                        Log.e("LOG", ex.toString());

                        ((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                Log.e("LOG", ex.toString());

                ((TextView)findViewById(R.id.refreshStatusText)).setText("Error: " + ex.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public static JSONObject getJSONObjectFromURL(final Context context, String urlString) throws IOException, JSONException {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlConnection = null;
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(2500 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                String UTF8 = "UTF-8";
                int BUFFER_SIZE = 8192;

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), UTF8), BUFFER_SIZE);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                String jsonString = sb.toString();

                urlConnection.disconnect();
                urlConnection = null;

                br = null;
                sb = null;

                if (jsonString != null &&
                        jsonString.length() > 0) {

                    return new JSONObject(jsonString);
                }
            } catch (SocketTimeoutException ex) {
                Log.e("LOG", ex.toString());

                //ErrorLogging.Log(context, "Step10a", ex.getMessage(), ex.getStackTrace());
                // TODO
            } catch (IOException ex) {
                Log.e("LOG", ex.toString());

                //ErrorLogging.Log(context, "Step10b", ex.getMessage(), ex.getStackTrace());
                // TODO
            }
        }

        return null;
    }

    private static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
    public void onFragmentInteraction(String userURL, String siteURL, String address) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("userStatsURL", userURL);
        editor.putString("siteStatsURL", siteURL);
        editor.putString("userAddress", address);

        userStatsURL = userURL;
        siteStatsURL = siteURL;
        userAddress = address;

        editor.commit();
    }

    private void CopyDevDonation(String text){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("DevDonationText", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Address Copied...", Toast.LENGTH_SHORT).show();
    }
}
