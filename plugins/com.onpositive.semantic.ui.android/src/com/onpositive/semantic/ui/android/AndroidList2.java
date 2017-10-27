package com.onpositive.semantic.ui.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.IField;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.groups.IFieldGroupingCalculator;
import com.onpositive.businessdroids.ui.ILongClickListener;
import com.onpositive.businessdroids.ui.actions.ColumnContributionItemProvider;
import com.onpositive.businessdroids.ui.actions.IContributionItemProvider;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.persistence.NoSuchElement;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.semantic.editactions.AddFromRealmAction;
import com.onpositive.semantic.editactions.DefaultAddNewAction;
import com.onpositive.semantic.editactions.DefaultRemoveAction;
import com.onpositive.semantic.editactions.OpenAction;
import com.onpositive.semantic.model.api.changes.INotifyableCollection;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.changes.ObjectChangeManager;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.order.IOrderMaintainer;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.query.IQueryExecutor;
import com.onpositive.semantic.model.api.query.memimpl.InMemoryExecutor;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.api.realm.IFiltrable;
import com.onpositive.semantic.model.api.realm.IModifiableRealm;
import com.onpositive.semantic.model.api.realm.IOwned;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.IRealmChangeListener;
import com.onpositive.semantic.model.api.realm.OrderedRealm;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.tree.AbstractClusterizationPointProvider;
import com.onpositive.semantic.model.tree.IClusterizationPointProvider;
import com.onpositive.semantic.model.tree.PropertyValueHierarchicalPointProvider;
import com.onpositive.semantic.model.tree.PropertyValuePointProvider;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IObjectContributionManager;
import com.onpositive.semantic.model.ui.actions.ObjectContributionManager;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.IProvidesToolbarManager;
import com.onpositive.semantic.model.ui.generic.IRowStyleProvider;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IActionInterceptor;
import com.onpositive.semantic.model.ui.generic.widgets.ICanDrop;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectionListener;
import com.onpositive.semantic.model.ui.generic.widgets.RealmUtils;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindingSelectionExpressionController;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.IOpenListener;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.StructuredSelection;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;
import com.onpositive.semantic.ui.android.customwidgets.actions.presenters.DialogContributionPresenter;
import com.onpositive.semantic.ui.android.customwidgets.dialogs.OkCancelDialog;
import com.onpositive.semantic.ui.android.dataview.adapter.DataviewToolbarManager;
import com.onpositive.semantic.ui.businessdroids.QueryBasedTableModel2;

public class AndroidList2 extends AndroidUIElement implements IListElement<View>, IFiltrable {

	private static final String CONFIRM_DELETE_DIALOG_TITLE = "Confirm deletion";

	private static final String DELETE_ITEM_QUESTION_TEXT = "Delete item";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final BindingSelectionExpressionController controller = new BindingSelectionExpressionController(
			this, this);

	IRowStyleProvider provider;
	HashSet<ISelectionListener> listeners = new HashSet<ISelectionListener>();
	HashSet<IOpenListener> olisteners = new HashSet<IOpenListener>();
	@SuppressWarnings("rawtypes")
	protected HashSet<IObjectDecorator> ds;
	protected String elementRole;
	protected String defaultImage;

	private ImageDescriptor dobject;

	protected QueryBasedTableModel2 dataSource;

	protected String property;

	HashMap<String, HashSet<IActionInterceptor>>interceptors=null;

	OrderedRealm<IFilter>flts=null;

	protected String currentGroup;

	private boolean openOnDblClick;

	boolean allowCell;

	StructuredSelection sel;

	private boolean vasSelection;

	boolean asTree;

	IRealm<Object> cRealm;

	protected boolean headerVisible;
	
	protected DataviewToolbarManager toolbarManager = new DataviewToolbarManager();

	private ArrayList<Column> dcolumnList;

	@HandlesAttributeIndirectly("bindSelectionTo")
	public BindingSelectionExpressionController getBindingSelectionExpressionController() {
		return controller;
	}
	public ImageDescriptor getDefaultImageDescriptor() {
		return dobject;
	}
	public String getDefaultImage() {
		return defaultImage;
	}
	@HandlesAttributeDirectly("defaultImage")
	public void setDefaultImage(String defaultImage) {
		this.defaultImage = defaultImage;
		this.dobject=(ImageDescriptor) ImageManager.getImageDescriptor(defaultImage);		
	}
	
