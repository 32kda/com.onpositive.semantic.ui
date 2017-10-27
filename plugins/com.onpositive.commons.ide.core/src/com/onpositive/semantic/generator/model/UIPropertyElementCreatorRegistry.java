package com.onpositive.semantic.generator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.onpositive.semantic.language.model.NamespacesModel;


public class UIPropertyElementCreatorRegistry
{
	protected static final String EXTENSION_POINT_NAME = "com.onpositive.commons.ide.core.uiPropertyElementCreators";
	protected static final int DEFAULT_CREATOR_PRIORITY = 10;
	protected HashMap<String, ArrayList<UIPropertyElementCreator>> registry;	
	protected static UIPropertyElementCreatorRegistry instance = null;
	
	public static String STRING_DISPLAY_LABEL = "Single-line input string";
	public static String TEXT_DISPLAY_LABEL = "Multi-line text input ";
	public static String RICHTEXT_DISPLAY_LABEL = "`input string";
	
	protected UIPropertyElementCreatorRegistry()
	{
		registry  = new HashMap<String,ArrayList<UIPropertyElementCreator>>();
		loadFromRegistry();
		//testInit();
	}
	
	public static UIPropertyElementCreatorRegistry getInstance()
	{
		if (instance == null) instance = new UIPropertyElementCreatorRegistry();
		return instance;
	}
	
	public void addNewElementCreator(String typeName, UIPropertyElementCreator creator)
	{
		ArrayList<UIPropertyElementCreator> creatorList = registry.get(typeName);
		if (creatorList == null)
		{
			creatorList = new ArrayList<UIPropertyElementCreator>();
			registry.put(typeName,creatorList);
		}
		creatorList.add(creator);
	}
	
	public void addElementCreatorList(String typeName, ArrayList<UIPropertyElementCreator> creatorList)
	{
		ArrayList<UIPropertyElementCreator> list = registry.get(typeName);
		if (list == null)
			registry.put(typeName,creatorList);
		else list.addAll(creatorList);
	}
	
	protected void loadFromRegistry()
	{		
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT_NAME);
        for (final IConfigurationElement element : elements) 
        {          
            try
            {
	            final String name = element.getAttribute("name");
	            final String typeName = element.getAttribute("typeName");
	            final String description = element.getAttribute("description");
	            int priority = DEFAULT_CREATOR_PRIORITY;
	            IBindingConfigurator bindingConfigurator = null;
	            IElementConfigurator elementConfigurator = null;
	            bindingConfigurator = (IBindingConfigurator) element.createExecutableExtension("bindingConfiguratorClass");
	            elementConfigurator = (IElementConfigurator) element.createExecutableExtension("elementConfiguratorClass");
	            priority = Integer.parseInt(element.getAttribute("priority"));
	            addNewElementCreator(typeName, new UIPropertyElementCreator(typeName,
						 NamespacesModel.getInstance().resolveElement(elementConfigurator.getNamespace(),elementConfigurator.getName()),
						  description,
						  elementConfigurator, bindingConfigurator,priority)
						);
	        }
	        catch (CoreException e) {
	        	e.printStackTrace();
			}
	        catch (NumberFormatException e) {
	        	e.printStackTrace();
			}
	        catch (Exception e) {
	        	e.printStackTrace();
			}
        }

	}
	
	//TODO debug
	protected void testInit()
	{
		addNewElementCreator("java.lang.String", new UIPropertyElementCreator("java.lang.String",
							 NamespacesModel.getInstance().resolveElement("http://jetface.org/JetFace1.0/","string"),
							  "<string> : Simple input string",
							 new StringElementConfigurator(), new StandartBindingConfigurator(),1)
							);
		addNewElementCreator("java.lang.String", new UIPropertyElementCreator("java.lang.String",
				 NamespacesModel.getInstance().resolveElement("http://jetface.org/JetFace1.0/","text"),
				 "<text> : Multi-line text input",
				 new StringElementConfigurator(), new StandartBindingConfigurator(),2)
				);
		addNewElementCreator("java.lang.String", new UIPropertyElementCreator("java.lang.String",
				 NamespacesModel.getInstance().resolveElement("http://jetface.org/JetFace1.0/","richtext"),
				 "<richtext> : Rich text editor",
				 new RichtextElementConfigurator(), new StandartBindingConfigurator(),3)
				);
		addNewElementCreator("int", new UIPropertyElementCreator("int",
				 NamespacesModel.getInstance().resolveElement("http://jetface.org/JetFace1.0/","spinner"),
				 "<spinner> : Spinner input",
				 new SpinnerElementConfigurator(), new StandartBindingConfigurator(),1)
				);
		addNewElementCreator("int", new UIPropertyElementCreator("int",
				 NamespacesModel.getInstance().resolveElement("http://jetface.org/JetFace1.0/","string"),
				 "<string> : Simple input string",
				 new StringElementConfigurator(), new StandartBindingConfigurator(),2)
				);
	}
	
	public UIPropertyElementCreator getForTypeWithMaxPriority(String typeName)
	{
		ArrayList<UIPropertyElementCreator> creators = registry.get(typeName);
		UIPropertyElementCreator bestCreator = null;
		if (creators == null) return null;
		for (Iterator iterator = creators.iterator(); iterator.hasNext();)
		{
			UIPropertyElementCreator creator = (UIPropertyElementCreator) iterator
					.next();
			if (bestCreator == null || creator.getPriority() < bestCreator.getPriority())
				bestCreator = creator;
		}
		return bestCreator;
	}
	
	public ArrayList<UIPropertyElementCreator> getCreatorList(String typeName)
	{
		return registry.get(typeName);
	}
	
}
