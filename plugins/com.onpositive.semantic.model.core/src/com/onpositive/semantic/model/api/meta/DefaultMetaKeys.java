package com.onpositive.semantic.model.api.meta;

import com.onpositive.semantic.model.api.property.IProperty;

public class DefaultMetaKeys {

	// @Unique
	public static final String UNIQUE_KEY = "unique";

	// @Id
	public static final String ID_KEY = "id";

	public static final String ON_CREATE_KEY = "onCreate";
	public static final String ON_MODIFY_KEY = "onModify";
	public static final String ON_DELETE_KEY = "onDelete";

	public static final String PROP_ID_KEY = "prop_id";
	public static final String CHILD_KEY = "child";
	public static final String PARENT_KEY = "parent";
	public static final String SEARCHABLE_KEY = "searchable";
	public static final String COMMUTATIVE_WITH = "inverseOf";

	public static final String INDEXABLE_KEY = "indexable";
	public static final String IMAGE_IS_ENOUGH_IN_CELLS = "image_is_enough";
	
	public static final String ASSOTIATION_KEY = "assotiation";
	public static final String READ_ONLY_KEY = "readonly";
	public static final String MULTI_VALUE_KEY = "multivalue";
	public static final String FIXED_BOUND_KEY = "fixedBound";
	public static final String STATIC_KEY = "static";
	public static final String IGNORE_PREPROCESSORS = "ignore_preprocessors";
	public static final String CATEGORY_KEY = "category";
	public static final String COMPUTED_KEY = "computed";
	public static final String ENABLED = "enabled";
	public static final String CAPTION_KEY = "caption";
	public static final String DISPLAY_KEY = "display";
	public static final String DESCRIPTION_KEY = "description";
	public static final String REQUIRED_KEY = "required";
	public static final String ORDERED_KEY = "ordered";
	public static final String REALM__KEY = "realm";
	public static final String USE_ADD_REMOVE__KEY = "use_add_remove";
	public static final String RANGE_MIN__KEY = "range_min";
	public static final String RANGE_MAX__KEY = "range_max";
	public static final String RANGE_DIGITS__KEY = "range_digits";
	public static final String RANGE_INCREMENT__KEY = "range_increment";
	public static final String RANGE_PAGE_INCREMENT__KEY = "range_increment";
	public static final String ROLE_KEY = "role";
	public static final String DESCRIPTION_SUFFIX = ".description";
	public static final String SEPARATOR_CHARACTERS_KEY = "separators";
	public static final String GROUP_KEY = "group";
	public static final String FORMULA_KEY = "formula";
	public static final String GROUPABLE_KEY = "groupable";
	public static final String CONTENT_ASSIST_CONFIG_KEY = "content_assit_config";
	public static final String SUBJECT_CLASS_KEY = "subject_class";
	public static final String DEFAULT_VALUE = "default_value";
	public static final String USER_RUNNABLE = "user_runnable";
	public static final String ROOT_UNDO_CONTEXT = "root_undo_ctx";
	public static final String SHORT_LYFECYCLE = "short_life_cycle";
	public static final String THEME_KEY = "theme";

	public static final String IMAGE_KEY = "image_key";

	public static final String PUBLIC_KEY = "public_key";

	public static final String CONTENT_TYPE_KEY = "contentType";

	public static final Object CONTENT_TYPE_VALUE_DATE = "contentType.date";

	public static final String REGEXP = "regexg";

	public static final String USER = "user";
	
	public static final String PASSWORD_USER = "password";

	public static final String SETTINGS_KEY = "settings";

	public static final String TIMESTAMP = "ts";

	public static final String OBJECT_KEY = "object_key";

	public static final String PERSISTENT = "persistent";
	
	public static final String LIMIT = "limit";
	
	public static final String AGGREGATOR_FUNCTION_KEY = "aggregator_function_key";
	
	public static final String GROUPING_FUNCTION_KEY = "grouping_function_key";

	public static final String NAME_KEY = "name";

	public static final String PRIMARY_PROPERTY = "primary";

	


	public static boolean isSearchable(IHasMeta meta) {
		return meta.getMeta().getSingleValue(SEARCHABLE_KEY, Boolean.class,
				false);
	}

	public static boolean isIndexable(IHasMeta meta) {
		return meta.getMeta().getSingleValue(INDEXABLE_KEY, Boolean.class,
				false);
	}

	public static boolean isFixedBound(IHasMeta meta) {
		return meta.getMeta().getSingleValue(FIXED_BOUND_KEY, Boolean.class,
				false);
	}

	public static boolean isStatic(IHasMeta meta) {
		return meta.getMeta().getSingleValue(STATIC_KEY, Boolean.class, false);
	}
	
