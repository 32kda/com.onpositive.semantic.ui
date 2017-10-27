package com.onpositive.semantic.ui.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SWTTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = Display.getDefault();
		final Shell sh = new Shell(SWT.SHELL_TRIM);
		sh.setText("Hello");
		sh.setVisible(false);
		sh.setRedraw(false);
		createUI(sh);
		sh.pack();
		final Shell activeShell = display.getActiveShell();
		if (activeShell != null) {
			final Point location = activeShell.getLocation();
			final Point size = activeShell.getSize();
			location.x += size.x + activeShell.getBorderWidth() + 3;
			sh.setLocation(location);
		}
		sh.open();
		sh.setRedraw(true);
		sh.setVisible(true);
		while (!sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void createUI(Shell sh) {
		final long l0 = System.currentTimeMillis();
		sh.setLayout(new FillLayout());
		sh.setBackground(new Color(sh.getDisplay(), 255, 255, 255));
		final ToolBar tm = new ToolBar(sh, SWT.NO_BACKGROUND);
		tm.setBackground(sh.getBackground());
		final ToolItem ts = new ToolItem(tm, SWT.NONE);
		ts.setText("AA");
		final FormToolkit toolkit = new FormToolkit(sh.getDisplay());
		final Form frm = toolkit.createForm(sh);
		frm.setText("Hello");
		frm.getBody().setLayout(new GridLayout(1, false));
		toolkit.setBorderStyle(SWT.NULL);
		final Table t = toolkit.createTable(frm.getBody(), SWT.NONE);
		toolkit.paintBordersFor(t);
		final Label ls = new Label(frm.getBody(), SWT.NONE);
		toolkit.paintBordersFor(frm.getBody());
		ls.setText("AA");
		ls.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				System.out.println("Painting");
			}

		});
		final long l1 = System.currentTimeMillis();
		System.out.println("Timing:" + (l1 - l0));
	}

}
