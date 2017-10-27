package com.onpositive.businessdroids.ui.themes;

public class BasicAppLabelProvider implements IApplicationMessagesProvider {

	@Override
	public String getBooleanFilterDialogTitle() {
		return "Allowed values";
	}

	@Override
	public String getColumnsDialogTitle() {
		return "Visible columns";
	}

	@Override
	public String getComparableFilterDialogTitle() {
		return "Compare filter";
	}

	@Override
	public String getObjectSelectDilaogTitle() {
		return "Allowed values";
	}

	@Override
	public String getSimpleDateFilterDialogTitle() {
		return "Select";
	}

	@Override
	public String getStringSelectDialogTitle() {
		return "Select...";
	}

	@Override
	public String getCancelTitle() {
		return "Cancel";
	}

	@Override
	public String getTrueTitle() {
		return "true";
	}

	@Override
	public String getFalseTitle() {
		return "false";
	}

	@Override
	public String getMinTitle() {
		return "min";
	}

	@Override
	public String getMaxTitle() {
		return "max";
	}

	@Override
	public String getTextFilterDialogTitle() {
		return "Text filter";
	}

}
