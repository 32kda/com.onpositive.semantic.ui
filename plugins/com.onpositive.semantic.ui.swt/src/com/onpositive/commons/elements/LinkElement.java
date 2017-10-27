package com.onpositive.commons.elements;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.IHyperlinkListener;
import com.onpositive.semantic.model.ui.generic.widgets.IHasHyperlinks;
import com.onpositive.semantic.model.ui.property.editors.FormTextElement;

public class LinkElement extends AbstractUIElement<Hyperlink> implements IHasHyperlinks<Hyperlink> {

	protected HashSet<IHyperlinkListener> listeners = new HashSet<IHyperlinkListener>();

	private String url;

	
	public String getCaption() {
		return super.getCaption();
	}

	
	public void setCaption(String string) {
		if (string == null) {
			string = "";
		}
		super.setCaption(string);
	}

	
	protected Hyperlink createControl(Composite conComposite) {
		Hyperlink hyperlink ;
		FormToolkit service = getService(FormToolkit.class);
		if (service!=null){
			hyperlink=service.createHyperlink(conComposite, "", SWT.NONE);
			hyperlink.redraw();
		}else {
		hyperlink= new Hyperlink(conComposite, SWT.NONE);
		}
		hyperlink.setUnderlined(true);
		if (url != null) {
			hyperlink.setHref(url);
		}
		
		hyperlink.addHyperlinkListener(new FormTextElement.HL(listeners));
		hyperlink.setText(getCaption());
		return hyperlink;
	}

	public void addHyperLinkListener(IHyperlinkListener listener) {
		listeners.add(listener);
		
	}

	public void removeHyperLinkListener(IHyperlinkListener listener) {
		listeners.remove(listener);		
	}

	
	public boolean needsLabel() {
		return false;
	}

	@HandlesAttributeDirectly("url")
	public void setUrl(String attribute) {
		this.url = attribute;
	}

	public String getUrl() {
		return this.url;
	}
}
