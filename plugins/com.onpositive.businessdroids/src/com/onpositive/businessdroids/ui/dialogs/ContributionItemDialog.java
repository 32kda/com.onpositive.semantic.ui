package com.onpositive.businessdroids.ui.dialogs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ContributionItemDialog extends CustomHeaderDialog {

	private static final int MIN_WIDTH = 300;

	protected final class ClickListener implements View.OnClickListener {

		protected final ActionContribution actionContribution;
		protected final Button button;

		public ClickListener(ActionContribution actionContribution,
				Button button) {
			this.actionContribution = actionContribution;
			this.button = button;
		}

		@Override
		public void onClick(View v) {
			this.actionContribution.onRun();
			ContributionItemDialog.this.dismiss();
		}
	}

	protected final IContributionItem[] items;
	protected final IViewer dataView;

	public ContributionItemDialog(Context context, IContributionItem[] items,
			String title, IViewer dataView) {
		super(context, 0, dataView.getCurrentTheme());
		this.setTitle(title);
		this.items = items;
		this.dataView = dataView;
	}

	public ContributionItemDialog(Context context, int theme,
			IContributionItem[] items, StructuredDataView dataView) {
		super(context, theme, dataView.getCurrentTheme());
		this.items = items;
		this.dataView = dataView;
	}

	@Override
	protected View createContents() {
		Context context = this.getContext();
		LinearLayout layout = new LinearLayout(context);
		updateContents(layout);
		
		LinearLayout.LayoutParams wrapParams = doOk();
		for (final IContributionItem item : this.items) {

			final PropertyChangeListener l = new PropertyChangeListener() {
				final Button[] bs = new Button[1];

				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getPropertyName().equals("selected")) {
						bs[0].setSelected((Boolean) event.getNewValue());
					}
				}
			};

			final Button button = new Button(context) {
				@Override
				protected void onDetachedFromWindow() {
					super.onDetachedFromWindow();
					item.removePropertyChangeListener(l);
				}
			};
			button.setText(item.getText());
			layout.addView(button, wrapParams);
			if (item instanceof ActionContribution) {
				button.setOnClickListener(new ClickListener(
						(ActionContribution) item, button));
			}
			item.addPropertyChangeListener(l);

			button.setEnabled(item.isEnabled());
		}
		constraints(layout);
		return layout;
	}

	protected void updateContents(LinearLayout layout) {
		layout.setOrientation(LinearLayout.VERTICAL);
	}

	protected LinearLayout.LayoutParams doOk() {
		LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		return wrapParams;
	}

	protected void constraints(LinearLayout layout) {
		layout.setMinimumWidth(ContributionItemDialog.MIN_WIDTH);
	}
}
