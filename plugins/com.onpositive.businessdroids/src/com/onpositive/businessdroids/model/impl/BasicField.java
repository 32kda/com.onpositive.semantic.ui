package com.onpositive.businessdroids.model.impl;


public class BasicField implements IEditableField {

	protected String id;
	protected Class<?> type;
	protected IPropertyMapping propertyMapping;
	private String title;
	private String[] categories;

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public BasicField(String id, Class<?> type) {
		if ((id == null) || (id.trim().length() == 0)) {
			throw new AssertionError("Id must not be empty!");
		}
		this.id = id;
		this.type = type;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Class<?> getType() {
		return this.type;
	}

	@Override
	public void setType(Class<?> type) {
		this.type = type;
	}
	

	@Override
	public String toString() {
		String text = this.id;		
		return text;
	}

	@Override
	public Object getPropertyValue(Object object) {
		return this.propertyMapping.getPropertyValue(object, this);
	}

	@Override
	public void setPropertyValue(Object object, Object value) {
		this.propertyMapping.setPropertyValue(object, this, value);
	}

	public IPropertyMapping getPropertyMapping() {
		return this.propertyMapping;
	}

	public void setPropertyMapping(IPropertyMapping propertyMapping) {
		this.propertyMapping = propertyMapping;
		// this.propertyMapping = new ProxyPropertyMapping(propertyMapping);
	}

	@Override
	public String getTitle() {
		if (this.title == null) {
			return this.getId();
		}
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;

	}

	@Override
	public boolean isReadOnly(Object object) {
		return false;
	}

	@Override
	public String[] getCategories() {
		return categories;
	}

}
