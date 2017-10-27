package com.onpositive.semantic.model.ui.property.editors;

import org.eclipse.jface.text.source.SourceViewer;

import com.onpositive.commons.elements.AbstractUIElement;


public interface IViewerTextElement
{
	public SourceViewer getSourceViewer();
	
	public AbstractUIElement<?> getElement();
}
