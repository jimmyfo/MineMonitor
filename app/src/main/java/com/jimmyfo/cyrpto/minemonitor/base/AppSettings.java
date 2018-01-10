package com.jimmyfo.cyrpto.minemonitor.base;

import android.content.Context;
import android.content.SharedPreferences;

import com.jimmyfo.cyrpto.minemonitor.DataCollection;

import java.util.Date;

public class AppSettings {
    public static String UserAddressName = "UserAddress";
    public static String UserStatsURLName = "UserStatsURL";
    public static String SiteStatsURLName = "SiteStatsURL";
    public static String NotifyFoundName = "NotifyFound";
    public static String NotifyFoundIntervalName = "NotifyFoundInterval";
    public static String NotifyMaturedName = "NotifyMatured";
    public static String NotifyMaturedIntervalName = "NotifyMaturedInterval";
    public static String NotifyPaymentName = "NotifyPayment";
    public static String NotifyPaymentIntervalName = "NotifyPaymentInterval";
    public static String ShowedWarningName = "ShowedWarning";
    public static String TrackPoolLastFoundName = "TrackPoolLastFound";
    public static String TrackLowestHeightWaitingOnName = "TrackLowestHeightWaitingOn";
    public static String TrackLastPaymentMadeName = "TrackLastPaymentMade";
    public static String PendingFoundNotificationName = "PendingFoundNotification";
    public static String PendingMaturedNotificationName = "PendingMaturedNotification";
    public static String PendingPaymentNotificationName = "PendingPaymentNotification";
    public static String NextFoundNotificationName = "NextFoundNotification";
    public static String NextMaturedNotificationName = "NextMaturedNotification";
    public static String NextPaymentNotificationName = "NextPaymentNotification";

    private String UserAddress;
    private String SiteStatsURL;
    private String UserStatsURL;

    // Last time we did something, loaded from preferences at start, updated as run.
    private long TrackLastPoolFound;
    private long TrackLowestHeightWaitingOn;
    private long TrackLastPaymentMade;
    private boolean NotifyFound;
    private String NotifyFoundInterval;
    private boolean NotifyMatured;
    private String NotifyMaturedInterval;
    private boolean NotifyPayment;
    private String NotifyPaymentInterval;
    private boolean ShowedWarning;

    // Last shown notifications
    private boolean pendingFoundNotification;
    private boolean pendingMaturedNotification;
    private boolean pendingPaymentNotification;

    // Timestamps of when to show a notification if one is pending.
    private long NextFoundNotification;
    private long NextMaturedNotification;
    private long NextPaymentNotification;

    public void setUserAddress(String userAddress) {
        UserAddress = userAddress;
    }

    public void setSiteStatsURL(String siteStatsURL) {
        SiteStatsURL = siteStatsURL;
    }

    public void setUserStatsURL(String userStatsURL) {
        UserStatsURL = userStatsURL;
    }

    public void setTrackLastPoolFound(long trackLastPoolFound) {
        TrackLastPoolFound = trackLastPoolFound;
    }

    public void setTrackLastPaymentMade(long trackLastPaymentMade) {
        TrackLastPaymentMade = trackLastPaymentMade;
    }

    public void setTrackLowestHeightWaitingOn(long trackLowestHeightWaitingOn) {
        TrackLowestHeightWaitingOn = trackLowestHeightWaitingOn;
    }

    public void setNotifyFound(boolean notifyFound) {
        NotifyFound = notifyFound;
    }

    public void setNotifyFoundInterval(String notifyFoundInterval) {
        NotifyFoundInterval = notifyFoundInterval;
    }

    public void setNotifyMatured(boolean notifyMatured) {
        NotifyMatured = notifyMatured;
    }

    public void setNotifyMaturedInterval(String notifyMaturedInterval) {
        NotifyMaturedInterval = notifyMaturedInterval;
    }

    public void setNotifyPayment(boolean notifyPayment) {
        NotifyPayment = notifyPayment;
    }

    public void setNotifyPaymentInterval(String notifyPaymentInterval) {
        NotifyPaymentInterval = notifyPaymentInterval;
    }

    public void setShowedWarning(boolean showedWarning) {
        ShowedWarning = showedWarning;
    }

    public void setPendingFoundNotification(boolean pendingFoundNotification) {
        this.pendingFoundNotification = pendingFoundNotification;
    }

    public void setPendingMaturedNotification(boolean pendingMaturedNotification) {
        this.pendingMaturedNotification = pendingMaturedNotification;
    }

    public void setPendingPaymentNotification(boolean pendingPaymentNotification) {
        this.pendingPaymentNotification = pendingPaymentNotification;
    }

    public void setNextFoundNotification(long nextFoundNotification) {
        NextFoundNotification = nextFoundNotification;
    }

