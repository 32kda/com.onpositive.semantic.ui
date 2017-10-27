package com.onpositive.businessdroids.ui.dataview.renderers;

import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.filters.IPossibleFiltersProvider;
import com.onpositive.businessdroids.model.impl.IEditableField;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.actions.IContributionItem;

import android.graphics.Point;


public interface IEditableColumn extends IColumn, IEditableField {

	public abstract void setRenderer(IFieldRenderer renderer);

	public abstract void setContributions(List<IContributionItem> contributions);

	public abstract void addContribution(IContributionItem item);

	public abstract void removeContribution(IContributionItem item);

	public abstract void setFixedImageSize(Point fixedImageSize);

	public abstract void setFitImageToContent(boolean fitImageToContent);
	
	

	public abstract void setPossibleFiltersProvider(
			IPossibleFiltersProvider possibleFiltersProvider);

	@Override
	public abstract void setPropertyValue(Object object, Object value);

	
	public abstract void setCaption(boolean caption);

	@Override
	public abstract void setType(Class<?> type);

	@Override
	public abstract void setId(String id);

	@Override
	public abstract void setTitle(String title);

	public void setImageProvider(IFieldImageProvider imageProvider);
	
	public abstract void setCanShrink(Boolean shrink);

}