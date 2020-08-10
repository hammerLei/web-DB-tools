package com.dbTool.util;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
/**
 * Created by leifeifei on 17-10-12.
 */
public class DateUtil {
  public static int datesBetween(Date from, Date to) {
    if (from.getTime() > to.getTime()) {
      Date tmp = from;
      from = to;
      to = tmp;
    }
    return (int) ((to.getTime() - from.getTime()) / (24 * 60 * 60 * 1000));
  }


  private static final Logger log = Logger.getLogger(DateUtil.class);


  public static final int SECONDS_IN_HOUR = 3600;
  public static final long MILLIS_IN_HOUR = 3600000L;
  public static final int SECONDS_IN_DAY = 86400;
  public static final long MILLIS_IN_DAY = 86400000L;
  private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
  public static final String DateFormat_yyyyMMdd = "yyyyMMdd";
  public static final String DateFormat_yyyyMMddHHmmss = "yyyyMMddHHmmss";
  public static final String DateFormat_yyyyMM = "yyyyMM";
  public static final String DATEFORMAT_YYYY_MM_DD = "yyyy-MM-dd";
  public static final String DATEFORMAT_YYYY_MM = "yyyy-MM";
  public static final String DATEFORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
  public static final String DATEFORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
  public static final String DATEFORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final String DATEFORMAT_MM_DD_YYYY = "MM/dd/yyyy";

  public static final String DATEFORMAT_MM_DD_YY = "MM/dd/yy";


  /**
   * 将Date类型转换成String类型
   *
   * @param date
   * @param pattern
   * @return
   */
  public static String format(Date date, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(date);
  }

  public static String formatTimeZone(Date date, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }

