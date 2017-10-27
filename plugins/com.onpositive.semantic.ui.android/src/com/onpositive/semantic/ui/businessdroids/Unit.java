package com.onpositive.semantic.ui.businessdroids;

import java.io.Serializable;

public class Unit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());		
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Unit other = (Unit) obj;
		if (kind != other.kind)
			return false;
		if (primaryUnit == null) {
			if (other.primaryUnit != null)
				return false;
		} 
		if (primaryUnit!=this){
			if (!primaryUnit.equals(other.primaryUnit))
			return false;
		}
		if (Double.doubleToLongBits(relationToPrimary) != Double
				.doubleToLongBits(other.relationToPrimary))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	protected final String shortName;
	protected final UnitKind kind;
	protected final double relationToPrimary;
	protected final Unit primaryUnit ;
	
	public Unit(String shortName, UnitKind kind, double relationToPrimary)
	{
		super();
		this.shortName = shortName;
		this.kind = kind;
		this.relationToPrimary = relationToPrimary;
		this.primaryUnit = this ;
	}
	
	public Unit(String shortName, UnitKind kind, double relationToPrimary, Unit primaryUnit )
	{
		super();
		this.shortName = shortName;
		this.kind = kind;
		this.relationToPrimary = relationToPrimary;
		this.primaryUnit = primaryUnit ;
	}

	public String getShortName() {
		return shortName;
	}

	public UnitKind getKind() {
		return kind;
	}

	public double getRelationToPrimary() {
		return relationToPrimary;
	}

	public Unit getPrimaryUnit() {
		return primaryUnit;
	}
	
	@Override
	public String toString()
	{
		StringBuilder bld = new StringBuilder() ;
		bld.append( getShortName() ) ;
		bld.append("(") ;
		bld.append( getKind() ) ;
		bld.append(")") ;
		return bld.toString() ;
	}
}

