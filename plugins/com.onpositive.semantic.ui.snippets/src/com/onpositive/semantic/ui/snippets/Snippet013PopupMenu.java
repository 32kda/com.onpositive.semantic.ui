package com.onpositive.semantic.ui.snippets;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.TabFolderElement;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.InputElementDialog;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.OneLineTextElement;

public class Snippet013PopupMenu extends AbstractSnippet {

	
	protected Point getSize() {
		return new Point(300, 300);
	}

	
	protected AbstractUIElement<?> createContent() {
		final Container cm = new Container();
		cm.setLayoutManager(new OneElementOnLineLayouter());
		final TabFolderElement fld = new TabFolderElement();
		final Container partOne = new Container();
		partOne.setLayout(new GridLayout(2, false));

		partOne.setCaption("General"); //$NON-NLS-1$
		fld.add(partOne);
		partOne.setCreatePopupMenu(true);
		final IMenuManager popupMenuManager = partOne.getActualPopupMenuManager();
		popupMenuManager.add(new Action("Add Text Field") { //$NON-NLS-1$
					
					public void run() {
						final OneLineTextElement<String> text = new OneLineTextElement<String>();
						text.setCaption("Label"); //$NON-NLS-1$
						partOne.add(text);
						Snippet013PopupMenu.this.editCaption(text);
					}
				});
		popupMenuManager.add(new Action("Add Check Box") { //$NON-NLS-1$
					
					public void run() {
						final ButtonSelector createText = new ButtonSelector(null,
								SWT.CHECK);
						createText.setCaption("Check Box"); //$NON-NLS-1$
						partOne.add(createText);
						Snippet013PopupMenu.this.editCaption(createText);
					}
				});
		popupMenuManager.add(new Action("Add Button") { //$NON-NLS-1$
					
					public void run() {
						final ButtonSelector createText = new ButtonSelector();
						createText.setCaption("Button"); //$NON-NLS-1$
						partOne.add(createText);
						Snippet013PopupMenu.this.editCaption(createText);
					}
				});
		popupMenuManager.add(new Action("Rename Tab") { //$NON-NLS-1$
					
					public void run() {
						Snippet013PopupMenu.this.editCaption(partOne);
					}
				});
		cm.add(fld);

		return cm;
	}

	
	protected String getDescription() {
		return "This snippet shows how to setup popup menu"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Popup Menu Sample"; //$NON-NLS-1$
	}

	private void editCaption(AbstractUIElement createText) {
		final Binding binding = new Binding(createText);
		final Binding binding2 = binding.getBinding("Caption"); //$NON-NLS-1$
		final InputElementDialog elementDialog = new InputElementDialog(binding2,
				new OneLineTextElement<String>(binding2),
				"Caption", "Please specify caption"); //$NON-NLS-1$ //$NON-NLS-2$
		elementDialog.open();
		binding.dispose();
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}

}
