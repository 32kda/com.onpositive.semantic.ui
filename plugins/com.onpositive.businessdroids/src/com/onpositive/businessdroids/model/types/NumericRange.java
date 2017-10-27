package com.onpositive.businessdroids.model.types;

import java.text.NumberFormat;

@SuppressWarnings("rawtypes")
public class NumericRange implements Comparable<Object>,IRangedValue {
	protected Number start;
	protected Number end;

	protected static final double E = 0.0000001;

	public NumericRange(Number start, Number end) {
		super();
		this.start = start;
		this.end = end;
	}

	public int compare(Number value) {
		if (value.doubleValue() < this.start.doubleValue() - NumericRange.E) {
			return 1;
		}
		if (value.doubleValue() > this.end.doubleValue() + NumericRange.E) {
			return -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return '[' + NumberFormat.getInstance().format(this.start) + ".."
				+ NumberFormat.getInstance().format(this.end) + ']';
	}

	public Number getStart() {
		return this.start;
	}

	public void setStart(Number start) {
		this.start = start;
	}

	public Number getEnd() {
		return this.end;
	}

	public void setEnd(Number end) {
		this.end = end;
	}

	@Override
	public int compareTo(Object another) {
		if (another instanceof NumericRange) {
			Number end2 = ((NumericRange) another).getEnd();
			double diffEnd = this.end.doubleValue() - end2.doubleValue();
			if (Math.abs(diffEnd) < NumericRange.E) {
				double diffStart = ((NumericRange) another).getStart()
						.doubleValue() - this.start.doubleValue();
				if (Math.abs(diffStart) < NumericRange.E) {
					return 0;
				}
				return (int) Math.signum(diffStart);
			}
			return (int) Math.signum(diffEnd);
		} else if (another instanceof Number) {
			return this.compare((Number) another);
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.end == null) ? 0 : this.end.hashCode());
		result = prime * result
				+ ((this.start == null) ? 0 : this.start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		NumericRange other = (NumericRange) obj;
		if (this.end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!this.end.equals(other.end)) {
			return false;
		}
		if (this.start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!this.start.equals(other.start)) {
			return false;
		}
		return true;
	}

	@Override
	public Comparable getMin() {
		return (Comparable) start;
	}

	@Override
	public Comparable getMax() {
		return (Comparable) end;
	}
}
