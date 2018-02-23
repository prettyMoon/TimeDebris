package com.timedebris.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.timedebris.R;
import com.timedebris.Activity.FiveMinutesActivity;
import com.timedebris.Activity.MoreBookActivity;
import com.timedebris.Activity.MoreParagraphActivity;
import com.timedebris.Activity.SingleActivity;
import com.timedebris.Activity.TenMinutesActivity;
import com.timedebris.Adapter.MyAdapter;
import com.timedebris.tools.ListViewDealer;
import com.timedebris.tools.NetCore;

@SuppressLint("NewApi")
public class RecommendFragment extends Fragment implements OnClickListener {

	private Button btn_five_minutes;
	private Button btn_ten_minutes;
	private Button btn_more01;
	private Button btn_more02;
	// private String Image_path =
	// "http://7xkl94.com1.z0.glb.clouddn.com/201300301180.jpg";
	// private Handler mainHandler;
	private String[] adapterString;
	private int[] adapterInt;
	private Bitmap cover = null;
	// 推荐书摘
	private ListView lv_recommend_paragraph;
	private List<Bitmap> paragraphCover;
	private List<String> paragraphFrom;
	private List<String> paragraphWriter;
	private List<String> paragraph_content;
	private List<Map<String, Object>> items_paragraph;
	private MyAdapter simpleAdapter_paragraph;
	private String latest_url = "http://1.timedebris.sinaapp.com/refresh_latest.php";// 第一次给三条
	private List<Map<String, Object>> DataList_paragraph;
	private String result_paragraph = "";
	// 热评图书
	private ListView lv_recommend_book;
	private List<Drawable> bookcover;
	private List<String> bookname;
	private List<String> book_writer;
	private List<String> book_introduction;
	private List<Map<String, Object>> items_book;
	private MyAdapter simpleAdapter_book;
	private String hotest_url = "http://1.timedebris.sinaapp.com/refresh_hotest.php";// 第一次给两条
	private List<Map<String, Object>> DataList_book;
	private NewThread loadThread;
	private String result_book = "";

	public static RecommendFragment newInstance() {
		return new RecommendFragment();
	}

