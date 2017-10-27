package com.onpositive.businessdroids.model.filters;

import com.onpositive.businessdroids.model.IColumn;
import com.onpositive.businessdroids.ui.dataview.StructuredDataView;
import com.onpositive.businessdroids.ui.dataview.persistence.IDataViewInitilizable;
import com.onpositive.businessdroids.ui.dataview.persistence.IStore;
import com.onpositive.businessdroids.ui.dataview.renderers.IFieldRenderer;
import com.onpositive.businessdroids.ui.dataview.renderers.IStringRenderer;

public class BasicStringFilter implements IFilter, IDataViewInitilizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4548036754812795189L;
	private static final String MODE = "mode";
	private static final String STRING = "string";
	public static final int PREFFIX_MODE = 0;
	public static final int CONTAIN_MODE = 1;

	protected int mode = BasicStringFilter.PREFFIX_MODE;
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	protected String str;
	protected String property;

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	protected boolean ignoreCase = true;
	private StructuredDataView dataView;

	public BasicStringFilter(StructuredDataView dataView, String str, int mode) {
		this(dataView, str, mode, true);
	}

	public BasicStringFilter(StructuredDataView dataView, String str, int mode,
			boolean ignoreCase) {
		this.dataView = dataView;
		if (str == null) {
			throw new AssertionError("Filter string shouldn't be null");
		}
		if (ignoreCase) {
			str = str.toLowerCase();
		}
		this.str = str;
		this.mode = mode;
		this.ignoreCase = ignoreCase;
	}

	@Override
	public boolean matches(Object record) {
		if (this.dataView == null) {
			return true;
		}
		for (IColumn column : this.dataView.getColumns()) {
			if (property!=null){
				if (!column.getId().equals(property)){
					continue;
				}
			}
			Object propValue = column.getPropertyValue(record);
			IFieldRenderer renderer = this.dataView.getRenderer(column);
			if (renderer instanceof IStringRenderer) {
				IStringRenderer rend = (IStringRenderer) renderer;
				propValue = rend.getStringFromValue(propValue, this.dataView,
						record);
			}
			if (propValue == null) {
				propValue = "";
			}
			String vl = propValue.toString();
			if (this.ignoreCase) {
				vl = vl.toLowerCase();
			}
			if (this.mode == BasicStringFilter.PREFFIX_MODE) {
				boolean startsWith = vl.startsWith(this.str);
				if (startsWith) {
					return startsWith;
				}
			} else if (this.mode == BasicStringFilter.CONTAIN_MODE) {
				boolean contains = vl.contains(this.str);
				if (contains) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getTitle() {
		return "Text";
	}

	public void setText(String text) {
		this.str = text;
	}

	public String getString() {
		return this.str;
	}

	public void setString(String str) {
		this.str = str;
	}

	@Override
	public void save(IStore store) {
		store.putString(BasicStringFilter.STRING, this.str);
		store.putInt(BasicStringFilter.MODE, this.mode);
	}

	@Override
	public void load(IStore store) {
		this.str = store.getString(BasicStringFilter.STRING, "");
		this.mode = store.getInt(BasicStringFilter.MODE,
				BasicStringFilter.CONTAIN_MODE);

	}

	@Override
	public void initialize(StructuredDataView dataView) {
		this.dataView = dataView;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.ignoreCase ? 1231 : 1237);
		result = prime * result + this.mode;
		result = prime * result
				+ ((this.str == null) ? 0 : this.str.hashCode());
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
		BasicStringFilter other = (BasicStringFilter) obj;
		if (this.ignoreCase != other.ignoreCase) {
			return false;
		}
		if (this.mode != other.mode) {
			return false;
		}
		if (this.str == null) {
			if (other.str != null) {
				return false;
			}
		} else if (!this.str.equals(other.str)) {
			return false;
		}
		return true;
	}

}
