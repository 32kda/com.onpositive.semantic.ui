package com.onpositive.semantic.ui.businessdroids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.graphics.Point;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IEditorCreationFactory;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.aggregation.IAggregatorChangeListener;
import com.onpositive.businessdroids.model.aggregation.IdentityAggregator;
import com.onpositive.businessdroids.model.filters.IPossibleFiltersProvider;
import com.onpositive.businessdroids.model.groups.IGroupingCalculator;
import com.onpositive.businessdroids.model.impl.BasicFieldComparator;
import com.onpositive.businessdroids.model.impl.IEditableField;
import com.onpositive.businessdroids.ui.IFieldImageProvider;
import com.onpositive.businessdroids.ui.actions.IContributionItem;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.renderers.BasicPossibleFiltersProvider;
import com.onpositive.businessdroids.ui.dataview.renderers.IEditableColumn;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.richtext.IRichLabelProvider;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.ui.android.AndroidList2;
import com.onpositive.semantic.ui.android.ColumnAdapterField;


public class ColumnBridge implements Comparable<IColumn>, IEditableColumn,
Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8170243452600808662L;
	Column mColumn ;
	AndroidList2 list ;
	private boolean isCaption;
	protected int visible = IColumn.AUTOMATIC;

	protected IFieldRenderer renderer;
	protected transient List<IContributionItem> contributions;
	protected transient Point fixedImageSize = null;
	protected boolean fitImageToContent = true;
	
	protected transient IPossibleFiltersProvider possibleFiltersProvider;
	protected IFieldImageProvider imageProvider;
	protected IAggregator aggregatorUsed = IdentityAggregator.INSTANCE;
	protected transient List<IAggregatorChangeListener> aggregatorChangeListeners = new ArrayList<IAggregatorChangeListener>();
	
	private String[] categories;
	
	public ColumnBridge(Column column, AndroidList2 androidTable2) {
		this.mColumn = column ;
		this.list = androidTable2 ;
		this.possibleFiltersProvider = new BasicPossibleFiltersProvider(this);
	}

	@Override
	public boolean isGroupable() {
		return true;
	}

	protected IEditorCreationFactory factory;

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
	
	//complete
	@Override
	public int getVisible() {
		return this.visible;
	}

	@Override
	public void setVisible(int visible) {
		this.visible = visible;
	}

	
	@Override
	public boolean isCaption() {
		return this.isCaption;
	}
	
	@Override
	public void setCaption(boolean caption) {
		this.isCaption = caption;
	}
	
	@Override
	public void setId(String id) {
		this.mColumn.setId(id) ;
	}

	@Override
	public void setTitle(String title) {
		this.mColumn.setCaption(title);
	}
	
	@Override
	public String getTitle() {
		return this.mColumn.getCaption() ;
	}
	
	@Override
	public Object getPropertyValue(Object object) {
		
		if( object instanceof Group){
			Group gr = (Group)object ;
			object = gr.getKey() ;
			ITextLabelProvider labelProvider = mColumn.getLabelProvider();
			if (labelProvider != null) {
				if (labelProvider instanceof IRichLabelProvider) {
					IRichLabelProvider ta = (IRichLabelProvider) labelProvider;
					return ta.getRichTextLabel(object);
				}
				IHasMeta meta = mColumn.meta();
				Object parentObject = mColumn.getOwnerSelector().getParentObject();
				String text = labelProvider.getText(meta, parentObject,object);
				return new StyledString(text);
			}
			return object ;
		}		
			
		return this.mColumn.getRichTextLabel( object ) ;
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		this.mColumn.setValue(object, value);
	}

	
	@Override
	public boolean isReadOnly(Object object) {
		return !this.mColumn.isEditable() ;
	}

	
	public IField getField() {
		return new ColumnAdapterField(mColumn) ;
	}

	
	@Override
	public String getId() {
		return mColumn.getId() ;
	}

	@Override
	public Class<?> getType() {
		return mColumn.getType(null) ;
	}
	
	public Column getModelColumn() {
		return mColumn;
	}

	@Override
	public String toString() {
		return getId() + "|" + getTitle();
	}
	
	//incomplete	
	@Override
	public void setType(Class<?> type) {
	}
	
	@Override
	public String[] getCategories() {
		return new String[0] ;
	}


	
}
