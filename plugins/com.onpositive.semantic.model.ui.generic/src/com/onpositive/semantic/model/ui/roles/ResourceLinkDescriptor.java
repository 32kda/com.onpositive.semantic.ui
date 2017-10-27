package com.onpositive.semantic.model.ui.roles;

import java.io.InputStream;

import com.onpositive.commons.xml.language.IResourceLink;

public class ResourceLinkDescriptor implements ImageDescriptor {

	private static final long serialVersionUID = -8699782482907708663L;
	protected IResourceLink link;

	public ResourceLinkDescriptor(IResourceLink resourceAttribute) {
		this.link = resourceAttribute;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		return result;
	}

	public InputStream openStream() {
		return link.openStream();
	}

	public String location() {
		return link.location();
	}
	
	public boolean equals(Object obj) {

		try {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ResourceLinkDescriptor other = (ResourceLinkDescriptor) obj;
			if (link == null) {
				if (other.link != null)
					return false;
			} else if (!link.equals(other.link))
				return false;
			return false;
		} catch (Throwable e) {
			return false;
		}
	}


	public IResourceLink getLink() {
		return link;
	}

}
