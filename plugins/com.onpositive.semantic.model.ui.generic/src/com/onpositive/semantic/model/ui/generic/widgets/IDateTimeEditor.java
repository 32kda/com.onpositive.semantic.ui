package com.onpositive.semantic.model.ui.generic.widgets;

import java.util.GregorianCalendar;

public interface IDateTimeEditor<T> extends IUIElement<T>, ICanBeReadOnly<T> {
	public void setCalendar(GregorianCalendar calendar);
	public GregorianCalendar getCalendar();
	public void setStyle(DateTimeEditorStyle style);
	public DateTimeEditorStyle getStyle();
}
