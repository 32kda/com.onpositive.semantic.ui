package com.onpositive.semantic.language.model;

import java.io.File;
import java.net.URL;

public class DocumentationContribution {

	String title;

	String contents;

	String icon;

	public final String getIcon() {
		if (icon == null) {
			return "";
		}
		return icon;
	}

	public final void setIcon(String icon) {
		this.icon = icon;
		this.owner.changed();
	}

	DocumentationContributionModel owner;

	public DocumentationContribution(DocumentationContributionModel owner) {
		super();
		this.owner = owner;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		if (title != null && !title.equals(this.title)) {
			boolean fire = this.title != null;

			this.title = title;
			if (fire) {
				this.owner.changed();
			}
		}
	}

	public String getContents() {
		if (contents == null) {
			return "";
		}
		return this.contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
		this.owner.changed();
	}

	public boolean isEmpty() {
		if (this.contents == null) {
			return true;
		}
		return this.contents.trim().length() == 0;
	}

	public URL getResource(String name) {
		return owner.getResource(name);
	}

	public File getLocation() {
		return owner.getLocation();
	}

}
