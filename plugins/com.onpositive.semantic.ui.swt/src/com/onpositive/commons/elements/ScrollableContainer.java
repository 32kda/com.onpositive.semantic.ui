package com.onpositive.commons.elements;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public class ScrollableContainer extends Container{

	private ScrolledComposite composite;
	private Composite contentParent;
	private boolean alwaysShowScrollBars=false;
	private boolean expandHorizontal=true;
	private boolean expandVertical=true;


	public boolean isAlwaysShowScrollBars() {
		return alwaysShowScrollBars;
	}

	@HandlesAttributeDirectly("alwaysShowScrollBars")
	public void setAlwaysShowScrollBars(boolean alwaysShowScrollBars) {
		this.alwaysShowScrollBars = alwaysShowScrollBars;
		if (isCreated()){
			composite.setAlwaysShowScrollBars(alwaysShowScrollBars);
		}
	}
	
	public ScrollableContainer(){
		super.getLayoutHints().setGrabVertical(true);
		super.getLayoutHints().setGrabHorizontal(true);
	}

	public boolean isExpandHorizontal() {
		return expandHorizontal;
	}
	
	public void add(AbstractUIElement element) {
		super.add(element);
		if (isCreated()){
		composite.layout(true, true);
		}
	}
	
	public void add(int position, AbstractUIElement<?> element) {
		super.add(position, element);
		if (isCreated()){
			Point computeSize = contentParent.computeSize(-1,-1);
			composite.setMinSize(computeSize);
			composite.layout(true, true);
			}
	}
	
	public void addAbove(int position, AbstractUIElement<?> element) {
		super.addAbove(position, element);
		if (isCreated()){
			Point computeSize = contentParent.computeSize(-1,-1);
			composite.setMinSize(computeSize);
			composite.layout(true, true);
			}
	}	

	public void setExpandHorizontal(boolean expandHorizontal) {
		this.expandHorizontal = expandHorizontal;
		if (isCreated()){
			composite.setExpandHorizontal(expandHorizontal);
		}
	}

	public boolean isExpandVertical() {
		return expandVertical;
	}

	public void setExpandVertical(boolean expandVertical) {
		this.expandVertical = expandVertical;
		if (isCreated()){
			composite.setExpandVertical(expandVertical);
		}
	}


	
	public void adapt(AbstractUIElement<?> element) {
		super.adapt(element);
		if (isCreated()){
		Point computeSize = contentParent.computeSize(-1,-1);
		composite.setMinSize(computeSize);
		}
	}

	protected Composite createControl(Composite conComposite) {		
		Composite createControl = super.createControl(conComposite);
		createControl.setLayout(new FillLayout());
		composite = new ScrolledComposite(createControl, SWT.H_SCROLL|SWT.V_SCROLL);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		composite.setBackground(null);
		contentParent = new Composite(composite,SWT.NONE);		
		composite.setAlwaysShowScrollBars(alwaysShowScrollBars);
		composite.setExpandHorizontal(expandHorizontal);
		composite.setExpandVertical(expandVertical);
		composite.setContent(contentParent);		
		contentParent.setLayout(new FillLayout(SWT.VERTICAL));		
		return createControl;
	}
	
	public void internalCreate(){
		super.internalCreate();
		Point computeSize = contentParent.computeSize(-1,-1);
		composite.setMinSize(computeSize);
	}

	
	public Composite getContentParent() {
		return contentParent;
	}
}
