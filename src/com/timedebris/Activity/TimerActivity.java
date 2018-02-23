package com.timedebris.Activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timedebris.R;
import com.timedebris.View.ClockTextView;
import com.timedebris.View.ConfirmDialog;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.SystemBarTintManager;
import com.timedebris.tools.TaskDataManager;

public class TimerActivity extends Activity implements OnClickListener {
	private Vibrator vibrator;
	// 震动跳动类
	private String word = "已完成的番茄数: ";
	// 完成番茄数
	private int tomatonum = 0;
	// 倒计时后dialog的提示信息
	private String hint;
	// 判断是否工作结束
	private boolean IsWorkDone;
	// 时间线程
	private Timer timer;
	// 时间差记录
	private long time_start;
	private long time_stop;
	// 倒计时变量
	private CountTimer cd;
	// button的变量
	private Button btn_timerStart, btn_timerStop, btn_timerDone;
	// 显示时间的TextView
	private ClockTextView tv_hours, tv_minutes, tv_mills;
	// 显示工作内容跟番茄数的TextView
	private TextView tv_workTitle, tv_tomatoNum;
	// 显示时间的类型
	private int hours, minutes, mills;
	// 判断参数
	private boolean IsCountDown;
	private boolean IsButtonup;
	// 获得的是由列表传来的值
	int groupPosition, childPosition;
	// UI线程刷新时间
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 显示时间方式 like: 00:00:00
			revealTime();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_timer_main);
		init();
		// 获取由列表传来的参数
		Bundle bundle = getIntent().getExtras();
		groupPosition = bundle.getInt("groupPosition");
		childPosition = bundle.getInt("childPosition");
		// 判断开启哪种计时
		judgeTimeSort();
		IsButtonup = true;
	}

	private void initVibrator() {
		// 震动的频率，开始与结果
		long[] pattern = { 400, 600, 400, 600, 400, 600, 400, 600, 400, 600,
				400, 600 };
		vibrator.vibrate(pattern, -1);
	}

	// 退出之后的activity销毁
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (vibrator != null) {
			vibrator.cancel();
			vibrator = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (cd != null) {
			cd.cancel();
			cd = null;
		}
		System.gc();
	}

	// 实例化对象
	private void init() {
		setConditionBarColor();

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		tv_tomatoNum = (TextView) findViewById(R.id.tv_tomatosnum);
		tv_workTitle = (TextView) findViewById(R.id.tv_worktitle);
		tv_tomatoNum.setText(word + tomatonum);
		tv_hours = (ClockTextView) findViewById(R.id.tv_hours);
		tv_minutes = (ClockTextView) findViewById(R.id.tv_minutes);
		tv_mills = (ClockTextView) findViewById(R.id.tv_mills);
		btn_timerStart = (Button) findViewById(R.id.btn_startTime);
		btn_timerStop = (Button) findViewById(R.id.btn_stopTime);
		btn_timerDone = (Button) findViewById(R.id.btn_finishTime);
		btn_timerStart.setOnClickListener(this);
		btn_timerStop.setOnClickListener(this);
		btn_timerDone.setOnClickListener(this);
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

	// 判断参数来开启哪种计时
	public void judgeTimeSort() {
		switch (groupPosition) {
		// 開啟生活紀實
		case 1:
			cd = new CountTimer(1500000, 1000);
			IsCountDown = true;
			// 改换button内容
			changeButtonSystle("计时开始", btn_timerStart);
			changeButtonSystle("时间暂停", btn_timerStop);
			// 获取列表内容
			changeTaskContent();
			btn_timerStart.performClick();
			break;
		// 开启
		case 0:
			IsCountDown = false;
			changeTaskContent();
			btn_timerStart.performClick();
			break;
		// 开启小事计时
		case 2:
			IsCountDown = false;
			changeTaskContent();
			btn_timerStart.performClick();
			break;
		}
	}

	// 初始列表跟番茄数的更新
	public void changeTaskContent() {
		tv_workTitle.setText(TaskDataManager.getTaskName(groupPosition,
				childPosition));
		if (groupPosition == 1) {
			tomatonum = TaskDataManager.getTomatoCount(groupPosition,
					childPosition);
			tv_tomatoNum.setText(word + tomatonum);
		} else
			tv_tomatoNum.setText("");

	}

	// button的调用
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_startTime:
			btn_timerStart.setEnabled(false);
			btn_timerStop.setEnabled(true);
			// 判断并启动倒计时器
			if (startCountDown(IsCountDown, "工作时间结束，请好好休息", 1800000, 1000)) {
				IsWorkDone = true;
				// 改变button的内容
				changeButtonSystle("工作中", btn_timerStart);
				changeButtonSystle("休息一下", btn_timerStop);
			} else {
				openTimeTask();
				btn_timerStop.setEnabled(false);
			}
			break;
		case R.id.btn_stopTime:
			btn_timerStop.setEnabled(false);
			btn_timerStart.setEnabled(true);
			if (startCountDown(IsCountDown, "休息时间结束", 100000, 1000)) {
				IsWorkDone = false;
				changeButtonSystle("休息中", btn_timerStop);
				changeButtonSystle("继续工作", btn_timerStart);
			} else {
				timer.cancel();
			}
			break;
		case R.id.btn_finishTime:
			// 记录结束的时间
			time_stop = System.currentTimeMillis();
			TaskDataManager.workTaskFinish(groupPosition, childPosition,
					time_start, time_stop, false);
			if (!IsCountDown)
				TaskDataManager.lifeTaskFinish(groupPosition, childPosition,
						time_start, time_stop);

			finish();
			break;
		}
	}

	// 完成任务后，返回番茄数给= =
	public void givebacktomatos() {
		// TaskExpandableListAdapter.addTomato(taskName)
	}

	// //改变button的样式
	public void changeButtonSystle(String changedText, Button btn_changed) {
		btn_changed.setText(changedText);
	}

	// 启动TimerTask来启动计时
	public void openTimeTask() {
		if (timer != null)
			timer.cancel();
		timer = new Timer(true);
		// 开始计时的时间
		time_start = System.currentTimeMillis();
		TimerTask task = new TimerTask() {
			public void run() {

				recordTime();
				handler.sendEmptyMessage(1);
			}
		};
		timer.schedule(task, 0, 1000);
	}

	// 计时算法
	private void recordTime() {
		mills++;
		if (mills == 60 || mills > 60) {
			mills = 0;
			minutes++;
		}
		if (minutes == 60 || minutes > 60) {
			minutes = 0;
			hours++;
		}
	}

	// 启动倒计时的方法或是启动计时
	public boolean startCountDown(boolean IsCountDown, String timeHint,
			final int timeTotal, final int timeInterval) {
		if (IsCountDown) {
			hint = timeHint;
			if (cd != null)
				cd.cancel();
			cd = new CountTimer(timeTotal, timeInterval);
			cd.start();
			time_start = System.currentTimeMillis();
		}
		return IsCountDown;
	}

	// 显示时间的细节方法
	public void revealTime() {
		String hh = (hours < 10) ? "0" : "";
		tv_hours.setText(hh + hours + ":");
		String mm = (minutes < 10) ? "0" : "";
		tv_minutes.setText(mm + minutes + ":");
		String ss = (mills < 10) ? "0" : "";
		tv_mills.setText(ss + mills + "");
	}

	// 倒计时的内部类
	class CountTimer extends CountDownTimer {

		public CountTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		// 当倒计时结束时，调用finish()
		@Override
		public void onFinish() {
			tomatonum++;
			time_stop = System.currentTimeMillis();
			TaskDataManager.workTaskFinish(groupPosition, childPosition,
					time_start, time_stop, true);
			tv_tomatoNum.setText(word + tomatonum);

			// 开启震动
			initVibrator();
			// 创建自定义dialog，告诉用户时间结束
			final ConfirmDialog dialog = new ConfirmDialog(TimerActivity.this,
					"提示", hint, "确定");
			dialog.show();
			dialog.setClickListener(new ConfirmDialog.ClickListenerInterface() {
				@Override
				public void doConfirm() {
					if (IsWorkDone) {
						// 番茄数增加
						IsWorkDone = false;
						btn_timerStop.performClick();
					} else {
						// 自动触发事件
						btn_timerStart.performClick();
						initVibrator();
					}
					vibrator.cancel();

					dialog.dismiss();
				}
			});
		}

		// 倒计时算法
		@Override
		public void onTick(long timeLast) {
			minutes = (int) ((timeLast / 1000) / 60);
			mills = (int) ((timeLast / 1000) % 60);
			handler.sendEmptyMessage(1);
		}
	}
}
