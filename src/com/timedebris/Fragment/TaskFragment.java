package com.timedebris.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timedebris.R;
import com.timedebris.Activity.TimerActivity;
import com.timedebris.Adapter.ImagePagerAdapter;
import com.timedebris.Adapter.TaskExpandableListAdapter;
import com.timedebris.View.AutoScrollViewPager;
import com.timedebris.View.ViewPagerPointer;
import com.timedebris.tools.AnimationHelper;
import com.timedebris.tools.FadeOutPageTransformer;
import com.timedebris.tools.TaskDataBaseManager;
import com.timedebris.tools.TaskDataManager;

public class TaskFragment extends Fragment {
	public static final int autoScrollTime = 5000;
	public static final double scrollSpeedRate = 4.00f;

	private static ExpandableListView expandList;
	private static TaskExpandableListAdapter listAdapter;
	private String[] operations_1plus2 = new String[] { "开始任务", "从列表中删除" };
	private String[] operations_3 = new String[] { "完成琐事", "拖下去斩了~" };

	private Button btnAddTask;
	private Button btn1, btn2, btn3;
	private LinearLayout btnContainer;
	private RelativeLayout rootView;

	private int[] tipIcons = new int[] { R.drawable.evaluate_no_1,
			R.drawable.evaluate_no_2, R.drawable.evaluate_no_3,
			R.drawable.evaluate_no_4 };
	private String[] tips = new String[] { "番茄总会有的  <(￣︶￣)/ ",
			"听麻麻说：人丑就要多读书  O.O", "做爱做的事，交配交的人  ㄟ(▔▽▔ㄟ) ",
			"人不知而不愠，不亦绅士乎？ (*▔＾▔*)" };

	private static Context mContext;

	private static Animation anim, anim_reverse;
	private static Animation anim_1, anim_1_reverse;
	private static Animation anim_2, anim_2_reverse;
	private static Animation anim_3, anim_3_reverse;

