package com.timedebris.Fragment;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timedebris.R;
import com.timedebris.Activity.RecentRecordListActivity;
import com.timedebris.View.DataCellView;
import com.timedebris.View.EvaluateLayout;
import com.timedebris.View.HorizontalListView;
import com.timedebris.tools.DateTimeTransformer;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.NumberHelper;
import com.timedebris.tools.TaskDataBaseManager;

public class StatisticFragment extends Fragment {
	int totalDay, totalWeek, totalMonth;
	int monthDay;
	boolean monthOk = false, dayOk = false;
	int[] tomatoDay, tomatoWeek, tomatoMonth;
	int[] thingsDay, thingsWeek, thingsMonth;
	String[] dateDay, dateWeek, dateMonth;
	int[] countWeek, countMonth;
	private int widDay, widWeek, widMonth;
	private TextView tomatoTip, worktimeTip, triviaTip;
	private TextView tomato, worktime, trivia;
	private EvaluateLayout evaluation;
	private DataCellView lastDataCellView;
	private DataCellView dayLeftBorderView, dayRightBorderView,
			weekLeftBorderView, weekRightBorderView, monthLeftBorderView,
			monthRightBorderView;
	private Button add, reduce;
	private Button showRecent;
	private boolean day = true, week = false, month = false;
	private int dayPosition, weekPosition, monthPosition;
	private float centerDay, centerWeek, centerMonth;

	static Context mContext;
	static Handler handler;

