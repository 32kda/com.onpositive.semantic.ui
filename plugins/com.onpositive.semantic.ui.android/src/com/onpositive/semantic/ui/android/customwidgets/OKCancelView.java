package com.onpositive.semantic.ui.android.customwidgets;

import com.onpositive.semantic.ui.android.R;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Encapsulates a composite (possibly scrolled) witk OK & Cancel butons. 
 * Override if needed
 * @author Dmitry Karpenko
 *
 */
public abstract class OKCancelView extends LinearLayout {
	
	protected android.view.View.OnClickListener buttonListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == OKCancelView.this.okButton) {
				performOk();
			} else {
				performCancel();
			}
		}
	};
	
	protected ImageButton okButton;

	protected ImageButton cancelButton;

	public interface IOKCancelListener {
		public void onOK();
		public void onCancel();
	}

	public OKCancelView(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		createContent();
	}

	protected void createContent() {
		Context context = getContext();
		ScrollView scrollView = new ScrollView(context);
		View contents = getContents();
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
			addView(scrollView, contentParams);
		} else {
			addView(contents, contentParams);
		}
		LinearLayout buttonLayout = createButtonBar(context);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		addView(buttonLayout, params);
	}
	
	protected LinearLayout createButtonBar(Context context) {
		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		this.okButton = new ImageButton(context);
		this.okButton.setOnClickListener(this.buttonListener);
		Drawable okIcon = context.getResources().getDrawable(R.drawable.ok);
		((ImageView) this.okButton).setImageDrawable(okIcon);
		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		buttonLayout.addView(this.okButton, params);
		this.cancelButton = new ImageButton(context);
		this.cancelButton.setOnClickListener(this.buttonListener);
		Drawable cancelIcon = context.getResources().getDrawable(R.drawable.cancel);
		((ImageView) this.cancelButton).setImageDrawable(cancelIcon);
		buttonLayout.addView(this.cancelButton, params);
		return buttonLayout;
	}
	
	protected boolean needScrollView() {
		return true;
	}
	
	protected int measureDesiredHeight(View contents) {
		contents.measure(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				MeasureSpec.UNSPECIFIED);
		int measuredHeight = contents.getMeasuredHeight();
		return measuredHeight;
	};

	protected abstract View getContents();

	protected abstract void performOk();

	protected abstract void performCancel();

}
