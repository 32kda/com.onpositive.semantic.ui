package com.onpositive.semantic.model.ui.property.editors.structured;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observer;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class ColumnLocking  {
	
	private ITableAdapter table;
	private ColumnMovementListener columnMovementListener = new ColumnMovementListener();
	
	private int[] columnOrder;
	
	private Set lockedColumns = new HashSet();
	private Observer orderListener;
	
	public void dispose(){
		int colCount = table.getColumnCount();
		for (int i = 0; i < colCount; ++i){
			table.getColumn(i).removeListener(SWT.Move, columnMovementListener);
		}
	}
	
	private class ColumnMovementListener implements Listener{
		public void handleEvent(Event event) {
			Set movedColumns = new HashSet(2);			
			int[] newColumnOrder = table.getColumnOrder();
			if (Arrays.equals(newColumnOrder, columnOrder)){
				return;
			}
			if (newColumnOrder.length>columnOrder.length){
				int[] no=new int [newColumnOrder.length];
				System.arraycopy(columnOrder, 0, no, 0, columnOrder.length);
				for (int b=columnOrder.length;b<newColumnOrder.length;b++){
					no[b]=newColumnOrder[b];
				}
				columnOrder=newColumnOrder;
			}
			for (int i = 0; i < newColumnOrder.length; ++i){
				if (columnOrder[i] != newColumnOrder[i]){
					movedColumns.add(table.getColumn(newColumnOrder[i]).getAdaptee());
				}
			}
			
			boolean lockedWasMoved = false;
			
			for (Iterator iter = movedColumns.iterator(); iter.hasNext(); ){
				Object movedColumn = iter.next();
				if (lockedColumns.contains(movedColumn)){
					lockedWasMoved = true;
					break;
				}
			}			
			if (lockedWasMoved){
				table.setColumnOrder(columnOrder);
			}
			else{
				columnOrder = newColumnOrder;
			}
			if (orderListener!=null){
				orderListener.update(null, columnOrder);
			}
		}
	}
	
	public ColumnLocking(ITableAdapter table, int[] lockedColumnIdxs) {
		super();
		this.table = table;
		this.columnOrder = table.getColumnOrder();
		
		for (int i = 0; i < lockedColumnIdxs.length; ++i){
			int idx = lockedColumnIdxs[i];
			IColumnAdapter columnAdapter = table.getColumn(idx);
			columnAdapter.setMoveable(false);
			lockedColumns.add(columnAdapter.getAdaptee());
		}
		
		int colCount = table.getColumnCount();
		for (int i = 0; i < colCount; ++i){
			table.getColumn(i).addListener(SWT.Move, columnMovementListener);
		}
	}

	public void addOrderListener(Observer observer) {
		this.orderListener=observer;
	}
}
