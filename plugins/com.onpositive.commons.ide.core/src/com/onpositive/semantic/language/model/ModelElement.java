package com.onpositive.semantic.language.model;

import org.w3c.dom.Element;

import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.Required;

public abstract class ModelElement {

	protected String name;
	private String description;
	private String handler;
	private ModelElement owner;
	private String group;

	public void setName(String name) {
		if ((this.name != null) && this.name.equals(name)) {
			return;
		}
		final NameSpaceContributionModel model = this.getModel();
		if (model != null) {
			this.updateDocumentationNodes(this.name, name);
		}
		this.name = name;
		this.changed();

	}

	public abstract void updateDocumentationNodes(String oldName, String newName);

	public abstract String getPath();

	public void setDescription(String description) {
		this.description = description;
		this.changed();
	}

	@Required
	@Caption("Name")
	public String getName() {
		if (this.name == null) {
			return "";
		}
		return this.name;
	}

	@Caption("Description")
	public String getDescription() {
		if ((this.description == null)
				|| (this.description.trim().length() == 0)) {
			return "Description is not specified";
		}
		return this.description;
	}

	public ModelElement(Element el, ModelElement owner) {
		if (el != null) {
			this.name = el.getAttribute("name"); //$NON-NLS-1$
			this.description = el.getAttribute("description"); //$NON-NLS-1$
			final String attribute = el.getAttribute("handler");
			if (attribute.length() > 0) {
				this.handler = attribute;
			}
			final String group = el.getAttribute("group");
			if (group.length() != 0) {
				this.group = group;
			}
		}

		this.owner = owner;
	}

	public void store(Element element) {
		element.setAttribute("name", this.name);
		element.setAttribute("description",
				this.description != null ? this.description : "");
		if ((this.group != null) && (this.group.trim().length() > 0)) {
			element.setAttribute("group", this.group);
		}
		if (this.handler != null) {
			element.setAttribute("handler", this.handler);
		}
	}

	public ModelElement getOwner() {
		return this.owner;
	}

	public void setOwner(ModelElement owner) {
		this.owner = owner;
	}

	public void changed() {
		if (this.owner != null) {
			this.owner.changed();
		}
	}

	public String getHandler() {
		return this.handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getGroup() {
		return this.group != null ? this.group : "";
	}

	public void setGroup(String group) {
		this.group = group;
		this.changed();
	}

	public NameSpaceContributionModel getModel() {
		ModelElement owner = this;
		while (!(owner instanceof NameSpaceContributionModel)) {
			owner = owner.getOwner();
			if (owner == null) {
				break;
			}
		}
		return (NameSpaceContributionModel) owner;
	}

	public DocumentationContribution getDocumentationContribution() {
		final DocumentationContributionModel documentation = this.getModel()!=null?this.getModel()
				.getDocumentation():null;
		if (documentation != null) {
			return documentation.getElement(this.getPath());
		} else {
			return null;
		}
	}

}