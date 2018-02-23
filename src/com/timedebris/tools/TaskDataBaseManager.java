package com.timedebris.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.timedebris.BroadcastReceiver.UpdateBroadCast;
import com.timedebris.View.ClockTextView;

public class TaskDataBaseManager {

	private static final String DB_PATH = "my_db";
	private static SQLiteDatabase db;

	private static String life = "life";
	private static String work = "work";
	private static String daily = "daily";
	private static String week = "week";
	private static String month = "month";

	public static int COUNT_DAY;
	public static int COUNT_WEEK;
	public static int COUNT_MONTH;

	private static String DB_CREATE_TABLE_LIFE = "create table "
			+ life
			+ " (_id integer primary key autoincrement,"
			+ " task_name varchar(20), time_start varchar(20), time_end varchar(20), interval int(20))";

	private static String DB_CREATE_TABLE_WORK = "create table "
			+ work
			+ " (_id integer primary key autoincrement,"
			+ " task_name varchar(20), time_start varchar(20), time_end varchar(20), interval int(20), has_tomato int(1))";

	private static String DB_CREATE_TABLE_EASYTASK = "create table easy_task "
			+ "(_id integer primary key autoincrement,"
			+ " task_name varchar(20), time_end varchar(20))";

	private static String DB_CREATE_TABLE_SUMMARY = "create table summary "
			+ "(task_type integer, task_name varchar(20))";

	private static String DB_CREATE_TABLE_DAILY = "create table "
			+ daily
			+ "("
			+ "_id integer primary key autoincrement,tomato_sum numeric(3,0), little_sum numeric(3,0), day_time varchar(20)"
			+ ")";

	private static String DB_CREATE_TABLE_WEEK = "create table "
			+ week
			+ "("
			+ "_id integer primary key autoincrement,tomato_sum numeric(4,0), little_sum numeric(4,0), week_time varchar(20)"
			+ ", count numeric(2,0)" + ")";

	private static String DB_CREATE_TABLE_MONTH = "create table "
			+ month
			+ "("
			+ "_id integer primary key autoincrement,tomato_sum numeric(5,0), little_sum numeric(5,0), month_time varchar(20)"
			+ ", count numeric(2,0)" + ")";

	@SuppressLint("NewApi")
	public static void deleteDateBaseInstance(Context context) {
		SQLiteDatabase.deleteDatabase(new File(context.getFilesDir().toString()
				+ DB_PATH));
	}

