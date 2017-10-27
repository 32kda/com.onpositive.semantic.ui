package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.UIElementFactory;
import com.onpositive.commons.ui.appearance.AbstractLayouter;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.java.annotations.Description;
import com.onpositive.semantic.model.api.property.java.annotations.TextLabel;

@TextLabel("{Name}")
@Description("{Description}")
public abstract class AbstractSnippet {

	private Shell sh;

	protected int getStyle() {
		return SWT.DIALOG_TRIM | SWT.RESIZE;
	}

	void run() {
		final long l0 = System.currentTimeMillis();
		final Display display = Display.getDefault();
		this.sh = new Shell(this.getStyle());
		this.sh.setText(this.getName());
		this.sh.setVisible(false);
		this.sh.setRedraw(false);
		this.createUI(this.sh);
		if (this.getSize() != null) {
			this.sh.setSize(this.getSize());
		} else {
			this.sh.pack();
		}

		final Shell activeShell = display.getActiveShell();
		if (activeShell != null) {
			final Point location = activeShell.getLocation();
			final Point size = activeShell.getSize();
			location.x += size.x + activeShell.getBorderWidth() + 3;
			this.sh.setLocation(location);
		}
		final long l1 = System.currentTimeMillis();
		System.out
				.println("UI Creation time " + this.getName() + "=" + (l1 - l0) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.sh.open();
		this.sh.setRedraw(true);
		this.sh.setVisible(true);
		while (!this.sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected Point getSize() {
		return null;
	}

	protected void createUI(Composite comp) {
		final RootElement cm = new RootElement(comp);

		cm.add(UIElementFactory.createHeaderLabel(this.getName()));
		cm.add(UIElementFactory.createRichLabel(this.getDescription()));
		final AbstractLayouter el = new OneElementOnLineLayouter();
		final AbstractUIElement<?> createContent = this.createContent();
		cm.add(createContent);
		el.elementCreated(cm);
	}

	protected abstract AbstractUIElement<?> createContent();

	protected abstract String getName();

	protected abstract String getDescription();

	public void close() {
		if (!this.sh.isDisposed()) {
			this.sh.dispose();

		}
	}

	public void activate() {
		if (!this.sh.isDisposed()) {
			this.sh.setActive();
		} else {
			this.run();
		}
	}

	public abstract String getGroup();
}