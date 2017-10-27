package com.onpositive.commons.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;

import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;

public class RootElement extends Container {

	public RootElement(Composite parent) {
		init(parent,SWT.NONE);
	}
	
	public RootElement(Composite parent,int style) {
		init(parent,style);
	}

	private void init(Composite parent,int style) {
		this.widget = new Composite(parent, style);
		if (this.getContentParent().getLayout() == null&&!(parent instanceof TabFolder)&&!(parent instanceof CTabFolder)) {
			this.getContentParent().setLayout(new FillLayout());
		}
		if (parent.getLayout() == null&&(!(parent instanceof TabFolder)&&!(parent instanceof CTabFolder))) {
			parent.setLayout(new FillLayout());
		}
		parent.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				RootElement.this.dispose();
			}

		});
	}
	public BasicUIElement<Composite> getRoot() {
		return this;
	}

	public void internalCreate() {
		throw new UnsupportedOperationException();
	}

}
