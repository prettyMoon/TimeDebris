package com.timedebris.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.timedebris.R;
import com.timedebris.Activity.FiveMinutesActivity.GestureListener;
import com.timedebris.tools.NetCore;

public class SingleActivity extends Activity {
	private GestureDetector mGesture;
	private TextView tv_actionbar;
	private int countPraise = 0;
	private boolean isstart = true;
	private ImageView imv_singleBookCover;
	private TextView tv_singleBookName;
	private TextView tv_singleWriter;
	private TextView tv_singleBookIntroduction;
	private TextView tv_singleParagraph01;
	private TextView tv_singleParagraph02;
	private TextView tv_singleParagraph03;
	private ImageView img_book01;
	private ImageView img_book02;
	private ImageView img_book03;
	private TextView tv_priase;
	private TextView tv_Date;
	private Button btn_praise;
	private ImageButton imb_back;
	private Bundle bundle;
	private String url_setPraise = "http://1.timedebris.sinaapp.com/add_praise.php";
	private String url_getPraise = "http://1.timedebris.sinaapp.com/get_praise.php";
	private Handler mhandler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x111:
				if (msg.obj.toString().equals("1")) {
					Toast.makeText(SingleActivity.this, "点赞成功",
							Toast.LENGTH_LONG).show();
					countPraise++;
					btn_praise.setBackground(getResources().getDrawable(
							R.drawable.praise));
					setPraise(countPraise);
				}

				if (msg.obj.toString().equals("0")) {
					Toast.makeText(SingleActivity.this, "点赞失败",
							Toast.LENGTH_LONG).show();
				}

