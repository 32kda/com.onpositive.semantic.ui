package com.onpositive.semantic.model.tree2;

import java.util.LinkedHashSet;

import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.tree.LeafNode;

@SuppressWarnings("unchecked")
public class BasicNode extends RemoteTreeNode implements Comparable<ITreeNode<?>>,ISlowNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LinkedHashSet<Object>obj=new LinkedHashSet<Object>();
	IClusterizationPoint<?>point;
	public BasicNode(IClusterizationPoint<?> element, ITreeNode<?> parent) {
		super(element, parent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ITreeNode<Object>[] getChildren() {
		ITreeNode[]r=new ITreeNode[obj.size()];
		int a=0;
		for (Object o:obj){
			r[a++]=new LeafNode(this, o);
		}
		return r;
	}
	
	@Override
	public boolean contains(Object o) {
		if (o instanceof ITreeNode<?>){
			ITreeNode<?>r=(ITreeNode<?>) o;
			if (obj.contains(r.getElement())){
				return true;
			}
		}
		return obj.contains(o);
	}



	@Override
	public int compareTo(ITreeNode<?> arg0) {
		Object element2 = arg0.getElement();
		if (element2!=null&&this.element!=null){
			try{
			Comparable d=(Comparable<?>) element2;
			Comparable d1=(Comparable<?>) element;
			return d.compareTo(d1);
			}catch (Exception e) {
				
			}
		}
		String label = LabelAccess.getLabel(element2);
		String label2 = LabelAccess.getLabel(element);
		return label.compareTo(label2);
	}

}
