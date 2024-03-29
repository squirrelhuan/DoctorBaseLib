package cn.demomaster.huan.doctorbaselibrary.util;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author squirrel桓
 * @date 2018/11/27.
 * description：
 */
public class DateTimeUtil {

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        long ts = date.getTime();
        return ts;
    }


    /**
     * 获取今天往后一周的集合
     */
    public  void get7week(){
        String week = "";
        List<String> weeksList = new ArrayList<String>();
        List<String> dateList = getDateAsCount(21);
        for (String s : dateList) {
            System.out.println(s);
            if (s.equals(StringData())) {
                week = "今天";
            } else {
                week = "周"+getWeek(s);
            }
            weeksList.add(week);
        }
        for(String str :weeksList){
            System.out.println(str);
        }
        //return weeksList;
    }



    public static List<String> getDateAsCount(int count) {
        List<String> dates = new ArrayList<String>();
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat sim = new SimpleDateFormat(
                "yyyy-MM-dd");
        String date = sim.format(c.getTime());
        dates.add(date);
        for (int i = 0; i < count-1; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            date = sim.format(c.getTime());
            dates.add(date);
        }
        return dates;
    }

    public static List<String> getDateAsCount(Date targetDate,int count) {
        List<String> dates = new ArrayList<String>();
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat sim = new SimpleDateFormat(
                "yyyy-MM-dd");
        String date = sim.format(targetDate);//targetDate+" 00:00:00"/*c.getTime()*/);
        dates.add(date);
        for (int i = 0; i < count-1; i++) {
            dates.add(addDay(date,i+1));
            /*c.add(java.util.Calendar.DAY_OF_MONTH, 1);
            date = sim.format(c.getTime());
            dates.add(date);*/
        }
        return dates;
    }

    public static String addDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加一天
            //cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }

    }

    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }


    private static String mYear; // 当前年
    private static String mMonth; // 月
    private static String mDay;
    private static String mWay;

    /**
     * 获取当前年月日
     * 
     * @return
     */
    public static String StringData() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        if(Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear),(Integer.parseInt(mMonth)))){
            mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear),(Integer.parseInt(mMonth))));
        }
        return mYear + "-" + (mMonth.length()==1?"0"+mMonth:mMonth) + "-" + (mDay.length()==1?"0"+mDay:mDay);
    }

    public static MyDate getToday(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

       int year = cal.get(Calendar.YEAR);
       int month = cal.get(Calendar.MONTH)+1;
       int day = cal.get(Calendar.DATE);
       int hour =0;
        if (cal.get(Calendar.AM_PM) == 0)
            hour = cal.get(Calendar.HOUR);
        else
            hour = cal.get(Calendar.HOUR)+12;
       int minute = cal.get(Calendar.MINUTE);
       int second = cal.get(Calendar.SECOND);

        //my_time_1 = year + "-" + month + "-" + day;
        //my_time_2 = hour + "-" + minute + "-" + second;
        MyDate myDate = new MyDate(year,month,day,hour,minute,second);
        return  myDate;
    }

    public static String format2LenStr(int num) {
        return num < 10 ? "0" + num : String.valueOf(num);
    }

    public static class MyDate{
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;
        private int second;

        public MyDate(int year, int month, int day, int hour, int minute, int second) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

       public String getYearMonthDay(){
            return year+"-"+format2LenStr(month)+"-"+format2LenStr(day);
        }
        public String getHourMinuteSecond(){
            return format2LenStr(hour)+":"+format2LenStr(minute)+":"+format2LenStr(second);
        }
        public String getHourMinute(){
            return format2LenStr(hour)+":"+format2LenStr(minute);
        }
        public String getDateTimeStr(){
            return getYearMonthDay()+" "+getHourMinuteSecond();
        }
        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public int getSecond() {
            return second;
        }

        public void setSecond(int second) {
            this.second = second;
        }
    }

    /**得到当年当月的最大日期**/
    public static int MaxDayFromDay_OF_MONTH(int year,int month){
        Calendar time=Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR,year);
        time.set(Calendar.MONTH,month-1);//注意,Calendar对象默认一月为0                 
        int day=time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
        return day;
    }


    /**
     * 根据当前日期获得是星期几
     * @return
     */
    public static int getWeek(String time) {
        //国际上是以星期日为一周第一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c.get(Calendar.DAY_OF_WEEK);
    }
    public static String getWeekName(String time) {
        String[] weeknames ={"周日","周一","周二","周三","周四","周五","周六"};
        //国际上是以星期日为一周第一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weeknames[c.get(Calendar.DAY_OF_WEEK)-1];
    }

    /**
     * 获取前n天日期、后n天日期
     * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
     * @return
     */
    public static Date getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("CGQ","前7天==" + dft.format(endDate));
        return endDate;//dft.format(endDate);
    }

//获取本周
    public static long getTimeOfWeekStart(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        ca.clear(Calendar.MILLISECOND);
        ca.set(Calendar.DAY_OF_WEEK, ca.getFirstDayOfWeek());
        return ca.getTimeInMillis();
    }

    //获取本周
    public static String getTimeOfWeekStartDate(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        ca.clear(Calendar.MILLISECOND);
        //国外以周日为第一天，国内以周一为开始
        ca.set(Calendar.DAY_OF_WEEK, ca.getFirstDayOfWeek());

        mYear = String.valueOf(ca.get(Calendar.YEAR));// 获取当前年份
        mMonth = String.valueOf(ca.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        if(Integer.parseInt(mDay) > MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear),(Integer.parseInt(mMonth)))){
            mDay = String.valueOf(MaxDayFromDay_OF_MONTH(Integer.parseInt(mYear),(Integer.parseInt(mMonth))));
        }
       // return ca.getTimeInMillis();
       return mYear + "-" + (mMonth.length()==1?"0"+mMonth:mMonth) + "-" + (mDay.length()==1?"0"+mDay:mDay);
    }


    public static long getTimeOfMonthStart(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        ca.clear(Calendar.MILLISECOND);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        return ca.getTimeInMillis();
    }

    public static long getTimeOfYearStart(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.clear(Calendar.MINUTE);
        ca.clear(Calendar.SECOND);
        ca.clear(Calendar.MILLISECOND);
        ca.set(Calendar.DAY_OF_YEAR, 1);
        return ca.getTimeInMillis();
    }


}