  /**
   * 字符串日期转换成date类型日期
   *
   * @param date
   * @param pattern
   * @return
   */
  public static Date parse(String date, String pattern) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    try {
      return sdf.parse(date);
    } catch (ParseException e) {
      log.warn("解析日期字符串出错:" + date, e);
      return null;
    }
  }


  public static String changeMonthDate(int monthNum) {
    Date currentDate = new Date();
    GregorianCalendar gc = new GregorianCalendar();
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

    gc.setTime(currentDate);
    gc.add(Calendar.MONTH, -monthNum);
    gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
    return sf.format(gc.getTime());
  }


  /**
   * java中对日期的加减操作
   * gc.add(1,-1)表示年份减一.
   * gc.add(2,-1)表示月份减一.
   * gc.add(3.-1)表示周减一.
   * gc.add(5,-1)表示天减一.
   * 以此类推应该可以精确的毫秒吧.没有再试.大家可以试试.
   * GregorianCalendar类的add(int field,int amount)方法表示年月日加减.
   * field参数表示年,月.日等.
   * amount参数表示要加减的数量.
   * return 格式如"2013-09-01"
   */
  public static String changeDate(int dayNum) {
    Date currentDate = new Date();
    GregorianCalendar gc = new GregorianCalendar();
    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

    gc.setTime(currentDate);
    gc.add(Calendar.DATE, -dayNum);
    gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE));
    return sf.format(gc.getTime());
  }


  /**
   * 比较当前时间是否在指定的时间之内
   *
   * @param startDay 格式为"yyyy-HH-dd"
   * @param endDay   格式为"yyyy-HH-dd"
   * @return
   * @throws Exception
   */
  public static Boolean isBetweenTwoDates(String startDay, String endDay) throws Exception {
    boolean result = false;
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date nowDate = new Date();
    Date startTime = df2.parse(startDay);
    Date endTime = df2.parse(endDay);
    if (nowDate.after(startTime) && nowDate.before(endTime)) {
      result = true;
    }
    return result;
  }

  /**
   * 2个日期相差多少天
   *
   * @param start
   * @param end
   * @return
   */
  public static int daysBetween(String start, String end) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(sdf.parse(start));
      long time1 = cal.getTimeInMillis();
      cal.setTime(sdf.parse(end));
      long time2 = cal.getTimeInMillis();
      long between_days = (time2 - time1) / (1000 * 3600 * 24);
      return Integer.parseInt(String.valueOf(between_days));
    } catch (ParseException e) {
      log.error("解析日期字符串出错:" + start + "," + end, e);
      return 0;
    }
  }

  /**
   * 201702 返回 Feb 17
   *
   * @param dateString
   * @return
   */
  public static String formatEnDateYm(String dateString) {
    SimpleDateFormat sdf = new SimpleDateFormat(DateFormat_yyyyMM);
    Locale l = new Locale("en");
    String result = null;
    try {
      Date date = sdf.parse(dateString);
      String month = String.format(l, "%tb", date);
      String year = String.format("%ty", date);
      result = month + " " + year;
    } catch (ParseException e) {
      log.warn("解析日期字符串出错:" + dateString, e);
      return null;
    }
    return result;
  }

  public static int daysBetween(Date start, Date end) {
    if (null == start || null == end || start.getTime() > end.getTime()) {
      return -1;
    }

    LocalDateTime startDateTime = LocalDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault());
    LocalDateTime endDateTime = LocalDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
    return (int) Duration.between(startDateTime, endDateTime).toDays();
  }

  /**
   * 根据当前时间获取前或者后month月,月份加减后，设置date号hour小时minute分钟second秒,这个是带时区的，慎用！
   *
   * @param month
   * @param date
   * @param hour
   * @param minute
   * @param second
   * @return
   */
  public static Date addTime(int month, int date, int hour, int minute, int second) {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    Calendar instance = Calendar.getInstance();
    instance.add(Calendar.MONTH, month);
    instance.set(Calendar.DATE, date);
    instance.set(Calendar.HOUR_OF_DAY, hour);
    instance.set(Calendar.MINUTE, minute);
    instance.set(Calendar.SECOND, second);
    return instance.getTime();
  }

  public static Date addMonth(int m) {
    String ym = format(DateUtils.addMonths(new Date(), m), DateFormat_yyyyMM);
    return parse(ym, DateFormat_yyyyMM);
  }

  public static Date minus(Date date, int amount, ChronoUnit unit) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    return Date.from(localDateTime.truncatedTo(unit).minus(amount, unit).atZone(ZoneId.systemDefault()).toInstant());
  }

  public static List<Date> getMonthBetween(Date minDate, Date maxDate) throws ParseException {
    ArrayList<Date> result = new ArrayList<>();

    Calendar min = Calendar.getInstance();
    Calendar max = Calendar.getInstance();

    min.setTime(minDate);
    max.setTime(maxDate);

    Calendar curr = min;
    while (curr.get(Calendar.YEAR)<max.get(Calendar.YEAR)||
        curr.get(Calendar.MONTH)<max.get(Calendar.MONTH)) {
      result.add(curr.getTime());
      curr.add(Calendar.MONTH, 1);
    }
    result.add(curr.getTime());
    return result;
  }


  public static void test(int a){
//    ThreadExecutorUtil.executeTask(new Runnable() {
//      @Override
//      public void run() {
//        try{
//          System.out.println(aaaa);
//          sb=new StringBuffer();
//          sb.append("---------").append(aaaa);
//          if(aaaa.equals("a")){
//            Thread.sleep(5000);
//          }
//          System.out.println(sb.toString());
//        }catch (Exception e){
//          e.printStackTrace();
//        }
//
//      }
//    });
    a++;
    System.out.println(a);

  }

  public static void main(String args[]){
//    String statement="sadasd;\n\n  \n  dfsfsfsf;\ndsadsa;\n;";
//    String a=StringUtil.replceMultipleOnlySpace(statement);
//    String sqlStr [] = a.split(";\n");
//    System.out.println(sqlStr);
//    System.out.println(sqlStr.length);
//    String streetLine="121 dada ada";
//    String houseNumber = streetLine.trim().substring(0,streetLine.trim().indexOf(" "));
//    String streetName = streetLine.trim().substring(streetLine.trim().indexOf(" ")+1);
int i=0;
test(i);
    System.out.println(i);
//    Date date = parse("02/18/18",DateUtil.DATEFORMAT_MM_DD_YY);


//    long regDate = 0;
//      Date date = DateUtil.parse("02/05/17",DateUtil.DATEFORMAT_MM_DD_YY);
//      regDate = date!=null ? date.getTime() : 0;
//    System.out.println(regDate);
  }
}

