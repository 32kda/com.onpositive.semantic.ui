package com.onpositive.commons.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public abstract class BasicPopupDialog extends FocusedPopupDialog {

	protected Control control;

	public BasicPopupDialog(Shell parent, int shellStyle,
			boolean takeFocusOnOpen, boolean persistBounds,
			boolean showDialogMenu, boolean showPersistAction,
			String titleText, String infoText) {
		super(parent, shellStyle, takeFocusOnOpen, persistBounds,
				showDialogMenu, showPersistAction, titleText, infoText);
	}

	public BasicPopupDialog(String title, String info) {
		super(Display.getCurrent().getActiveShell(), SWT.NONE, true, true,
				false, false, title, info);
	}
	
	public BasicPopupDialog(String title, String info, int style) {
		super(Display.getCurrent().getActiveShell(), style, true, true,
				false, false, title, info);
	}

	boolean closeOnMouseOut;
	boolean wasInside;
	private Listener listener;

	public boolean isCloseOnMouseOut() {
		return closeOnMouseOut;
	}

	public void setCloseOnMouseOut(boolean closeOnMouseOut) {
		this.closeOnMouseOut = closeOnMouseOut;
	}

	protected Control createDialogArea(Composite parent) {
		final GridLayout gridLayout = new GridLayout(1, false);
		parent.setLayout(gridLayout);
		final Composite c = new Composite(parent, SWT.NONE);
		c.setBackground(parent.getBackground());
		final GridLayout gridLayout1 = new GridLayout(1, false);
		gridLayout1.marginHeight = 0;
		c.setLayout(gridLayout1);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.control = this.createControl(c);
		parent.pack();

		// parent.setLayoutData(layoutData);
		return c;
	}

	
	static BasicPopupDialog current;
	
	protected void adjustBounds() {
		if (current!=this&&current!=null){
			current.close();
		}
		current=this;
		super.adjustBounds();
		final Point cursorLocation = Display.getCurrent().getCursorLocation();
		int i = this.getShell().getSize().y
				/ 2;
		Rectangle bounds = Display.getCurrent().getBounds();
		if (cursorLocation.y + i * 2 > bounds.height) {
			cursorLocation.y -= i;
			cursorLocation.x += 15;
		} else {
			cursorLocation.y -= 10;
			cursorLocation.x -= 10;
		}
		this.getShell().setLocation(cursorLocation);
		if (closeOnMouseOut) {
			listener = new Listener() {

				public void handleEvent(Event event) {
					Widget widget = event.widget;
					if (widget instanceof Control) {
						Control c = (Control) widget;
						if (c.getShell()==getShell()){
							wasInside=true;
							return;
						}
						if (wasInside) {
							if (c.getShell() != getShell()) {
								Display.getCurrent().removeFilter(
										SWT.MouseEnter, this);
								close();
							}
						}
					}

				}
			};
			Display.getCurrent().addFilter(SWT.MouseEnter, listener);
			
		}
	}
	
	public boolean close() {
		if (closeOnMouseOut){
		Display.getCurrent().removeFilter(SWT.MouseEnter, listener);
		}
		current=null;
		return super.close();
	}

	public abstract Control createControl(Composite c);

	protected Control getFocusControl() {
		return this.control;
	}

}