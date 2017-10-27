package com.onpositive.semantic.model.ui.roles;

import com.onpositive.core.runtime.CoreException;
import com.onpositive.core.runtime.IConfigurationElement;

public class ImageObject extends RoleObject {

	private ImageDescriptor descriptor;
	private boolean urlProviderInited;
	private IImageDescriptorProvider urlProvider;
	
	
	public ImageObject(IConfigurationElement element) {
		super(element);
	}

	public ImageDescriptor getImageDescriptor(Object target) {
		if (!this.urlProviderInited) {
			try {

				final String stringAttribute = this.getStringAttribute(
						"imageDescriptorProvider", //$NON-NLS-1$
						null);
				if (stringAttribute != null) {
					Object objectAttribute = this.getObjectAttribute(
							"imageDescriptorProvider", Object.class);
					if (objectAttribute instanceof IImageDescriptorProvider) {
						this.urlProvider = (IImageDescriptorProvider) objectAttribute; //$NON-NLS-1$
					} 
				}
			} catch (final CoreException e) {
				throw new RuntimeException(e);
			}
			this.urlProviderInited = true;
		}
		if (this.urlProvider != null) {
			return this.urlProvider.getImageDescriptor(target);
		}
		
		return this.getImageDescriptor();
	}

//	
//	public Image getImage(Object target) {
//		final ImageDescriptor imageDescriptor = this.getImageDescriptor(target);
//		if (imageDescriptor == null) {
//			if (canGetImage(target)) {
//				return imageProvider.getImage(target);
//			}
//			return null;
//		}
//		if (this.urlImages == null) {
//			this.urlImages = new HashMap<ImageDescriptor, Image>();
//		}
//		final Image image2 = this.urlImages.get(imageDescriptor);
//		if (image2 != null) {
//			return image2;
//		}
//		final Image createImage = imageDescriptor.toImage();
//		this.urlImages.put(imageDescriptor, createImage);
//		return createImage;
//	}

	public ImageDescriptor getImageDescriptor() {
		if (this.descriptor == null) {
			this.descriptor = new ResourceLinkDescriptor(this.getResourceAttribute("image")); //$NON-NLS-1$
		}
		return this.descriptor;
	}

//	public Image getImage() {
//		if (this.image == null) {
//			this.image = this.getImageDescriptor().toImage();
//		}
//		return this.image;
//	}

//	public void disposeImage() {
//		if (this.image != null) {
//			if (!this.image.isDisposed()) {
//				this.image.dispose();
//				this.image = null;
//			}
//		}
//		if (this.urlImages != null) {
//			for (final Image i : this.urlImages.values()) {
//				if (i != null) {
//					if (!i.isDisposed()) {
//						i.dispose();
//					}
//				}
//			}
//			this.urlImages = null;
//		}
//	}
}