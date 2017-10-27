package com.onpositive.semantic.ui.xml;

import org.eclipse.swt.SWT;
import org.w3c.dom.Element;

import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler;
import com.onpositive.semantic.model.ui.property.editors.DateTimeEditor;

public class DateTimeHandler extends UIElementHandler {

	protected Object createElement(Element element, Object parentContext,
			String localName) {
		String attribute = element.getAttribute("size");
		String type=element.getAttribute("type");
		DateTimeEditor dateTimeEditor = new DateTimeEditor();
		if (type.equals("time")){
			dateTimeEditor.setStyle(SWT.TIME);
		}
		else if (type.equals("date")){
			dateTimeEditor.setStyle(SWT.DATE);
		}
		else if (type.equals("calendar")){
			dateTimeEditor.setStyle(SWT.CALENDAR);
		}
		if (attribute.equals("short")){
			dateTimeEditor.setSize(SWT.SHORT);
		}
		else if (attribute.equals("medium")){
			dateTimeEditor.setSize(SWT.MEDIUM);
		}
		else if (attribute.equals("long")){
			dateTimeEditor.setSize(SWT.LONG);
		}
		return dateTimeEditor;		
	}
}
