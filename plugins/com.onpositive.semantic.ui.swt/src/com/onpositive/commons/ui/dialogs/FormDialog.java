package com.onpositive.commons.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.HorizontalLayouter;
import com.onpositive.semantic.model.api.property.IHasStatus;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.api.roles.RoleManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.IDisplayable;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.ui.core.Alignment;

public class FormDialog extends Dialog implements IDisplayable {

	protected FormEditor element;
	protected IHasStatus bnd;
	protected String title;
	protected String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected String role;
	protected String theme;
	private LabelStatusListener listener;
	private ButtonSelector okButton;
	private int mode = EDIT;

	public final int getMode() {
		return mode;
	}

	public final void setMode(int mode) {
		this.mode = mode;
	}

	public static final int CREATE = 1;
	public static final int EDIT = 1;
	public static final int VIEW = 2;

	public FormDialog(Binding bnd, FormEditor element) {
		super(Display.getCurrent().getActiveShell());
		setShellStyle(getShellStyle() | SWT.APPLICATION_MODAL);
		this.element = element;
		this.bnd = bnd;
	}

	protected Control createContents(Composite parent) {
		Composite cm = new Composite(parent, SWT.NONE);
		createDialogArea(cm);
		GridLayout layout = new GridLayout(1, false);
		cm.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		cm.setLayoutData(new GridData(GridData.FILL_BOTH));
		// parent.setLayout(layout);
		return cm;
	}

	public FormDialog(IHasStatus bnd, FormEditor element, String title,
			String description) {
		super(Display.getCurrent().getActiveShell());
		this.element = element;
		this.bnd = bnd;
		this.title = title;
		this.description = description;
	}

	protected Rectangle getConstrainedShellBounds(Rectangle preferredSize) {
		Rectangle r = preferredSize;
		int w = r.width;
		int h = r.height;
		if (w < 400) {
			w = 400;
		}
		if (h < 300) {
			h = 300;
		}
		return new Rectangle(r.x, r.y, w, h);
	}

	/**
	 * Initializes the location and size of this window's SWT shell after it has
	 * been created.
	 * <p>
	 * This framework method is called by the <code>create</code> framework
	 * method. The default implementation calls <code>getInitialSize</code> and
	 * <code>getInitialLocation</code> and passes the results to
	 * <code>Shell.setBounds</code>. This is only done if the bounds of the
	 * shell have not already been modified. Subclasses may extend or
	 * reimplement.
	 * </p>
	 */
	protected void initializeBounds() {

		Point size = getInitialSize();
		Point location = getInitialLocation(size);
		Rectangle constrainedShellBounds = getConstrainedShellBounds(new Rectangle(
				location.x, location.y, size.x, size.y));
		location = getInitialLocation(new Point(constrainedShellBounds.width,
				constrainedShellBounds.height));
		constrainedShellBounds = getConstrainedShellBounds(new Rectangle(
				location.x, location.y, constrainedShellBounds.width,
				constrainedShellBounds.height));
		getShell().setBounds(constrainedShellBounds);
	}

	public FormDialog(FormEditor editor, int mode) {
		super(Display.getCurrent().getActiveShell());
		this.mode = mode;
		this.element = editor;
		this.bnd = editor.getBinding();
		this.title = editor.getBinding().getName();
		this.description = editor.getBinding().getDescription();
		this.setBlockOnOpen(true);
	}

	private final class LabelStatusListener implements IStatusChangeListener {

		Color color;

		public void statusChanged(IBinding bnd, CodeAndMessage cm) {
			final boolean b = (cm.getCode() == IStatus.ERROR);
			// final Button button = FormDialog.this.getButton(Window.OK);
			// if (button != null) {
			// button.setEnabled(!b);
			// }
			okButton.setEnabled(!b);
			if (b) {
				FormDialog.this.element.getControl().setMessage(
						cm.getMessage(), IMessageProvider.ERROR);
			} else {
				FormDialog.this.element.getControl().setMessage(null,
						IMessageProvider.ERROR);
				if (cm.getMessage() != null) {
					FormDialog.this.setMessage(cm.getMessage());
				} else {
					FormDialog.this.setMessage(FormDialog.this.description);
				}
			}
		}
	}

	/**
	 * Notifies that the window's close button was pressed, the close menu was
	 * selected, or the ESCAPE key pressed.
	 * <p>
	 * The default implementation of this framework method sets the window's
	 * return code to <code>CANCEL</code> and closes the window using
	 * <code>close</code>. Subclasses may extend or reimplement.
	 * </p>
	 */
	protected void handleShellCloseEvent() {
		close();
	}

