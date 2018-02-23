package com.timedebris.tools;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayHelper {

	private static int width;
	private static int height;
	private static float density;

	public static void init(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
	}

	public static float density() {
		return density;
	}

	public static float width() {
		return width;
	}

	public static float height() {
		return height;
	}
	
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