	public String getProperty() {
		return property;
	}
	
	protected void onRealmChanged(){
		if (!isValueAsSelection()&&isCreated()){
			setSelection(new ArrayList<Object>());
//			if (sel!=null&&!sel.isEmpty()){
//				selectionChanged(new StructuredSelection());
//				Table t=(Table) getControl();
//				t.select(t.getNullSelectionItemId());
//			}
		}
	}
	
	@Override
	public HashSet<IActionInterceptor> getInterceptors(String interceptorKind) {
		if (interceptors==null){
			interceptors=new HashMap<String, HashSet<IActionInterceptor>>();
		}
		HashSet<IActionInterceptor> hashSet = interceptors.get(interceptorKind);
		if (hashSet==null){
			hashSet=new HashSet<IActionInterceptor>();
			interceptors.put(interceptorKind, hashSet);
		}
		return hashSet;
	}
	
	@Override
	public IModifiableRealm<IFilter> getFilters() {
		if (flts==null){
			flts=new OrderedRealm<IFilter>();
			flts.addRealmChangeListener(new IRealmChangeListener<IFilter>() {
				
				private static final long serialVersionUID = 2384634529501697523L;

				@Override
				public void realmChanged(IRealm<IFilter> realmn, ISetDelta<IFilter> delta) {
					for (IFilter filter:delta.getAddedElements()){
					if (filter instanceof IOwned) {
						IOwned own = (IOwned) filter;
						own.setOwner(AndroidList2.this);
					}
					}					
					dataSource.setModelFilters( new ArrayList<IFilter>( flts.getContents()) ) ;
					refresh();
				}
			});
		}
		return flts;
	}
	

	@HandlesAttributeDirectly("property")
	public void setProperty(String property) {
		this.property = property;
		if(isCreated()){
			refresh();
		}
	}
	public String getInitiallyGroup() {
		return currentGroup;
	}

	@HandlesAttributeDirectly("initiallyGroup")
	public void setInitiallyGroup(String initiallyGroup) {		
		this.currentGroup = initiallyGroup;
		if( initiallyGroup != null ){
			final IColumn column = dataSource.getColumnById(initiallyGroup);
		
			this.dataSource.setCurrentGrouping( new IFieldGroupingCalculator() {
				
				@Override
				public void save(IStore store) {}
				
				@Override
				public void load(IStore store) throws NoSuchElement {}
				
				@Override
				public List<Group> calculateGroups(TableModel tableModel) {
					return null ;
				}
	
				@Override
				public String getId() {
					return currentGroup ;
				}
	
				@Override
				public IColumn getGroupField() {
					return column;
				}

				@Override
				public void setGroupField(IField field){};
			}) ;
		}
		else{
			dataSource.setCurrentGrouping(null) ;
		}
		
		if (isCreated()){
			initContainer(getRealm());
		}
	}

//	@SuppressWarnings("unchecked")
//	protected QueryDelegate createDefaultDelegate(IRealm<?> r,
//			IListElement<?> rq) {
//		ArrayList<Column> dcolumnList = getColumnsList();
//		return new QueryDelegate((IRealm<Object>) r, dcolumnList,isImageOnFirstColumn()||dcolumnList.get(0).isImageFromBase(),getFilters());
//	}

	protected List<Column> getColumnsList() {
		if (dcolumnList == null) {
			dcolumnList = new ArrayList<Column>();
			{
				Column dcolumn = new Column() {
					/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					@Override
					public Object getElement(Object item) {
						return item;
					}
				};
				dcolumn.setId("label");
				dcolumn.setOwnerSelector(this);
				dcolumn.setImageFromBase(true);
				dcolumn.setTextFromBase(true);
				dcolumn.getLayoutData().setGrowth(1);
				dcolumnList.add(dcolumn);
			}
		}
		return dcolumnList;
	}
	
	public AndroidList2() {
		
	}

	protected AndroidList2(IBinding binding) {
		super();
		setBinding(binding);
	}
	
