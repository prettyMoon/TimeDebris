package com.timedebris.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.timedebris.R;

public class ConfirmDialog extends Dialog {

	private Context context;
	private String title, hint;
	private String confirmButtonText;
	private ClickListenerInterface clickListenerInterface;

	// 接口对外实现按钮点击事件
	public interface ClickListenerInterface {
		public void doConfirm();
	}

	public ConfirmDialog(Context context, String title, String hint,
			String confirmButtonText) {
		super(context);
		this.context = context;
		this.title = title;
		this.hint = hint;
		this.confirmButtonText = confirmButtonText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init();
	}

		
	// 初始化
	public void init() {
		// 获取到布局加载器，并加载xml文件
		LayoutInflater inflate = LayoutInflater.from(context);
		View view = inflate.inflate(R.layout.confrirm_dialog, null);
		setContentView(view);
		setTitle(title);
		// 初始化Dialog
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title_dialog);
		TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint_dialog);
		TextView tv_confirm = (TextView) view
				.findViewById(R.id.tv_confirm_dialog);

		tv_title.setText(title);
		tv_hint.setText(hint);
		tv_confirm.setText(confirmButtonText);

		tv_confirm.setOnClickListener(new ClickListener());

		// 修改dialogWindow的宽度为屏幕的0.8
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		// 获取屏幕的宽高
		DisplayMetrics d = context.getResources().getDisplayMetrics();
		lp.width = (int) (d.widthPixels * 0.8);
		dialogWindow.setAttributes(lp);
	}

	public void setClickListener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}

	private class ClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			clickListenerInterface.doConfirm();
		}
	}

}
