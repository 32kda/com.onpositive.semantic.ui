package com.onpositive.commons.ui.appearance;

import java.util.HashSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.semantic.ui.core.Alignment;
import com.onpositive.semantic.ui.core.Dimension;
import com.onpositive.semantic.ui.core.GenericLayoutHints;

public abstract class AbstractLayouter implements IContainerLayoutManager {

	protected HashSet<AbstractUIElement<?>> toExclude = new HashSet<AbstractUIElement<?>>();

	public Composite getLabelParent(Container container,
			AbstractUIElement<?> element) {
		return container.getContentParent();
	}

	public void registerLabelPropogation(AbstractUIElement<?> element) {

	}

	public void elementCreated(Container element) {
		this.doLayout(element);
	}

	public AbstractLayouter() {
		super();
	}

	public void elementAdded(Container cnt, AbstractUIElement<?> element) {
		if (cnt.isCreated()) {
			if (cnt.getParent() != null) {
				final IContainerLayoutManager layoutManager = cnt.getParent()
						.getLayoutManager();
				if (layoutManager != null) {
					layoutManager.elementAdded(cnt.getParent(), element);
				}
			}
			this.doLayout(cnt);
		}
	}

	public void elementAdding(Container container, AbstractUIElement<?> element) {

	}

	public void elementDisposed(Container element) {

	}

	public void elementRemoved(Container cnt, AbstractUIElement<?> element) {
		if (cnt.getParent() != null) {
			final IContainerLayoutManager layoutManager = cnt.getParent()
					.getLayoutManager();
			if (layoutManager != null) {
				layoutManager.elementRemoved(cnt.getParent(), element);
			}
		}
		this.doLayout(cnt);
	}

	protected abstract void doLayout(Container cm);

	public void excludeFromLayout(AbstractUIElement<?> element) {
		this.toExclude.add(element);
	}

	protected void calcLayout(Container cm, int mn) {
		for (final AbstractUIElement<?> wrapper : cm.getChildren()) {
			if (this.toExclude.contains(wrapper)) {
				continue;
			}
			final Control[] allControls = wrapper.getAllControls();

			for (int a = 0; a < allControls.length; a++) {

				final Control control = allControls[a];

				GridDataFactory defaultsFor2 = GridDataFactory
						.defaultsFor(control);

				if (a == allControls.length - 1) {
					if (mn != -1) {
						defaultsFor2.span(
								this.getSpan(wrapper, mn, allControls), 1);
					}
				}
				if (control instanceof Button) {
					if ((control.getStyle() & SWT.CHECK) != 0) {
						defaultsFor2.align(SWT.LEFT, SWT.TOP);
						defaultsFor2.indent(0, 4);
					} else {
						defaultsFor2.align(SWT.RIGHT, SWT.TOP);
					}
				} else if (control instanceof Text) {
					defaultsFor2.align(SWT.FILL, SWT.CENTER);
				} else if (control instanceof Spinner) {
					defaultsFor2.align(SWT.FILL, SWT.CENTER);
				} else if (control instanceof Label) {
					if ((control.getStyle() & SWT.SEPARATOR) != 0) {
						defaultsFor2.align(SWT.FILL, SWT.FILL);
					} else {
						defaultsFor2.align(SWT.LEFT, SWT.CENTER);
					}
				} else if (control instanceof Hyperlink) {
					defaultsFor2.align(SWT.LEFT, SWT.CENTER);
				} else if (control instanceof Combo) {
					defaultsFor2.align(SWT.FILL, SWT.CENTER);
				} else if (control instanceof Table) {
					defaultsFor2.align(SWT.FILL, SWT.FILL);
					defaultsFor2.grab(true, true);
				} else if (control instanceof Composite) {
					if (!(control instanceof FormText)) {
						defaultsFor2.align(SWT.FILL, SWT.FILL);
						if (wrapper instanceof Container) {
							final Container cma = (Container) wrapper;
							if (this.requireVertical(cma)) {
								defaultsFor2.grab(true, true);
							} else {
								defaultsFor2.grab(true, false);
							}
						} else {
							defaultsFor2.grab(true, true);
						}
					}

				}
				if (a > 0) { // Indentation after label
					if ((allControls[a - 1] instanceof Label)
							|| (allControls[a - 1] instanceof CLabel)
							|| (allControls[a - 1] instanceof Hyperlink)) {
						defaultsFor2.indent(5, 0);
					}
				}
				final Object layoutData = control.getLayoutData();
				if (layoutData instanceof GridData) {
					final GridData ds = (GridData) layoutData;
					defaultsFor2 = defaultsFor2.minSize(ds.minimumWidth,
							ds.minimumHeight);
					defaultsFor2 = defaultsFor2.hint(ds.widthHint,
							ds.heightHint);
				}
				GridData create = defaultsFor2.create();
				if (control.equals(wrapper.getLayoutControl())) {
					final GenericLayoutHints layoutHints = wrapper
							.getLayoutHints();
					create = apply(layoutHints,create);
				}
				control.setLayoutData(create);
			}
		}
	}

