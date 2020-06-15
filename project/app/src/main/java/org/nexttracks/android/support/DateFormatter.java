package org.nexttracks.android.support;

import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateFormatter {
    private static SimpleDateFormat dateFormatterFull = new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault());
    private static SimpleDateFormat dateFormatterTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String formatDateShort(long tstSeconds) {
        return dateFormatterTime.format(new Date(TimeUnit.SECONDS.toMillis(tstSeconds)));
    }

    public static String formatDate(long tstSeconds) {
        return formatDate(new Date(TimeUnit.SECONDS.toMillis(tstSeconds)));
    }

    public static String formatDate(@NonNull Date d) {
        if(DateUtils.isToday(d.getTime())) {
            return dateFormatterTime.format(d);
        } else {
            return dateFormatterFull.format(d);
        }
    }
}
