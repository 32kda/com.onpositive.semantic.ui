package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.ui.generic.widgets.IDecoratableEditor;

public abstract class DecoratableEditor<T, A extends Control> extends
		AbstractEditor<A> implements IDecoratableEditor<A>{

	private final class MyDecoration extends ControlDecoration {
		private MyDecoration(Control control, int position) {
			super(control, position);
		}

		protected void update() {
			if (created){
				super.update();
			}
		}
	}

	private static final boolean INSTALL_ERROR_DECORATION = true;
	protected ControlDecoration errorDecoration;

	private FieldDecoration getErrorDecoration() {
		final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
		final FieldDecoration dec = registry
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		return dec;
	}

	private FieldDecoration getRequiredDecoration() {
		final FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
		final FieldDecoration dec = registry
				.getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
		return dec;
	}

	private boolean showHoverOnError = false;
	private boolean installRequiredDecoration = true;
	protected ControlDecoration requiredDecoration;
	protected IStatusChangeListener bindingStatusListener;
	protected FocusListener focusListenr;

	public DecoratableEditor() {
		super();
	}

	public DecoratableEditor(int style) {
		super(style);
	}

	public void dispose() {
		this.errorDecoration = null;
		this.requiredDecoration = null;
		super.dispose();
	}

	boolean created;
	
	protected void installDecorations(final Control text) {
		// Cache the handler service so we don't have to retrieve it each time
		try{
		final IBinding binding2 = this.getBinding();
		if (this.INSTALL_ERROR_DECORATION) {
			// Note top left is used for compatibility with 3.2, although
			// this may change to center alignment in the future.
			if (this.errorDecoration == null) {
				this.errorDecoration = new MyDecoration(text, SWT.TOP
						| SWT.RIGHT);
			}
			this.errorDecoration.setShowOnlyOnFocus(true);
			final FieldDecoration dec = this.getErrorDecoration();
			this.errorDecoration.setImage(dec.getImage());
			this.errorDecoration.setDescriptionText(dec.getDescription());
			this.errorDecoration.setMarginWidth(1);
			this.errorDecoration.hide();
			final KeyListener listener2 = new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 27) {
						DecoratableEditor.this.errorDecoration.hideHover();
					}
				}

				public void keyReleased(KeyEvent e) {
				}

			};
			this.focusListenr = new FocusListener() {

				public void focusGained(FocusEvent e) {
					if (binding2 != null) {
						DecoratableEditor.this.resetErrorStatus(text, listener2, binding2.getStatus());
					}
				}

				public void focusLost(FocusEvent e) {
					DecoratableEditor.this.errorDecoration.hideHover();
				}
			};
			text.addFocusListener(this.focusListenr);
			// text.getShell().addFocusListener(focusListenr);
			if (binding2 != null) {
				this.bindingStatusListener = new IStatusChangeListener() {

					public void statusChanged(IBinding bnd, CodeAndMessage cm) {
						DecoratableEditor.this.resetErrorStatus(text, listener2, cm);
					}
				};
				binding2.addStatusChangeListener(this.bindingStatusListener);
				this.resetErrorStatus(text, listener2, binding2.getStatus());
			}
		}
		// Cache the handler service so we don't have to retrieve it each time
		if (this.installRequiredDecoration && (binding2 != null)) {
			// Note top left is used for compatibility with 3.2, although
			// this may change to center alignment in the future.
			final Control c = this.getLabelControl();
			if (this.requiredDecoration == null) {
				this.requiredDecoration = c != null ? new MyDecoration(c,
						SWT.TOP | SWT.RIGHT) : new MyDecoration(text,
						SWT.BOTTOM | SWT.LEFT);
			}
			this.requiredDecoration.setShowOnlyOnFocus(false);
			final FieldDecoration dec = this.getRequiredDecoration();
			this.requiredDecoration.setImage(dec.getImage());
			this.requiredDecoration.setMarginWidth(1);
			// requiredDecoration.hide();
			this.requiredDecoration.setDescriptionText(dec.getDescription());

		}
		if (this.installRequiredDecoration) {
			if (binding2 != null) {
				if (!binding2.isRequired()) {
					this.requiredDecoration.hide();
				} else {
					this.requiredDecoration.show();
				}
			}
		}
		}finally{
			created=true;
		}
	}

	private void resetErrorStatus(final Control text,
			final KeyListener listener2, CodeAndMessage cm) {
		final Display current = Display.getCurrent();
		if (this.errorDecoration == null) {
			return;
		}

		if (current.getActiveShell() != text.getShell()) {
			return;
		}
		if (cm.getCode() == IStatus.ERROR) {
			this.errorDecoration.show();
			if (this.showHoverOnError) {

				// if (current.getFocusControl() != text) {
				// text.setFocus();
				// }
				this.errorDecoration.showHoverText(cm.getMessage());
				text.addKeyListener(listener2);
				// text.addFocusListener(ls);
			}
			this.errorDecoration.setDescriptionText(cm.getMessage());
		} else {
			this.errorDecoration.hide();
			this.errorDecoration.hideHover();
			text.removeKeyListener(listener2);
			// text.removeFocusListener(ls);
		}
	}

	public boolean isShowHoverOnError() {
		return this.showHoverOnError;
	}

	public void setShowHoverOnError(boolean showHoverOnError) {
		this.showHoverOnError = showHoverOnError;
	}

	protected void unhookBinding(IBinding old) {
		if (this.bindingStatusListener != null) {
			final IBinding binding2 = this.getBinding();
			if (binding2 != null) {
				binding2.removeStatusChangeListener(this.bindingStatusListener);
			}
		}
		if (this.focusListenr != null) {
			this.getControl().removeFocusListener(this.focusListenr);
		}
		super.unhookBinding(this.getBinding());
	}

	public boolean isInstallRequiredDecoration() {
		return this.installRequiredDecoration;
	}

	public void setInstallRequiredDecoration(boolean installRequiredDecoration) {
		this.installRequiredDecoration = installRequiredDecoration;
	}

}