package com.onpositive.semantic.realm.registries;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.model.api.property.java.annotations.Caption;
import com.onpositive.semantic.model.api.property.java.annotations.OrderMaintainer;
import com.onpositive.semantic.model.api.property.java.annotations.RealmProvider;
import com.onpositive.semantic.model.api.property.java.annotations.Required;
import com.onpositive.semantic.model.api.property.java.annotations.Unique;
import com.onpositive.semantic.model.api.roles.ImageManager;

public class ViewerTabConfiguration {

	private transient ViewerTabDefinition definition;
	
	private transient Image image;
	
	private transient ViewerConfiguration owner;
	
	private ArrayList<Integer> columnOrder;
	
	public ViewerConfiguration getOwner() {
		return owner;
	}

	public void setOwner(ViewerConfiguration owner) {
		this.owner = owner;
	}

	@Unique
	@Required
	@Caption("Tab Name")
	private String name;
	
	private String id;
	
	@RealmProvider(AvailableColumnsProvider.class)
	@OrderMaintainer(ColumnOrderMaintainer.class)
	private ArrayList<ColumnConfiguration>columns=new ArrayList<ColumnConfiguration>();
	
	private ArrayList<FilterConfiguration>filters=new ArrayList<FilterConfiguration>();

	
	
	public ViewerTabConfiguration(ViewerTabDefinition viewerTabDefinition) {
		setDefinition(viewerTabDefinition);
	}
	
	public ViewerTabConfiguration(){
		
	}
	
	public ViewerTabDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ViewerTabDefinition definition) {
		this.definition = definition;
		this.name=definition.getName();
		this.image=definition.getImage();
		this.id=definition.getId();
	}

	public String getName() {		
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ColumnConfiguration> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<ColumnConfiguration> columns) {
		this.columns = columns;
	}

	public ArrayList<FilterConfiguration> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<FilterConfiguration> filters) {
		this.filters = filters;
	}

	public void addColumn(ColumnConfiguration createColumnConfiguration) {
		columns.add(createColumnConfiguration);
	}
	
	public void addFilter(FilterConfiguration createColumnConfiguration) {
		filters.add(createColumnConfiguration);
	}

	public Image getImage() {
		if (image==null){
			image=SWTImageManager.getImage(NamedEntity.DEFAULT_ENTITY_IMAGE);
		}
		return image;
	}
	
	public int[] getColumnOrder(){
		if (columnOrder!=null){
		int[] z=new int[columnOrder.size()];
		for (int a=0;a<columnOrder.size();a++){
			z[a]=columnOrder.get(a);
		}
		return z;
		}
		return null;
	}

	public String getTooltipText() {
		return null;
	}
	
	public String toString(){
		return getName();
	}

	public String getId() {
		return id;
	}

	public void setColumnOrder(int[] columnOrder2) {
		this.columnOrder=new ArrayList<Integer>();
		for (int a=0;a<columnOrder2.length;a++){
			columnOrder.add(columnOrder2[a]);
		}
	}

	public void removeColumn(ColumnConfiguration columnConfiguration) {
		columns.remove(columnConfiguration);
	}
}
