package com.onpositive.semantic.model.expressions.operatorimplementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class BinaryOperatorEvaluator {

	static HashMap<Integer, HashMap<ClassPair, BinaryOperator<?, ?>>> operatorMap = new HashMap<Integer, HashMap<ClassPair, BinaryOperator<?, ?>>>();
	static ClassUpcaster classUpcaster = new ClassUpcaster();
	// static HashMap< ClassPair, ClassPair > conversionMap ;

	static {
		for (int i = 0; i < BinaryOperator.operatorsCount; i++)
			operatorMap.put(i, new HashMap<ClassPair, BinaryOperator<?, ?>>());

		Object[] mapParams = BinaryOperator.getCorrespondanceArray();
		for (int i = 0; (i < mapParams.length) && (mapParams[i] != null);) {
			int kind = (Integer) mapParams[i++];
			ClassPair pair = (ClassPair) mapParams[i++];
			BinaryOperator<?, ?> operator = (BinaryOperator<?, ?>) mapParams[i++];

			operatorMap.get(kind).put(pair, operator);
		}
	}
	
	

	public static final class ClassPair {

		Class<?> class1;
		Class<?> class2;

		public ClassPair(Class<?> class1, Class<?> class2) {
			this.class1 = class1;
			this.class2 = class2;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((class1 == null) ? 0 : class1.hashCode());
			result = prime * result
					+ ((class2 == null) ? 0 : class2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			ClassPair other = (ClassPair) obj;
			if (class1 == null) {
				if (other.class1 != null)
					return false;
			} else if (!class1.equals(other.class1))
				return false;
			if (class2 == null) {
				if (other.class2 != null)
					return false;
			} else if (!class2.equals(other.class2))
				return false;
			return true;
		}

	}

	static final protected class ClassUpcaster {

		static {
			primaryUpcastMap = new HashMap<Class<?>, Class<?>>();
			integerMap = new HashMap<Class<?>, Integer>();
			fillMaps();
		}

		static HashMap<Class<?>, Class<?>> primaryUpcastMap;
		static HashMap<Class<?>, Integer> integerMap;
		static final int defaultValue = 1000000;

		protected ClassPair upcast(ClassPair pair) {

			Class<?> class1 = primaryUpcastMap.get(pair.class1);
			Class<?> class2 = primaryUpcastMap.get(pair.class2);

			class1 = (class1 == null) ? pair.class1 : class1;
			class2 = (class2 == null) ? pair.class2 : class2;

			Integer code1 = integerMap.get(class1);
			Integer code2 = integerMap.get(class2);

			code1 = code1 == null ? defaultValue : code1;
			code2 = code2 == null ? defaultValue : code2;

			if ((code1 != 0) && (code2 != 0)) {
				if (code1 > code2)
					return (code1 == defaultValue) ? new ClassPair(
							Object.class, Object.class) : new ClassPair(class1,
							class1);
				else
					return (code2 == defaultValue) ? new ClassPair(
							Object.class, Object.class) : new ClassPair(class2,
							class2);
			}
			return new ClassPair(class1, class2);

		}

		protected static void fillMaps() {

			primaryUpcastMap.put(Byte.class, Integer.class);
			primaryUpcastMap.put(Short.class, Integer.class);
			primaryUpcastMap.put(Float.class, Double.class);
			primaryUpcastMap.put(Character.class, String.class);
			primaryUpcastMap.put(ArrayList.class, Collection.class);

			integerMap.put(Integer.class, 100);
			integerMap.put(Long.class, 200);
			integerMap.put(Double.class, 300);
			integerMap.put(Boolean.class, 400);
			integerMap.put(String.class, defaultValue + 100);
			integerMap.put(Collection.class, 0);
			integerMap.put(Class.class, 0);
		}
	}

	static ObjectOperator object = new ObjectOperator();

	public Object getOperatorValue(int kind, Object arg1, Object arg2) {
		boolean arg1IsNull = arg1 == null;
		boolean arg2IsNull = arg2 == null;

		boolean bothAreNull = arg1IsNull && arg2IsNull;

		if (bothAreNull) {
			if (kind == BinaryOperator.NEQ) {
				return false;
			}
			if (kind == BinaryOperator.EQ) {
				return true;
			}
			return null;
		}

		Class<?> class1 = arg1IsNull ? arg2.getClass() : arg1.getClass();
		Class<?> class2 = arg2IsNull ? arg1.getClass() : arg2.getClass();

		ClassPair upcastedPair = classUpcaster.upcast(new ClassPair(class1,
				class2));

		BinaryOperator<?, ?> operator = getOperator(kind, upcastedPair);
		if (operator == null) {
			return object.doGetValue(kind, arg1, arg2);
		}
		return operator.getValue(arg1, arg2);
	}
	static FilterByOperator fb=new FilterByOperator();
	static TransformByOperator tb=new TransformByOperator();
	static OrderByOperator ob=new OrderByOperator();
	static CommaOperator cb=new CommaOperator();
	

	public static BinaryOperator<?, ?> getOperator(int kind, ClassPair CPair) {
		Class<?> class1 = CPair.class1;
		Class<?> class2 = CPair.class2;
		if (kind==BinaryOperator.FILTER_BY){
			return fb;
		}
		if (kind==BinaryOperator.COMMA){
			return cb;
		}
		if (kind==BinaryOperator.ORDER_BY){
			return ob;
		}
		if (kind==BinaryOperator.TRANSFORM_BY){
			return tb;
		}
		HashMap<ClassPair, BinaryOperator<?, ?>> mapOfThePresentKind = operatorMap
				.get(kind);
		BinaryOperator<?, ?> result = mapOfThePresentKind.get(CPair);

		if (result != null)
			return result;

		else {

			ArrayList<ClassPair> CPList = new ArrayList<ClassPair>();
			CPList.add(CPair);

			if (class1 == class2) {
				ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
				extractClasses(class1, classList);

				int classListSize = classList.size();
				for (int i = 0; i < classListSize; i++) {

					CPair = new ClassPair(classList.get(i), classList.get(i));
					result = mapOfThePresentKind.get(CPair);

					if (result == null)
						CPList.add(CPair);

					else {
						int CPListSize = CPList.size();
						for (int k = 0; k < CPListSize; k++)
							mapOfThePresentKind.put(CPList.get(k), result);

						return result;
					}
				}
			} else {
				ArrayList<Class<?>> class1List = new ArrayList<Class<?>>();
				ArrayList<Class<?>> class2List = new ArrayList<Class<?>>();

				extractClasses(class1, class1List);
				extractClasses(class2, class2List);

				int class1ListSize = class1List.size();
				int class2ListSize = class2List.size();
				for (int i = 0; i < class1ListSize; i++)
					for (int j = 0; j < class2ListSize; j++) {

						CPair = new ClassPair(class1List.get(i),
								class2List.get(j));
						result = mapOfThePresentKind.get(CPair);

						if (result == null)
							CPList.add(CPair);

						else {
							int CPListSize = CPList.size();
							for (int k = 0; k < CPListSize; k++)
								mapOfThePresentKind.put(CPList.get(k), result);

							return result;
						}
					}
			}
		}
		return null;
	}

	private static void extractClasses(Class<? extends Object> clazz,
			ArrayList<Class<?>> classList) {
		
		if (!classList.contains(Object.class))
			classList.add(Object.class);

		if (clazz == null)
			return;

		classList.add(clazz);

		Class<?> superClass = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();

		extractClasses(superClass, classList);

		for (int i = 0; i < interfaces.length; i++)
			extractClasses(superClass, classList);
	}
}
