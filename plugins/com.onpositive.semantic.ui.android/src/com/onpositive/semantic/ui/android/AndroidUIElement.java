package com.onpositive.semantic.ui.android;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.ICanBeReadOnly;
import com.onpositive.semantic.model.ui.generic.widgets.impl.BasicUIElement;
import com.onpositive.semantic.ui.android.composites.AndroidComposite;

public abstract class AndroidUIElement extends BasicUIElement<View> implements ICanBeReadOnly<View>{

	protected class KeystrokePropagationListener implements OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			return propagateKeystroke(v,keyCode,event);
		}
		
	}
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 9008886977913858841L;
	
	
	private boolean readOnly = false;
	protected ViewGroup contentParent;

	@Override
	public void redraw() {
		getControl().refreshDrawableState();
	}

	@Override
	public void executeOnUiThread(Runnable runnable) {
		runnable.run();
	}
	
	@Override
	protected final View createControl(ICompositeElement<?,?> parent){
		AndroidComposite cm=(AndroidComposite) parent;
		View createdView = internalCreate(cm,cm.getContext());
//		createdView.setFocusableInTouchMode(true);
		if (needKeystrokePropagation()) {
			createdView.setOnKeyListener(new KeystrokePropagationListener());
		}
		if (!isEnabled()){
			createdView.setEnabled(false);
		}
		if (!isDisplayable()) {
			createdView.setVisibility(View.GONE);
		}
		return createdView;
	}
	
	public boolean propagateKeystroke(View v, int keyCode, KeyEvent event) {
		if (getParent() instanceof AndroidComposite) {
			((AndroidComposite) getParent()).propagateKeystroke(v,keyCode,event);
		}
		return false;
	}

	protected boolean needKeystrokePropagation() {
		if (getParent() instanceof AndroidComposite) {
			return ((AndroidComposite) getParent()).needKeystrokePropagation();
		}
		return false;
	}

	@Override
	protected void endCreate() {
		super.endCreate();
		if (getBinding() != null) {
			onStatus(getBinding().getStatus());
		}
	}
	@Override
	protected void refreshAppearance() {
		if (isCreated()){
			widget.setEnabled(isEnabled());
			if (background != null) {
				doSetBackground(widget, background);
			}
			//TODO FILL ME
		}
		super.refreshAppearance();
	}
	
	protected void doSetBackground(View widget, String background) { //TODO normal color parsing
		try {
			widget.setBackgroundColor(Color.class.getField(background.toUpperCase()).getInt(null));
		} catch (Exception e) {
			System.err.println("Unknown color constant in dlf: " + background);
		}
	}

	@Override
	protected void onCreate(ICompositeElement<?, ?> parent) {
		super.onCreate(parent);
		resetValue();
	}

	protected void resetValue() {
		
	}
	
	public ViewGroup getContentParent() {
		if (contentParent != null)
			return contentParent;
		if (isCreated())
			return (ViewGroup) getControl().getParent();
		return null;
	}
	
	public void setContentParent(ViewGroup contentParent) {
		this.contentParent = contentParent;
	}

	@Override
	public void setEnabled(boolean val) {
		val = val && !readOnly;
		if (isCreated()){
			getControl().setEnabled(val);
		}
		super.setEnabled(val);
	}

	protected abstract View internalCreate(AndroidComposite cm, Context context);	
	
	@Override
	@HandlesAttributeDirectly("readonly")
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		setEnabled(!readOnly);
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	@Override
	protected void processValueChange(ISetDelta<?> valueElements) {
		super.processValueChange(valueElements);
		resetValue();
	}

}
