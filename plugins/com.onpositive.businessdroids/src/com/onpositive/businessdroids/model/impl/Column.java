package com.onpositive.businessdroids.model.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IEditorCreationFactory;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IAggregatorChangeListener;
import com.onpositive.businessdroids.model.aggregation.IdentityAggregator;
import com.onpositive.businessdroids.model.filters.IPossibleFiltersProvider;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.renderers.BasicPossibleFiltersProvider;
import com.onpositive.businessdroids.ui.dataview.renderers.IEditableColumn;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;

import android.graphics.Point;

public class Column implements Comparable<IColumn>, IEditableColumn,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected transient IField field;
	protected IFieldRenderer renderer;
	protected transient List<IContributionItem> contributions;
	protected transient Point fixedImageSize = null;
	protected boolean fitImageToContent = true;
	protected int visible = IColumn.AUTOMATIC;
	protected transient IPossibleFiltersProvider possibleFiltersProvider;
	protected IFieldImageProvider imageProvider;
	protected IAggregator aggregatorUsed = IdentityAggregator.INSTANCE;
	protected transient List<IAggregatorChangeListener> aggregatorChangeListeners = new ArrayList<IAggregatorChangeListener>();
	private String title;
	private String id;
	private boolean caption;
	private String[] categories;
	
	@Override
	public boolean isGroupable() {
		return true;
	}

	protected IEditorCreationFactory factory;

	@Override
	public boolean equals(Object o) {
		if (o instanceof IField) {
			IField fl = (IField) o;
			String id2 = this.getId();
			if (id2 != null && id2.length() > 0) {
				return id2.equals(fl.getId());
			}
		}
		return super.equals(o);
	}

	public Column(IField field) {
		this.field = field;
		this.possibleFiltersProvider = new BasicPossibleFiltersProvider(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getImageProvider()
	 */
	@Override
	public IFieldImageProvider getImageProvider() {
		return this.imageProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setImageProvider(com
	 * .onpositive.android.dataview.imageprovider.IImageProvider)
	 */

	@Override
	public void setImageProvider(IFieldImageProvider imageProvider) {
		this.imageProvider = imageProvider;
	}

	public Column(IField field, IFieldRenderer renderer) {
		this(field);
		this.renderer = renderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getAggregatorUsed()
	 */
	@Override
	public IAggregator getAggregator() {
		return this.aggregatorUsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getId()
	 */
	@Override
	public String getId() {
		if (this.id != null) {
			return this.id;
		}
		return this.field.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getType()
	 */
	@Override
	public Class<?> getType() {
		return this.field.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#isCaption()
	 */
	@Override
	public boolean isCaption() {
		return this.caption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getField()
	 */
	public IField getField() {
		return this.field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setField(com.onpositive
	 * .android.dataview.datamodel.IField)
	 */

	public void setField(IField field) {
		this.field = field;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getRenderer()
	 */
	@Override
	public IFieldRenderer getRenderer() {
		return this.renderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setRenderer(com.onpositive
	 * .android.dataview.renderer.IFieldRenderer)
	 */
	@Override
	public void setRenderer(IFieldRenderer renderer) {
		this.renderer = renderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getContributions()
	 */
	@Override
	public List<IContributionItem> getContributions() {
		return this.contributions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setContributions(java
	 * .util.List)
	 */
	@Override
	public void setContributions(List<IContributionItem> contributions) {
		this.contributions = contributions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#addContribution(com.
	 * onpositive.android.dataview.contributions.IContributionItem)
	 */
	@Override
	public void addContribution(IContributionItem item) {
		this.contributions.add(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#removeContribution(com
	 * .onpositive.android.dataview.contributions.IContributionItem)
	 */
	@Override
	public void removeContribution(IContributionItem item) {
		this.contributions.remove(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getFixedImageSize()
	 */
	@Override
	public Point getFixedImageSize() {
		return this.fixedImageSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setFixedImageSize(android
	 * .graphics.Point)
	 */
	@Override
	public void setFixedImageSize(Point fixedImageSize) {
		this.fixedImageSize = fixedImageSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#isFitImageToContent()
	 */
	@Override
	public boolean isFitImageToContent() {
		return this.fitImageToContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setFitImageToContent
	 * (boolean)
	 */
	@Override
	public void setFitImageToContent(boolean fitImageToContent) {
		this.fitImageToContent = fitImageToContent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#toString()
	 */
	@Override
	public String toString() {
		return getId() + "|" + getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getVisible()
	 */
	@Override
	public int getVisible() {
		return this.visible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#setVisible(int)
	 */
	@Override
	public void setVisible(int visible) {
		this.visible = visible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setFieldAggregator(com
	 * .onpositive.android.dataview.datamodel.aggregation.IAggregator)
	 */
	@Override
	public void setAggregator(IAggregator aggregator) {
		IAggregator oldAggregator = this.aggregatorUsed;
		this.aggregatorUsed = aggregator;
		this.fireAggregatorChanged(oldAggregator, aggregator, this.getField());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#isAlwaysVisible()
	 */
	@Override
	public boolean isAlwaysVisible() {
		return this.isCaption();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#getPossibleFiltersProvider
	 * ()
	 */
	@Override
	public IPossibleFiltersProvider getPossibleFiltersProvider() {
		return this.possibleFiltersProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setPossibleFiltersProvider
	 * (com.onpositive.android.dataview.filters.IPossibleFiltersProvider)
	 */
	@Override
	public void setPossibleFiltersProvider(
			IPossibleFiltersProvider possibleFiltersProvider) {
		this.possibleFiltersProvider = possibleFiltersProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#compareTo(com.onpositive
	 * .android.dataview.renderer.Column)
	 */

	@Override
	public int compareTo(IColumn another) {
		if (this.isCaption()) {
			return -1;
		}
		if (another.isCaption()) {
			return 1;
		}
		return this.getId().compareTo(another.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#addAggregatorChangeListener
	 * (com.onpositive.android.dataview.datamodel.aggregation.
	 * IAggregatorChangeListener)
	 */
	@Override
	public void addAggregatorChangeListener(IAggregatorChangeListener listener) {
		this.aggregatorChangeListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#
	 * removeAggregatorChangeListener
	 * (com.onpositive.android.dataview.datamodel.aggregation
	 * .IAggregatorChangeListener)
	 */
	@Override
	public void removeAggregatorChangeListener(
			IAggregatorChangeListener listener) {
		this.aggregatorChangeListeners.remove(listener);
	}

	protected void fireAggregatorChanged(IAggregator oldAggregator,
			IAggregator newAggregator, IField field) {
		for (Object name : this.aggregatorChangeListeners) {
			IAggregatorChangeListener listener = (IAggregatorChangeListener) name;
			listener.aggregatorChanged(oldAggregator, newAggregator, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#getPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object object) {
		return this.field.getPropertyValue(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object object, Object value) {
		this.field.setPropertyValue(object, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#isReadOnly()
	 */
	@Override
	public boolean isReadOnly(Object object) {
		return this.field.isReadOnly(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#getTitle()
	 */
	@Override
	public String getTitle() {
		if (this.title != null) {
			return this.title;
		}
		return this.field.getTitle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.android.dataview.renderer.IColumn#setCaption(boolean)
	 */
	@Override
	public void setCaption(boolean caption) {
		this.caption = caption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setType(java.lang.Class)
	 */
	@Override
	public void setType(Class<?> type) {
		if (this.field instanceof IEditableField) {
			IEditableField d = (IEditableField) this.field;
			d.setType(type);
			return;
		}
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		if (this.field instanceof IEditableField) {
			IEditableField d = (IEditableField) this.field;
			d.setId(id);
			return;
		}
		this.id = id;
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.android.dataview.renderer.IColumn#setTitle(java.lang.String
	 * )
	 */
	@Override
	public void setTitle(String title) {
		if (this.field instanceof IEditableField) {
			IEditableField d = (IEditableField) this.field;
			d.setTitle(title);
			return;
		}
		this.title = title;
	}

	@Override
	public String[] getCategories() {
		if (this.categories != null) {
			return this.categories;
		}
		return field.getCategories();
	}

	Boolean shrink;

	@Override
	public boolean canShrink() {
		if (shrink != null) {
			return shrink;
		}
		if (isCaption()) {
			return true;
		}
		return false;
	}

	@Override
	public void setCanShrink(Boolean shrink) {
		this.shrink = shrink;
	}

	transient Comparator<?> comp;

	public void setComparator(Comparator<?> comp) {
		this.comp = comp;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparator getComparator(boolean ascending) {
		if (comp != null) {
			if (ascending) {
				return new Comparator<Object>() {

					@Override
					public int compare(Object lhs, Object rhs) {
						int comparator = ((Comparator) comp).compare(lhs, rhs);
						return -comparator;
					}
				};
			}
			return comp;
		}
		return new BasicFieldComparator(this, ascending);
	}

	int cg;
	private IGroupingCalculator groupcalc;

	@Override
	public boolean canGrow() {
		if (cg == 0) {
			return isCaption();
		}
		return cg == 1;
	}

	public void setCanGrow(int cg) {
		this.cg = cg;
	}

	@Override
	public IEditorCreationFactory getEditorCreationFactory() {
		return this.factory;
	}

	@Override
	public void setEditorCreationFactory(IEditorCreationFactory factory) {
		this.factory = factory;
	}

	@Override
	public IGroupingCalculator getGroupingCalculator() {
		return groupcalc;
	}
	
	public void setGroupingCalulator(IGroupingCalculator calc){
		this.groupcalc=calc;
	}

	@Override
	public boolean addWhenGrouped() {
		return false;
	}
}
