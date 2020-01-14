package com.phz.dropdownmenudemo.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 工具类，处理时间
 */
public class TimeUtil {
    /**
     * SimpleDateFormat是线程不安全的的类，一般不要定义为static，如果定义为static，请加锁使用
     */
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_DATE_YEAR = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat DATE_FORMAT_DATE_MONTH = new SimpleDateFormat("MM");
    public static final SimpleDateFormat DATE_FORMAT_DATE_DATE = new SimpleDateFormat("dd");
    private TimeUtil() {
    }

    /**
     * 返回当天日期的字符串，可以自己定格式
     */
    public static String now(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(Calendar.getInstance().getTime());
    }

    /**
     * string转date   拿到某一天的序列化字符串，可以自己定格式
     * @param stringTime
     * @param Pattern
     * @return
     */
    public static Date format(String stringTime,String Pattern){
        SimpleDateFormat dataformat = new SimpleDateFormat(Pattern);
        Date date=null;
        try {
            date = dataformat.parse(stringTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * date转string
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * 其实还是推荐使用Calendar，这样也行
     * yyyy-MM-dd HH:mm:ss
     */
    public static String DateToStringYMDHMS(Date date){
        synchronized (DEFAULT_DATE_FORMAT){
            return DEFAULT_DATE_FORMAT.format(date);
        }
    }

    public static String DateToStringYMD(Date date){
        synchronized (DATE_FORMAT_DATE){
            return DATE_FORMAT_DATE.format(date);
        }
    }

    public static String getYear(Date date){
        synchronized (DATE_FORMAT_DATE_YEAR){
            return DATE_FORMAT_DATE_YEAR.format(date);
        }
    }

    public static String getMonth(Date date){
        synchronized (DATE_FORMAT_DATE_MONTH){
            return DATE_FORMAT_DATE_MONTH.format(date);
        }
    }

    public static String getDay(Date date){
        synchronized (DATE_FORMAT_DATE_DATE){
            return DATE_FORMAT_DATE_DATE.format(date);
        }
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param pattern
     * @return
     */
    public static String getTime(long timeInMillis, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date(timeInMillis));
    }

    /**
     *  string to long
     * @param strTime
     * @param formatType
     * @return
     */
    public static long getTime(String strTime, String formatType)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = simpleDateFormat.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // date类型转成long类型
        long currentTime = date.getTime();
        return currentTime;
    }

    /**
     * 得到一个格式化的时间
     *
     * @param time 时间 毫秒
     *
     * @return 时：分：秒
     */
    public static String getFormatTime(long time) {
        time = time/1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;

        // 秒显示两位
        String strSecond = ("00" + second).substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute).substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

        return strHour + ":" + strMinute + ":" + strSecond;
    }

    /**
     * 返回时间差
     * @param beginTime
     * @param endTime
     * @return 时：分：秒
     */
    public static String getTotaltime(String beginTime,String endTime,String formate1,String formate2){
        String strTime =null;
        long begin = getTime(beginTime,formate1);
        long end = getTime(endTime,formate2);
        //strTime= DatatoTime((int)(end/1000-begin/1000));
        strTime= getFormatTime(end-begin);
        return strTime;
    }

    /**
     * 返回时分秒（比如00:00:00）
     * @param data
     * @return
     */
    private static  String  DatatoTime(int data) {
        String time = null;
        String time_second = "00";
        String time_miu = "00";
        String time_hour = "00";
        int miu = data / 60;
        int sec = data % 60;
        int hour = miu / 60;
        if (sec % 60 < 10) {
            time_second = "0" + sec;
        } else {
            time_second = "" + sec;
        }
        if (miu < 10) {
            time_miu = "0" + miu;
        } else if (miu < 60) {
            time_miu = "" + miu;
        } else if (miu >= 60 && hour < 10 && miu % 60 < 10) {
            time_hour = "0" + hour;
            time_miu = "0" + miu % 60;
        } else if (miu >= 60 && hour < 10 && miu % 60 >= 10) {
            time_hour = "0" + hour;
            time_miu = "" + miu % 60;
        } else if (miu > 60 && hour < 24 && miu % 60 < 10) {
            time_hour = "" + hour;
            time_miu = "0" + miu % 60;
        } else if (miu > 60 && hour < 24 && miu % 60 >= 10) {
            time_hour = "" + hour;
            time_miu = "" + miu % 60;
        }

        time = time_hour + ":" + time_miu + ":" + time_second;
        return time;
    }


    /**
     * 根据用户生日计算年龄
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthday)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                // monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

    //获得当前时间 返回13位时间戳
    @SuppressLint("WrongConstant")
    public static long getTimesWeeknight(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return (cal.getTime().getTime()+ (7 * 24 * 60 * 60 * 1000));
    }
}
