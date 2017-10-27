package com.onpositive.businessdroids.ui.dataview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Looper;
import android.os.Parcelable;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.IModelChangeListener;
import com.onpositive.businessdroids.model.IStatefullModel;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.aggregation.IAggregator;
import com.onpositive.businessdroids.model.filters.BasicFilterSetupVisualizer;
import com.onpositive.businessdroids.model.filters.IFilter;
import com.onpositive.businessdroids.model.filters.IFilterSetupVisualizer;
import com.onpositive.businessdroids.model.types.Money;
import com.onpositive.businessdroids.model.types.NumericRange;
import com.onpositive.businessdroids.ui.AbstractViewer;
import com.onpositive.businessdroids.ui.ILongClickListener;
import com.onpositive.businessdroids.ui.IOpenListener;
import com.onpositive.businessdroids.ui.IViewer;
import com.onpositive.businessdroids.ui.actionbar.ActionBar;
import com.onpositive.businessdroids.ui.dataview.actions.SortAction;
import com.onpositive.businessdroids.ui.dataview.handlers.ITablePartClickHandler;
import com.onpositive.businessdroids.ui.dataview.handlers.SortHeaderClickHandler;
import com.onpositive.businessdroids.ui.dataview.persistence.ISaveable;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;
import com.onpositive.businessdroids.ui.dataview.renderers.BooleanRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.CurrencyRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.DateRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IFooterRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IHeaderRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IRecordRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.ImageRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.NumberRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.StringRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.TimeRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.ViewRendererService;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.BasicFooterRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.BasicHeaderRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.impl.BasicRecordRenderer;
import com.onpositive.businessdroids.ui.themes.ITheme;

