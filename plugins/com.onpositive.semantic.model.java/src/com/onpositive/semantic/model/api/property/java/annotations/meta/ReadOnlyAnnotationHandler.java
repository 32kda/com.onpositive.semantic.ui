package com.onpositive.semantic.model.api.property.java.annotations.meta;

import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.MesagingExpression;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IWritableMeta;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.ITargetDependentReadonly;
import com.onpositive.semantic.model.api.property.java.annotations.ReadOnly;

public class ReadOnlyAnnotationHandler implements CustomHandler<ReadOnly> {

	
	public void handle(ReadOnly annotation, IWritableMeta meta) {
		final String expression = annotation.value();
		final String message=annotation.message();
		if (expression.length() > 0) {
			meta.registerService(ITargetDependentReadonly.class,
					new ITargetDependentReadonly() {

						
						public boolean isReadonly(IHasMeta meta, Object object) {
							Object vl=null;
							if (meta instanceof IProperty){
								vl=((IProperty)meta).getValue(object);
							}
							Object value = ExpressionAccess.calculate(
									expression, object, vl);
							
							return ValueUtils.toBoolean(value);
						}

						
						public IListenableExpression<?> buildReadonlyExpression(
								IHasMeta meta, IExpressionEnvironment env) {
							IListenableExpression<?> parse = ExpressionAccess.parse(expression, env);
							if (message.length()>0){
								MesagingExpression ex=new MesagingExpression(parse, message);
								return ex;
							}
							return parse;
						}
					});
			meta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, null);
		} else if (annotation.readOnlyProvider() != ITargetDependentReadonly.class) {
			try {
				meta.registerService(ITargetDependentReadonly.class, annotation
						.readOnlyProvider().newInstance());
				meta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, null);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
