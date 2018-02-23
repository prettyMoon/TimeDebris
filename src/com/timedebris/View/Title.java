package com.timedebris.View;

import com.timedebris.Activity.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Title extends LinearLayout {

	final int paddingLeftOut = 30, paddingRightOut = 30;
	final int paddingTop = 6, paddingBottom = 8;
	final int paddingCenter = 36;
	final int textSize = 18;

	TextView tv0, tv1, tv2;
	TextView[] textViews;

	Context mContext;
	MainActivity mMain;

	public Title(Context context, String[] str_title) {
		super(context);
		mContext = context;
		mMain = (MainActivity) context;

		tv0 = new TextView(context);
		tv1 = new TextView(context);
		tv2 = new TextView(context);

		textViews = new TextView[3];
		textViews[0] = tv0;
		textViews[1] = tv1;
		textViews[2] = tv2;

		tv0.setText(str_title[0]);
		tv1.setText(str_title[1]);
		tv2.setText(str_title[2]);

		tv0.setTextSize(textSize);
		tv1.setTextSize(textSize);
		tv2.setTextSize(textSize);
		tv0.setTextColor(MainActivity.color_title_unselected);
		tv1.setTextColor(MainActivity.color_title_unselected);
		tv2.setTextColor(MainActivity.color_title_unselected);

		tv0.setPadding(paddingCenter, paddingTop, paddingCenter, paddingBottom);
		tv1.setPadding(paddingCenter, paddingTop, paddingCenter, paddingBottom);
		tv2.setPadding(paddingCenter, paddingTop, paddingCenter, paddingBottom);

		setPadding(paddingLeftOut, 0, paddingRightOut, 0);

		addView(tv0);
		addView(tv1);
		addView(tv2);

		tv0.setTag(0);
		tv1.setTag(1);
		tv2.setTag(2);

		TitleOnClickListener listener = new TitleOnClickListener();
		tv0.setOnClickListener(listener);
		tv1.setOnClickListener(listener);
		tv2.setOnClickListener(listener);
	}

	public void changePage(int currentPage, int position) {
		// if used to init, then let currentPage = position~
		textViews[currentPage]
				.setTextColor(MainActivity.color_title_unselected);
		textViews[position].setTextColor(MainActivity.color_title_selected);
	}

	class TitleOnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();

			if (MainActivity.currentPage != position) {
				Title.this.changePage(MainActivity.currentPage, position);
				mMain.changePage(position);
				MainActivity.currentPage = position;
			}
		}
	}

}
