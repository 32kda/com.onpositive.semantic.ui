package com.onpositive.semantic.realm;


public interface IViewerConfiguration extends INamedEntity{
	
	IViewerDefinition getDefinition();
	
	IViewerTabConfiguration getTabs();
}
