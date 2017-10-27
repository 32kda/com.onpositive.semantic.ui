package com.onpositive.datamodel.model;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.onpositive.commons.Activator;
import com.onpositive.commons.platform.configuration.EquinoxBridge;
import com.onpositive.commons.xml.language.DOMEvaluator;

public final class ModelRegistry {

	private final HashMap<String, DataModel> loadedModels = new HashMap<String, DataModel>();
	static ModelRegistry instance;

	public DataModel getModel(String id) {
		DataModel dataModel = this.loadedModels.get(id);
		if (dataModel == null) {
			dataModel = this.loadModel(id);
			this.loadedModels.put(id, dataModel);
		}
		return dataModel;
	}


	public static ModelRegistry getInstance() {
		if (instance == null) {
			instance = new ModelRegistry();
		}
		return instance;
	}

	private ModelRegistry() {

	}

	private DataModel loadModel(String id) {
		final DataModel model = new DataModel();
		final IConfigurationElement[] configurationElementsFor = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						"com.onpositive.semantic.model.rdf.modelDefinition"); //$NON-NLS-1$
		for (final IConfigurationElement e : configurationElementsFor) {
			final String attribute = e.getAttribute("namespace"); //$NON-NLS-1$
			if ((attribute != null) && (attribute.length() > 0)
					&& attribute.equals(id)) {
				final Bundle bn = Platform.getBundle(e.getContributor()
						.getName());
				try {
					model.addClassLoader(new ClassLoader() {

						public Class<?> loadClass(String name)
								throws ClassNotFoundException {
							return bn.loadClass(name);
						}

						protected URL findResource(String name) {
							return bn.getResource(name);
						}

						@SuppressWarnings("unchecked")
						protected Enumeration<URL> findResources(String name)
								throws IOException {
							return bn.getResources(name);
						}

						protected synchronized Class<?> loadClass(String name,
								boolean resolve) throws ClassNotFoundException {
							return bn.loadClass(name);
						}
					});
					DOMEvaluator.getInstance().evaluateLocalPluginResource(EquinoxBridge.getBundle(bn),
							 e.getAttribute("path"), model); //$NON-NLS-1$
				} catch (final InvalidRegistryObjectException e1) {
					throw new RuntimeException();
				} catch (final Exception e1) {
					Activator.log(e1);
					throw new RuntimeException();
				}
			}
		}
		return model;
	}
}
