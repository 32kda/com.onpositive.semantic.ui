package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.Activator;
import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.common.ui.roles.WidgetRegistry;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.property.IObjectRealm;
import com.onpositive.semantic.model.api.property.IOrderListener;
import com.onpositive.semantic.model.api.property.IOrderMaintainer;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.roles.IObjectDecorator;
import com.onpositive.semantic.model.api.roles.ImageDescriptor;
import com.onpositive.semantic.model.api.roles.ImageManager;
import com.onpositive.semantic.model.api.undo.UndoRedoSupportExtension;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.binding.IBindingDepending;
import com.onpositive.semantic.model.binding.ICommitListener;
import com.onpositive.semantic.model.realm.HashDelta;
import com.onpositive.semantic.model.realm.IFactory;
import com.onpositive.semantic.model.realm.IFilter;
import com.onpositive.semantic.model.realm.IFiltrable;
import com.onpositive.semantic.model.realm.IIdentifiableObject;
import com.onpositive.semantic.model.realm.IIdentifiableRealm;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.realm.ISimpleChangeListener;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.generic.ElementListenerAdapter;
import com.onpositive.semantic.model.ui.generic.IColumnValueListener;
import com.onpositive.semantic.model.ui.generic.IMayHaveDecorators;
import com.onpositive.semantic.model.ui.generic.IRowStyleProvider;
import com.onpositive.semantic.model.ui.generic.widgets.IActionInterceptor;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.property.editors.DecoratableEditor;
import com.onpositive.semantic.model.ui.viewer.structured.RealmLazyContentProvider;

