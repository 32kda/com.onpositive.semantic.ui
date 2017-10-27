package com.onpositive.commons.elements;

import java.util.WeakHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;

public class CTabFolderElement extends Container {

	private static final String SELECTED_NUM = "selectedNum"; //$NON-NLS-1$

	public CTabFolderElement() {
		this.getLayoutHints().setGrabVertical(true);
		
	}
	
	public void create() {
		super.create();
		if (this.selectionIndex != -1) {
			this.getControl().setSelection(this.selectionIndex);
			
		}
		
	}

	private int selectionIndex;

	protected void setRedraw(boolean redraw) {

	}

	public boolean isGroup() {
		return true;
	}

	public void setLayout(Layout layout) {
		super.setLayout(layout);
	}

	public void internalLoadConfiguration(IAbstractConfiguration configuration) {
		this.selectionIndex = configuration.getIntAttribute(SELECTED_NUM);
	}

	public void internalStoreConfiguration(IAbstractConfiguration configuration) {
		final int selectionIndex = this.getControl().getSelectionIndex();
		configuration.setIntAttribute(SELECTED_NUM, selectionIndex);
	}

	protected CTabFolder createControl(Composite conComposite) {
		final CTabFolder CTabFolder = new CTabFolder(conComposite, this.calcStyle()|SWT.FLAT);
		CTabFolder.setSimple(true);
		
		Display display=Display.getCurrent();
		CTabFolder.setSelectionBackground(new Color[]{display.getSystemColor(SWT.COLOR_GRAY), 
				 		                           display.getSystemColor(SWT.COLOR_GRAY),
				 		                           display.getSystemColor(SWT.COLOR_WHITE), 
				 		                           display.getSystemColor(SWT.COLOR_WHITE)},
				 		               new int[] {25, 50, 100},true);
		
		CTabFolder.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				
				Control[] children = CTabFolder.getChildren();
				Control control = CTabFolder.getSelection().getControl();
				for (Control c:children){
					if (c!=control){
						c.setBounds(0, 0,0,0);
					}
					else{
						if (c instanceof Composite){
							Composite m=(Composite) c;
							m.layout(true, true);
						}
					}
				}
				
			}

		});
		CTabFolder.addListener(SWT.Resize, new Listener(){

			public void handleEvent(Event event) {
				Control[] children = CTabFolder.getChildren();
				for (Control c:children){
					if (c instanceof Composite){
						Composite m=(Composite) c;
						m.layout(true);
					}
				}
			}
			
		});
		return CTabFolder;
	}

	WeakHashMap<AbstractUIElement<?>, CTabItem> items = new WeakHashMap<AbstractUIElement<?>, CTabItem>();

	public void internalCreate() {
		basicCreate();		
		for (final AbstractUIElement<?> e : this.children) {
			final CTabItem item = new CTabItem(this.getControl(), SWT.NONE);
			String caption = e.getCaption();
			if (caption!=null){
			item.setText(caption);
			}
			items.put(e, item);
			e.create();
			item.setControl(e.getControl());
			this.adapt(e);
			this.accept(e);
		}		
		
		this.setRedraw(true);
	}
	
	public void adapt(AbstractUIElement<?> element) {
		if (this.isCreated()){
			if (items.get(element)==null){
				final CTabItem item = new CTabItem(this.getControl(), SWT.NONE);
				String caption = element.getCaption();
				if (caption!=null){
				item.setText(caption);
				}
				items.put(element, item);
				if (element.getLayoutControl()!=null){
					item.setControl(element.getLayoutControl());
					getControl().setSelection(item);
				}
			}
		}
	}

	protected void unudapt(AbstractUIElement<?> element) {
		if (this.isCreated()) {
			final CTabItem ti = this.items.get(element); 
			if (ti != null) {
				ti.dispose();
				this.items.remove(element);
			}
		}
	}

	protected void update(AbstractUIElement<?> container) {
		final CTabItem ti = this.items.get(container); 
		if (ti != null) {
			ti.setText(container.getText());
		}
	}

	public boolean handlesLabels() {
		return true;
	}

	public CTabFolder getControl() {
		return (CTabFolder) this.widget;
	}
}
