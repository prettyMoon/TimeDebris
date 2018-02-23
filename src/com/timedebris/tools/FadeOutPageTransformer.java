package com.timedebris.tools;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;

/*
 AutoViewPager切换效果
 */
public class FadeOutPageTransformer implements ViewPager.PageTransformer {

	@SuppressLint("NewApi")
	public void transformPage(View view, float position) {

		if (position < -1) {
			view.setAlpha(position * 90);
		} else if (position <= 1) {
			if (position < 0) {
				view.setAlpha(position + 1);
			} else {
				view.setAlpha(1 - position);
			}
		} else {
			view.setAlpha(0);
		}
	}
}