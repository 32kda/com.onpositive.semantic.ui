package com.onpositive.semantic.model.ui.generic.widgets.impl;

import java.util.Date;
import java.util.GregorianCalendar;

import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.widgets.IDateTimeEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class DateTimeDelegate extends EditorDelegate {

	private GregorianCalendar calendar = new GregorianCalendar();
	
	private boolean useCalendar = false;

	public DateTimeDelegate(BasicUIElement<?> element) {
		super(element);
	}

	@Override
	public void processValueChange(ISetDelta<?> valueElements) {
		if (!valueElements.getAddedElements().isEmpty()) {
			setValue(valueElements.getAddedElements()
					.iterator().next());
		} else {
			if (!valueElements.getChangedElements().isEmpty()) {
				setValue(valueElements
						.getChangedElements().iterator().next());
			} else {
				if (!valueElements.getRemovedElements().isEmpty()) {
					setValue(null);
				}
			}
		}

	}
	
	@Override
	public void setValue(Object value) {
		GregorianCalendar newCalendar = null;
		if (value instanceof Date) {
			Date d = (Date) value;
			newCalendar = new GregorianCalendar();
			newCalendar.setTime(d);
			useCalendar = false;
		} else if (value instanceof GregorianCalendar) {
			newCalendar = (GregorianCalendar) value;
			useCalendar = true;
		}
		if (newCalendar != null) {
			calendar = (GregorianCalendar) newCalendar.clone();
			((IDateTimeEditor<?>)ui).setCalendar(calendar);
		}
	}

	@Override
	public void internalSetBinding(IBinding binding) {
		if (binding != null) {
			final Object value = binding.getValue();
			this.setValue(value);
		}
	}

	@Override
	public void handleChange(IUIElement<?> b, Object value) {
		IBinding binding = ui.getBinding();
		if (binding != null) {
			doCommit(ui.getBinding());
		}
	}

	private void doCommit(IBinding binding) {
		GregorianCalendar calendar2 = ((IDateTimeEditor<?>)ui).getCalendar();
		calendar = (GregorianCalendar) calendar2.clone();
		ui.getBinding().setValue(useCalendar?calendar2:calendar2.getTime());
	}

	public final Date getTime() {
		return calendar.getTime();
	}

	public final void setTime(Date date) {
		calendar.setTime(date);
	}

	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	public void setTimeInMillis(long millis) {
		calendar.setTimeInMillis(millis);
	}

	public GregorianCalendar getCalendar() {
		return calendar;
	}

	public void setCalendar(GregorianCalendar calendar) {
		this.calendar = calendar;
	}

	public boolean isUseCalendar() {
		return useCalendar;
	}

	public void setUseCalendar(boolean useCalendar) {
		this.useCalendar = useCalendar;
	}
	
	public final void set(int year, int month, int date) {
		calendar.set(year, month, date);
	}
	
	public final void setTime(int hourOfDay, int minute) {
		calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(GregorianCalendar.MINUTE, minute);
	}

	public int getYear(){
		return calendar.get(GregorianCalendar.YEAR);
	}
	
	public int getMonth(){
		return calendar.get(GregorianCalendar.MONTH);
	}
	
	public int getDayOfMonth(){
		return calendar.get(GregorianCalendar.DAY_OF_MONTH);
	}
	
	public int getHourOfDay(){
		return calendar.get(GregorianCalendar.HOUR_OF_DAY);
	}
	
	public int getMinute(){
		return calendar.get(GregorianCalendar.MINUTE);
	}

	public CharSequence getFormattedValue() {
		
		return null;
	}



}
