package com.onpositive.semantic.model.ui.property.editors.structured;
import org.eclipse.swt.widgets.Tree;


public class TreeAdapter implements ITableAdapter {
	
	Tree tree;

	public TreeAdapter(Tree tree) {
		super();
		this.tree = tree;
	}

	public IColumnAdapter getColumn(int index) {
		return new TreeColumnAdapter(tree.getColumn(index));
	}

	public int getColumnCount() {
		return this.tree.getColumnCount();
	}

	public int[] getColumnOrder() {
		return this.tree.getColumnOrder();
	}

	public void setColumnOrder(int[] order) {
		this.tree.setColumnOrder(order);
	}

}
