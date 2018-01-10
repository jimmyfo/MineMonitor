package com.jimmyfo.cyrpto.minemonitor.base;

import java.io.Console;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formatting {
    public static final String DATE_FORMAT_STRING = "MM/dd/yyyy HH:mm:ss z";
    public static DateFormat dateFormatLocal = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);
    public static DateFormat dateFormatGmt = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);

    public static DecimalFormat decimalFormat = new DecimalFormat();

    public static String withSuffix(long count) {
        if (count < 1000) {
            return "" + count;
        }

        int exp = (int) (Math.log(count) / Math.log(1000));

        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp-1));
    }

    public static String approxTimeDifference(Date startDate, Date endDate) {
        //1 minute = 60 seconds
        //1 hour = 60 x 60 = 3600
        //1 day = 3600 x 24 = 86400

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

//        System.out.printf(
//                "%d days, %d hours, %d minutes, %d seconds%n",
//                elapsedDays,
//                elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedDays > 0){
            return elapsedDays + " Day" + (elapsedDays == 1 ? "" : "s") + " Ago";
        } else if (elapsedHours > 0){
            return elapsedHours + " Hour" + (elapsedDays == 1 ? "" : "s") + " Ago";
        } else if (elapsedMinutes > 0){
            return elapsedMinutes + " Minute" + (elapsedDays == 1 ? "" : "s") + " Ago";
        } else if (elapsedSeconds > 0){
            return elapsedSeconds + " Second" + (elapsedDays == 1 ? "" : "s") + " Ago";
        }

        return  "Unknown";
    }
}
