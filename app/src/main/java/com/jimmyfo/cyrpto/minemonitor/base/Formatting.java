package com.jimmyfo.cyrpto.minemonitor.base;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatting {
    public static final String DATE_FORMAT_STRING = "MM/dd/yyyy HH:mm:ss z";
    public static DateFormat dateFormatLocal = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);
    public static DateFormat dateFormatGmt = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.ENGLISH);

    public static DecimalFormat decimalFormat = new DecimalFormat();
}
