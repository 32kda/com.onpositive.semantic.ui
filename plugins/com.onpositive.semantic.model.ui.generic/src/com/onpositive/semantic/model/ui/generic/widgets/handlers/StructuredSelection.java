package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.onpositive.semantic.model.ui.generic.IStructuredSelection;

public class StructuredSelection implements IStructuredSelection,Serializable{

	Collection<Object>contents;
	
	public StructuredSelection(){}
	public StructuredSelection(Object[] array) {
		contents=Arrays.asList(array);
	}
	
	public int size(){
		if (contents==null){
			return 0;
		}
		return contents.size(); 
	}

	public List<? extends Object> toList() {
		if (contents==null){
			return Collections.emptyList();
		}
		return new ArrayList<Object>(contents);
	}
	public boolean isEmpty() {
		if (contents==null){
			return true;
		}
		return contents.isEmpty();
	}
	public Object getFirstElement() {
		if (contents==null){
			return null;
		}
		if (contents.isEmpty()){
			return null;
		}
		return contents.iterator().next();
	}

}