public abstract class AbstractEnumeratedValueSelector<T> extends
		DecoratableEditor<T, Control> implements IMayHaveDecorators<Control>,
		IHasEditingActions, IFiltrable,ISelectorElement<Control>,IColumnValueListener{

	private boolean isOrdered;

	public final static String EDIT_ACTION = "edit";

	public final static String ADD_ACTION = "add";

	public final static String REMOVE_ACTION = "remove";

	protected HashMap<String, HashSet<IActionInterceptor>> interceptors = new HashMap<String, HashSet<IActionInterceptor>>();

	@ChildSetter( value="interceptor", needCasting = false )
	public void addInterceptor(String interceptorKind,
			IActionInterceptor interceptor) {
		HashSet<IActionInterceptor> hashSet = interceptors.get(interceptorKind);
		if (hashSet == null) {
			hashSet = new HashSet<IActionInterceptor>();
			interceptors.put(interceptorKind, hashSet);
		}
		hashSet.add(interceptor);
	}

	public void removeInterceptor(String interceptorKind,
			IActionInterceptor interceptor) {
		HashSet<IActionInterceptor> hashSet = interceptors.get(interceptorKind);
		if (hashSet != null) {
			hashSet.remove(interceptor);
			if (hashSet.isEmpty()) {
				interceptors.remove(interceptorKind);
			}
		}
	}

	public boolean isOrdered() {
		return isOrdered;
	}
	
	HashSet<IBindingChangeListener<IFilter>>filterListener=new HashSet<IBindingChangeListener<IFilter>>();

	protected void fireFilterChange(ISetDelta<IFilter>dlt){
		for (IBindingChangeListener<IFilter>f:filterListener){
			f.valueChanged(dlt);
		}
	}
	
	public void addFilterChangeListener(
			IBindingChangeListener<IFilter> listeners) {
		filterListener.add(listeners);
	}

	public void removeFilterChangeListener(
			com.onpositive.semantic.model.binding.IBindingChangeListener<IFilter> listeners) {
		filterListener.remove(listeners);
	};

	@HandlesAttributeDirectly("isOrdered")
	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;
	}

	private final class ViewerFilterAdapter extends ViewerFilter implements
			ISimpleChangeListener<IFilter> {
		private final IFilter filter;

		private ViewerFilterAdapter(IFilter filter) {
			this.filter = filter;
			filter.addFilterListener(this);
		}

		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			return this.filter.accept(element);
		}

		public void changed(IFilter provider, Object extraData) {
			if (AbstractEnumeratedValueSelector.this.isCreated()) {
				AbstractEnumeratedValueSelector.this.viewer.refresh();
			}
		}
	}

	
	@SuppressWarnings("serial")
	public static abstract class OwnedAction extends Action implements
			com.onpositive.semantic.model.ui.generic.widgets.impl.IEnablementListener {

		//private static final long serialVersionUID = 5109903689056601566L;
		static HashMap<String, String> interceptorToImage = new HashMap<String, String>();
		static HashMap<String, String> interceptorToDisabledImage = new HashMap<String, String>();

		static {
			interceptorToImage.put(ADD_ACTION, "com.onpositive.semantic.ui.add");
			interceptorToImage.put(EDIT_ACTION,	"com.onpositive.semantic.ui.edit");
			interceptorToImage.put(REMOVE_ACTION,"com.onpositive.semantic.ui.delete");
			interceptorToDisabledImage.put(REMOVE_ACTION,"com.onpositive.semantic.ui.deleted");
		}
		
		final AbstractEnumeratedValueSelector<?> owner;
		final String interceptorKind;
		private ISelectionChangedListener selectionChangedListener;


		final class CommmitListener implements ICommitListener {
			private final StructuredSelection sel;

			private CommmitListener(StructuredSelection sel) {
				this.sel = sel;
			}

			public void commitPerformed(ICommand command) {
				HashSet<IActionInterceptor> hashSet = owner.interceptors
						.get(interceptorKind);
				if (hashSet != null) {
					for (IActionInterceptor i : hashSet) {
						i.postAction((IListElement<?>) owner, convertSelection(sel));
					}
				}

			}
		}
		public OwnedAction(String interceptorKind, int style, AbstractEnumeratedValueSelector<?> sl)
		{
			super( style);
			this.interceptorKind = interceptorKind;
			this.owner = sl;
			selectionChangedListener = new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					setEnabled(shouldBeEnabled((StructuredSelection) event
							.getSelection()));
				}

			};
			sl.addElementListener(new ElementListenerAdapter() {

				public void elementCreated(IUIElement<?> element) {
					
					setEnabled(shouldBeEnabled((StructuredSelection) owner
							.getViewer().getSelection()));
				}

				public void elementDisposed(IUIElement<?> element) {
					owner.getViewer().removeSelectionChangedListener(
							selectionChangedListener);
				}

			});
			
			this.owner.addEnablementListener(this);
			if (owner.isCreated()) {
				setEnabled(shouldBeEnabled((StructuredSelection) owner
						.getViewer().getSelection()));
				owner.getViewer().addSelectionChangedListener(
						selectionChangedListener);
				}
		}

		public ImageDescriptor getDisabledImageDescriptor() {

			ImageDescriptor disabledImageDescriptor = super
					.getDisabledImageDescriptor();
			if (disabledImageDescriptor == null
						&& super.getImageDescriptor() == null) {
				String id = interceptorToDisabledImage.get(interceptorKind);
				if (id != null) {
					return ImageManager.getImageDescriptor(id);
				}
			}
			return disabledImageDescriptor;
		}

		public com.onpositive.semantic.model.api.roles.IStructuredSelection convertSelection(
				final StructuredSelection sel) {
			return SelectionConverter.from(sel);
		}

		public ImageDescriptor getImageDescriptor() {
			ImageDescriptor imageDescriptor = super.getImageDescriptor();
			if (imageDescriptor == null) {
				String id = interceptorToImage.get(interceptorKind);
				if (id != null) {
					return ImageManager.getImageDescriptor(id);
				}
			}
			return imageDescriptor;
		}

		
		

		public void dipose() {
			this.owner.removeEnablementListener(this);
			if (owner.viewer != null) {
				owner.viewer
						.removeSelectionChangedListener(selectionChangedListener);
			}
		}

		public final AbstractEnumeratedValueSelector<?> getOwner() {
			return this.owner;
		}

		public void run() {
			HashSet<IActionInterceptor> hashSet = owner.interceptors
					.get(interceptorKind);
			if (hashSet != null) {
				StructuredSelection selection = (StructuredSelection) owner
						.getViewer().getSelection();
				for (IActionInterceptor i : hashSet) {
					if (!i.preAction((IListElement<?>) owner, convertSelection((StructuredSelection) owner
							.getViewer().getSelection()))) {
						return;
					}

				}
				internalRun();
				for (IActionInterceptor i : hashSet) {
					if (!i.postAction((IListElement<?>) owner, convertSelection(selection))) {
						return;
					}

				}
				return;
			} else {
				internalRun();
			}
		}

		public abstract void internalRun();

		public void enablementChanged(IUIElement<?> element,
				boolean enabled) {
			this.setEnabled(enabled && this.isActuallyEnabled());
		}

		protected boolean shouldBeEnabled(StructuredSelection selection) {
			HashSet<IActionInterceptor> hashSet = owner.interceptors
					.get(interceptorKind);
			if (hashSet != null) {
				for (IActionInterceptor i : hashSet) {
					if (!i.isEnabled((IListElement<?>) owner, convertSelection(selection))) {
						return false;
					}
				}
			}
			return owner.isEnabled() && isActuallyEnabled();
		}

		public abstract boolean isActuallyEnabled();

	}

	public static class AddFromRealmAction extends OwnedAction {

		public AddFromRealmAction( int asPushButton, AbstractEnumeratedValueSelector<?> abstractEnumeratedValueSelector)
		{
			super(ADD_ACTION, asPushButton, abstractEnumeratedValueSelector);
		}

		private String themeId;

		@SuppressWarnings("unchecked")
		public void internalRun() {
			if (this.owner.isValueAsSelection()) {
				throw new IllegalStateException();
			} else {
				WidgetRegistry.getInstance().addFromRealm(
						(ISelectorElement) this.owner,
						this.themeId,
						new CommmitListener((StructuredSelection) owner
								.getViewer().getSelection()),
						owner.getUndoContext());
			}
		}

		public String getThemeId() {
			return this.themeId;
		}

		public void setThemeId(String themeId) {
			this.themeId = themeId;
		}

		public boolean isActuallyEnabled() {
			return true;
		}

	}

	public static class AddNewAction extends OwnedAction {

		public AddNewAction(
				int asPushButton,
				AbstractEnumeratedValueSelector<?> abstractEnumeratedValueSelector) {
			super(ADD_ACTION, asPushButton, abstractEnumeratedValueSelector);

		}

		private String typeId;

		private String themeId;

		private Class<?> objectClass;

		public void setWidgetId(String themeId) {
			this.themeId = themeId;
		}

		public String getTypeId() {
			return this.typeId;
		}

		public void setTypeId(String typeId) {
			this.typeId = typeId;
		}

		public String getWidgetId() {
			return this.themeId;
		}

		@SuppressWarnings("unchecked")
		public void internalRun() {
			if (!this.owner.isValueAsSelection() && (this.objectClass != null)) {
				try {
					final Object object = this.objectClass.newInstance();
					WidgetRegistry
							.getInstance()
							.showNewObjectWidget(
									(ISelectorElement)this.owner,
									this.objectClass,
									this.typeId,
									this.themeId,
									object,
									new CommmitListener(
											(StructuredSelection) owner
													.getViewer().getSelection()),
									owner.getUndoContext());
				} catch (final InstantiationException e) {
					Activator.log(e);
				} catch (final IllegalAccessException e) {
					Activator.log(e);
				}
			} else {
				final IRealm<?> realm = this.owner.getRealm();
				if (realm instanceof IObjectRealm<?>) {
					WidgetRegistry.getInstance().showAddToNewObjectRealmWidget(
							(IObjectRealm<?>) realm, this.typeId, this.themeId,
							owner.getUndoContext());
				} else {
					throw new UnsupportedOperationException();
				}
			}
		}

		public Class<?> getObjectClass() {
			return this.objectClass;
		}

		public void setObjectClass(Class<?> objectClass) {
			this.objectClass = objectClass;
		}

		public boolean isActuallyEnabled() {
			return true;
		}
	}

	public static class OpenAction extends OwnedAction {

		boolean isDirectEdit;
		String widgetId;
		private String theme;
		private String role;

		public String getWidgetId() {
			return widgetId;
		}

		public void setWidgetId(String widgetId) {
			this.widgetId = widgetId;
		}

		public boolean isDirectEdit() {
			return isDirectEdit;
		}

		public String getTheme() {
			return theme;
		}

		public void setDirectEdit(boolean isDiectEdit) {
			this.isDirectEdit = isDiectEdit;
		}

		public OpenAction(int style, final AbstractEnumeratedValueSelector<?> sl) {
			super(EDIT_ACTION, style, sl);
			this.setActionDefinitionId("com.onpositive.semantic.ui.workbench.openCommand");

		}

		public boolean isActuallyEnabled() {
			if (this.owner != null) {
				final StructuredViewer viewer2 = this.owner.getViewer();
				if (viewer2 != null) {
					final StructuredSelection sel = (StructuredSelection) viewer2
							.getSelection();
					return sel.size() == 1;
				}
				return false;
			} else {
				return false;
			}
		}

		public void internalRun() {
			final ColumnViewer viewer = (ColumnViewer) this.owner.getViewer();
			final StructuredSelection sel = (StructuredSelection) viewer
					.getSelection();
			if (!sel.isEmpty()) {
				if (isDirectEdit) {
					viewer.editElement(sel.getFirstElement(), 0);
				} else {
					Object firstElement = sel.getFirstElement();
					if (firstElement instanceof ITreeNode<?>) {
						firstElement = ((ITreeNode<?>) firstElement)
								.getElement();
					}
					WidgetRegistry.getInstance().showEditObjectWidget(
							firstElement,
							role == null ? owner.getRole() : role,
							theme == null ? owner.getTheme() : theme, widgetId,
							new CommmitListener(sel), owner.getUndoContext());
				}
			}
		}

		public void setTheme(String theme) {
			this.theme = theme;
		}

		public void setRole(String role) {
			this.role = role;
		}
	}

	public static class RemoveAction extends OwnedAction {

		boolean doConfirm;
		private String title = "";
		private String description;

		public boolean isDoConfirm() {
			return this.doConfirm;
		}

		public void setDoConfirm(boolean doConfirm) {
			this.doConfirm = doConfirm;
		}

		public String getConfirmTitle() {
			return this.title;
		}

		public void setConfirmTitle(String title) {
			this.title = title;
		}

		public String getConfirmDescription() {
			if (this.title.length() > 0) {
				if (this.description.length() == 0) {
					return this.title;
				}
			}
			return this.description;
		}

		public void setConfirmDescription(String description) {
			this.description = description;
		}

		private RemoveAction(int style,	final AbstractEnumeratedValueSelector<?> vl) {
			super(REMOVE_ACTION, style, vl);
			this.setActionDefinitionId("org.eclipse.ui.edit.delete");
			this.setId("delete");
		}

		public boolean isActuallyEnabled() {
			if (this.owner != null) {
				final StructuredViewer viewer2 = this.owner.getViewer();
				if (viewer2 != null) {
					return !viewer2.getSelection().isEmpty();
				}
				return false;
			} else {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		public void internalRun() {
			if (this.doConfirm
					|| (this.title != null && this.title.length() > 0)) {
				final boolean openConfirm = MessageDialog.openConfirm(Display
						.getCurrent().getActiveShell(), this.title, this
						.getConfirmDescription());
				if (!openConfirm) {
					return;
				}
			}
			final List list = ((StructuredSelection) this.owner.getViewer()
					.getSelection()).toList();

			if (!this.owner.isValueAsSelection()) {
				this.owner.removeValues(list);
			} else {
				final IRealm<IIdentifiableObject> realm2 = (IRealm) this.owner
						.getRealm();
				if (realm2 instanceof IObjectRealm) {
					final IObjectRealm<IIdentifiableObject> er = (IObjectRealm<IIdentifiableObject>) realm2;
					final CompositeCommand cm = new CompositeCommand();
					cm.setUndoContext(owner.getUndoContext());
					for (final Object o : list) {
						IIdentifiableObject o2 = null;
						if (o instanceof IIdentifiableObject) {
							o2 = (IIdentifiableObject) o;
						} else if (o instanceof ITreeNode<?>) {
							ITreeNode<?> n = (ITreeNode<?>) o;
							o2 = (IIdentifiableObject) n.getElement();
						}
						final ICommand objectDeletionCommand = er
								.getObjectDeletionCommand(o2);
						cm.addCommand(objectDeletionCommand);
					}
					er.execute(cm);
				}
			}
		}
	}

	private IFactory addFromRealmFactory;
	private IFactory addNewFactory;
	private IFactory openFactory;
	private IFactory removeFactory;

	public void setAddFromRealmActionFactory(IFactory factory) {
		this.addFromRealmFactory = factory;
	}

	public Object getUndoContext() {

		IBinding binding = getBinding();
		if (binding != null) {
			Object undoContext = binding.getUndoContext();
			if (undoContext == null) {
				return UndoRedoSupportExtension.getDefaultContext();
			}
			return undoContext;
		}
		return null;
	}

	public void setAddNewActionFactory(IFactory factory) {
		this.addNewFactory = factory;
	}

	public void setOpenElementsActionFactory(IFactory factory) {
		this.openFactory = factory;
	}

	public void setRemoveElementsActionFactory(IFactory factory) {
		this.removeFactory = factory;
	}

	public RemoveAction createRemoveSelectedAction() {
		if (this.removeFactory != null) {
			return (RemoveAction) this.removeFactory.getValue(this);
		}
		return new RemoveAction(IAction.AS_PUSH_BUTTON, this);
	}

	public AddNewAction createAddNewAction() {
		if (this.addNewFactory != null) {
			return (AddNewAction) this.addNewFactory.getValue(this);
		}
		return new AddNewAction(IAction.AS_PUSH_BUTTON, this);
	}

	public AddFromRealmAction createAddFromRealm() {
		if (this.addFromRealmFactory != null) {
			return (AddFromRealmAction) this.addFromRealmFactory.getValue(this);
		}
		return new AddFromRealmAction(IAction.AS_PUSH_BUTTON, this);
	}

	public IAction createAddFromRealmAction() {
		return this.createAddFromRealm();
	}

	public IAction createOpenAction() {
		if (this.openFactory != null) {
			return (IAction) this.openFactory.getValue(this);
		}
		return new OpenAction(IAction.AS_PUSH_BUTTON, this);
	}

	public IAction createRemoveElementsAction() {
		return this.createRemoveSelectedAction();
	}

	protected HashSet<IObjectDecorator> decorators;

	protected IRealm<T> valueModel;

	protected StructuredViewer viewer;

	protected IContentProvider provider;

	protected IContentProvider uprovider;

	protected Object value;

	private String elementRole = "row"; //$NON-NLS-1$

	private boolean persistValue = false;

	public StructuredViewer getViewer() {
		return this.viewer;
	}

	public void setSingleValue(T object) {
		this.value = object;
		if (this.isCreated()) {
			if (object == null) {
				this.viewer.setSelection(new StructuredSelection());
			}
			this.viewer.setSelection(new StructuredSelection(this.value));
		}
	}

	@SuppressWarnings("unchecked")
	public void addValues(Collection object) {
		final StructuredSelection selection = (StructuredSelection) this.viewer
				.getSelection();
		final List<Object> list = new ArrayList<Object>(selection.toList());
		list.addAll(object);
		this.viewer.setSelection(new StructuredSelection(list), true);
	}

	@SuppressWarnings("unchecked")
	public void removeValues(Collection<Object> object) {
		final StructuredSelection selection = (StructuredSelection) this.viewer
				.getSelection();
		final List<Object> list = new ArrayList<Object>(selection.toList());
		list.removeAll(object);
		this.viewer.setSelection(new StructuredSelection(list), true);
	}

	@SuppressWarnings("unchecked")
	public void addValue(Object object) {
		final StructuredSelection selection = (StructuredSelection) this.viewer
				.getSelection();
		final List<Object> list = new ArrayList<Object>(selection.toList());
		list.add(object);
		this.viewer.setSelection(new StructuredSelection(list), true);
	}

	@SuppressWarnings("unchecked")
	public void removeValue(T object) {
		final StructuredSelection selection = (StructuredSelection) this.viewer
				.getSelection();
		final List<Object> list = new ArrayList<Object>(selection.toList());
		list.remove(object);
		this.viewer.setSelection(new StructuredSelection(list), true);
	}

	public void setValue(Collection<T> object) {
		this.value = object;
		if (this.isCreated()) {
			this.viewer.setSelection(new StructuredSelection(object.toArray()));
		}
	}

	protected void internalSetBinding(IBinding binding) {
		// IBinding old = this.getBinding();

		if (this.viewer != null) {
			this.initFromBinding(binding);
		}
	}

	public String getElementRole() {
		return this.elementRole;
	}

	public void setElementRole(String role) {
		this.elementRole = role;
		if (this.isCreated()) {
			this.viewer.refresh();
		}
	}

	private void initFromBinding(IBinding binding) {
		final ITextLabelProvider pr = binding
				.getAdapter(ITextLabelProvider.class);
		this.initLabelProvider(pr);
		this.initializeContent(binding);
		this.initializeFilter(binding);
	}

	private void initializeFilter(IBinding binding) {
		for (final IFilter f : this.filters) {
			if (f instanceof IBindingDepending) {
				final IBindingDepending b = (IBindingDepending) f;
				b.setBinding((Binding) binding);
			}
		}
	}

	public void setTheme(String theme) {
		super.setTheme(theme);
		if (this.isCreated()) {
			this.viewer.refresh();
		}
	}

	private IRowStyleProvider rowStyleProvider;
	private IOrderListener orderListener = new IOrderListener() {

		public void orderChanged() {
			if (isCreated()) {
				viewer.refresh();
				IContentProvider contentProvider2 = viewer.getContentProvider();
				if (contentProvider2 instanceof IRealmContentProvider) {
					IRealmContentProvider contentProvider = (IRealmContentProvider) contentProvider2;
					contentProvider.refresh();
				}
				processSelection((StructuredSelection) viewer.getSelection());
			}
		}

	};

	public IRowStyleProvider getRowStyleProvider() {
		return rowStyleProvider;
	}

	public void setRowStyleProvider(IRowStyleProvider rowStyleProvider) {
		this.rowStyleProvider = rowStyleProvider;
		if (isCreated()) {
			IBaseLabelProvider labelProvider = viewer.getLabelProvider();
			if (labelProvider instanceof UniversalLabelProvider<?>) {
				UniversalLabelProvider<?> l = (UniversalLabelProvider<?>) labelProvider;
				l.setRowStyleProvider(rowStyleProvider);
				viewer.refresh();
			}
		}
	}

	private ILabelProvider labelProvider;

	public ILabelProvider getLabelProvider() {
		if (viewer != null) {
			return (ILabelProvider) viewer.getLabelProvider();
		}
		return labelProvider;
	}

	@ChildSetter( value = "contentProvider", needCasting=false )
	@HandlesAttributeDirectly("labelProvider")
	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
		if (viewer != null) {
			viewer.setLabelProvider(labelProvider);
		}
	}

	protected void initLabelProvider(final ITextLabelProvider pr) {
		if (labelProvider != null) {
			viewer.setLabelProvider(labelProvider);
			return;
		}
		if (pr != null) {
			if (pr instanceof ILabelProvider) {
				final ILabelProvider llp = (ILabelProvider) pr;
				this.viewer.setLabelProvider(llp);
			} else {
				UniversalLabelProvider<T> universalLabelProvider = new UniversalLabelProvider<T>(
						this, pr);
				final ColumnLabelProvider ll = universalLabelProvider;
				universalLabelProvider.setRowStyleProvider(rowStyleProvider);
				this.viewer.setLabelProvider(ll);
			}
		} else {
			UniversalLabelProvider<T> labelProvider = new UniversalLabelProvider<T>(
					this, null);
			labelProvider.setRowStyleProvider(rowStyleProvider);
			this.viewer.setLabelProvider(labelProvider);
		}
	}

	@SuppressWarnings("unchecked")
	protected void initializeContent(IBinding binding2) {

		final IRealm<Object> realm2 = this.getRealm(binding2);
		final IRealm<Object> realm = UIRealm.toUI(realm2);
		if (realm != null) {
			this.initSorting(realm);
			this.viewer.setInput(realm);
		} else {
			if (this.valueModel!=null){
				this.viewer.setInput(valueModel);	
			}
			else{
			this.viewer.setInput(null);
			}
		}
		final Object value2 = this.getBinding().getValue();
		if (adapter != null) {
			adapter.setRealm(realm);
			adapter.init(this.getBinding().getObject(), value);

		}
		this.installValue((T) value2);
	}

	protected void initSorting(IRealm<Object> realm) {
		if (realm.isOrdered()) {
			this.viewer.setSorter(null);
		} else {
			this.viewer.setSorter(new ViewerSorter());
		}
	}

	protected IRealm<Object> getRealm(IBinding binding2) {
		return binding2.getRealm();
	}

	public void setRealm(IRealm<T> realm) {
		this.valueModel = UIRealm.toUI(realm);
		if (this.viewer != null) {
			this.viewer.setInput(this.valueModel);
			this.viewer.setComparator(realm.isOrdered() ? null
					: new ViewerComparator());
		}
	}

	@SuppressWarnings("unchecked")
	public IRealm<Object> getRealm() {
		final IBinding binding2 = this.getBinding();

		return (IRealm<Object>) (this.valueModel != null ? this.valueModel
				: binding2 != null ? binding2.getRealm() : null);
	}

	public Control createControl(Composite parent) {

		this.viewer = this.createViewer(parent);
		Control control = initViewer(parent);
		if (selection != null) {
			setSelection(selection);
		}
		return control;
	}

	private Control initViewer(Composite parent) {
		this.initSorting();
		initDragDrop();
		this.initContentProvider();
		if (this.valueModel != null) {
			this.viewer.setInput(this.valueModel);
		}
		this.initSelection();
		final IBinding binding2 = this.getBinding();
		if (this.isRedraw()) {
			this.viewer.getControl().setRedraw(false);
		}
		if (binding2 != null) {
			this.initFromBinding(binding2);
		} else {
			this.initLabelProvider(null);
		}
		this.initListeners();
		Control control = this.viewer.getControl();
		while (control.getParent() != parent) {
			control = control.getParent();
		}
		if (this.isRedraw()) {
			this.viewer.getControl().setRedraw(true);
		}
		for (final IFilter f : this.filters) {
			this.initFilter(f);
		}
		return control;
	}

	protected void initDragDrop() {

	}

	protected void initContentProvider() {
		// provider = new RealmContentProvider();
		// viewer.setContentProvider(provider);
		if (uprovider != null) {
			this.viewer.setContentProvider(uprovider);
			this.provider = uprovider;
		} else {
			final RealmLazyContentProvider realmLazyContentProvider = new RealmLazyContentProvider();
			this.viewer.setContentProvider(realmLazyContentProvider);
			this.registerProvider(realmLazyContentProvider);
			this.provider = realmLazyContentProvider;
		}
	}

	protected void registerProvider(
			IRealmContentProvider realmLazyContentProvider) {
		realmLazyContentProvider
				.addListener(new ISimpleChangeListener<IRealmContentProvider>() {

					public void changed(IRealmContentProvider provider,
							Object extraData) {
						AbstractEnumeratedValueSelector.this
								.handleChange(extraData);
					}

				});
		initComparator(realmLazyContentProvider);
	}

	protected void initComparator(IRealmContentProvider realmLazyContentProvider) {
		if (!isOrdered) {
			realmLazyContentProvider.setComparator(new Comparator<Object>() {

				@SuppressWarnings("unchecked")
				public int compare(Object o1, Object o2) {// TODO Auto-generated
					// method stub
					if ((o1 instanceof Comparable)
							&& (o2 instanceof Comparable)) {
						final Comparable<Object> oc1 = (Comparable<Object>) o1;
						final Comparable<Object> oc2 = (Comparable<Object>) o2;
						try {
							return -oc1.compareTo(oc2);
						} catch (final ClassCastException e) {
						}
					}
					final String txt = ((ILabelProvider) AbstractEnumeratedValueSelector.this.viewer
							.getLabelProvider()).getText(o1);
					final String txt2 = ((ILabelProvider) AbstractEnumeratedValueSelector.this.viewer
							.getLabelProvider()).getText(o2);
					return -txt.compareTo(txt2);
				}

			}, false);
		} else {
			IBinding binding = getBinding();
			if (adapter != null) {
				adapter.removeOrderListener(orderListener);
			}
			adapter = binding.getAdapter(IOrderMaintainer.class);
			if (adapter != null) {
				adapter.addOrderListener(orderListener);
				realmLazyContentProvider.setComparator(adapter, false);
			}
		}
	}

	protected void handleChange(Object extraData) {
		if (this.isCreated()) {
			final ILabelProvider ps = (ILabelProvider) this.viewer
					.getLabelProvider();
			if (ps instanceof UniversalLabelProvider<?>) {
				final UniversalLabelProvider<?> la = (UniversalLabelProvider<?>) ps;
				la.clearCache();
			}
		}
	}

	protected void initListeners() {
		this.viewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						final StructuredSelection ss = (StructuredSelection) event
								.getSelection();
						AbstractEnumeratedValueSelector.this
								.processSelection(ss);
					}

				});
	}

	protected void initSorting() {
		final IRealm<Object> realm = this.getRealm();
		if (realm != null) {
			this.viewer.setComparator(realm.isOrdered() ? null
					: new ViewerComparator());
		}
	}

	protected void initSelection() {
		if (this.value != null) {
			this.viewer.setSelection(new StructuredSelection(this.value));
		}
	}

	protected abstract StructuredViewer createViewer(Composite parent);

	public void dispose() {
		if (adapter != null) {
			adapter.removeOrderListener(orderListener);
		}
		this.provider.dispose();
		super.dispose();
		this.viewer = null;
	}

	@SuppressWarnings("unchecked")
	public void installValue(T value2) {
		if (value2 instanceof Collection) {
			final Collection<Object> values = (Collection<Object>) value2;
			this.viewer.setSelection(new StructuredSelection(values.toArray()));
		} else {
			if (value2 != null) {
				this.viewer.setSelection(new StructuredSelection(value2));
			} else {
				this.postProcessSelection(new HashSet<Object>());
			}
		}
	}

	protected void processSelection(StructuredSelection ss) {
		if (this.shouldIgnoreChanges()) {
			return;
		}
		final int size = ss.size();
		if (size == 0) {
			this.value = null;
			if (this.getBinding() != null) {
				this.commitToBinding(null);
			}
		} else if (size == 1) {
			this.value = ss.getFirstElement();
			this.commitToBinding(this.value);
		} else {
			this.value = ss.toList();
			this.commitToBinding(this.value);
		}
	}

	@SuppressWarnings("unchecked")
	protected void processValueChange(ISetDelta<?> valueElements) {
		if (viewer != null) {
			final StructuredSelection ss = (StructuredSelection) (this.viewer
					.getSelection());
			final HashSet<Object> elements = new HashSet<Object>(ss.toList());
			this.viewer.getControl().setRedraw(false);
			elements.addAll(valueElements.getAddedElements());
			elements.removeAll(valueElements.getRemovedElements());
			this.postProcessSelection(elements);

			this.viewer.update(valueElements.getChangedElements().toArray(),
					null);
			this.viewer.getControl().setRedraw(true);
		}
	}

	protected void postProcessSelection(HashSet<Object> elements) {
		// this.viewer.setSelection(new
		// StructuredSelection(elements.toArray()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.semantic.model.ui.property.editors.IMayHaveDecoratable
	 * #addDecorator(com.onpositive.semantic.common.ui.roles.IObjectDecorator)
	 */
	public boolean addDecorator(IObjectDecorator e) {
		if (this.decorators == null) {
			this.decorators = new HashSet<IObjectDecorator>();
		}
		final boolean add = this.decorators.add(e);
		if (this.isCreated() && add) {
			this.getViewer().refresh();
		}
		return add;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.onpositive.semantic.model.ui.property.editors.IMayHaveDecoratable
	 * #removeDecorator
	 * (com.onpositive.semantic.common.ui.roles.IObjectDecorator)
	 */
	public boolean removeDecorator(IObjectDecorator o) {
		if (this.decorators == null) {
			return false;
		}
		final boolean remove = this.decorators.remove(o);
		if (this.isCreated() && remove) {
			this.getViewer().refresh();
		}
		return remove;
	}

	public void setPersistValueInSettings(boolean parseBoolean) {
		this.persistValue = parseBoolean;
	}

	@SuppressWarnings("unchecked")
	public void internalLoadConfiguration(IAbstractConfiguration configuration) {
		if (this.persistValue) {
			final String[] stringArrayAttribute = configuration
					.getStringArrayAttribute("value"); //$NON-NLS-1$
			if ((stringArrayAttribute != null)
					&& (stringArrayAttribute.length > 0)) {
				final Object[] vls = new Object[stringArrayAttribute.length];
				final IIdentifiableRealm<Object> es = (IIdentifiableRealm<Object>) this
						.getRealm();
				for (int a = 0; a < stringArrayAttribute.length; a++) {
					vls[a] = es.getObject(stringArrayAttribute[a]);
				}

				this.setValue(new ArrayList<T>((Collection<? extends T>) Arrays
						.asList(vls)));
				this.commitToBinding(this.value);
				return;

			}
		}
		super.loadConfiguration(configuration);
	}

	public void internalStoreConfiguration(IAbstractConfiguration configuration) {
		if (this.persistValue) {
			if (this.value != null) {
				final IRealm<Object> realm = this.getRealm();
				if (realm instanceof IIdentifiableRealm) {
					final IIdentifiableRealm<Object> es = (IIdentifiableRealm<Object>) realm;
					if (this.value instanceof Collection) {
						final Collection<?> cm = (Collection<?>) this.value;
						final String[] ids = new String[cm.size()];
						int a = 0;
						for (final Object o : cm) {
							ids[a++] = es.getId(o);
						}
						configuration.setStringArrayAttribute("value", ids); //$NON-NLS-1$
					} else {
						if (this.value != null) {
							final String[] ids = new String[] { es
									.getId(this.value) };
							configuration.setStringArrayAttribute("value", ids); //$NON-NLS-1$
						} else {
							configuration.setStringArrayAttribute(
									"value", new String[0]); //$NON-NLS-1$
						}
					}

				}
			}
		}
		super.storeConfiguration(configuration);
	}

	public boolean isValueAsSelection() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> getCurrentValue() {
		if (this.isValueAsSelection()) {
			final StructuredSelection selection = (StructuredSelection) this.viewer
					.getSelection();
			return selection.toList();
		} else {
			throw new RuntimeException();
		}
	}

	private final ArrayList<IFilter> filters = new ArrayList<IFilter>();
	protected IOrderMaintainer adapter;

	protected IStructuredSelection selection;

	public IOrderMaintainer getOrderMaintainer() {
		return adapter;
	}

	public void addFilter(final IFilter filter) {
		this.filters.add(filter);
		if (filter instanceof IBindingDepending) {
			final IBindingDepending b = (IBindingDepending) filter;
			b.setBinding((Binding) this.getBinding());
		}
		if (this.isCreated()) {
			this.initFilter(filter);
		}
		fireFilterChange(HashDelta.createAdd(filter));
	}

	private void initFilter(final IFilter filter) {
		final IContentProvider contentProvider = this.viewer
				.getContentProvider();
		if (contentProvider instanceof IRealmContentProvider) {
			final IRealmContentProvider cp = (IRealmContentProvider) contentProvider;
			cp.addFilter(filter);
		} else {
			final ViewerFilter flt = new ViewerFilterAdapter(filter);
			this.viewer.addFilter(flt);
		}
	}

	public Collection<IFilter> getFilters() {
		return new ArrayList<IFilter>(this.filters);
	}

	public void removeFilter(IFilter filter) {
		this.filters.remove(filter);
		if (this.isCreated()) {
			final IContentProvider contentProvider = this.viewer
					.getContentProvider();
			if (contentProvider instanceof IRealmContentProvider) {
				final IRealmContentProvider cp = (IRealmContentProvider) contentProvider;
				cp.removeFilter(filter);
			} else {
				final ViewerFilter flt = new ViewerFilterAdapter(filter);
				this.viewer.removeFilter(flt);
			}
		}
		fireFilterChange(HashDelta.createRemove(filter));
	}

	public void setSelection(IStructuredSelection structuredSelection) {
		this.selection = structuredSelection;
		viewer.setSelection(selection);
	}

	public IStructuredSelection getSelection() {
		if (isCreated()) {
			return ((IStructuredSelection) viewer.getSelection());
		}
		return selection;		
	}
	
	public com.onpositive.semantic.model.api.roles.IStructuredSelection getViewerSelection() {
		return SelectionConverter.from(getSelection());
	}
	
	
	
}
