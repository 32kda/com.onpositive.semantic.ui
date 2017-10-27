package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.onpositive.commons.xml.language.ChildSetter;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.java.JavaObjectManager;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.binding.IBindingChangeListener;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.ISetDelta;
import com.onpositive.semantic.model.realm.ISimpleChangeListener;
import com.onpositive.semantic.model.realm.OrderedRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.tree.CompositeClusterizationPointProvider;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.IClusterizationPointProvider;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.tree.RealmNode;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectionListener;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.BindedAction;
import com.onpositive.semantic.model.ui.property.editors.BindingExpressionController;
import com.onpositive.semantic.model.ui.property.editors.IMayHaveCustomTooltipCreator;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.TableColumnLayout;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.TreeColumnLayout;
import com.onpositive.semantic.model.ui.viewer.IHasInnerComposite;
import com.onpositive.semantic.model.ui.viewer.structured.CustomColumnViewerTooltipSupport;
import com.onpositive.semantic.model.ui.viewer.structured.RealmContentProvider;
import com.onpositive.semantic.model.ui.viewer.structured.TreeNodeComparator;
import com.onpositive.semantic.model.ui.viewer.structured.TreeNodeContentProvider;
import com.onpositive.semantic.model.ui.viewer.structured.TreeNodeSupport;
import com.onpositive.viewer.extension.coloring.ColoredViewersManager;

