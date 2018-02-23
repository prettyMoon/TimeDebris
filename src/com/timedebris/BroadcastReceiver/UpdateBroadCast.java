package com.timedebris.BroadcastReceiver;

import java.util.Calendar;

import com.timedebris.tools.DateTimeTransformer;
import com.timedebris.tools.TaskDataBaseManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UpdateBroadCast extends BroadcastReceiver {

	public static final String ACTION_UPDATE_DATA = "com.timedebris.UPDATE_DATA";

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Update_data", Toast.LENGTH_SHORT).show();

		updateData(System.currentTimeMillis());
	}

	// 此方法弃用...
	public static void updateData(long time) {
		// time for today to insert today's items and update yesterday's items
		String timeToday = DateTimeTransformer.transformDate(time);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.DATE, -1);
		String timeYesterday = DateTimeTransformer.transformDate(cal
				.getTimeInMillis());
		// 插入今日的空记录
		TaskDataBaseManager.insertDailyData(timeToday);
		TaskDataBaseManager.insertWeekData(timeToday);
		TaskDataBaseManager.insertMonthData(timeToday);
		// 更新昨日纪录的数据
		TaskDataBaseManager.setDailyData(timeYesterday);
		TaskDataBaseManager.updateWeekData(timeYesterday);
		TaskDataBaseManager.updateMonthData(timeYesterday);
	}

	public static void testUpdateData(long time) {
		String timeToday = DateTimeTransformer.transformDate(time);

		// 插入今日的空记录
		TaskDataBaseManager.insertDailyData(timeToday);
		TaskDataBaseManager.insertWeekData(timeToday);
		TaskDataBaseManager.insertMonthData(timeToday);
		// 更新今日纪录的数据
		TaskDataBaseManager.setDailyData(timeToday);
		TaskDataBaseManager.updateWeekData(timeToday);
		TaskDataBaseManager.updateMonthData(timeToday);
	}

}
