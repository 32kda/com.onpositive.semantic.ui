package com.onpositive.businessdroids.model;

import java.util.Comparator;
import java.util.List;

import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IAggregatorChangeListener;
import com.onpositive.businessdroids.model.filters.IPossibleFiltersProvider;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;

import android.graphics.Point;

public interface IColumn extends IField {

	public static final int INVISIBLE = 0;
	public static final int VISIBLE = 1;
	public static final int CAN_GROW = 1;
	public static final int AUTOMATIC = 2;

	public abstract IFieldRenderer getRenderer();

	/*
	 * Image related group of methods
	 */
	public abstract IFieldImageProvider getImageProvider();

	/**
	 * Returns predefined image size
	 * 
	 * @return Predefined in-column image size or <code>null</code> if image
	 *         size should be calculated dynamically
	 */
	public abstract Point getFixedImageSize();

	public abstract boolean isFitImageToContent();

	/*
	 * Visibility related group of methods
	 */

	public abstract int getVisible();

	public abstract void setVisible(int visible);

	public abstract boolean isAlwaysVisible();

	public abstract boolean isCaption();

	/*
	 * Actions and filters
	 */
	public abstract IPossibleFiltersProvider getPossibleFiltersProvider();

	public abstract List<IContributionItem> getContributions();

	public abstract void addAggregatorChangeListener(
			IAggregatorChangeListener listener);

	public abstract void removeAggregatorChangeListener(
			IAggregatorChangeListener listener);

	public abstract IAggregator getAggregator();

	public abstract Comparator getComparator(boolean ascending);

	public abstract void setAggregator(IAggregator aggregator);

	public abstract boolean canShrink();

	public abstract boolean canGrow();

	IEditorCreationFactory getEditorCreationFactory();

	void setEditorCreationFactory(IEditorCreationFactory factory);

	public IGroupingCalculator getGroupingCalculator();

	public boolean isGroupable();
	
	public boolean addWhenGrouped();
}