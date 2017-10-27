package com.onpositive.commons.ui.dialogs;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.semantic.model.api.property.IHasStatus;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.ui.core.IHasErrorDecoration;

public class ElementDialog extends BasicPopupDialog {

	private final class LabelStatusListener implements IStatusChangeListener {
		String title;
		String oldMessage;
		Color color;

		public void statusChanged(IBinding bnd, CodeAndMessage cm) {
			if (this.title == null) {
				this.color = ElementDialog.this.label.getForeground();
				this.title = ElementDialog.this.label.getText();
			}
			if (ElementDialog.this.label == null) {
				return;
			}
			boolean needLayout = false;
			final String message = cm.getMessage();
			if ((message == null) || (message.length() == 0)) {
				ElementDialog.this.label.setText(this.title);
				ElementDialog.this.label.setForeground(this.color);
				if (((this.oldMessage == null) || ((this.title != null) && (this.title
						.length() > this.oldMessage.length())))) {
					needLayout = true;
				}
				this.oldMessage = this.title;
				// title = ""; //$NON-NLS-1$
				if (needLayout) {
					ElementDialog.this.getShell().layout(true, true);
					ElementDialog.this.getShell().pack();
				}
			} else {

				if (((this.oldMessage == null) || ((message != null) && (message
						.length() > this.oldMessage.length())))) {
					needLayout = true;
				}
				this.oldMessage = message;
				if (this.title == null) {
					this.color = ElementDialog.this.label.getForeground();
					this.title = ElementDialog.this.label.getText();
				}
				ElementDialog.this.label.setText(message);
				if (cm.getCode() == IStatus.ERROR) {
					ElementDialog.this.label
							.setForeground(ElementDialog.this.label
									.getDisplay().getSystemColor(SWT.COLOR_RED));

				} else {
					ElementDialog.this.label.setForeground(this.color);
				}
				if (needLayout) {
					ElementDialog.this.getShell().layout(true, true);
					ElementDialog.this.getShell().pack();
				}
			}

		}
	}

	private final AbstractUIElement<?> element;
	private Label label;
	private final IStatusChangeListener listener;
	private final IHasStatus status;

	public ElementDialog(IPropertyEditor<? extends AbstractUIElement<?>> editor) {
		this(editor.getBinding(), editor.getUIElement(), editor.getBinding()
				.getName(), MessageFormat.format(
				"Configure {0}", editor.getBinding().getName())); //$NON-NLS-1$		
	}

	public ElementDialog(IHasStatus status, AbstractUIElement<?> editor,
			String title, String description) {
		super(title, description);
		this.element = editor;
		this.listener = new LabelStatusListener();
		this.status = status;
	}
	
	public ElementDialog(IHasStatus status, AbstractUIElement<?> editor,
			String title, String description, boolean modal) {
		super(title, description, modal?SWT.APPLICATION_MODAL:SWT.NONE);
		this.element = editor;
		this.listener = new LabelStatusListener();
		this.status = status;
	}

	public boolean close() {
		this.status.removeStatusChangeListener(this.listener);
		return super.close();
	}

	public Control createControl(Composite c) {
		final RootElement root = new RootElement(c);
		this.element.setCaption(null);
		final LinkedHashSet<Control> editors = new LinkedHashSet<Control>();
		this.disallowHowers(this.element, editors);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		root.setLayout(layout);
		root.add(this.element);
		root.setLayoutData(new GridData(GridData.FILL_BOTH));
		final LinkedList<Control> ls = new LinkedList<Control>();
		this.fillEditors(root, ls);
		
		// Shell shell = getShell();
		// Control[] la=shell.getChildren();
		// shell.setTabList(new Control[]{la[0]});
		// ((Composite)la[0]).setTabList(new
		// Control[]{(((Composite)la[0]).getChildren()[0])});

		// ls.getLast().getParent().setTabList(new
		// Control[]{ls.getFirst(),ls.getLast()});
		final Composite contentParent = root.getContentParent();
		Control dd = this.element.getControl();
		while (dd.getParent()!=contentParent){
			dd=dd.getParent();
		}
		//if(dd.getParent()==contentParent){
			contentParent.setTabList(new Control[] { dd });
		//}
		return contentParent;
	}

	private void fillEditors(AbstractUIElement<?> element2,
			LinkedList<Control> ls) {
		if (element2 instanceof ICompositeElement) {
			final ICompositeElement<AbstractUIElement<?>,?> c = (ICompositeElement) element2;
			for (final AbstractUIElement<?> w : c.getChildren()) {
				this.fillEditors(w, ls);
			}
		} else if (element2 instanceof IPropertyEditor<?>) {
			ls.add(element2.getControl());
		}
	}

	private void disallowHowers(AbstractUIElement<?> element2,
			HashSet<Control> editors) {
		if (element2 instanceof ICompositeElement) {
			final ICompositeElement<AbstractUIElement<?>,?> c = (ICompositeElement) element2;
			for (final AbstractUIElement<?> w : c.getChildren()) {
				this.disallowHowers(w, editors);
			}
		}
		if (element2 instanceof IHasErrorDecoration) {
			((IHasErrorDecoration) element2).setShowHoverOnError(false);
		}
	}

	protected Control createInfoTextArea(Composite parent) {
		final Composite ca = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ca.setLayout(layout);
		ca.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.label = (Label) super.createInfoTextArea(ca);
		this.label.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				true, true));
		this.status.addStatusChangeListener(this.listener);
		this.listener.statusChanged(null, this.status.getStatus());
		// ToolBarManager tm=new ToolBarManager(SWT.NONE);
		// tm.add(new Action("Ok",Action.AS_PUSH_BUTTON){});
		// ToolBar createControl = tm.createControl(ca);
		this.getShell().pack();
		return this.label;
	}

	public AbstractUIElement<?> getElement() {
		return this.element;
	}
}
