package com.onpositive.semantic.model.ui.actions;

import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;
import com.onpositive.semantic.model.ui.roles.ImageObject;

public abstract class Action extends ContributionItem implements IAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String text="";
	protected String description="";
	private String actionDefinitionId ;
	protected ImageDescriptor imageDescriptor;
	protected ImageDescriptor disabledImageDescriptor;
	protected ImageDescriptor hoverImageDescriptor;
	protected int style;
	private String toolTipText;
	protected String imageId;
	protected String disabledImageId;
	protected String hoverImageId;
	
	protected static int lastSortId=0;
	
	protected int sortId=lastSortId++;
	
	public void dispose(){
		
	}
	
	public int getStyle() {
		return style;
	}
	
	public Action(int style) {
		super();
		this.style = style;
	}
	public Action() {
		super();
		this.style = IAction.AS_PUSH_BUTTON;
	}

	public String getText() {
		return text;
	}

	public String getDescription() {
		return description;
	}

	public void setText(String text) {
		String s=this.text;
		this.text=text;
		support.firePropertyChange("text", s, text);
	}

	public void setDescription(String text) {
		String s=this.description;
		this.description=text;
		support.firePropertyChange("description", s, description);
	}

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public ImageDescriptor getDisabledImageDescriptor() {
		return disabledImageDescriptor;
	}

	public void setImageDescriptor(ImageDescriptor desc) {
		ImageDescriptor s=this.imageDescriptor;
		this.imageDescriptor=desc;
		support.firePropertyChange("imageDescriptor", s, imageDescriptor);
	}

	public void setDisabledImageDescriptor(ImageDescriptor desc) {
		ImageDescriptor s=this.imageDescriptor;
		this.disabledImageDescriptor=desc;
		support.firePropertyChange("disabledImageDescriptor", s, disabledImageDescriptor);
	}
	
	private boolean selection;

	public boolean getSelection() {
		return selection;
	}

	public void setSelection(boolean selection) {
		if (this.selection!=selection){
			boolean old=this.selection;
			this.selection=selection;
			support.firePropertyChange("checked", old,selection);
		}
	}
	
	public void setChecked(boolean checked){
		setSelection(checked);
	}
	
	public boolean isChecked(){
		return getSelection();
	}
	
	public void setActionDefinitionId(String id) {
		actionDefinitionId = id;
	}
	
	public String getActionDefinitionId() {
		return actionDefinitionId;
	}
	
	/**
	 * Sets the tool tip text for this action.
	 * <p>
	 * Fires a property change event for the <code>TOOL_TIP_TEXT</code>
	 * property if the tool tip text actually changes as a consequence.
	 * </p>
	 * 
	 * @param toolTipText
	 *            the tool tip text, or <code>null</code> if none
	 */
	public void setToolTipText(String toolTipText) {
		String oldToolTipText = this.toolTipText;
		if (!(oldToolTipText == null ? toolTipText == null : oldToolTipText
				.equals(toolTipText))) {
			this.toolTipText = toolTipText;
//			firePropertyChange(TOOL_TIP_TEXT, oldToolTipText, toolTipText);
		}
	}
	public String getToolTipText() {
		return toolTipText;
	}
	
	public void setHoverImageDescriptor(ImageDescriptor newImage) {
		if (hoverImageDescriptor != newImage) {
			ImageDescriptor oldImage = hoverImageDescriptor;
			hoverImageDescriptor = newImage;
//			firePropertyChange(IMAGE, oldImage, newImage);
		}
	}
	public ImageDescriptor getHoverImageDescriptor() {
		return hoverImageDescriptor;
	}
	
	public String getImageId() {
		return this.imageId;
	}

	public void setImageId(String imageId)
	{
		this.imageId = imageId;
		if ((imageId != null) && (imageId.length() > 0)) {
			ImageDescriptor imageDescriptor2 = ImageManager.getImageDescriptorByPath(this,imageId);
			this.setImageDescriptor(imageDescriptor2);
		} else
			this.setImageDescriptor(null);
	}
	public String getDisabledImageId() {
		return this.disabledImageId;
	}

	
	public void setDisabledImageId(String disabledImageId)
	{
		this.disabledImageId = disabledImageId;
		if ((disabledImageId != null) && (disabledImageId.length() > 0))
		{
			final ImageObject imageObject = ImageManager.getInstance().get(
					disabledImageId);
			if (imageObject != null) {
				this.setDisabledImageDescriptor(ImageManager.getImageDescriptor(disabledImageId));
			} else {
				Platform.log(new IllegalArgumentException(
						"unable to find image with id:" + imageId));
			}
		} else {
			this.setDisabledImageDescriptor(null);
		}
	}

	public String getHoverImageId() {
		return this.hoverImageId;
	}

	public void setHoverImageId(String hoverImageId)
	{
		this.hoverImageId = hoverImageId;
		if ((hoverImageId != null) && (hoverImageId.length() > 0))
			this.setHoverImageDescriptor(ImageManager.getImageDescriptor(hoverImageId));
		else
			this.setHoverImageDescriptor(null);
	}
	

	
}
