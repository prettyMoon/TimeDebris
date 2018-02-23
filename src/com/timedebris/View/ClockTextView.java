package com.timedebris.View;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class ClockTextView extends TextView {

	public ClockTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ClockTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ClockTextView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		AssetManager assetMgr = context.getAssets();
		Typeface font = Typeface.createFromAsset(assetMgr,
				"fonts/font_lcd.ttf");
		setTypeface(font);
	}

}
