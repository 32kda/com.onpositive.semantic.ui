package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;

public class Snippet003RadioAndGroups extends AbstractSnippet {

	boolean goLeft;
	boolean goRight;
	boolean goForward;

	@Caption("%Left")
	public boolean isGoLeft() {
		return this.goLeft;
	}

	public void setGoLeft(boolean goLeft) {
		this.goLeft = goLeft;
		if (goLeft) {
			System.out.println("Left"); //$NON-NLS-1$
		}
	}

	@Caption("%Right")
	public boolean isGoRight() {
		return this.goRight;
	}

	public void setGoRight(boolean goRight) {
		this.goRight = goRight;
		if (goRight) {
			System.out.println("Right"); //$NON-NLS-1$
		}
	}

	@Caption("%Forward")
	public boolean isGoForward() {
		return this.goForward;
	}

	public void setGoForward(boolean goForward) {

		this.goForward = goForward;
		if (goForward) {
			System.out.println("Forward"); //$NON-NLS-1$
		}
	}

	
	protected AbstractUIElement<?> createContent() {
		final Container cs = new Container(Container.GROUP);
		cs.setCaption("Choose Direction"); //$NON-NLS-1$
		final Binding th = new Binding(this);
		cs.add(new ButtonSelector(th.getBinding("GoForward"), SWT.RADIO)); //$NON-NLS-1$
		cs.add(new ButtonSelector(th.getBinding("GoLeft"), SWT.RADIO)); //$NON-NLS-1$
		cs.add(new ButtonSelector(th.getBinding("GoRight"), SWT.RADIO)); //$NON-NLS-1$
		final FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.spacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		cs.setLayout(layout);
		return cs;
	}

	
	protected String getDescription() {
		return "This snippet shows how to create Radio Group"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Radio and Groups"; //$NON-NLS-1$
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}

}
