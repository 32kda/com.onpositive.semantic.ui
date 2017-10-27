package com.onpositive.semantic.language.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;

public class NamespaceModel {

	public NamespaceModel(String url2) {
		this.url = url2;
		models=null;
	}

	private final String url;

	private NameSpaceContributionModel models;

	public void register(IProject k, NameSpaceContributionModel ma) {
		models=ma;
	}

	public String getUrl() {
		return this.url;
	}

	public ArrayList<ModelElement> getMembers() {		
		return new ArrayList<ModelElement>(models.getContributions());
	}

	public HashSet<String> getName() {	
		return new HashSet<String>(Collections.singleton(models.getName()));
	}

	public boolean actuallyEquals(NamespaceModel model) {
		return this.models!=null&&this.models.equals(model.models);
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.url == null) ? 0 : this.url.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final NamespaceModel other = (NamespaceModel) obj;
		if (this.url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!this.url.equals(other.url)) {
			return false;
		}
		return true;
	}

	public void unregister(IProject project, NamespaceModel namespaceModel) {
		models=null;
	}

	public boolean isEmpty() {
		return this.models==null;
	}
	
	public ElementModel resolveParentElement(String tag) { //TODO ’з, по идее один должен считать родител€, второй - просто резолвить сам эл-т
		if (models==null){
			return null;
		}
		
		
				for (final ModelElement el : models.getContributions()) {
					if ((el instanceof ElementModel) && containsString(((ElementModel)el).getChilds(),tag)) {
						return (ElementModel) el;
					}
				}
		
		int lastIndexOf = tag.lastIndexOf('/');
		if (lastIndexOf!=-1){
			String url=tag.substring(0,lastIndexOf+1);
			NamespaceModel resolveNamespace = NamespacesModel.getInstance().resolveNamespace(url);
			if (resolveNamespace!=null){
				return resolveNamespace.resolveElement(tag.substring(lastIndexOf+1));
			}
		}
		return null;
	}

	public ElementModel resolveElement(String tag) {
		
		
			if (models==null){
				return null;
			}
				for (final ModelElement el : models.getContributions()) {
					if ((el instanceof ElementModel) && el.getName().equals(tag)) {
						return (ElementModel) el;
					}
				}
			
		
		int lastIndexOf = tag.lastIndexOf('/');
		if (lastIndexOf!=-1){
			String url=tag.substring(0,lastIndexOf+1);
			NamespaceModel resolveNamespace = NamespacesModel.getInstance().resolveNamespace(url);
			if (resolveNamespace!=null){
				return resolveNamespace.resolveElement(tag.substring(lastIndexOf+1));
			}
		}
		return null;
	}
	
	/**
	 * Check, whether String array contains string
	 * @param array - array to find string in
	 * @param whatToFind - string to find
	 * @return true/false
	 */
	protected boolean containsString(String[] array, String whatToFind)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i].equals(whatToFind)) return true;
		}
		return false;
	}

	public AttributeModel resolveAttribute(String tag) {
		
		
				for (final ModelElement el : models.getContributions()) {
					if ((el instanceof AttributeModel)
							&& el.getName().equals(tag)) {
						return (AttributeModel) el;
					}
				}
		
		return null;
	}

	public String getDescription() {
		return models.getDescription();			
	}

}
