package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

import com.onpositive.commons.xml.language.HandlesAttributeDirectly;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class EditableListEnumeratedValueSelector extends
		ListEnumeratedValueSelector<Object> {

	private Column column;
	
	protected void configureViewer(TableViewer newCheckList) {
		super.configureViewer(newCheckList);
		final ColumnViewerEditorActivationStrategy columnViewerEditorActivationStrategy = new ActivationStrategy(
				newCheckList);
		TableViewerEditor.create(newCheckList,
				columnViewerEditorActivationStrategy,
				ColumnViewerEditor.DEFAULT);
		if (this.column != null) {
			this.column.setOwnerSelector(this);
			this.column.setController(new TableColumnController(newCheckList,
					(TableColumn) this.columnWidget, null));
		}		
	}
	
	protected void configureViewer(TreeViewer newCheckList) {
		super.configureViewer(newCheckList);
		final ColumnViewerEditorActivationStrategy columnViewerEditorActivationStrategy = new ActivationStrategy(
				newCheckList);
		TreeViewerEditor.create(newCheckList,
				columnViewerEditorActivationStrategy,
				ColumnViewerEditor.DEFAULT);
		if (this.column != null) {
			this.column.setOwnerSelector(this);
			this.column.setController(new TreeColumnController(newCheckList,
					(TreeColumn) this.columnWidget, null));
		}
		
	}

	public String getDirectCellEditProperty() {
		if (this.column != null) {
			return this.column.getId();
		}
		return null;
	}

	protected void handleChange(Object extraData) {
		super.handleChange(extraData);
		if (this.column != null) {
			this.column.changed(extraData);
		}
	}

	@HandlesAttributeDirectly("directEditProperty")
	public void setDirectCellEditProperty(String property) {
		this.column = new Column("", property);
		this.column.setCacheElements(false);
	}

	public void setColumn(Column column) {
		this.column = column;
	}


}
