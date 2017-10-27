package com.onpositive.semantic.model.ui.roles;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.IResourceFinder;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.ui.generic.IKnownsImageObject;

/**
 * This class is responsible for handling images/image descriptors register via
 * <code>com.onpositive.semantic.ui.images</code> extension point.
 * 
 * @author kor
 * 
 */
public class ImageManager extends AbstractRoleMap<ImageObject> {

	private ImageManager() {
		super("com.onpositive.semantic.model.images", ImageObject.class); //$NON-NLS-1$
	}

	private static ImageManager instance;

	public static ImageManager getInstance() {
		if (instance == null) {
			instance = new ImageManager();
		}
		return instance;
	}

	/**
	 * Returns registered image descriptor (See
	 * <code>com.onpositive.semantic.ui.images</code> extension point)
	 * 
	 * @param id
	 *            registered image id
	 * @return image descriptor for id
	 */
	public static ImageDescriptor getImageDescriptor(String id) {
		ImageObject imageObject = getInstance().get(id);
		if (imageObject == null&&id.length()>0) {
			Context context = DOMEvaluator.getContext();
			if (context!=null){
			String uri = context.getUri();
			File f = new File(uri);
			File file = f = f.getParentFile();
			file = new File(file, id);
			String s = file.getPath();
			URL resource = context.getClassLoader().getResource(s);
			if (resource!=null){
				return new URLImageDescriptor(resource);
			}
			}
		}
		if (imageObject != null) {
			return imageObject.getImageDescriptor();
		}
		return null;
	}
	
	static IResourceFinder finder;
	
	static{
		finder=Platform.getFinder();
	}
	

	public static ImageDescriptor getImageDescriptorByPath(Object baseObject,
			String id) {
		Class<? extends Object> class1 = baseObject.getClass();
		URL resource = class1.getResource(id);
		if (resource != null) {
			return createFromURL(resource);
		}
		if (finder!=null){
			Object imageObject = finder.find(class1, id);
			if (imageObject instanceof IResourceLink){
				IResourceLink l=(IResourceLink) imageObject;
				return createFromLink(l);
			}
			if (imageObject instanceof URL){
				URL imageResource =(URL) imageObject;
				return createFromURL(imageResource);				
			}
		}
		
		return getImageDescriptor(id);
	}

	public ImageDescriptor getImageDescriptor(Object object, String role,
			String theme) {

		final ImageObject image = this.getImageObject(object, role, theme);
		if (image == null ) {
			if (object != null) {
				String imageKey = DefaultMetaKeys.getImageKey(MetaAccess
						.getMeta(object));
				if (!imageKey.isEmpty()) {
					ImageDescriptor descriptor = ImageManager
							.getImageDescriptorByPath(object, imageKey);
					return descriptor;
				}
			}
			return null;
		}		
		return image.getImageDescriptor(object);
	}

	public ImageObject getImageObject(Object object, String role, String theme) {
		if (object instanceof IKnownsImageObject) {
			IKnownsImageObject i = (IKnownsImageObject) object;
			return get(i.getImageID());
		}
		if (object == null) {
			return null;
		}
		this.checkLoad();
		final Class<?> class1 = object.getClass();
		Set<? extends Object> types = null;
		final RoleKey ks = new RoleKey(this.getName(class1), role, theme,
				this.getTypes(types));
		ImageObject object2 = this.getObject(class1, ks, types);

		return object2;
	}

	protected void initMap(HashMap<String, ImageObject> map) {
		super.initMap(map);
	}

	public static ImageDescriptor createFromURL(URL resourceAttribute) {
		return (ImageDescriptor) new URLImageDescriptor(resourceAttribute);
	}

	public static ImageDescriptor createFromLink(IResourceLink resourceAttribute) {
		return new ResourceLinkDescriptor(resourceAttribute);
	}

	@SuppressWarnings("rawtypes")
	public Object getService(IHasMeta meta, Class serv) {
		if (serv == IImageDescriptorProvider.class) {

		}
		return null;
	}
}
