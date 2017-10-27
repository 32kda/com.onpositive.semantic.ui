package com.onpositive.ui.simpleapp;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "com.onpositive.ui.simpleapp.perspective";
	private final String initialPerspectiveId;
	private final String appTitle;

	public ApplicationWorkbenchAdvisor(String initialPerspectiveId, String appTitle) {
		this.initialPerspectiveId = initialPerspectiveId;
		this.appTitle = appTitle;
	}

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer, appTitle);
	}

	public String getInitialWindowPerspectiveId() {
		if (initialPerspectiveId != null)
			return initialPerspectiveId;
		return PERSPECTIVE_ID;
	}

}
