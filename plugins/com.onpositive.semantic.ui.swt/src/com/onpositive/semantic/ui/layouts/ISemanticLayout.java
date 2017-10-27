package com.onpositive.semantic.ui.layouts;

public interface ISemanticLayout {

	String getName();

	String getDescription();

	String getIcon();

	public void installAspect(IVisualisationAspect aspect);

	public void uninstallAspect(IVisualisationAspect aspect);

	public void setActive(IVisualisationAspect aspect);

	void refreshAspect(VisualisationAspect e);
}
