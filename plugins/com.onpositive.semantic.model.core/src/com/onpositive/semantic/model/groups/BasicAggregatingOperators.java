package com.onpositive.semantic.model.groups;

import java.util.Collection;

import com.onpositive.semantic.model.api.property.FunctionOperator;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.ValueUtils;

public class BasicAggregatingOperators extends FunctionOperator {

	public static final int SUM_MODE = 0;
	public static final int MIN_MODE = 1;
	public static final int MAX_MODE = 2;
	public static final int AVE_MODE = 3;
	public static final int RANGE_MODE = 4;
	public static final int IDENTITY_MODE = 5;

	protected final int mode;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mode;
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
		BasicAggregatingOperators other = (BasicAggregatingOperators) obj;
		if (mode != other.mode)
			return false;
		return true;
	}

	protected BasicAggregatingOperators(int mode) {
		super();
		this.mode = mode;
	}

	public static final BasicAggregatingOperators MIN = new BasicAggregatingOperators(
			MIN_MODE);
	public static final BasicAggregatingOperators MAX = new BasicAggregatingOperators(
			MAX_MODE);
	public static final BasicAggregatingOperators AVE = new BasicAggregatingOperators(
			AVE_MODE);
	public static final BasicAggregatingOperators SUM = new BasicAggregatingOperators(
			MAX_MODE);
	public static final BasicAggregatingOperators RANGE = new BasicAggregatingOperators(
			RANGE_MODE);

	public static final BasicAggregatingOperators IDENT = new BasicAggregatingOperators(
			IDENTITY_MODE);

	@SuppressWarnings({})
	@Override
	protected Object calc(IFunction f, Object o2) {
		Collection<Object> collection = ValueUtils.toCollection(o2);
		int mode = this.mode;
		if (mode == IDENTITY_MODE) {
			return calcIdentity(collection);
		}

		try {
			return calcNumber(f, collection, mode);
		} catch (ClassCastException e) {
		}

		if (mode == RANGE_MODE) {
			return groupRange(f, collection);
		}
		if (mode != MIN_MODE && mode != MAX_MODE) {
			throw new IllegalStateException();
		}
		return minMaxRange(f, collection, mode);

	}

	protected Object calcNumber(IFunction f, Collection<Object> collection,
			int mode) {
		double vl = 0;
		double vl1 = 0;
		if (mode == MIN_MODE) {
			vl = Double.MAX_VALUE;
		}
		if (mode == MAX_MODE) {
			vl = Double.MIN_VALUE;
		}
		if (mode == RANGE_MODE) {
			vl = Double.MIN_VALUE;
			vl1 = Double.MAX_VALUE;
		}
		for (Object o : collection) {
			Object value = f.getValue(o);
			double doubleValue = getDouble(value);
			switch (mode) {
			case SUM_MODE:
			case AVE_MODE:
				vl += doubleValue;
				break;
			case MIN_MODE:
				vl = Math.min(vl, doubleValue);
				break;
			case MAX_MODE:
				vl = Math.max(vl, doubleValue);
				break;

			case RANGE_MODE:
				vl = Math.max(vl, doubleValue);
				vl1 = Math.min(vl1, doubleValue);
				break;

			default:
				break;
			}
		}
		if (mode == AVE_MODE) {
			vl = vl / collection.size();
		}
		if (mode == RANGE_MODE) {
			return new NumericRange(vl, vl1);
		}
		return vl;
	}

	protected double getDouble(Object value) {
		Number m = (Number) value;
		double doubleValue = m.doubleValue();
		return doubleValue;
	}

	protected Object calcIdentity(Collection<Object> collection) {
		Object cVal = null;
		for (Object vlq : collection) {

			if (vlq == null) {
				continue;
			}
			if (cVal == null) {
				cVal = vlq;
			} else {
				if (!cVal.equals(vlq)) {
					return null;
				}
			}
		}
		return cVal;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object minMaxRange(IFunction f, Collection<Object> collection,
			int mode) {
		Object res = null;
		for (Object i : collection) {
			i = f.getValue(i);
			if (i == null) {
				continue;
			}
			if (res == null) {
				res = (Comparable<?>) i;
			}
			if (i != null) {
				if (((mode == MIN_MODE) && (((Comparable) i).compareTo(res) < 0))
						|| ((mode == MAX_MODE) && (((Comparable) i)
								.compareTo(res) > 0))) {
					res = (Comparable<?>) i;
				}
			}
		}
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Object groupRange(IFunction f, Collection<Object> collection) {
		Comparable res = null;
		Comparable res1 = null;
		for (Object i : collection) {
			i = f.getValue(i);
			if (i == null) {
				continue;
			}
			if (res == null) {
				res = (Comparable<?>) i;
				res1 = (Comparable<?>) i;
			}
			if (i != null) {
				if (((Comparable) i).compareTo(res) < 0) {
					res = (Comparable<?>) i;
				}
				if (((((Comparable) i).compareTo(res1) > 0))) {
					res1 = (Comparable<?>) i;
				}
			}
		}
		return new ComparableRange(res, res1);
	}
	
	

}
