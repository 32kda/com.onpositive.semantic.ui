package com.onpositive.semantic.model.ui.roles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.commons.platform.registry.RegistryMap;

public class GatheringRoleMap<T extends RoleObject> extends RegistryMap<T> {

	HashMap<DecoratorDescriptor, Object> decorators = new HashMap<DecoratorDescriptor, Object>();
	HashMap<DecoratorDescriptor, ArrayList<T>> decoratorsCounted = new HashMap<DecoratorDescriptor, ArrayList<T>>();

	protected static class DecoratorDescriptor {
		String role;
		String className;
		HashSet<String> types;

		public DecoratorDescriptor(String className, String role) {
			super();
			this.className = className;
			this.role = role;
		}

		public DecoratorDescriptor(String targetClass, String role2,
				HashSet<String> targetType) {
			this(targetClass, role2);
			this.types = targetType;
		}

		public DecoratorDescriptor(String name, String role2,
				Set<? extends Object> types2) {
			this(name, role2);			
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((this.className == null) ? 0 : this.className.hashCode());
			result = prime * result
					+ ((this.role == null) ? 0 : this.role.hashCode());
			result = prime * result
					+ ((this.types == null) ? 0 : this.types.hashCode());
			return result;
		}

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
			final DecoratorDescriptor other = (DecoratorDescriptor) obj;
			if (this.className == null) {
				if (other.className != null) {
					return false;
				}
			} else if (!this.className.equals(other.className)) {
				return false;
			}
			if (this.role == null) {
				if (other.role != null) {
					return false;
				}
			} else if (!this.role.equals(other.role)) {
				return false;
			}
			if (this.types == null) {
				if (other.types != null) {
					return false;
				}
			} else if (!this.types.equals(other.types)) {
				return false;
			}
			return true;
		}

	}

	public GatheringRoleMap(String point, Class<T> ownerClass) {
		super(point, ownerClass);
	}

	@SuppressWarnings("unchecked")
	protected void initMap(HashMap<String, T> map) {
		super.initMap(map);
		for (final T d : map.values()) {
			
			DecoratorDescriptor key = new DecoratorDescriptor(d
					.getTargetClass(), d.getRole(), (HashSet<String>) null);
			Object object = this.decorators.get(key);
			if (object != null) {
				if (object instanceof ArrayList<?>) {
					ArrayList<T> m = (ArrayList<T>) object;
					m.add(d);
				} else {
					ArrayList<T> m = new ArrayList<T>();
					m.add((T) object);
					m.add(d);
					this.decorators.put(key, m);
				}
			} else
				this.decorators.put(key, d);
		}
	}

	public ArrayList<T> getElements(Object object, String role, String theme) {
		this.checkLoad();
		if (object == null) {
			return new ArrayList<T>();
		}
		final Class<?> c = object.getClass();
		final String name = c.getName();
		Set<? extends Object> types = null;
//		if (object instanceof ObjectdObject) {
//			final ObjectdObject ta = (ObjectdObject) object;
//			types = ta.getTypes();
//		}
		final DecoratorDescriptor key = new DecoratorDescriptor(name, role,
				types);

		final ArrayList<T> hashSet = this.decoratorsCounted.get(key);
		if (hashSet != null) {
			return hashSet;
		}
		final HashSet<T> result = new HashSet<T>();
		this.fillDecorators(result, object.getClass(), name, role);
		ArrayList<T> rs = null;
		if (types != null) {
			rs = new ArrayList<T>();
			l2: for (final T element : result) {
				final HashSet<String> ttypes = element.getTargetType();
				if (ttypes != null) {
					for (final String s : ttypes) {
						if (!this.check(s, types)) {
							continue l2;
						}
					}
				}
				rs.add(element);
			}
		} else {
			rs = new ArrayList<T>(result);
		}
		Collections.sort(rs);
		this.decoratorsCounted.put(key, rs);
		return rs;
	}

	private boolean check(String s, Set<? extends Object> types) {
		for (final Object t : types) {
			if (t.toString().equals(s)) {
				return true;
			}
		}		
		return false;
	}

	@SuppressWarnings("unchecked")
	private void fillDecorators(HashSet<T> result, Class<?> cls, String name,
			String role) {
		final DecoratorDescriptor key = new DecoratorDescriptor(name, role);

		Object object = this.decorators.get(key);
		if (object instanceof ArrayList<?>) {
			result.addAll((ArrayList<T>) object);
		} else {
			if (object != null) {
				T objectDecoratorDescriptor = (T) object;
				if (objectDecoratorDescriptor != null) {
					result.add(objectDecoratorDescriptor);
				}
			}
		}
		object = this.decorators.get(new DecoratorDescriptor(name, null));
		if (object instanceof ArrayList<?>) {
			result.addAll((ArrayList<T>) object);
		} else {
			if (object != null) {
				T objectDecoratorDescriptor = (T) object;
				if (objectDecoratorDescriptor != null) {
					result.add(objectDecoratorDescriptor);
				}
			}
		}
		final SemanticRole semanticRole = RoleManager.getInstance().get(role);
		if (semanticRole != null) {
			for (final SemanticRole r : semanticRole.getSuperRoles()) {
				this.fillDecorators(result, cls, name, r.getId());
			}
		}
		final Class<?> superclass = cls.getSuperclass();
		if (superclass != null) {
			this.fillDecorators(result, superclass, superclass.getName(), role);
		}
		for (final Class<?> s : cls.getInterfaces()) {
			this.fillDecorators(result, s, s.getName(), role);
		}
	}

}