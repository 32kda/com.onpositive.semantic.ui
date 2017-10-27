package com.onpositive.semantic.language.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.java.UseSetter;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.property.java.annotations.Validator;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;

public class NameSpaceContributionModel extends ModelElement {

	private static final String ELEMENT = "element"; //$NON-NLS-1$
	private static final String ATTRIBUTE = "attribute"; //$NON-NLS-1$
	private String url;
	private DocumentationContributionModel documentation;
	private ArrayList<ModelElement> elementContributions = new ArrayList<ModelElement>();
	private Realm<String> elementNames = new Realm<String>();
	private Realm<String> attributeTypes = new Realm<String>();

	IFile resource;
	
	
	public IFile getResource() {
		return resource;
	}


	public void setResource(IFile resource) {
		this.resource = resource;
	}


	@Required
	@Validator(validatorClass=DeclUrlValidator.class)
	@RealmProvider(value = com.onpositive.semantic.language.model.DeclUrlRealmProvider.class)
	public String getUrl() {
		return this.url;
	}

	
	public void setName(String name) {
		this.name = name;
		this.changed();
	}

	private final HashSet<IValueListener<NameSpaceContributionModel>> listeners = new HashSet<IValueListener<NameSpaceContributionModel>>();
	private String declUrl;

	public void addValueListener(
			IValueListener<NameSpaceContributionModel> listener) {
		this.listeners.add(listener);
	}

	public void removeValueListener(
			IValueListener<NameSpaceContributionModel> listener) {
		this.listeners.add(listener);
	}

	
	public void changed() {
		for (final IValueListener<NameSpaceContributionModel> m : this.listeners) {
			m.valueChanged(this, this);
		}
	}

	public NameSpaceContributionModel(Element element) {
		super(element, null);
		this.url = element.getAttribute("url"); //$NON-NLS-1$
		final NodeList childNodes = element.getChildNodes();
		final int length = childNodes.getLength();
		for (int a = 0; a < length; a++) {
			final Node item = childNodes.item(a);
			if (item instanceof Element) {
				final Element el = (Element) item;
				if (el.getNodeName().equals(ELEMENT)) {
					final ElementModel elementModel = new ElementModel(el, this);
					for (final AttributeModel m : elementModel.getProperties()) {
						this.attributeTypes.add(m.getType());
					}
					this.elementNames.add(elementModel.getName());
					this.elementContributions.add(elementModel);
				} else if (el.getNodeName().equals(ATTRIBUTE)) {
					final AttributeModel attributeModel = new AttributeModel(
							el, this);
					this.attributeTypes.add(attributeModel.getType());
					this.elementContributions.add(attributeModel);
				}
			}
		}
		this.attributeTypes.add("java");
		this.attributeTypes.add("regexp");
		this.attributeTypes.add("extension");
		this.attributeTypes.add("string");
		this.attributeTypes.add("enum");
		this.attributeTypes.add("integer");
		this.attributeTypes.add("image");
	}

	public NameSpaceContributionModel() {
		super(null, null);
		this.url = "";
	}

	public Collection<ModelElement> getContributions() {
		return this.elementContributions;
	}

	@UseSetter
	public void setContributions(Collection<ModelElement> el) {
		this.elementContributions = new ArrayList<ModelElement>();
		this.elementNames = new Realm<String>();
		this.attributeTypes = new Realm<String>();
		for (final ModelElement m : el) {
			m.setOwner(this);
			if (m instanceof ElementModel) {
				final ElementModel elementModel = (ElementModel) m;
				for (final AttributeModel ma : elementModel.getProperties()) {
					this.attributeTypes.add(ma.getType());
				}
				this.elementNames.add(elementModel.getName());
				this.elementContributions.add(elementModel);
			} else if (m instanceof AttributeModel) {
				final AttributeModel attributeModel = (AttributeModel) m;
				this.attributeTypes.add(attributeModel.getType());
				this.elementContributions.add(attributeModel);
			}
		}
		this.changed();
	}

	public void store(Element element) {
		super.store(element);
		if (this.url != null) {
			element.setAttribute("url", this.url);
		}
		for (final ModelElement el : this.elementContributions) {
			if (el instanceof ElementModel) {
				final Element createElement = element.getOwnerDocument()
						.createElement(ELEMENT);
				el.store(createElement);
				element.appendChild(createElement);
			} else {
				final Element createElement = element.getOwnerDocument()
						.createElement(ATTRIBUTE);
				el.store(createElement);
				element.appendChild(createElement);
			}
		}
	}

	public void setUrl(String url) {
		this.updateDocumentationNodes(this.url, url);
		this.url = url;
		this.changed();
	}

	public ElementModel getElement(String s) {
		for (final ModelElement e : this.elementContributions) {
			if (e instanceof ElementModel) {
				if (e.getName().equals(s)) {
					return (ElementModel) e;
				}
			}
		}
		return null;
	}

	public DocumentationContributionModel getDocumentation() {
		if (documentation==null){
			
		}
		return this.documentation;
	}

	public void setDocumentation(DocumentationContributionModel documentation) {
		this.documentation = documentation;
		this.changed();
	}

	
	public String getPath() {
		return this.getUrl();
	}

	public ModelElement getOwner() {
		return this;
	}

	
	public void updateDocumentationNodes(String oldName, String newName) {
		final DocumentationContributionModel documentation2 = this.getModel()
				.getDocumentation();
		final ModelElement owner = this.getOwner();
		documentation2.replace(owner.getPath() + "/" + oldName, owner.getPath()
				+ "/" + newName);
		final HashMap<String, DocumentationContribution> newCOntributions = new HashMap<String, DocumentationContribution>();
		for (final DocumentationContribution c : documentation2.getAll()
				.values()) {
			c.setTitle(c.getTitle().replace(oldName, newName));
			newCOntributions.put(c.getTitle(), c);
		}
		documentation2.setAll(newCOntributions);
	}

	public IRealm<String> getElementNames() {
		return this.elementNames;
	}

	public IRealm<String> getAttributeTypes() {
		return this.attributeTypes;
	}

	public Collection<ElementModel> getSubElements(ElementModel element2) {
		final ArrayList<ElementModel> elements = new ArrayList<ElementModel>();
		l2: for (final ModelElement el : this.elementContributions) {
			if (el instanceof ElementModel) {
				final ElementModel element = (ElementModel) el;
				final String name2 = element2.getName();
				if (!element.getName().equals(name2)) {
					final String[] superElement = element.getSuperElement();
					for (final String s : superElement) {
						if (s.equals(name2)) {
							elements.add(element);
							continue l2;
						}
					}
				}

			}
		}
		return elements;

	}


	public void setUrlExt(String declUrl) {
		this.declUrl=declUrl;
	}


	public String getDeclUrl() {
		return declUrl;
	}
}