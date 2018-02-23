package com.timedebris.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.timedebris.tools.DisplayHelper;

/**
 * {@link AutoScrollViewPager}的图片计数效果
 * 
 */
public class ViewPagerPointer extends View {

	private int POINT_RADIUS = 8;
	private int POINT_SPACING = 28;

	private int points;
	private int currentPoint = 0;

	public ViewPagerPointer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewPagerPointer(Context context, AttributeSet attrs, int points) {
		super(context, attrs);
		this.points = points;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		// 反锯齿
		paint.setAntiAlias(true);
		paint.setDither(true);
		int cx = POINT_RADIUS;
		int cy = POINT_RADIUS;
		// 画出所有空标志点
		paint.setColor(0x44DDDDDD);
		for (int i = 0; i < points; i++) {
			canvas.drawCircle(cx, cy, POINT_RADIUS, paint);
			cx += POINT_SPACING;
		}
		// 画出现在所在位置的标志点
		paint.setColor(0xfffafafa);
		canvas.drawCircle(POINT_RADIUS + currentPoint * POINT_SPACING,
				POINT_RADIUS, POINT_RADIUS, paint);
	}

	public void setLayoutParams(ViewGroup.LayoutParams params, int radius) {
		POINT_RADIUS = (int) (radius * DisplayHelper.density());
		POINT_SPACING = POINT_RADIUS * 3;

		int viewWidth = POINT_RADIUS * 2 + POINT_SPACING * (points - 1);
		int viewHeight = POINT_RADIUS * 6;
		// 设置相对布局
		if (params == null)
			params = getLayoutParams();
		params.width = viewWidth;
		params.height = viewHeight;
		
		this.setLayoutParams(params);
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void setCurrentPoint(int currentPoint) {
		this.currentPoint = currentPoint;
		this.invalidate();
	}

}
