package com.onpositive.semantic.ui.snippets;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class SnippetsList implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		new SnippetList().run();

		return null;
	}

	public void stop() {

	}

}
