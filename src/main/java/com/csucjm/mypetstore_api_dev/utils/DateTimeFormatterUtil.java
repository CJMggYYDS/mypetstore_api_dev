package com.csucjm.mypetstore_api_dev.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtil {

    private static final String DATETIME_PATTERN = "yyyy年MM月dd日 HH:mm:ss";

    private DateTimeFormatterUtil() {}

    public static String format(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        if(dateTime == null) {
            return "";
        }
        return dateTimeFormatter.format(dateTime);
    }
}
