package com.timedebris.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;

public class DateTimeTransformer {

	static SimpleDateFormat format1 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM");

	static Calendar cal = Calendar.getInstance();
	static Date date;

	public static String transformDateTime(long timeInMillis) {
		Date date = new Date(timeInMillis);
		String strTime = format1.format(date);
		return strTime;
	}

	public static String transformDate(long timeInMillis) {
		Date date = new Date(timeInMillis);
		String strTime = format2.format(date);
		return strTime;
	}

	public static String transformMonth(long timeInMillis) {
		Date date = new Date(timeInMillis);
		String strTime = format3.format(date);
		return strTime;
	}

	public static String getMonthString(String datetime) {
		if (datetime == "")
			return datetime;

		long currentTimeMillis = System.currentTimeMillis();
		String thisMonth = transformMonth(currentTimeMillis);

		cal.setTimeInMillis(currentTimeMillis);
		cal.add(Calendar.MONTH, -1);
		String lastMonth = transformMonth(cal.getTimeInMillis());

		String yearAndMonth = datetime.substring(0, 7);
		String month = datetime.substring(5, 7);

		if (yearAndMonth.equalsIgnoreCase(thisMonth))
			return "本月";
		if (yearAndMonth.equalsIgnoreCase(lastMonth))
			return "上月";
		if (month.equalsIgnoreCase("01"))
			return datetime.substring(0, 4) + "年";
		return Integer.parseInt(month) + "月";
	}

	public static String getWeekString(String datetime) {
		if (datetime == "")
			return datetime;

		try {
			date = format2.parse(datetime);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.DATE, (8 - cal.get(Calendar.DAY_OF_WEEK)) % 7);
		int month2 = cal.get(Calendar.MONTH) + 1;
		int day2 = cal.get(Calendar.DAY_OF_MONTH);

		int month1 = Integer.parseInt(datetime.substring(5, 7));
		int day1 = Integer.parseInt(datetime.substring(8, 10));

		return month1 + "/" + day1 + "-" + month2 + "/" + day2;
	}

	public static String getDayString(String datetime) {
		if (datetime == "")
			return datetime;

		long currentTimeMillis = System.currentTimeMillis();

		String today = transformDate(currentTimeMillis);
		String yesterday = transformDate(currentTimeMillis
				- AlarmManager.INTERVAL_DAY);

		try {
			date = format2.parse(datetime);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (datetime.equalsIgnoreCase(today))
			return "今天";
		if (datetime.equalsIgnoreCase(yesterday))
			return "昨天";
		if (cal.get(Calendar.DAY_OF_WEEK) == 2)
			return "周一";
		int month = Integer.parseInt(datetime.substring(5, 7));
		int day = Integer.parseInt(datetime.substring(8, 10));
		return month + "/" + day;
	}

	public static Date parseDateTimeString(String dateString) {
		Date date = null;
		try {
			date = format1.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date parseDateFromString(String dateString) {
		Date date = null;
		try {
			date = format2.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String getintervalStringFromLong(long interval) {
		if (interval == 0) {
			// for easytask
			return "";
		}
		long seconds = interval / 1000;
		if (seconds == 0)
			seconds = 1;
		if (seconds < 60) {
			return seconds + "秒";
		}
		long minutes = seconds / 60;
		seconds %= 60;
		if (minutes < 60) {
			return minutes + "分" + seconds + "秒";
		}
		int hours = (int) (minutes / 60);
		minutes %= 60;
		return hours + "小时" + minutes + "分";
	}
}
