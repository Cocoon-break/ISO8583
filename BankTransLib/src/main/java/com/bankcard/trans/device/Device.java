package com.bankcard.trans.device;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Device {


    private static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 获取日期YYYYMMDD
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

    /**
     * 获取日期YYYYMMDD
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYear() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * 获取时间HHMMSS
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date(System.currentTimeMillis());
        return dateFormat.format(date);
    }

}