			case 0x000:
				if (isstart) {
					try {
						countPraise = Integer.parseInt(msg.obj.toString());
						setPraise(countPraise);
						isstart = false;
						// Log.i("111111", "222222");
					} catch (Exception e) {
						e.printStackTrace();
						Log.i("get", "get");
					}

				}
			}
		}
	};

	// 获取赞数
	class GetpraiseThread extends Thread {
		@Override
		public void run() {
			super.run();
			// 推荐书摘
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("key", "1"));
			params.add(new BasicNameValuePair("no", bundle.getString("bno")));

			String result = "";
			try {
				result = NetCore.postResultToNet(url_getPraise, params);
				Log.i("praise内容", result);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result获取失败");
			}

			Message msg_two = new Message();
			msg_two.what = 0x000;
			msg_two.obj = result;
			mhandler.sendMessage(msg_two);

		}
	}

	// 点赞
	class PraiseThread extends Thread {

		@Override
		public void run() {
			super.run();
			// 推荐书摘
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("key", "1"));
			params.add(new BasicNameValuePair("no", bundle.getString("bno")));

			String result = "";
			try {
				result = NetCore.postResultToNet(url_setPraise, params);
				Log.i("result内容", result);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result获取失败");
			}

			Message msg_two = new Message();
			msg_two.what = 0x111;
			msg_two.obj = result;
			mhandler.sendMessage(msg_two);

		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_activity);

		bundle = getIntent().getExtras();
		GetpraiseThread t = new GetpraiseThread();
		t.start();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View titleView = inflater.inflate(R.layout.action_bar_title, null);
		actionBar.setCustomView(titleView, lp);

		actionBar.setDisplayShowHomeEnabled(false);// ȥ������
		actionBar.setDisplayShowTitleEnabled(false);// ȥ������
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		tv_actionbar = (TextView) findViewById(R.id.actionBar_title);
		tv_actionbar.setText("图书详情");
		btn_praise = (Button) findViewById(R.id.btn_praise);
		// 读者点赞处理

		tv_priase = (TextView) findViewById(R.id.PriseNumber);
		imb_back = (ImageButton) this.findViewById(R.id.action_bar_back);
		imb_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});
		imv_singleBookCover = (ImageView) this
				.findViewById(R.id.SingleBookCover);
		tv_singleBookName = (TextView) this.findViewById(R.id.SingleBookName);
		tv_singleWriter = (TextView) this.findViewById(R.id.SingleWriter);
		tv_singleBookIntroduction = (TextView) this
				.findViewById(R.id.SingleBookIntroduction);
		tv_singleParagraph01 = (TextView) this
				.findViewById(R.id.SingleParagraph01);
		tv_singleParagraph02 = (TextView) this
				.findViewById(R.id.SingleParagraph02);
		tv_singleParagraph03 = (TextView) this
				.findViewById(R.id.SingleParagraph03);
		img_book01 = (ImageView) findViewById(R.id.img_book01);
		img_book02 = (ImageView) findViewById(R.id.img_book02);
		img_book03 = (ImageView) findViewById(R.id.img_book03);
		tv_Date = (TextView) this.findViewById(R.id.Date);
		findViewById(R.id.scro_book).setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return mGesture.onTouchEvent(event);
					}
				});

		mGesture = new GestureDetector(this, new GestureListener());
		init();
		btn_praise.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PraiseThread p = new PraiseThread();
				p.start();

				btn_praise.setClickable(false);
			}
		});

	}

	// 读取数据并设置相关属xin
	public void init() {
		tv_singleBookName.setText(bundle.getString("name"));
		tv_singleWriter.setText(bundle.getString("writer"));
		tv_singleBookIntroduction.setText(bundle.getString("intro"));

		String[] contents = getStringList(bundle.getString("bdigest"));

		tv_singleParagraph01.setText(contents[0]);
		tv_singleParagraph02.setText(contents[1]);
		tv_singleParagraph03.setText(contents[2]);
		tv_Date.setText(formatData(bundle.getString("publish_date")));
		// 加载上方小图片
		new MyTask().execute(getURL() + ".jpg");
		// 加载内容插图
		new MyTask01().execute(getURL() + "1.jpg");
		new MyTask02().execute(getURL() + "2.jpg");
		new MyTask03().execute(getURL() + "3.jpg");
	}

	// 格式化日期
	public String formatData(String str) {
		String result = "";
		for (int i = 0; i < 10; i++) {
			result += str.charAt(i);
		}
		return result;
	}

	// 获取并格式化url
	public String getURL() {
		String proURl = "http://7xkl94.com1.z0.glb.clouddn.com/";
		String suffix = bundle.getString("name");
		String reslult = proURl + suffix;
		return reslult;
	}

	class MyTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			Bitmap bitmap = null;
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpEntity = httpResponse.getEntity();
					byte[] data = EntityUtils.toByteArray(httpEntity);
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		// 更新UI
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			imv_singleBookCover.setImageBitmap(result);

		}

	}

	public void setPraise(int count) {
		String str = "已有" + count + "人为此书点赞！";
		tv_priase.setText(str);
	}

	public String[] getStringList(String str) {
		String[] result = str.split("#@@#");
		return result;
	}

	class MyTask01 extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Log.i("MyTask01", "MyTask01");
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			Bitmap bitmap = null;
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpEntity = httpResponse.getEntity();
					byte[] data = EntityUtils.toByteArray(httpEntity);
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		// 更新UI
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			img_book01.setImageBitmap(result);

		}

	}

	class MyTask02 extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Log.i("MyTask02", "MyTask02");
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			Bitmap bitmap = null;
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpEntity = httpResponse.getEntity();
					byte[] data = EntityUtils.toByteArray(httpEntity);
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		// 更新UI
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			img_book02.setImageBitmap(result);

		}

	}

	class MyTask03 extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Log.i("MyTask03", "MyTask03");
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0]);
			Bitmap bitmap = null;
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					HttpEntity httpEntity = httpResponse.getEntity();
					byte[] data = EntityUtils.toByteArray(httpEntity);
					bitmap = BitmapFactory
							.decodeByteArray(data, 0, data.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		// 更新UI
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			img_book03.setImageBitmap(result);

		}

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
