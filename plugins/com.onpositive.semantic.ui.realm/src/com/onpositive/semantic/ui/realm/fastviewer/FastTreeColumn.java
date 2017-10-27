package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.Comparator;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.graphics.Image;

public class FastTreeColumn {

	private String title;
	private String tooltipTitle;
	private int layoutData;
	private IColumnRenderer renderer;
	private Comparator comparator;
	private int weight;
	private Image image;
	private boolean resizable=true;
	public final boolean isResizable() {
		return resizable;
	}

	public final void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	private final String id;
	private boolean isRequired;

	public void setRequired() {
		this.isRequired = true;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public FastTreeColumn(String title, String tooltipTitle, int weight,
			int charCount, String id) {
		super();
		this.title = title;
		this.tooltipTitle = tooltipTitle;
		this.layoutData = charCount;
		this.weight = weight;
		this.id = id;
	}

	public ColumnLayoutData getLayoutData(int ch) {
		if (weight == 0) {
			return new ColumnPixelData(layoutData * ch);
		}
		return new ColumnWeightData(weight, layoutData * ch);
	}

	public String getTitle() {
		return title;
	}

	public String getTooltip() {
		return tooltipTitle;
	}

	public IColumnRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(IColumnRenderer renderer) {
		this.renderer = renderer;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public String getText(Object el) {
		return renderer.getText(el);
	}

	public String getColumnId() {
		return id;
	}
}
