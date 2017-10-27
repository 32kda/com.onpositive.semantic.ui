package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.api.property.IProperty;

public class TextCellEditorWithButton extends TextCellEditorWithDecoration{

	private Button bs;

	public TextCellEditorWithButton(Composite control, Object parent,
			IProperty property) {
		super(control, parent, property);
	}
	@Override
	protected void focusLost() {
		Display.getCurrent().asyncExec(new Runnable() {
			
			public void run() {
				if (bs.isDisposed()||text.isDisposed()){
					return;
				}
				if (!bs.isFocusControl()&&!text.isFocusControl()){
					if (Display.getCurrent().getActiveShell()==text.getShell()){
						acc();
					}
				}
				
			}
		});
		
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite createControl = (Composite) super.createControl(parent);
		bs = new Button(createControl,SWT.PUSH);
		bs.setText("...");
		GridLayout l= (GridLayout) createControl.getLayout();
		l.numColumns=2;
		l.makeColumnsEqualWidth=false;
		l.marginRight=0;
		l.marginLeft=0;
		l.horizontalSpacing=8;
		bs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				onButton();
				text.setFocus();
			}
		});
		bs.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		createControl.layout(true, true);
		return createControl;
	}
	protected void onButton() {
		
	}
	protected void acc() {
		super.focusLost();
	}
}
