package com.onpositive.semantic.model.api.categories;

import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class Category extends BaseMeta implements Comparable<Category>{
	
	private static final long serialVersionUID = 1L;

	public Category(String name, String image) {
		super();
		putMeta(DefaultMetaKeys.CAPTION_KEY, name);
		putMeta(DefaultMetaKeys.ID_KEY, name);
		putMeta(DefaultMetaKeys.IMAGE_KEY, image);
		this.parentMeta=MetaAccess.getMeta(Category.class).getMeta();
	}

	public String getImageID(){		
		return DefaultMetaKeys.getStringValue(this, DefaultMetaKeys.IMAGE_KEY);
	}
	public String getName(){		
		return DefaultMetaKeys.getStringValue(this, DefaultMetaKeys.CAPTION_KEY);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getImageID() == null) ? 0 : getImageID().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
		Category other = (Category) obj;
		if (getImageID() == null) {
			if (other.getImageID() != null)
				return false;
		} else if (!getImageID().equals(other.getImageID()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public String toString(){
		return getName();
	}

	@Override
	public int compareTo(Category category2)
	{
		return getName().compareTo(category2.getName());
	}

	@Override
	public IMeta getMeta() {
		return this;
	}
}
