package com.onpositive.semantic.ui.labels;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;

public class DateLabelProvider implements ITextLabelProvider {

	public DateLabelProvider() {
	}

	
	public String getDescription(Object object) {
		return DateFormat.getDateTimeInstance().format(object);
	}
	
	public static boolean isSameDay(GregorianCalendar cal,GregorianCalendar cal1){
		boolean sameYear = isSameYear(cal, cal1);		
		int i = cal.get(GregorianCalendar.DAY_OF_YEAR);
		int j = cal1.get(GregorianCalendar.DAY_OF_YEAR);
		return sameYear&&(i==j);
	}

	public static boolean isSameYear(GregorianCalendar cal,
			GregorianCalendar cal1) {
		boolean sameYear = cal.get(GregorianCalendar.YEAR) == cal1
		.get(GregorianCalendar.YEAR);
		return sameYear;
	}
	
	public static boolean isSameWeek(GregorianCalendar cal,GregorianCalendar cal1){
		boolean sameYear = isSameYear(cal, cal1);		
		int i = cal.get(GregorianCalendar.WEEK_OF_YEAR);
		int j = cal1.get(GregorianCalendar.WEEK_OF_YEAR);
		return sameYear&&(i==j);			
	}
	
	public static boolean isSameMonth(GregorianCalendar cal,GregorianCalendar cal1){
		boolean sameYear = isSameYear(cal, cal1);		
		int i = cal.get(GregorianCalendar.MONTH);
		int j = cal1.get(GregorianCalendar.MONTH);
		return sameYear&&(i==j);			
	}
	
	

	
	public String getText(Object object) {
		Date ds = (Date) object;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(ds);

		GregorianCalendar calendar1 = new GregorianCalendar();
		int i = calendar.get(GregorianCalendar.DAY_OF_YEAR);
		int j = calendar1.get(GregorianCalendar.DAY_OF_YEAR);
		boolean sameYear = isSameYear(calendar, calendar1);
		if (i == j) {
			if (sameYear) {
				return DateFormat.getTimeInstance(DateFormat.SHORT).format(ds);
			}
		}
		if (sameYear) {
			boolean sameWeek = calendar.get(GregorianCalendar.WEEK_OF_YEAR) == calendar1
					.get(GregorianCalendar.WEEK_OF_YEAR);
			if (sameWeek) {
//				return calendar.getDisplayName(GregorianCalendar.DAY_OF_WEEK,
//						Calendar.LONG, Locale.getDefault())
//						+ ' '
//						+ DateFormat.getTimeInstance(DateFormat.SHORT).format(
//								ds);
			}
			return DateFormat.getDateInstance(DateFormat.SHORT).format(ds);
		}
		return DateFormat.getDateInstance(DateFormat.SHORT).format(ds);
	}

}
