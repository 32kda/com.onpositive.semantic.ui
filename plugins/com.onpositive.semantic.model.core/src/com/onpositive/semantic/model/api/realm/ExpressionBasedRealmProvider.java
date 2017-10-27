package com.onpositive.semantic.model.api.realm;

import com.onpositive.semantic.model.api.expressions.ConstantExpression;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.GetPropertyLookup;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;

public class ExpressionBasedRealmProvider extends ContextNotAwareProvider<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String expression;

	public ExpressionBasedRealmProvider(String expression) {
		super();
		this.expression = expression;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public IRealm<Object> getRealm(IHasMeta model, Object parentObject,
			Object object) {
		//FIXME
		GetPropertyLookup environment = new GetPropertyLookup(
				new ConstantExpression(object), null);
		environment.setParentContext(new ConstantExpression(parentObject));
		IListenableExpression<?> parse = ExpressionAccess.parse(expression,
				environment);
		ExpressionToRealm es = new ExpressionToRealm(parse);
		return es.getValue();
	}

	@Override
	IRealm<Object> getRealm(IHasMeta model, Object parentObject) {
		IExpressionEnvironment service = DefaultMetaKeys.getService(model, IExpressionEnvironment.class);
		if (service!=null){
			IListenableExpression<?> parse = ExpressionAccess.parse(expression,
					service);
			ExpressionToRealm es = new ExpressionToRealm(parse);
			return es.getValue();
		}
		GetPropertyLookup environment = new GetPropertyLookup(
				new ConstantExpression(parentObject), null);
		IListenableExpression<?> parse = ExpressionAccess.parse(expression,
				environment);
		ExpressionToRealm es = new ExpressionToRealm(parse);
		return es.getValue();
	}

}
