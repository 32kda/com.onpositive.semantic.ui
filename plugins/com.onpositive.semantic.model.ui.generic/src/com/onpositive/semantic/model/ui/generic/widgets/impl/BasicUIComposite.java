package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.ui.core.Rectangle;

public abstract class BasicUIComposite<T> extends BasicUIElement<T> implements ICompositeElement<BasicUIElement<T>, T>{

	protected static final String MARGIN_PROP_ID = "margin";
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 5126377667746915196L;
	protected ArrayList<BasicUIElement<T>>children=new ArrayList<BasicUIElement<T>>();

	
	class Listener implements PropertyChangeListener, Serializable{

		
		/**
		 * Serial version UID
		 */
		private static final long serialVersionUID = 1529998183090225819L;

		public void propertyChange(PropertyChangeEvent event) {
			propertyChanged(event);
		}
		
	}
	
	@Override
	public IUIElement<?> getElement(String id) {
		if (id.equals(getId()))
			return this;
		for (BasicUIElement<?>e:children){
			if(id.equals(e.getId())){
				return e;
			}
			if (e instanceof ICompositeElement) {
				IUIElement<?> foundElement = ((ICompositeElement<?, ?>) e).getElement(id);
				if (foundElement != null)
					return foundElement;
			}
				
		}
		return null;
	}
	
	public void onDisplayable(IUIElement<?> element) {
		onDisplayable(element, element.isDisplayable());
	}
	
	public void setMargin(Rectangle parseRectangle) {
		setData(MARGIN_PROP_ID, parseRectangle);
	}
	
	protected void propertyChanged(PropertyChangeEvent event) {
		redraw();
	}

	public Rectangle getMargin(){
		Object data = getData(MARGIN_PROP_ID);
		if (data==null){
			data=new Rectangle(2, 2, 2, 2);
		}
		return (Rectangle) data;
	}	
	
	protected final Listener listener=new Listener();
	
	public void add(BasicUIElement<T> element) {
		children.add(element);
		element.parent=this;
		element.fireAdding(this);
		element.fireHiearachyChange();
		if (isCreated()){			
			adapt(element);
		}
	}
	@Override
	protected void onCreate(ICompositeElement<?, ?> parent) {
		super.onCreate(parent);
		addChildren();
	}

	protected void addChildren() {
		for (BasicUIElement<T>d:children){
			adapt(d);
		}
	}
	@Override
	protected void onDispose() {
		super.onDispose();
		for (BasicUIElement<T>d:children){
			unadapt(d);
		}
	}
	
	protected void onDisplayable(IUIElement<?>element,boolean displayable){
		
	}
	
	protected void fireHiearachyChange() {
		super.fireHiearachyChange();
		for (final BasicUIElement<?> a : this.children) {
			a.fireHiearachyChange();
		}
	}
	
	protected void adapt(BasicUIElement<T> element) {
		createChild(element);
	}
	
	protected void createChild(BasicUIElement<T>element){
		element.addPropertyChangeListener(listener);
		element.onCreate(this);
	}
	
	protected void unadapt(BasicUIElement<T> element) {
		element.removePropertyChangeListener(listener);
		element.onDispose();
	}
	
	public List<BasicUIElement<T>> getChildren() {
		return new ArrayList<BasicUIElement<T>>(children);
	}

	public void remove(BasicUIElement<T> element) {
		element.parent=null;
		children.remove(element);
		if (isCreated()){
			
			unadapt(element);			
		}
//		for (final IContainerListener l : this.l) {
//			l.elementRemoved((ICompositeElement)this, (IUIElement)element);
//		}
		element.fireRemoved(this);
		element.fireHiearachyChange();
	}

}
