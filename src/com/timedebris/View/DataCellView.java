package com.timedebris.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.timedebris.tools.DisplayHelper;

public class DataCellView extends LinearLayout {
	Context mContext;
	float tomatoHeight, littleHeight;
	String mDate = new String();
	static final int color_tomato = 0xff00A0EB;
	static final int color_tomato_light = 0xff1FB8FF;
	static final int color_little = 0xff54B3DF;
	static final int color_little_light = 0xff86CAE9;
	static final int color_date = 0xff777777;
	static final int color_date_light = 0xff0090d0;

	boolean isChoosen = false;
	static final float[] perNum = new float[] { 16, 4, 1 };
	static final int[] widthNum = new int[] { 11, 7, 5 };
	int mKind = 0;
	int width = 0;
	float height = 340;
	float bottomHeight = 36;
	float border = 1;
	static float[][] textPositionX;
	float textX;
	float textY;
	float textSize = 9;

	int colorLittle = color_little, colorTomato = color_tomato;
	InnerCell innerCell;

	public DataCellView(Context context, int tomato, int little, String date,
			boolean highLight, int kind) {
		super(context);
		mContext = context;
		mKind = kind;
		mDate = date;
		isChoosen = highLight;
		if (highLight) {
			colorLittle = color_little_light;
			colorTomato = color_tomato_light;
		} else {
			colorLittle = color_little;
			colorTomato = color_tomato;
		}

		tomatoHeight = tomato * perNum[mKind];
		littleHeight = little * perNum[mKind];

		float fullWidth = DisplayHelper.width();
		float density = DisplayHelper.density();
		width = (int) (fullWidth / widthNum[mKind]);
		height *= density;
		bottomHeight *= density;
		border *= density;
		textSize *= density;

		innerCell = new InnerCell(mContext);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) width, (int) height);
		addView(innerCell, params);
	}

	public DataCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private class InnerCell extends View {

		public InnerCell(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			Paint p = new Paint();
			p.setColor(colorLittle);
			canvas.drawRect(border, height - bottomHeight - tomatoHeight
					- littleHeight, width - border, height - bottomHeight
					- tomatoHeight, p);
			p.setColor(colorTomato);
			canvas.drawRect(border, height - bottomHeight - tomatoHeight, width
					- border, height - bottomHeight, p);
			p.setColor(0xfff2f2f2);
			canvas.drawRect(0, height - bottomHeight, width, height, p);
			if (isChoosen)
				p.setColor(0xff0090d0);
			else
				p.setColor(0xff888888);
			p.setTextSize(20);

			if (textPositionX == null) {
				textPositionX = new float[][] {
						{ 0, 0, width * 0.20f, width * 0.27f, width * 0.20f,
								width * 0.12f },
						{ 0, 0, 0, 0, 0, 0, 0, width * 0.40f, width * 0.30f,
								width * 0.15f, width * 0.08f, width * 0.02f },
						{ 0, 0, width * 0.82f, width * 0.82f, 0, width * 0.68f } };
			}
			textX = textPositionX[mKind][mDate.length()];
			textY = height - bottomHeight * 0.52f;

			p.setAntiAlias(true);
			p.setDither(true);
			p.setTextSize(textSize);

			canvas.drawText(mDate, textX, textY, p);
		}
	}

	public void setSelectCondition(boolean isSelected) {
		if (isSelected) {
			colorLittle = color_little_light;
			colorTomato = color_tomato_light;
			isChoosen = true;
		} else {
			colorLittle = color_little;
			colorTomato = color_tomato;
			isChoosen = false;
		}
		innerCell.invalidate();
	}

	public void setParams(int tomato, int little, String date,
			boolean highLight, int kind) {
		mDate = date;
		mKind = kind;
		isChoosen = highLight;

		if (highLight) {
			colorLittle = color_little_light;
			colorTomato = color_tomato_light;
		} else {
			colorLittle = color_little;
			colorTomato = color_tomato;
		}

		tomatoHeight = tomato * perNum[mKind];
		littleHeight = little * perNum[mKind];

		float fullWidth = DisplayHelper.width();
		width = (int) (fullWidth / widthNum[mKind]);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(int) width, (int) height);
		innerCell.setLayoutParams(params);
	}
}
