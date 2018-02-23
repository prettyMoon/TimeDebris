package com.timedebris.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.timedebris.R;
import com.timedebris.Adapter.StatisticDailyExpandableListAdapter;
import com.timedebris.Adapter.StatisticWeekExpandableListAdapter;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.SystemBarTintManager;

public class RecentRecordListActivity extends Activity {

	private Button btnMode;
	private ImageButton btnBack;
	private int mode = 0;
	String[] operations = new String[] { "近期纪录", "两周内纪录" };

	private ExpandableListView expandList;
	private StatisticDailyExpandableListAdapter dailyAdapter;
	private StatisticWeekExpandableListAdapter weekAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.statistic_detail_layout);

		setConditionBarColor();

		dailyAdapter = new StatisticDailyExpandableListAdapter(this);

		expandList = (ExpandableListView) findViewById(R.id.task_detail_list);
		expandList.setAdapter(dailyAdapter);
		expandList.setGroupIndicator(null);

		btnMode = (Button) findViewById(R.id.btnMode);
		btnMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						RecentRecordListActivity.this).setAdapter(
						getDialogListAdapter(), new DialogClickListener())
						.setInverseBackgroundForced(true);
				builder.create().show();
			}
		});

		btnBack = (ImageButton) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private ArrayAdapter<String> getDialogListAdapter() {
		return new ArrayAdapter<String>(this, R.layout.task_click_dialog,
				R.id.operation, operations);
	}

	private class DialogClickListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == 0) {
				if (mode == 0)
					return;
				mode = 0;
				btnMode.setText(operations[mode]);
				expandList.setAdapter(dailyAdapter);
			} else if (which == 1) {
				if (mode == 1)
					return;
				if (weekAdapter == null)
					weekAdapter = new StatisticWeekExpandableListAdapter(
							RecentRecordListActivity.this);
				mode = 1;
				btnMode.setText(operations[mode]);
				expandList.setAdapter(weekAdapter);
			}
		}
	}

	private void setConditionBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);

			fitsStatusBarHeight();

			// 创建状态栏的管理实例
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			// 激活导航栏设置
			tintManager.setNavigationBarTintEnabled(true);
			// 激活状态栏设置
			tintManager.setStatusBarTintEnabled(true);
			// 加载你的actionbar的颜色或者背景图
			tintManager.setStatusBarAlpha(0f);
		}
	}

	protected void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private void fitsStatusBarHeight() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.fitStatusBar);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, DisplayHelper
						.getStatusBarHeight(this)));
	}
}
