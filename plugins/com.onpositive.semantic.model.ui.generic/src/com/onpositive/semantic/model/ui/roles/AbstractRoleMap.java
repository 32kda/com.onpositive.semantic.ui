package com.onpositive.semantic.model.ui.roles;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import com.onpositive.commons.platform.registry.RegistryMap;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;

@SuppressWarnings("rawtypes")
public abstract class AbstractRoleMap<T extends RoleObject> extends
		RegistryMap<T> {

	public static class RoleKey {
		String className;
		String roleName;
		String themeName;
		HashSet<String> typeName;
	
		public RoleKey(String className, String roleName, String themeName,
				String type) {
			super();
			this.className = className;
			this.roleName = roleName;
			this.themeName = themeName;
			if (type != null) {
				this.typeName = new HashSet<String>();
				this.typeName.add(type);
			}
		}
	
		public RoleKey(String className2, String roleName2, String themeName2,
				HashSet<String> typeName2) {
			this.className = className2;
			this.roleName = roleName2;
			this.themeName = themeName2;
			this.typeName = typeName2;
		}
	
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((this.className == null) ? 0 : this.className.hashCode());
			result = prime * result
					+ ((this.roleName == null) ? 0 : this.roleName.hashCode());
			result = prime
					* result
					+ ((this.themeName == null) ? 0 : this.themeName.hashCode());
			result = prime
					* result
					+ ((this.typeName == null || this.typeName.isEmpty()) ? 0
							: this.typeName.hashCode());
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
			final RoleKey other = (RoleKey) obj;
			if (this.className == null) {
				if (other.className != null) {
					return false;
				}
			} else if (!this.className.equals(other.className)) {
				return false;
			}
			if (this.roleName == null) {
				if (other.roleName != null) {
					return false;
				}
			} else if (!this.roleName.equals(other.roleName)) {
				return false;
			}
			if (this.themeName == null) {
				if (other.themeName != null) {
					return false;
				}
			} else if (!this.themeName.equals(other.themeName)) {
				return false;
			}
			if (this.typeName == null) {
				if (other.typeName != null && !other.typeName.isEmpty()) {
					return false;
				}
			} else if (!this.typeName.equals(other.typeName)
					&& (!(other.typeName == null && this.typeName.isEmpty()))) {
				return false;
			}
			return true;
		}
	
		public String toString() {
			return this.className
					+ ":" + this.roleName + ":" + this.themeName + ":" + this.typeName; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private final IdentityHashMap<Class<?>, String> names = new IdentityHashMap<Class<?>, String>();

	private final HashMap<Set<? extends Object>, HashSet<String>> tpes = new HashMap<Set<? extends Object>, HashSet<String>>();

	protected HashMap<RoleKey, T> objects = new HashMap<RoleKey, T>();

	@SuppressWarnings("unchecked")
	public AbstractRoleMap(String point, Class ownerClass) {
		super(point, ownerClass);
	}

	protected RoleKey getRoleKey(IHasMeta meta) {
		String role = DefaultMetaKeys.getStringValue(meta,
				DefaultMetaKeys.ROLE_KEY);
		String theme = DefaultMetaKeys.getStringValue(meta,
				DefaultMetaKeys.THEME_KEY);
		String subjectClass = DefaultMetaKeys.getStringValue(meta,
				DefaultMetaKeys.SUBJECT_CLASS_KEY);
		return new RoleKey(subjectClass, role, theme, (String)null);

	}

	protected void initMap(HashMap<String, T> map) {
		super.initMap(map);
		for (final T o : map.values()) {
			final RoleKey key = o.toKey();
			this.objects.put(key, o);
		}
	}

	protected final T getObject(Class<?> class1, RoleKey ks,
			Set<? extends Object> types) {
		T imageObject;
		// this.objects.clear();
		if (this.objects.containsKey(ks)) {

			imageObject = this.objects.get(ks);
			return imageObject;
		}

		if (ks.themeName != null) {

			final T imageKey = this.getObject(class1, new RoleKey(ks.className,
					ks.roleName, null, ks.typeName), types);
			if (imageKey != null) {
				this.objects.put(ks, imageKey);
				return imageKey;
			}
		}
		if (ks.roleName != null) {
			final SemanticRole semanticRole = RoleManager.getInstance().get(
					ks.roleName);
			if (semanticRole != null) {
				final Collection<SemanticRole> superRoles = semanticRole
						.getSuperRoles();
				for (final SemanticRole r : superRoles) {
					final RoleKey sm = new RoleKey(ks.className, r.getId(),
							ks.themeName, ks.typeName);
					final T imageKey = this.getObject(class1, sm, types);
					if (imageKey != null) {
						this.objects.put(ks, imageKey);
						return imageKey;
					}
				}
			}
			if (ks.roleName.length() > 0) {
				final Class<?> class2 = getSuper(class1);
				if (class2 != null) {
					imageObject = this.getObject(class2,
							new RoleKey(this.getName(class2), ks.roleName,
									ks.themeName, ks.typeName), types);
					if (imageObject != null) {
						this.objects.put(ks, imageObject);
						return imageObject;
					}
					imageObject = this.getObject(class2,
							new RoleKey(this.getName(class2), ks.roleName,
									null, new HashSet<String>()), types);
					if (imageObject != null) {
						this.objects.put(ks, imageObject);
						return imageObject;
					}
				}
			}
			final T imageKey = this.getObject(class1, new RoleKey(ks.className,
					null, null, ks.typeName), types);
			if (imageKey != null) {
				this.objects.put(ks, imageKey);
				return imageKey;
			}
		}
		if (ks.typeName != null) {
			if (types.size() > 1) {
				for (final Object t : types) {
					final RoleKey roleKey = new RoleKey(ks.className,
							ks.roleName, ks.themeName, t.toString());
					final T imageKey = this.getObject(class1, roleKey,
							Collections.singleton(t));
					if (imageKey != null) {
						this.objects.put(ks, imageKey);
						return imageKey;
					}
				}
			}
//			for (final Object t : types) {
//				final Set<? extends Object> superClasses = t.getSuperClasses();
//				final HashSet<String> sn = new HashSet<String>();
//				for (final Object s : superClasses) {
//					sn.add(s.toString());
//				}
//				final RoleKey roleKey = new RoleKey(ks.className, ks.roleName,
//						ks.themeName, sn);
//				final T imageKey = this
//						.getObject(class1, roleKey, superClasses);
//				if (imageKey != null) {
//					this.objects.put(ks, imageKey);
//					return imageKey;
//				}
//			}
		}
		final Class<?> class2 = getSuper(class1);
		if (class2 != null) {
			imageObject = this.getObject(class2,
					new RoleKey(this.getName(class2), ks.roleName,
							ks.themeName, ks.typeName), types);
			if (imageObject != null) {
				this.objects.put(ks, imageObject);
				return imageObject;
			}
			imageObject = this.getObject(class2,
					new RoleKey(this.getName(class2), ks.roleName, null,
							new HashSet<String>()), types);
			if (imageObject != null) {
				this.objects.put(ks, imageObject);
				return imageObject;
			}
		}
		if (class1 != null) {
			for (final Class<?> cl : class1.getInterfaces()) {
				imageObject = this.getObject(cl, new RoleKey(this.getName(cl),
						ks.roleName, ks.themeName, ks.typeName), types);
				if (imageObject != null) {
					this.objects.put(ks, imageObject);
					return imageObject;
				}
			}
		}
		this.objects.put(ks, null);
		return null;
	}

	private Class<?> getSuper(Class<?> class1) {
		Class<?> superclass = class1.getSuperclass();
		if (superclass == Object.class) {
			return null;
		}
		return superclass;
	}

	protected final HashSet<String> getTypes(Set<? extends Object> types) {
		if (types == null) {
			return null;
		}
		final HashSet<String> hashSet = this.tpes.get(types);
		if (hashSet != null) {
			return hashSet;
		}
		final HashSet<String> s = new HashSet<String>();
		for (final Object t : types) {
			s.add(t.toString());
		}
		this.tpes.put(types, s);
		return s;
	}

	protected final String getName(Class<?> cl) {
		String string = this.names.get(cl);
		if (string == null) {
			string = cl.getName();
			this.names.put(cl, string);
		}
		return string;
	}
}