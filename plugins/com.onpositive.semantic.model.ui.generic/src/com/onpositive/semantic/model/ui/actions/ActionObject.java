package com.onpositive.semantic.model.ui.actions;

import java.util.ArrayList;

import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.roles.RoleObject;

public class ActionObject extends RoleObject {

	boolean inited;
	ArrayList<IActionContributor> contributors = new ArrayList<IActionContributor>();

	public ActionObject(IConfigurationElement element) {
		super(element);
		
	}

	public void contribute(IContributionManager manager,
			IStructuredSelection selection, String role) {
		if (!inited) {
			init();
		}
		for (IActionContributor a : contributors) {
			a.contributeActions(manager, selection, role);
		}
	}

	private void init() {
		IConfigurationElement[] children = fElement.getChildren();
		for (IConfigurationElement e : children) {
			if (e.getName().equals("contributor")) {
				try {
					contributors.add((IActionContributor) e
							.createExecutableExtension("class"));
				} catch (Exception e1) {
					Platform.log(e1);
				}
			}
		}
		inited = true;
	}

}
