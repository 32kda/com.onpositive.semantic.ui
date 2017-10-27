package com.onpositive.semantic.ui.realm.viewer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.xml.language.Activator;
import com.onpositive.semantic.common.ui.roles.DisplayableCreator;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.realm.IColumnDefinition;
import com.onpositive.semantic.realm.registries.ColumnConfiguration;
import com.onpositive.semantic.realm.registries.IViewerConfigurationListener;
import com.onpositive.semantic.realm.registries.ViewerConfiguration;
import com.onpositive.semantic.realm.registries.ViewerTabConfiguration;
import com.onpositive.semantic.ui.realm.fastviewer.CustomViewerControl;
import com.onpositive.semantic.ui.realm.fastviewer.FastTreeColumn;
import com.onpositive.semantic.ui.realm.fastviewer.SavablePart;

public class UniversalTableViewer {

	private static final String TAB_SELECTION = "tabSelection";
	private static final String CONFIGURE = "configure";
	private static final String REMOVE = "remove";
	private static final String ADD = "add";
	private ViewerConfiguration configuration;
	private CTabFolder tabOwner;
	private List<ViewerTabConfiguration> tabs;

	private final class ItemReorderHandler implements Listener {
		boolean drag = false;
		boolean exitDrag = false;
		CTabItem dragItem;
		CTabItem targetItem;

		public void handleEvent(Event e) {
			Point p = new Point(e.x, e.y);
			if (e.type == SWT.DragDetect) {
				p = tabOwner.toControl(tabOwner.getDisplay()
						.getCursorLocation()); // see
				// bug
				// 43251
			}
			switch (e.type) {
			case SWT.Paint: {
				if (!drag)
					return;
				if (targetItem != null) {
					Rectangle bounds = targetItem.getBounds();
					e.gc.setLineWidth(2);
					e.gc.drawFocus(bounds.x, bounds.y, bounds.width,
							bounds.height);
				}
				break;
			}
			case SWT.DragDetect: {
				CTabItem item = tabOwner.getItem(p);
				if (item == null)
					return;
				drag = true;
				exitDrag = false;
				dragItem = item;
				break;
			}
			case SWT.MouseEnter:
				if (exitDrag) {
					exitDrag = false;
					drag = e.button != 0;
				}
				break;
			case SWT.MouseExit:
				if (drag) {
					targetItem = null;
					tabOwner.setInsertMark(null, false);
					exitDrag = true;
					tabOwner.redraw();
					drag = false;
				}
				break;
			case SWT.MouseUp: {
				if (!drag)
					return;
				tabOwner.setInsertMark(null, false);
				CTabItem item = tabOwner.getItem(new Point(p.x, tabOwner
						.getSize().y - 1));
				if (item != null) {
					int de = tabOwner.indexOf(item);
					int de0 = tabOwner.indexOf(dragItem);
					boolean after = de0 < de;// + (rect.width / 4);
					int index = tabOwner.indexOf(item);
					int indexOf = Arrays.asList(tabOwner.getItems()).indexOf(
							dragItem);
					index = after ? index + 1 : index - 1;
					index = Math.max(0, index);
					configuration.moveTab(indexOf, index);
					// moveTab(index, indexOf);
				}
				tabOwner.redraw();
				drag = false;
				targetItem = null;
				exitDrag = false;
				dragItem = null;
				break;
			}
			case SWT.MouseMove: {
				if (!drag)
					return;
				CTabItem item = tabOwner.getItem(new Point(p.x, tabOwner
						.getSize().y - 2));
				if (item == null) {
					tabOwner.setInsertMark(null, false);
					if (targetItem != null) {
						targetItem = null;
						tabOwner.redraw();
					}
					return;
				}
				if (item != null && item != targetItem) {
					targetItem = item;
					Rectangle rect = item.getBounds();
					boolean after = p.x > rect.x + rect.width / 2;
					tabOwner.setInsertMark(item, after);
					tabOwner.redraw();
				}
				break;
			}
			}
		}
	}

	public static class ViewerTabState {

	}

	protected ViewerTabConfiguration activeTab;
	protected HashMap<ViewerTabConfiguration, ViewerTabState> tabActivations = new HashMap<ViewerTabConfiguration, ViewerTabState>();

