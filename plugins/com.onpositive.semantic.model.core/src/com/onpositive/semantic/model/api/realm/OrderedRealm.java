package com.onpositive.semantic.model.api.realm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.onpositive.semantic.model.api.changes.INotifyableCollection;
import com.onpositive.semantic.model.api.changes.ISetDelta;
import com.onpositive.semantic.model.api.order.IOrderListener;
import com.onpositive.semantic.model.api.order.IOrderMaintainer;

public class OrderedRealm<T> extends CollectionBasedRealm<T> implements IModifiableRealm<T>,
		INotifyableCollection<T>,Serializable,IOrderMaintainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public OrderedRealm(T... elements) {
		this.collection = new ArrayList<T>(Arrays.asList(elements));
		registerService(IOrderMaintainer.class, this);		
	}

	public OrderedRealm(Collection<T> elements) {
		this.collection = new ArrayList<T>(elements);		
	}

	public OrderedRealm() {
		this.collection = new ArrayList<T>();
	}

	
	
	protected Collection<T> createCollection(Collection<T> elements) {
		return new ArrayList<T>(elements);
	}

	public int compare(Object arg0, Object arg1) {
		return 0;
	}

	protected HashSet<IOrderListener>l=new HashSet<IOrderListener>();

	public boolean canMove(Object obj, boolean direction) {
		if (obj instanceof List){
			List l=(List) obj;
			for (Object o:l){
				if (!canMove(o, direction)){
					return false;
				}
			}
			return true;
		}
		List collection2 = (List)collection;
		if (direction&&collection2.indexOf(obj)>0){
			return true; 
		}
		if (!direction&&collection2.indexOf(obj)<collection2.size()-1){
			return true; 
		}
		return false;
	}
	@Override
	protected void fireDelta(ISetDelta<T> dlt) {
		super.fireDelta(dlt);
	}

	public void addOrderListener(IOrderListener listener) {
		l.add(listener);
	}

	public void removeOrderListener(IOrderListener listener) {
		l.remove(listener);
	}

}