package com.shopping.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by wang on 2017/5/14.
 */
public class DateTimeUtil {
    //joda-time

    public static String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //str->date
    public static Date strToDate(String str, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    //date->str
    public static String dateToStr(Date date, String format) {
        if (date == null)
            return StringUtils.EMPTY;

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    //str->date
    public static Date strToDate(String str) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    //date->str
    public static String dateToStr(Date date) {
        if (date == null)
            return StringUtils.EMPTY;

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateTimeUtil.strToDate("2017-05-15 11:11:11", "yyyy-MM-dd HH:mm:ss"));
    }
}
