package com.onpositive.semantic.language.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.onpositive.semantic.model.api.property.java.AdderRemover;
import com.onpositive.semantic.model.api.property.java.UseSetter;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;

public class ElementModel extends ModelElement {

	private static final String[] NO_ELEMENTS = new String[0];
	private String[] superElement;
	private String[] childs;
	private ArrayList<AttributeModel> properties = new ArrayList<AttributeModel>();
	private HashSet<ElementModel> superElements;
	private HashSet<String>restrictedAttributes=new HashSet<String>();
	private boolean isAbstract = false;
	private boolean allowMultiple = true;
	private String handlerClass;

	public HashSet<ElementModel> getAllSuperElements() {
		final HashSet<ElementModel> allsuperElements = new HashSet<ElementModel>();
		this.fillSupers(allsuperElements);
		return allsuperElements;
	}
	
	@AdderRemover(adder="addRestrictedAttribute",remover="removeRestrictedAttribute")
	public HashSet<AttributeModel>getRestrictedAttributes(){
		HashSet<AttributeModel>mdl=new HashSet<AttributeModel>();
		for (String s: restrictedAttributes){
			for (AttributeModel m: getAllProperties()){
				if (m.getName().equals(s)){
					mdl.add(m);
				}
			}
			
		}
		return mdl;
	}
	
	public void addRestrictedAttribute(AttributeModel mdl){
		restrictedAttributes.add(mdl.getName());
	}
	
	public void removeRestrictedAttribute(AttributeModel mdl){
		restrictedAttributes.remove(mdl.getName());
	}
	
	public HashSet<ElementModel>getSuperElements(){
		checkSupers();
		return new HashSet<ElementModel>(superElements);
	}

	private void fillSupers(HashSet<ElementModel> allsuperElements) {
		this.checkSupers();
		for (final ElementModel m : this.superElements) {
			if (m != null) {
				if (!allsuperElements.contains(m)) {
					allsuperElements.add(m);
					m.fillSupers(allsuperElements);
				}
			}
		}
	}

//	public boolean equals(Object object) {
//		if (object instanceof ElementModel){
//			ElementModel ml=(ElementModel) object;
//			boolean equals = this.getModel().getUrl().equals(ml.getModel().getUrl());
//			return equals&&this.getName().equals(ml.getName());
//		}
//		return false;
//	}
//
//	public int hashCode() {
//		if (name != null) {
//			return this.name.hashCode();
//		}
//		return 0;
//	}

	private void checkSupers() {
		if (this.superElements == null||true) {
			this.superElements = new HashSet<ElementModel>();
			final String[] split = this.superElement != null ? this.superElement
					: NO_ELEMENTS;
			for (String s : split) {
				s = s.trim();
				if (s.length() > 0) {
					final NameSpaceContributionModel na = (NameSpaceContributionModel) this
							.getOwner();
					final ElementModel el = na.getElement(s);
					if (el != null) {
						this.superElements.add(el);
					} else {
						final int lastIndexOf = s.lastIndexOf('/');
						if (lastIndexOf != -1) {
							final String substring = s.substring(0,
									lastIndexOf + 1);
							final ElementModel ele = NamespacesModel
									.getInstance().resolveElement(substring,
											s.substring(lastIndexOf + 1));
							if (ele != null) {
								this.superElements.add(ele);
							}
						}
					}
				}
			}
		}
	}

	public void setSuperElement(String[] superElement) {
		this.superElement = superElement;
		this.superElements = null;
		this.changed();
	}

	public void setChilds(String[] childs) {
		this.childs = childs;
		this.changed();
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
		this.changed();
	}

	public void setAllowMultiple(boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
		this.changed();
	}

	@RealmProvider(value = ElementRealmProvider.class)
	public String[] getSuperElement() {
		return this.superElement;
	}

	public ElementModel() {
		super(null, null);
	}

	public ElementModel(Element el, NameSpaceContributionModel owner) {
		super(el, owner);
		String attribute = el.getAttribute("extends").trim();
		if (attribute.length() > 0) {
			this.superElement = attribute.split(","); //$NON-NLS-1$			
		} else {
			this.superElement = NO_ELEMENTS;
		}
		attribute = el.getAttribute("allowsChilds").trim();

		if (attribute.length() > 0) {
			this.childs = attribute.split(",");
		} else {
			this.childs = NO_ELEMENTS;
		}
		attribute=el.getAttribute("restrictedAttrs");
		if(attribute.length()>0){
			String[] split = attribute.split(",");
			for (String s:split){
				String trim = s.trim();
				if (trim.length()>0){
				restrictedAttributes.add(trim);
				}
			}
		}

		final NodeList elementsByTagName = el.getElementsByTagName("property");
		for (int a = 0; a < elementsByTagName.getLength(); a++) {
			this.properties.add(new AttributeModel((Element) elementsByTagName
					.item(a), this));
		}
		String els = el.getAttribute("isAbstract");
		if (els.length() != 0) {
			this.isAbstract = Boolean.parseBoolean(els);
		}
		els = el.getAttribute("allowMultiple");
		if (els.length() != 0) {
			this.allowMultiple = Boolean.parseBoolean(els);
		}
		this.handlerClass = el.getAttribute("class");
	}

