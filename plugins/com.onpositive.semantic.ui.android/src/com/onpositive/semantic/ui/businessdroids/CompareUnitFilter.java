package com.onpositive.semantic.ui.businessdroids;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.model.filters.ComparableFilter;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;

public class CompareUnitFilter extends ComparableFilter {

	protected String primaryUnit;

	public String getPrimaryUnit() {
		return primaryUnit;
	}

	public void setPrimaryUnit(String primaryUnit) {
		this.primaryUnit = primaryUnit;
	}

	protected String unit;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		if (primaryUnit == null) {
			primaryUnit = unit;
		}
		Comparable<?> min = getMin();
		Comparable<?> max = getMax();
		this.unit = unit;
		setMax(max);
		setMin(min);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public CompareUnitFilter(TableModel tableModel, IColumn field,
			Comparable min, Comparable max) {
		super(tableModel, field, min, max);
	}

	@SuppressWarnings("rawtypes")
	
	public void setMax(Comparable max) {
		Double dl = (Double) max;
		if (dl != null) {
			dl = Units.convertToPrimary(dl, unit);
		}
		super.setMax(dl);
	}

	@SuppressWarnings("rawtypes")
	
	public void setMin(Comparable min) {
		Double dl = (Double) min;
		if (dl != null) {
			dl = Units.convertToPrimary(dl, unit);
		}
		super.setMin(dl);
	}

	@SuppressWarnings("rawtypes")
	
	public Comparable getMax() {
		Double min2 = (Double) super.getMax();
		if (min2 != null) {
			return Units.convertFromPrimary(min2, unit);
		}
		return min2;
	}

	@SuppressWarnings("rawtypes")	
	public Comparable getMin() {
		Double min2 = (Double) super.getMin();
		if (min2 != null) {
			return Units.convertFromPrimary(min2, unit);
		}
		return min2;
	}
	@SuppressWarnings("rawtypes")
	
	public Comparable getMaxActual() {
		Double min2 = (Double) super.getMax();
		
		return min2;
	}

	@SuppressWarnings("rawtypes")	
	public Comparable getMinActual() {
		Double min2 = (Double) super.getMin();
		
		return min2;
	}
	
	public void loadSpecificData(IStore store) {
		unit=store.getString("unit", unit);
		primaryUnit=store.getString("dunit", primaryUnit);
		super.loadSpecificData(store);
	}

	
	public void saveSpecificData(IStore store) {
		super.saveSpecificData(store);
		store.putString("unit", unit);
		store.putString("dunit", primaryUnit);	
	}
}
