package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.onpositive.semantic.realm.IViewerDefinition;

public class ViewerConfiguration {

	private transient ViewerDefinition definition;

	private ArrayList<ViewerTabConfiguration> configuration = new ArrayList<ViewerTabConfiguration>();

	private transient HashSet<IViewerConfigurationListener> listeners = new HashSet<IViewerConfigurationListener>();

	public void addViewerConfigurationListener(
			IViewerConfigurationListener listener) {
		listeners.add(listener);
	}

	public void removeViewerConfigurationListener(
			IViewerConfigurationListener listener) {
		listeners.remove(listener);
	}

	public ViewerConfiguration(
			ArrayList<ViewerTabConfiguration> configuration2,
			ViewerDefinition viewerDefinition) {
		this.configuration = configuration2;
		this.definition = viewerDefinition;
		for (ViewerTabConfiguration t:configuration){
			t.setOwner(this);
		}
	}

	public ViewerConfiguration() {

	}

	public List<ViewerTabConfiguration> getTabs() {
		return new ArrayList<ViewerTabConfiguration>(configuration);
	}

	public IViewerDefinition getDefinition() {
		return definition;
	}

	void setDefinition(ViewerDefinition definition) {
		this.definition = definition;
		HashSet<ViewerTabConfiguration> toRemove = new HashSet<ViewerTabConfiguration>();
		for (ViewerTabConfiguration c : this.configuration) {
			String id = c.getId();
			c.setOwner(this);
			if (id != null && id.length() > 0) {
				ViewerTabDefinition definition2 = ViewerTabDefinitionRegistry
						.getInstance().get(id);
				if (definition2 == null) {
					toRemove.add(c);
				} else {
					c.setDefinition(definition2);
				}
			}
		}
		
		this.configuration.removeAll(toRemove);
	}

	public boolean mayRemove(ViewerTabConfiguration viewerTabConfiguration) {
		return true;
	}

	public void removeTab(ViewerTabConfiguration activeTab) {
		boolean remove = configuration.remove(activeTab);
		if (remove) {
			for (IViewerConfigurationListener l : listeners) {
				l.tabsRemoved(Collections.singleton(activeTab));
			}
		}
	}

	public void moveTab(int indexOf, int index) {
		ViewerTabConfiguration viewerTabConfiguration = configuration
				.remove(indexOf);
		configuration.add(index != 0 ? index - 1 : index,
				viewerTabConfiguration);
		for (IViewerConfigurationListener l : listeners) {
			l.tabMoved(indexOf, index);
		}
	}

	public void addTab(ViewerTabConfiguration conf) {
		configuration.add(conf);
		for (IViewerConfigurationListener l : listeners) {
			l.tabsAdded(Collections.singleton(conf));
		}
	}
	
	public void save(){
		((ViewerDefinition)getDefinition()).save(this);
	}
}