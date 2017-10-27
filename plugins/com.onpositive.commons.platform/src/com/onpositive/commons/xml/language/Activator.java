package com.onpositive.commons.xml.language;

public class Activator {

	public static void log(Exception e) {
		e.printStackTrace();
	}

	public static void logError(String string, String string2, Throwable object) {
		System.err.println(string+string2);
		object.printStackTrace();
	}

}
