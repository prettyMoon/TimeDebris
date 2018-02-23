package com.timedebris.View;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timedebris.R;
import com.timedebris.tools.DisplayHelper;

public class EvaluateLayout extends LinearLayout {

	ImageView iv1, iv2, iv3, iv4;
	int width = 36, height = 36;
	TextView tv;
	float rate = 2f;

	public EvaluateLayout(Context context) {
		super(context);
	}

	public EvaluateLayout(final Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOrientation(LinearLayout.HORIZONTAL);

		iv1 = new ImageView(context);
		iv2 = new ImageView(context);
		iv3 = new ImageView(context);
		iv4 = new ImageView(context);

		iv1.setImageResource(R.drawable.evaluate_no_1);
		iv2.setImageResource(R.drawable.evaluate_no_2);
		iv3.setImageResource(R.drawable.evaluate_no_3);
		iv4.setImageResource(R.drawable.evaluate_no_4);

		float density = DisplayHelper.density();
		width = (int) (width * density);
		height = (int) (height * density);

		tv = new TextView(context);
		tv.setTextSize(18);
		tv.setText("少年快去努力叭~");

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LinearLayout layout = (LinearLayout) View.inflate(context,
						R.layout.introduce_layout, null);

				new AlertDialog.Builder(context).setTitle("关于-总体评价")
						.setView(layout).create().show();
			}
		});
	}

	public void setScoreSum(double sum) {
		this.removeAllViews();

		if (sum == 0)
			dontGiveFruit();
		else {
			// 范围1~15
			int _sum = dealWithSum(sum);
			giveFruit(_sum);
		}
	}

	private void dontGiveFruit() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = (int) (6 * DisplayHelper.density());
		addView(tv, params);
	}

	private void giveFruit(int _sum) {
		if (_sum >= 8) {
			addView(iv1, width, height);
			_sum -= 8;
		}
		if (_sum >= 4) {
			addView(iv2, width, height);
			_sum -= 4;
		}
		if (_sum >= 2) {
			addView(iv3, width, height);
			_sum -= 2;
		}
		if (_sum > 0)
			addView(iv4, width, height);
	}

	private int dealWithSum(double sum) {
		int count = (int) Math.ceil(sum / rate);

		return (count > 15) ? 15 : count;
	}
}
