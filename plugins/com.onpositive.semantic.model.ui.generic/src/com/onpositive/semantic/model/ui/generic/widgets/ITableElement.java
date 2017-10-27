package com.onpositive.semantic.model.ui.generic.widgets;

import java.util.List;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.Column;


public interface ITableElement<T>extends IListElement<T> {

	

	@HandlesAttributeDirectly("headerVisible")
	public void setHeaderVisible(boolean headerVisible);
	
	@HandlesAttributeDirectly("linesVisible")
	public void setLinesVisible(boolean linesVisible) ;	

	@HandlesAttributeDirectly("imageOnFirstColumn")
	void setImageOnFirstColumn(boolean parseBoolean);
	
	public boolean isImageOnFirstColumn() ;
	
	public boolean isLinesVisible() ;
	
	public boolean isHeaderVisible() ;

	public void setColumns(List<Column> columns);


}
