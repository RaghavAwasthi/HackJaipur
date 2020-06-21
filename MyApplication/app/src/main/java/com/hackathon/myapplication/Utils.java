package com.hackathon.myapplication;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

public class Utils {

    public static long getCurrentDateTime() {
        return Instant.now().toEpochMilli();
    }

    public static LocalDateTime getLocalDateTime(long datetime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(datetime), ZoneId.systemDefault());
    }

    public static String getDateTime(long date) {
        String mdate = "";
        LocalDateTime dateTime = getLocalDateTime(date);
        mdate = dateTime.getMonth() + " " + dateTime.getDayOfMonth() + "," + dateTime.getYear() + " " + dateTime.getHour() + ":" + dateTime.getMinute();

        return mdate;
    }
}
