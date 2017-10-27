package com.onpositive.commons.elements;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;

public class SashElement extends Container {

	private boolean horizontal;
	private int[] weights;

	public int[] getWeights() {
		return this.weights;
	}

	public SashElement() {
		getLayoutHints().setGrabHorizontal(true);
		getLayoutHints().setGrabVertical(true);
	}

	
	public void add(AbstractUIElement<?> element) {
		super.add(element);
		if (isCreated()) {
			MDSashForm frm = (MDSashForm) getControl();
			frm.hookSashListeners();
		}
	}

	public boolean isGroup() {
		return true;
	}

	@HandlesAttributeDirectly("weights")
	public void setWeights(int[] weights) {
		this.weights = weights;
		if (this.isCreated()) {
			if (weights != null) {
				this.getControl().setWeights(weights);
			}
		}
	}

	public boolean isHorizontal() {
		return this.horizontal;
	}

	private void onSashPaint(Event e) {
		Sash sash = (Sash) e.widget;
		FormToolkit toolkit = getService(FormToolkit.class);
		if (toolkit != null) {
			FormColors colors = toolkit.getColors();
//			boolean vertical = (sash.getStyle() & SWT.VERTICAL) != 0;
			GC gc = e.gc;
//			Boolean hover = (Boolean) sash.getData("hover"); //$NON-NLS-1$
			gc.setBackground(colors.getColor(IFormColors.TB_BG));
			gc.setForeground(colors.getColor(IFormColors.TB_BORDER));
			Point size = sash.getSize();
			
			gc.fillRectangle(0, 0, size.x, size.y);
		}
	}

	class MDSashForm extends SashForm {
		ArrayList sashes = new ArrayList();
		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseEnter:
					e.widget.setData("hover", Boolean.TRUE); //$NON-NLS-1$
					((Control) e.widget).redraw();
					break;
				case SWT.MouseExit:
					e.widget.setData("hover", null); //$NON-NLS-1$
					((Control) e.widget).redraw();
					break;
				case SWT.Paint:
					onSashPaint(e);
					break;
				case SWT.Resize:
					hookSashListeners();
					break;
				}
			}
		};

		public MDSashForm(Composite parent, int style) {
			super(parent, style);
		}

		public void layout(boolean changed) {
			super.layout(changed);
			hookSashListeners();
		}

		public void layout(Control[] children) {
			super.layout(children);
			hookSashListeners();
		}

		private void hookSashListeners() {
			purgeSashes();
			Control[] children = getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i] instanceof Sash) {
					Sash sash = (Sash) children[i];
					if (sashes.contains(sash))
						continue;
					sash.addListener(SWT.Paint, listener);
					sash.addListener(SWT.MouseEnter, listener);
					sash.addListener(SWT.MouseExit, listener);
					sashes.add(sash);
				}
			}
		}

		private void purgeSashes() {
			for (Iterator iter = sashes.iterator(); iter.hasNext();) {
				Sash sash = (Sash) iter.next();
				if (sash.isDisposed())
					iter.remove();
			}
		}
	}

	public void internalLoadConfiguration(IAbstractConfiguration configuration) {
		final String str = configuration.getStringAttribute("horizontal");
		if ((str != null) && (str.length() > 0)) {
			final boolean booleanAttribute = Boolean.parseBoolean(str);
			if (this.horizontal != booleanAttribute) {
				this.setHorizontal(booleanAttribute);
			}
		}
		final String[] stringArrayAttribute = configuration
				.getStringArrayAttribute("weights"); //$NON-NLS-1$
		if ((stringArrayAttribute != null) && (stringArrayAttribute.length > 0)) {
			final int[] ws = new int[stringArrayAttribute.length];
			for (int a = 0; a < stringArrayAttribute.length; a++) {
				ws[a] = Integer.parseInt(stringArrayAttribute[a]);
			}
			this.setWeights(ws);
		}
		super.loadConfiguration(configuration);
	}

	public void internalStoreConfiguration(IAbstractConfiguration configuration) {
		configuration.setBooleanAttribute("horizontal", this.horizontal); //$NON-NLS-1$
		final int[] weights2 = this.getControl().getWeights();
		final String[] ws = new String[weights2.length];
		for (int a = 0; a < ws.length; a++) {
			ws[a] = Integer.toString(weights2[a]);
		}
		configuration.setStringArrayAttribute("weights", ws); //$NON-NLS-1$
		super.storeConfiguration(configuration);
	}

	protected Composite createControl(Composite conComposite) {
		final MDSashForm sashForm = new MDSashForm(conComposite, this
				.calcStyle()
				| (this.horizontal ? SWT.HORIZONTAL : SWT.VERTICAL));
		System.out.println(sashForm.getOrientation() == SWT.HORIZONTAL);
		sashForm.hookSashListeners();
		return sashForm;
	}

	public void create() {
		super.create();
		if (this.weights != null) {
			try {
				this.getControl().setWeights(this.weights);
			} catch (final IllegalArgumentException e) {
				// silently ignore
			}
		}
		final MDSashForm s = (MDSashForm) getControl();
		s.addListener(SWT.Resize, new Listener() {

			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				s.layout(true);
				Rectangle bounds = s.getBounds();
				if (bounds.width > 1) {
					s.removeListener(SWT.Resize, this);
				}
			}

		});

	}

	public SashForm getControl() {
		return (SashForm) this.widget;
	}
	
	@HandlesAttributeDirectly("horizontal")
	public void setHorizontal(boolean isHorizontal) {
		this.horizontal = isHorizontal;
		if (this.isCreated()) {
			this.getControl().setOrientation(
					(this.horizontal ? SWT.HORIZONTAL : SWT.VERTICAL));
		}
	}
}
