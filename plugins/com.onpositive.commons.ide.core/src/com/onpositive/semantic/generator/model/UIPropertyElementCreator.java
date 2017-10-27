package com.onpositive.semantic.generator.model;

import com.onpositive.semantic.language.model.ElementModel;


public class UIPropertyElementCreator
{
	protected int priority;
	
	protected String typeName;
	protected ElementModel elementModel;
	protected IBindingConfigurator iBindingConfigurator;
	protected IElementConfigurator configurator;
	protected String displayLabel;
	
	public UIPropertyElementCreator(String typeName, ElementModel elementModel,
			String displayLabel,
			IElementConfigurator configurator,
			IBindingConfigurator bindingConfigurator, int priority
			)
	{
		super();
		this.configurator = configurator;
		this.elementModel = elementModel;
		iBindingConfigurator = bindingConfigurator;
		this.priority = priority;
		this.typeName = typeName;
		this.displayLabel = displayLabel;
	}

	
	/**
	 * @return the priority
	 */
	public int getPriority()
	{
		return priority;
	}

	
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	
	/**
	 * @return the typeName
	 */
	public String getTypeName()
	{
		return typeName;
	}

	
	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	
	/**
	 * @return the elementModel
	 */
	public ElementModel getElementModel()
	{
		return elementModel;
	}

	
	/**
	 * @param elementModel the elementModel to set
	 */
	public void setElementModel(ElementModel elementModel)
	{
		this.elementModel = elementModel;
	}

	
	/**
	 * @return the iBindingConfigurator
	 */
	public IBindingConfigurator getIBindingConfigurator()
	{
		return iBindingConfigurator;
	}

	
	/**
	 * @param bindingConfigurator the iBindingConfigurator to set
	 */
	public void setIBindingConfigurator(IBindingConfigurator bindingConfigurator)
	{
		iBindingConfigurator = bindingConfigurator;
	}

	
	/**
	 * @return the configurator
	 */
	public IElementConfigurator getConfigurator()
	{
		return configurator;
	}

	
	/**
	 * @param configurator the configurator to set
	 */
	public void setConfigurator(IElementConfigurator configurator)
	{
		this.configurator = configurator;
	}
	
	
	public String toString()
	{	
		return displayLabel;
	}
	
	
}
