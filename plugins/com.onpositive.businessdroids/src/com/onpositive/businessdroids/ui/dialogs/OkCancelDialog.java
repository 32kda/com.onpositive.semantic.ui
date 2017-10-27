package com.onpositive.businessdroids.ui.dialogs;

import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;


public abstract class OkCancelDialog extends CustomHeaderDialog {

	protected android.view.View.OnClickListener buttonListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == OkCancelDialog.this.okButton) {
				OkCancelDialog.this.performOk();
			} else {
				OkCancelDialog.this.performCancel();
			}
		}
	};

	protected View okButton;
	protected View cancelButton;

	public OkCancelDialog(Context context, ITheme dialogTheme) {
		super(context, 0, dialogTheme);
	}

	public OkCancelDialog(Context context, int theme, ITheme dialogTheme) {
		super(context, theme, dialogTheme);
	}

	protected abstract void performOk();

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		View header = this.createHeader();
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		if (header != null) {
			layout.addView(header, params);
		}
		ScrollView scrollView = new ScrollView(context);
		View contents = this.createContents();
		LayoutParams contentParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		contentParams.weight = 2;
		if (this.needScrollView()) {
			int measuredHeight = this.measureDesiredHeight(contents);
			Display display = ((WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			int height = display.getHeight();
			scrollView.addView(contents);
			scrollView.setFillViewport(true);
			scrollView.setMinimumHeight(Math
					.min(measuredHeight, height * 2 / 3));
			layout.addView(scrollView, contentParams);
		} else {
			layout.addView(contents, contentParams);
		}
		LinearLayout buttonLayout = this.createButtonBar(context);
		layout.addView(buttonLayout, params);
		this.setContentView(layout);
	}

	protected int measureDesiredHeight(View contents) {
		contents.measure(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				MeasureSpec.UNSPECIFIED);
		int measuredHeight = contents.getMeasuredHeight();
		return measuredHeight;
	};

	protected LinearLayout createButtonBar(Context context) {
		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.okButton = new ImageButton(context);
		this.okButton.setOnClickListener(this.buttonListener);
		Drawable okIcon = this.dialogTheme.getIconProvider().getOkIcon(
				this.getContext());
		((ImageView) this.okButton).setImageDrawable(okIcon);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		buttonLayout.addView(this.okButton, params);
		this.cancelButton = new ImageButton(context);
		this.cancelButton.setOnClickListener(this.buttonListener);
		Drawable cancelIcon = this.dialogTheme.getIconProvider().getCancelIcon(
				this.getContext());
		((ImageView) this.cancelButton).setImageDrawable(cancelIcon);
		buttonLayout.addView(this.cancelButton, params);
		return buttonLayout;
	}

}
