package com.onpositive.semantic.model.ui.property.editors.structured;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;


public final class SelectionConverter {

	private SelectionConverter(){		
	}
	
	public static IStructuredSelection from(final org.eclipse.jface.viewers.IStructuredSelection sel){
		return new IStructuredSelection() {
			
			public List<? extends Object> toList() {
				return sel.toList();
			}
			
			public boolean isEmpty() {
				if( sel == null )
					return false ;
				return sel.isEmpty();
			}
			
			public Object getFirstElement() {
				return sel.getFirstElement();
			}
		};
	}
	
	public static org.eclipse.jface.viewers.IStructuredSelection to(IStructuredSelection sel){
		return new StructuredSelection(sel.toList());
	}
}
