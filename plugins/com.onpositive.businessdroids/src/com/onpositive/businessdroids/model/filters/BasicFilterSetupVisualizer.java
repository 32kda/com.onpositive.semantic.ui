package com.onpositive.businessdroids.model.filters;

import java.util.Arrays;
import java.util.Collection;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dialogs.BooleanFilterDialog;
import com.onpositive.businessdroids.ui.dialogs.ComparableFilterDialog;
import com.onpositive.businessdroids.ui.dialogs.DateRangeFilterDialog;
import com.onpositive.businessdroids.ui.dialogs.ObjectSelectDialog;
import com.onpositive.businessdroids.ui.dialogs.StringFilterDialog;
import com.onpositive.businessdroids.ui.dialogs.TableItemSelectDialog;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class BasicFilterSetupVisualizer extends CompositeFilterSetupVisualizer {

	// protected static int maxValuesToShow = 10;

	@Override
	public boolean setupFilter(IFilter filter, StructuredDataView dataView) {
		if (super.setupFilter(filter, dataView)) {
			return true;
		}
		ITheme dialogTheme = dataView.getCurrentTheme();
		if (filter instanceof BooleanFilter) {
			BooleanFilterDialog dialog = new BooleanFilterDialog(
					dataView.getContext(), (BooleanFilter) filter, dialogTheme);
			dialog.show();
			return true;
		} else if (filter instanceof DateFilter) {
			// SimpleDateFilterDialog dialog = new
			// SimpleDateFilterDialog(dataView.getContext(), (ComparableFilter)
			// filter);
			DateRangeFilterDialog dialog = new DateRangeFilterDialog(
					dataView.getContext(), (ComparableFilter) filter,
					dialogTheme);
			dialog.show();
			return true;
		} else if (filter instanceof ComparableFilter) {
			ComparableFilterDialog dialog = new ComparableFilterDialog(
					dataView.getContext(), (ComparableFilter) filter,
					dialogTheme);
			dialog.show();
			return true;
		} else if (filter instanceof ExplicitValueFilter) {
			this.configureExplicitValueFilter((ExplicitValueFilter) filter,
					dataView);
			return true;
		} else if (filter instanceof BasicStringFilter) {
			StringFilterDialog dialog = new StringFilterDialog(
					dataView.getContext(), (BasicStringFilter) filter,
					dataView.getTableModel(), dialogTheme);
			dialog.show();
			return true;
		}
		return false;
	}

	protected void configureExplicitValueFilter(
			final ExplicitValueFilter filter, StructuredDataView dataView) {
		Object[] uniqueValuesForField = filter.getTableModel()
				.getUniqueValuesForColumnAsArray(filter.getColumn());
		if (uniqueValuesForField.length == 0) {
			return;
		}
		if (!filter.getTableModel().hasFilter(filter)) {
			filter.setValues(Arrays.asList(uniqueValuesForField));
		}
		final ObjectSelectDialog selectDialog = new TableItemSelectDialog(
				Arrays.asList(uniqueValuesForField), dataView,
				filter.getColumn(), filter.getValues(),
				dataView.getCurrentTheme());
		selectDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				Collection<Object> result = selectDialog.getResult();
				if ((result != null) && (result.size() > 0)
						&& !filter.getValues().equals(result)) {
					filter.setValues(result);
					filter.getTableModel().addFilter(filter);
				}
			}
		});
		selectDialog.show();
	}

	@Override
	public void registerSetupVisualizer(Class<? extends IFilter> type,
			IFilterSetupVisualizer visualizer) {
		// TODO Auto-generated method stub

	}

}
