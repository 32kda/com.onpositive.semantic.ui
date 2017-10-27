package com.onpositive.semantic.model.groups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.onpositive.semantic.model.api.labels.IHasPresentationObject;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.FunctionOperator;
import com.onpositive.semantic.model.api.property.IBinaryOperator;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.realm.IRealm;

public class BasicGroupingOperators extends FunctionOperator {

	public static class Group implements IHasPresentationObject,Comparable<Object> {

		protected Object key;
		public Object getKey() {
			return key;
		}

		public void setKey(Object key) {
			this.key = key;
		}

		protected final IFunction gFunc;
		protected final ArrayList<Object> vls = new ArrayList<Object>();
		protected HashMap<IFunction, Object> values = new HashMap<IFunction, Object>();

		public Group(Object vl, IFunction gFunc) {
			this.key = vl;
			this.gFunc = gFunc;
		}

		public Group(Object object, IFunction q, Object[] array) {
			this.key = object;
			this.gFunc = q;
			this.vls.addAll(Arrays.asList(array));
		}
		
		public Collection<Object> getValues(){
			return vls;			
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((gFunc == null) ? 0 : gFunc.hashCode());
			result = prime * result + ((key == null) ? 0 : key.hashCode());
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
			Group other = (Group) obj;
			if (gFunc == null) {
				if (other.gFunc != null)
					return false;
			} else if (!gFunc.equals(other.gFunc))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}

		public Object getValue(IFunction m) {
			if (values.containsKey(m)) {
				return values.get(m);
			}
			IBinaryOperator value = DefaultMetaKeys.getValue(m,
					DefaultMetaKeys.AGGREGATOR_FUNCTION_KEY,
					IBinaryOperator.class);
			if (value != null) {
				Object value2 = value.getValue(m, values);
				values.put(m, value2);
				return value2;
			}
			values.put(m, null);
			return m;
		}

		@Override
		public Object getElement() {
			return key;
		}

		public int getChildrenCount() {
			return vls.size();
		}

		public Object getChild(int position) {
			if (position < 0 || position > vls.size())
				throw new IndexOutOfBoundsException(
						"Group child index out of bounds. Should be 0 <= position < "
								+ getChildrenCount() + ", got " + position);
			return vls.get(position);
		}

		@Override
		public String toString() {
			return key.toString();
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Object arg0) {
			if (arg0 instanceof IHasPresentationObject){
				IHasPresentationObject m=(IHasPresentationObject) arg0;
				arg0=m.getElement();
			}
			try{
				if (key==null){
					return ((Comparable)arg0).compareTo(key);
				}
			return ((Comparable)this.key).compareTo(arg0);
			}catch (Exception e) {
				return LabelAccess.getLabel(this.key).compareTo(LabelAccess.getLabel(arg0));
			}
		}

	}

	static BasicGroupingOperators INSTANCE = new BasicGroupingOperators();

	@Override
	public Object calc(IFunction f, Object o2) {
		Collection<Object> collection = ValueUtils.toCollection(o2);
		LinkedHashMap<Object, Group> gs = new LinkedHashMap<Object, BasicGroupingOperators.Group>();
		for (Object o : collection) {	
			Object vl = f.getValue(o);
			Collection<Object> collection2 = ValueUtils.toCollection(vl);
			for (Object q : collection2) {
				Group v = gs.get(q);
				if (q.toString().length()==0){
					System.out.println("A");
				}
				if (v == null) {
					v = new Group(q, f);
					gs.put(q, v);
				}
				v.vls.add(o);
			}
		}
		return new ArrayList<Group>(gs.values());
	}

	@SuppressWarnings("unchecked")
	public static Collection<Group> group(IRealm<Object> r) {
		IBinaryOperator value = DefaultMetaKeys.getValue(r,
				DefaultMetaKeys.GROUPING_FUNCTION_KEY, IBinaryOperator.class);
		if (value != null) {
			return (Collection<Group>) value.getValue(value, r);
		}
		return Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	public static Collection<Group> group(Collection<?> objects,
			IBinaryOperator groupingOperator, IFunction function) {
		if (groupingOperator != null) {
			return (Collection<Group>) groupingOperator.getValue(function,
					objects);
		}
		return Collections.emptySet();
	}

	public static BasicGroupingOperators getInstance() {
		return INSTANCE;
	}
}
