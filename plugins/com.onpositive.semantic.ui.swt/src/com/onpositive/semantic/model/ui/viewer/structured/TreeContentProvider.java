package com.onpositive.semantic.model.ui.viewer.structured;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.ui.property.editors.structured.UIRealm;

public class TreeContentProvider implements ITreeContentProvider,ITreePathContentProvider {

	private IRealm<Object> currentRealm;
	private StructuredViewer vs;
	private String property;
	
	

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Object[] getChildren(Object parentElement) {
		IProperty property2 = PropertyProviderLookup
				.getProperty(parentElement, property);
		if (property2 != null) {
			Object value = property2.getValue(parentElement);
			if (value instanceof Object[]){
				return (Object[]) value;
			}
			if (value instanceof Collection<?>){
				Collection<?>r=(Collection<?>) value;
				return r.toArray();
			}
			if (value instanceof IRealm<?>){
				IRealm<Object>rs=(IRealm<Object>) value;
				rs.addRealmChangeListener( listener);
				listeners.add(rs);
				Object[] array = rs.getContents().toArray();
				return array;
			}
			return new Object[]{value};
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected HashSet<IRealm>listeners=new HashSet<IRealm>();

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		IProperty property2 = PropertyProviderLookup
				.getProperty(element, property);
		if (property2 != null) {
			return property2.hasValue(element) ;
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		IRealm<?> ra = (IRealm<?>) inputElement;
		HashSet<Object> notReferenced = new HashSet<Object>(ra.getContents());
		for (Object o : ra) {
			IProperty property2 = PropertyProviderLookup
					.getProperty(o, property);
			if (property2 != null) {
				Set<Object> values = property2.getValues(o);
				notReferenced.removeAll(values);
			}
		}
		return notReferenced.toArray();
	}

	private final IRealmChangeListener<Object> listener = new IRealmChangeListener<Object>() {

		public void realmChanged(IRealm<Object> realmn, ISetDelta<Object> delta) {
			clearListeners();
			if (Display.getCurrent() == null)
			{
				Display.getDefault().asyncExec(new Runnable(){

					public void run()
					{
						vs.refresh();			
					}
					
				});
			}
			else
				vs.refresh();
		}

		
	};
	@SuppressWarnings("unchecked")
	private void clearListeners() {
		for (IRealm r:listeners){
			r.removeRealmChangeListener(listener);
		}
	}

	public void dispose() {
		if (this.currentRealm != null) {
			this.currentRealm.removeRealmChangeListener(this.listener);
			this.currentRealm = null;
		}
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.vs = (StructuredViewer) viewer;
		if (this.currentRealm != newInput) {
			if (this.currentRealm != null) {
				this.currentRealm.removeRealmChangeListener(this.listener);
				clearListeners();
			}
			final IRealm<Object> newInput2 = (IRealm<Object>) newInput;
			if (newInput2 != null) {
				this.currentRealm = UIRealm.toUI(newInput2);
				this.currentRealm.addRealmChangeListener(this.listener);
			} else {
				this.currentRealm = null;
			}
		}
	}

	public Object[] getChildren(TreePath parentPath) {
		Object lastSegment = parentPath.getLastSegment();
		Object[] children = getChildren(lastSegment);
		HashSet<Object>mm=new HashSet<Object>();
		for (int a=0;a<parentPath.getSegmentCount();a++){
			mm.add(parentPath.getSegment(a));
		}
		ArrayList<Object>result=new ArrayList<Object>();
		for (Object o:children){
			if (!mm.contains(o)){
				result.add(o);
			}
		}
		return result.toArray();
	}

	public TreePath[] getParents(Object element) {
		return new TreePath[0];
	}

	public boolean hasChildren(TreePath path) {
		return hasChildren(path.getLastSegment());
	}
}