	public static void createDateBaseInstance(Context context) {
		db = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir()
				.toString() + DB_PATH, null);
		testDBExists();
		checkDBUpdate();
	}

	private static void checkDBUpdate() {
		Cursor cursor = db.rawQuery("select max(day_time) from daily", null);
		String lastDayTime, todayDayTime;
		if (cursor.moveToFirst()) {
			lastDayTime = cursor.getString(0);
			Date lastUpdateDate = DateTimeTransformer
					.parseDateFromString(lastDayTime);
			Calendar calCurrent = Calendar.getInstance();
			calCurrent.setTime(lastUpdateDate);

			todayDayTime = DateTimeTransformer.transformDate(System
					.currentTimeMillis());
			Date todayDate = DateTimeTransformer
					.parseDateFromString(todayDayTime);
			Calendar calToday = Calendar.getInstance();
			calToday.setTime(todayDate);

			while (calToday.compareTo(calCurrent) > 0) {
				String timeCurrent = DateTimeTransformer
						.transformDate(calCurrent.getTimeInMillis());

				calCurrent.add(Calendar.DATE, 1);
				String timeNext = DateTimeTransformer.transformDate(calCurrent
						.getTimeInMillis());

				TaskDataBaseManager.setDailyData(timeCurrent);
				TaskDataBaseManager.updateWeekData(timeCurrent);
				TaskDataBaseManager.updateMonthData(timeCurrent);
				TaskDataBaseManager.insertDailyData(timeNext);
				TaskDataBaseManager.insertWeekData(timeNext);
				TaskDataBaseManager.insertMonthData(timeNext);
			}

		}

	}

	public static void testDBExists() {
		try {
			createTable();
			initialTestData();
			initialTableData();
		} catch (Exception e) {
			Log.e("TaskDBManager", "tables already exist, no creation !");
		}
	}

	public static void createTable() {
		db.execSQL(DB_CREATE_TABLE_LIFE);
		db.execSQL(DB_CREATE_TABLE_WORK);
		db.execSQL(DB_CREATE_TABLE_EASYTASK);
		db.execSQL(DB_CREATE_TABLE_SUMMARY);
		db.execSQL(DB_CREATE_TABLE_DAILY);
		db.execSQL(DB_CREATE_TABLE_WEEK);
		db.execSQL(DB_CREATE_TABLE_MONTH);
	}

	private static void initialTestData() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int days = 20;
		cal.add(Calendar.DATE, -days);
		Random ran = new Random();
		for (int i = -days; i < 0; i++) {
			for (int m = 0; m < ran.nextInt(48); m++) {
				workTaskFinish(
						"work-" + m,
						cal.getTimeInMillis(),
						cal.getTimeInMillis() + AlarmManager.INTERVAL_HALF_HOUR,
						true);
			}
			for (int n = 0; n < ran.nextInt(20); n++) {
				easyTaskFinish("easytask-" + n, cal.getTimeInMillis());
			}
			UpdateBroadCast.testUpdateData(cal.getTimeInMillis());

			cal.add(Calendar.DATE, 1);
		}
	}

	private static void initialTableData() {
		long time = System.currentTimeMillis();
		String strTime = DateTimeTransformer.transformDate(time);
		insertDailyData(strTime);
		insertWeekData(strTime);
		insertMonthData(strTime);
	}

	public static void lifeTaskFinish(String taskName, long startTime,
			long endTime) {
		db.execSQL("insert into life values(null, ?, ?, ?, ?)", new Object[] {
				taskName, DateTimeTransformer.transformDateTime(startTime),
				DateTimeTransformer.transformDateTime(endTime),
				endTime - startTime });
	}

	public static void workTaskFinish(String taskName, long startTime,
			long endTime, boolean hasTomato) {
		db.execSQL(
				"insert into work values(null, ?, ?, ?, ?, ?)",
				new Object[] { taskName,
						DateTimeTransformer.transformDateTime(startTime),
						DateTimeTransformer.transformDateTime(endTime),
						endTime - startTime, hasTomato ? 1 : 0 });
	}

	public static void easyTaskFinish(String taskName, long endTime) {
		db.execSQL("insert into easy_task values(null, ?, ?)", new Object[] {
				taskName, DateTimeTransformer.transformDateTime(endTime) });
	}

	public static void deleteTaskFromSummary(String taskName) {
		db.execSQL("delete from summary where task_name = ? ",
				new String[] { taskName });
	}

	public static void addTaskToSummary(int groupPosition, String taskName) {
		db.execSQL("insert into summary values(?, ?)", new Object[] {
				groupPosition, taskName });
	}

	public static void getTaskFromSummary(List<List<String>> task) {
		Cursor cursor = db.rawQuery("select * from summary", null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int taskType = cursor.getInt(0);
			String taskName = cursor.getString(1);
			task.get(taskType).add(taskName);
		}
	}

	public static int getTomatoCount(String taskName) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str_date = sdf.format(new java.util.Date());

		Cursor cursor = db
				.rawQuery(
						"select count(*) from work where task_name = ? and "
								+ "strftime('%Y-%m-%d', time_end) = ? and has_tomato = 1",
						new String[] { taskName, str_date });
		if (cursor.moveToFirst() == false)
			// moveToFirse() is necessary~
			return 0;
		else
			return cursor.getInt(0);
	}

	public static void getTomatoCounts(List<List<String>> task,
			Map<String, Integer> tomatoCount) {
		for (String taskName : task.get(1))
			tomatoCount.put(taskName, getTomatoCount(taskName));
	}

	public static boolean isTaskExistInSummary(String taskName) {
		Cursor cursor = db.rawQuery(
				"select * from summary where task_name = ? ",
				new String[] { taskName });
		boolean isExist = cursor.getCount() != 0;
		cursor.close();
		return isExist;
	}

	public static Map<String, Integer> getDailyData(String datetime) {
		Cursor cursor = db.rawQuery(
				"select tomato_sum, little_sum from daily where day_time = ?",
				new String[] { datetime });
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (cursor.moveToFirst()) {
			map.put("tomato_sum", cursor.getInt(0));
			map.put("little_sum", cursor.getInt(1));
		} else {
			map.put("tomato_sum", 0);
			map.put("little_sum", 0);
		}
		return map;
	}

	public static void insertDailyData(String datetime) {
		db.execSQL("insert into daily values(null,?,?,?)", new Object[] { 0, 0,
				datetime });
	}

	public static void setDailyData(String datetime) {
		// datetime as YYYY:mm:dd yesterday~
		int tomatoSumDaily = tomatoSumDaily(datetime);
		int littleSumDaily = littleSumDaily(datetime);

		db.execSQL(
				"update daily set tomato_sum=?, little_sum=? where day_time=?",
				new Object[] { tomatoSumDaily, littleSumDaily, datetime });
	}

	public static int tomatoSumDaily(String datetime) {
		Cursor cursor = db.rawQuery("select count(*) from work where "
				+ "strftime('%m-%d' , time_end) = ? and has_tomato = 1",
				new String[] { datetime.substring(5, 10) });
		// 先添加当天的内容
		cursor.moveToFirst();
		int sum = cursor.getInt(0);
		cursor.close();
		return sum;
	}

	public static int littleSumDaily(String datetime) {
		Cursor cursor = db.rawQuery("select count(*) from easy_task where "
				+ "strftime('%m-%d' , time_end) = ?",
				new String[] { datetime.substring(5, 10) });
		// 先给番茄再算小事
		cursor.moveToFirst();
		int sum = cursor.getInt(0);
		cursor.close();
		return sum;
	}

	public static void insertWeekData(String datetime) {
		Cursor cursor = db
				.rawQuery(
						"select * from week where ? >=datetime(week_time,'start of day','-7 day','weekday 0') AND ? <datetime(week_time,'start of day','+0 day','weekday 0')",
						new String[] { datetime, datetime });
		if (cursor.getCount() == 0) {
			db.execSQL("insert into week values(null,?,?,?,0)", new Object[] {
					0, 0, datetime });
		}
	}

	public static void updateWeekData(String datetime) {
		// datetime --> yesterday
		Map<String, Integer> map = getDailyData(datetime);
		db.execSQL(
				"update week set tomato_sum = (tomato_sum + ?), little_sum = (little_sum + ?), count = (count + 1) where ? >=datetime(week_time,'start of day','-7 day','weekday 0') AND ? <datetime(week_time,'start of day','+0 day','weekday 0')",
				new Object[] { map.get("tomato_sum"), map.get("little_sum"),
						datetime, datetime });
	}

	public static void insertMonthData(String datetime) {
		Cursor cursor = db.rawQuery(
				"select * from month where strftime('%Y-%m', month_time) = ? ",
				new String[] { datetime.substring(0, 7) });
		if (cursor.getCount() == 0) {
			db.execSQL("insert into month values(null,?,?,?,0)", new Object[] {
					0, 0, datetime });
		}
	}

	public static void updateMonthData(String datetime) {
		// datetime --> yesterday
		Map<String, Integer> map = getDailyData(datetime);
		db.execSQL(
				"update month set tomato_sum = (tomato_sum + ?), little_sum = (little_sum + ?), count = (count + 1) where strftime('%Y-%m', month_time) = ? ",
				new Object[] { map.get("tomato_sum"), map.get("little_sum"),
						datetime.substring(0, 7) });
	}

	public static Map<Integer, Object> selectDailyData() {
		Map<Integer, Object> result = new HashMap<Integer, Object>();

		Cursor cursor = db
				.rawQuery(
						"select tomato_sum, little_sum, day_time from daily order by day_time",
						null);
		int curnum = cursor.getCount();
		COUNT_DAY = curnum + 10;
		int[] tomato = new int[COUNT_DAY];
		int[] little = new int[COUNT_DAY];
		String[] date = new String[COUNT_DAY];

		for (int i = 0; i < 5; i++) {
			tomato[i] = 0;
			little[i] = 0;
			date[i] = "";
		}
		curnum = 5;

		if (cursor.moveToFirst()) {
			do {
				tomato[curnum] = cursor.getInt(0);
				little[curnum] = cursor.getInt(1);
				date[curnum] = cursor.getString(2);
				curnum++;
			} while (cursor.moveToNext());
		}
		for (int i = curnum; i < curnum + 5; i++) {
			tomato[i] = 0;
			little[i] = 0;
			date[i] = "";
		}

		result.put(0, tomato);
		result.put(1, little);
		result.put(2, date);

		return result;
	}

	public static Map<Integer, Object> selectWeekData() {
		Map<Integer, Object> result = new HashMap<Integer, Object>();

		Cursor cursor = db
				.rawQuery(
						"select tomato_sum, little_sum, week_time, count from week order by week_time",
						null);
		int curnum = cursor.getCount();
		COUNT_WEEK = curnum + 6;
		int[] tomato = new int[COUNT_WEEK];
		int[] little = new int[COUNT_WEEK];
		String[] date = new String[COUNT_WEEK];
		int[] count = new int[COUNT_WEEK];
		for (int i = 0; i < 3; i++) {
			tomato[i] = 0;
			little[i] = 0;
			date[i] = "";
			count[i] = 1;
		}
		curnum = 3;
		if (cursor.moveToFirst()) {
			do {
				tomato[curnum] = cursor.getInt(0);
				little[curnum] = cursor.getInt(1);
				date[curnum] = cursor.getString(2);
				count[curnum] = cursor.getInt(3);
				curnum++;
			} while (cursor.moveToNext());
		}
		if (count[curnum - 1] == 0)
			count[curnum - 1] = 1;

		for (int i = curnum; i < curnum + 3; i++) {
			tomato[i] = 0;
			little[i] = 0;
			date[i] = "";
			count[i] = 1;
		}

		result.put(0, tomato);
		result.put(1, little);
		result.put(2, date);
		result.put(3, count);

		return result;
	}

	public static Map<Integer, Object> selectMonthData() {
		Map<Integer, Object> result = new HashMap<Integer, Object>();

		Cursor cursor = db
				.rawQuery(
						"select tomato_sum, little_sum, month_time, count from month order by month_time",
						null);
		int curnum = cursor.getCount();
		COUNT_MONTH = curnum + 4;
		int[] tomato = new int[COUNT_MONTH];
		int[] little = new int[COUNT_MONTH];
		String[] date = new String[COUNT_MONTH];
		int[] count = new int[COUNT_MONTH];
		for (int i = 0; i < 2; i++) {
			tomato[i] = 0;
			little[i] = 0;
			date[i] = "";
			count[i] = 1;
		}
		curnum = 2;
		if (cursor.moveToFirst()) {
			do {
				tomato[curnum] = cursor.getInt(0);
				little[curnum] = cursor.getInt(1);
				date[curnum] = cursor.getString(2);
				count[curnum] = cursor.getInt(3);
				curnum++;
			} while (cursor.moveToNext());
		}
		if (count[curnum - 1] == 0)
			count[curnum - 1] = 1;
		for (int i = curnum; i < curnum + 2; i++) {
			tomato[i] = 0;
			little[i] = 0;
			date[i] = "";
			count[i] = 1;
		}

		result.put(0, tomato);
		result.put(1, little);
		result.put(2, date);
		result.put(3, count);

		return result;
	}

	public static int getDailyFromWeek(String datetime) {
		// datetime: YYYY-mm-dd
		Cursor cursor = db.rawQuery(
				"select _id from daily where day_time = ? ",
				new String[] { datetime });
		int position = 5;
		if (cursor.moveToFirst()) {
			position = cursor.getInt(0) + 5 - 1;
		}
		return position;
	}

	public static int getWeekFromDaily(String datetime) {
		// weektime: YYYY-mm-dd
		Cursor cursor = db
				.rawQuery(
						"select _id from week where ? >=datetime(week_time,'start of day','-7 day','weekday 0') AND ? <datetime(week_time,'start of day','+0 day','weekday 0')",
						new String[] { datetime, datetime });
		int position = 3;
		if (cursor.moveToFirst()) {
			position = cursor.getInt(0) + 3 - 1;
		}
		return position;
	}

	public static int getWeekFromMonth(String datetime) {
		// datetime: YYYY-mm-dd
		return getWeekFromDaily(datetime);
	}

	public static int getMonthFromWeek(String datetime) {
		// datetime: YYYY-mm-dd
		datetime = datetime.substring(0, 7);
		Cursor cursor = db
				.rawQuery(
						"select _id from month where strftime('%Y-%m', month_time) = ? ",
						new String[] { datetime });
		int position = 2;
		if (cursor.moveToFirst()) {
			position = cursor.getInt(0) + 2 - 1;
		}
		return position;
	}

	public static Map<Integer, Object> getDailyDetail() {
		Comparator<DetaiDailyData> comparator = new Comparator<DetaiDailyData>() {
			@Override
			public int compare(DetaiDailyData lhs, DetaiDailyData rhs) {
				// DetailData值大 == dateEnd晚
				return lhs.dateEnd.compareTo(rhs.dateEnd);
			}
		};

		List<String> dateList = new ArrayList<String>();
		List<List<DetaiDailyData>> dailyList = new ArrayList<List<DetaiDailyData>>();

		for (int i = 0; i < 3; i++) {
			Set<DetaiDailyData> tempSet = new TreeSet<DetaiDailyData>(
					comparator);
			String tempDate = DateTimeTransformer.transformDate(System
					.currentTimeMillis() - AlarmManager.INTERVAL_DAY * i);
			dateList.add(tempDate.substring(5));

			Cursor cursor;
			// today--------------------------------------------------------------------------
			cursor = db
					.rawQuery(
							"select task_name, time_start, time_end, interval from life where strftime('%Y-%m-%d', time_end) = ?",
							new String[] { tempDate });
			if (cursor.moveToFirst()) {
				do {
					tempSet.add(new DetaiDailyData(0, cursor.getString(0),
							cursor.getString(1), cursor.getString(2), cursor
									.getLong(3)));
				} while (cursor.moveToNext());
			}
			cursor = db
					.rawQuery(
							"select task_name, time_start, time_end, interval from work where strftime('%Y-%m-%d', time_end) = ?",
							new String[] { tempDate });
			if (cursor.moveToFirst()) {
				do {
					tempSet.add(new DetaiDailyData(1, cursor.getString(0),
							cursor.getString(1), cursor.getString(2), cursor
									.getLong(3)));
				} while (cursor.moveToNext());
			}
			cursor = db
					.rawQuery(
							"select task_name, time_end from easy_task where strftime('%Y-%m-%d', time_end) = ?",
							new String[] { tempDate });
			if (cursor.moveToFirst()) {
				do {
					tempSet.add(new DetaiDailyData(2, cursor.getString(0),
							cursor.getString(1), cursor.getString(1), 0));
				} while (cursor.moveToNext());
			}
			List<DetaiDailyData> tempList = new ArrayList<DetaiDailyData>(
					tempSet);
			dailyList.add(tempList);
		}

		Map<Integer, Object> packMap = new HashMap<Integer, Object>();
		packMap.put(0, dateList);
		packMap.put(1, dailyList);
		return packMap;
	}

	public static Map<Integer, Object> getWeekDetail() {
		List<String> dateList = new ArrayList<String>();
		Map<String, Integer> parseMap = new HashMap<String, Integer>();
		List<List<DetaiWeeklyData>> weekList = new ArrayList<List<DetaiWeeklyData>>();
		for (int i = 0; i < 14; i++) {
			String tempDate = DateTimeTransformer.transformDate(System
					.currentTimeMillis() - AlarmManager.INTERVAL_DAY * i);
			dateList.add(tempDate.substring(5));
			parseMap.put(tempDate, i);
			weekList.add(new ArrayList<DetaiWeeklyData>());
		}
		String today = DateTimeTransformer.transformDateTime(System
				.currentTimeMillis());

		Cursor cursor;
		// today--------------------------------------------------------------------------
		cursor = db
				.rawQuery(
						"select task_name, strftime('%Y-%m-%d', time_end), sum(interval) from life"
								+ " where time_end >=datetime(?,'start of day','-13 day')"
								+ " group by task_name, strftime('%Y-%m-%d', time_end)",
						new String[] { today });
		if (cursor.moveToFirst()) {
			do {
				weekList.get(parseMap.get(cursor.getString(1))).add(
						new DetaiWeeklyData(0, cursor.getString(0), cursor
								.getString(1), cursor.getLong(2)));
			} while (cursor.moveToNext());
		}
		cursor = db
				.rawQuery(
						"select task_name, strftime('%Y-%m-%d', time_end), count(*) from work"
								+ " where time_end >=datetime(?,'start of day','-13 day') and has_tomato = 1"
								+ " group by task_name, strftime('%Y-%m-%d', time_end)",
						new String[] { today });
		if (cursor.moveToFirst()) {
			do {
				weekList.get(parseMap.get(cursor.getString(1))).add(
						new DetaiWeeklyData(1, cursor.getString(0), cursor
								.getString(1), cursor.getInt(2)));
			} while (cursor.moveToNext());
		}
		cursor = db
				.rawQuery(
						"select task_name, strftime('%Y-%m-%d', time_end), count(*) from easy_task"
								+ " where time_end >=datetime(?,'start of day','-13 day')"
								+ " group by task_name, strftime('%Y-%m-%d', time_end)",
						new String[] { today });
		if (cursor.moveToFirst()) {
			do {
				weekList.get(parseMap.get(cursor.getString(1))).add(
						new DetaiWeeklyData(2, cursor.getString(0), cursor
								.getString(1), cursor.getInt(2)));
			} while (cursor.moveToNext());
		}

		Comparator<DetaiWeeklyData> comparator = new Comparator<DetaiWeeklyData>() {
			@Override
			public int compare(DetaiWeeklyData lhs, DetaiWeeklyData rhs) {
				if (lhs.type != lhs.type)
					return lhs.type - rhs.type;
				return (lhs.intervalOrCount - rhs.intervalOrCount) > 0 ? -1 : 1;
			}
		};
		for (List<DetaiWeeklyData> list : weekList) {
			Collections.sort(list, comparator);
		}

		Map<Integer, Object> packMap = new HashMap<Integer, Object>();
		packMap.put(0, dateList);
		packMap.put(1, weekList);
		return packMap;
	}
}