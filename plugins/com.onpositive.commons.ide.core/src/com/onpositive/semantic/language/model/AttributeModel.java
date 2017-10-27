package com.onpositive.semantic.language.model;

import org.w3c.dom.Element;

import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;

public class AttributeModel extends ModelElement {

	private String type;
	private String typeSpecialization;
	private boolean required;
	private boolean translatable;

	public boolean isRequired() {
		return this.required;
	}

	public boolean isTranslatable() {
		return this.translatable;
	}

	@RealmProvider(value = TypeRealmProvider.class)
	public String getType() {
		return this.type;
	}

	public AttributeModel() {
		super(null, null);
	}
	
	public boolean supportsExpressions(){
		return true;		
	}

	public AttributeModel(Element el, ModelElement nameSpaceContributionModel) {
		super(el, nameSpaceContributionModel);
		this.type = el.getAttribute("type"); //$NON-NLS-1$
		String attribute = el.getAttribute("required");
		if (attribute.length() > 0) {
			this.required = Boolean.parseBoolean(attribute);
		}
		attribute = el.getAttribute("translatable");
		if (attribute.length() > 0) {
			this.translatable = Boolean.parseBoolean(attribute);
		}
		attribute=el.getAttribute("typeSpecialization");
		if (attribute.length()>0){
			typeSpecialization=attribute;
		}
	}

	
	public void store(Element element) {
		if (this.type != null) {
			element.setAttribute("type", this.type);
		}
		if (this.required) {
			element.setAttribute("required", "true");
		}
		if (this.translatable) {
			element.setAttribute("translatable", "true");
		}
		if (this.typeSpecialization!=null&&this.typeSpecialization.length()>0){
			element.setAttribute("typeSpecialization", this.typeSpecialization);
		}
		super.store(element);
	}

	public void setType(String type) {
		this.type = type;
		this.changed();
	}

	public void setRequired(boolean required) {
		this.required = required;
		this.changed();
	}

	public void setTranslatable(boolean translatable) {
		this.translatable = translatable;
		this.changed();
	}

	public String getTypeSpecialization() {
		return this.typeSpecialization;
	}
	
	public String getTypeSpecializationDescription() {
		String preg="Type specialization may contain ";
		if (type==null){
			return "";
		}
		if (type.equals("string")){
			return preg+"regexp to constrain allowed values for this string";
		}
		if (type.equals("java")){
			return preg+"fully qualified java type name to constrain super class or super interface";
		}
		if (type.equals("extension")){
			return preg+"fully qualified extension point/element name to constrain ids of referred elements";
		}
		if (type.equals("integer")){
			return preg+"interval like:0,10 to constain possible values range";
		}
		if (type.equals("enum")){
			return preg+"comma separated sequence to constrain allowed values like: fill,top,bottom,center";
		}
		return "";
	}
	
	boolean isTypeSpecializationEnabled(){
		if (type==null){
			return false;
		}
		if (type.equals("string")){
			return true;
		}
		if (type.equals("java")){
			return true;
		}
		if (type.equals("extension")){
			return true;
		}
		if (type.equals("integer")){
			return true;
		}
		if (type.equals("enum")){
			return true;
		}
		return false;
	}

	public void setTypeSpecialization(String typeSpecialization) {
		this.typeSpecialization = typeSpecialization;
		this.changed();
	}

	public String toString() {
		return this.getName();
	}

	
	public String getPath() {
		return this.getOwner().getPath() + "/" + this.getName();
	}

	
	public void updateDocumentationNodes(String oldName, String newName) {
		final NameSpaceContributionModel model = this.getModel();
		if (model != null) {
			model.getDocumentation().replace(
					this.getOwner().getPath() + "/" + oldName,
					this.getOwner().getPath() + "/" + newName);
		}
	}
}
