package com.onpositive.semantic.model.api.query;

import java.io.Serializable;

import com.onpositive.semantic.model.api.labels.LabelProperty;

public class QueryFilter implements Serializable{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((filterConstraint == null) ? 0 : filterConstraint.hashCode());
		result = prime * result
				+ ((filterKind == null) ? 0 : filterKind.hashCode());
		result = prime * result + ((propId == null) ? 0 : propId.hashCode());
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
		QueryFilter other = (QueryFilter) obj;
		if (filterConstraint == null) {
			if (other.filterConstraint != null)
				return false;
		} else if (!filterConstraint.equals(other.filterConstraint))
			return false;
		if (filterKind == null) {
			if (other.filterKind != null)
				return false;
		} else if (!filterKind.equals(other.filterKind))
			return false;
		if (propId == null) {
			if (other.propId != null)
				return false;
		} else if (!propId.equals(other.propId))
			return false;
		return true;
	}

	public static final String LABEL_PROPERTY=LabelProperty.INSTANCE.getId();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FILTER_EQUALS = "==";
	public static final String FILTER_NOT_EQUALS = "!=";
	public static final String FILTER_ONE_OF = " one_of";
	public static final String FILTER_ALL_OF = " all_of";
	public static final String FILTER_STARTS_WITH = "starts_with";
	public static final String FILTER_CONTAINS = "containsValue";

	public static final String FILTER_GT = ">";
	public static final String FILTER_LT = "<";
	public static final String FILTER_GE = ">=";
	public static final String FILTER_LE = "<=";

	protected String propId;

	/**
	 * constraint value or nested query
	 */
	protected Object filterConstraint;

	public String getPropId() {
		return propId;
	}

	public Object getFilterConstraint() {
		return filterConstraint;
	}

	public String getFilterKind() {
		return filterKind;
	}

	public QueryFilter(String propId, Object filterConstraint, String filterKind) {
		super();
		this.propId = propId;
		this.filterConstraint = filterConstraint;
		this.filterKind = filterKind;
	}

	protected String filterKind;

	
	
}