	public static StatisticFragment newInstance(Context context) {
		mContext = context;

		return new StatisticFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		LinearLayout rootContainer = (LinearLayout) inflater.inflate(
				R.layout.statistic_fragment, null);

		float width = DisplayHelper.width();
		widDay = (int) (width / 11);
		widWeek = (int) (width / 7);
		widMonth = (int) (width / 5);
		centerDay = widDay * 5;
		centerWeek = widWeek * 3;
		centerMonth = widMonth * 2;

		Map<Integer, Object> resultDay = TaskDataBaseManager.selectDailyData();
		tomatoDay = (int[]) resultDay.get(0);
		thingsDay = (int[]) resultDay.get(1);
		dateDay = (String[]) resultDay.get(2);
		totalDay = tomatoDay.length;

		Map<Integer, Object> resultWeek = TaskDataBaseManager.selectWeekData();
		tomatoWeek = (int[]) resultWeek.get(0);
		thingsWeek = (int[]) resultWeek.get(1);
		dateWeek = (String[]) resultWeek.get(2);
		countWeek = (int[]) resultWeek.get(3);
		totalWeek = tomatoWeek.length;

		Map<Integer, Object> resultMonth = TaskDataBaseManager
				.selectMonthData();
		tomatoMonth = (int[]) resultMonth.get(0);
		thingsMonth = (int[]) resultMonth.get(1);
		dateMonth = (String[]) resultMonth.get(2);
		countMonth = (int[]) resultMonth.get(3);
		totalMonth = tomatoMonth.length;

		dayPosition = totalDay - 6;
		weekPosition = totalWeek - 4;
		monthPosition = totalMonth - 3;

		add = (Button) rootContainer.findViewById(R.id.add);
		reduce = (Button) rootContainer.findViewById(R.id.reduce);
		showRecent = (Button) rootContainer.findViewById(R.id.recentList);
		final Animation anim2 = AnimationUtils.loadAnimation(mContext,
				R.anim.anim2);
		anim2.setFillAfter(true);

		final HorizontalListView listView = (HorizontalListView) rootContainer
				.findViewById(R.id.listview);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0x111);
			}
		}, 3000);

		tomatoTip = (TextView) rootContainer.findViewById(R.id.tomatoTip);
		worktimeTip = (TextView) rootContainer.findViewById(R.id.worktimeTip);
		triviaTip = (TextView) rootContainer.findViewById(R.id.triviaTip);

		tomato = (TextView) rootContainer.findViewById(R.id.tomato);
		worktime = (TextView) rootContainer.findViewById(R.id.worktime);
		trivia = (TextView) rootContainer.findViewById(R.id.trivia);
		evaluation = (EvaluateLayout) rootContainer
				.findViewById(R.id.evaluation);

		tomato.setText((tomatoDay[totalDay - 6]) + "只");
		worktime.setText(NumberHelper.getAccurateAverage(
				tomatoDay[totalDay - 6] / 2.0d, 1) + "小时");
		trivia.setText(thingsDay[totalDay - 6] + "件");
		evaluation.setScoreSum(tomatoDay[totalDay - 6]
				+ thingsDay[totalDay - 6] / 3.0d);

		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 0x111) {
					listView.setAdapter(dayAdapter);
					listView.scrollTo(dayPosition * (int) widDay
							- (int) centerDay, 0, true);
				}

				if (msg.what == 0x123) {
					listView.startAnimation(anim2);
					listView.setAdapter(weekAdapter);
					day = false;
					week = true;
					weekPosition = TaskDataBaseManager
							.getWeekFromDaily(dateDay[dayPosition]);
					listView.scrollTo(weekPosition * (int) widWeek
							- (int) centerWeek, 1, true);
					int position = weekPosition;
					tomato.setText(NumberHelper.getAccurateAverage(
							((double) tomatoWeek[position])
									/ countWeek[position], 1)
							+ "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							((double) tomatoWeek[position])
									/ countWeek[position] / 2, 1)
							+ "小时");
					trivia.setText(NumberHelper.getAccurateAverage(
							((double) thingsWeek[position])
									/ countWeek[position], 1)
							+ "件");
					evaluation
							.setScoreSum((tomatoWeek[position] + thingsWeek[position] / 3.0d)
									/ countWeek[position]);

					tomatoTip.setText("日均番茄数");
					worktimeTip.setText("日均工作时间");
					triviaTip.setText("日均琐事数");
				}
				if (msg.what == 0x124) {
					listView.startAnimation(anim2);
					listView.setAdapter(dayAdapter);
					day = true;
					week = false;
					dayPosition = TaskDataBaseManager
							.getDailyFromWeek(dateWeek[weekPosition]);
					listView.scrollTo(dayPosition * (int) widDay
							- (int) centerDay, 0, true);
					int position = dayPosition;
					tomato.setText((tomatoDay[position]) + "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							tomatoDay[position] / 2.0d, 1) + "小时");
					trivia.setText(thingsDay[position] + "件");
					evaluation.setScoreSum(tomatoDay[position]
							+ thingsDay[position] / 3.0d);

					tomatoTip.setText("全天番茄数");
					worktimeTip.setText("全天工作时间");
					triviaTip.setText("全天琐事数");
				}
				if (msg.what == 0x125) {
					listView.startAnimation(anim2);
					listView.setAdapter(monthAdapter);
					week = false;
					month = true;
					monthPosition = TaskDataBaseManager
							.getMonthFromWeek(dateWeek[weekPosition]);
					listView.scrollTo(monthPosition * (int) widMonth
							- (int) centerMonth, 2, true);
					int position = monthPosition;
					tomato.setText(NumberHelper.getAccurateAverage(
							((double) tomatoMonth[position])
									/ countMonth[position], 1)
							+ "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							((double) tomatoMonth[position])
									/ countMonth[position] / 2, 1)
							+ "小时");
					trivia.setText(NumberHelper.getAccurateAverage(
							((double) thingsMonth[position])
									/ countMonth[position], 1)
							+ "件");
					evaluation
							.setScoreSum((tomatoMonth[position] + thingsMonth[position] / 3.0d)
									/ countMonth[position]);
				}
				if (msg.what == 0x126) {
					listView.startAnimation(anim2);
					listView.setAdapter(weekAdapter);
					month = false;
					week = true;
					weekPosition = TaskDataBaseManager
							.getWeekFromMonth(dateMonth[monthPosition]);
					listView.scrollTo(weekPosition * (int) widWeek
							- (int) centerWeek, 1, true);
					int position = weekPosition;
					tomato.setText(NumberHelper.getAccurateAverage(
							((double) tomatoWeek[position])
									/ countWeek[position], 1)
							+ "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							((double) tomatoWeek[position])
									/ countWeek[position] / 2, 1)
							+ "小时");
					trivia.setText(NumberHelper.getAccurateAverage(
							((double) thingsWeek[position])
									/ countWeek[position], 1)
							+ "件");
					evaluation
							.setScoreSum((tomatoWeek[position] + thingsWeek[position] / 3.0d)
									/ countWeek[position]);
				}
			}
		};

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (day) {
					if (lastDataCellView != null) {
						lastDataCellView.setSelectCondition(false);
					}

					int nowPosition = position;
					if (nowPosition < 5) {
						nowPosition = 5;
						lastDataCellView = dayLeftBorderView;
					} else if (nowPosition > totalDay - 6) {
						nowPosition = totalDay - 6;
						lastDataCellView = dayRightBorderView;
					} else {
						lastDataCellView = (DataCellView) view;
					}
					lastDataCellView.setSelectCondition(true);
					int pos = nowPosition * (int) widDay - (int) centerDay;
					if (pos < 0)
						pos = 0;
					listView.scrollTo(pos, 0, false);

					tomato.setText((tomatoDay[nowPosition]) + "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							tomatoDay[nowPosition] / 2.0d, 1) + "小时");
					trivia.setText(thingsDay[nowPosition] + "件");
					evaluation.setScoreSum(tomatoDay[nowPosition]
							+ thingsDay[nowPosition] / 3.0d);
					dayPosition = nowPosition;
				}
				if (week) {
					if (lastDataCellView != null) {
						lastDataCellView.setSelectCondition(false);
					}
					int nowPosition = position;
					if (nowPosition < 3) {
						nowPosition = 3;
						lastDataCellView = weekLeftBorderView;
					} else if (nowPosition > totalWeek - 4) {
						nowPosition = totalWeek - 4;
						lastDataCellView = weekRightBorderView;
					} else {
						lastDataCellView = (DataCellView) view;
					}
					lastDataCellView.setSelectCondition(true);

					int pos = nowPosition * (int) widWeek - (int) centerWeek;
					if (pos < 0)
						pos = 0;
					listView.scrollTo(pos, 1, false);

					tomato.setText(NumberHelper.getAccurateAverage(
							((double) tomatoWeek[nowPosition])
									/ countWeek[nowPosition], 1)
							+ "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							((double) tomatoWeek[nowPosition])
									/ countWeek[nowPosition] / 2, 1)
							+ "小时");
					trivia.setText(NumberHelper.getAccurateAverage(
							((double) thingsWeek[nowPosition])
									/ countWeek[nowPosition], 1)
							+ "件");
					evaluation
							.setScoreSum((tomatoWeek[nowPosition] + thingsWeek[nowPosition] / 3.0d)
									/ countWeek[nowPosition]);
					weekPosition = nowPosition;
				}
				if (month) {
					if (lastDataCellView != null) {
						lastDataCellView.setSelectCondition(false);
					}
					int nowPosition = position;
					if (nowPosition < 2) {
						nowPosition = 2;
						lastDataCellView = monthLeftBorderView;
					} else if (nowPosition > totalMonth - 3) {
						nowPosition = totalMonth - 3;
						lastDataCellView = monthRightBorderView;
					} else {
						lastDataCellView = (DataCellView) view;
					}
					lastDataCellView.setSelectCondition(true);

					int pos = nowPosition * (int) widMonth - (int) centerMonth;
					if (pos < 0)
						pos = 0;
					listView.scrollTo(pos, 2, false);

					tomato.setText(NumberHelper.getAccurateAverage(
							((double) tomatoMonth[nowPosition])
									/ countMonth[nowPosition], 1)
							+ "只");
					worktime.setText(NumberHelper.getAccurateAverage(
							((double) tomatoMonth[nowPosition])
									/ countMonth[nowPosition] / 2, 1)
							+ "小时");
					trivia.setText(NumberHelper.getAccurateAverage(
							((double) thingsMonth[nowPosition])
									/ countMonth[nowPosition], 1)
							+ "件");
					evaluation
							.setScoreSum((tomatoMonth[nowPosition] + thingsMonth[nowPosition] / 3.0d)
									/ countMonth[nowPosition]);
					monthPosition = nowPosition;
				}
			}
		});

		showRecent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						RecentRecordListActivity.class);
				startActivity(intent);
			}
		});

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (dayOk)
					return;
				if (week) {
					dayOk = true;
					add.setBackgroundResource(R.drawable.addchange);
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(0x124);
						}

					}, 0);
				}
				if (month) {
					monthOk = false;
					reduce.setBackgroundResource(R.drawable.reduce);
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(0x126);
						}

					}, 0);
				}
			}

		});

		reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (monthOk)
					return;
				if (day) {
					dayOk = false;
					add.setBackgroundResource(R.drawable.add);
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(0x123);
						}

					}, 0);
				}
				if (week) {
					monthOk = true;
					reduce.setBackgroundResource(R.drawable.reducechange);
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() {
							handler.sendEmptyMessage(0x125);
						}

					}, 0);
				}
			}

		});

		return rootContainer;
	}

	private BaseAdapter monthAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return totalMonth;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DataCellView dcView;

			if (convertView != null) {
				dcView = (DataCellView) convertView;
				dcView.setParams(
						tomatoMonth[position],
						thingsMonth[position],
						DateTimeTransformer.getMonthString(dateMonth[position]),
						position == monthPosition, 2);
			} else {
				dcView = new DataCellView(
						mContext,
						tomatoMonth[position],
						thingsMonth[position],
						DateTimeTransformer.getMonthString(dateMonth[position]),
						position == monthPosition, 2);
			}

			if (position == monthPosition) {
				lastDataCellView = dcView;
			}
			if (position == 2)
				monthLeftBorderView = dcView;
			if (position == totalMonth - 3)
				monthRightBorderView = dcView;

			return dcView;
		}

	};

	private BaseAdapter weekAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return totalWeek;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DataCellView dcView;

			if (convertView != null) {
				dcView = (DataCellView) convertView;
				dcView.setParams(tomatoWeek[position], thingsWeek[position],
						DateTimeTransformer.getWeekString(dateWeek[position]),
						position == weekPosition, 1);
			} else {
				dcView = new DataCellView(mContext, tomatoWeek[position],
						thingsWeek[position],
						DateTimeTransformer.getWeekString(dateWeek[position]),
						position == weekPosition, 1);
			}
			if (position == weekPosition)
				lastDataCellView = dcView;
			if (position == 3)
				weekLeftBorderView = dcView;
			if (position == totalWeek - 4)
				weekRightBorderView = dcView;

			return dcView;
		}

	};

	private BaseAdapter dayAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return totalDay;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DataCellView dcView;

			if (convertView != null) {
				dcView = (DataCellView) convertView;
				dcView.setParams(tomatoDay[position], thingsDay[position],
						DateTimeTransformer.getDayString(dateDay[position]),
						position == dayPosition, 0);
			} else {
				dcView = new DataCellView(mContext, tomatoDay[position],
						thingsDay[position],
						DateTimeTransformer.getDayString(dateDay[position]),
						position == dayPosition, 0);
			}
			if (position == dayPosition)
				lastDataCellView = dcView;
			if (position == 5) {
				dayLeftBorderView = dcView;
			}
			if (position == totalDay - 6) {
				dayRightBorderView = dcView;
			}

			return dcView;
		}

	};

}