	protected Control createDialogArea(Composite parent) {
		final Control createDialogArea = parent;
		Container cont = new Container();
		okButton = new ButtonSelector();
		if (mode == CREATE) {
			okButton.setCaption("Finish");
		}
		if (mode == EDIT) {
			okButton.setCaption("Finish");
		}

		ButtonSelector sel1 = new ButtonSelector();

		sel1.setCaption("Cancel");
		if (mode != VIEW) {
			cont.add(okButton);
		} else {
			sel1.setCaption("Close");
		}
		cont.getLayoutHints().setGrabVertical(false);
		cont.getLayoutHints().setAlignmentVertical(SWT.BOTTOM);
		cont.add(sel1);
		setReturnCode(Dialog.CANCEL);
		okButton.addListener(SWT.Selection, new SWTEventListener<Button>() {

			public void handleEvent(AbstractUIElement<Button> element,
					Event event) {
				setReturnCode(Dialog.OK);
				try {
					FormDialog.this.element.getBinding().commit();
					getShell().close();
				} catch (Exception e) {
					((FormEditor) FormDialog.this.element).getControl()
							.setMessage(e.getMessage(), IMessageProvider.ERROR);
					setReturnCode(Dialog.CANCEL);
					return;
				}
				// getShell().dispose();
			}

		});
		getShell().setDefaultButton(okButton.getControl());
		sel1.addListener(SWT.Selection, new SWTEventListener<Button>() {

			public void handleEvent(AbstractUIElement<Button> element,
					Event event) {
				setReturnCode(Dialog.CANCEL);
				getShell().close();
				// getShell().dispose();
			}

		});
		cont.getLayoutHints().setAlignmentHorizontal(Alignment.RIGHT);
		cont.setMargin(new com.onpositive.semantic.ui.core.Rectangle(0, 0, 0, 10));
		cont.setLayoutManager(new HorizontalLayouter());
		this.element.add(cont);
		this.element.getLayoutHints().setGrabVertical(true);
		this.element.getLayoutHints().setAlignmentVertical(Alignment.FILL);
		this.element.setLayout(new FillLayout());
		final RootElement cm = new RootElement((Composite) createDialogArea);
		cm.add(this.element);
		if (this.role == null) {
			if (this.bnd instanceof Binding) {
				final Binding n = (Binding) this.bnd;
				final Object value = n.getValue();
				if (value != null) {
					this.role = RoleManager.EDIT_ROLE;
					final IRealm<Object> realm = n.getRealm();
					if (realm != null) {
						if (!realm.contains(n)) {
							this.role = RoleManager.ADD_ROLE;
						}

					}
				} else {
					this.role = RoleManager.NEW_ROLE;
				}
			}
		}
		if (this.title != null) {
			this.setTitle(this.title);
		} else if (this.bnd instanceof Binding) {
			final Binding n = (Binding) this.bnd;
			this.setTitle(LabelManager.getInstance().getText(n.getValue(),
					this.getRole(), this.getTheme()));
		}
		if (this.description != null) {
			this.setMessage(this.description);
		} else if (this.bnd instanceof Binding) {
			final Binding n = (Binding) this.bnd;
			final String description2 = LabelManager.getInstance()
					.getDescription(n.getValue(), this.getRole(),
							this.getTheme());
			this.description = description2;
			this.setMessage(description2);
		}

		this.listener = new LabelStatusListener();

		this.bnd.addStatusChangeListener(this.listener);
		// cm.getContentParent().setLayout(new FillLayout());
		cm.getContentParent().setLayoutData(new GridData(GridData.FILL_BOTH));
		element.getContentParent().setRedraw(true);
		element.getContentParent().layout(true, true);

		return parent;
	}

	private void setTitle(String title2) {
		getShell().setText(title2);
		element.getControl().setText(title2);
	}

	private void setMessage(String description2) {
		this.element.getControl().setMessage(description2);
	}

	public boolean close() {
		this.bnd.removeStatusChangeListener(this.listener);
		return super.close();
	}

	public void create() {
		super.create();
		this.listener.statusChanged(null, this.bnd.getStatus());
		if (this.title != null && this.title.length() > 0) {
			this.getShell().setText(this.title);
		} else if (this.bnd instanceof Binding) {
			final Binding n = (Binding) this.bnd;
			final String name = LabelManager.getInstance().getText(
					n.getValue(), this.getRole(), this.getTheme());
			if (name != null) {
				this.getShell().setText(name);
			}
			if (n.getName()!=null&&n.getName().length()>0){
				this.getShell().setText(n.getName());
			}
		}
		if (this.description == null || this.description.length() == 0) {
			if (this.bnd instanceof Binding) {
				final Binding n = (Binding) this.bnd;
				String caption = element.getCaption();
				if (caption==null||caption.length()==0){
					element.setCaption(n.getDescription());
				}
			}
		}
		this.getShell().layout(true, true);
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public boolean isModal() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.commons.ui.dialogs.IDisplayable#openWidget()
	 */
	public int openWidget() {
		return open();
	}
}