package com.timedebris.Activity;

import java.util.ArrayList;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.timedebris.R;
import com.timedebris.Adapter.MyFragmentPagerAdapter;
import com.timedebris.Fragment.RecommendFragment;
import com.timedebris.Fragment.StatisticFragment;
import com.timedebris.Fragment.TaskFragment;
import com.timedebris.View.Title;
import com.timedebris.tools.DisplayHelper;
import com.timedebris.tools.SystemBarTintManager;

public class MainActivity extends FragmentActivity {

	public static final int color_title_selected = 0xffffffff;
	public static final int color_title_unselected = 0xffc2d4ff;
	public static final int color_title_container_bg = 0xff448aff;

	public static int currentPage = 1;

	public Title title;
	public LinearLayout titleContainer;

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;

	/*
	 * 此处声明自己的Fragment，如：Fragment_name fragment_name;
	 */
	RecommendFragment recoFragment;
	TaskFragment taskFragment;
	StatisticFragment statFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		setConditionBarColor();

		initViewPager();
		initTitle();
		initTitleContainer();

		mPager.setCurrentItem(1);

		DisplayHelper.init(this);
	}

	private void setConditionBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);

			fitsStatusBarHeight();
			
			// 创建状态栏的管理实例
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			// 激活导航栏设置
			tintManager.setNavigationBarTintEnabled(true);
			// 激活状态栏设置
			tintManager.setStatusBarTintEnabled(true);
			// 加载你的actionbar的颜色或者背景图
			tintManager.setStatusBarAlpha(0f);
		}
	}

	protected void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private void fitsStatusBarHeight() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.fitStatusBar);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, DisplayHelper
						.getStatusBarHeight(this)));
	}

	public void initTitle() {
		String[] str_title = new String[] { "推荐", "任务","统计"};
		title = new Title(this, str_title);
		title.changePage(1, 1);
	}

	private void initTitleContainer() {
		titleContainer = (LinearLayout) findViewById(R.id.titleContainer);
		titleContainer.setBackgroundColor(color_title_container_bg);

		LinearLayout.LayoutParams params = new LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(20, 20, 20, 30);
		titleContainer.addView(title, params);
		titleContainer.setGravity(Gravity.CENTER_HORIZONTAL);
	}

	public void initViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		// 设置预加载的页数,默认且最小为1
		mPager.setOffscreenPageLimit(3);
		// 设置当前页标
		currentPage = 1;
		// 页面变化时的监听器
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		fragmentList = new ArrayList<Fragment>();
		/*
		 * 此处定义自己的Fragment，实例如下: fragment_name =
		 * Fragment_Name.newInstance(this);
		 * 并添加，如：fragmentList.add(fragment_name);
		 */
		recoFragment = RecommendFragment.newInstance();
		taskFragment = TaskFragment.newInstance(this);
		statFragment = StatisticFragment.newInstance(this);
		fragmentList.add(recoFragment);
		fragmentList.add(taskFragment);
		fragmentList.add(statFragment);

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixelsAuto) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onPageSelected(int position) {
			title.changePage(currentPage, position);
			currentPage = position;
			if (taskFragment.isShowButton())
				taskFragment.btnGroupFadeOut();
		}
	}

	private long lastTime;
	private Toast outToast = null;

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (lastTime != 0 && currentTime - lastTime <= 2500)
			super.onBackPressed();
		else {
			lastTime = currentTime;

			if (outToast == null) {
				outToast = Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT);
			}
			outToast.show();
		}
	}

	public void changePage(int position) {
		mPager.setCurrentItem(position, true);
	}

}