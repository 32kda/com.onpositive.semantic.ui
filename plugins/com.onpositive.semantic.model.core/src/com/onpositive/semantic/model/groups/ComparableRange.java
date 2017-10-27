package com.onpositive.semantic.model.groups;

import java.text.NumberFormat;
import java.util.Date;

import com.onpositive.semantic.model.api.labels.LabelAccess;

@SuppressWarnings("rawtypes")
public class ComparableRange implements Comparable,IRangedValue {
	protected Comparable<?> start;
	protected Comparable<?> end;

	protected double e = 0.0000001;

	public ComparableRange(Comparable start, Comparable end) {
		super();
		this.start = start;
		this.end = end;
	}

	@SuppressWarnings({ "unchecked" })
	public int compare(Comparable value) {
		if (value.compareTo(this.start) < 0) {
			return -1;
		}
		if (value.compareTo(this.end) > 0) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		Comparable<?> start2 = this.start;
		Comparable<?> end2 = this.end;
		if (start2 instanceof Number) {
			start2 = NumberFormat.getInstance().format(start2);
		}
		if (start2 instanceof Date) {
			start2 = LabelAccess.getLabel(start2);
		}
		if (end2 instanceof Number) {
			end2 = NumberFormat.getInstance().format(end2);
		}
		if (end2 instanceof Date) {
			end2 = LabelAccess.getLabel(start2);;
		}
		return "[" + start2 + ".." + end2 + "]";
	}

	public Comparable getStart() {
		return this.start;
	}

	public void setStart(Comparable start) {
		this.start = start;
	}

	public Comparable getEnd() {
		return this.end;
	}

	public void setEnd(Comparable end) {
		this.end = end;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Object another) {
		if (another instanceof ComparableRange) {
			ComparableRange r = (ComparableRange) another;
			if (r.end != null && this.end != null) {
				return ((Comparable) this.end).compareTo(r.end);
			}
			if (r.start != null && this.start != null) {
				return ((Comparable) this.start).compareTo(r.start);
			}
			if (this.start != null) {
				if (r.end != null) {
					return ((Comparable) this.start).compareTo(r.end);
				}
			}
			if (this.end != null) {
				if (r.start != null) {
					return ((Comparable) this.end).compareTo(r.start);
				}
			}
		}
		if (another instanceof Comparable){
			Comparable c=(Comparable) another;
			if (this.start!=null){
				return ((Comparable)this.start).compareTo(c);
			}
		}
		
		return 0;
	}

	@Override
	public Comparable getMin() {
		return start;
	}

	@Override
	public Comparable getMax() {
		return end;
	}
}
