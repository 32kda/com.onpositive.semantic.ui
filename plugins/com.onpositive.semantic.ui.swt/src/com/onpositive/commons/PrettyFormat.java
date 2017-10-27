package com.onpositive.commons;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import com.onpositive.semantic.model.api.property.DefaultPropertyMetadata;

public class PrettyFormat {
	
	static GregorianCalendar z=new GregorianCalendar();
	static int lastYear=z.get(GregorianCalendar.YEAR);
	static int lastMonth=z.get(GregorianCalendar.MONTH);
	static int week=z.get(GregorianCalendar.WEEK_OF_YEAR);
	static int date=z.get(GregorianCalendar.DAY_OF_WEEK);

	static DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT);
	
	public static Date parseDate(String tl){
 		if (tl.equals("Today")){
			GregorianCalendar m=new GregorianCalendar();
			m.add(GregorianCalendar.DAY_OF_YEAR, 0);
			m.set(GregorianCalendar.HOUR_OF_DAY, 0);
			m.set(GregorianCalendar.MINUTE, 0);
			m.set(GregorianCalendar.SECOND, 0);
			m.set(GregorianCalendar.MILLISECOND, 0);
			return m.getTime();
		}
		if (tl.equals("Tomorrow")){
			GregorianCalendar m=new GregorianCalendar();
			m.add(GregorianCalendar.DAY_OF_YEAR, 1);
			m.set(GregorianCalendar.HOUR_OF_DAY, 0);
			m.set(GregorianCalendar.MINUTE, 0);
			m.set(GregorianCalendar.SECOND, 0);
			m.set(GregorianCalendar.MILLISECOND, 0);
			return m.getTime();
		}
		if (tl.equals("This week")){
			GregorianCalendar m=new GregorianCalendar();
			int actualMaximum = m.getActualMaximum(GregorianCalendar.DAY_OF_WEEK);
			m.set(GregorianCalendar.DAY_OF_WEEK, actualMaximum);			
			m.set(GregorianCalendar.HOUR_OF_DAY, 0);
			m.set(GregorianCalendar.MINUTE, 0);
			m.set(GregorianCalendar.SECOND, 0);
			m.set(GregorianCalendar.MILLISECOND, 0);
			return m.getTime();
		}
		if (tl.equals("This month")){
			GregorianCalendar m=new GregorianCalendar();
			int actualMaximum = m.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);			
			m.set(GregorianCalendar.DAY_OF_MONTH, actualMaximum);			
			m.set(GregorianCalendar.HOUR_OF_DAY, 0);
			m.set(GregorianCalendar.MINUTE, 0);
			m.set(GregorianCalendar.SECOND, 0);
			m.set(GregorianCalendar.MILLISECOND, 0);
			return m.getTime();
		}
		GregorianCalendar m=new GregorianCalendar();
		
			Map<String, Integer> displayNames = m.getDisplayNames(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG,Locale.getDefault());
			for (String s:displayNames.keySet()){
				if (s.equals(tl)){
					int actualMaximum =displayNames.get(s);
					
					m.set(GregorianCalendar.DAY_OF_WEEK, actualMaximum);
					GregorianCalendar gregorianCalendar = new GregorianCalendar();
					if (gregorianCalendar.get(GregorianCalendar.DAY_OF_YEAR)>=m.get(GregorianCalendar.DAY_OF_YEAR)){
						m.add(GregorianCalendar.WEEK_OF_YEAR,1);
					}
					
					m.set(GregorianCalendar.HOUR_OF_DAY, 0);
					m.set(GregorianCalendar.MINUTE, 0);
					m.set(GregorianCalendar.SECOND, 0);
					m.set(GregorianCalendar.MILLISECOND, 0);	
					return m.getTime();
				}
			}
			//m.get(GregorianCalendar.DAY_OF_WEEK);
		if (tl.trim().length()==0){
			return null;
		}	
		try{
		return timeInstance.parse(tl.trim());
		}catch (ParseException e) {
			try {
				return DefaultPropertyMetadata.DEFAULT_DATE_FORMAT.parse(tl.trim());
			} catch (ParseException e1) {			
			}
		}
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd,MMMM");			
			Date parse = simpleDateFormat.parse(tl.trim());
			GregorianCalendar s=new GregorianCalendar();
			int i = s.get(GregorianCalendar.YEAR);
			s.setTime(parse);
			s.set(GregorianCalendar.YEAR,i);
			return s.getTime();
		} catch (ParseException e) {
			
		}
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM,yy");			
			Date parse = simpleDateFormat.parse(tl.trim());
			return parse;
		} catch (ParseException e) {
			
		}
		return null;
	}
	
	public static void initDates(){
		z=new GregorianCalendar();
		lastYear=z.get(GregorianCalendar.YEAR);
		lastMonth=z.get(GregorianCalendar.MONTH);
		week=z.get(GregorianCalendar.WEEK_OF_YEAR);
		date=z.get(GregorianCalendar.DAY_OF_WEEK);
	}
		
	private static GregorianCalendar temp=new GregorianCalendar();
	public static String format(Object element2,boolean shortF) {
		if (element2 instanceof Collection){
			Collection c=(Collection) element2;
			if (c.size()==1){
				return format(c.iterator().next(),shortF);
			}
			StringBuilder bld=new StringBuilder();
			int p=c.size();
			int a=0;
			for (Object o:c){
				bld.append(PrettyFormat.format(o, shortF));
				if (a!=p-1){
					bld.append(", ");
				}
				a++;
			}
			return bld.toString();
		}
		if (element2 instanceof Number){
			Number m=(Number) element2;
			if (m.doubleValue()==0){
				return "";
			}
			return NumberFormat.getInstance().format(element2);
		}
		if (element2 instanceof Date){
			Date dt=(Date) element2;
			if(!shortF){
				return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dt);
			}
			temp.setTime(dt);
			int i = temp.get(GregorianCalendar.YEAR);
			int k = z.get(GregorianCalendar.DAY_OF_YEAR);
			int de = temp.get(GregorianCalendar.DAY_OF_YEAR);
			if (k+1==de){
				return "Tomorrow";
			}			
			if (k-1==de){
				return "Yestarday";
			}
			if (i!=lastYear){								
				return temp.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.getDefault())+","+i;
			}
			int j = temp.get(GregorianCalendar.WEEK_OF_YEAR);
			if (j!=week){
				if (j==week+1){
					return temp.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG, Locale.getDefault());
				}
				if (temp.get(GregorianCalendar.MONTH)>=lastMonth){
					return temp.get(GregorianCalendar.DAY_OF_MONTH)+", "+temp.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.getDefault());
				}
				else{
					return temp.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.getDefault())+", "+temp.get(GregorianCalendar.YEAR);
				}
			}
			int j2 = temp.get(GregorianCalendar.DAY_OF_WEEK);
			if (j2==date){
				if (temp.get(GregorianCalendar.HOUR_OF_DAY)==0){
					if (temp.get(GregorianCalendar.MINUTE)==0){
						return "Today";
					}
				}			
				return timeInstance.format(element2);
			}
			if (de>k){
				return temp.getDisplayName(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG, Locale.getDefault());
			}
			else{
				return temp.get(GregorianCalendar.DAY_OF_MONTH)+", "+temp.getDisplayName(GregorianCalendar.MONTH, GregorianCalendar.LONG, Locale.getDefault());				
			}
			//return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(element2);
		}
		if (element2==null){
			return "";
		}
		return element2.toString();
	}

}
