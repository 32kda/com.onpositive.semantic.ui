package com.onpositive.semantic.ui.android.customwidgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccorditionView extends LinearLayout {

	protected static final int HEADER_VERTICAL_PADDING = 7;
	protected List<String> captions = new ArrayList<String>();
	private View[] children;
	private View[] wrappedChildren;
	private boolean initialized = false;

	public AccorditionView(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
	}

	public void addView(String caption, View view) {
		addView(view);
		captions.add(caption);
	}

	public void createSections() {
		if (initialized) {
			return;
		}
		int childCount = getChildCount();

		children = new View[childCount];
		wrappedChildren = new View[childCount];

		for (int i = 0; i < childCount; i++) {
			children[i] = getChildAt(i);
		}
		removeAllViews();

		for (int i = 0; i < childCount; i++) {
			wrappedChildren[i] = getView(i);
			wrappedChildren[i].setVisibility(GONE);
			View header = getViewHeader(i);
			View footer = getViewFooter(i);
			final LinearLayout section = new LinearLayout(getContext());
			section.setOrientation(LinearLayout.VERTICAL);
			section.addView(header, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			section.addView(wrappedChildren[i]);
			section.addView(footer, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			addView(section, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}
		initialized = true;
	}

	private View getViewFooter(int i) {
		return new TextView(getContext());
	}

	private View getViewHeader(final int position) {
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setPadding(0,HEADER_VERTICAL_PADDING,0,HEADER_VERTICAL_PADDING);
		final ToggleImageLabeledButton foldButton = new ToggleImageLabeledButton(
				getContext());
		layout.addView(foldButton, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		TextView textView = new TextView(getContext());
		textView.setGravity(Gravity.CENTER);
		textView.setText(captions.get(position));
		layout.addView(textView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		if (foldButton instanceof ToggleImageLabeledButton) {
			final ToggleImageLabeledButton toggleButton = (ToggleImageLabeledButton) foldButton;
			toggleButton
					.setState(wrappedChildren[position].getVisibility() == VISIBLE);
		}

		final OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wrappedChildren[position].getVisibility() == VISIBLE) {
					wrappedChildren[position].setVisibility(GONE);
				} else {
					wrappedChildren[position].setVisibility(VISIBLE);
				}
				requestLayout();
			}
		};
		foldButton.setOnClickListener(onClickListener);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onClickListener.onClick(v);

				if (foldButton instanceof ToggleImageLabeledButton) {
					final ToggleImageLabeledButton toggleButton = (ToggleImageLabeledButton) foldButton;
					toggleButton.setState(wrappedChildren[position]
							.getVisibility() == VISIBLE);
				}

			}
		});

		return layout;
	}

	private View getView(int i) {
		return children[i];
	}

}
