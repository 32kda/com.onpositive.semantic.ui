package com.onpositive.businessdroids.ui.dataview.renderers;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.format.DateUtils;

public class PrettyFormat {

	private static GregorianCalendar temp = new GregorianCalendar();

	static GregorianCalendar z = new GregorianCalendar();
	static int lastYear = PrettyFormat.z.get(GregorianCalendar.YEAR);
	static int lastMonth = PrettyFormat.z.get(GregorianCalendar.MONTH);
	static int week = PrettyFormat.z.get(GregorianCalendar.WEEK_OF_YEAR);
	static int date = PrettyFormat.z.get(GregorianCalendar.DAY_OF_WEEK);

	static DateFormat timeInstance = DateFormat
			.getTimeInstance(DateFormat.SHORT);

	// public static Date parseDate(String dateString) {
	// if (dateString.equals("Today")) {
	// GregorianCalendar m = new GregorianCalendar();
	// m.add(GregorianCalendar.DAY_OF_YEAR, 0);
	// m.set(GregorianCalendar.HOUR_OF_DAY, 0);
	// m.set(GregorianCalendar.MINUTE, 0);
	// m.set(GregorianCalendar.SECOND, 0);
	// m.set(GregorianCalendar.MILLISECOND, 0);
	// return m.getTime();
	// }
	// if (dateString.equals("Tomorrow")) {
	// GregorianCalendar m = new GregorianCalendar();
	// m.add(GregorianCalendar.DAY_OF_YEAR, 1);
	// m.set(GregorianCalendar.HOUR_OF_DAY, 0);
	// m.set(GregorianCalendar.MINUTE, 0);
	// m.set(GregorianCalendar.SECOND, 0);
	// m.set(GregorianCalendar.MILLISECOND, 0);
	// return m.getTime();
	// }
	// if (dateString.equals("This week")) {
	// GregorianCalendar m = new GregorianCalendar();
	// int actualMaximum = m
	// .getActualMaximum(GregorianCalendar.DAY_OF_WEEK);
	// m.set(GregorianCalendar.DAY_OF_WEEK, actualMaximum);
	// m.set(GregorianCalendar.HOUR_OF_DAY, 0);
	// m.set(GregorianCalendar.MINUTE, 0);
	// m.set(GregorianCalendar.SECOND, 0);
	// m.set(GregorianCalendar.MILLISECOND, 0);
	// return m.getTime();
	// }
	// if (dateString.equals("This month")) {
	// GregorianCalendar m = new GregorianCalendar();
	// int actualMaximum = m
	// .getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
	// m.set(GregorianCalendar.DAY_OF_MONTH, actualMaximum);
	// m.set(GregorianCalendar.HOUR_OF_DAY, 0);
	// m.set(GregorianCalendar.MINUTE, 0);
	// m.set(GregorianCalendar.SECOND, 0);
	// m.set(GregorianCalendar.MILLISECOND, 0);
	// return m.getTime();
	// }
	// GregorianCalendar m = new GregorianCalendar();
	//
	// Map<String, Integer> displayNames = m.getDisplayNames(
	// GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG,
	// Locale.getDefault());
	// for (String s : displayNames.keySet()) {
	// if (s.equals(dateString)) {
	// int actualMaximum = displayNames.get(s);
	//
	// m.set(GregorianCalendar.DAY_OF_WEEK, actualMaximum);
	// GregorianCalendar gregorianCalendar = new GregorianCalendar();
	// if (gregorianCalendar.get(GregorianCalendar.DAY_OF_YEAR) >= m
	// .get(GregorianCalendar.DAY_OF_YEAR)) {
	// m.add(GregorianCalendar.WEEK_OF_YEAR, 1);
	// }
	//
	// m.set(GregorianCalendar.HOUR_OF_DAY, 0);
	// m.set(GregorianCalendar.MINUTE, 0);
	// m.set(GregorianCalendar.SECOND, 0);
	// m.set(GregorianCalendar.MILLISECOND, 0);
	// return m.getTime();
	// }
	// }
	// // m.get(GregorianCalendar.DAY_OF_WEEK);
	// if (dateString.trim().length() == 0) {
	// return null;
	// }
	// try {
	// return timeInstance.parse(dateString.trim());
	// } catch (ParseException e) {
	// try {
	// return DefaultPropertyMetadata.DEFAULT_DATE_FORMAT
	// .parse(dateString.trim());
	// } catch (ParseException e1) {
	// }
	// }
	// try {
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd,MMMM");
	// Date parse = simpleDateFormat.parse(dateString.trim());
	// GregorianCalendar s = new GregorianCalendar();
	// int i = s.get(GregorianCalendar.YEAR);
	// s.setTime(parse);
	// s.set(GregorianCalendar.YEAR, i);
	// return s.getTime();
	// } catch (ParseException e) {
	//
	// }
	// try {
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM,yy");
	// Date parse = simpleDateFormat.parse(dateString.trim());
	// return parse;
	// } catch (ParseException e) {
	//
	// }
	// return null;
	// }

