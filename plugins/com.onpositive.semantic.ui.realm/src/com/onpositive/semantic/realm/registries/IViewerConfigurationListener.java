package com.onpositive.semantic.realm.registries;

import java.util.Collection;

public interface IViewerConfigurationListener {

	public void tabsAdded(Collection<ViewerTabConfiguration> tabs);

	public void tabsRemoved(Collection<ViewerTabConfiguration> tabs);
	
	public void tabMoved(int oldPosition,int newPosition);
	
	public void tabOrderChanged(Collection<ViewerTabConfiguration>newTabOrder);

}
