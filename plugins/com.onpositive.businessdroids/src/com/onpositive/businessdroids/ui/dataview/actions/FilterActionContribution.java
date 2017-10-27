package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;

import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.filters.IFilterSetupVisualizer;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.ActionContribution;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.themes.ITheme;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;


public class FilterActionContribution extends AbstractCompositeContributionItem {

	protected final IFilter[] possibleFilters;
	protected final IFilterSetupVisualizer filterSetupVisualizer;
	protected final StructuredDataView dataView;

	public FilterActionContribution(StructuredDataView dataView,
			IFilter[] possibleFilters,
			IFilterSetupVisualizer filterSetupVisualizer, Drawable icon) {
		super("", icon);
		this.dataView = dataView;
		this.possibleFilters = possibleFilters;
		this.filterSetupVisualizer = filterSetupVisualizer;
	}

	// @Override
	// public void run() {
	// if (possibleFilters.length == 0)
	// return;
	// if (possibleFilters.length == 1)
	// {
	// filterSetupVisualizer.setupFilter(possibleFilters[0],dataView);
	// } else {
	// Context context = dataView.getContext();
	// Dialog dialog = new Dialog(context);
	// LinearLayout layout = new LinearLayout(context);
	// layout.setOrientation(LinearLayout.VERTICAL);
	// fillLayout(layout,dialog);
	// dialog.setTitle("Filter");
	// dialog.setContentView(layout);
	// dialog.show();
	// }
	// }

	protected void fillLayout(LinearLayout layout, Dialog dialog) {
		for (IFilter filter : this.possibleFilters) {
			Button button = new Button(layout.getContext());
			button.setText(filter.getTitle());
			button.setOnClickListener(this
					.createOnClickListener(filter, dialog));
			layout.addView(button, new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		}
	}

	protected OnClickListener createOnClickListener(final IFilter filter,
			final Dialog dialog) {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				FilterActionContribution.this.filterSetupVisualizer
						.setupFilter(filter,
								FilterActionContribution.this.dataView);
				dialog.dismiss();
			}
		};
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Drawable getIcon() {
		if (this.icon == null) {
			ITheme currentTheme = this.dataView.getCurrentTheme();
			return new LayerDrawable(new Drawable[] {
					currentTheme.getQuickActionBackgroundDrawable(),
					currentTheme.getIconProvider().getFilterIconBlack(
							this.dataView.getContext()) });
		}
		return this.icon;
	}

	@Override
	public String getText() {
		return "Add Filters";
	}

	@Override
	public String getId() {
		return "Filters Add";
	}

	@Override
	public IContributionItem[] getChildren() {
		ArrayList<IContributionItem> items = new ArrayList<IContributionItem>();
		for (IFilter filter : this.possibleFilters) {
			ActionContribution contribution = new AddFilterActionContribution(
					filter.getTitle(), filter, this.dataView,
					this.filterSetupVisualizer);
			items.add(contribution);
		}
		return items.toArray(new IContributionItem[0]);
	}

}