	// //主线程的handler
	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			switch (msg.what) {
			case 0x111:// 推荐书摘
				result_paragraph = msg.obj.toString();
				AnalysisJsonData(result_paragraph, items_paragraph,
						DataList_paragraph);

				simpleAdapter_paragraph = new MyAdapter(getActivity(),
						items_paragraph);
				lv_recommend_paragraph.setAdapter(simpleAdapter_paragraph);
				ListViewDealer
						.setListViewHeightBasedOnChildren(lv_recommend_paragraph);
				break;
			case 0x222:// 热评图书
				result_book = msg.obj.toString();
				AnalysisJsonData(result_book, items_book, DataList_book);
				simpleAdapter_book = new MyAdapter(getActivity(), items_book);
				lv_recommend_book.setAdapter(simpleAdapter_book);
				ListViewDealer
						.setListViewHeightBasedOnChildren(lv_recommend_book);
				break;
			}
		}

	};

	// 获取网络数据的子线程
	class NewThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			// 推荐书摘
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("latest_date", "0"));
			try {
				result_paragraph = NetCore.postResultToNet(latest_url, params);
				Log.i("listItems_paragraph内容", result_paragraph);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result_paragraph获取失败");
			}
			Message msg_three = new Message();
			msg_three.what = 0x111;
			msg_three.obj = result_paragraph;
			mhandler.sendMessage(msg_three);
			// 热评图书
			List<NameValuePair> anotherparams = new ArrayList<NameValuePair>();
			anotherparams.add(new BasicNameValuePair("hotest_date", "0"));
			try {
				result_book = NetCore
						.postResultToNet(hotest_url, anotherparams);
				Log.i("result_book内容", result_book);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("异常提示", "result_book获取失败");
			}
			Message msg_four = new Message();
			msg_four.what = 0x222;
			msg_four.obj = result_book;
			mhandler.sendMessage(msg_four);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savaInstanceState) {
		final View layout = inflater.inflate(R.layout.recommend_fragment,
				container, false);
		((Button) layout.findViewById(R.id.netRetry))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isNetworkAvailable(getActivity())) {
							layout.findViewById(R.id.mode1).setVisibility(
									View.GONE);
							layout.findViewById(R.id.mode2).setVisibility(
									View.VISIBLE);
							loadThread = new NewThread();
							loadThread.start();
						} else {
							Toast.makeText(getActivity(), "网络仍未连接",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		checkNetAndResponse(layout);

		return layout;
	}

	public boolean checkNetAndResponse(View layout) {
		if (layout == null)
			layout = getView();

		if (isNetworkAvailable(getActivity())) {
			layout.findViewById(R.id.mode1).setVisibility(View.GONE);
			layout.findViewById(R.id.mode2).setVisibility(View.VISIBLE);
			return true;
		} else {
			Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			// 如果仅仅是用来判断网络连接
			// 则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onActivityCreated(Bundle saveInstanceState) {
		super.onActivityCreated(saveInstanceState);
		// ImageLoader l = new ImageLoader();
		Log.i("aga", "adsas");
		btn_five_minutes = (Button) getView().findViewById(
				R.id.btn_five_minutes);
		btn_ten_minutes = (Button) getView().findViewById(R.id.btn_ten_minutes);
		btn_more01 = (Button) getView().findViewById(R.id.btn_more01);
		btn_more02 = (Button) getView().findViewById(R.id.btn_more02);

		btn_five_minutes.setOnClickListener(this);
		btn_ten_minutes.setOnClickListener(this);
		btn_more01.setOnClickListener(this);
		btn_more02.setOnClickListener(this);
		lv_recommend_book = (ListView) getView().findViewById(
				R.id.listview_book);
		lv_recommend_paragraph = (ListView) getView().findViewById(
				R.id.listview_paragraph);

		paragraphCover = new ArrayList<Bitmap>();
		paragraphFrom = new ArrayList<String>();
		paragraphWriter = new ArrayList<String>();
		paragraph_content = new ArrayList<String>();
		items_paragraph = new ArrayList<Map<String, Object>>();
		DataList_paragraph = new ArrayList<Map<String, Object>>();

		bookcover = new ArrayList<Drawable>();
		bookname = new ArrayList<String>();
		book_writer = new ArrayList<String>();
		book_introduction = new ArrayList<String>();
		items_book = new ArrayList<Map<String, Object>>();
		DataList_book = new ArrayList<Map<String, Object>>();
		//
		adapterString = new String[] { "Cover", "From", "Writer", "content" };
		adapterInt = new int[] { R.id.Cover, R.id.Name, R.id.Writer,
				R.id.Introduction };
		// 线程启动
		loadThread = new NewThread();
		loadThread.start();

		lv_recommend_paragraph
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {
						Intent intent = new Intent(getActivity(),
								SingleActivity.class);
						Bundle mBundle = new Bundle();
						mBundle.putString("name",
								DataList_paragraph.get(position).get("From")
										.toString());
						mBundle.putString("writer",
								DataList_paragraph.get(position).get("Writer")
										.toString());
						mBundle.putString("intro",
								DataList_paragraph.get(position).get("content")
										.toString());
						mBundle.putString("bdigest",
								DataList_paragraph.get(position).get("bdigest")
										.toString());
						mBundle.putString("publish_date", DataList_paragraph
								.get(position).get("publish_date").toString());
						// mBundle.putByteArray("Cover", value)
						mBundle.putString("type", "Paragraph");
						mBundle.putString("bno",
								DataList_paragraph.get(position).get("bno")
										.toString());
						mBundle.putString("bpraise",
								DataList_paragraph.get(position).get("bpraise")
										.toString());
						intent.putExtras(mBundle);
						startActivity(intent);
					}

				});
		ListViewDealer.setListViewHeightBasedOnChildren(lv_recommend_paragraph);
		ListViewDealer.setListViewHeightBasedOnChildren(lv_recommend_book);
		lv_recommend_book.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

				Intent intent = new Intent(getActivity(), SingleActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putString("name",
						DataList_book.get(position).get("From").toString());
				mBundle.putString("writer",
						DataList_book.get(position).get("Writer").toString());
				mBundle.putString("intro",
						DataList_book.get(position).get("content").toString());
				mBundle.putString("bdigest",
						DataList_book.get(position).get("bdigest").toString());
				mBundle.putString("publish_date", DataList_book.get(position)
						.get("publish_date").toString());
				mBundle.putString("type", "Book");
				mBundle.putString("bno", DataList_book.get(position).get("bno")
						.toString());
				mBundle.putString("bpraise",
						DataList_book.get(position).get("bpraise").toString());
				intent.putExtras(mBundle);
				startActivity(intent);
			}

		});

	}

	@Override
	public void onClick(View v) {
		if (!checkNetAndResponse(getView()))
			return;

		Intent intent;
		switch (v.getId()) {
		case R.id.btn_five_minutes:
			intent = new Intent(getActivity(), FiveMinutesActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_ten_minutes:
			intent = new Intent(getActivity(), TenMinutesActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_more01:
			intent = new Intent(getActivity(), MoreParagraphActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_more02:
			intent = new Intent(getActivity(), MoreBookActivity.class);
			startActivity(intent);
			break;
		}
	}

	// 从后台读取数据并进行适配器的相关操作
	public void AnalysisJsonData(String jsonString,
			List<Map<String, Object>> adapterList,
			List<Map<String, Object>> dataList) {
		if (jsonString.equals(null) || jsonString.equals("")) {
			return;
		}
		JSONArray jsonArray = null;
		// Resources res = getResources();
		// Drawable drawable = res.getDrawable(R.drawable.bb2);
		try {
			jsonArray = new JSONArray(jsonString);
		} catch (Exception e) {
			Log.i("MoreActivity", "jsonArray = new JSONArray(result);出错了");
		}

		int iSize = jsonArray.length();
		try {
			for (int i = 0; i < iSize; i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Map<String, Object> singledata = new HashMap<String, Object>();
				Map<String, Object> item = new HashMap<String, Object>();
				// new MyTask().execute(Image_path);
				item.put("Cover", cover);
				singledata.put("Cover", cover);

				item.put("From", jsonObject.getString("bname"));
				singledata.put("From", jsonObject.getString("bname"));

				item.put("Writer", jsonObject.getString("bauthor"));
				singledata.put("Writer", jsonObject.getString("bauthor"));

				item.put("content",
						formatIntroduction(jsonObject.getString("bintro")));
				singledata.put("content", jsonObject.getString("bintro"));

				singledata.put("publish_date",
						jsonObject.getString("publish_date"));
				singledata.put("bdigest", jsonObject.getString("bdigest"));
				singledata.put("bpraise", jsonObject.getString("bpraise"));
				singledata.put("bno", jsonObject.getString("bno"));
				dataList.add(singledata);
				adapterList.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 将introduction格式化一下
	public String formatIntroduction(String str) {
		String result = "";
		if (str.length() <= 40) {
			return str;
		} else {
			result = str.substring(0, 37) + "...";
			return result;
		}

	}

}
