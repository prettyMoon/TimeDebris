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
import com.timedebris.Adapter.MyAdapter;
import com.timedebris.View.LoadMoreListView;
import com.timedebris.View.LoadMoreListView.OnLoadMore;
import com.timedebris.tools.NetCore;

public class MoreBookActivity extends Activity implements OnLoadMore {
	private TextView tv_actionbar;
	private List<Map<String, Object>> listItems_book;// 适配器数据
	private GestureDetector mGesture;
	private NewThread newThread = new NewThread();

	private LoadMoreListView lv_morebook;
	private MyAdapter simpleAdapter_book;

	private String url = "http://1.timedebris.sinaapp.com/refresh_hotest.php";
	private String result;
	private String publish_date;

	private List<Map<String, Object>> DataList;
	// actionBar的返回按钮
	private ImageButton imb_back;
	// 适配器参数
	String[] adapterString = new String[] { "Cover", "From", "Writer",
			"content" };
	int[] adapterInt = new int[] { R.id.Cover, R.id.Name, R.id.Writer,
			R.id.Introduction };

	// 主线程的handler
	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x111:
				AnalysisJsonData(msg.obj.toString());
				simpleAdapter_book = new MyAdapter(MoreBookActivity.this,
						listItems_book);
				lv_morebook.setAdapter(simpleAdapter_book);
				result = null;
				break;
			case 0x222:
				String res = msg.obj.toString();
				Log.i("res", res);
				if (res.equals("null") || res.equals("")) {
					Log.i("0x222", "无最新数据");
					Toast.makeText(MoreBookActivity.this, "无最新数据",
							Toast.LENGTH_SHORT).show();
					lv_morebook.onLoadComplete();
					return;
				}
				if (msg.obj.toString().equals("0")) {
					Toast.makeText(MoreBookActivity.this, "获取失败",
							Toast.LENGTH_SHORT).show();
					lv_morebook.onLoadComplete();
					return;
				}
				Log.i("msg.obj.toString()", msg.obj.toString());
				AnalysisJsonData(msg.obj.toString());
				simpleAdapter_book.notifyDataSetChanged();
				lv_morebook.onLoadComplete();
				break;
			}
		}
	};

	// 第一次获取网络数据的子线程
	class NewThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			// 推荐书摘
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("hotest_date", "0"));
			try {
				result = NetCore.postResultToNet(url, params);
				Log.i("listItems_paragraph内容", result);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result_paragraph获取失败");
			}

			Message msg_one = new Message();
			msg_one.what = 0x111;
			msg_one.obj = result;
			mhandler.sendMessage(msg_one);

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
			params.add(new BasicNameValuePair("hotest_date", publish_date));
			Log.i("加载线程", publish_date);
			try {
				result = NetCore.postResultToNet(url, params);
				Log.i("result内容", result);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result获取失败");
			}

			Message msg_two = new Message();
			msg_two.what = 0x222;
			msg_two.obj = result;
			mhandler.sendMessage(msg_two);

		}

	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_list);
		// 启动线程

		newThread.start();
		// actionbar设置样式
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
		DataList = new ArrayList<Map<String, Object>>();

		imb_back = (ImageButton) this.findViewById(R.id.action_bar_back);
		imb_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		lv_morebook = (LoadMoreListView) findViewById(R.id.more_paragraph);
		lv_morebook.setLoadMoreListen(this);
		lv_morebook.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGesture.onTouchEvent(event);
			}
		});
		mGesture = new GestureDetector(this, new GestureListener());
		listItems_book = new ArrayList<Map<String, Object>>();

		lv_morebook.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {

				Intent intent = new Intent(MoreBookActivity.this,
						SingleActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("name", DataList.get(position)
						.get("bookFrom").toString());
				mBundle.putString("writer",
						DataList.get(position).get("bookWriter").toString());
				mBundle.putString("intro",
						DataList.get(position).get("book_content").toString());
				mBundle.putString("bdigest",
						DataList.get(position).get("bdigest").toString());
				mBundle.putString("publish_date",
						DataList.get(position).get("publish_date").toString());

				mBundle.putString("type", "Book");
				mBundle.putString("bno", DataList.get(position).get("bno")
						.toString());
				mBundle.putString("bpraise",
						DataList.get(position).get("bpraise").toString());

				intent.putExtras(mBundle);
				startActivity(intent);

			}

		});

	}

	// 实现上划刷新的接口
	@Override
	public void loadMore() {
		LoadMoreThread t = new LoadMoreThread();
		t.start();

	}

	// 获取数据后的一系列操作
	public void AnalysisJsonData(String jsonString) {
		if (jsonString.equals(null) || jsonString.equals("")) {
			return;
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(jsonString);
		} catch (Exception e) {
			lv_morebook.onLoadComplete();
			Log.i("MoreActivity", "jsonArray = new JSONArray(result);出错了");
		}

		int iSize = jsonArray.length();
		try {
			for (int i = 0; i < iSize; i++) {
				/**
				 * 解析json数据
				 */
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> singledata = new HashMap<String, Object>();
				Map<String, Object> item = new HashMap<String, Object>();

				item.put("From", jsonObject.getString("bname"));
				singledata.put("bookFrom", jsonObject.getString("bname"));

				item.put("Writer", jsonObject.getString("bauthor"));
				singledata.put("bookWriter", jsonObject.getString("bauthor"));

				item.put("content",
						formatIntroduction(jsonObject.getString("bintro")));
				singledata.put("book_content", jsonObject.getString("bintro"));

				singledata.put("publish_date",
						jsonObject.getString("publish_date"));
				singledata.put("bdigest", jsonObject.getString("bdigest"));

				singledata.put("bpraise", jsonObject.getString("bpraise"));
				singledata.put("bno", jsonObject.getString("bno"));

				DataList.add(singledata);
				listItems_book.add(item);

				// 最后一条数据的日期，用于向服务器发送请求
				publish_date = jsonObject.getString("publish_date");

			}
		} catch (Exception e) {
			e.printStackTrace();

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

	// 将introduction格式化一下
	public String formatIntroduction(String str) {
		String result = "";
		if (str.length() <= 40) {
			result = str;
		} else {
			for (int i = 0; i < 30; i++) {
				result += str.charAt(i);
			}
			result = "      " + result + "···";
		}

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
				lv_morebook.setOnItemClickListener(null);
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
