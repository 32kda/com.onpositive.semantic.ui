package com.onpositive.ui.simpleapp;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.xml.language.DOMEvaluator;

/**
 * This class controls all aspects of the application's execution
 */
@SuppressWarnings("restriction")
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();
		try {
			String initialPerspectiveId = null;
			String title = "Application";
//			try {
//				ContributionHelper.setContext(this);
//				Object evaluated = DOMEvaluator.getInstance().evaluateLocalPluginResource(Application.class,"app.dlf",null);
//				if (evaluated instanceof ApplicationElement) {
//					ContributionHelper.setApplicationElement((ApplicationElement) evaluated);
//				}
//				initialPerspectiveId = ContributionHelper.getPerspectiveId(evaluated);
//				title = ContributionHelper.getApplicationTitle(evaluated);
//				IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
//				IElementContributor elementContributor = ElementContributorFactory.getElementContributor(evaluated.getClass());
//				elementContributor.contribute(evaluated,(ExtensionRegistry) extensionRegistry);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor(initialPerspectiveId, title));
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
