package org.nexttracks.android.support;

import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateFormatter {
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
    private static SimpleDateFormat dateFormatterToday = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static SimpleDateFormat dateFormatterDate = new SimpleDateFormat("HH:mm", Locale.getDefault());


    public static String formatDate(long tstSeconds) {
        return formatDate(new Date(TimeUnit.SECONDS.toMillis(tstSeconds)));
    }

    public static String formatDateShort(long tstSeconds) {
        Date d = new Date(TimeUnit.SECONDS.toMillis(tstSeconds));
        if(DateUtils.isToday(d.getTime())) {
            return dateFormatterToday.format(d);
        } else {
            return dateFormatterDate.format(d);
        }
    }

    public static String formatDate(@NonNull Date d) {
        if(DateUtils.isToday(d.getTime())) {
            return dateFormatterToday.format(d);
        } else {
            return dateFormatter.format(d);
        }
    }
}