public class ListEnumeratedValueSelector<T> extends
		AbstractEnumeratedValueSelector<T> implements
		IMayHaveCustomTooltipCreator<T>, IHasInnerComposite, ISelectionProvider,IListElement<Control> {

	protected Control getEventControl() {
		return this.viewer.getControl();
	}

	private boolean isAsCheckBox;
	private boolean bordered = true;
	private boolean isNoScrollBar = false;
	private boolean isTree;
	private boolean allowCellEditing = false;

	private Boolean isValueAsSelection;
	private CustomColumnViewerTooltipSupport tooltipSupport;
	private IInformationalControlContentProducer infoProducer;
	private RealmNode<Object> node;
	private CompositeClusterizationPointProvider cProvider;
	protected HashSet<IClusterizationPointProvider<?>> providers = new HashSet<IClusterizationPointProvider<?>>();
	protected IBinding selectionBinding;
	private Collection<Object> safeValue;

	protected HashSet<IOpenListener>openListeners=new HashSet<IOpenListener>();
	public void addOpenListener(IOpenListener iOpenListener) {
		openListeners.add(iOpenListener);
	}

	

	public void removeOpenListener(IOpenListener iOpenListener) {
		openListeners.remove(iOpenListener);
	}
	IOpenListener openListener = new IOpenListener() {

		public void open(OpenEvent event) {
			if (ListEnumeratedValueSelector.this.isAllowCellEditing()) {
				final StructuredSelection sel = (StructuredSelection) event
						.getSelection();
				if (sel.size() == 1) {
					final int index = 0;
					((ColumnViewer) ListEnumeratedValueSelector.this
							.getViewer()).editElement(sel.getFirstElement(),
							index);
				}
			}
			for (IOpenListener l:openListeners){
				l.open(event);
			}
		}
	};
	
	private boolean openOnDoubleClick = false;

	public final boolean isOpenOnDoubleClick() {
		return openOnDoubleClick;
	}

	public final void setOpenOnDoubleClick(boolean openOnDoubleClick) {
		this.openOnDoubleClick = openOnDoubleClick;
	}

	public Control createControl(Composite parent) {
		Control createControl = super.createControl(parent);
		viewer.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				if (openOnDoubleClick) {
					OpenAction openAction = new OpenAction(
							Action.AS_PUSH_BUTTON,
							ListEnumeratedValueSelector.this);
					openAction.run();
					openAction.dipose();
				}
			}

		});
		return createControl;
	}

	public boolean isAsTree() {
		return this.isTree;
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> getCurrentValue() {
		if (this.value == null) {
			return Collections.emptySet();
		}
		if (this.value instanceof Collection) {
			return (Collection<Object>) this.value;
		}
		if (this.value instanceof Object[]) {
			return Arrays.asList(this.value);
		}
		return Collections.singleton(this.value);
	}

	protected IRealm<?> oldRealm;
	private final IValueListener<Object> valueListener = new IValueListener<Object>() {

		public void valueChanged(Object oldValue, Object newValue) {

		}

	};

	public void setSingleValue(T object) {
		if (this.isValueAsSelection()) {
			super.setSingleValue(object);
		} else {
			final IRealm<Object> realm = this.getRealm(object);
			this.commitToBinding(realm.getContents());
			this.viewer.setInput(realm);
			this.value = object;
		}
	}

	public void addValues(Collection object) {
		if (this.isValueAsSelection()) {
			if (this.isAsCheckBox) {
				if (!this.isTree) {
					final CheckboxTableViewer tv = (CheckboxTableViewer) this.viewer;
					for (final Object o : object) {
						tv.setChecked(o, true);
					}
					this.processSelection(new StructuredSelection(tv
							.getCheckedElements()));
				}
			} else {
				super.addValues(object);
			}
		} else {
			final IRealm<Object> realm = this.getRealm(this.getBinding());
			final Collection<Object> contents = realm.getContents();
			contents.addAll(object);
			this.commitToBinding(contents);
			this.adjustInput(contents);

			this.value = contents;
		}
	}

	private void adjustInput(Collection<Object> contents) {
		if (this.oldRealm != null) {
			JavaObjectManager
					.unregisterRealm(this.oldRealm, this.valueListener);
		}

		this.oldRealm = null;
		final IRealm<Object> realm = this.getRealm(contents);
		if (isTree) {
			node.setRealm(realm);
			if (viewer != null) {
				viewer.setInput(node);
			}
		} else {
			if (viewer != null) {
				this.viewer.setInput(realm);
			}
		}
		this.oldRealm = realm;
		System.out.println("S" + realm.size());
		this.packColumnsIfNeeded();

		JavaObjectManager.registerRealm(this.oldRealm, this.valueListener);
	}

	public void removeValues(Collection<Object> object) {
		if (this.isValueAsSelection()) {
			if (this.isAsCheckBox) {
				if (!this.isTree) {
					final CheckboxTableViewer tv = (CheckboxTableViewer) this.viewer;
					for (final Object o : object) {
						tv.setChecked(o, false);
					}
					this.processSelection(new StructuredSelection(tv
							.getCheckedElements()));
				}
			} else {
				super.removeValues(object);
			}
		} else {
			final IRealm<Object> realm = this.getRealm(this.getBinding());
			final Collection<Object> contents = realm.getContents();
			contents.removeAll(object);
			this.commitToBinding(contents);
			this.adjustInput(contents);
			this.value = contents;
		}
	}

	public void editValue(T object, int pos) {
		if (!this.isValueAsSelection()) {
			final IRealm<Object> realm = this.getRealm(this.getBinding());
			if (realm != null) {
				final Collection<Object> contents = realm.getContents();
				List<Object> result = new LinkedList<Object>();

				if (contents == null) {
					result.add(value);					
				} else {
					int size = contents.size();
					if (pos >= 0 && pos < size) {
						List<Object> asList = Arrays.asList(contents
								.toArray(new Object[size]));
						List<Object> before = asList.subList(0, pos);
						List<Object> after = null;
						if (pos < size - 1) {
							after = asList.subList(pos + 1, size);
						} else {
							after = new LinkedList<Object>();
						}

						result.addAll(before);
						result.add(object);
						result.addAll(after);

					} else if (pos < 0) {

						result.add(object);
						result.addAll(contents);
					} else {
						result.addAll(contents);
						result.add(object);
					}
				}
				
				this.commitToBinding(result);
				this.adjustInput(result);
				this.value = result;
			}
		}
	}

	public void addValue(Object object) {
		if (this.isValueAsSelection()) {
			if (this.isAsCheckBox) {
				if (!this.isTree) {
					final CheckboxTableViewer tv = (CheckboxTableViewer) this.viewer;
					tv.setChecked(object, true);
					this.processSelection(new StructuredSelection(tv
							.getCheckedElements()));
				}
			} else {
				super.addValue(object);
			}
		} else {
			final IRealm<Object> realm = this.getRealm(this.getBinding());
			if (realm != null) {
				final Collection<Object> contents = realm.getContents();
				contents.add(object);
				this.commitToBinding(contents);
				this.adjustInput(contents);
				this.value = contents;
			}

		}

	}

	public void removeValue(T object) {
		if (this.isValueAsSelection()) {
			super.removeValue(object);
		} else {
			final IRealm<Object> realm = this.getRealm(this.getBinding());
			final Collection<Object> contents = realm.getContents();
			contents.remove(object);
			this.commitToBinding(contents);
			this.adjustInput(contents);

			this.value = contents;
		}
	}

	public void setValue(Collection<T> object) {
		if (this.isValueAsSelection()) {
			super.setValue(object);
		} else {
			this.commitToBinding(object);
			if (viewer != null) {
				this.viewer.setInput(this.getRealm(object));
			}
			this.value = object;
		}
	}

	protected Control getMenuControl() {
		return this.viewer.getControl();
	}

	public boolean needsLabel() {
		return false;
	}

	public void addClusterizationPointProvider(
			IClusterizationPointProvider<?> provider) {
		if (this.cProvider != null) {
			this.cProvider.add(provider);
		}
		this.providers.add(provider);
	}

	public void removeClusterizationPointProvider(
			IClusterizationPointProvider<?> provider) {
		if (this.cProvider != null) {
			this.cProvider.remove(provider);
		}
		this.providers.remove(provider);
	}

	public ListEnumeratedValueSelector(IBinding objectBinding) {
		super();
		this.getLayoutHints().setGrabVertical(true);
		this.getLayoutHints().setGrabHorizontal(true);
		this.setBinding(objectBinding);
	}

	public void dispose() {
		currentSelection.clear();
		if (this.tooltipSupport != null) {
			this.tooltipSupport.hide();
			this.tooltipSupport.deactivate();
		}
		super.dispose();
		this.disconnectTreeNode();
		owner = null;
		if (this.cProvider != null) {
			this.cProvider.dispose();
			this.cProvider = null;
		}
		if (this.oldRealm != null) {
			JavaObjectManager
					.unregisterRealm(this.oldRealm, this.valueListener);
		}
	}

	public ListEnumeratedValueSelector() {
		this.getLayoutHints().setGrabVertical(true);
		this.getLayoutHints().setGrabHorizontal(true);
	}

	protected void internalSetBinding(IBinding binding) {
		if (binding == null) {
			super.internalSetBinding(null);
			return;
		}
		if (this.isValueAsSelection()) {
			if (binding.allowsMultiValues()) {
				final boolean b = binding.getRealm() != null;
				if (this.isAsCheckBox != b) {
					if (this.isCreated()) {
						// this.isAsCheckBox = b;
						this.recreate();
					}
				}
				// this.isAsCheckBox = b;
			} else {
				final boolean b = this.isAsCheckBox;
				this.isAsCheckBox = false;
				if (b) {
					if (this.isCreated()) {
						this.recreate();
					}
				}
			}
		}
		super.internalSetBinding(binding);
	}

	private final class RepackListener implements ITreeViewerListener {
		public void treeCollapsed(TreeExpansionEvent event) {
			ListEnumeratedValueSelector.this.packColumnsIfNeeded();
		}

		public void treeExpanded(TreeExpansionEvent event) {
			ListEnumeratedValueSelector.this.packColumnsIfNeeded();
		}
	}

	private final class TreeViewerListener implements ITreeViewerListener {
		private final CheckboxTreeViewer ts;

		private TreeViewerListener(CheckboxTreeViewer ts) {
			this.ts = ts;
		}

		public void treeCollapsed(TreeExpansionEvent event) {

		}

		public void treeExpanded(TreeExpansionEvent event) {
			final ITreeNode<?> element = (ITreeNode<?>) event.getElement();
			ListEnumeratedValueSelector.this.doSet(
					ListEnumeratedValueSelector.this.safeValue, this.ts,
					element);
		}
	}

	static class Entry {
		ITreeNode<?> node;
		HashMap<ITreeNode<?>, Entry> childs = new HashMap<ITreeNode<?>, Entry>();

		public Entry(ITreeNode<?> node) {
			super();
			this.node = node;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.node == null) ? 0 : this.node.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final Entry other = (Entry) obj;
			if (this.node == null) {
				if (other.node != null) {
					return false;
				}
			} else if (!this.node.equals(other.node)) {
				return false;
			}
			return true;
		}

		boolean isFullySelected() {
			return this.node.size() == this.childs.size();
		}

		Entry add(ITreeNode<?> ll) {
			if (ll == null) {
				return null;
			}
			final Entry e = new Entry(ll);
			if (this.childs.containsKey(ll)) {
				return this.childs.get(ll);
			}
			if (ll.getParentNode() == this.node) {
				this.childs.put(ll, e);
				return e;
			}
			final Entry add = this.add(ll.getParentNode());
			if (add == null) {
				return null;
			}
			add.childs.put(ll, e);
			return e;
		}

	}

	public void check(Object[] elements, ITreeNode<?> node) {
		final Entry root = new Entry(node);
		for (final Object o : elements) {
			final ITreeNode<?> ll = (ITreeNode<?>) o;
			root.add(ll);
		}
		this.open(root);
	}

	private void open(Entry r) {
		if (r.isFullySelected()) {
			((CheckboxTreeViewer) this.viewer).setChecked(r.node, true);
		}
		boolean hasFullySelected = false;
		for (final Entry o : r.childs.values()) {
			if (o.isFullySelected()) {
				((CheckboxTreeViewer) this.viewer).setChecked(o.node, true);
				hasFullySelected = true;
			} else {
				((CheckboxTreeViewer) this.viewer).setGrayChecked(o.node, true);
			}
		}
		if (hasFullySelected) {
			((CheckboxTreeViewer) this.viewer).setGrayChecked(r.node, true);
		}
	}

	// 89137572288
	@SuppressWarnings("unchecked")
	public void installValue(T value) {

		this.value = value;
		if (this.viewer != null) {
			final IContentProvider contentProvider = this.viewer
					.getContentProvider();
			if (contentProvider instanceof TreeNodeContentProvider) {
				final TreeNodeContentProvider ss = (TreeNodeContentProvider) contentProvider;
				value = (T) ss.mapValue(value);
				this.initSafeValue((Collection<Object>) value);
			}
			if (contentProvider instanceof RealmLazyTreeContentProvider) {
				final RealmLazyTreeContentProvider tsd = (RealmLazyTreeContentProvider) contentProvider;
				value = (T) tsd.mapValue(value);
				this.initSafeValue((Collection<Object>) value);

			}
			;
			if (!this.isValueAsSelection()) {
				this.packColumnsIfNeeded();
				return;
			}
			if (this.isAsCheckBox) {
				if (this.isTree) {
					final CheckboxTreeViewer ts = (CheckboxTreeViewer) this.viewer;
					if (this.listener2 != null) {
						ts.removeTreeListener(this.listener2);
					}
					this.listener2 = new TreeViewerListener(ts);
					ts.addTreeListener(this.listener2);
					this.doSet(value, ts, (ITreeNode<?>) this.viewer.getInput());
				} else {
					final CheckboxTableViewer ts = (CheckboxTableViewer) this.viewer;
					if (value instanceof Collection) {
						ts.setCheckedElements(((Collection<?>) value).toArray());
					} else {
						if (value == null) {
							ts.setAllChecked(false);
						} else {
							ts.setCheckedElements(new Object[] { value });
						}
					}
				}
			} else {
				try {
					super.installValue(value);
				} catch (SWTException e) {
					// TODO: handle exception
				}
			}
		}
		this.packColumnsIfNeeded();
	}

	protected void packColumnsIfNeeded() {
		if (!this.isNoScrollBar) {
			Display.getCurrent().asyncExec(new Runnable() {

				public void run() {
					if (ListEnumeratedValueSelector.this.isCreated()) {
						if (ListEnumeratedValueSelector.this.isTree) {
							updateSize((TreeViewer) getViewer());
						} else {
							updateSize((TableViewer) getViewer());

						}
					}
				}
			});

		}
	}

	private void initSafeValue(Collection<Object> value) {
		this.safeValue = value;
	}

	@SuppressWarnings("unchecked")
	private void doSet(Object value, final CheckboxTreeViewer ts,
			ITreeNode<?> node) {
		if (value instanceof Collection) {
			this.check(((Collection) value).toArray(), node);
		} else {
			ts.setCheckedElements(new Object[] { value });
		}
	}

	protected void handleChange(Object extraData) {
		super.handleChange(extraData);
		if (this.isCreated()) {
			if (this.isNoScrollBar) {
				this.packColumnsIfNeeded();
			}
		}
	}

	protected void initContentProvider() {
		if (this.uprovider != null) {
			super.initContentProvider();
		} else {
			if (this.isTree) {
				this.provider = TreeNodeSupport.configure(
						(AbstractTreeViewer) this.viewer, this);
				if (this.provider instanceof IRealmContentProvider) {
					final IRealmContentProvider cp = (IRealmContentProvider) this.provider;
					this.registerProvider(cp);
				}
			} else {
				if (this.isAsCheckBox) {
					this.provider = new RealmContentProvider();
					this.viewer.setContentProvider(this.provider);
				} else {
					initContentProviderFromParent();
				}
			}
		}
	}

	protected void initContentProviderFromParent() {
		super.initContentProvider();
	}

	protected StructuredViewer createViewer(Composite parent) {
		if (owner == null) {
			this.owner = new Composite(parent, SWT.NONE);
		}
		if (this.isAsCheckBox) {
			if (this.isTree) {
				final CheckboxTreeViewer newCheckList = new CheckboxTreeViewer(
						this.owner, SWT.CHECK | SWT.V_SCROLL
								| SWT.HIDE_SELECTION

								| (this.isBordered() ? SWT.BORDER : SWT.NONE));
				newCheckList.setUseHashlookup(true);
				this.configureViewer(newCheckList);
				if (!this.isNoScrollBar) {
					final ITreeViewerListener repackListener = new RepackListener();
					newCheckList.addTreeListener(repackListener);
				}
				return newCheckList;
			} else {
				final CheckboxTableViewer newCheckList = CheckboxTableViewer
						.newCheckList(this.owner, SWT.CHECK | SWT.V_SCROLL
								| SWT.HIDE_SELECTION
								| (this.isBordered() ? SWT.BORDER : SWT.NONE));
				newCheckList.setUseHashlookup(true);
				this.configureViewer(newCheckList);
				return newCheckList;
			}
		}
		if (this.isTree) {
			final TreeViewer tableViewer = new TreeViewer(this.owner,
					SWT.V_SCROLL | (uprovider == null ? SWT.VIRTUAL : SWT.NONE)
							| this.getSelectionStyle()
							| (this.isBordered() ? SWT.BORDER : SWT.NONE)) {

				public void replace(Object parentElementOrTreePath, int index,
						Object element) {

					Widget[] itemsToDisassociate;

					itemsToDisassociate = this.internalFindItems(element);
					final Tree tree = this.getTree();
					if (this.internalIsInputOrEmptyPath(parentElementOrTreePath)) {

						if (index < tree.getItemCount()) {
							final TreeItem item = tree.getItem(index);
							// disassociate any different item that represents
							// the
							// same element under the same parent (the tree)
							for (int i = 0; i < itemsToDisassociate.length; i++) {
								if (itemsToDisassociate[i] instanceof TreeItem) {
									final TreeItem itemToDisassociate = (TreeItem) itemsToDisassociate[i];
									if ((itemToDisassociate != item)
											&& (itemToDisassociate
													.getParentItem() == null)) {
										final int indexToDisassociate = tree
												.indexOf(itemToDisassociate);
										this.disassociate(itemToDisassociate);
										tree.clear(indexToDisassociate, true);
									}
								}
							}
							final Object oldData = item.getData();
							this.updateItem(item, element);
							if (!this.equals(oldData, element)) {
								item.clearAll(true);
							}
						}
					} else {
						final Widget[] parentItems = this
								.internalFindItems(parentElementOrTreePath);
						for (int i = 0; i < parentItems.length; i++) {
							final TreeItem parentItem = (TreeItem) parentItems[i];
							if (index < parentItem.getItemCount()) {
								final TreeItem item = parentItem.getItem(index);
								for (int j = 0; j < itemsToDisassociate.length; j++) {
									if (itemsToDisassociate[j] instanceof TreeItem) {
										final TreeItem itemToDisassociate = (TreeItem) itemsToDisassociate[j];
										if ((itemToDisassociate != item)
												&& (itemToDisassociate
														.getParentItem() == parentItem)) {
											final int indexToDisaccociate = parentItem
													.indexOf(itemToDisassociate);
											this.disassociate(itemToDisassociate);
											parentItem.clear(
													indexToDisaccociate, true);
										}
									}
								}
								final Object oldData = item.getData();
								this.updateItem(item, element);
								if (!this.equals(oldData, element)) {
									item.clearAll(true);
								}
							}
						}
					}
				}

			};
			final ITreeViewerListener repackListener = new RepackListener();
			if (!this.isNoScrollBar) {
				tableViewer.addTreeListener(repackListener);
				this.configureViewer(tableViewer);
			}
			tableViewer.setUseHashlookup(true);
			return tableViewer;
		} else {
			final TableViewer tableViewer = new TableViewer(this.owner,
					SWT.V_SCROLL | SWT.VIRTUAL | this.getSelectionStyle()
							| (this.isBordered() ? SWT.BORDER : SWT.NONE));

			tableViewer.setUseHashlookup(true);
			this.configureViewer(tableViewer);
			return tableViewer;
		}

	}

	protected int getSelectionStyle() {
		final IBinding binding = this.getBinding();
		if ((binding != null) && (!binding.allowsMultiValues())) {
			return SWT.SINGLE;
		}
		return SWT.MULTI;
	}

	public ToolTip getTooltip() {
		return this.tooltipSupport;
	}

	protected Widget columnWidget;

	protected void configureViewer(final TableViewer newCheckList) {
		ColoredViewersManager.install(newCheckList);
		newCheckList.addOpenListener(this.openListener);
		this.tooltipSupport = new CustomColumnViewerTooltipSupport(
				newCheckList, this.infoProducer);
		final Composite parent2 = newCheckList.getTable().getParent();
		if (this.isNoScrollBar) {
			final TableColumnLayout layout = new TableColumnLayout();
			parent2.setLayout(layout);
			this.columnWidget = new TableColumn(newCheckList.getTable(),
					SWT.NONE);
			layout.setColumnData(this.columnWidget, new ColumnWeightData(1));
			;
		} else {
			final FillLayout ls = new FillLayout();
			ls.marginHeight = 1;
			ls.marginWidth = 1;
			parent2.setLayout(ls);
			this.columnWidget = new TableColumn(newCheckList.getTable(),
					SWT.NONE);
			newCheckList.getTable().addListener(SWT.Resize, new Listener() {

				boolean isInside = false;

				public void handleEvent(Event event) {
					if (!isInside) {
						try {
							isInside = true;
							updateSize(newCheckList);
						} finally {
							isInside = false;
						}
					}
				}

			});
			updateSize(newCheckList);
		}
	}

	protected void updateSize(final TableViewer newCheckList) {
		TableColumn tableColumn = (TableColumn) columnWidget;
		tableColumn.pack();
		int width = tableColumn.getWidth();
		int width2 = newCheckList.getTable().getBounds().width;
		if (width < width2) {
			((TableColumn) columnWidget).setWidth(width2 - 35);
		}
	}

	private boolean updatingSize;

	private void updateSize(final TreeViewer newCheckList) {
		if (updatingSize) {
			return;
		}
		try {

			updatingSize = true;
			TreeColumn treeColumn = (TreeColumn) columnWidget;
			if (newCheckList.getTree().getItemCount() > 0) {
				try {
					treeColumn.pack();
				} catch (NullPointerException e) {

				}
			}
			int width = treeColumn.getWidth();
			int width2 = newCheckList.getTree().getBounds().width;
			if (width < width2) {
				((TreeColumn) columnWidget).setWidth(width2 - 35);

			}
		} finally {
			updatingSize = false;
		}
	}

	@SuppressWarnings("unchecked")
	protected IRealm<Object> getRealm(IBinding binding2) {
		if (binding2 == null) {
			return (IRealm<Object>) this.oldRealm;
		}
		if (this.isValueAsSelection()) {
			return super.getRealm(binding2);
		}

		final Object value2 = binding2.getValue();
		return this.getRealm(value2);
	}

	public IRealm getRealm() {
		IBinding binding = getBinding();
		if (this.isValueAsSelection() || binding == null) {
			return super.getRealm();
		} else {
			return (IRealm<T>) getRealm(binding);
		}
	}

	@SuppressWarnings("unchecked")
	protected IRealm<Object> getRealm(Object value2) {

		if (this.oldRealm != null) {
			return (IRealm<Object>) this.oldRealm;
		}

		if (value2 instanceof IRealm<?>) {
			return (IRealm<Object>) value2;
		}
		if (value2 instanceof Collection) {
			final Collection<?> es = (Collection<?>) value2;

			if (value2 instanceof List) {
				return new OrderedRealm<Object>((Collection<Object>) es);
			}

			return new Realm<Object>((Collection<Object>) es);
		}
		final Realm<Object> realm = new Realm<Object>();
		if (value2 != null) {
			realm.add(value2);
		}
		this.oldRealm = realm;
		return realm;
	}

	@SuppressWarnings("unchecked")
	protected void initializeContent(IBinding binding2) {

		if (binding2.getRealm() == null) {
			this.hasRealm = false;
		} else {
			this.hasRealm = true;
		}
		final IRealm<?> l = this.oldRealm;
		this.oldRealm = null;
		IRealm<Object> realm = this.getRealm(binding2);
		Object value2 = null;
		if (binding2 != null) {
			value2 = binding2.getValue();
			if (!this.isValueAsSelection()) {
				{
					if (l != null) {
						JavaObjectManager
								.unregisterRealm(l, this.valueListener);
					}
					JavaObjectManager.registerRealm(realm, this.valueListener);
					this.oldRealm = realm;
				}
			}
		}
		if (this.selectionBinding != null) {
			this.selectionBinding.setValue(null, null);
		}
		if (!this.isTree || this.uprovider != null) {
			super.initializeContent(binding2);
			return;
		}

		final CompositeClusterizationPointProvider clusterizationPointProvider = new CompositeClusterizationPointProvider();
		for (final IClusterizationPointProvider<?> w : this.providers) {
			clusterizationPointProvider.add(w);
		}
		if (this.cProvider != null) {
			this.cProvider.dispose();
		}
		this.cProvider = clusterizationPointProvider;
		this.cProvider
				.addChangeListener(new ISimpleChangeListener<IClusterizationPointProvider<Object>>() {

					public void changed(
							IClusterizationPointProvider<Object> provider,
							Object extraData) {
						ListEnumeratedValueSelector.this
								.initializeContent(ListEnumeratedValueSelector.this
										.getBinding());
						if (!ListEnumeratedValueSelector.this.isNoScrollBar()) {
							ListEnumeratedValueSelector.this
									.packColumnsIfNeeded();
						}
					}

				});
		this.node = new RealmNode<Object>(clusterizationPointProvider);
		if (realm != null) {
			realm = UIRealm.toUI(realm);
			this.node.setRealm(realm);
			if (realm != null) {
				if (realm.isOrdered()) {
					this.viewer.setSorter(null);
				} else {
					if (!this.isTree) {
						this.viewer.setSorter(new ViewerSorter());
					} else {
						this.viewer.setSorter(new TreeNodeComparator());
					}
				}
				this.viewer.setInput(this.node);
			}
		} else {
			this.viewer.setSorter(new ViewerSorter());
		}
		if (adapter != null) {
			adapter.setRealm(realm);
			adapter.init(this.getBinding().getObject(), value);
		}
		this.installValue((T) value2);

	}

	protected void initSelection() {
		if (!this.isTree) {
			super.initSelection();
		} else if (this.value != null) {
			final IContentProvider contentProvider = this.viewer
					.getContentProvider();
			if (contentProvider instanceof TreeNodeContentProvider) {
				final TreeNodeContentProvider ss = (TreeNodeContentProvider) contentProvider;
				this.value = ss.mapValue(this.value);
			}
			if (contentProvider instanceof RealmLazyTreeContentProvider) {
				final RealmLazyTreeContentProvider ss = (RealmLazyTreeContentProvider) contentProvider;
				this.value = ss.mapValue(this.value);
			}
			if (this.value instanceof List) {
				this.viewer.setSelection(new StructuredSelection(
						(List) this.value));
			} else {
				this.viewer.setSelection(new StructuredSelection(this.value));
			}
		}
	}

	protected void configureViewer(final TreeViewer newCheckList) {
		ColoredViewersManager.install(newCheckList);
		newCheckList.addOpenListener(this.openListener);
		this.tooltipSupport = new CustomColumnViewerTooltipSupport(
				newCheckList, this.infoProducer);
		final Composite parent2 = newCheckList.getTree().getParent();
		if (this.isNoScrollBar) {
			final TreeColumnLayout layout = new TreeColumnLayout();
			parent2.setLayout(layout);
			this.columnWidget = new TreeColumn(newCheckList.getTree(), SWT.NONE);
			layout.setColumnData(this.columnWidget, new ColumnWeightData(1));
		} else {
			final FillLayout layout = new FillLayout();
			layout.marginHeight = 1;
			layout.marginWidth = 1;
			parent2.setLayout(layout);
			this.columnWidget = new TreeColumn(newCheckList.getTree(), SWT.NONE);
			newCheckList.getTree().addListener(SWT.Resize, new Listener() {

				public void handleEvent(Event event) {
					updateSize(newCheckList);
				}

			});
			updateSize(newCheckList);
		}
	}

	@SuppressWarnings("unchecked")
	protected void processValueChange(ISetDelta<?> valueElements) {
		if (this.isValueAsSelection()) {
			super.processValueChange(valueElements);
		} else {
			this.viewer.getControl().setRedraw(false);

			final Object value3 = this.getBinding().getValue();
			HashSet<Object> elements = null;
			if (value3 instanceof Collection<?>) {
				final Collection<? extends Object> value2 = (Collection<? extends Object>) value3;
				elements = new HashSet<Object>(value2);
			} else {
				if (value3 != null) {
					elements = new HashSet<Object>(
							Collections.singleton(value3));
				} else {
					elements = new HashSet<Object>();
				}
			}
			elements.addAll(valueElements.getAddedElements());
			elements.removeAll(valueElements.getRemovedElements());
			final ISelection selection = this.viewer.getSelection();
			JavaObjectManager
					.unregisterRealm(this.oldRealm, this.valueListener);
			final Realm<Object> input = new Realm<Object>(elements);
			JavaObjectManager.registerRealm(input, this.valueListener);
			this.oldRealm = input;
			if (adapter != null) {
				adapter.setRealm(input);
				adapter.init(this.getBinding().getObject(), value3);
			}
			if (isTree && node != null) {
				node.setRealm(input);
			} else {
				this.viewer.setInput(input);
			}
			this.viewer.setSelection(selection);
			this.handleChange(null);
			this.packColumnsIfNeeded();
			this.viewer.getControl().setRedraw(true);
		}
	}

	HashSet<Object> currentSelection = new HashSet<Object>();

	protected void processSelection(StructuredSelection ss) {
		if (isTree) {
			ss = convertSelection(ss);
		}
		for (ISelectionChangedListener l : listeners) {
			l.selectionChanged(new SelectionChangedEvent(this, ss));
		}
		// HashSet<Object> ma=new HashSet<Object>(ss.toList());
		// if (ma.equals(currentSelection)){
		// currentSelection=ma;
		// return;
		// }
		// currentSelection=ma;
		if (this.viewer != null) {
			if (this.isValueAsSelection()) {
				super.processSelection(ss);
			} else {
				if (this.shouldIgnoreChanges()) {
					return;
				}
				final int size = ss.size();
				if (size == 0) {
					this.commitSelection(null);
				} else if (size == 1) {
					Object firstElement = ss.getFirstElement();
					this.commitSelection(firstElement);
				} else {
					this.commitSelection(ss.toList());
				}
				return;
			}
		}
		super.processSelection(ss);
	}

	private StructuredSelection convertSelection(StructuredSelection ss) {
		final ArrayList<Object> result = new ArrayList<Object>();
		for (final Object o : ss.toList()) {
			if (o instanceof ITreeNode<?>) {
				final ITreeNode<?> m = (ITreeNode<?>) o;
				final Object element = m.getElement();
				if ((element != null)
						&& (element instanceof IClusterizationPoint)) {
					IClusterizationPoint<?> pa = (IClusterizationPoint<?>) element;
					Object primaryValue = pa.getPrimaryValue();
					if (primaryValue != null) {
						result.add(primaryValue);
					}
				} else {
					result.add(element);
				}
			} else {
				result.add(o);
			}
		}
		StructuredSelection ss2 = new StructuredSelection(result);
		return ss2;
	}

	protected void initListeners() {
		if (this.isAsCheckBox) {
			if (this.isTree) {
				final CheckboxTreeViewer ts = (CheckboxTreeViewer) this.viewer;
				ts.addCheckStateListener(new ICheckStateListener() {
					public void checkStateChanged(CheckStateChangedEvent event) {
						if (ListEnumeratedValueSelector.this
								.shouldIgnoreChanges()) {
							return;
						}
						if (event.getChecked()) {
							final Object element = event.getElement();
							ListEnumeratedValueSelector.this
									.addElementToValue(element);
						} else {
							final Object element = event.getElement();
							ListEnumeratedValueSelector.this
									.removeElementFromValue(element);
						}
					}
				});
			} else {
				final CheckboxTableViewer ts = (CheckboxTableViewer) this.viewer;
				ts.addCheckStateListener(new ICheckStateListener() {

					public void checkStateChanged(CheckStateChangedEvent event) {
						if (ListEnumeratedValueSelector.this
								.shouldIgnoreChanges()) {
							return;
						}
						ListEnumeratedValueSelector.this
								.processSelection(new StructuredSelection(ts
										.getCheckedElements()));
					}
				});
			}
		} else {
			super.initListeners();
		}
	}

	boolean isChangingCheck = false;
	private TreeViewerListener listener2;
	private final IBindingChangeListener<?> selectionListener = new IBindingChangeListener<Object>() {

		public void changed() {

		}

		public void enablementChanged(boolean isEnabled) {

		}

		public void valueChanged(ISetDelta<Object> valueElements) {
			ListEnumeratedValueSelector.super.processValueChange(valueElements);
		}

	};
	private boolean hasRealm;
	private Composite owner;

	protected void addElementToValue(Object element) {
		if (this.isChangingCheck) {
			return;
		}
		try {
			this.viewer.getControl().setRedraw(false);
			this.isChangingCheck = true;

			final ITreeNode<?> node = (ITreeNode<?>) element;
			this.addChilds(node);
			if (node.hasChildren()) {
				final CheckboxTreeViewer checkboxTreeViewer = (CheckboxTreeViewer) this.viewer;
				if (checkboxTreeViewer.getExpandedState(node)) {
					for (final ITreeNode<?> q : node.getChildren()) {
						checkboxTreeViewer.setChecked(q, true);
					}
				}
			}
			this.refreshCheck(node);
			this.refreshCheck(node.getParentNode());
			this.processSelection(new StructuredSelection(this.safeValue));
			this.viewer.getControl().setRedraw(true);
			this.viewer.getControl().redraw();
		} finally {
			this.isChangingCheck = false;
		}
	}

	private void removeChilds(ITreeNode<?> node2) {
		this.safeValue.remove(node2);
		if (node2.hasChildren()) {
			for (final ITreeNode<?> n : node2.getChildren()) {
				this.removeChilds(n);
			}
		}
	}

	private void addChilds(ITreeNode<?> node2) {
		this.safeValue.add(node2);
		if (node2.hasChildren()) {
			for (final ITreeNode<?> n : node2.getChildren()) {
				this.addChilds(n);
			}
		}
	}

	protected void removeElementFromValue(Object element) {
		if (this.isChangingCheck) {
			return;
		}
		try {
			this.viewer.getControl().setRedraw(false);
			this.isChangingCheck = true;
			final ITreeNode<?> node = (ITreeNode<?>) element;
			if (node.hasChildren()) {
				final CheckboxTreeViewer checkboxTreeViewer = (CheckboxTreeViewer) this.viewer;
				if (checkboxTreeViewer.getExpandedState(node)) {
					for (final ITreeNode<?> q : node.getChildren()) {
						checkboxTreeViewer.setChecked(q, false);
					}
				}
			}
			this.removeChilds(node);
			this.refreshCheck(node);

			if (safeValue instanceof List) {
				this.processSelection(new StructuredSelection(
						(List) this.safeValue));
			} else {
				this.processSelection(new StructuredSelection(this.safeValue));
			}
			this.viewer.getControl().setRedraw(true);
			this.viewer.getControl().redraw();
		} finally {
			this.isChangingCheck = false;
		}
	}

	private void refreshCheck(ITreeNode<?> node) {
		if (node.getParentNode() != null) {
			refreshCheck(node.getParentNode());
		}
		if (!node.hasChildren()) {
			return;
		}
		int count = 0;
		for (final ITreeNode<?> q : node.getChildren()) {
			if (this.safeValue.contains(q)) {
				count++;
			}
		}
		if (count == 0) {
			((CheckboxTreeViewer) this.viewer).setChecked(node, false);
		} else {
			((CheckboxTreeViewer) this.viewer).setChecked(node, true);
			if (count != node.size()) {
				((CheckboxTreeViewer) this.viewer).setGrayed(node, true);
			} else {
				((CheckboxTreeViewer) this.viewer).setGrayed(node, false);
			}
		}

	}

	public boolean asCheckBox() {
		return this.isAsCheckBox;
	}

	
	public boolean isBordered() {
		return this.bordered && !this.parentDrawsBorder();
	}

	public void setBordered(boolean bordered) {
		this.bordered = bordered;
	}

	public void setAsCheckBox(boolean isAsCheckBox) {
		this.isAsCheckBox = isAsCheckBox;
		if (this.isCreated()) {
			this.recreate();
		}
	}
	
	public boolean isAsCheckBox(){
		return isAsCheckBox;
	}

	public boolean isNoScrollBar() {
		return this.isNoScrollBar;
	}

	public void setNoScrollBar(boolean isNoScrollBar) {
		this.isNoScrollBar = isNoScrollBar;
		if (this.isCreated()) {
			if (isNoScrollBar) {
				final TableColumnLayout layout = new TableColumnLayout();
				layout.setColumnData(((TableViewer) this.viewer).getTable()
						.getColumn(0), new ColumnWeightData(1));
			} else {
				this.viewer.getControl().getParent()
						.setLayout(new FillLayout());
			}
		}
	}

	/**
	 * @see com.onpositive.semantic.model.ui.property.editors.IMayHaveCustomTooltipCreator#setTooltipInformationControlCreator(com.onpositive.semantic.common.ui.roles.IInformationalControlContentProducer)
	 */
	public void setTooltipInformationControlCreator(
			IInformationalControlContentProducer informationalControlContentProducer) {
		this.infoProducer = informationalControlContentProducer;
	}

	public void setAsTree(boolean selection) {
		final boolean old = this.isTree;
		this.isTree = selection;
		if (old != this.isTree) {
			if (this.isCreated()) {
				StructuredSelection sel = (StructuredSelection) this.viewer
						.getSelection();
				this.setIgnoreChanges(true);
				this.recreate();
				this.setIgnoreChanges(false);
				IContentProvider contentProvider = this.getContentProvider();
				if (contentProvider instanceof IRealmContentProvider) {
					IRealmContentProvider cp = (IRealmContentProvider) contentProvider;
					cp.restoreSelection(sel);
				}
			}
		}
	}
	
	public void setClusterizationPointProviders(
			IClusterizationPointProvider... providers){
		
		setClusterizationPointProviders("",providers);
	}

	public void setClusterizationPointProviders(String columnId,
			IClusterizationPointProvider... providers) {
		if (providers == null) {
			return;
		}

		lockColumn(columnId);

		this.disconnectTreeNode();
		final HashSet<IClusterizationPointProvider> hashSet = new HashSet<IClusterizationPointProvider>(
				Arrays.asList(providers));
		this.providers = (HashSet)hashSet;
		if (this.isCreated()) {
			if (this.isTree) {
				// this.viewer.setSelection(new StructuredSelection());
				this.initializeContent(this.getBinding());

			}
		}

	}

	protected void lockColumn(String columnId) {

	}

	private void disconnectTreeNode() {
		if (this.node != null) {
			this.node.setRealm(null);
		}
		this.node = null;
	}

	public boolean isValueAsSelection() {
		if (this.isValueAsSelection != null) {
			return this.isValueAsSelection;
		}
		return this.hasRealm;
	}

	@HandlesAttributeDirectly("useSelectionAsValue")
	public void setValueAsSelection(boolean isValueAsSelection) {
		this.isValueAsSelection = isValueAsSelection;
		if (!isValueAsSelection) {
			this.isAsCheckBox = false;
		}
		if (this.isCreated()) {
			this.recreate();
		}
	}

	public void setSelectionBinding(IBinding binding) {
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
		this.selectionBinding.addBindingChangeListener(this.selectionListener);
	}
	
	
	
	private final BindingSelectionExpressionController controller=new BindingSelectionExpressionController(this,this);
	
	@HandlesAttributeIndirectly( "bindSelectionTo")
	public BindingSelectionExpressionController getBindingSelectionExpressionController(){
		return controller;
	}

	protected void commitSelection(Object newValue) {
		if (this.selectionBinding != null) {
			System.out.println("Commiting selection:" + newValue);
			this.selectionBinding.setValue(newValue, this.selectionListener);
		}
	}

	public IBinding getSelectionBinding() {
		return this.selectionBinding;
	}

	public Composite getComposite() {
		return this.owner;
	}

	public boolean isAllowCellEditing() {
		return this.allowCellEditing;
	}

	public void setAllowCellEditing(boolean allowCellEditing) {
		this.allowCellEditing = allowCellEditing;
	}

	public int getColumnCount() {
		return 1;
	}

	public void editorValueApplied(Object element, Object value) {
		final IBinding binding2 = this.getBinding();
		binding2.notifyPossibleChange();
		if (!(this.provider instanceof IRealmContentProvider)) {
			viewer.update(element, null);
		}
	}

	public void setIgnoreChanges(boolean b) {
		super.setIgnoreChanges(b);
	}

	@ChildSetter( value = "contentProvider", needCasting=false )
	public void setContentProvider(IContentProvider provider) {
		this.provider = provider;
		this.uprovider = provider;
		if (isCreated()) {
			recreate();
		}
	}

	public IContentProvider getContentProvider() {
		return this.provider;
	}

	protected HashSet<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	public void setSelection(IStructuredSelection structuredSelection) {
		selection = structuredSelection;
		if (isCreated() || viewer != null && !viewer.getControl().isDisposed()) {
			if (isAsCheckBox) {
				if (isTree) {
					CheckboxTreeViewer tv = (CheckboxTreeViewer) viewer;
					tv.setCheckedElements(structuredSelection.toArray());
				} else {
					CheckboxTableViewer tv = (CheckboxTableViewer) viewer;
					tv.setCheckedElements(structuredSelection.toArray());
				}
			} else
				super.setSelection(structuredSelection);
		}
	}

	public IStructuredSelection getSelection() {
		if (isCreated()) {
			return ((IStructuredSelection) viewer.getSelection());
		}
		return selection;
	}

	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			setSelection((IStructuredSelection) selection);
		}
	}

	public Control getMainControl() {
		return viewer.getControl();
	}

	public IContributionItem createAddFromContributionItem() {
		return createAddFromRealm() ;
		//return new SWTContributionItem(createAddFromRealm());
	}
	
	public IContributionItem createOpenContributionItem() {
		return createOpenAction();
		//return new SWTContributionItem(createOpenAction());
	}
	
	public IContributionItem createAddNewContributionItem() {
		return createAddNewAction();
		//return new SWTContributionItem(createAddNewAction());
	}
	
	public IContributionItem createRemoveSelectedContributionItem() {
		return createRemoveSelectedAction();
		//return new SWTContributionItem(createRemoveSelectedAction());
//		Object ra = createRemoveSelectedAction() ;
//		Class<?> c = ra.getClass() ;
//		Class<?> c1 = c.getSuperclass() ;
//		Class<?> c2 = c1.getSuperclass() ;
//		Class<?> c3 = c2.getSuperclass() ;
//		return (BindedAction)ra ;
	}
	
	public void removeOpenListener(
			com.onpositive.semantic.model.ui.generic.widgets.handlers.IOpenListener iOpenListener) {
		
	}
	
	public void addOpenListener(
			com.onpositive.semantic.model.ui.generic.widgets.handlers.IOpenListener iOpenListener) {
		
	}
	
	public void addSelectionListener(ISelectionListener selectionHandler) {
		
	}
	
	public void removeSelectionListener(ISelectionListener selectionHandler) {
		
	}

	public void setSelection(
			com.onpositive.semantic.model.api.roles.IStructuredSelection structuredSelection) {
		setSelection(SelectionConverter.to(structuredSelection));
	}



	
}