    public void setNextMaturedNotification(long nextMaturedNotification) {
        NextMaturedNotification = nextMaturedNotification;
    }

    public void setNextPaymentNotification(long nextPaymentNotification) {
        NextPaymentNotification = nextPaymentNotification;
    }

    public String getUserAddress() {
        return UserAddress;
    }

    public String getSiteStatsURL() {
        return SiteStatsURL;
    }

    public String getUserStatsURL() {
        return UserStatsURL;
    }

    public long getTrackLastPoolFound() {
        return TrackLastPoolFound;
    }

    public long getTrackLastPaymentMade() {
        return TrackLastPaymentMade;
    }

    public long getTrackLowestHeightWaitingOn() {
        return TrackLowestHeightWaitingOn;
    }

    public boolean getNotifyFound(){
        return NotifyFound;
    }

    public String getNotifyFoundInterval() {
        return NotifyFoundInterval;
    }

    public boolean getNotifyMatured(){
        return NotifyMatured;
    }

    public String getNotifyMaturedInterval() {
        return NotifyMaturedInterval;
    }

    public boolean getNotifyPayment(){
        return NotifyPayment;
    }

    public String getNotifyPaymentInterval() {
        return NotifyPaymentInterval;
    }

    public boolean getShowedWarning(){
        return ShowedWarning;
    }

    public boolean getPendingFoundNotification() {
        return pendingFoundNotification;
    }

    public boolean getPendingMaturedNotification() {
        return pendingMaturedNotification;
    }

    public boolean getPendingPaymentNotification() {
        return pendingPaymentNotification;
    }

    public long getNextFoundNotification() {
        return NextFoundNotification;
    }

    public long getNextMaturedNotification() {
        return NextMaturedNotification;
    }

    public long getNextPaymentNotification() {
        return NextPaymentNotification;
    }

    public void Load(Context context){
        SharedPreferences settings = context.getSharedPreferences(Enums.PreferencesFileName.toString(), 0);

        setUserAddress(settings.getString(AppSettings.UserAddressName, ""));
        setUserStatsURL(settings.getString(AppSettings.UserStatsURLName, Enums.UserWebAddress.toString()));
        setSiteStatsURL(settings.getString(AppSettings.SiteStatsURLName, Enums.SiteWebAddress.toString()));
        setNotifyFound(settings.getBoolean(AppSettings.NotifyFoundName, false));
        setNotifyFoundInterval(settings.getString(AppSettings.NotifyFoundIntervalName, "5 min"));
        setNotifyMatured(settings.getBoolean(AppSettings.NotifyMaturedName, false));
        setNotifyMaturedInterval(settings.getString(AppSettings.NotifyMaturedIntervalName, "5 min"));
        setNotifyPayment(settings.getBoolean(AppSettings.NotifyPaymentName, false));
        setNotifyPaymentInterval(settings.getString(AppSettings.NotifyPaymentIntervalName, "5 min"));
        setShowedWarning(settings.getBoolean(AppSettings.ShowedWarningName, false));

        setTrackLastPoolFound(settings.getLong(AppSettings.TrackPoolLastFoundName, new Date().getTime()));
        setTrackLowestHeightWaitingOn(settings.getLong(AppSettings.TrackLowestHeightWaitingOnName, Long.MAX_VALUE));
        setTrackLastPaymentMade(settings.getLong(AppSettings.TrackLastPaymentMadeName, new Date().getTime()));

        setPendingFoundNotification(settings.getBoolean(AppSettings.PendingFoundNotificationName, false));
        setPendingMaturedNotification(settings.getBoolean(AppSettings.PendingMaturedNotificationName, false));
        setPendingPaymentNotification(settings.getBoolean(AppSettings.PendingPaymentNotificationName, false));

        long initialize = settings.getLong(AppSettings.NextFoundNotificationName, 0);
        if (initialize == 0){
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(AppSettings.NextFoundNotificationName, DataCollection.GetNextNotificationTime(new Date(), getNotifyFoundInterval()));
            editor.commit();
        } else{
            setNextFoundNotification(initialize);
        }

        initialize = settings.getLong(AppSettings.NextMaturedNotificationName, 0);
        if (initialize == 0){
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(AppSettings.NextMaturedNotificationName, DataCollection.GetNextNotificationTime(new Date(), getNotifyMaturedInterval()));
            editor.commit();
        } else{
            setNextMaturedNotification(initialize);
        }

        initialize = settings.getLong(AppSettings.NextPaymentNotificationName, 0);
        if (initialize == 0){
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(AppSettings.NextPaymentNotificationName, DataCollection.GetNextNotificationTime(new Date(), getNotifyPaymentInterval()));
            editor.commit();
        } else{
            setNextPaymentNotification(initialize);
        }
    }
}
