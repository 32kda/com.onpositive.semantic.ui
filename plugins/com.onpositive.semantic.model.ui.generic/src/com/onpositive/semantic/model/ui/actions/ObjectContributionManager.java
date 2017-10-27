package com.onpositive.semantic.model.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;

public class ObjectContributionManager extends ContributionManager implements
		IObjectContributionManager {

	private static final long serialVersionUID = -4743638661494576880L;
	
	Map<IContributionItem, Class<?>> targetClasses = new HashMap<IContributionItem, Class<?>>();

	@Override
	public IContributionItem[] getItems(IStructuredSelection selection) {
		List<IContributionItem> matchedItems = new ArrayList<IContributionItem>();
		IContributionItem[] items = getItems();
		if (targetClasses.size() == 0) //No object contributions - nothing to filter
			return items;
		if (selection.isEmpty()) { //Empty selection - filter out all object contributions
			matchedItems.addAll(Arrays.asList(items));
			matchedItems.removeAll(targetClasses.keySet());
		}
		List<? extends Object> selectedList = selection.toList();
		for (int i = 0; i < items.length; i++) {
			Class<?> targetClass = targetClasses.get(items[i]);
			if (targetClass == null) {//No target class specified
				matchedItems.add(items[i]);
			} else {
				boolean match = true;
				for (Object object : selectedList) {
					if (!targetClass.isAssignableFrom(object.getClass())) {
						match = false;
						break;
					}
				}
				if (match) {
					matchedItems.add(items[i]);
				}
			}
		}
		return matchedItems.toArray(new IContributionItem[0]);
	}

	@Override
	public void add(IContributionItem item, Class<?> targetClass) {
		super.add(item);
		targetClasses.put(item,targetClass);
	}

	@Override
	public void addAfter(String id, IContributionItem item, Class<?> targetClass) {
		super.addAfter(id,item);
		targetClasses.put(item,targetClass);
	}

}
