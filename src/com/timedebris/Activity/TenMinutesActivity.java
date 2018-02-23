package com.timedebris.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.timedebris.R;
import com.timedebris.Activity.FiveMinutesActivity.GestureListener;
import com.timedebris.Adapter.ArticalAdapter;
import com.timedebris.View.LoadMoreListView;
import com.timedebris.View.LoadMoreListView.OnLoadMore;
import com.timedebris.tools.ListViewDealer;
import com.timedebris.tools.NetCore;

public class TenMinutesActivity extends Activity implements OnLoadMore {
	private TextView tv_actionbar;
	private GestureDetector mGesture;

	private ImageButton img_back;
	private String date;
	private LoadMoreListView lv_tenMinutes;
	private ArticalAdapter simpleAdapter;
	private List<Map<String, Object>> listitems;
	private List<Map<String, Object>> bundleList;

	private String result;
	private String url = "http://1.timedebris.sinaapp.com/refresh_article_10.php";
	// 主线程处理获取的网络信息
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 0:
				String str = msg.obj.toString();
				analyData(str);
				simpleAdapter = new ArticalAdapter(TenMinutesActivity.this,
						listitems);
				lv_tenMinutes.setAdapter(simpleAdapter);

				break;
			case 2:
				String res = msg.obj.toString();
				Log.i("res", res);
				if (res.equals("null") || res.equals("")) {
					Log.i("0x222", "无最新数据");
					Toast.makeText(TenMinutesActivity.this, "无最新数据",
							Toast.LENGTH_SHORT).show();
					lv_tenMinutes.onLoadComplete();
					return;
				}
				if (msg.obj.toString().equals("0")) {
					Toast.makeText(TenMinutesActivity.this, "获取失败",
							Toast.LENGTH_SHORT).show();
					lv_tenMinutes.onLoadComplete();
					return;
				}
				Log.i("msg.obj.toString()", msg.obj.toString());
				analyData(msg.obj.toString());
				simpleAdapter.notifyDataSetChanged();
				lv_tenMinutes.onLoadComplete();
				break;
			}

		}
	};

	// 第一次获取文章的子线程
	class MyThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("last_date", "0"));
			try {
				result = NetCore.postResultToNet(url, params);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Log.i("result内容", result);
				result = result.replace(" ", "");
				Message mes = new Message();
				mes.what = 0;
				mes.obj = result;
				mHandler.sendMessage(mes);
			}

		}

		public MyThread() {

		}
	}

	// 第二次获取网络数据的子线程
	class LoadMoreThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			// 推荐书摘
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			date = formatDate(date);
			params.add(new BasicNameValuePair("last_date", date));
			Log.i("加载线程", date);
			try {
				result = NetCore.postResultToNet(url, params);
				Log.i("result内容", result);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result获取失败");
			}

			Message msg_two = new Message();
			msg_two.what = 2;
			msg_two.obj = result;
			mHandler.sendMessage(msg_two);

		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ten_list);
		MyThread t = new MyThread();
		t.start();
		// 设置actionBar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View titleView = inflater.inflate(R.layout.action_bar_title, null);
		actionBar.setCustomView(titleView, lp);

		actionBar.setDisplayShowHomeEnabled(false);// 去掉导航
		actionBar.setDisplayShowTitleEnabled(false);// 去掉标题
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);

		tv_actionbar = (TextView) findViewById(R.id.actionBar_title);
		tv_actionbar.setText("碎阅");

		img_back = (ImageButton) this.findViewById(R.id.action_bar_back);
		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		listitems = new ArrayList<Map<String, Object>>();
		bundleList = new ArrayList<Map<String, Object>>();
		lv_tenMinutes = (LoadMoreListView) this
				.findViewById(R.id.lv_tenMinutes);
		lv_tenMinutes.setLoadMoreListen(this);
		lv_tenMinutes.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGesture.onTouchEvent(event);
			}
		});

		mGesture = new GestureDetector(this, new GestureListener());
		ListViewDealer.setListViewHeightBasedOnChildren(lv_tenMinutes);

		lv_tenMinutes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long Id) {
				Intent intent = new Intent(TenMinutesActivity.this,
						SingleArticalActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("title", bundleList.get(position)
						.get("title").toString());

				mBundle.putString("writer",
						bundleList.get(position).get("Writer").toString());

				mBundle.putString("intro",
						bundleList.get(position).get("content").toString());

				mBundle.putString("apraise",
						bundleList.get(position).get("apraise").toString());
				mBundle.putString("ano", bundleList.get(position).get("ano")
						.toString());

				intent.putExtras(mBundle);
				startActivity(intent);
			}

		});
	}

	public void analyData(String jsontring) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(jsontring);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = null;
			try {
				jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> item = new HashMap<String, Object>();
				Map<String, Object> artical = new HashMap<String, Object>();

				item.put("title", jsonObject.getString("aname"));
				artical.put("title", jsonObject.getString("aname"));

				item.put("Writer", jsonObject.getString("aauthor"));
				artical.put("Writer", jsonObject.getString("aauthor"));

				item.put("digest", getDigest(jsonObject.getString("acontents")));

				artical.put("content", jsonObject.getString("acontents"));

				artical.put("ano", jsonObject.getString("ano"));
				artical.put("apraise", jsonObject.getString("apraise"));
				date = jsonObject.getString("publish_date");
				Log.i("item", "" + item.size());
				listitems.add(item);
				bundleList.add(artical);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	// 修改日期格式
	public String formatDate(String str) {
		String result = "";
		String front = "";
		String behind = "";

		for (int i = 0; i < 10; i++) {
			front += str.charAt(i);
		}
		for (int i = 10; i < str.length(); i++) {
			behind += str.charAt(i);
		}
		result = front + " " + behind;
		return result;
	}

	// 下拉刷新接口
	@Override
	public void loadMore() {
		LoadMoreThread l = new LoadMoreThread();
		l.start();
	}

	// 截取digest
	public String getDigest(String target) {
		String result = "";

		for (int i = 0; i < 30; i++) {
			result += target.charAt(i);
		}
		result += "······";
		return result;
	}

	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1,
				float steepX, float steepY) {
			if (steepX >= 2000 && (Math.abs(steepY) < 0.7 * steepX)) {
				lv_tenMinutes.setOnItemClickListener(null);
				finish();
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
