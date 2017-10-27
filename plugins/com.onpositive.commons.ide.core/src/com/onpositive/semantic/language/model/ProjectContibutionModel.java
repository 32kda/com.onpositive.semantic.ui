package com.onpositive.semantic.language.model;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

public class ProjectContibutionModel {

	private final IProject project;
	private final ArrayList<NameSpaceContributionModel> ns;

	public ProjectContibutionModel(ArrayList<NameSpaceContributionModel> ns,
			IProject pr) {
		super();
		this.ns = ns;
		this.project = pr;
	}

	public IProject getProject() {
		return this.project;
	}

	public ArrayList<NameSpaceContributionModel> getNamespaces() {
		return this.ns;
	}
}
