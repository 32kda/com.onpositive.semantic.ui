package com.onpositive.semantic.model.ui.generic.widgets;

import java.io.Serializable;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;

public interface ISelectionListener extends Serializable{

	void selectionChanged(IStructuredSelection selection);
}
