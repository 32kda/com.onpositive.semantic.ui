/**
 * 
 */
package com.onpositive.commons.ui.dialogs;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.IDisplayable;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.ui.help.HelpSystem;

public class BindedWizard extends Wizard implements IDisplayable {
	private final Binding bnd;

	IWizardListener wizardListener;

	private DialogSettings settings;

	private String helpContext;

	public final IWizardListener getWizardListener() {
		return wizardListener;
	}

	public final void setWizardListener(IWizardListener wizardListener) {
		this.wizardListener = wizardListener;
	}

	public BindedWizard(Binding bnd, String title) {
		this.bnd = bnd;
		setWindowTitle(title);
	}

	public boolean performFinish() {
		if (wizardListener != null) {
			if (!wizardListener.preFinish(this)) {

				return false;

			}

		}
		getBinding().commit();
		if (wizardListener != null) {
			return wizardListener.performFinish(this);
		}
		return true;
	}

	
	public boolean performCancel() {
		if (wizardListener != null) {
			return wizardListener.performCancel(this);
		}
		return super.performCancel();
	}
	
	public void addPage(CompositeEditor editor){
		this.addPage(new BindedWizardPage(editor));
	}
	@SuppressWarnings("unchecked")
	public IWizardPage addPage(String name,AbstractUIElement editor){
		BindedWizardPage page = new BindedWizardPage(name,(Binding) getBinding(),editor);
		this.addPage(page);
		return page;
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.onpositive.commons.ui.dialogs.IDisplayable#openWidget()
	 */
	public int openWidget() {
		WizardDialog dialog = new WizardDialog(Display.getCurrent()
				.getActiveShell(), this) {

			
			protected Control createDialogArea(Composite parent) {
				if (helpContext != null) {
					HelpSystem.setHelp(parent.getShell(), helpContext);
				}
				// TODO Auto-generated method stub
				return super.createDialogArea(parent);
			}

			/**
			 * The Next button has been pressed.
			 */
			protected void nextPressed() {
				BindedWizardPage pa = (BindedWizardPage) getCurrentPage();
				boolean beforeNext = pa.beforeNext();
				if (beforeNext){
					super.nextPressed();
				}
			}

			protected void backPressed() {
				BindedWizardPage pa = (BindedWizardPage) getCurrentPage();
				
				boolean beforeBack = pa.beforeBack();
				if (beforeBack){
					super.backPressed();
				}
			}

		};

		return dialog.open();
	}

	public boolean isModal() {
		return true;
	}

	
	public IDialogSettings getDialogSettings() {
		return settings;
	}

	public Binding getBinding() {
		return bnd;
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

	public void onChanged() {
		
	}
}