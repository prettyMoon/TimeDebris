package com.timedebris.Adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timedebris.R;
import com.timedebris.tools.DetaiDailyData;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.TaskDataBaseManager;

public class StatisticDailyExpandableListAdapter extends
		BaseExpandableListAdapter {

	private Context mContext;
	private List<String> dateList;
	private List<List<DetaiDailyData>> dailyList;

	private List<Integer> taskTypeIcons = new ArrayList<Integer>(3);

	public StatisticDailyExpandableListAdapter(Context context) {
		mContext = context;

		taskTypeIcons = Arrays.asList(new Integer[] { R.drawable.task_type_1,
				R.drawable.task_type_2, R.drawable.task_type_3 });

		Map<Integer, Object> packMap = TaskDataBaseManager.getDailyDetail();
		dailyList = (List<List<DetaiDailyData>>) packMap.get(1);

		List<String> tempDateList = (List<String>) packMap.get(0);
		dateList = new ArrayList<String>();
		dateList.add(0, "今天" + "(" + tempDateList.get(0) + ")");
		dateList.add(1, "昨天" + "(" + tempDateList.get(1) + ")");
		dateList.add(2, "前天" + "(" + tempDateList.get(2) + ")");
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return dailyList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return dailyList.get(groupPosition).size();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.statistic_detail_daily_children,
					null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (54 * DisplayHelper.density()));
			view.setLayoutParams(params);
		}
		ImageView icon = (ImageView) view
				.findViewById(R.id.daily_children_icon);
		TextView taskName = (TextView) view
				.findViewById(R.id.daily_children_taskname);
		TextView time = (TextView) view.findViewById(R.id.daily_children_time);
		TextView interval = (TextView) view
				.findViewById(R.id.daily_children_interval);

		DetaiDailyData data = dailyList.get(groupPosition).get(childPosition);
		icon.setImageResource(taskTypeIcons.get(data.type));
		taskName.setText(data.name);
		time.setText(data.dateStart.toString().substring(11, 16) + "  ~  "
				+ data.dateEnd.toString().substring(11, 16));
		interval.setText(data.interval);

		return view;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return dateList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return dateList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.statistic_detail_daily_parent,
					null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (54 * DisplayHelper.density()));
			view.setLayoutParams(params);
		}
		TextView title = (TextView) view.findViewById(R.id.daily_parent_date);
		title.setText(dateList.get(groupPosition));
		TextView info = (TextView) view
				.findViewById(R.id.daily_parent_taskcount);
		info.setText(dailyList.get(groupPosition).size() + "项");

		ImageView image = (ImageView) view.findViewById(R.id.daily_marker);
		// 判断实例可以展开，如果可以则改变右侧的图标
		if (isExpanded)
			image.setBackgroundResource(R.drawable.marker2);
		else
			image.setBackgroundResource(R.drawable.marker1);

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
