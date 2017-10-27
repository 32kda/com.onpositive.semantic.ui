package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import java.lang.reflect.Method;

import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Layout;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.model.ui.generic.ColumnLayoutData;
import com.onpositive.semantic.model.ui.generic.IColumnContoller;

public abstract class AbstractController implements IColumnContoller {

	protected Item item;
	protected Layout layout;
	protected ViewerColumn clm;

	public Item getItem() {
		return this.item;
	}

	public AbstractController(ViewerColumn column, Item item, Layout layout) {
		super();
		this.item = item;
		item.setData(Policy.JFACE + ".columnViewer", null);
		this.layout = layout;
		
		this.clm = column;
	}
	
	

	public void setText(String text) {
		
		if (text == null) {
			text = "";
		}
		else if (text.trim()==null){
			text="";
		}
		this.item.setText(text);
	}

	public void setImage(String image) {
		if (image != null) {
			final Image imageActual = SWTImageManager.getImage(image);
			this.item.setImage(imageActual);
		} else {
			this.item.setImage(null);
		}
	}

	public abstract void setMovable(boolean movable);

	public abstract void setResizable(boolean resizable);

	public abstract void setTooltipText(String tooltip);

	public void setLayoutData(ColumnLayoutData ld){
		org.eclipse.jface.viewers.ColumnLayoutData l=null;
		int width = ld.getWidth();
		if (width<0){
			width=0;
		}
		if (ld.isResizeable()){
			l=new ColumnWeightData(ld.getGrowth(), width,true);
		}
		else{
			l=new ColumnPixelData(width);
		}
		setLayoutData(l);
	}

	protected abstract void setLayoutData(org.eclipse.jface.viewers.ColumnLayoutData l);

	protected abstract void setSortColumn(boolean up);
	protected abstract int getIndex();

	public void dispose() {
		this.item.dispose();
	}

	public void setEditingSupport(EditingSupport support) {
		try{
		Method declaredMethod = ColumnViewer.class.getDeclaredMethod("getViewerColumn", int.class);
		declaredMethod.setAccessible(true);
		clm=(ViewerColumn) declaredMethod.invoke(clm.getViewer(), getIndex());
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		this.clm.setEditingSupport(support);
		
	}

	public int getWidth() {
		final org.eclipse.jface.viewers.ColumnLayoutData ls = (org.eclipse.jface.viewers.ColumnLayoutData) this.item
				.getData(AbstractColumnLayout.LAYOUT_DATA);
		if (ls instanceof ColumnPixelData) {
			final ColumnPixelData pa = (ColumnPixelData) ls;
			return pa.width;
		}
		return -1;
	}

	public abstract ColumnViewer getViewer();

}