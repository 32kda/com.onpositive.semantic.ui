package com.onpositive.semantic.model.ui.roles;



import java.io.Serializable;

import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.generic.IDisplayable;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;

public class DisplayableCreator implements IWidgetCreator,
		IContextWidgetCreator {

	private static final class DR implements Runnable,Serializable {
		private final Binding bnd;

		private DR(Binding bnd) {
			this.bnd = bnd;
		}

		@Override
		public void run() {
			bnd.dispose();					
		}
	}


	public void showWidget(final Binding bnd, WidgetObject object, String role) {
		try {
			showWidget(bnd,object);
		} catch (Exception e) {
			Platform.log(e);
		}
	}

	public static void showWidget(final Binding bnd,WidgetObject w) throws Exception {
		System.err.println("Starting to evaluate");
		try{
		Object evaluateLocalPluginResource =w.evaluate(bnd);
		
		System.err.println("evaluated");
		if (!(evaluateLocalPluginResource instanceof IDisplayable)) {
			Platform.log(new RuntimeException("Evaluation result for "
					+ w.getResource() + " is not instance of IDisplayable"));
		}
		IDisplayable ll = (IDisplayable) evaluateLocalPluginResource;
		ll.openWidget();
		addDispose(bnd, ll);
		}catch (Exception e) {
			e.printStackTrace(System.err);
			throw new IllegalStateException(e); 
			// TODO: handle exception
		}
	}

	protected static void addDispose(final Binding bnd, IDisplayable ll) {
		if (ll.isModal()) {
			bnd.dispose();
		}
		else{
			ll.addDisposeCallback(new DR(bnd));
		}
	}

	/**
	 * Opens a widget evaluated from specified dlf file(resource)
	 * 
	 * @param bnd
	 *            Binding for widget - it contains a value being edited
	 * @param clazz
	 *            class, which classloader is responsible for searching/loading
	 *            a resource. Resoure and class must be in the same plugin
	 * @param resource
	 *            Resorce for evaluating widget contents from. Normally - .dlf
	 *            file
	 * @return Widget opening result. By default - {@link Window#OK}
	 * @throws Exception
	 */
	public static int showWidget(final Binding bnd, @SuppressWarnings("rawtypes") Class clazz, String resource)
			throws Exception {
		Object evaluateLocalPluginResource = DOMEvaluator.getInstance()
				.evaluateLocalPluginResource(clazz, resource, bnd);
		IDisplayable ll = (IDisplayable) evaluateLocalPluginResource;
		int result = ll.openWidget();
		addDispose(bnd, ll);		
		return result;
	}


	public IUIElement<?> createWidget(Binding bnd, WidgetObject object) {
		Object evaluateLocalPluginResource = null;
		try {
			evaluateLocalPluginResource =object.evaluate(bnd);
			if (!(evaluateLocalPluginResource instanceof IUIElement<?>)) {
				Platform.log(new RuntimeException("Evaluation result for "
						+ object.getResource() + " is not instance of abstract UI element"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (IUIElement<?>) evaluateLocalPluginResource;
	}
}
