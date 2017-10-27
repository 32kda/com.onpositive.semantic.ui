package com.onpositive.semantic.ui.snippets;

import com.onpositive.commons.Activator;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.model.api.expressions.IValueListener;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;


public class Snippet025MyFirstSnippet extends AbstractSnippet
{
	
	Realm<String> powtGroups = new Realm<String>("T-31", "T-32", "T-33", "T-34");	
	Realm<String> pmGroups = new Realm<String>("T-31", "T", "de-33", "dede-34");
	Realm<String> imGroups = new Realm<String>("T", "dede-32", "dede33", "dede");

	// start of data model
	@Caption("Full Name: ")
	@Required
	String fio;
	
	@Required("Age should be specified!")
	@Caption("Age: ")
	int age;

	@RealmProvider(KnownSpecialities.class)
	@Required
	@Caption("You should have specialization")
	String spec;

	@RealmProvider(KnownGroups.class)
	@Required
	@Caption("Group")
	String group;

	public static class KnownSpecialities implements IRealmProvider<String> {

		public IRealm<String> getRealm(IBinding options) {
			return new Realm<String>(
					"Developer", "IT", "Sales"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		}
	}
	
	public static class KnownGroups implements IRealmProvider<String>
	{
		protected static Realm groups;
		
		public IRealm<String> getRealm(IBinding model)
		{
			return groups;
		}
		
		/**
		 * @return the groups
		 */
		public static Realm getGroups()
		{
			return groups;
		}

		/**
		 * @param groups the groups to set
		 */
		public static void setGroups(Realm newGroups)
		{
			groups = newGroups;
		}
		
	}

	// end data model
	
	
	protected AbstractUIElement<?> createContent()
	{
		KnownGroups.setGroups(powtGroups);
		try {
			final Binding context = new Binding(this);
			final AbstractUIElement<?> evaluateLocalPluginResource = (AbstractUIElement<?>) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							Snippet017XMLUIElementSnippet.class,
							"snippet025.dlf", context); //$NON-NLS-1$
			context.getBinding("spec").addValueListener(new IValueListener(){

				public void valueChanged(Object oldValue, Object newValue)
				{
					if (newValue.equals("Developer")) KnownGroups.setGroups(powtGroups);
					else if (newValue.equals("IT")) KnownGroups.setGroups(pmGroups);
					else if (newValue.equals("Sales")) KnownGroups.setGroups(imGroups);
					
					context.getBinding("group").setRealm(KnownGroups.getGroups());
					//context.getBinding("group").setValue(null,null);
				}
				
			});
			DisposeBindingListener.linkBindingLifeCycle(context,
					evaluateLocalPluginResource);
			return evaluateLocalPluginResource;

		} catch (final Exception e) {
			Activator.log(e);
		}
		return null;
	}

	
	protected String getDescription()
	{
		return "32kda's first snippet";
	}

	
	public String getGroup()
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	protected String getName()
	{
		return "32kda's first snippet";
	}

}
