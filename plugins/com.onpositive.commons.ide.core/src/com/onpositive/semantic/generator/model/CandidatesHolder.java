package com.onpositive.semantic.generator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespacesModel;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;


public class CandidatesHolder
{
	ArrayList<CandidateConfigurationListElement> properties;
	ArrayList<UIPropertyCandidate> actions;
	
	@RealmProvider(value = WindowStyleRealmProvider.class)
	protected ElementModel curMainWindowModel = NamespacesModel.getInstance().resolveElement("http://jetface.org/JetFace1.0/","form");
	protected String savePath;
	protected String qualifiedName;
	
	public static class WindowStyleRealmProvider implements IRealmProvider
	{
		ArrayList<ElementModel> elements;

		public IRealm getRealm(IBinding model)
		{
			if (elements == null)
			{
				elements = new ArrayList<ElementModel>();
				fillWindowStyleElements(elements);
			}
			return new Realm(elements); 
		}


		
	}
	
	public CandidatesHolder(TypeDeclaration rootType)
	{
		properties = new ArrayList<CandidateConfigurationListElement>();
		ITypeBinding resolveBinding = rootType.resolveBinding();
		qualifiedName = resolveBinding.getQualifiedName();
		while (resolveBinding != null && !resolveBinding.getName().equals("Object"))
		{			
			IVariableBinding[] declaredFields = resolveBinding.getDeclaredFields();
			for (int i = 0; i < declaredFields.length; i++)
			{
				ITypeBinding type = declaredFields[i].getType();
				if (type.isArray()) break;
				String typeName = type.getQualifiedName();
				String name = declaredFields[i].getName();
				UIPropertyElementCreator bestCreator = UIPropertyElementCreatorRegistry.getInstance().getForTypeWithMaxPriority(typeName);
				
				if (bestCreator != null && needToAdd(name)) 
														properties.add(new CandidateConfigurationListElement(
														bestCreator, 
														new UIPropertyCandidate("", name, name, typeName)));
				
			}
			resolveBinding = resolveBinding.getSuperclass();
		}	
		
	}
	
	
	protected boolean needToAdd(String name)
	{
		for (Iterator iterator = properties.iterator(); iterator.hasNext();)
		{
			UIPropertyCandidate candidate = ((CandidateConfigurationListElement) iterator.next()).getCurCandidate();
			if (candidate.getName().equals(name)) return false;			
		}
		return true;
	}


	/**
	 * @return the properties
	 */
	public List<CandidateConfigurationListElement> getProperties()
	{
		return properties;
	}

	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(
			ArrayList<CandidateConfigurationListElement> properties)
	{
		this.properties = properties;
	}

	protected static void fillWindowStyleElements(ArrayList<ElementModel> elements)
	{
		NamespacesModel model = NamespacesModel.getInstance();
		elements.add(model.resolveElement("http://jetface.org/JetFace1.0/","composite-editor"));
		elements.add(model.resolveElement("http://jetface.org/JetFace1.0/","form"));
		elements.add(model.resolveElement("http://jetface.org/JetFace1.0/","section"));
		elements.add(model.resolveElement("http://jetface.org/JetFace1.0/widgets","dialog"));
		elements.add(model.resolveElement("http://jetface.org/JetFace1.0/widgets","popupDialog"));
		
	}

	
	/**
	 * @return the curMainWindowModel
	 */
	public ElementModel getCurMainWindowModel()
	{
		return curMainWindowModel;
	}

	
	/**
	 * @param curMainWindowModel the curMainWindowModel to set
	 */
	public void setCurMainWindowModel(ElementModel curMainWindowModel)
	{
		this.curMainWindowModel = curMainWindowModel;
	}
	
	
	/**
	 * @return the savePath
	 */
	public String getSavePath()
	{
		return savePath;
	}


	
	/**
	 * @return the qualifiedName
	 */
	public String getQualifiedTargetClassName()
	{
		return qualifiedName;
	}

}
