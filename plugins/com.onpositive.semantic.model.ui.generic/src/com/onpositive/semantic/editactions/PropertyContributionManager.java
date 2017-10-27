package com.onpositive.semantic.editactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onpositive.semantic.model.api.expressions.LyfecycleUtils;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.method.IMethodEvaluator;
import com.onpositive.semantic.model.api.method.MethodAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.RealmAccess;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.ContributionManager;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.actions.IContributionItem;
import com.onpositive.semantic.model.ui.actions.IPopulatingContributionManager;
import com.onpositive.semantic.model.ui.actions.Separator;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;

public class PropertyContributionManager extends ContributionManager implements IPopulatingContributionManager{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3420228692990018306L;
	protected Object[] baseObjects;
	protected IContributionItem[] items;
	protected IPropertyProvider propertyProvider;
	private IBinding binding;
	
	public PropertyContributionManager() {
		super(IAction.AS_DROP_DOWN_MENU);
		setText("Modify object");
	}

	@Override
	public IContributionItem[] getItems() {
		if (items == null) {
			items = internalGetItems();
		}
		return items;
	}

	protected IContributionItem[] internalGetItems() {
		if (baseObjects == null || baseObjects.length == 0)
			return new IContributionItem[0];
		Map<String, List<IContributionItem>> categories = new HashMap<String, List<IContributionItem>>();
		List<IContributionItem> commonResults = new ArrayList<IContributionItem>();
		Iterable<IProperty> properties;
		if (propertyProvider != null)
			properties = propertyProvider.getProperties(baseObjects[0]);
		else
			properties = PropertyAccess.getProperties(baseObjects[0]);
		if (properties instanceof Collection) {
			for (int i = 0; i < baseObjects.length; i++) {
				Iterable<IProperty> currentProps = PropertyAccess.getProperties(baseObjects[i]);
				if (currentProps instanceof Collection)
					((Collection<IProperty>) properties).retainAll((Collection<?>) currentProps);
				else
					return new IContributionItem[0];
			}
		}
		
		for (IProperty property : properties) {
			if (shouldCreateAction(property)) {
				IContributionItem action = createAction(property);
				String category = getCategory(property);
				if (category != null) {
					putToCategory(categories, category, action);
				} else
					commonResults.add(action);
			}
		}
		List<IContributionItem> categoryItems = new ArrayList<IContributionItem>();
		for (String categoryName : categories.keySet()) {
			ContributionManager manager = new ContributionManager(IAction.AS_DROP_DOWN_MENU);
			manager.setText(categoryName);
			List<IContributionItem> actionList = categories.get(categoryName);
			for (IContributionItem item : actionList) {
				manager.add(item);
			}
			categoryItems.add(manager);
		}
		Collections.sort(categoryItems, new Comparator<IContributionItem>() {

			@Override
			public int compare(IContributionItem o1, IContributionItem o2) {
				if (((Action) o1).getText() != null)
					return ((Action) o1).getText().compareTo(((Action) o2).getText());
				else if (((Action) o2).getText() != null)
					return -((Action) o2).getText().compareTo(((Action) o1).getText());
				return 0;
			}
			
		});
		commonResults.addAll(0,categoryItems);
		
		List<Action> methodActionList = new ArrayList<Action>();
		Iterable<IMethodEvaluator> availableEvaluators = MethodAccess.getAvailableEvaluators(baseObjects[0]);
		for (IMethodEvaluator evaluator : availableEvaluators) {
			if (shouldCreateAction(evaluator)) {
				MethodAction action = new MethodAction(binding, baseObjects, evaluator);
				String name = DefaultMetaKeys.getCaption(evaluator);
				if (name==null||name.isEmpty())
					name = evaluator.getMethodName();
				action.setText(name);
				methodActionList.add(action);
			}
		}
		if (methodActionList.size() > 0) {
			commonResults.add(new Separator());
			commonResults.addAll(methodActionList);
		}
		
		return commonResults.toArray(new IContributionItem[0]);
	}

	private void putToCategory(Map<String, List<IContributionItem>> categories,
			String category, IContributionItem action) {
		List<IContributionItem> list = categories.get(category); 
		if (list == null) {
			list = new ArrayList<IContributionItem>();
			categories.put(category,list);
		}
		list.add(action);
	}