	public ArrayList<AttributeModel> getProperties() {
		return this.properties;
	}

	@RealmProvider(value = ElementRealmProvider.class)
	public String[] getChilds() {
		return this.childs;
	}

	public boolean isAbstract() {
		return this.isAbstract;
	}

	public boolean isAllowMultiple() {
		return this.allowMultiple;
	}

	
	public void store(Element element) {
		StringBuilder bld = new StringBuilder();
		if (this.superElement != null) {
			for (int a = 0; a < this.superElement.length; a++) {
				bld.append(this.superElement[a]);
				if (a < this.superElement.length - 1) {
					bld.append(',');
				}
			}
		}
		element.setAttribute("extends", bld.toString());
		bld = new StringBuilder();
		if (this.childs != null) {
			for (int a = 0; a < this.childs.length; a++) {
				bld.append(this.childs[a]);
				if (a < this.childs.length - 1) {
					bld.append(',');
				}
			}
		}
		
		element.setAttribute("allowsChilds", bld.toString());
		bld = new StringBuilder();
		int a=0;
		for (String s:restrictedAttributes){
			bld.append(s);
			a++;
			if (a!=restrictedAttributes.size()){
			bld.append(',');
			}
		}
		if (bld.length()>0){
			element.setAttribute("restrictedAttrs", bld.toString());
		}
		element.setAttribute("class", this.handlerClass);
		if (this.isAbstract) {
			element.setAttribute("isAbstract", "true");
		}
		if (!this.allowMultiple) {
			element.setAttribute("isAbstract", "true");
		}
		super.store(element);
		for (final AttributeModel m : this.properties) {
			final Element createElement = element.getOwnerDocument()
					.createElement("property");
			m.store(createElement);
			element.appendChild(createElement);
		}
	}

	@AdderRemover(adder = "addProperty", remover = "removeProperty")
	public HashSet<AttributeModel> getAllProperties() {
		final HashSet<AttributeModel> prop = new HashSet<AttributeModel>();
		final HashSet<ElementModel> allSuperElements = this
				.getAllSuperElements();
		for (final ElementModel e : allSuperElements) {
			prop.addAll(e.getProperties());
		}
		prop.addAll(this.properties);
		
		return prop;
	}
	
	@AdderRemover(adder = "addProperty", remover = "removeProperty")
	public HashSet<AttributeModel> getAllNotRestrictedProperties() {
		HashSet<AttributeModel> allProperties = getAllProperties();
		allProperties.removeAll(getRestrictedAttributes());
		return allProperties;
	}
	
	

	@UseSetter
	public void setProperties(ArrayList<AttributeModel> properties) {
		this.properties = properties;
		for (final AttributeModel m : properties) {
			if (m.getOwner() != this) {
				m.setOwner(this);
			}
		}
		this.changed();
	}

	public void addProperty(AttributeModel prop) {
		prop.setOwner(this);
		this.properties.add(prop);
	}

	public void removeProperty(AttributeModel prop) {
		this.properties.remove(prop);
	}

	public String toString() {
		return this.getName();
	}

	public boolean isAllowedChild(ElementModel el) {
		final String[] split = this.childs;
		for (final String s : split) {
			if (s.length() > 0) {
				final ElementModel resolveElement = NamespacesModel
						.getInstance().resolveElement(
								((NameSpaceContributionModel) this.getOwner())
										.getUrl(), s);
				if (resolveElement != null) {
					final HashSet<ElementModel> allSuperElements = el
							.getAllSuperElements();
					if (allSuperElements.contains(resolveElement)) {
						return true;
					}
					if (el.equals(resolveElement)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	
	public String getPath() {
		return this.getOwner().getPath() + "/" + this.getName();
	}

	
	public void updateDocumentationNodes(String oldName, String newName) {
		this.getModel().getDocumentation().replace(
				this.getOwner().getPath() + "/" + oldName,
				this.getOwner().getPath() + "/" + newName);
		for (final AttributeModel m : this.properties) {
			this.getModel().getDocumentation().replace(
					this.getOwner().getPath() + "/" + oldName + "/"
							+ m.getName(),
					this.getOwner().getPath() + "/" + newName + "/"
							+ m.getName());
		}
	}

	public String getHandlerClass() {
		return this.handlerClass;
	}

	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
		this.changed();
	}

	public AttributeModel getProperty(String s) {
		for (final AttributeModel m : this.properties) {
			if (m.getName().equals(s)) {
				return m;
			}
		}
		return null;
	}

	public ArrayList<AttributeModel> getRequiredAttributes() {
		final HashSet<AttributeModel> allProperties = this.getAllProperties();
		final ArrayList<AttributeModel> result = new ArrayList<AttributeModel>();
		for (final AttributeModel m : allProperties) {
			if (m.isRequired()) {
				result.add(m);
			}
		}
		Collections.sort(result, new Comparator<AttributeModel>() {

			public int compare(AttributeModel o1, AttributeModel o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		return result;
	}

	public boolean mayHaveChilds() {
		return (this.childs != null) && (this.childs.length > 0)
				&& (this.childs[0].trim().length() > 0);
	}

}