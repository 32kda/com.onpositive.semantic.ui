package com.onpositive.semantic.model.ui.actions;

import com.onpositive.semantic.model.ui.roles.ImageDescriptor;

public interface IAction extends IContributionItem,Runnable{

	public final static int AS_CHECK_BOX = 1;
	public final static int AS_PUSH_BUTTON = 2;
	public static final int AS_RADIO_BUTTON = 3;
	public static final int AS_DROP_DOWN_MENU = 4;
	
	String getText();

	String getDescription();
	
	void setText(String text);
	
	void setDescription(String text);
	
	ImageDescriptor getImageDescriptor();
	
	ImageDescriptor getDisabledImageDescriptor();
	
	void setImageDescriptor(ImageDescriptor desc);
	
	void setDisabledImageDescriptor(ImageDescriptor desc);

	public void setHoverImageDescriptor(ImageDescriptor newImage);
	
	public ImageDescriptor getHoverImageDescriptor() ;
	
	boolean getSelection();
	
	void setSelection(boolean selection);
	
	int getStyle();
	
	void run();
	
	public void setActionDefinitionId(String id) ;

	public String getActionDefinitionId() ;
	
	public void setToolTipText(String toolTipText);
	
	public String getToolTipText() ;
	
	
}