	public static void initDates() {
		PrettyFormat.z = new GregorianCalendar();
		PrettyFormat.lastYear = PrettyFormat.z.get(Calendar.YEAR);
		PrettyFormat.lastMonth = PrettyFormat.z.get(Calendar.MONTH);
		PrettyFormat.week = PrettyFormat.z.get(Calendar.WEEK_OF_YEAR);
		PrettyFormat.date = PrettyFormat.z.get(Calendar.DAY_OF_WEEK);
	}

	public static String format(Object element2, boolean shortF) {
		if (element2 instanceof Collection) {
			@SuppressWarnings("rawtypes")
			Collection c = (Collection) element2;
			if (c.size() == 1) {
				return PrettyFormat.format(c.iterator().next(), shortF);
			}
			StringBuilder bld = new StringBuilder();
			int p = c.size();
			int a = 0;
			for (Object o : c) {
				bld.append(PrettyFormat.format(o, shortF));
				if (a != p - 1) {
					bld.append(", ");
				}
				a++;
			}
			return bld.toString();
		}
		if (element2 instanceof Number) {
			Number m = (Number) element2;
			if (m.doubleValue() == 0) {
				return "";
			}
			return NumberFormat.getInstance().format(element2);
		}
		if (element2 instanceof Date) {
			Date dt = (Date) element2;
			if (!shortF) {
				return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dt);
			}
			PrettyFormat.temp.setTime(dt);
			int i = PrettyFormat.temp.get(Calendar.YEAR);
			int k = PrettyFormat.z.get(Calendar.DAY_OF_YEAR);
			int de = PrettyFormat.temp.get(Calendar.DAY_OF_YEAR);
			if (k + 1 == de) {
				return "Tomorrow";
			}
			if (k - 1 == de) {
				return "Yestarday";
			}
			if (i != PrettyFormat.lastYear) {
				return PrettyFormat.formatMonth() + ", " + i;
				// return temp.getDisplayName(GregorianCalendar.MONTH,
				// GregorianCalendar.LONG, Locale.getDefault()) + "," + i;
			}
			int j = PrettyFormat.temp.get(Calendar.WEEK_OF_YEAR);
			if (j != PrettyFormat.week) {
				if (j == PrettyFormat.week + 1) {
					PrettyFormat.formatWeek();
					// return temp.getDisplayName(GregorianCalendar.DAY_OF_WEEK,
					// GregorianCalendar.LONG, Locale.getDefault());
				}
				if (PrettyFormat.temp.get(Calendar.MONTH) >= PrettyFormat.lastMonth) {
					return PrettyFormat.temp.get(Calendar.DAY_OF_MONTH) + ", "
							+ PrettyFormat.formatMonth();
					// + temp.getDisplayName(GregorianCalendar.MONTH,
					// GregorianCalendar.LONG, Locale.getDefault());
				} else {
					return PrettyFormat.formatMonth() + ", "
							+ PrettyFormat.temp.get(Calendar.YEAR);
				}
			}
			int j2 = PrettyFormat.temp.get(Calendar.DAY_OF_WEEK);
			if (j2 == PrettyFormat.date) {
				if (PrettyFormat.temp.get(Calendar.HOUR_OF_DAY) == 0) {
					if (PrettyFormat.temp.get(Calendar.MINUTE) == 0) {
						return "Today";
					}
				}
				return PrettyFormat.timeInstance.format(element2);
			}
			if (de > k) {
				return PrettyFormat.formatWeek();
			} else {
				return PrettyFormat.temp.get(Calendar.DAY_OF_MONTH) + ", "
						+ PrettyFormat.formatMonth();
			}
			// return DateFormat.getDateTimeInstance(DateFormat.SHORT,
			// DateFormat.SHORT).format(element2);
		}
		if (element2 == null) {
			return "";
		}
		return element2.toString();
	}

	protected static String formatWeek() {
		return DateUtils.getDayOfWeekString(
				PrettyFormat.temp.get(Calendar.DAY_OF_WEEK),
				DateUtils.LENGTH_LONG);
	}

	protected static String formatMonth() {
		return DateUtils.getMonthString(PrettyFormat.temp.get(Calendar.MONTH),
				DateUtils.LENGTH_LONG);
	}

}
