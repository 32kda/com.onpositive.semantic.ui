package com.onpositive.semantic.ui.android.customwidgets.dialogs;


import com.onpositive.semantic.ui.android.customwidgets.OKCancelView;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;


public abstract class OkCancelDialog extends CustomHeaderDialog {
	
	public static final int OK_RESULT = 1;
	public static final int CANCEL_RESULT = 0;
	private int result = 0;

//	protected android.view.View.OnClickListener buttonListener = new android.view.View.OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			if (v == OkCancelDialog.this.okButton) {
//				result = 1;
//				OkCancelDialog.this.performOk();
//			} else {
//				result = 0;
//				OkCancelDialog.this.performCancel();
//			}
//		}
//	};

//	protected View okButton;
//	protected View cancelButton;

	public OkCancelDialog(Context context) {
		super(context);
	}

	public OkCancelDialog(Context context, int theme) {
		super(context, theme);
	}

	protected abstract void performOk();

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		Context context = this.getContext();
//		LinearLayout layout = new LinearLayout(context);
//		layout.setOrientation(LinearLayout.VERTICAL);
		OKCancelView view = new OKCancelView(context) {
			
			@Override
			protected void performOk() {
				OkCancelDialog.this.performOk();
				dismiss();
			}
			
			@Override
			protected void performCancel() {
				OkCancelDialog.this.performCancel();
				dismiss();
			}
			
			@Override
			protected View getContents() {
				return createContents();
			}
		};
		View header = this.createHeader();
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		if (header != null) {
			view.addView(header, 0, params);
		}
//		ScrollView scrollView = new ScrollView(context);
//		View contents = this.createContents();
//		LayoutParams contentParams = new LayoutParams(
//				android.view.ViewGroup.LayoutParams.FILL_PARENT,
//				android.view.ViewGroup.LayoutParams.FILL_PARENT);
//		contentParams.weight = 2;
//		if (this.needScrollView()) {
//			int measuredHeight = this.measureDesiredHeight(contents);
//			Display display = ((WindowManager) context
//					.getSystemService(Context.WINDOW_SERVICE))
//					.getDefaultDisplay();
//			int height = display.getHeight();
//			scrollView.addView(contents);
//			scrollView.setFillViewport(true);
//			scrollView.setMinimumHeight(Math
//					.min(measuredHeight, height * 2 / 3));
//			layout.addView(scrollView, contentParams);
//		} else {
//			layout.addView(contents, contentParams);
//		}
//		LinearLayout buttonLayout = this.createButtonBar(context);
//		layout.addView(buttonLayout, params);
		this.setContentView(view);
	}

//	protected int measureDesiredHeight(View contents) {
//		contents.measure(android.view.ViewGroup.LayoutParams.FILL_PARENT,
//				MeasureSpec.UNSPECIFIED);
//		int measuredHeight = contents.getMeasuredHeight();
//		return measuredHeight;
//	};

//	protected LinearLayout createButtonBar(Context context) {
//		LinearLayout buttonLayout = new LinearLayout(context);
//		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
//		this.okButton = new ImageButton(context);
//		this.okButton.setOnClickListener(this.buttonListener);
//		Drawable okIcon = context.getResources().getDrawable(R.drawable.ok);
//		((ImageView) this.okButton).setImageDrawable(okIcon);
//		LayoutParams params = new LayoutParams(
//				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
//				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//		params.weight = 1;
//		buttonLayout.addView(this.okButton, params);
//		this.cancelButton = new ImageButton(context);
//		this.cancelButton.setOnClickListener(this.buttonListener);
//		Drawable cancelIcon = context.getResources().getDrawable(R.drawable.cancel);
//		((ImageView) this.cancelButton).setImageDrawable(cancelIcon);
//		buttonLayout.addView(this.cancelButton, params);
//		return buttonLayout;
//	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

}
