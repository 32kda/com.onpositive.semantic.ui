package com.onpositive.semantic.realm.registries;

import java.util.Set;

import com.onpositive.commons.xml.language.Activator;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.semantic.realm.IColumnDefinition;
import com.onpositive.semantic.realm.IViewerTab;

public class ViewerTabDefinition extends NamedEntity implements IViewerTab {

	private ColumnConfiguration firstColumn;
	//private Set<IC>

	public ViewerTabDefinition(IConfigurationElement element) {
		super(element);
	}

	public ColumnConfiguration getFirstColumn() {
		return firstColumn;
	}

	public String viewerDefinition() {
		return getStringAttribute("targetViewer", null);
	}

	
	public IColumnDefinition firstColumn() {
		return null;
	}

	
	public Set<IColumnDefinition> requiredColumns() {
		return null;
	}

	public int getPriority() {
		int integerAttribute = getIntegerAttribute("priority", 0);
		return integerAttribute;
	}

	public ViewerTabConfiguration createViewerTabConfiguration() {
		IConfigurationElement[] children = fElement.getChildren();
		ViewerTabConfiguration configuration = new ViewerTabConfiguration(this);
		String firstColumn = fElement.getAttribute("firstColumn");
		for (IConfigurationElement e : children) {
			String attribute = e.getAttribute("targetId");
			if (e.getName().equals("columnReference")) {
				ColumnDefinitionObject columnDefinitionObject = ColumnDefinitionRegistry
						.getInstance().get(attribute);
				if (columnDefinitionObject != null) {
					ColumnConfiguration createColumnConfiguration = columnDefinitionObject
							.createColumnConfiguration();
					if (createColumnConfiguration.getId().equals(firstColumn)) {
						this.firstColumn = createColumnConfiguration;
					}
					configuration.addColumn(createColumnConfiguration);
				} else {
					Activator.log(new IllegalArgumentException(
							"Column with id:" + attribute
									+ " was not found in the column registry"));
				}

			} else if (e.getName().equals("filterReference")) {
				FilterDefinitionObject columnDefinitionObject = FilterDefinitionRegistry
						.getInstance().get(attribute);
				if (columnDefinitionObject != null) {
					FilterConfiguration createFilterConfiguration = columnDefinitionObject
							.createFilterConfiguration(columnDefinitionObject);
					configuration.addFilter(createFilterConfiguration);
				} else {
					Activator.log(new IllegalArgumentException(
							"Column with id:" + attribute
									+ " was not found in the column registry"));
				}
			}
		}
		return configuration;
	}

}
