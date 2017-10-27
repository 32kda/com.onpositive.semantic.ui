package com.onpositive.semantic.model.ui.actions;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.ComponentEnablementController;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public abstract class ContributionItem implements IContributionItem,Serializable{


	public static final String ENABLED_PROP_ID = "enabled";
	public static final String VISIBLE_PROP_ID = "visible";

	private  class ActionController extends ComponentEnablementController {
		private static final long serialVersionUID = -2797905656097543109L;

		private ActionController(IUIElement<?> modelElement, String path) {
			super(modelElement, path);
		}

		protected IBinding getBinding(IBindable service) {
			if (service instanceof ISelectorElement<?>){
				ISelectorElement<?>s=(ISelectorElement<?>) service;
				return (IBinding) s.getSelectionBinding();
			}
			return service.getBinding();
		}

		@Override
		protected void doOp(boolean val) {
			mm=val;
			setEnabled(val);
		}

		@Override
		protected IUIElement<?> doRaiseUp(IUIElement<?> parent) {
					return parent;
		}
	}
	Boolean mm;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean visible=true;
	private boolean enabled=true;
	protected final PropertyChangeSupport support=new PropertyChangeSupport(this);
	protected String id;

	public String id() {
		return id;
	}

	public void setId(String id) {
		String id2 = this.id;
		this.id = id;
		support.firePropertyChange("id", id2, id);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	public void setEnabled2(boolean b) {
		if (this.enabled!=b){
			boolean old=enabled;
			enabled=b;
			support.firePropertyChange(ENABLED_PROP_ID, old,b);
		}
	}

	public void setEnabled(boolean b) {
		boolean pass=mm==null||mm;
		setEnabled2(b&&pass);
	}

	public void setVisible(boolean visible) {
		if (this.visible!=visible){
			boolean old=this.visible;
			this.visible=visible;
			support.firePropertyChange(VISIBLE_PROP_ID, old,visible);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isVisible() {
		return visible;
	}
	
	protected ComponentEnablementController ec;
	private ComponentEnablementController vc;

	public void setEnablementExpression(String enablementExpression, IUIElement<?> parentUI) {
		if (ec != null) {
			ec.dispose();
			ec=null;
			mm=null;
		}
		if (enablementExpression != null && enablementExpression.length() > 0) {
			
			ComponentEnablementController el = new ActionController(parentUI, enablementExpression);
			ec = el;
			
			parentUI.addElementListener(el);
			el.hierarchyChanged(parentUI);
			//el.bindingChanged(null, getBinding(), getBinding());
		}
	}

	public void setVisibilityExpression(String enablementExpression,
			IUIElement<?> parentUI) {
		if (vc != null) {
			vc.dispose();
			vc=null;
		}
		if (enablementExpression != null && enablementExpression.length() > 0) {
			
			ComponentEnablementController el = new ActionController(parentUI, enablementExpression){
				
				/**
				 * 
				 */
				private static final long serialVersionUID = -5735104232061759652L;

				@Override
				protected void doOp(boolean val) {
					setVisible(val);
				}
			};
			vc = el;
			
			parentUI.addElementListener(el);
			el.hierarchyChanged(parentUI);
			//el.bindingChanged(null, getBinding(), getBinding());
		}
	}

}