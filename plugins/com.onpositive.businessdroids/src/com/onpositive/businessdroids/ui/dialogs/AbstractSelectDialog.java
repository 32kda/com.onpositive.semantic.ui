package com.onpositive.businessdroids.ui.dialogs;

import java.util.Collection;

import com.onpositive.businessdroids.ui.themes.ITheme;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;


public abstract class AbstractSelectDialog<T> extends CustomHeaderDialog {

	protected final Collection<Object> items;
	protected T result = null;

	public AbstractSelectDialog(Context context, int theme,
			Collection<Object> items, ITheme dialogTheme) {
		super(context, theme, dialogTheme);
		this.items = items;
	}

	public AbstractSelectDialog(Context context, Collection<Object> items,
			ITheme dialogTheme) {
		this(context, 0, items, dialogTheme);
	}

	@Override
	protected View createContents() {
		final Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);

		LayoutParams wrapParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		wrapParams.weight = 0;
		ListView listView = new ListView(context);
		this.configureAdapter(context, listView);
		LinearLayout ll0 = new LinearLayout(context);
		ll0.addView(listView, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		LayoutParams gridParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		gridParams.weight = 2;
		ll0.setMinimumHeight((int) (new TextView(context).getTextSize() * 8));
		layout.addView(ll0, gridParams);
		Button cancelButton = new Button(context);
		cancelButton.setText(this.dialogTheme.getLabelProvider()
				.getCancelTitle());
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AbstractSelectDialog.this.result = null;
				AbstractSelectDialog.this.dismiss();
			}
		});
		layout.addView(cancelButton, wrapParams);
		layout.setMinimumWidth(300);
		// layout.setMinimumHeight((int) (new TextView(context).getTextSize() *
		// 4));
		return layout;
	}

	protected abstract ArrayAdapter<T> configureAdapter(Context context,
			ListView listView);

	public T getResult() {
		return this.result;
	}

}
