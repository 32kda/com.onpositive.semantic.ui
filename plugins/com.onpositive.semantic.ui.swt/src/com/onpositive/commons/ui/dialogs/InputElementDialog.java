package com.onpositive.commons.ui.dialogs;

import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.contentassist.ContentProposalAdapter;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.semantic.model.api.property.IHasStatus;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IDisplayable;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;

@SuppressWarnings("unchecked")
public class InputElementDialog extends ElementDialog implements IDisplayable {

	@SuppressWarnings("rawtypes")
	public InputElementDialog(final IPropertyEditor<AbstractUIElement> ed) {
		super(ed.getBinding(), ed.getUIElement(), ed.getUIElement()
				.getCaption().length() == 0 ? ed.getBinding().getName() : ed
				.getUIElement().getCaption(), ed.getBinding().getDescription());
		this.init(ed);
	}

	@SuppressWarnings("rawtypes")
	public InputElementDialog(IHasStatus status, AbstractUIElement editor,
			String title, String description) {
		super(status, editor, title, description);
		if (editor instanceof IPropertyEditor) {
			this.init((IPropertyEditor<?>) editor);
		}
	}
	
	public InputElementDialog(IHasStatus status, AbstractUIElement<?> editor,
			String title, String description, boolean modal) {
		super(status, editor, title, description, modal);
		if (editor instanceof IPropertyEditor) {
			this.init((IPropertyEditor<?>) editor);
		}
	}
	
	static WeakHashMap<Control,Boolean>eds=new WeakHashMap<Control, Boolean>();
	
	public static void pushControlFromContentAssist(Control c){
		eds.put(c, Boolean.TRUE);
	}
	

	private void init(final IPropertyEditor<?> ed) {
		final AbstractUIElement<? extends Control> element2 = (AbstractUIElement<? extends Control>) ed.getUIElement();
		final SWTEventListener eventListener = new SWTEventListener() {

			public void handleEvent(AbstractUIElement element, Event event) {
				if (eds.containsKey(event.widget)){
					eds.remove(event.widget);
					return;
				}
				if (event.keyCode == '\r'&&event.doit&&!ContentProposalAdapter.isContentPopupOpen()) {
					
					if (ed.getBinding().getStatus().getCode() != IStatus.ERROR) {
						ed.getBinding().commit();
						InputElementDialog.this.close();
					}
				}
			}
		};
		this.hookListeners(element2, eventListener);
		//this.setCloseOnMouseOut(true);
	}
	
	

	private void hookListeners(AbstractUIElement<?> ed,
			SWTEventListener eventListener) {
		if (ed instanceof ICompositeElement) {
			final ICompositeElement em = (ICompositeElement) ed;
			final List<AbstractUIElement<?>> children = em.getChildren();
			for (final AbstractUIElement<?> e : children) {
				this.hookListeners(e, eventListener);
			}
		}
		ed.addListener(SWT.KeyUp, eventListener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.onpositive.commons.ui.dialogs.IDisplayable#openWidget()
	 */
	public int openWidget() {
		return open();
	}

	public boolean isModal() {
		return false;
	}

}
