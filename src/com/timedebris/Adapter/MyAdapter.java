package com.timedebris.Adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.timedebris.R;

public class MyAdapter extends BaseAdapter {

	private SharedPreferences sp;
	private Context context;
	private List<Map<String, Object>> listitems;
	private LayoutInflater mInflater;
	DisplayImageOptions options;
	String[] imageUrls;
	int DisNum, AppNum;

	// 构造器
	public MyAdapter(Context context, List<Map<String, Object>> listitems) {
		sp = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.listitems = listitems;
		// imageUrls = com.androidlab.eatschool.util.Constants
		// .initImageUrlsHotest();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.cloud)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listitems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.recommend_listitem, null);
			holder.Cover = (ImageView) convertView.findViewById(R.id.Cover);
			holder.Name = (TextView) convertView.findViewById(R.id.Name);
			holder.Writer = (TextView) convertView.findViewById(R.id.Writer);
			holder.Introduction = (TextView) convertView
					.findViewById(R.id.Introduction);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		imageLoader.displayImage("http://7xkl94.com1.z0.glb.clouddn.com/"
				+ (String) listitems.get(position).get("From") + ".jpg",
				holder.Cover, options, new SimpleImageLoadingListener() {
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
					}
				}, new ImageLoadingProgressListener() {
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {

					}
				});
		holder.Name.setText((String) listitems.get(position).get("From"));
		holder.Writer.setText((String) listitems.get(position).get("Writer"));
		holder.Introduction.setText((String) listitems.get(position).get(
				"content"));
		return convertView;
	}

	static final class ViewHolder {

		public ImageView Cover;
		public TextView Name, Writer, Introduction;

	}
}
