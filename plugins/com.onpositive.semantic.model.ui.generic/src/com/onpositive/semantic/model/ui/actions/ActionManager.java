package com.onpositive.semantic.model.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.roles.GatheringRoleMap;

public class ActionManager extends GatheringRoleMap<ActionObject> {

	public ActionManager() {
		super("com.onpositive.semantic.model.objectActions", ActionObject.class); //$NON-NLS-1$
	}

	private static ActionManager instance;

	public static ActionManager getInstance() {
		if (instance == null) {
			instance = new ActionManager();
		}
		return instance;
	}

	public void fillContributionManager(IContributionManager manager,
			IStructuredSelection selection, String role, String theme) {
		final HashSet<ActionObject> total = new HashSet<ActionObject>();
		for (final Object object : selection.toList()) {
			
			final ArrayList<ActionObject> elements = this.getElements(object,
					role, theme);
			total.addAll(elements);
			if (object instanceof ITreeNode<?>){
				final ArrayList<ActionObject> elements1 = this.getElements(((ITreeNode<?>) object).getElement(),
						role, theme);
				total.addAll(elements1);
			}
		}
		final HashSet<ActionObject> as = new HashSet<ActionObject>();
		for (final ActionObject a : total) {
			//if (a.isApplyable(selection)) {
				as.add(a);
			//}
		}
		final ArrayList<ActionObject> actionObjects = new ArrayList<ActionObject>(
				as);
		Collections.sort(actionObjects);
		for (final ActionObject o : actionObjects) {
			o.contribute(manager, selection, role);
		}
	}

}
