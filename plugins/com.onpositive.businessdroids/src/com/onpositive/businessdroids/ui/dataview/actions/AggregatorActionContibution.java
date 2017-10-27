package com.onpositive.businessdroids.ui.dataview.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IModesProvider;
import com.onpositive.businessdroids.ui.actions.AbstractCompositeContributionItem;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;

import android.graphics.drawable.Drawable;


public class AggregatorActionContibution extends
		AbstractCompositeContributionItem {

	protected final List<IAggregator> possibleAggregators;
	protected final IColumn column;
	protected final StructuredDataView dataView;

	public AggregatorActionContibution(Drawable icon, IColumn column,
			List<IAggregator> possibleAggregators, StructuredDataView dataView) {
		super("Select aggregator", icon);
		this.column = column;
		this.possibleAggregators = possibleAggregators;
		this.dataView = dataView;
	}

	public AggregatorActionContibution(Drawable icon, IColumn column,
			IAggregator aggregator, StructuredDataView dataView) {
		super("Select aggregator", icon);
		this.column = column;
		this.dataView = dataView;
		this.possibleAggregators = new ArrayList<IAggregator>();
		this.possibleAggregators.add(aggregator);
	}

	// @Override
	// public Drawable getIcon() {
	// return ThemeManager.getCurrentTheme().getQuickActionBackgroundDrawable();
	// }

	// @Override
	// public void run() {
	// Context context = dataView.getContext();
	// Dialog dialog = new Dialog(context);
	// LinearLayout layout = new LinearLayout(context);
	// layout.setOrientation(LinearLayout.VERTICAL);
	// fillLayout(layout,dialog);
	// dialog.setTitle("Aggregator");
	// dialog.setContentView(layout);
	// dialog.show();
	// }
	//
	// protected void fillLayout(LinearLayout layout, Dialog dialog) {
	// for (Iterator<IAggregator> iterator = possibleAggregators.iterator();
	// iterator.hasNext();) {
	// IAggregator aggregator = (IAggregator) iterator.next();
	// if (aggregator instanceof IModesProvider)
	// {
	// Map<String, Integer> supportedModes = ((IModesProvider)
	// aggregator).getSupportedModes();
	// String[] titles = supportedModes.keySet().toArray(new String[0]);
	// Arrays.sort(titles);
	// for (int i = 0; i < titles.length; i++) {
	// Button button = new Button(layout.getContext());
	// button.setText(titles[i]);
	// button.setOnClickListener(createOnClickListener(aggregator,
	// supportedModes.get(titles[i]),dialog));
	// layout.addView(button, new
	// LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	// }
	// } else {
	// Button button = new Button(layout.getContext());
	// button.setText(aggregator.getTitle());
	// button.setOnClickListener(createOnClickListener(aggregator, 0,dialog));
	// layout.addView(button, new
	// LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
	// }
	//
	// }
	//
	// }
	//
	// protected OnClickListener createOnClickListener(final IAggregator
	// aggregator,
	// final int mode, final Dialog dialog) {
	// return new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// TableModel tableModel = dataView.getTableModel();
	// IAggregator oldAggregator = tableModel.getFieldAggregator(field);
	// if (aggregator instanceof IModesProvider)
	// {
	// if (oldAggregator == aggregator && ((IModesProvider)
	// oldAggregator).getMode() == mode)
	// {
	// dialog.dismiss();
	// return;
	// }
	// ((IModesProvider) aggregator).setMode(mode);
	// } else if (oldAggregator == aggregator) {
	// dialog.dismiss();
	// return;
	// }
	// field.setAggregator(aggregator);
	// dialog.dismiss();
	// }
	// };
	// }

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public IContributionItem[] getChildren() {
		List<IContributionItem> result = new ArrayList<IContributionItem>();
		Drawable aggregateIcon = this.dataView.getCurrentTheme()
				.getIconProvider().getAggregateIcon(this.dataView.getContext());
		for (IAggregator aggregator : this.possibleAggregators) {
			if (aggregator instanceof IModesProvider) {
				Map<String, Integer> supportedModes = ((IModesProvider) aggregator)
						.getSupportedModes();
				String[] titles = supportedModes.keySet()
						.toArray(new String[0]);
				Arrays.sort(titles);
				for (String title : titles) {
					result.add(new SetAggregatorActionContribution(title,
							aggregateIcon, this.column, aggregator,
							this.dataView, supportedModes.get(title)));
				}
			} else {
				SetAggregatorActionContribution actionContribution = new SetAggregatorActionContribution(
						aggregator.getTitle(), aggregateIcon, this.column,
						aggregator, this.dataView);
				result.add(actionContribution);
			}
		}
		return result.toArray(new IContributionItem[0]);
	}

}