public class StructuredDataView extends AbstractViewer implements ISaveable,
		IViewer {

	private static final int MAX_COLUMNS = 6;
	protected Object adapter;
	protected ListView gridView;
	protected FrameLayout headerView;
	protected FrameLayout footerView;
	protected final TableModel tableModel;
	protected IHeaderRenderer headerRenderer = new BasicHeaderRenderer(this);

	public IFooterRenderer getFooterRenderer() {
		return footerRenderer;
	}

	public void setFooterRenderer(IFooterRenderer footerRenderer) {
		this.footerRenderer = footerRenderer;
	}

	public IRecordRenderer getRecordRenderer() {
		return recordRenderer;
	}

	public void setRecordRenderer(IRecordRenderer recordRenderer) {
		this.recordRenderer = recordRenderer;
	}

	protected IFooterRenderer footerRenderer = new BasicFooterRenderer();
	protected IRecordRenderer recordRenderer = new BasicRecordRenderer();
	protected ImageProviderService imageService = new ImageProviderService();
	protected ViewRendererService viewRendererService = new ViewRendererService();
	protected int[] fieldWidths;
	protected ITablePartClickHandler headerClickHandler;
	protected ITablePartClickHandler footerClickHandler;
	protected boolean headerVisible = true;
	protected boolean footerVisible = true;

	protected boolean horizontalScrollable = false;
	protected HorizontalScrollView scrollView = null;

	protected LinearLayout scrollLayout;
	protected ISaveable delegate;
	protected IFilterSetupVisualizer filterSetupVisualizer = new BasicFilterSetupVisualizer();
	/**
	 * First visible element index
	 */
	protected int firstVisibleIndex = -1;
	protected int firstVisibleY = 0;

	boolean userHorScroll;
	protected Parcelable initParcelable;
	protected String title;

	IOpenListener openListener;
	ILongClickListener longClickListener;
	private boolean renderHeadersClickable;
	boolean autoSort;

	boolean ignoreEvents;

	IModelChangeListener modelListener = new IModelChangeListener() {

		@Override
		public void modelChanged(final TableModel tableModel) {
			if (ignoreEvents) {
				return;
			}
			if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
				post(new Runnable() {

					@Override
					public void run() {
						onModelChanged(tableModel);
					}
				});
				return;
			}
			onModelChanged(tableModel);
		}

		@Override
		public void aggregatorChanged(IAggregator oldAggregator,
				IAggregator newAggregator, IColumn column) {
			onAggregatorChanged(oldAggregator, newAggregator, column);
		}
	};
	private OnItemSelectedListener itemSelectedListener;

	public StructuredDataView(Context context, TableModel tableModel) {
		super(context);
		this.tableModel = tableModel;
		this.registerBasicRenderers();
		this.setOrientation(LinearLayout.VERTICAL);
		this.initView(context, tableModel);
		tableModel.addModelChangeListener(modelListener);
	}

	@Override
	protected void onAttachedToWindow() {
		setMinimumHeight(10);
		setMinimumWidth(10);
		tableModel.addModelChangeListener(modelListener);

		super.onAttachedToWindow();
		if (shouldInit()) {
			recreate();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		tableModel.removeModelChangeListener(modelListener);
		super.onDetachedFromWindow();
	}

	public boolean isGrouped() {
		return this.tableModel.getCurrentGroupingCalculator() != null;
	}

	// public IColumn getGroupBy() {
	// return tableModel.getCurrentGroupColumn();
	// }
	public ViewGroup getPrimaryView() {
		return this.horizontalScrollable ? this.scrollView : this.gridView;
	}

	protected void initView(Context context, TableModel tableModel) {

		this.scrollView = null;		
		
		if(this.actionBar == null)
			this.actionBar = new ActionBar(getContext(), this, this.getTitle());

		if (this.headerClickHandler == null && tableModel != null) {
			this.headerClickHandler = this.createHeaderClickHandler(tableModel);

		}
		if (this.tableModel == null) {
			return;
		}
		if (!shouldInit()) {
			return;
		}

		this.inited = true;
		if (tableModel instanceof IStatefullModel) {
			IStatefullModel s = (IStatefullModel) tableModel;
			if (s.getModelState() == IStatefullModel.STATE_NO_DATA_SYNC_INPROGRESS) {
				inProgress = true;
			}
		}
		if (inProgress) {
			setInProgress(true);
			return;
		}
		if (tableModel.getCurrentGroupingCalculator() == null) {
			this.gridView = new ListView(context) {

				@Override
				protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

					int length = 0;
					super.onMeasure(widthMeasureSpec, heightMeasureSpec);
					if (StructuredDataView.this.horizontalScrollable) {
						if (StructuredDataView.this.fieldWidths != null) {
							for (int fieldWidth : StructuredDataView.this.fieldWidths) {
								length += fieldWidth;
							}
						}
						if ((length != 0)
								&& (length > StructuredDataView.this.scrollView
										.getWidth())) {
							super.setMeasuredDimension(length,
									this.getMeasuredHeight());
						}
					}
				};
			};
			this.preInitList(this.gridView);
			if (this.openListener != null) {
				this.gridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (id >= 0) {
							Object item = StructuredDataView.this 
									.getTableModel().getItem((int) id);
							StructuredDataView.this.openListener.openElement(
									parent, view, item, (int)id );
						}
					}
				});
			}
			this.gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					if (longClickListener != null) {
						Object item = StructuredDataView.this 
								.getTableModel().getItem((int) id);
						longClickListener.handleLongClick(parent, view, item);
					}
					return false;
				}
				
			});
			this.adapter = new TableModelDataAdapter(context, this,
					this.headerRenderer, this.recordRenderer);
			(this.gridView).setAdapter((ListAdapter) this.adapter);
		} else {
			ExpandableListView expandableListView = new ExpandableListView(
					context) {

				@Override
				protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
					
					int length = 0;
					super.onMeasure(widthMeasureSpec, heightMeasureSpec );
					if (StructuredDataView.this.horizontalScrollable) {
						if (StructuredDataView.this.fieldWidths != null) {
							for (int fieldWidth : StructuredDataView.this.fieldWidths) {
								length += fieldWidth;
							}
						}
						if ((length != 0)
								&& (length > StructuredDataView.this.scrollView
										.getWidth())) {
							super.setMeasuredDimension(length,
									this.getMeasuredHeight());
						}
					}
				};

			};
			this.preInitList(expandableListView);
			try {
				java.lang.reflect.Field declaredField = ExpandableListView
						.class.getDeclaredField("mGroupIndicator");
				declaredField.setAccessible(true);
				Drawable object = (Drawable) declaredField
						.get(expandableListView);
				int bounds = object.getIntrinsicHeight();
				expandableListView.setGroupIndicator(new InsetDrawable(object,
						bounds / 5, bounds / 5, 0,0));
			} catch (Exception e) {
				// Shouldn't happen
			}
			expandableListView.setIndicatorBounds(
					0,
					this.currentTheme.getIndicatorBound()
							+ this.currentTheme.getBaseLeftPadding());

			expandableListView
					.setOnChildClickListener(new OnChildClickListener() {

						@Override
						public boolean onChildClick(ExpandableListView parent,
								View v, int groupPosition, int childPosition,
								long id) {
							if (StructuredDataView.this.openListener != null) {
								Object child = parent
										.getExpandableListAdapter().getChild(
												groupPosition, childPosition);
								StructuredDataView.this.openListener
										.openElement(parent, v, child, groupPosition);
							}
							return true;
						}
					});
			this.gridView = expandableListView;

			this.adapter = new GroupingDataAdapter(this,
					new TableModelDataAdapter(context, this,
							this.headerRenderer, this.recordRenderer));
			((ExpandableListView) this.gridView)
					.setAdapter((ExpandableListAdapter) this.adapter);

		}

		this.calculateWidth(tableModel);
		customFit(fieldWidths, userHorScroll);
		this.headerView = new FrameLayout(context);
		this.footerView = new FrameLayout(context);
		this.gridView.setRecyclerListener(new RecyclerListener() {

			@Override
			public void onMovedToScrapHeap(View view) {
				recordRenderer.recycled(StructuredDataView.this, view);
			}
		});
		gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		gridView.setDrawSelectorOnTop(false);
		if (itemSelectedListener != null) {
			gridView.setOnItemSelectedListener(itemSelectedListener);
		}
		this.addActionBar(this);

		RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		//gridParams.weight = 1;
		LinearLayout ll0 = new LinearLayout(context);
		ll0.addView(
				this.gridView,
				new LayoutParams(
						this.horizontalScrollable ? android.view.ViewGroup.LayoutParams.WRAP_CONTENT
								: android.view.ViewGroup.LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.FILL_PARENT));

		this.gridView.setPadding(0, 0, 0, 0);

		// determine if we actually horizontal scrollable;
		this.horizontalScrollable = this.scrollNeeded();
		if (this.horizontalScrollable) {
			this.scrollLayout = new LinearLayout(context);
			this.scrollLayout.setOrientation(LinearLayout.VERTICAL);
			this.scrollView = new HorizontalScrollView(context) {
				@Override
				protected void onScrollChanged(int l, int t, int oldl, int oldt) {
					super.onScrollChanged(l, t, oldl, oldt);
					StructuredDataView.this.gridView.invalidate();
				}
			};
			this.scrollView.setPadding(0, 0, 0, 0);
			this.scrollView.setScrollContainer(true);
			// scrollView.set
			this.scrollView.setFillViewport(true);
			this.scrollView.addView(this.scrollLayout,
					new FrameLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.FILL_PARENT));

			this.addHeader(this.scrollLayout);
			this.scrollLayout.addView(ll0, gridParams);
			this.addFooter(this.scrollLayout);
			this.addView(this.scrollView, gridParams);
			addBottomBarIfNeeded();

		} else {

			RelativeLayout lt = new RelativeLayout(context) ;
			LayoutParams params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.FILL_PARENT);
			
			this.addView( lt, params ) ;
			ll0.removeAllViews() ;
			
			this.headerView.setId(8001) ;
			this.gridView.setId(8002) ;
			this.footerView.setId(8003) ;
			
			gridParams.addRule( RelativeLayout.BELOW, 8001) ;
			gridParams.addRule( RelativeLayout.ABOVE, 8003) ;
			
			this.addHeader(lt);			
			lt.addView(this.gridView, gridParams);
			this.addFooter(lt);
		}
		initBGText();

		if (this.initParcelable != null) {
			try {
				this.gridView.onRestoreInstanceState(this.initParcelable);
			} catch (Exception e) {
				// ignore
			}
			this.initParcelable = null;
		}
		if (this.firstVisibleIndex > -1) {
			this.setFirstElement(this.firstVisibleIndex, this.firstVisibleY);
			this.firstVisibleIndex = -1;
		}
		this.setupEmptyMsg();
		// addView(layout, new
		// LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	protected boolean shouldInit() {
		return this.getWidth() != 0;
	}

	protected void preInitList(ListView gridView) {

	}

	protected boolean shouldRecreateOnResize() {
		return (this.scrollNeeded() != this.horizontalScrollable)
				|| !this.inited;
	}

	protected boolean scrollNeeded() {
		int sum = 0;
		if (this.fieldWidths == null) {
			this.calculateWidth(this.getTableModel());
			customFit(fieldWidths, userHorScroll);
		}
		for (int fieldWidth : this.fieldWidths) {
			sum += fieldWidth;
		}
		int width = this.getWidth();
		return this.userHorScroll && (sum > width);
	}

	protected void addFooter(ViewGroup parent) {
		if (this.isFooterVisible()) {
			RelativeLayout.LayoutParams footerParams = new RelativeLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

			footerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) ;
			//footerParams.addRule( RelativeLayout.BELOW, 8003) ;

			parent.addView(this.footerView, footerParams);
		}

	}

	protected void addHeader(ViewGroup parent) {
		if (this.headerVisible) {
			LayoutParams headerParams = new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			headerParams.weight = 0;
			parent.addView(this.headerView/*, this.actionBarVisible
					&& !this.horizontalScrollable ? 1 : 0*/, headerParams);
		}
	}

	protected void calculateWidth(TableModel tableModel) {
		IColumn[] sortedColumns = this.getSortedColumns();
		this.fieldWidths = new int[sortedColumns.length];
		for (int a = 0; a < sortedColumns.length; a++) {
			IColumn column = sortedColumns[a];
			if (column.getVisible() != IColumn.INVISIBLE) {
				this.fieldWidths[a] = this.headerRenderer.measureField(this,
						column);
				this.fieldWidths[a] = Math.max(this.fieldWidths[a],
						this.footerRenderer.measureField(this, column));
				this.fieldWidths[a] = Math.max(this.fieldWidths[a],
						this.recordRenderer.measureField(this, column));
			} else {
				this.fieldWidths[a] = 0;
			}
		}
	}

	protected void registerBasicRenderers() {

		this.viewRendererService.registerClassRenderer(Boolean.class,
				new BooleanRenderer());
		this.viewRendererService.registerClassRenderer(Date.class,
				new DateRenderer());
		this.viewRendererService.registerClassRenderer(Time.class,
				new TimeRenderer());
		this.viewRendererService.registerClassRenderer(Number.class,
				new NumberRenderer());
		this.viewRendererService.registerClassRenderer(Money.class,
				new CurrencyRenderer());
		this.viewRendererService.registerClassRenderer(Drawable.class,
				new ImageRenderer());
		this.viewRendererService.registerClassRenderer(Bitmap.class,
				new ImageRenderer());

		this.viewRendererService.registerGroupClassRenderer(Boolean.class,
				new StringRenderer());
		this.viewRendererService.registerGroupClassRenderer(NumericRange.class,
				new StringRenderer());
	}

	protected ITablePartClickHandler createHeaderClickHandler(
			TableModel tableModel) {
		return new SortHeaderClickHandler(tableModel);
	}

	public ImageProviderService getImageProviderService() {
		return this.imageService;
	}

	public ViewRendererService getViewRendererService() {
		return this.viewRendererService;
	}

	protected Map<IColumn, SortAction> createSortActions(IColumn[] columns) {
		Map<IColumn, SortAction> result = new HashMap<IColumn, SortAction>();
		for (IColumn column : columns) {
			result.put(column, new SortAction(this.tableModel, column));
		}
		return result;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = this.getWidth();
		// TODO RETHINK THIS BLIADSTVO
		if (this.inited && headerView != null
				&& (this.headerView.getChildCount() == 0) && (width > 0)) {
			// if (footerRenderer.needToRenderFooter())
			// fieldWidths =
			// footerRenderer.calculateFooterWidths(this,fieldWidths);
			boolean horizontalScrollable2 = this.isHorizontalScrollable();
			TableModelDataAdapter.fitFieldWidths(this.getColumns(),
					this.fieldWidths, width, horizontalScrollable2);
			customFit(this.fieldWidths, horizontalScrollable2);
			int renderWidth = width;
			if (horizontalScrollable)
				renderWidth = widthMeasureSpec;
			this.headerView.addView(this.headerRenderer.render(this,
					renderWidth));

			if (this.footerRenderer.needToRenderFooter(this)) {
				this.footerView.addView(this.footerRenderer.render(this,
						renderWidth));
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		if(footerView!=null)
//			footerHeight = footerView.getMeasuredHeight();		
	}

	protected void customFit(int[] fieldWidths, boolean horizontalScrollable2) {

	}

	public IHeaderRenderer getHeaderRenderer() {
		return this.headerRenderer;
	}

	public void setHeaderRenderer(IHeaderRenderer headerRenderer) {
		this.headerRenderer = headerRenderer;
	}

	public IColumn[] getColumns() {
		return this.getSortedColumns();
	}

	// public IPropertyMapping getPropertyMapping() {
	// return tableModel.getPropertyMapping();
	// }

	public TableModel getTableModel() {
		return this.tableModel;
	}

	public void onHeaderClick(IColumn column, View source) {
		this.headerClickHandler.handleClick(column, source);
	}

	public void onFooterClick(IColumn column, View source) {
		if (this.footerClickHandler != null) {
			this.footerClickHandler.handleClick(column, source);
		}
	}

	public int getFieldWidth(int field) {
		if (this.fieldWidths == null) {
			this.calculateWidth(this.getTableModel());
			customFit(fieldWidths, userHorScroll);
		}
		return this.fieldWidths[field];
	}

	public boolean isFooterVisible() {
		return this.footerVisible;
	}

	public void setFooterVisible(boolean footerVisible) {
		boolean footerVisible2 = this.footerVisible;
		this.footerVisible = footerVisible;
		if (footerVisible != footerVisible2 && this.inited) {
			if (footerVisible) {
				if (this.scrollView != null) {
					this.addFooter(this.scrollView);
				} else {
					this.addFooter(this);
				}
			} else {
				this.removeView(this.footerView);
			}
		}
	}

	public boolean isHeaderVisible() {
		return this.headerVisible;
	}

	public void setHeaderVisible(boolean headerVisible) {
		boolean headerVisible2 = this.headerVisible;
		this.headerVisible = headerVisible;
		if ((headerVisible != headerVisible2) && this.inited) {
			if (headerVisible) {
				if (this.scrollView != null) {
					this.addHeader(this.scrollLayout);
				} else {
					this.addHeader(this);
				}
			} else {
				if (this.scrollView != null) {
					this.scrollLayout.removeView(this.headerView);
				} else {
					this.removeView(this.headerView);
				}
			}
		}

	}

	public ListView getListView() {
		return this.gridView;
	}

	public ITablePartClickHandler getHeaderClickHandler() {
		return this.headerClickHandler;
	}

	public void setHeaderClickHandler(ITablePartClickHandler headerClickHandler) {
		this.headerClickHandler = headerClickHandler;
	}

	public IColumn[] getSortedColumns() {
		IColumn[] fields = tableModel.getColumns().clone();
		Arrays.sort(fields, new Comparator<IColumn>() {

			@Override
			public int compare(IColumn object1, IColumn object2) {
				if (object1.isCaption()) {
					return -1;
				}
				if (object2.isCaption()) {
					return 1;
				}
				return 0;
			}
		});
		return fields;
	}

	public IFieldRenderer getRenderer(IColumn column) {
		IFieldRenderer renderer = column.getRenderer();
		if (renderer == null) {
			renderer = this.viewRendererService.getRenderer(column);
		}
		return renderer;
	}

	public IColumn getColumnById(String id) {
		return tableModel.getColumnById(id);
	}

	public boolean hasAggregators() {
		for (IColumn column : tableModel.getColumns()) {
			if (column.getAggregator() != null) {
				return true;
			}
		}
		return false;
	}

	public void updateWidth() {
		this.calculateWidth(this.tableModel);
		customFit(fieldWidths, userHorScroll);
	}

	public ITablePartClickHandler getFooterClickHandler() {
		return this.footerClickHandler;
	}

	public void setFooterClickHandler(ITablePartClickHandler footerClickHandler) {
		this.footerClickHandler = footerClickHandler;
	}

	public void recreateFooter() {
		if (this.inited) {
			int width = this.footerView.getWidth();
			if (width > 0) {
				this.footerView.removeAllViews();
				this.footerView
						.addView(this.footerRenderer.render(this, width));
			}
		}
	}

	public void setVisibleColumns(Collection<IColumn> columns) {
		IColumn[] allColumns = this.getColumns();
		IColumn caption = allColumns[0]; // At least one column should remain
											// visible
		IColumn[] array = columns.toArray(new IColumn[columns.size()]);
		int visibleMode = IColumn.VISIBLE;
		if (!this.horizontalScrollable) {
			visibleMode = IColumn.AUTOMATIC;
		}
		boolean visibilityChanged = false;
		for (IColumn allColumn : allColumns) {
			if (allColumn.isAlwaysVisible()) {
				allColumn.setVisible(IColumn.VISIBLE);
			} else {
				boolean visible = columns.contains(allColumn);
				if (visible) {
					if (allColumn.getVisible() != visibleMode) {
						allColumn.setVisible(visibleMode);
						visibilityChanged = true;
					}
				} else if (allColumn.getVisible() != IColumn.INVISIBLE) {
					allColumn.setVisible(IColumn.INVISIBLE);
					visibilityChanged = true;
				}
				if (allColumn.isCaption()) {
					caption = allColumn;
				}
			}
		}

		if (columns.size() == 0) {
			caption.setVisible(IColumn.VISIBLE);
		}

		if (visibilityChanged) {
			autoSort(array);
			this.recreate();
		}
	}

	protected void autoSort(IColumn[] array) {
		if (autoSort) {
			ignoreEvents = true;
			try {
				if (array.length > 0) {
					tableModel.sort(array[array.length - 1], false);
				}
			} finally {
				ignoreEvents = false;
			}
		}
	}

	@Override
	protected void initView() {
		initView(getContext(), tableModel);
	}

	protected void recreate() {
		this.removeAllViews();
		this.initView(this.getContext(), this.tableModel);
	}

	public final Collection<IColumn> getVisibleColumns() {
		ArrayList<IColumn> obj = new ArrayList<IColumn>();
		for (IColumn c : this.getColumns()) {
			if (c.getVisible() != IColumn.INVISIBLE) {
				obj.add(c);
			}
		}
		return obj;
	}

	public boolean isHorizontalScrollable() {
		return this.horizontalScrollable;
	}

	public void setHorizontalScrollable(boolean horizontalScrollable) {
		this.userHorScroll = horizontalScrollable;
		if (this.userHorScroll != this.horizontalScrollable) {
			this.recreate();
		}
	}

	protected void onAggregatorChanged(IAggregator oldAggregator,
			IAggregator newAggregator, IColumn column) {
		boolean isFW = this.isFooterVisible();
		if (!this.inited) {
			return;
		}
		boolean footerVisible2 = this.footerVisible&&footerView!=null&&footerView.getParent()!=null;
		if (isFW != footerVisible2) {
			this.recreate();
			return;
		}
		if (this.isGrouped()) {
			GroupingDataAdapter groupingDataAdapter = (GroupingDataAdapter) this.adapter;
			int groupCount = groupingDataAdapter.getGroupCount();
			for (int a = 0; a < groupCount; a++) {
				Group group = (Group) groupingDataAdapter.getGroup(a);
				group.clearField(column);
			}
			groupingDataAdapter.notifyDataSetChanged();
		}

		if (this.footerRenderer != null) {
			this.recreateFooter();
		}
	}

	public ITheme getCurrentTheme() {
		return this.currentTheme;
	}

	public int getFirstElement() {
		if (!this.inited) {
			return this.firstVisibleIndex;
		}
		return this.gridView.getFirstVisiblePosition();
	}

	public int getFirstElementY() {
		if (!this.inited) {
			return this.firstVisibleY;
		}
		View v = this.gridView.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		return top;
	}

	public void setFirstElement(int index, int y) {
		if (!this.inited) {
			this.firstVisibleIndex = index;
			this.firstVisibleY = y;
			return;
		}
		this.gridView.setSelectionFromTop(Math.max(0, index), y);
	}

	protected void setupEmptyMsg() {
		Long unfilteredItemCount = this.tableModel.getUnfilteredItemCount();
		if ((this.tableModel.getItemCount() == 0)
				&& (unfilteredItemCount==null||unfilteredItemCount > 0)) {
			this.setFilteredMsg();
			return;
		}
		if (this.adapter instanceof BaseAdapter) {
			if (((BaseAdapter) this.adapter).isEmpty()) {
				this.setEmptyMsg();
			} else {
				this.removeEmptyMsg();
			}
		} else if (this.adapter instanceof GroupingDataAdapter) {
			if (((GroupingDataAdapter) this.adapter).isEmpty()
					&& ((GroupingDataAdapter) this.adapter).getChildAdapter()
							.isEmpty()) {
				this.setEmptyMsg();
			} else {
				this.removeEmptyMsg();
			}
		}
	}

	@Override
	public void save(IStore store) {
		this.delegate.save(store);
	}

	@Override
	public void load(IStore store) {
		try {
			this.delegate.load(store);
		} catch (NoSuchElement e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Parcelable saveInstanceState() {
		if (this.inited) {
			this.initParcelable = this.gridView.onSaveInstanceState();
		}
		return this.initParcelable;
	}

	public void restoreInstanceState(Parcelable parcelable) {
		if (parcelable != null) {
			if (this.inited) {
				this.gridView.onRestoreInstanceState(parcelable);
			} else {
				this.initParcelable = parcelable;
			}
		}
	}

	public IOpenListener getOpenListener() {
		return this.openListener;
	}

	public void setOpenListener(IOpenListener openListener) {
		this.openListener = openListener;
	}

	public IFilterSetupVisualizer getFilterSetupVisualizer() {
		return this.filterSetupVisualizer;
	}

	public void setFilterSetupVisualizer(
			IFilterSetupVisualizer filterSetupVisualizer) {
		this.filterSetupVisualizer = filterSetupVisualizer;
	}

	public IColumn[] getPresentationSortedColumns() {
		IColumn[] columns = this.getColumns();
		if (columns.length > StructuredDataView.MAX_COLUMNS) {
			Arrays.sort(columns);
		}
		return columns;
	}

//	public IColumn getCurrentGroupColumn() {
//		return (IColumn) this.tableModel.getCurrentGroupField();
//	}

	public IField getSortField() {
		return this.tableModel.getSortField();
	}

	public IFilter[] getColumnFilters(IColumn column) {
		return this.tableModel.getFieldFilters(column);
	}

	@Override
	public View getView() {
		return this;
	}

	protected void onModelChanged(TableModel tableModel) {
		if (tableModel instanceof IStatefullModel) {
			IStatefullModel stm = (IStatefullModel) tableModel;
			int modelState = stm.getModelState();
			boolean b = modelState == IStatefullModel.STATE_SYNCING;
			if (modelState == IStatefullModel.STATE_NO_DATA_SYNC_INPROGRESS) {
				// NO DATA TO HANDLE
				setInProgress(true);
				return;
			} else {
				if (inProgress) {
					// LETS RECREATE EVERYTHING AND RETURN
					setInProgress(false);
					// check if progress indicator needed
					setActionBarProgress(b);
					return;
				}
			}
			setActionBarProgress(b);
		}
		if (StructuredDataView.this.headerRenderer
				.updateHeaders(StructuredDataView.this)) {
			StructuredDataView.this.recreate();
			return;
		}
		if (this.adapter != null
				&& this.adapter instanceof GroupingDataAdapter
				&& this.tableModel != null
				&& this.tableModel.getCurrentGroupingCalculator() != null )
		{
			((GroupingDataAdapter) this.adapter).onModelChanged(tableModel);
		}
		
		if (StructuredDataView.this.adapter instanceof BaseAdapter) {
			if (StructuredDataView.this.isGrouped()) {
				StructuredDataView.this.removeAllViews();
				StructuredDataView.this.initView(
						StructuredDataView.this.getContext(), tableModel);
				return;
			}
			((BaseAdapter) StructuredDataView.this.adapter)
					.notifyDataSetChanged();
			StructuredDataView.this.setupEmptyMsg();
		}
		if (StructuredDataView.this.adapter instanceof BaseExpandableListAdapter) {
			if (!StructuredDataView.this.isGrouped()) {
				StructuredDataView.this.removeAllViews();
				StructuredDataView.this.initView(
						StructuredDataView.this.getContext(), tableModel);
				return;
			}
			((BaseExpandableListAdapter) StructuredDataView.this.adapter)
					.notifyDataSetChanged();
			StructuredDataView.this.setupEmptyMsg();
		}
		if (StructuredDataView.this.inited) {
			if (StructuredDataView.this.footerRenderer
					.needToRenderFooter(StructuredDataView.this)) {
				StructuredDataView.this.footerRenderer
						.updateAggregatedValues(StructuredDataView.this);
			}
			if (StructuredDataView.this.isHeaderVisible()) {
				StructuredDataView.this.headerView.refreshDrawableState();
				StructuredDataView.this.headerView.invalidate();
			}
			StructuredDataView.this.actionBar.updateActionStates();
		}

	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener itemSelectedListener){
		this.itemSelectedListener = itemSelectedListener;
		if (gridView != null) {
			gridView.setOnItemSelectedListener(itemSelectedListener);
		}
	}

	public boolean renderHeadersClicable() {
		return renderHeadersClickable;
	}

	public void setRenderHeadersClickable(boolean renderHeadersClickable) {
		this.renderHeadersClickable = renderHeadersClickable;
	}

	public boolean isAutoSort() {
		return autoSort;
	}

	public void setAutoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	public ListView getGridView() {
		return gridView;
	}

	public ILongClickListener getLongClickListener() {
		return longClickListener;
	}

	public void setLongClickListener(ILongClickListener longClickListener) {
		this.longClickListener = longClickListener;
	}
}
