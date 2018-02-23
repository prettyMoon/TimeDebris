package com.timedebris.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.timedebris.Fragment.TaskFragment;

public class TaskDataManager {

	public static List<String> taskTypes;
	public static List<List<String>> task;

	public static Map<String, Integer> tomatoCount;

	public static void updateData() {
		if (taskTypes != null)
			return;

		taskTypes = new ArrayList<String>(3);
		task = new ArrayList<List<String>>(3);
		tomatoCount = new HashMap<String, Integer>();

		taskTypes.add("生活事务");
		taskTypes.add("工作事务");
		taskTypes.add("琐事");

		for (int i = 0; i < 3; i++)
			task.add(new ArrayList<String>());

		TaskDataBaseManager.getTaskFromSummary(task);

		TaskDataBaseManager.getTomatoCounts(task, tomatoCount);
	}

	public static void addTask(int groupPosition, String taskName) {
		switch (groupPosition) {
		case 0:
			task.get(0).add(taskName);
			TaskDataBaseManager.addTaskToSummary(groupPosition, taskName);
			break;
		case 1:
			task.get(1).add(taskName);
			tomatoCount.put(taskName,
					TaskDataBaseManager.getTomatoCount(taskName));
			TaskDataBaseManager.addTaskToSummary(groupPosition, taskName);
			break;
		case 2:
			task.get(2).add(taskName);
			TaskDataBaseManager.addTaskToSummary(groupPosition, taskName);
			break;
		}
		TaskFragment.updateList();
	}

	public static void lifeTaskFinish(int groupPosition, int childPosition,
			long startTime, long endTime) {
		String taskName = getTaskName(groupPosition, childPosition);
		TaskDataBaseManager.lifeTaskFinish(taskName, startTime, endTime);
	}

	public static void workTaskFinish(int groupPosition, int childPosition,
			long startTime, long endTime, boolean hasTomato) {
		// 添加番茄数
		String taskName = getTaskName(groupPosition, childPosition);
		if (hasTomato)
			tomatoCount.put(taskName, tomatoCount.get(taskName) + 1);
		TaskFragment.updateList();

		TaskDataBaseManager.workTaskFinish(taskName, startTime, endTime,
				hasTomato);
	}

	public static void easyTaskFinish(int groupPosition, int childPosition,
			long endTime) {
		String taskName = task.get(groupPosition).remove(childPosition);
		TaskFragment.updateList();

		TaskDataBaseManager.easyTaskFinish(taskName, endTime);
		TaskDataBaseManager.deleteTaskFromSummary(taskName);
	}

	public static void deleteTaskFromList(int groupPosition, int childPosition) {
		String taskName = task.get(groupPosition).remove(childPosition);
		TaskFragment.updateList();

		TaskDataBaseManager.deleteTaskFromSummary(taskName);
	}

	public static String getTaskName(int groupPosition, int childPosition) {
		return task.get(groupPosition).get(childPosition);
	}

	public static String getGroupName(int groupPosition) {
		return taskTypes.get(groupPosition);
	}

	public static Integer getTomatoCount(int groupPosition, int childPosition) {
		return tomatoCount.get(getTaskName(groupPosition, childPosition));
	}

	public static Integer getTomatoCount(String taskName) {
		return tomatoCount.get(taskName);
	}

	public static boolean isTaskExist(String taskName) {
		for (List<String> list : task)
			for (String task_name : list) {
				if (taskName.equalsIgnoreCase(task_name))
					return true;
			}
		return false;
	}
}
