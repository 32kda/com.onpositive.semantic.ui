package com.onpositive.semantic.model.api.labels;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

//TODO FIXME
public class LabelAccess {

	public static Object getPresentationObject(Object obj){
		if (obj instanceof IHasPresentationObject){
			IHasPresentationObject p=(IHasPresentationObject) obj;
			Object element = p.getElement();
			return getPresentationObject(element);
		}
		return obj;
	}
	
	public static String getLabel(IHasMeta meta, Object parentObject,
			Object object) {
		object=getPresentationObject(object);
		ITextLabelProvider service = DefaultMetaKeys.getService(meta,
				ITextLabelProvider.class);
		if (service==null||(service instanceof IDefaultProvider)){
			ITextLabelProvider service2 = DefaultMetaKeys.getService(MetaAccess.getMeta(object),
					ITextLabelProvider.class);
			if (service2!=null){
			service = service2;
			}
		}
		if (service != null) {
			return service.getText(meta, parentObject, object);
		}
		if (object != null) {
			return object.toString();
		}
		return "";
	}
	public static String getDescription(IHasMeta meta, Object parentObject,
			Object object) {
		ITextLabelProvider service = DefaultMetaKeys.getService(meta,
				ITextLabelProvider.class);
		if (service != null) {
			return service.getDescription(object);
		}
		if (object != null) {
			return object.toString();
		}
		return "";
	}

	public static String getLabel(Object x) {
		if (x == null) {
			return "";
		}
		return getLabel(MetaAccess.getMeta(x), null, x);
	}

	public static String getLabel(IHasMeta meta, Object object){
		return getLabel(meta, null, object);
	}

	public static Object lookupFromLabel(IHasMeta meta, Object parentObject,
			String object) throws NotFoundException{
		ILabelLookup lm=DefaultMetaKeys.getService(meta, ILabelLookup.class);
		if (lm!=null){
			return lm.lookUpByLabel(meta, parentObject, object);
		}
		return null;
	}

	public static Object lookupFromLabel(IHasMeta meta, String object)throws NotFoundException {
		return getLabel(meta, null, object);
	}
	
}
