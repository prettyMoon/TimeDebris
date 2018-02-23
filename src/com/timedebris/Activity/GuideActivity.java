package com.timedebris.Activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.timedebris.R;
import com.timedebris.tools.DisplayHelper;

@SuppressLint("NewApi")
public class GuideActivity extends Activity {
	private ViewPager viewPager;

	private ArrayList<View> pageViews;
	private ImageView imageView;

	private ImageView enterView;

	private ImageView[] imageViews;

	private ViewGroup viewPics;

	private LinearLayout viewPoints;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.viewpager_page1, null));
		pageViews.add(inflater.inflate(R.layout.viewpager_page2, null));
		pageViews.add(inflater.inflate(R.layout.viewpager_page3, null));

		imageViews = new ImageView[pageViews.size()];

		viewPics = (ViewGroup) inflater.inflate(R.layout.activity_guide, null);

		viewPoints = (LinearLayout) viewPics.findViewById(R.id.viewGroup);
		viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);

		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(GuideActivity.this);

			DisplayHelper.init(this);
			int a = (int) (9 * DisplayHelper.density());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(a,
					a);
			params.setMargins(12, 12, 12, 12);

			imageView.setLayoutParams(params);// 创建一个宽高均为20 的布局
			imageViews[i] = imageView;

			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.white_dot);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.dark_dot);
				imageView.setAlpha(0.5f);
			}

			viewPoints.addView(imageViews[i]);
		}

		setContentView(viewPics);

		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	private Button.OnClickListener Button_OnClickListener = new Button.OnClickListener() {
		public void onClick(View v) {
			setGuided();
			Intent mIntent = new Intent();
			mIntent.setClass(GuideActivity.this, MainActivity.class);
			GuideActivity.this.startActivity(mIntent);
			GuideActivity.this.finish();
		}
	};

	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

	private void setGuided() {
		SharedPreferences settings = getSharedPreferences(
				SHAREDPREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEY_GUIDE_ACTIVITY, "false");
		editor.commit();
	}

	class GuidePageAdapter extends PagerAdapter {

		// 销毁position位置的界面
		@Override
		public void destroyItem(View v, int position, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) v).removeView(pageViews.get(position));

		}

		@Override
		public void finishUpdate(View arg0) {

		}

		// 获取当前窗体界面数
		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public Object instantiateItem(View v, int position) {
			((ViewPager) v).addView(pageViews.get(position));

			if (position == 2) {
				ImageView iv = (ImageView) findViewById(R.id.btn_entermain);
				iv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						setGuided();
						Intent mIntent = new Intent();
						mIntent.setClass(GuideActivity.this, MainActivity.class);
						GuideActivity.this.startActivity(mIntent);
						GuideActivity.this.finish();
					}
				});
			}

			return pageViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View v, Object arg1) {
			return v == arg1;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < imageViews.length; i++) {
				// 不是当前选中的page，其小圆点设置为未选中的状态
				if (position != i) {
					imageViews[i].setBackgroundResource(R.drawable.dark_dot);
					imageViews[i].setAlpha(0.5f);
				} else {
					imageViews[position]
							.setBackgroundResource(R.drawable.white_dot);
					imageViews[position].setAlpha(1.0f);
				}
			}

		}
	}
}