	@Override
	public void setBinding(IBinding binding) {
		super.setBinding(binding);
		Object value = binding.getValue();
		if (value instanceof INotifyableCollection) {
			ObjectChangeManager.registerRealm((INotifyableCollection<?>) value);
		}
	}

	@Override
	public boolean isNoScrollBar() {
		return false;
	}

	@Override
	public void setNoScrollBar(boolean isNoScrollBar) {
		throw new UnsupportedOperationException();
	}

	HashSet<ICanDrop> droppers = new HashSet<ICanDrop>();

	@Override
	public void addDropSupportParticipant(ICanDrop dropper) {
		droppers.add(dropper);
	}

	@Override
	public void removeDropSupportParticipant(ICanDrop dropper) {
		droppers.remove(dropper);
	}

	@Override
	public IRowStyleProvider getRowStyleProvider() {
		return provider;
	}

	@Override
	public void setRowStyleProvider(IRowStyleProvider rowStyleProvider) {
		this.provider = rowStyleProvider;
	}

	@Override
	public void addSelectionListener(ISelectionListener selectionHandler) {
		listeners.add(selectionHandler);
	}

	@Override
	public void removeSelectionListener(ISelectionListener selectionHandler) {
		listeners.remove(selectionHandler);
	}

	@Override
	public void removeOpenListener(IOpenListener iOpenListener) {
		olisteners.remove(iOpenListener);
	}

	@Override
	public void addOpenListener(IOpenListener iOpenListener) {
		olisteners.add(iOpenListener);
	}

	@Override
	public void addInterceptor(String kind, IActionInterceptor newInstance) {
		throw new UnsupportedOperationException();
	}

	public boolean isOpenOnDoubleClick() {
		return openOnDblClick;
	}

	@Override
	public void setOpenOnDoubleClick(boolean openOnDoubleClick) {
		this.openOnDblClick = openOnDoubleClick;
	}

	@Override
	public IContributionItem createSettingsContributionItem() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IContributionItem createRemoveSelectedContributionItem() {
		if (removeAction == null) {
			removeAction = new DefaultRemoveAction(0,this)  {
				
				@Override
				public String getImageId() {
					return "delete";
				}
				
				private static final long serialVersionUID = -3485970925683161101L;
	
				@Override
				public void internalRun() {
					if (!doConfirm) {
						internalDelete();
						return;
					}
					OkCancelDialog dialog = new OkCancelDialog(getControl().getContext()) {
						
						@Override
						protected View createContents() {
							String title = DELETE_ITEM_QUESTION_TEXT + "?";
							if (sel != null) {
								String name = DefaultMetaKeys.getName(MetaAccess.getMeta(sel.getFirstElement()));
								if (name != null && name.length() > 0) {
									title = DELETE_ITEM_QUESTION_TEXT + " " + name + " ?";
								}
							}
							sel.getFirstElement();
							TextView view = new TextView(getControl().getContext());
							view.setText(title);
							return view;
						}
						
						@Override
						protected void performOk() {
							internalDelete();
						}
					};
					dialog.setTitle(CONFIRM_DELETE_DIALOG_TITLE);
					dialog.show();
				}
				
				private void internalDelete() {
					Object expandedTreePaths = null;
					expandedTreePaths = preDelete(expandedTreePaths);
					try {
						final List<?> list = (owner.getViewerSelection()).toList();
						doDelete(list); 
					} finally {
						postDelete(expandedTreePaths);
					}
				}
	
				@Override
				protected boolean doConfirm() {
					
					return true;
				}
			};
		}
		removeAction.setDoConfirm(true);
		return removeAction;
	}

	@Override
	public IContributionItem createAddFromContributionItem() {
		return new AddFromRealmAction(Action.AS_PUSH_BUTTON,this);
	}

	@Override
	public IContributionItem createOpenContributionItem() {
		if (openAction == null) {
			openAction = new OpenAction(Action.AS_PUSH_BUTTON, this);
		}
		return openAction;
	}

	@Override
	public IContributionItem createRefreshContributionItem() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IContributionItem createAddNewContributionItem() {
		return new DefaultAddNewAction(Action.AS_PUSH_BUTTON, this);
	}

