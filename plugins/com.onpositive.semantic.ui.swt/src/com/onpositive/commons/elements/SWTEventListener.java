package com.onpositive.commons.elements;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

public interface SWTEventListener<T extends Control> {

	void handleEvent(AbstractUIElement<T> element, Event event);
}