	protected boolean requireVertical(Container cm) {
		GenericLayoutHints layoutHints = cm.getLayoutHints();
		final boolean b = layoutHints != null;
		if (b) {
			final Boolean grabVertical = layoutHints.getGrabVertical();
			if (grabVertical != null) {
				return grabVertical;
			}
		}
		for (final AbstractUIElement<?> wapper : cm.getChildren()) {
			if (this.toExclude.contains(wapper)) {
				continue;
			}
			final Control[] allControls = wapper.getAllControls();
			for (int a = 0; a < allControls.length; a++) {

				final Control control = allControls[a];
				final GridDataFactory defaultsFor2 = GridDataFactory
						.defaultsFor(control);
				if (control instanceof Table) {
					defaultsFor2.grab(true, true);
				} else if (control instanceof Composite) {
					if (!(control instanceof FormText)) {
						defaultsFor2.align(SWT.FILL, SWT.FILL);
						defaultsFor2.grab(true, true);
					}
				}
				if (a > 0) {
					if ((allControls[a - 1] instanceof Label)
							|| (allControls[a - 1] instanceof CLabel)
							|| (allControls[a - 1] instanceof Hyperlink)) {
						defaultsFor2.indent(5, 0);
					}
				}
				final GridData create = defaultsFor2.create();

				if (control.equals(wapper.getControl())) {
					if (wapper instanceof Container) {
						final Container cma = (Container) wapper;
						if (this.requireVertical(cma)) {
							return true;
						} else {
							create.grabExcessVerticalSpace = false;
						}
					} else {
						layoutHints = wapper.getLayoutHints();
						apply(layoutHints,create);
					}
				}
				if (create.grabExcessVerticalSpace) {
					return true;
				}

			}

		}
		return false;
	}

	protected int getSpan(AbstractUIElement<?> el, int mn, Control[] allControls) {
		return mn - allControls.length + 1;
	}

	public GridLayout adaptLayout(Container cs, GridLayout or) {
		final Rectangle margin = cs.getMargin();
		if (margin != null) {
			final GridLayout ls = or;
			ls.marginHeight = 0;
			ls.marginWidth = 0;
			ls.marginLeft = margin.x;
			ls.marginTop = margin.y;
			ls.marginRight = margin.width;
			ls.marginBottom = margin.height;
			return ls;
		}
		return or;
	}

	public void install(Container container) {

	}

	public void uninstall(Container container) {

	}

	public GridData apply(GenericLayoutHints hints, GridData data) {
		if (hints.getGrabHorizontal() != null) {
			data.grabExcessHorizontalSpace = hints.getGrabHorizontal();
		}
		if (hints.getGrabVertical() != null) {
			data.grabExcessVerticalSpace = hints.getGrabVertical();
		}
		if (hints.getIndent() != null) {
			Point p=convert(hints.getIndent());
			data.horizontalIndent = p.x;
			data.verticalIndent = p.y;
		}
		if (hints.getSpan() != null) {
			Point p=convert(hints.getSpan());
			data.horizontalSpan =p.x;
			data.verticalSpan = p.y;
		}
		if (hints.getAlignmentHorizontal() != null) {
			data.horizontalAlignment = convertAlign(hints.getAlignmentHorizontal());
		}
		if (hints.getAlignmentVertical() != null) {
			data.verticalAlignment = convertAlign(hints.getAlignmentVertical());
		}
		if (hints.getMinimumSize() != null) {
			Point p=convert(hints.getMinimumSize());
			data.minimumWidth = p.x;
			data.minimumHeight = p.y;
		}
		if (hints.getHint() != null) {
			Point p=convert(hints.getHint());
			data.heightHint = p.y;
			data.widthHint = p.x;
		}
		return data;
	}
	
	
	private int convertAlign(int alignmentHorizontal) {
		switch (alignmentHorizontal) {
		case Alignment.LEFT:
			return GridData.BEGINNING;		
		case Alignment.RIGHT:
			return GridData.END;
		case Alignment.FILL:
			return GridData.FILL;
		case Alignment.TOP:
			return GridData.BEGINNING;
		case Alignment.BOTTOM:
			return GridData.END;
		case Alignment.CENTER:
			return GridData.CENTER;
		default:
			break;
		}
		return 0;
	}


	static boolean dluInited;
	private int baseUnitX;
	private int baseUnitY;

	private Point convert(com.onpositive.semantic.ui.core.Point span) {
		if (!dluInited){
			GC gc=new GC(Display.getDefault());			
			 Point size = gc.textExtent("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", 52);
			 baseUnitX = (size.x / 26 + 1) / 2;
			 baseUnitY = gc.getFontMetrics().getHeight();			 
			 gc.dispose();
		}
		int x=(int) (span.horizontal.unit==Dimension.UNIT_DLU?span.horizontal.value*baseUnitX:span.horizontal.value);
		int y=(int) (span.vertical.unit==Dimension.UNIT_DLU?span.vertical.value*baseUnitY:span.vertical.value);
		return new Point(x,y);
	}
}