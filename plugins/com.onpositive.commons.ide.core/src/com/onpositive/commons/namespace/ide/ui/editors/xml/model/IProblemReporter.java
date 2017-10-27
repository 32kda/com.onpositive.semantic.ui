package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

public interface IProblemReporter {

	public void accept(int severity, int start, int end, String message);
}
