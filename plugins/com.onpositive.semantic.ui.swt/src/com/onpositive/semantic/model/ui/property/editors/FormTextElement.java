package com.onpositive.semantic.model.ui.property.editors;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;

import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.IHyperlinkListener;
import com.onpositive.semantic.model.ui.generic.widgets.IHasHyperlinks;

public class FormTextElement<T> extends TextElement<T, FormText> implements IHasHyperlinks<FormText> {

	public static final class HL implements
			org.eclipse.ui.forms.events.IHyperlinkListener {
		
		HashSet<IHyperlinkListener>listeners;
		
		public HL(HashSet<IHyperlinkListener> listeners) {
			super();
			this.listeners = listeners;
		}

		public void linkExited(HyperlinkEvent e) {
			for (IHyperlinkListener l:listeners){
				l.linkExited(new com.onpositive.semantic.model.ui.generic.HyperlinkEvent(e.getHref(),e.getLabel()));
			}
			
		}

		public void linkEntered(HyperlinkEvent e) {
			for (IHyperlinkListener l:listeners){
				l.linkEntered(new com.onpositive.semantic.model.ui.generic.HyperlinkEvent(e.getHref(),e.getLabel()));
			}
		}

		public void linkActivated(HyperlinkEvent e) {
			for (IHyperlinkListener l:listeners){
				l.linkActivated(new com.onpositive.semantic.model.ui.generic.HyperlinkEvent(e.getHref(),e.getLabel()));
			}
		}
	}

	HashSet<IHyperlinkListener>listeners=new HashSet<IHyperlinkListener>();
	
	public void addHyperLinkListener(final IHyperlinkListener listener){
		listeners.add(listener);		
	}
	
	public void removeHyperLinkListener(IHyperlinkListener listener){
		listeners.remove(listener);		
	}
	
	protected void internalSetCaption() {
		if (this.getBinding()==null){
			try{
			getControl().setText(this.getCaption(), false, true);
			}catch (Exception e) {
				getControl().setText(this.getCaption(), false, true);
			}
		}
	}

	protected boolean isEnabled(IBinding binding) {
		return true;
	}

	public boolean needsLabel() {
		return false;
	}

	protected void internalSetText(String txt) {
		if (txt != null) {
			try{
			this.getControl().setText(UIElementFactory.adaptText(txt), true,
					true);
			}catch (Exception e) {
				getControl().setText(txt, false, true);
			}
		} else {
			this.getControl().setText("", false, false); //$NON-NLS-1$
		}
	}

	protected FormText createControl(Composite conComposite) {
		final FormText formText = new FormText(conComposite, SWT.WRAP);
		formText.addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void focusGained(FocusEvent e) {
				formText.traverse(SWT.TRAVERSE_TAB_NEXT);
			}
		});
		formText.addHyperlinkListener(new HL(listeners));		
		return formText;	
	}



	public String getContentAssistRole() {
		return "";
	}

	public void setContentAssistRole(String contentAssistRole) {
		
	}



	public void setUrl(String attribute) {
		this.setText(attribute);
	}
}