	static ImageDescriptor configureImage = AbstractUIPlugin
			.imageDescriptorFromPlugin("com.onpositive.semantic.ui.realm",
					"/icons/threadgroup_obj.gif");

	static ImageDescriptor addImage = AbstractUIPlugin
			.imageDescriptorFromPlugin("com.onpositive.semantic.ui.realm",
					"/icons/add_exc.png");
	static ImageDescriptor removeImage = AbstractUIPlugin
			.imageDescriptorFromPlugin("com.onpositive.semantic.ui.realm",
					"/icons/delete.gif");

	public ViewerConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ViewerConfiguration configuration) {
		this.configuration = configuration;
	}

	public void createControl(Composite parent) {
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		Form form = toolkit.createForm(parent);
		Composite viewerHolder = new Composite(form.getBody(), SWT.NONE);
		viewerHolder.addDisposeListener(new DisposeListener() {

			
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}

		});
		form.setText(configuration.getDefinition().name());
		form.getBody().setLayout(new FillLayout());
		toolkit.decorateFormHeading(form);
		GridLayout layout = new GridLayout(2, false);
		viewerHolder.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginRight = -5;
		initTabFolder(parent, viewerHolder);
		viewerControl = new CustomViewerControl(tabOwner, SWT.NONE,
				new SavablePart() {

					
					public void saveColumnOrder() {
						if (activeTab != null) {
							int[] columnOrder = viewerControl.getColumnOrder();
							activeTab.setColumnOrder(columnOrder);
							activeTab.getOwner().save();
						}
					}

					
					public void saveSorting(String columnId, boolean b) {

					}

				});
		tabOwner.setLayoutData(new GridData(GridData.FILL_BOTH));
		popupMenu = new MenuManager();
		popupMenu.setRemoveAllWhenShown(true);
		popupMenu.addMenuListener(new IMenuListener() {

			
			public void menuAboutToShow(IMenuManager manager) {
				MenuManager ma = new MenuManager("Columns");
				ArrayList<ColumnConfiguration> columns = activeTab.getColumns();
				final HashMap<String, ColumnConfiguration> ids = new HashMap<String, ColumnConfiguration>();
				for (ColumnConfiguration c : columns) {
					ids.put(c.getId(), c);
				}
				for (final IColumnDefinition c : activeTab.getOwner()
						.getDefinition().allPossibleColumns()) {
					Action action = new Action(c.name(), SWT.CHECK) {

						public void run() {
							if (isChecked()) {
								ColumnConfiguration createColumnConfiguration = new ColumnConfiguration(
										c.id());
								activeTab.addColumn(createColumnConfiguration);
								viewerControl
										.addColumn(createColumn(createColumnConfiguration));
								viewerControl.layout(true, true);
								activeTab.getOwner().save();
							} else {
								activeTab.removeColumn(ids.get(c.id()));
								viewerControl.removeColumn(c.id());
							}
						}

					};
					action.setImageDescriptor(SWTImageManager.getImageDescriptor(c.icon()));
					action.setChecked(ids.containsKey(c.id()));
					ma.add(action);
				}
				manager.add(ma);
			}

		});
		Control control = viewerControl.getStructuredViewer().getControl();
		control.setMenu(popupMenu.createContextMenu(control));
		initTabs();

	}

	private void initTabFolder(Composite parent, Composite viewerHolder) {
		tabOwner = new CTabFolder(viewerHolder, SWT.BOTTOM | SWT.NO_BACKGROUND
				| SWT.FLAT);

		Display display = parent.getDisplay();
		this.tabs = new ArrayList<ViewerTabConfiguration>();
		tabOwner.setSimple(true);
		installReorderHandler();
		tabOwner.setSelectionBackground(new Color[] {
				display.getSystemColor(SWT.COLOR_WHITE),
				display.getSystemColor(SWT.COLOR_WHITE),
				display.getSystemColor(SWT.COLOR_WHITE),
				display.getSystemColor(SWT.COLOR_GRAY) }, new int[] { 25, 50,
				100 }, true);
		tabOwner.setTabHeight(21);
		Composite toolbar = new Composite(tabOwner, SWT.NONE);
		toolbar
				.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
						false));
		GridLayout layout = new GridLayout(2, false);
		ToolBar tb = new ToolBar(toolbar, SWT.FLAT);
		// tabs.setTabHeight(21);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		toolbar.setLayout(layout);
		ToolBarManager tmanager = new ToolBarManager(tb);
		Action item = new Action() {
			public void run() {
				final ViewerTabConfiguration conf = new ViewerTabConfiguration();
				conf.setOwner(configuration);
				try {
					Binding bnd = new Binding(conf) {
						public void commit() {
							super.commit();
							configuration.addTab(conf);
						}

					};
					bnd.setRealm(new Realm<ViewerTabConfiguration>(tabs));
					bnd.setAutoCommit(false);
					DisplayableCreator.showWidget(bnd,
							"com.onpositive.semantic.ui.realm", "addTab.dlf");
				} catch (Exception e1) {
					Activator.log(e1);
				}

			}

		};
		item.setImageDescriptor(addImage);

		item.setText("Add new viewer tab configuration");
		removeItem = new Action() {

			public void run() {
				boolean openConfirm = MessageDialog
						.openConfirm(
								tabOwner.getShell(),
								"Remove viewer tab?",
								MessageFormat
										.format(
												"Are you sure want to remove tab ''{0}'' from view",
												activeTab.getName()));
				if (openConfirm) {
					configuration.removeTab(activeTab);
				}
			}

		};
		removeItem.setImageDescriptor(removeImage);
		// removeItem.setData(REMOVE);
		// item.addSelectionListener(listener);
		// removeItem.addSelectionListener(listener);
		tb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		removeItem.setText("Remove selected viewer tab configuration");
		removeItem.setEnabled(false);
		tmanager.add(item);
		tmanager.add(removeItem);
		tmanager.update(false);
		// ToolBar right = new ToolBar(toolbar, SWT.NONE);
		// ToolItem item2 = new ToolItem(right, SWT.PUSH);
		// item2.setImageD(configureImage);
		// item2.setToolTipText("Settings");
		// item2.setData(CONFIGURE);
		// item2.addSelectionListener(listener);
		// right.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
		tabOwner.setTopRight(toolbar, SWT.FILL);
		tabOwner.setData(TAB_SELECTION);
		tabOwner.addSelectionListener(listener);
		MenuManager manager = new MenuManager();
		manager.add(item);
		manager.add(removeItem);
		Menu createContextMenu = manager.createContextMenu(tabOwner);

		tabOwner.setMenu(createContextMenu);
	}

	private void installReorderHandler() {
		Listener dragListener = new ItemReorderHandler();
		tabOwner.addListener(SWT.DragDetect, dragListener);
		tabOwner.addListener(SWT.MouseUp, dragListener);
		tabOwner.addListener(SWT.MouseMove, dragListener);
		tabOwner.addListener(SWT.MouseExit, dragListener);
		tabOwner.addListener(SWT.MouseEnter, dragListener);
		tabOwner.addListener(SWT.Paint, dragListener);
	}

	SelectionListener listener = new SelectionListener() {

		public void widgetDefaultSelected(SelectionEvent e) {

		}

		public void widgetSelected(SelectionEvent e) {
			if (e.widget.getData().equals(TAB_SELECTION)) {
				int selectionIndex = tabOwner.getSelectionIndex();
				ViewerTabConfiguration viewerTabConfiguration = tabs
						.get(selectionIndex);
				initOrActivate(viewerTabConfiguration);
				removeItem.setEnabled(configuration
						.mayRemove(viewerTabConfiguration));
			}
		}

	};
	private IViewerConfigurationListener configurationListener = new IViewerConfigurationListener() {

		public void tabOrderChanged(
				Collection<ViewerTabConfiguration> newTabOrder) {

		}

		public void tabsRemoved(Collection<ViewerTabConfiguration> tabs) {
			for (ViewerTabConfiguration t : tabs) {
				tabRemoved(t);
			}
		}

		public void tabsAdded(Collection<ViewerTabConfiguration> tabs) {
			for (ViewerTabConfiguration t : tabs) {
				addTab(t);
			}
		}

		public void tabMoved(int oldPosition, int newPosition) {
			moveTab(newPosition, oldPosition);
		}

	};
	private Action removeItem;

	public void initTabs() {
		List<ViewerTabConfiguration> tabs = configuration.getTabs();
		for (ViewerTabConfiguration t : tabs) {
			addTab(t);

		}
		configuration.addViewerConfigurationListener(configurationListener);
		tabOwner.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				configuration
						.removeViewerConfigurationListener(configurationListener);
			}

		});
	}

	private void addTab(ViewerTabConfiguration t) {
		tabs.add(t);
		CTabItem item = new CTabItem(tabOwner, SWT.NONE);
		String name = t.getName();
		item.setText(name);
		Image image = t.getImage();
		if (image != null) {
			item.setImage(image);
		}
		String tooltipText = t.getTooltipText();
		if (tooltipText != null) {
			item.setToolTipText(tooltipText);
		}
		item.setControl(viewerControl);

		tabOwner.setSelection(item);
		tabOwner.layout(true);
		initOrActivate(t);
		viewerControl.setVisible(true);
	}

	protected void initOrActivate(ViewerTabConfiguration viewerTabConfiguration) {
		if (activeTab != null) {
			deactivate(activeTab, tabActivations.get(viewerTabConfiguration));
		}
		activeTab = viewerTabConfiguration;
		ViewerTabState viewerTabState = tabActivations
				.get(viewerTabConfiguration);
		if (viewerTabState == null) {
			viewerTabState = initializeTab(viewerTabConfiguration);
			tabActivations.put(viewerTabConfiguration, viewerTabState);
		}
		ArrayList<ColumnConfiguration> columns2 = viewerTabConfiguration
				.getColumns();
		FastTreeColumn[] columns = new FastTreeColumn[columns2.size()];
		int a = 0;
		int[] cOrder = new int[columns.length];
		for (ColumnConfiguration c : columns2) {
			cOrder[a] = a;
			columns[a++] = createColumn(c);
		}
		int[] columnOrder = viewerTabConfiguration.getColumnOrder();
		if (columnOrder != null) {
			cOrder = columnOrder;
		}
		viewerControl
				.configure(
						new com.onpositive.semantic.ui.realm.fastviewer.ViewerConfiguration(
								columns, viewerTabState, false, cOrder,
								viewerTabConfiguration.getId(), "", false),
						true);
		getTabItem(viewerTabConfiguration).setControl(viewerControl);
		activateTab(viewerTabConfiguration, viewerTabState);
		viewerControl.layout(true, true);

	}

	private FastTreeColumn createColumn(ColumnConfiguration c) {
		FastTreeColumn ca = new FastTreeColumn(c.name(), c.description(), c
				.isResizable() ? 2 : 0, c.getExpectedCharCount(), c.getId());
		ca.setImage(c.getImage());
		ca.setResizable(c.isResizable());
		return ca;
	}

	private void activateTab(ViewerTabConfiguration viewerTabConfiguration,
			ViewerTabState viewerTabState) {
	}

	CustomViewerControl viewerControl;
	private MenuManager popupMenu;

	private ViewerTabState initializeTab(
			ViewerTabConfiguration viewerTabConfiguration) {

		return null;
	}

	private void deactivate(ViewerTabConfiguration activeTab2,
			ViewerTabState viewerTabState) {

	}

	protected void tabRemoved(ViewerTabConfiguration configuration) {
		CTabItem tabItem = getTabItem(configuration);
		ViewerTabState viewerTabState = tabActivations.get(activeTab);
		if (activeTab == configuration) {
			deactivate(activeTab, viewerTabState);
		}

		tabActivations.remove(configuration);
		tabs.remove(configuration);
		tabItem.dispose();
		disposeTabResources(configuration, viewerTabState);
		viewerControl.setVisible(true);
		tabOwner.layout(true);
		// tabOwner.setSelection(tabOwner.getItemCount()-1);
	}

	private void disposeTabResources(ViewerTabConfiguration configuration2,
			ViewerTabState viewerTabState) {
	}

	private CTabItem getTabItem(ViewerTabConfiguration configuration) {
		int indexOf = tabs.indexOf(configuration);
		return tabOwner.getItem(indexOf);
	}

	private void moveTab(int index, int indexOf) {
		CTabItem dragItem = tabOwner.getItem(indexOf);
		CTabItem newItem = new CTabItem(tabOwner, SWT.NONE, index);
		newItem.setText(dragItem.getText());
		newItem.setImage(dragItem.getImage());
		newItem.setToolTipText(dragItem.getToolTipText());
		Control c = dragItem.getControl();
		dragItem.setControl(null);
		newItem.setControl(c);
		dragItem.dispose();
		tabOwner.setSelection(newItem);
	}
}