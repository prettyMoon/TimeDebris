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
import com.timedebris.Activity.SingleActivity.PraiseThread;
import com.timedebris.tools.NetCore;

public class SingleArticalActivity extends Activity {
	private GestureDetector mGesture;
	private TextView tv_actionbar;
	private boolean isstart = true;
	private TextView tv_singleTitle;
	private TextView tv_singleWriter;
	private TextView tv_singleContent01;
	private TextView tv_singleContent02;
	private TextView tv_singleContent03;
	private ImageView img_picture01;
	private ImageView img_picture02;
	private ImageView img_picture03;
	private Button btn_praise;
	private TextView tv_praise;
	private int countPraise = 0;
	private Bundle bundle;
	private String url_setPraise = "http://1.timedebris.sinaapp.com/add_praise.php";
	private String url_getPraise = "http://1.timedebris.sinaapp.com/get_praise.php";
	private ImageButton img_back;
	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x111:
				if (msg.obj.toString().equals("1")) {
					Toast.makeText(SingleArticalActivity.this, "点赞成功",
							Toast.LENGTH_LONG).show();
					countPraise++;
					setPraise(countPraise);
				}

				if (msg.obj.toString().equals("0")) {
					Toast.makeText(SingleArticalActivity.this, "点赞失败",
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

			params.add(new BasicNameValuePair("key", "0"));
			params.add(new BasicNameValuePair("no", bundle.getString("ano")));

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

			params.add(new BasicNameValuePair("key", "0"));
			params.add(new BasicNameValuePair("no", bundle.getString("ano")));

			String result = "";
			try {
				result = NetCore.postResultToNet(url_setPraise, params);
				Log.i("result内容", result);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result获取失败");
			}

			Message msg_e = new Message();
			msg_e.what = 0x111;
			msg_e.obj = result;
			mhandler.sendMessage(msg_e);

		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_artical);

		findViewById(R.id.scro_artical).setOnTouchListener(
				new View.OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return mGesture.onTouchEvent(event);
					}
				});

		mGesture = new GestureDetector(this, new GestureListener());
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

		actionBar.setDisplayShowHomeEnabled(false);// 去掉导航
		actionBar.setDisplayShowTitleEnabled(false);// 去掉标题
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);
		tv_actionbar = (TextView) findViewById(R.id.actionBar_title);
		tv_actionbar.setText("好文细读");
		btn_praise = (Button) findViewById(R.id.btn_praise01);
		tv_praise = (TextView) findViewById(R.id.PriseNumber01);

		tv_singleTitle = (TextView) this.findViewById(R.id.single_title);
		tv_singleWriter = (TextView) this.findViewById(R.id.single_writer);
		tv_singleContent01 = (TextView) this.findViewById(R.id.single_content1);
		tv_singleContent02 = (TextView) this.findViewById(R.id.single_content2);
		tv_singleContent03 = (TextView) this.findViewById(R.id.single_content3);
		img_picture01 = (ImageView) this.findViewById(R.id.image_picture01);
		img_picture02 = (ImageView) this.findViewById(R.id.image_picture02);
		img_picture03 = (ImageView) this.findViewById(R.id.image_picture03);
		img_back = (ImageButton) findViewById(R.id.action_bar_back);
		img_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btn_praise.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PraiseThread p = new PraiseThread();
				p.start();
				btn_praise.setBackground(getResources().getDrawable(
						R.drawable.praise));
				btn_praise.setClickable(false);
			}
		});

		setContent();
	}

	// 设置显示内容的方�?
	public void setContent() {
		tv_singleTitle.setText(bundle.getString("title"));
		tv_singleWriter.setText("[" + bundle.getString("writer") + "]");
		String content = bundle.getString("intro").replace(" ", "");
		String[] contents = getStringList(content);
		tv_singleContent01.setText(contents[0]);
		tv_singleContent02.setText(contents[1]);
		tv_singleContent03.setText(contents[2]);
		new MyTask01().execute(getURL() + "1.jpg");
		new MyTask02().execute(getURL() + "2.jpg");
		new MyTask03().execute(getURL() + "3.jpg");
	}

	public String[] getStringList(String str) {
		String[] result = str.split("#@@#");
		return result;
	}

	// 获取并格式化url
	public String getURL() {
		String proURl = "http://7xkl94.com1.z0.glb.clouddn.com/";
		String suffix = bundle.getString("title");
		String reslult = proURl + suffix;
		return reslult;
	}

	class MyTask01 extends AsyncTask<String, Void, Bitmap> {

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
			img_picture01.setImageBitmap(result);

		}

	}

	class MyTask02 extends AsyncTask<String, Void, Bitmap> {

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
			img_picture02.setImageBitmap(result);

		}

	}

	class MyTask03 extends AsyncTask<String, Void, Bitmap> {

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
			img_picture03.setImageBitmap(result);

		}

	}

	public void setPraise(int count) {
		String str = "已有" + count + "人为此文章点赞！";
		Log.i("setPraise", str);
		tv_praise.setText(str);
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
