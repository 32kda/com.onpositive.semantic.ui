package com.onpositive.semantic.model.ui.generic.widgets;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.commons.xml.language.HandlesAttributeIndirectly;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.ui.actions.IContributionManager;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IElementListener;
import com.onpositive.semantic.model.ui.generic.widgets.impl.IEnablementListener;
import com.onpositive.semantic.ui.core.GenericLayoutHints;
import com.onpositive.semantic.ui.core.IConfigurable;

public interface IUIElement<T> {

	public final static String ID_PROPERTY = DefaultMetaKeys.ID_KEY+"_component";
	public final static String THEME_PROPERTY=DefaultMetaKeys.THEME_KEY;
	public final static String ROLE_PROPERTY=DefaultMetaKeys.ROLE_KEY;
	public final static String CAPTION_PROPERTY=DefaultMetaKeys.CAPTION_KEY;
	public static final String LAYOUT_HINTS_PROPERTY = "LAYOUT_HINTS";
	public static final String ENABLED_PROPERTY = DefaultMetaKeys.ENABLED;
	public static final String SERVICES_PROPERTY="services";
	public static final String TOOLTIP_PROPERTY="tooltip";	
	public static final String HAS_POPUP="has_popup";
	public static final String DISPLAYABLE_PROPERTY="displayable";

	ICompositeElement<?,?> getParent();
	
	public boolean isCreated();
	
	public <R> R getService(Class<R> clazz);
	
	T getControl();

	void setEnabled(boolean val);
	
	boolean isEnabled();

	void setDisplayable(boolean bl);
	
	public boolean isDisplayable();

	void addElementListener(IElementListener disposeBindingListener);
	
	void removeElementListener(IElementListener disposeBindingListener);
	
	void addConfigurationPart(IConfigurable iConfigurable);
	
	void removeConfigurationPart(IConfigurable iConfigurable);
	
	@HandlesAttributeIndirectly({"vAlign","hAlign","grabHorizontal","grabVertical","span","indent","hint","minimumSize"})
	GenericLayoutHints getLayoutHints();
	
	@HandlesAttributeDirectly("text")
	public void setText(String txt) ;

	void setLayoutHints(GenericLayoutHints layoutHints);
		
	@HandlesAttributeDirectly("theme")
	void setTheme(String theme);
	
	public String getTheme();
	
	@HandlesAttributeDirectly("caption")
	void setCaption(String caption);
	
	@HandlesAttributeDirectly("tooltip-text")
	void setToolTipText(String whyBindingIsDisabled);
	
	String getToolTipText();	
	
	String getCaption();
		
	public void redraw();

	@HandlesAttributeDirectly("id")
	void setId(String id);
		
	public String getId();

	void setConfiguration(IAbstractConfiguration configuration);

	IContributionManager getPopupMenuManager();
	
	Object getData(String string);	
	
	void setData(String key,Object value);
	
	@HandlesAttributeDirectly("role")
	void setRole(String role);
	
	public String getRole();
	
	@HandlesAttributeDirectly("background")
	public void setBackground(String background);
	
	
	public String getBackground();
	
	@HandlesAttributeDirectly("foreground")
	public void setForeground(String foreground);
	
	public String getForeground();
	
	@HandlesAttributeDirectly("background-image")
	public void setBackgroundImage(String image);
	
	public String getBackgroundImage();
	
	@HandlesAttributeDirectly("icon")
	public void setIcon(String image);
	
	public String getIcon();
	
	@HandlesAttributeDirectly("font")
	public void setFont(String font);
	
	@HandlesAttributeDirectly("enablement")
	public void setEnablement(String enablement);
	
	@HandlesAttributeDirectly("visibility")
	public void setVisibility(String visibility);
	
	public boolean inStateChange();

	void executeOnUiThread(Runnable runnable);

	String getDescription();
		
	public  void addEnablementListener(IEnablementListener listener);

	public  void removeEnablementListener(IEnablementListener listener);
}