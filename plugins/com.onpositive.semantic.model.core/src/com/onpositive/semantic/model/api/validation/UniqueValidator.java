package com.onpositive.semantic.model.api.validation;

import java.text.MessageFormat;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.status.CodeAndMessage;

public class UniqueValidator extends ValidatorAdapter<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static final String NOT_UNIQUE_MESSAGE = Messages.getString("AbstractBinding.2"); //$NON-NLS-1$

	
	@Override
	public CodeAndMessage isValid(IValidationContext context, Object object) {
		if (DefaultMetaKeys.isUnique(context)&&ValueUtils.hasValue(object)) {
			IValidationContext parent = context.getParent();
			IHasMeta m=null;
			if (parent!=null){
				m=parent;
			}
			else{
				m=MetaAccess.getMeta(context.getObject());
			}
			IFindAllWithSimilarValue service = DefaultMetaKeys.getService(m,
					IFindAllWithSimilarValue.class);
			Object pobj = context.getObject();
			
			
			IProperty prop=DefaultMetaKeys.getService(context, IProperty.class);
			if (service!=null&&prop!=null){
			Iterable<Object> find = service.find(m,pobj, object, prop);
			if (find!=null){
				for (Object o:find){
					if ((o!=pobj)&&(o!=null)&&(!o.equals(pobj))){
						return CodeAndMessage
						.errorMessage(MessageFormat
								.format(NOT_UNIQUE_MESSAGE,
										DefaultMetaKeys
												.getCaption(context)));
					}
				}
			}
			}
		}
		return super.isValid(context, object);
	}
}