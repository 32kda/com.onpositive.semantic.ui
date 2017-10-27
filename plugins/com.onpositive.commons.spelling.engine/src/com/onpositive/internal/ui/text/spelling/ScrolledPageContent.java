package com.onpositive.internal.ui.text.spelling;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;

public class ScrolledPageContent extends SharedScrolledComposite {

	private final FormToolkit fToolkit;

	public ScrolledPageContent(Composite parent) {
		this(parent, SWT.V_SCROLL | SWT.H_SCROLL);
	}

	public ScrolledPageContent(Composite parent, int style) {
		super(parent, style);

		this.setFont(parent.getFont());

		this.fToolkit = new FormToolkit(parent.getDisplay());

		this.setExpandHorizontal(true);
		this.setExpandVertical(true);

		final Composite body = new Composite(this, SWT.NONE);
		body.addDisposeListener(new DisposeListener() {

			
			public void widgetDisposed(DisposeEvent e) {
				ScrolledPageContent.this.fToolkit.dispose();
			}

		});
		body.setFont(parent.getFont());
		this.setContent(body);
	}

	public void adaptChild(Control childControl) {
		this.fToolkit.adapt(childControl, true, true);
	}

	public Composite getBody() {
		return (Composite) this.getContent();
	}

}
