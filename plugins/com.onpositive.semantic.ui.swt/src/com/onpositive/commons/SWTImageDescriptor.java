package com.onpositive.commons;

import org.eclipse.jface.resource.ImageDescriptor;

public class SWTImageDescriptor implements ISWTDescriptor{

	final ImageDescriptor descriptor;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descriptor == null) ? 0 : descriptor.hashCode());
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
		SWTImageDescriptor other = (SWTImageDescriptor) obj;
		if (descriptor == null) {
			if (other.descriptor != null)
				return false;
		} else if (!descriptor.equals(other.descriptor))
			return false;
		return true;
	}

	public SWTImageDescriptor(ImageDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
	}

	public ImageDescriptor getDescripror(){
		return descriptor;
	}
}
