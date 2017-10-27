package com.onpositive.semantic.ui.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;

public class TabFolderLayout extends CompositeEditor implements ISemanticLayout {

	public TabFolder getControl() {
		return (TabFolder) super.getControl();
	}

	static class AspectInfo {
		AbstractUIElement<?> container;
		TabItem item;
		IVisualisationAspect aspect;
	}

	private final HashMap<IVisualisationAspect, AspectInfo> editors = new HashMap<IVisualisationAspect, AspectInfo>();
	private final HashMap<TabItem, AspectInfo> titems = new HashMap<TabItem, AspectInfo>();
	private final ArrayList<IVisualisationAspect> aspects = new ArrayList<IVisualisationAspect>();
	private AspectInfo aspectInfo;

	public String getDescription() {
		return null;
	}

	public String getIcon() {
		return null;
	}

	public String getName() {
		return "Tab Folder Layout"; //$NON-NLS-1$
	}

	@SuppressWarnings("unchecked")
	public void installAspect(IVisualisationAspect aspect) {
		if (this.isCreated()) {
			this.aspectInfo = this.getAspectInfo(aspect);
			final int binarySearch = Collections.binarySearch((List) this.aspects, aspect);
			final int insertionPoint = -binarySearch - 1;
			final TabItem tabItem = new TabItem(this.getControl(), SWT.NONE,
					insertionPoint);
			this.aspects.add(insertionPoint, aspect);
			tabItem.setText(aspect.getName());
			this.titems.put(tabItem, this.aspectInfo);
			this.aspectInfo.item = tabItem;
			this.aspectInfo.item = tabItem;
		}
	}

	private AspectInfo getAspectInfo(IVisualisationAspect aspect) {
		AspectInfo aspectInfo = this.editors.get(aspect);
		if (aspectInfo == null) {
			aspectInfo = new AspectInfo();
			this.editors.put(aspect, aspectInfo);
			aspectInfo.aspect = aspect;
		}
		return aspectInfo;
	}

	public void refreshAspect(VisualisationAspect e) {

	}

	public void setActive(IVisualisationAspect aspect) {
		if (this.isCreated()) {
			final AspectInfo aspectInfo = this.editors.get(aspect);
			if (aspectInfo != null) {
				if (aspectInfo.container == null) {
					aspectInfo.container = this.initContainer(aspect);
				}
				this.getControl().setSelection(aspectInfo.item);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AbstractUIElement<Control> initContainer(IVisualisationAspect aspect) {
		final IPropertyEditor<AbstractUIElement> editor = (IPropertyEditor<AbstractUIElement>) aspect.getEditor().getEditor();
		editor.setBinding(this.getBinding());
		final AbstractUIElement<? extends Control> element = editor.getUIElement();
		this.add(element);
		final AspectInfo aspectInfo = this.getAspectInfo(aspect);
		if (aspectInfo.item != null) {
			aspectInfo.item.setControl(element.getControl());
		}
		return (AbstractUIElement<Control>) element;
	}

	public void uninstallAspect(IVisualisationAspect aspect) {
		if (this.isCreated()) {
			final AspectInfo aspectInfo = this.editors.get(aspect);
			this.aspects.remove(aspect);
			if (aspectInfo != null) {
				if (aspectInfo.item != null) {
					aspectInfo.item.dispose();
					aspectInfo.item = null;
				}
			}
		}
	}

	protected TabFolder createControl(Composite conComposite) {
		final TabFolder tabFolder = new TabFolder(conComposite, SWT.NONE);
		tabFolder.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				final int selectionIndex = tabFolder.getSelectionIndex();
				final TabItem item = tabFolder.getItem(selectionIndex);
				AspectInfo aspectInfo = TabFolderLayout.this.titems.get(item);
				if (aspectInfo == null) {
					aspectInfo = TabFolderLayout.this.aspectInfo;
				}
				if (aspectInfo != null) {
					if (aspectInfo.container == null) {
						aspectInfo.container = TabFolderLayout.this.initContainer(aspectInfo.aspect);
					}
					final Control control = aspectInfo.container.getControl();
					if (item.getControl() != control) {
						item.setControl(control);
					}
				}

			}

		});
		return tabFolder;
	}
}