	private boolean isShowButtons = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				btnContainer.setVisibility(View.GONE);
			}
		};
	};

	public static TaskFragment newInstance(Context context) {
		mContext = context;

		anim = AnimationHelper.getAnimation(mContext,
				R.anim.rotate_task_add_btn);
		anim_reverse = AnimationHelper.getAnimation(mContext,
				R.anim.rotate_task_add_btn_reverse);

		anim_1 = AnimationHelper.getAnimation(mContext,
				R.anim.translate_task_add_btn_1);
		anim_2 = AnimationHelper.getAnimation(mContext,
				R.anim.translate_task_add_btn_2);
		anim_3 = AnimationHelper.getAnimation(mContext,
				R.anim.translate_task_add_btn_3);
		anim_1_reverse = AnimationHelper.getAnimation(mContext,
				R.anim.translate_task_add_btn_1_reverse);
		anim_2_reverse = AnimationHelper.getAnimation(mContext,
				R.anim.translate_task_add_btn_2_reverse);
		anim_3_reverse = AnimationHelper.getAnimation(mContext,
				R.anim.translate_task_add_btn_3_reverse);

		TaskDataBaseManager.createDateBaseInstance(mContext);

		return new TaskFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (RelativeLayout) inflater.inflate(R.layout.task_fragment,
				container, false);

		LinearLayout tipView = (LinearLayout) inflater.inflate(
				R.layout.footer_tip_view, null);
		int rand = new Random().nextInt(4);
		((ImageView) tipView.findViewById(R.id.tasklist_tip_icon))
				.setImageResource(tipIcons[rand]);
		((TextView) tipView.findViewById(R.id.tasklist_tip))
				.setText(tips[rand]);
		listAdapter = new TaskExpandableListAdapter(mContext);

		expandList = (ExpandableListView) rootView.findViewById(R.id.task_list);
		expandList.setGroupIndicator(null);
		expandList.addFooterView(tipView, null, false);
		expandList.setAdapter(listAdapter);
		expandList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String taskName = TaskDataManager.getTaskName(groupPosition,
						childPosition);
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
						.setTitle(taskName).setAdapter(
								getDialogListAdapter(groupPosition),
								new DialogClickListener(groupPosition,
										childPosition));
				builder.create().show();

				return false;
			}
		});

		btnAddTask = (Button) rootView.findViewById(R.id.btn_add_task_summary);
		btnAddTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isShowButtons) {
					btnGroupFadeIn();
				} else {
					btnGroupFadeOut();
				}
			}
		});

		btnContainer = (LinearLayout) rootView
				.findViewById(R.id.btn_add_task_containter);
		btnContainer.setVisibility(View.GONE);
		btn1 = (Button) rootView.findViewById(R.id.btn_add_task_life);
		btn1.setOnClickListener(new MyButtonAddTaskClickListener());
		btn2 = (Button) rootView.findViewById(R.id.btn_add_task_work);
		btn2.setOnClickListener(new MyButtonAddTaskClickListener());
		btn3 = (Button) rootView.findViewById(R.id.btn_add_task_easytask);
		btn3.setOnClickListener(new MyButtonAddTaskClickListener());

		return rootView;
	}

	private ArrayAdapter<String> getDialogListAdapter(int type) {
		assert (type >= 0 && type <= 2);

		String[] operations = null;
		switch (type) {
		case 0:
		case 1:
			operations = operations_1plus2;
			break;
		case 2:
			operations = operations_3;
			break;
		}

		return new ArrayAdapter<String>(mContext, R.layout.task_click_dialog,
				R.id.operation, operations);
	}

	private class DialogClickListener implements
			DialogInterface.OnClickListener {
		private int groupPosition;
		private int childPosition;

		public DialogClickListener(int groupPosition, int childPosition) {
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (groupPosition == 0 || groupPosition == 1) {
				if (which == 0)
					startTask();
				else if (which == 1)
					TaskDataManager.deleteTaskFromList(groupPosition,
							childPosition);
			} else {
				if (which == 0)
					TaskDataManager.easyTaskFinish(2, childPosition,
							System.currentTimeMillis());
				else if (which == 1)
					TaskDataManager.deleteTaskFromList(2, childPosition);
			}
		}

		private void startTask() {
			Intent intent = new Intent(mContext, TimerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("groupPosition", groupPosition);
			bundle.putInt("childPosition", childPosition);
			intent.putExtras(bundle);
			startActivity(intent);
		}

	}

	private class MyButtonAddTaskClickListener implements View.OnClickListener {
		int groupPosition = 0;

		@Override
		public void onClick(View v) {
			String title = null;

			switch (v.getId()) {
			case R.id.btn_add_task_life:
				title = "任务名称 - 生活";
				groupPosition = 0;
				break;
			case R.id.btn_add_task_work:
				title = "任务名称 - 工作";
				groupPosition = 1;
				break;
			case R.id.btn_add_task_easytask:
				title = "任务名称 - 琐事";
				groupPosition = 2;
				break;
			}
			btnGroupFadeOut();

			LinearLayout dialogLayout = (LinearLayout) View.inflate(mContext,
					R.layout.task_add_dialog_layout, null);
			final AlertDialog dialog = new AlertDialog.Builder(mContext)
					.setTitle(title).setView(dialogLayout).setCancelable(false)
					.create();
			final EditText et = (EditText) dialogLayout
					.findViewById(R.id.nameInput);
			final TextView tipView = (TextView) dialogLayout
					.findViewById(R.id.tvTip);

			lengthFilter(mContext, et, 20, "任务名称已超过最大长度 Orz~", tipView);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					et.requestFocus();
					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, 100);

			et.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					tipView.setVisibility(View.GONE);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			Button btnAdd = (Button) dialogLayout.findViewById(R.id.btnAdd);
			btnAdd.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					String taskName = et.getText().toString().trim();
					String resultCode;
					if (!(resultCode = checkInput(taskName))
							.equalsIgnoreCase("")) {
						tipView.setText(resultCode);
						tipView.setVisibility(View.VISIBLE);
						return;
					} else {
						TaskDataManager.addTask(groupPosition, taskName);

						dialog.dismiss();
						Toast.makeText(mContext, "任务创建成功~", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
			Button btnCancel = (Button) dialogLayout
					.findViewById(R.id.btnCancel);
			btnCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
	}

	private String checkInput(String nameInput) {
		if (nameInput.equalsIgnoreCase(""))
			return "任务名称不能为空";
		else if (TaskDataManager.isTaskExist(nameInput))
			return "同名任务已存在";
		else
			return "";
	}

	private void lengthFilter(final Context context, final EditText editText,
			final int max_length, final String tipMsg, final TextView tipView) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(max_length) {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				int destLen = getCharacterNum(dest.toString()); // 获取字符个数(一个中文算2个字符)
				int sourceLen = getCharacterNum(source.toString());
				if (destLen + sourceLen > max_length) {
					tipView.setText(tipMsg);
					tipView.setVisibility(View.VISIBLE);
					return "";
				}
				return source;
			}
		};
		editText.setFilters(filters);
	}

	private int getCharacterNum(final String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	private int getChineseNum(String s) {
		int num = 0;
		char[] myChar = s.toCharArray();

		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;
	}

	private void btnGroupFadeIn() {
		isShowButtons = true;

		btnContainer.setVisibility(View.VISIBLE);
		mHandler.removeMessages(0x123);

		btnAddTask.startAnimation(anim);
		btn1.startAnimation(anim_1);
		btn2.startAnimation(anim_2);
		btn3.startAnimation(anim_3);
	}

	public void btnGroupFadeOut() {
		isShowButtons = false;

		btnAddTask.startAnimation(anim_reverse);
		btn1.startAnimation(anim_1_reverse);
		btn2.startAnimation(anim_2_reverse);
		btn3.startAnimation(anim_3_reverse);

		mHandler.sendEmptyMessageDelayed(0x123, 250);
	}

	public boolean isShowButton() {
		return isShowButtons;
	}

	public static void updateList() {
		listAdapter.notifyDataSetChanged();
	}
}
