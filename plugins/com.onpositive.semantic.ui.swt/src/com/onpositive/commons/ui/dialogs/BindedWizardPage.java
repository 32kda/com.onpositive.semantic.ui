package com.onpositive.commons.ui.dialogs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.onpositive.commons.Activator;
import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.IHasStatus;
import com.onpositive.semantic.model.api.property.IStatusChangeListener;
import com.onpositive.semantic.model.api.roles.ImageManager;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.api.roles.RoleManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.CodeAndMessage;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.ui.help.HelpSystem;

public class BindedWizardPage extends WizardPage {

	protected AbstractUIElement<?> element;
	public AbstractUIElement<?> getElement() {
		return element;
	}

	public void setElement(AbstractUIElement<?> element) {
		this.element = element;
	}

	protected IHasStatus bnd;
	protected String title;
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected String description;
	protected String role;
	protected String theme;
	protected IWizardPageListener wlistener;
	private LabelStatusListener listener;
	
	public void setWizardPageListener(IWizardPageListener listener){
		wlistener=listener;
	}
	
	public IWizardPageListener getWizardPageListener(){
		return wlistener;
	}

	public BindedWizardPage(Binding bnd, AbstractUIElement<?> element) {
		super("");
		this.element = element;
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
		this.bnd = bnd;
	}
	
	public BindedWizardPage(String name,Binding bnd, AbstractUIElement<?> element) {
		super(name);
		this.element = element;
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
		this.bnd = bnd;
	}

	public BindedWizardPage(IHasStatus bnd, AbstractUIElement<?> element,
			String title, String description) {
		super(title);
		this.element = element;
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
		this.bnd = bnd;
		this.title = title;
		this.description = description;
	}

	public BindedWizardPage(IPropertyEditor<AbstractUIElement> editor) {
		super("");
		this.element = (AbstractUIElement<?>) editor.getUIElement();
		this.bnd = editor.getBinding();
		this.title = editor.getBinding().getName();
		this.description = editor.getBinding().getDescription();
		if (element.getCaption().length() > 0) {
			this.setTitle(element.getCaption());
		}
	}

	private final class LabelStatusListener implements IStatusChangeListener {

		public void statusChanged(IBinding bnd, CodeAndMessage cm) {
			boolean foundError = false;
			for (IBinding m : mb) {
				
				CodeAndMessage status = m.getStatus();
				if (status.getCode() == IStatus.ERROR) {
					String message = status.getMessage();
					BindedWizardPage.this.setErrorMessage(message);
					foundError = true;
					break;
				}

			}
			if (!foundError) {
				setErrorMessage(null);
			}
			setPageComplete(foundError);
		}
	}

	protected Control createDialogArea(Composite parent) {
		// final Control createDialogArea = super.createDialogArea(parent);
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
		if (this.bnd instanceof Binding) {
			if (getWizard().getDefaultPageImage()==null)
			{
			final Binding n = (Binding) this.bnd;
			final Object value = n.getValue();
			final ImageDescriptor image = SWTImageManager.getImageDescriptor( ImageManager.getInstance()
					.getImageDescriptor(value, this.getRole(), this.getTheme()));
			
			if (image != null) {
				this.setImageDescriptor(image);
			}
			}
		}
		final RootElement cm = new RootElement((Composite) parent);
		
		// fillBindings(this.element);
		cm.add(this.element);
		this.listener = new LabelStatusListener();

		if (element instanceof Container) {
			Container c = (Container) element;
			List<AbstractUIElement<?>> children = c.getChildren();
			for (AbstractUIElement<?> m : children) {
				fillBindings(m);
			}
		}
		//mb.add((IBinding) getBinding());
		for (IBinding m : mb) {
			m.addStatusChangeListener(this.listener);
			m.addValueListener(new IValueListener(){

				public void valueChanged(Object oldValue, Object newValue) {
					onChanged();
				}
			});
		}
		for (IBinding m : mb) {
			this.listener.statusChanged(m, m.getStatus());
			break;
		}
		// createDialogArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		cm.getContentParent().setLayoutData(new GridData(GridData.FILL_BOTH));
		if (helpContext!=null){
			HelpSystem.setHelp(cm.getContentParent(),helpContext);
		}	
		return cm.getControl();
	}

	protected void onChanged() {
		if(getWizard() instanceof BindedWizard){
		((BindedWizard)getWizard()).onChanged();
		}
	}

	private LinkedHashSet<IBinding> mb = new LinkedHashSet<IBinding>();
	private IDialogSettings settings;
	private String helpContext;

	private void fillBindings(AbstractUIElement<?> element2) {
		if (element2 instanceof Container) {
			Container c = (Container) element2;
			List<AbstractUIElement<?>> children = c.getChildren();
			for (AbstractUIElement<?> m : children) {
				fillBindings(m);
			}
		}
		if (element2 instanceof IPropertyEditor) {
			IPropertyEditor<?> pa = (IPropertyEditor<?>) element2;
			IBinding binding = pa.getBinding();
			if (binding != null) {
				mb.add(binding);
			}
		}
		if (element2 instanceof ListEnumeratedValueSelector<?>){
			ListEnumeratedValueSelector<?>s=(ListEnumeratedValueSelector<?>) element2;
			IBinding selectionBinding = s.getSelectionBinding();
			if (selectionBinding!=null){
				mb.add(selectionBinding);
			}
		}
	}

	
	public boolean isPageComplete() {
		for (IBinding bnd : mb) {
			boolean b = bnd.getStatus().getCode() == IStatus.ERROR;
			if (b) {
				return false;
			}
		}
		return true;
	}

	public void create() {
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

	public void createControl(Composite parent) {
		setControl(createDialogArea(parent));
	}

	
	public IHasStatus getBinding(){
		return bnd;
	}
	
	public boolean beforeNext() {
		if (wlistener!=null){
			return wlistener.beforeNext(this);
		}
		return true;
	}

	public boolean beforeBack() {
		if (wlistener!=null){
			return wlistener.beforeBack(this);
		}
		return true;
	}
	
	
	public IDialogSettings getDialogSettings() {
		return settings;
	}

	

	public void setDialogSettingsFile(File file) {
		this.settings = new DialogSettings("");
		try {
			settings.load(file.getAbsolutePath());
		} catch (IOException e) {
			Activator.log(e);
		}
	}

	public void setHelpContext(String hh) {
		this.helpContext = hh;
	}

	public void setBinding(Binding binding) {
		// TODO Auto-generated method stub
		
	}
}