	public static String getCategory(IMeta meta) {
		return meta.getSingleValue(CATEGORY_KEY,String.class,null);
	}

	public static boolean isComputed(IHasMeta meta) {
		return meta.getMeta()
				.getSingleValue(COMPUTED_KEY, Boolean.class, false);
	}
	
	public static boolean isUserRunnable(IHasMeta meta) {
		return meta.getMeta()
				.getSingleValue(USER_RUNNABLE, Boolean.class, false);
	}

	public static String getCaption(IHasMeta meta) {
		IMeta meta2 = meta.getMeta();
		if (meta2 == null) {
			return "";
		}
		return meta2.getSingleValue(CAPTION_KEY, String.class, null);
	}
	public static String getName(IHasMeta meta) {
		IMeta meta2 = meta.getMeta();
		if (meta2 == null) {
			return "";
		}
		return meta2.getSingleValue(NAME_KEY, String.class, null);
	}

	public static String getDescription(IHasMeta meta) {
		return meta.getMeta().getSingleValue(DESCRIPTION_KEY, String.class,
				null);
	}
	
	public static String getImageKey(IHasMeta meta) {
		IMeta meta2 = meta.getMeta();
		if (meta2 == null) {
			return "";
		}
		return meta2.getSingleValue(IMAGE_KEY, String.class, "");
	}

	public static boolean isRequired(IHasMeta meta) {
		return meta.getMeta()
				.getSingleValue(REQUIRED_KEY, Boolean.class, false);
	}

	public static boolean isUnique(IHasMeta meta) {
		return meta.getMeta().getSingleValue(UNIQUE_KEY, Boolean.class, false);
	}

	public static String getMetaDescription(IHasMeta baseMeta,
			String requiredKey) {
		IMeta meta = baseMeta.getMeta();
		String string = meta.getSingleValue(requiredKey + DESCRIPTION_SUFFIX,
				String.class, null);
		return string;
	}

	public static String getStringValue(IHasMeta meta, String roleKey) {
		if (meta != null) {
			Object singleValue = meta.getMeta().getSingleValue(roleKey,
					Object.class, null);
			if (!(singleValue instanceof String)) {
				if (singleValue != null) {
					return singleValue.toString();
				}
			}
			return (String) singleValue;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T, A extends T> A getValue(IHasMeta meta, String roleKey,
			Class<T> clazz) {
		IMeta meta2 = meta.getMeta();
		return (A) meta2.getSingleValue(roleKey, clazz, (T) null);
	}

	public static <T, A extends T> A getService(IHasMeta context, Class<T> clazz) {
		if (context==null){
			return null;
		}
		IMeta meta2 = context.getMeta();
		if (meta2!=null){
		return meta2.getService(clazz);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T, A extends T> A getValue(IHasMeta context,
			String rangeMinKey, Class<T> class1, Object minValue) {
		IMeta meta2 = context.getMeta();
		return (A) meta2.getSingleValue(rangeMinKey, class1, minValue);
	}

	public static boolean getValue(IHasMeta context, String rangeMinKey) {
		return getValue(context, rangeMinKey, Boolean.class, false);
	}

	// TODO REMEMBER THAT READONLY IS CONTEXT DEPENDENT
	public static boolean isReadonly(IHasMeta property) {
		if (property != null) {
			return property.getMeta().getSingleValue(READ_ONLY_KEY,
					Boolean.class, false);
		}
		return true;
	}
	
	public static boolean isId(IHasMeta property) {
		if (property != null) {
			return property.getMeta().getSingleValue(ID_KEY,
					Boolean.class, false);
		}
		return true;
	}

	public static boolean isMultivalue(IHasMeta property) {
		if (property != null) {
			IMeta meta = property.getMeta();
			if (meta != null) {
				Boolean singleValue = meta.getSingleValue(MULTI_VALUE_KEY,
						Boolean.class, false);
				if (singleValue != null) {
					return singleValue;
				}
			}
		}
		return false;
	}
	
	public static boolean isPersistent(IHasMeta property) {
		if (property != null) {
			IMeta meta = property.getMeta();
			if (meta != null) {
				Boolean singleValue = meta.getSingleValue(PERSISTENT,
						Boolean.class, false);
				if (singleValue != null) {
					return singleValue;
				}
			}
		}
		return false;
	}

	public static Class<?> getSubjectClass(IHasMeta property2) {
		if (property2==null){
			return Object.class;
		}
		IMeta meta = property2.getMeta();
		if (meta==null){
			return Object.class;
		}
		return meta.getSingleValue(SUBJECT_CLASS_KEY,
				Class.class, Object.class);
	}

	public static boolean isPrimary(IProperty p0) {
		return getValue(p0, DefaultMetaKeys.PRIMARY_PROPERTY);
	}
}