	protected IContributionItem createAction(IProperty property) {
		if (Boolean.class.equals(property.getMeta().getSingleValue(DefaultMetaKeys.SUBJECT_CLASS_KEY,Class.class,null))){
			return new BooleanPropAction(binding, property, baseObjects);
		}
		IRealm<Object> realm;
		realm = getRealm(property);
		try{
		if (realm != null) {
			Boolean multivalue = property.getMeta().getSingleValue(DefaultMetaKeys.MULTI_VALUE_KEY,Boolean.class,null);
			if (multivalue == null)
				multivalue = false;
			return createRealmContribution(property,realm, multivalue);
		} else {
			return createEditValueContribution(property);
		}
		}finally{
			LyfecycleUtils.disposeIfShortLyfecycle(realm);
		}
	}

	private IRealm<Object> getRealm(IProperty property) {
		IRealm<Object> realm;
		if (baseObjects.length == 1)
			realm = RealmAccess.getRealm(property,baseObjects[0]);
		else 
			realm = RealmAccess.getRealm(property,null);
		return realm;
	}

	protected IContributionItem createEditValueContribution(IProperty property) {
		return new EditPropertyValueAction(binding, property,baseObjects,Action.AS_PUSH_BUTTON);
	}

	protected IContributionItem createRealmContribution(IProperty property,
			IRealm<Object> realm, boolean multivalue) {
		ContributionManager realmContributionManager = new ContributionManager(IAction.AS_DROP_DOWN_MENU);
		if (!DefaultMetaKeys.isFixedBound(property)) {
			realmContributionManager.add(new EditPropertyValueAction(binding, property,baseObjects,IAction.AS_PUSH_BUTTON, "Other..."));
			realmContributionManager.add(new Separator());
		}
		realmContributionManager.setText("Set " + property.getId() + " to");
		int style = Action.AS_CHECK_BOX;
		if (!multivalue)
			style = Action.AS_RADIO_BUTTON;
		for (Object object : realm) {
			realmContributionManager.add(new SetPropertyAction(binding, property,baseObjects,object,style));
		}
		setActionLook(realmContributionManager,property);
		return realmContributionManager;
	}	
	
	protected void setActionLook(IAction actionContribution, IProperty property) {
		String name = DefaultMetaKeys.getCaption(property);
		if (name != null)
			actionContribution.setText(name);
		String imageKey = DefaultMetaKeys.getImageKey(property);
		if (!imageKey.isEmpty()) {
			ImageDescriptor descriptor = ImageManager.getImageDescriptorByPath(this.baseObjects[0], imageKey);
			if (descriptor != null)
				actionContribution.setImageDescriptor(descriptor);
		}
	}

	protected boolean shouldCreateAction(IProperty property) {
		IHasMeta meta = MetaAccess.getMeta(property); //TODO correct determination of available actions
		if (DefaultMetaKeys.isId(property))
			return false;
		if (isReadonly(property))
			return false;
		if (Boolean.FALSE.equals(meta.getMeta().getSingleValue(DefaultMetaKeys.PERSISTENT,Boolean.class,null))) {
			return false;
		}
		IRealm<Object> realm = getRealm(property);
		if (realm != null)
			return true;
		LyfecycleUtils.disposeIfShortLyfecycle(realm);
		
		return (String.class.equals(PropertyAccess.getSubjectClass(property)) || property.getMeta().getService(ILabelLookup.class) != null);
	}
	
	protected boolean isSupportedType(Class<?> type) {
		if (type == null)
			return true;
		return 
				(String.class.equals(type) || Integer.class.equals(type) || Long.class.equals(type) ||
			Short.class.equals(type) || Double.class.equals(type) || Float.class.equals(type) ||
			Boolean.class.equals(type) || Character.class.equals(type));
	}

	protected boolean isReadonly(IProperty property) {
		for (Object object : baseObjects) {
			if (PropertyAccess.isReadonly(property,object))
				return true;
		}
		return false;
	}

	protected boolean shouldCreateAction(IMethodEvaluator evaluator) {
		return DefaultMetaKeys.isUserRunnable(evaluator);
	}

	public Object getBaseObject() {
		return baseObjects;
	}
	
	protected String getCategory(IProperty property) {
		return DefaultMetaKeys.getCategory(property.getMeta());
	}

	public void setBaseObjects(Object[] baseObjects, IBinding iBinding) {
		this.baseObjects = baseObjects;
		this.binding=iBinding;
		refreshItems();
	}

	protected void refreshItems() {
		items = internalGetItems();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseObjects == null) ? 0 : baseObjects.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyContributionManager other = (PropertyContributionManager) obj;
		if (baseObjects == null) {
			if (other.baseObjects != null)
				return false;
		} else if (!baseObjects.equals(other.baseObjects))
			return false;
		return true;
	}

	public IPropertyProvider getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(IPropertyProvider propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

}
