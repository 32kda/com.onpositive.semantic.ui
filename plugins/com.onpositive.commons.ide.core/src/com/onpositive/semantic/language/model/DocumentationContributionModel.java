package com.onpositive.semantic.language.model;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentationContributionModel {

	private HashMap<String, DocumentationContribution> contributions = new HashMap<String, DocumentationContribution>();
	private final NameSpaceContributionModel owner;
	private File location;
	private IResourceLoader loader;

	public IResourceLoader getLoader() {
		return loader;
	}

	public void setLoader(IResourceLoader loader) {
		this.loader = loader;
	}

	public Map<String, DocumentationContribution> getAll() {
		return new HashMap<String, DocumentationContribution>(
				this.contributions);
	}

	public DocumentationContributionModel(NameSpaceContributionModel owner,
			Document document) {
		final Element documentElement = document.getDocumentElement();
		final NodeList childNodes = documentElement.getChildNodes();
		for (int a = 0; a < childNodes.getLength(); a++) {
			final Node item = childNodes.item(a);
			if (item instanceof Element) {
				final Element el = (Element) item;
				final DocumentationContribution cm = new DocumentationContribution(
						this);
				cm.setTitle(el.getAttribute("ref"));
				cm.setContents(el.getTextContent());
				cm.setIcon(el.getAttribute("icon"));
				final String title = cm.getTitle();
				this.contributions.put(title, cm);
			}
		}
		this.owner = owner;
	}

	public DocumentationContributionModel(NameSpaceContributionModel model) {
		this.owner = model;
	}

	public void store(Document document) {
		document.appendChild(document.createElement("documentation"));
		for (final DocumentationContribution c : this.contributions.values()) {
			if (c.isEmpty()) {
				continue;
			}
			final Element createElement = document
					.createElement("documentation");
			createElement.setAttribute("ref", c.getTitle());
			createElement.setAttribute("icon", c.getIcon());
			createElement.setTextContent(c.getContents());
			document.getDocumentElement().appendChild(createElement);
		}
	}

	public void changed() {
		if (this.owner != null) {
			this.owner.changed();
		}
	}

	public DocumentationContribution getElement(String path) {
		final DocumentationContribution documentationContribution = this.contributions
				.get(path);
		if (documentationContribution == null) {
			final DocumentationContribution cm = new DocumentationContribution(
					this);
			cm.setTitle(path);
			this.contributions.put(path, cm);
			return cm;
		}
		return documentationContribution;
	}

	public void replace(String string, String string2) {
		final DocumentationContribution documentationContribution = this.contributions
				.get(string);
		if (documentationContribution != null) {
			documentationContribution.setTitle(string2);
			this.contributions.put(string2, documentationContribution);
			this.contributions.remove(string);
		}
	}

	public void setAll(
			HashMap<String, DocumentationContribution> newCOntributions) {
		this.contributions = newCOntributions;
	}

	public File getLocation() {
		return location;
	}

	public void setLocation(File file) {
		this.location=file;
	}

	public URL getResource(String name) {
		if (loader!=null){
			return loader.getResource(name);
		}
		return null;
	}
}
