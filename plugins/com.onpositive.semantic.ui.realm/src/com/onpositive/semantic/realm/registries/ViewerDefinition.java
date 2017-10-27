package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import com.onpositive.commons.platform.configuration.ConfigurationPersistence;
import com.onpositive.commons.platform.configuration.EclipsePreferencesConfiguration;
import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.realm.IColumnDefinition;
import com.onpositive.semantic.realm.IViewerDefinition;

public class ViewerDefinition extends NamedEntity implements IViewerDefinition {

	private static EclipsePreferencesConfiguration pluginPreferences = EclipsePreferencesConfiguration
			.getPluginPreferences("com.onpositive.semantic.ui.realm");

	public ViewerDefinition(IConfigurationElement element) {
		super(element);
	}

	
	public IColumnDefinition[] allPossibleColumns() {
		return ColumnDefinitionRegistry.getInstance()
				.getDefinedColumns(getId());
	}

	ViewerConfiguration configuration;

	public ViewerConfiguration getConfiguration() {
		if (configuration != null) {
			return configuration;
		}
		configuration = internalGetConfiguration();
		configuration
				.addViewerConfigurationListener(new IViewerConfigurationListener() {

					
					public void tabMoved(int oldPosition, int newPosition) {
						save(configuration);
					}

					
					public void tabOrderChanged(
							Collection<ViewerTabConfiguration> newTabOrder) {
						save(configuration);
					}

					
					public void tabsAdded(
							Collection<ViewerTabConfiguration> tabs) {
						save(configuration);
					}

					
					public void tabsRemoved(
							Collection<ViewerTabConfiguration> tabs) {
						save(configuration);
					}

				});
		return configuration;
	}

	private ViewerConfiguration internalGetConfiguration() {
		IAbstractConfiguration subConfiguration2 = pluginPreferences
				.getSubConfiguration(getId());
		boolean booleanAttribute = subConfiguration2
				.getBooleanAttribute("inited");
		if (booleanAttribute) {
			IAbstractConfiguration subConfiguration = subConfiguration2;
			ViewerConfiguration load = ConfigurationPersistence.load(
					ViewerConfiguration.class, subConfiguration);
			load.setDefinition(this);
			return load;
		}
		return getDefaultConfiguration();
	}

	public ViewerConfiguration getDefaultConfiguration() {
		ViewerTabDefinition[] definedTabs = ViewerTabDefinitionRegistry
				.getInstance().getDefinedTabs(getId());

		ArrayList<ViewerTabConfiguration> configuration = new ArrayList<ViewerTabConfiguration>();
		if (definedTabs != null) {
			Arrays.sort(definedTabs, new Comparator<ViewerTabDefinition>() {

				
				public int compare(ViewerTabDefinition o1,
						ViewerTabDefinition o2) {
					int p0 = o1.getPriority();
					int p1 = o2.getPriority();
					int z = p0 - p1;
					if (z == 0) {
						z = o1.compareTo(o2);
					}
					return z;
				}

			});
			for (ViewerTabDefinition d : definedTabs) {
				ViewerTabConfiguration createViewerTabConfiguration = d
						.createViewerTabConfiguration();
				
				configuration.add(createViewerTabConfiguration);
			}
		}
		ViewerConfiguration viewerConfiguration = new ViewerConfiguration(configuration, this);		
		return viewerConfiguration;
	}

	public void save(ViewerConfiguration configuration) {
		IAbstractConfiguration subConfiguration = pluginPreferences
				.getSubConfiguration(getId());
		subConfiguration.setBooleanAttribute("inited", true);
		ConfigurationPersistence.store(configuration, subConfiguration);
		subConfiguration.flush();
	}
}