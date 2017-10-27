package com.onpositive.semantic.model.ui.richtext;

import com.onpositive.semantic.model.api.labels.IDefaultProvider;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;

public class RichLabelAccess {

	public static StyledString getLabel(IHasMeta meta, Object parentObject,
			Object object) {
		IRichLabelProvider service = DefaultMetaKeys.getService(meta,
				IRichLabelProvider.class);
		if (service==null){
			service=DefaultMetaKeys.getService(MetaAccess.getMeta(object), IRichLabelProvider.class);
		}
		if (service != null) {
			return service.getRichTextLabel(object);
		}
		ITextLabelProvider tservice = DefaultMetaKeys.getService(meta,
				ITextLabelProvider.class);
		if (tservice==null||tservice instanceof IDefaultProvider){
			ITextLabelProvider service2 = DefaultMetaKeys.getService(MetaAccess.getMeta(object), ITextLabelProvider.class);
			if (service2!=null){
			tservice=service2;
			}
		}
		if (tservice != null) {
			if (tservice instanceof IRichLabelProvider){
				return ((IRichLabelProvider)tservice).getRichTextLabel(object);
			}
			return new StyledString(tservice.getText(meta, parentObject, object));
		}
		if (object!=null){
			return new StyledString(object.toString());
		}		
		return new StyledString();
	}

	public static StyledString getLabel(Object x) {
		if (x == null) {
			return new StyledString();
		}
		return getLabel(MetaAccess.getMeta(x), null, x);
	}

	public static StyledString getLabel(IHasMeta meta, Object object)
			throws NotFoundException {
		return getLabel(meta, null, object);
	}
}
