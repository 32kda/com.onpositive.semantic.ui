package com.onpositive.commons.ui.dialogs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.Activator;
import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.semantic.model.api.property.IHasStatus;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.api.roles.RoleManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.IDisplayable;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.ui.help.HelpSystem;

public class TitledDialog extends TitleAreaDialog implements IDisplayable {

	protected AbstractUIElement<?> element;
	protected IHasStatus bnd;
	protected String title;
	protected String description;
	protected String role;
	protected String theme;
	private LabelStatusListener listener;
	private String image;
	private IDialogSettings settings;
	private File dlgSettingsFile;
	private String helpContext;

	protected IDialogSettings getDialogBoundsSettings() {
		return settings;
	}

	public void setDialogSettings(IDialogSettings settings) {
		this.settings = settings;
	}

	public TitledDialog(Binding bnd, AbstractUIElement<?> element) {
		super(Display.getCurrent().getActiveShell());
		this.element = element;
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
		this.bnd = bnd;
	}

	public TitledDialog(IHasStatus bnd, AbstractUIElement<?> element,
			String title, String description) {
		this(bnd, element, title, description, (String) null);
	}

	public TitledDialog(IPropertyEditor<AbstractUIElement<?>> editor) {
		super(Display.getCurrent().getActiveShell());
		this.element = editor.getUIElement();
		this.bnd = editor.getBinding();

		this.title = editor.getBinding().getName();
		this.description = editor.getBinding().getDescription();
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
	}

	public TitledDialog(IHasStatus bnd2, AbstractUIElement<?> evaluate,
			String attribute, String attribute2, String attribute3) {
		super(Display.getCurrent().getActiveShell());
		this.element = evaluate;
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
		this.bnd = bnd2;
		this.title = attribute;
		this.description = attribute2;
		this.image = attribute3;
	}

	private final class LabelStatusListener implements IStatusChangeListener {

		Color color;

		public void statusChanged(IBinding bnd, CodeAndMessage cm) {
			final boolean b = (cm.getCode() == IStatus.ERROR);
			final Button button = TitledDialog.this.getButton(Window.OK);
			if (button != null) {
				button.setEnabled(!b);
			}
			if (b) {
				TitledDialog.this.setErrorMessage(cm.getMessage());
			} else {
				TitledDialog.this.setErrorMessage(null);
			}
			if (cm.getMessage() != null) {
				TitledDialog.this.setMessage(cm.getMessage());
			} else {
				TitledDialog.this.setMessage(TitledDialog.this.description);
			}
		}
	}

	boolean isTitleImageSet;

	
	public void setTitleImage(Image newTitleImage) {
		isTitleImageSet = true;
		super.setTitleImage(newTitleImage);
	}

	protected Control createDialogArea(Composite parent) {
		final Control createDialogArea = super.createDialogArea(parent);
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
		if (this.title != null && title.length() > 0) {
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
		if (!isTitleImageSet) {
			if (this.image != null && image.length() > 0) {
				super.setTitleImage(SWTImageManager.getImage(image));
			} else {
				if (this.bnd instanceof Binding) {
					final Binding n = (Binding) this.bnd;
					final Object value = n.getValue();

					final Image image = SWTImageManager.getImage(value, this.getRole(), this.getTheme());
					if (image != null) {
						super.setTitleImage(image);
					}
				}
			}
		}
		final RootElement cm = new RootElement((Composite) createDialogArea);
		if (helpContext != null) {
			HelpSystem.setHelp(createDialogArea, helpContext);
		}
		cm.add(this.element);
		this.listener = new LabelStatusListener();

		this.bnd.addStatusChangeListener(this.listener);
		createDialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		cm.getContentParent().setLayoutData(new GridData(GridData.FILL_BOTH));
		return parent;
	}

	public boolean close() {
		this.bnd.removeStatusChangeListener(this.listener);
		boolean close = super.close();
		if (dlgSettingsFile != null) {
			try {
				settings.save(dlgSettingsFile.getAbsolutePath());
			} catch (IOException e) {
				Activator.log(e);
			}
		}
		return close;
	}

	public void create() {
		super.create();
		this.listener.statusChanged(null, this.bnd.getStatus());
		if (this.title != null) {
			this.getShell().setText(this.title);
		} else if (this.bnd instanceof Binding) {
			final Binding n = (Binding) this.bnd;
			final String name = LabelManager.getInstance().getText(
					n.getValue(), this.getRole(), this.getTheme());
			if (name != null) {
				this.getShell().setText(name);
			}
		}
		// getShell().pack();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.onpositive.commons.ui.dialogs.IDisplayable#openWidget()
	 */
	public int openWidget() {
		int open = this.open();
		if (open == OK) {
			if (bnd instanceof IBinding) {
				IBinding b = (IBinding) bnd;
				b.commit();
			}
		}
		return open;
	}

	public boolean isModal() {
		return true;
	}

	public void setResizable(boolean resizable) {

		if (resizable) {
			setShellStyle(getShellStyle() | SWT.RESIZE);
		} else {
			setShellStyle(getShellStyle() & ~SWT.RESIZE);
		}
	}

	public void setDialogSettingsFile(File file) {
		this.dlgSettingsFile = file;
		this.settings = new DialogSettings("");
		try {
			settings.load(file.getAbsolutePath());
		} 
		catch (FileNotFoundException e) {
			//ignore silently
		}
		catch (IOException e) {
			Activator.log(e);
		}
	}

	public void setHelpContext(String hh) {
		this.helpContext = hh;
	}
}