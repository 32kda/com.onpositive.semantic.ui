package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import com.onpositive.semantic.model.api.property.IOrderListener;
import com.onpositive.semantic.model.api.property.IOrderMaintainer;
import com.onpositive.semantic.model.realm.IRealm;

public class ColumnOrderMaintainer implements IOrderMaintainer {

	protected ViewerTabConfiguration config;
	private ArrayList<Object> contents;
	protected HashSet<IOrderListener> listeners = new HashSet<IOrderListener>();

	public int compare(Object o1, Object o2) {
		return contents.indexOf(o1)-contents.indexOf(o2);
	}

	public void init(Object base, Object value) {
		config = (ViewerTabConfiguration) base;
	}

	
	public boolean canMove(Object obj, boolean direction) {
		if (!direction) {
			return contents.get(0) != obj;
		} else {
			return contents.get(contents.size() - 1) != obj;
		}
	}

	
	public void move(Object obj, boolean up) {
		int indexOf = contents.indexOf(obj);
		int a1 = up ?  indexOf + 1:indexOf-1;
		Object object = contents.get(a1);
		contents.set(a1, obj);
		contents.set(indexOf, object);
		fireChange();
	}

	
	public void setRealm(IRealm<Object> realm) {
		this.contents = new ArrayList<Object>(realm.getContents());
		Collections.sort(contents, this);
		Collections.sort(contents,new Comparator<Object>(){

			
			public int compare(Object o1, Object o2) {
				ColumnConfiguration c1 = (ColumnConfiguration) o1;
				ColumnConfiguration c2 = (ColumnConfiguration) o1;
				int priority1 = c1.getDefinition().priority();
				int priority2 = c2.getDefinition().priority();
				return priority1 - priority2;				
			}
			
		});
		
	}

	
	public void addOrderListener(IOrderListener listener) {
		listeners.add(listener);
	}

	
	public void removeOrderListener(IOrderListener listener) {
		listeners.remove(listener);
	}

	protected void fireChange(){
		for (IOrderListener l:listeners){
			l.orderChanged();
		}
	}
}
