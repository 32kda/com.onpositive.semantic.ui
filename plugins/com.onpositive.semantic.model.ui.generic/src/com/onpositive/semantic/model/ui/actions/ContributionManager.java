package com.onpositive.semantic.model.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import com.onpositive.commons.xml.language.ChildSetter;

/**
 * Base class for abstraction maintaining list of contribution items(actions & etc)
 * @author Pavel
 *
 */
public class ContributionManager extends Action implements IContributionManager{
	
	private final class InnerListener implements PropertyChangeListener ,Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void propertyChange(PropertyChangeEvent arg0) {
			support.firePropertyChange(arg0);		
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<IContributionItem>items=new ArrayList<IContributionItem>();
	private HashSet<Integer> hashSet = new HashSet<Integer>() ;  
	
	public ContributionManager() {
		super(IAction.AS_PUSH_BUTTON);
	}
	
	public ContributionManager(int style) {
		super(style);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	
	final PropertyChangeListener listener = new InnerListener();

	@ChildSetter( value = "contributionItem" ,
		      needCasting = true )
	public void add(IContributionItem item) {
		
		if ( !hashSet.add( item.hashCode() ) )
			return ;
		
		items.add(item);		
		item.addPropertyChangeListener(listener);
		support.fireIndexedPropertyChange("items", items.size(), null, item);
	}
	
	public void remove(IContributionItem item) {
		
		if ( !hashSet.remove( item.hashCode() ) )
			return ;
		
		items.remove(item);
		item.removePropertyChangeListener(listener);
		support.fireIndexedPropertyChange("items", items.size(), item, null);
	}
	public void addAfter(String id,IContributionItem item) {
		IContributionItem find = find(id);
		if (find==null){
			return;
		}
		int indexOf = items.indexOf(find);
		items.add(indexOf, item);				
		item.addPropertyChangeListener(listener);
		support.fireIndexedPropertyChange("items", items.size(), null, item);
	}
	
	
	
	public IContributionItem find(String id){
		for (IContributionItem i:items){
			if (i.id()!=null&&i.id().equals(id)){
				return i;
			}
		}
		return null;
	}

	public IContributionItem[] getItems() {
		return items.toArray(new IContributionItem[items.size()]);
	}

	public void run() {
		
	}	
}