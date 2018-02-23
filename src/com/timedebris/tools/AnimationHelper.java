package com.timedebris.tools;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationHelper {

	public static Animation getAnimation(Context context, int animId) {
		Animation anim = AnimationUtils.loadAnimation(context, animId);
		anim.setFillAfter(true);
		return anim;
	}
}
