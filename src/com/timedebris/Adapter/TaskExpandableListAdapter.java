package com.timedebris.Adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timedebris.R;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.TaskDataManager;

public class TaskExpandableListAdapter extends BaseExpandableListAdapter {

	private Context mContext;

	private List<Integer> taskTypeIcons = new ArrayList<Integer>(3);

	public TaskExpandableListAdapter(Context context) {
		taskTypeIcons = Arrays.asList(new Integer[] { R.drawable.task_type_1,
				R.drawable.task_type_2, R.drawable.task_type_3 });

		mContext = context;
		TaskDataManager.updateData();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return TaskDataManager.task.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return TaskDataManager.task.get(groupPosition).size();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.tasklist_children_layout, null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (54 * DisplayHelper.density()));
			view.setLayoutParams(params);
		}
		final TextView title = (TextView) view
				.findViewById(R.id.children_title);
		title.setText(TaskDataManager.task.get(groupPosition)
				.get(childPosition).toString());
		final TextView info = (TextView) view.findViewById(R.id.children_info);
		if (groupPosition != 1)
			info.setText("");
		else {
			int tomatoCount = TaskDataManager.getTomatoCount(groupPosition,
					childPosition);
			info.setText(tomatoCount == 0 ? "木有番茄" : tomatoCount + "只番茄");
		}
		return view;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return TaskDataManager.taskTypes.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return TaskDataManager.taskTypes.size();
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
			view = inflater.inflate(R.layout.tasklist_parent_layout, null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (54 * DisplayHelper.density()));
			view.setLayoutParams(params);
		}
		ImageView icon = (ImageView) view.findViewById(R.id.parent_icon);
		icon.setImageResource(taskTypeIcons.get(groupPosition));

		TextView title = (TextView) view.findViewById(R.id.parent_title);
		title.setText(getGroup(groupPosition).toString());
		TextView info = (TextView) view.findViewById(R.id.parent_info);
		info.setText(TaskDataManager.task.get(groupPosition).size() + "项");

		ImageView image = (ImageView) view.findViewById(R.id.marker);
		// 判断实例可以展开，如果可以则改变右侧的图标
		if (isExpanded)
			image.setBackgroundResource(R.drawable.marker2);
		else
			image.setBackgroundResource(R.drawable.marker1);

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
