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
import com.timedebris.tools.DateTimeTransformer;
import com.timedebris.tools.DetaiWeeklyData;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.TaskDataBaseManager;

public class StatisticWeekExpandableListAdapter extends
		BaseExpandableListAdapter {

	private Context mContext;
	private List<String> weekGroup;
	private List<List<DetaiWeeklyData>> weekList;

	private List<Integer> taskTypeIcons = new ArrayList<Integer>(3);

	public StatisticWeekExpandableListAdapter(Context context) {
		taskTypeIcons = Arrays.asList(new Integer[] { R.drawable.task_type_1,
				R.drawable.task_type_2, R.drawable.task_type_3 });

		mContext = context;
		Map<Integer, Object> packMap = TaskDataBaseManager.getWeekDetail();
		weekGroup = (List<String>) packMap.get(0);
		weekList = (List<List<DetaiWeeklyData>>) packMap.get(1);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return weekList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return weekList.get(groupPosition).size();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.statistic_detail_week_children,
					null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (54 * DisplayHelper.density()));
			view.setLayoutParams(params);
		}
		ImageView icon = (ImageView) view.findViewById(R.id.week_children_icon);
		TextView taskName = (TextView) view
				.findViewById(R.id.week_children_taskname);
		TextView count = (TextView) view.findViewById(R.id.week_children_count);

		DetaiWeeklyData data = weekList.get(groupPosition).get(childPosition);
		icon.setImageResource(taskTypeIcons.get(data.type));
		taskName.setText(data.name);
		switch (data.type) {
		case 0:
			count.setText(DateTimeTransformer
					.getintervalStringFromLong(data.intervalOrCount));
			break;
		case 1:
			count.setText(data.intervalOrCount + "个番茄");
			break;
		case 2:
			count.setText("完成" + data.intervalOrCount + "次");
			break;
		}

		return view;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return weekGroup.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return weekGroup.size();
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
			view = inflater
					.inflate(R.layout.statistic_detail_week_parent, null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (54 * DisplayHelper.density()));
			view.setLayoutParams(params);
		}
		TextView date = (TextView) view.findViewById(R.id.week_parent_date);
		date.setText(weekGroup.get(groupPosition));
		TextView intervalOrCount = (TextView) view
				.findViewById(R.id.week_parent_taskcount);
		intervalOrCount.setText(weekList.get(groupPosition).size() + "项");

		ImageView image = (ImageView) view.findViewById(R.id.week_marker);
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
