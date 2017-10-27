package com.onpositive.semantic.model.api.method.java;

import java.lang.reflect.*;
import java.util.*;

public class ReflectionUtil {
	private static Map<Class<?>, Class<?>> primitiveMap = new HashMap<Class<?>, Class<?>>();
	static {
		primitiveMap.put(boolean.class, Boolean.class);
		primitiveMap.put(byte.class, Byte.class);
		primitiveMap.put(char.class, Character.class);
		primitiveMap.put(short.class, Short.class);
		primitiveMap.put(int.class, Integer.class);
		primitiveMap.put(long.class, Long.class);
		primitiveMap.put(float.class, Float.class);
		primitiveMap.put(double.class, Double.class);
	}

	public static Method getCompatibleMethod(Class<?> c, String methodName,
			Class<?>... paramTypes) {
		Method[] methods = addAll(c.getMethods(),c.getDeclaredMethods());
		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];

			if (!m.getName().equals(methodName)) {
				continue;
			}

			Class<?>[] actualTypes = m.getParameterTypes();
			if (actualTypes.length != paramTypes.length) {
				continue;
			}

			boolean found = true;
			for (int j = 0; j < actualTypes.length; j++) {
				if (!actualTypes[j].isAssignableFrom(paramTypes[j])) {
					if (actualTypes[j].isPrimitive()) {
						found = primitiveMap.get(actualTypes[j]).equals(
								paramTypes[j]);
					} else if (paramTypes[j].isPrimitive()) {
						found = primitiveMap.get(paramTypes[j]).equals(
								actualTypes[j]);
					}
				}

				if (!found) {
					break;
				}
			}

			if (found) {
				return m;
			}
		}

		return null;
	}
	
	   /**
     * <p>Adds all the elements of the given arrays into a new array.</p>
     * <p>The new array contains all of the element of <code>array1</code> followed
     * by all of the elements <code>array2</code>. When an array is returned, it is always
     * a new array.</p>
     *
     * <pre>
     * ArrayUtils.addAll(null, null)     = null
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * ArrayUtils.addAll([null], [null]) = [null, null]
     * ArrayUtils.addAll(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
     * </pre>
     *
     * @param array1  the first array whose elements are added to the new array, may be <code>null</code>
     * @param array2  the second array whose elements are added to the new array, may be <code>null</code>
     * @return The new array, <code>null</code> if <code>null</code> array inputs. 
     *      The type of the new array is the type of the first array.
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
	public static <T> T[] addAll(T[] array1, T[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        T[] joinedArray = (T[]) Array.newInstance(array1.getClass().getComponentType(),
                                                            array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    // Clone
    //-----------------------------------------------------------------------
    /**
     * <p>Shallow clones an array returning a typecast result and handling
     * <code>null</code>.</p>
     *
     * <p>The objects in the array are not cloned, thus there is no special
     * handling for multi-dimensional arrays.</p>
     * 
     * <p>This method returns <code>null</code> for a <code>null</code> input array.</p>
     * @param <T>
     * 
     * @param array  the array to shallow clone, may be <code>null</code>
     * @return the cloned array, <code>null</code> if <code>null</code> input
     */
    public static <T> T[] clone(T[] array) {
        if (array == null) {
            return null;
        }
        return (T[]) array.clone();
    }
}