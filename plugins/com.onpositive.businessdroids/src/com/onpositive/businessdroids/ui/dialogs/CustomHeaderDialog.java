package com.onpositive.businessdroids.ui.dialogs;

import com.onpositive.businessdroids.ui.dataview.renderers.impl.DialogHeaderSeparatorDrawable;
import com.onpositive.businessdroids.ui.themes.BasicIconProvider;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public abstract class CustomHeaderDialog extends Dialog {

	protected ImageButton closeButton;
	protected TextView titleView;
	protected final ITheme dialogTheme;
	protected CharSequence title = "";

	// public CustomHeaderDialog(Context context) {
	// this(context, 0);
	// }

	// public CustomHeaderDialog(Context context, int theme) {
	// this(context, theme, null);
	// }

	public CustomHeaderDialog(Context context, int theme, ITheme dialogTheme) {
		super(context, theme);
		this.dialogTheme = dialogTheme;
		this.configureAppearance();
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		this.setHeaderText(title);
	}
	

	protected View createHeader() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		this.titleView = new TextView(context);

		TextAppearanceSpan span = new TextAppearanceSpan(context,
				android.R.style.TextAppearance_DialogWindowTitle);
		float size = span.getTextSize();
		this.closeButton = new ImageButton(this.getContext());
		Drawable clearIcon;
		if (this.dialogTheme != null) {
			clearIcon = this.dialogTheme.getIconProvider()
					.getCloseIcon(context);
		} else {
			clearIcon = new BasicIconProvider().getCloseIcon(context);
		}
		if (clearIcon instanceof BitmapDrawable) {
			int height = ((BitmapDrawable) clearIcon).getBitmap().getHeight();
			int density = ((BitmapDrawable) clearIcon).getBitmap().getDensity();
			((BitmapDrawable) clearIcon).setTargetDensity((int) Math
					.round(density * (2.0 * size / height)));
		}
		this.closeButton.setImageDrawable(clearIcon);
		this.closeButton.setBackgroundDrawable(null);
		this.closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomHeaderDialog.this.doClose();
			}
		});

		this.titleView.setGravity(Gravity.CENTER);
		this.titleView.setText(this.title);
		this.titleView.setMinHeight((int) (this.titleView.getTextSize() * 2.5));

		this.titleView.setPadding(5, 3, 5, 3);
		if (this.dialogTheme != null) {
			this.titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,this.dialogTheme.getActionBarTextSize());
			this.titleView.setTextColor(this.dialogTheme
					.getDialogTitleFontColor());
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.weight = 2;
		params.gravity = Gravity.CENTER;
		layout.addView(this.titleView, params);
		layout.addView(this.closeButton, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.setBackgroundDrawable(new DialogHeaderSeparatorDrawable(
				Color.GRAY));
		return layout;
	}

	protected void setHeaderText(CharSequence title) {
		this.title = title;
		if (this.titleView != null) {
			this.titleView.setText(title);
		}
	}

	protected void doClose() {
		this.performCancel();
	}

	protected void configureAppearance() {

	}

	protected boolean needScrollView() {
		return true;
	}

	protected abstract View createContents();

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		View header = this.createHeader();
		if (header != null) {
			layout.addView(header, new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
		}
		android.widget.LinearLayout.LayoutParams params = getRootParam();
		if (this.needScrollView()) {
			ScrollView scrollView = new ScrollView(context);
			scrollView.setFillViewport(true);
			scrollView.addView(this.createContents());
			params.weight = 2;
			layout.addView(scrollView, params);
		} else {
			layout.addView(this.createContents(), params);
		}
		this.setContentView(layout);
	}

	protected android.widget.LinearLayout.LayoutParams getRootParam() {
		return new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	}

	protected void performCancel() {
		this.dismiss();
	}

}