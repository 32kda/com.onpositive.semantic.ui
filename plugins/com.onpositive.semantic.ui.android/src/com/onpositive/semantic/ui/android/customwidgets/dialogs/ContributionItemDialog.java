package com.onpositive.semantic.ui.android.customwidgets.dialogs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.onpositive.semantic.model.ui.actions.ContributionItem;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.ui.android.AndroidImageManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ContributionItemDialog extends CustomHeaderDialog {

	private static final int MIN_WIDTH = 300;

	protected final class ClickListener implements View.OnClickListener {

		protected final IAction actionContribution;
		protected final Button button;

		public ClickListener(IAction actionContribution,
				Button button) {
			this.actionContribution = actionContribution;
			this.button = button;
		}

		@Override
		public void onClick(View v) {
			this.actionContribution.run();
			ContributionItemDialog.this.dismiss();
		}
	}

	protected IContributionItem[] items;
	private LinearLayout layout;

	public ContributionItemDialog(Context context, IContributionItem[] items,
			String title) {
		super(context, 0);
		this.setTitle(title);
		this.items = items;
	}

	public ContributionItemDialog(Context context, int theme,
			IContributionItem[] items) {
		super(context, theme);
		this.items = items;
	}

	@Override
	protected View createContents() {
		Context context = this.getContext();
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		createItemControls(layout);
		layout.setMinimumWidth(ContributionItemDialog.MIN_WIDTH);
		return layout;
	}

	protected void createItemControls(LinearLayout layout) {
		LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		for (final IContributionItem item : this.items) {

			final PropertyChangeListener l = new PropertyChangeListener() {
				final Button[] bs = new Button[1];

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getPropertyName().equals("selected")) {
						bs[0].setSelected((Boolean) event.getNewValue());
					} else if (event.getPropertyName().equals(ContributionItem.ENABLED_PROP_ID)) {
						bs[0].setEnabled((Boolean) event.getNewValue());
					} else if (event.getPropertyName().equals(ContributionItem.VISIBLE_PROP_ID)) {
						bs[0].setVisibility((Boolean) event.getNewValue()?View.VISIBLE:View.GONE);
					}
				}
			};

			final Button button = new Button(getContext()) {
				@Override
				protected void onDetachedFromWindow() {
					super.onDetachedFromWindow();
					item.removePropertyChangeListener(l);
				}
			};
			IAction action = (IAction) item;
			String text = action.getText();
			if (text.length() == 0) {
				text = "Action";
			}
			button.setText(text);
			if (action.getImageDescriptor() != null) {
				Drawable imageDrawable = AndroidImageManager.getImageDrawable(action.getImageDescriptor());
				button.setCompoundDrawablesWithIntrinsicBounds(imageDrawable,null,null,null);
			}
			layout.addView(button, wrapParams);
			if (item instanceof IAction) {
				button.setOnClickListener(new ClickListener(
						(IAction) item, button));
			}
			item.addPropertyChangeListener(l);
			
			button.setEnabled(item.isEnabled());
			button.setVisibility(item.isVisible()?View.VISIBLE:View.GONE);
		}
	}

	public void setItems(IContributionItem[] items) {
		layout.removeAllViews();
		this.items = items;
		createItemControls(layout);
	}
}