	@Override
	public IContributionItem createCopyContributionItem() {
		throw new UnsupportedOperationException();
	}

	public boolean isAllowCellEditing() {
		return allowCell;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean addDecorator(IObjectDecorator e) {
		if (ds == null) {
			ds = new HashSet<IObjectDecorator>();
		}
		return ds.add(e);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection<IObjectDecorator<?>> getDecorators() {
		return (Collection) ds;
	}

	@Override
	public String getElementRole() {
		return elementRole;
	}

	@Override
	public void setElementRole(String role) {
		this.elementRole = role;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean removeDecorator(IObjectDecorator o) {
		if (ds != null) {
			return ds.remove(o);
		}
		return false;
	}

	@HandlesAttributeDirectly("enableDirectEdit")
	public void setAllowCellEditing(boolean allowCellEditing) {
		this.allowCell = allowCellEditing;
	}

	@Override
	public void setPersistValueInSettings(boolean parseBoolean) {

	}

	@HandlesAttributeDirectly("imageOnFirstColumn")
	public void setImageOnFirstColumn(boolean parseBoolean) {
		this.imageOnFirstColumn=parseBoolean;
	}
	boolean imageOnFirstColumn=false;
	public boolean isImageOnFirstColumn() {
		return imageOnFirstColumn;
	}

	public com.onpositive.semantic.model.ui.generic.IStructuredSelection getViewerSelection() {
		if (isCreated()&&sel!=null) {
			
			return sel;
		}
		return new StructuredSelection();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setClusterizationPointProviders(String lock,
			IClusterizationPointProvider[] ns) {
		if (ns==null){
			asTree=false;
			setInitiallyGroup(null);
			return;
		}
		if (ns.length==1){
			String propId=null;
			AbstractClusterizationPointProvider<?>pm= (AbstractClusterizationPointProvider<?>) ns[0];
			if (pm instanceof PropertyValuePointProvider){
				PropertyValuePointProvider<?>pq=(PropertyValuePointProvider<?>) pm;
				propId= pq.getProperty().getId();
			}
			if (pm instanceof PropertyValueHierarchicalPointProvider){
				PropertyValueHierarchicalPointProvider<?>pq=(PropertyValueHierarchicalPointProvider)pm;
				propId=pq.getProperty().getId();
			}
			asTree=true;
			if (propId!=null){
				setInitiallyGroup(propId);
			}
			
			return;
		}
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setClusterizationPointProviders(
			IClusterizationPointProvider[] object) {
		setClusterizationPointProviders(null, object);
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	protected IBinding selectionBinding = new Binding() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Binding getParent() {
			return (Binding) (AndroidList2.this.getBinding());
		};

		public <U, A extends U> A getService(java.lang.Class<U> requestedClass) {
			return MetaAccess.getMeta(value).getMeta()
					.getService(requestedClass);
		};

		public void onChildChanged() {
			AbstractBinding abstractBinding = (AbstractBinding) getParent();
			if (abstractBinding != null) {
				abstractBinding.onChildChanged();
			}
		}

	};

	protected void selectionChanged(StructuredSelection sl) {
		this.sel = sl;
		for (ISelectionListener m : listeners) {
			m.selectionChanged(sl);
		}
		IBinding selectionBinding2 = getSelectionBinding();
		if (selectionBinding2 != null) {
			if (sl.size() == 1) {
				selectionBinding2.setValue(sl.getFirstElement(),
						binding_chlistener);
			} else {
				selectionBinding2.setValue(sl.toList(), binding_chlistener);
			}
		}
	}

	public IBinding getSelectionBinding() {
		if (isValueAsSelection()) {
			return getBinding();
		}
		return this.selectionBinding;
	}

	private final IBindingChangeListener<?> selectionListener = new IBindingChangeListener<Object>() {

		private static final long serialVersionUID = 7825490975821764328L;

		public void changed() {

		}

		public void enablementChanged(boolean isEnabled) {

		}

		public void valueChanged(ISetDelta<Object> valueElements) {
			setSelection(valueElements);
		}

	};

	private OpenAction openAction;

	private DefaultRemoveAction removeAction;
	@Override
	public void setSelectionBinding(IBinding binding) {
		// FIXME and in vaadin to
		if (this.isValueAsSelection()) {
			return;
		}
		if (this.selectionBinding != binding) {
			if (this.selectionBinding != null) {
				this.selectionBinding
						.removeBindingChangeListener(this.selectionListener);
			}
		}
		this.selectionBinding = binding;
		if (binding != null) {
			this.selectionBinding
					.addBindingChangeListener(this.selectionListener);
		}
	}

	protected void setSelection(ISetDelta<Object> valueElements) {
		// TODO Auto-generated method stub

	}

//	protected void onOpen(Item item) {
//		for (IOpenListener m : olisteners) {
//			m.open(getViewerSelection());
//		}
//		Action createOpenContributionItem = (Action) createOpenContributionItem();
//		createOpenContributionItem.run();
//		createOpenContributionItem.dispose();
//	}

	@Override
	public boolean isValueAsSelection() {
		return vasSelection;
	}

	@HandlesAttributeDirectly("useSelectionAsValue")
	public void setValueAsSelection(boolean v) {
		this.vasSelection = v;
	}

	@Override
	public void setAsCheckBox(boolean isAsCheckBox) {

	}

	@Override
	public boolean isAsCheckBox() {
		return false;
	}

	@Override
	@HandlesAttributeDirectly("asTree")
	public void setAsTree(boolean selection) {
		this.asTree = selection;
	}

	@Override
	public boolean isAsTree() {
		return asTree;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Object> getCurrentValue() {
		if (isValueAsSelection()) {
			return (Collection<Object>) getViewerSelection().toList();
		}
		return getRealm().getContents();
	}
	
	@Override
	protected void processValueChange(ISetDelta<?> valueElements) {
		if (!isCreated()) {
			return;
		}
		if (isValueAsSelection()) {
			Object value = binding.getValue();
			Collection<Object> collection = ValueUtils.toCollection(value);
			setSelection(collection);
			return;
		}
		if (isCreated()) {
			cRealm = null;
			initContainer(getRealm());
			if (!isValueAsSelection()){
				setSelection(new ArrayList<Object>());
			}
			((BaseAdapter) ((StructuredDataView)getControl()).getGridView().getAdapter()).notifyDataSetChanged();
			// dataSource.setRealm(getRealm());
			// getControl().getApplication().getMainWindow().executeJavaScript("javascript:vaadin.forceSync();");
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		dataSource.dispose();
		dataSource = null;
	}

	protected void initContainer(IRealm<Object> realm) {
		
		if (dataSource != null) {
//			dataSource.dispose();
		}
//		RealmContainer createContainer = createContainer(realm);
//		createContainer.addListener(new ItemSetChangeListener() {
//			
//			@Override
//			public void containerItemSetChange(ItemSetChangeEvent event) {
//				onRealmChanged();
//			}
//		});
//		if (this.dataSource!=null&&this.dataSource.getSorters()!=null){
//			createContainer.getSorters().addAll(this.dataSource.getSorters());
//		}
//		dataSource = createContainer;
//		if (isCreated()) {
//			ListView control = getControl();
//			postInit();
//			control.setContainerDataSource(dataSource);
//		}
//
//		dataSource.attach();
		
	}
//
//	protected RealmContainer createContainer(IRealm<Object> realm) {
//		return new RealmContainer(createDefaultDelegate(realm, this), realm);
//	}
//
	protected void setSelection(Collection<Object> collection) {
		//ListView control = (ListView) getControl();
	}
//
//	public boolean isHeaderVisible() {
//		return headerVisible;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeValues(Collection<Object> lsa) {
		if (isValueAsSelection()) {
			IStructuredSelection viewerSelection = getViewerSelection();
			List<Object> list = (List<Object>) viewerSelection.toList();
			list.removeAll(lsa);
			setSelection(list);
			binding.setValue(list, binding_chlistener);
			return;
		}
		RealmAccess.removeElements(getRealm(), lsa);
		commitToBinding();
	}

	private void commitToBinding() {
		Object value = binding.getValue();
		if (value instanceof IRealm) {
			return;
		}
		Collection<Object> contents = cRealm.getContents();
		binding.setValue(contents, binding_chlistener);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addValues(Collection<Object> lsa) {
		if (isValueAsSelection()) {
			IStructuredSelection viewerSelection = getViewerSelection();
			List<Object> list = (List<Object>) viewerSelection.toList();
			list.addAll(lsa);
			setSelection(list);
			binding.setValue(list, binding_chlistener);
			return;
		}
		RealmAccess.addElements(getRealm(), lsa);
		commitToBinding();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelection(IStructuredSelection structuredSelection) {
		setSelection((Collection<Object>) structuredSelection.toList());
	}

	@Override
	public IOrderMaintainer getOrderMaintainer() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addValue(Object value) {
		if (isValueAsSelection()) {
			IStructuredSelection viewerSelection = getViewerSelection();
			List<Object> list = (List<Object>) viewerSelection.toList();
			list.add(value);
			setSelection(list);
			binding.setValue(list, binding_chlistener);
			return;
		}
		RealmAccess.addElement(getRealm(), value);
		commitToBinding();
	}

	@Override
	public void move(Collection<Object> firstElement, boolean direction) {
		throw new IllegalStateException();
	}

	protected void postInit() {
//		getInterceptors("edit").add(editInterceptor);
//		ValueChangeListener listener = new ValueChangeListener() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void valueChange(ValueChangeEvent event) {
//				Object value2 = event.getProperty().getValue();
//				StructuredSelection sl = null;
//				if (value2 instanceof RowId) {
//					RowId v = (RowId) value2;
//					Object objectById = dataSource.getObjectById(v);
//					sl = new StructuredSelection(new Object[] { objectById });
//				} else {
//					Set<?> value = (Set<?>) value2;
//					if (null == value || value.size() == 0) {
//						sl = new StructuredSelection();
//					} else {
//						Object[] sla = new Object[value.size()];
//						int a = 0;
//						for (Object o : value) {
//							RowId v = (RowId) o;
//							Object objectById = dataSource.getObjectById(v);
//							sla[a++] = objectById;
//						}
//						sl = new StructuredSelection(sla);
//					}
//				}
//				selectionChanged(sl);
//			}
//		};
//		StructuredDataView control = (StructuredDataView) getControl();
//		control.addListener(listener);
	}

	@Override
	public IRealm<Object> getRealm() {
		if (cRealm != null) {
			return cRealm;
		}
		IBinding binding2 = getBinding();
		if (binding2 == null) {
			return null;
		}
		final Object value2 = binding2.getValue();
		IRealm<Object> realm = RealmUtils.getRealm(value2, binding2,
				isValueAsSelection());
		cRealm = realm;
		
		if(dataSource != null)
			cRealm.addRealmChangeListener( dataSource.getRealmListener() ) ;
		ObjectChangeManager.registerRealm((INotifyableCollection<?>) cRealm); //TODO correct registering & unregistering
		return realm;
	}

	@Override
	public void refresh() {
		processValueChange(null);
	}

	@Override
	protected void endCreate() {
		postInit();
		super.endCreate();
	}
	@Override
	public void move(boolean direction) {
		// TODO Not supported for now
	}
	@Override
	public boolean canMove(boolean direction) {
		return false;	// TODO Always false for now
	}
	@Override
	public void editValue(Object object, int pos) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void editElement(Object firstElement, int i) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected View internalCreate(AndroidComposite cm, Context context) {
		IQueryExecutor executor = createDefaultExecutor();
		QueryBasedTableModel2 tableModel = new QueryBasedTableModel2(new IColumn[] {new com.onpositive.businessdroids.model.impl.Column(new ToStringField())},executor);
		this.dataSource = tableModel ;
		initContainer( getRealm() );
		StructuredDataView dataView = new StructuredDataView(context, tableModel);
		dataView.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					showMenu();
					return true;
				}
				return false;
			}
		});
		configureDataView(dataView);
		return dataView;
	}
	
	protected void showMenu() {
		if (cManager instanceof IObjectContributionManager) {
			new DialogContributionPresenter(getControl().getContext()).presentObjectContributionItem((IObjectContributionManager) cManager,sel,getControl());
		} else {
			new DialogContributionPresenter(getControl().getContext()).presentContributionItem(cManager,getControl());
		}
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
	@Override
	public <R> R getService(Class<R> clazz) {
		if (IProvidesToolbarManager.class.isAssignableFrom(clazz)) {
			return (R) toolbarManager;
		}
		return super.getService(clazz);
	}
	
	protected void configureDataView( final StructuredDataView dataView) {		
		
		IContributionItemProvider contributionItemProvider = new IContributionItemProvider() {
			
			ColumnContributionItemProvider ccip = new ColumnContributionItemProvider(dataView) ;
			
			@Override
			public List<com.onpositive.businessdroids.ui.actions.IContributionItem> getContributionItemsFor( IColumn column) {
				return ccip.getContributionItemsFor( column);
			}
			
			@Override
			public List<com.onpositive.businessdroids.ui.actions.IContributionItem> getCommonContributionItems() {
				ArrayList<com.onpositive.businessdroids.ui.actions.IContributionItem> lst =
						new ArrayList<com.onpositive.businessdroids.ui.actions.IContributionItem>() ;
				
				lst.addAll( toolbarManager.getContributionItems() ) ;
//				lst.add(new FilterActionContribution(new BasicStringFilter(this.dataView, "",
//					BasicStringFilter.CONTAIN_MODE)));
//				lst.addAll( ccip.getCommonContributionItems() ) ;
				return lst;
				
			}
		};  
				;
		dataView.setContributionItemProvider(contributionItemProvider);
		toolbarManager.setActionBar(dataView.getActionBar());
		dataView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				Object item = ((ArrayAdapter<?>)paramAdapterView.getAdapter()).getItem(paramInt);
				StructuredSelection structuredSelection = new StructuredSelection(new Object[] {item});
				selectionChanged(structuredSelection);
			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				selectionChanged(new StructuredSelection());
			}
		});
		
		dataView.setOpenListener(new com.onpositive.businessdroids.ui.IOpenListener() {
			
			@Override
			public void openElement(View parent, View elementView, Object element, int pos) {
				sel = new StructuredSelection(new Object[]{element});
				((IAction) createOpenContributionItem()).run();
			}
		});
		
		dataView.setLongClickListener(new ILongClickListener() {
			
			@Override
			public void handleLongClick(View parent, View elementView, Object element) {
				sel = new StructuredSelection(new Object[]{element});
//				((IAction) createRemoveSelectedContributionItem()).run();
				showMenu();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	protected IQueryExecutor createDefaultExecutor() {
		Object value = getBinding()!=null?getBinding().getValue():null;
		IQueryExecutor queryExecutor = null;
		while(true){
			if (value == null)
				break ;
			
			if (value instanceof IMeta)
				queryExecutor = ((IMeta) value).getService(IQueryExecutor.class);
			
			if( queryExecutor != null )
				break ;
			
			IRealm<Object> realm = null ;
			if( value instanceof IRealm ){
				realm = (IRealm<Object>) value ;
			}
			else if (value instanceof Collection) {
				realm = new Realm<Object>(value);				
			}
			
			if( realm != null ){
				realm.addRealmChangeListener(new IRealmChangeListener<Object>() {

					private static final long serialVersionUID = 1890484261019484624L;

					@Override
					public void realmChanged(IRealm<Object> realmn, ISetDelta<Object> delta) {
						for (Column column : getColumnsList()) {
							column.changed(delta);
						}
						refresh();
					}
				});
				queryExecutor = new InMemoryExecutor(realm);
				break ;
			}			
			
			if (value instanceof Iterable) {
				final IQueryExecutor finalExecutor = queryExecutor = new InMemoryExecutor((Iterable<Object>) value);
				getBinding().addValueListener(new IValueListener<Object>() {
					
					private static final long serialVersionUID = 8780789827416889253L;
					
					@Override
					public void valueChanged(Object oldValue, Object newValue) {
						if (newValue instanceof Iterable) {
							((InMemoryExecutor) finalExecutor).setSpace((Iterable<Object>) newValue);
							refresh();
						}
					}
				});
			}
		
			break ;
		}
		
		return queryExecutor;
	}
	
	@Override
	protected ContributionManager createContributionManager() {
		return new ObjectContributionManager();
	}
	
	@Override
	public boolean propagateKeystroke(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showMenu();
			return true;
		}
		return false;
	}
}
