package com.onpositive.semantic.model.api.realm;

import java.util.Collection;

import com.onpositive.semantic.model.api.changes.HashDelta;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.expressions.AbstractListenableExpression;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.expressions.LyfecycleUtils;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IServiceProvider;
import com.onpositive.semantic.model.api.property.ComputedProperty;
import com.onpositive.semantic.model.api.property.ValueUtils;

@SuppressWarnings("rawtypes")
public class ExpressionToRealm extends AbstractListenableExpression<IRealm>
		implements IValueListener ,IDisposable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IListenableExpression<?> expression;
	private CollectionBasedRealm<?> rvalue;
	private ObjectListeningRealm pm = new ObjectListeningRealm();

	public ExpressionToRealm(final IListenableExpression<?> expression) {
		this.expression = expression;
		expression.addValueListener(this);
		setNewValue(calc());
		pm.putMeta(DefaultMetaKeys.READ_ONLY_KEY, new ComputedProperty() {

			
			@Override
			public Object getValue(Object obj) {
				if (expression instanceof IEditableExpression) {
					IEditableExpression<?> r = (IEditableExpression<?>) expression;
					return r.isReadOnly();
				}
				if (rvalue == null) {
					return DefaultMetaKeys.isReadonly(pm.getParent());
				}
				return null;
			}
		});
		pm.registerService(IHasCommandExecutor.class,
				new IServiceProvider<IHasCommandExecutor>() {

					
					@Override
					public IHasCommandExecutor getService(IHasMeta meta,
							Class<IHasCommandExecutor> serv, IHasMeta original) {
						if (rvalue == null) {
							return DefaultMetaKeys.getService(pm.getParent(),
									IHasCommandExecutor.class);
						}
						if (expression instanceof IEditableExpression) {
							IEditableExpression<?> r = (IEditableExpression<?>) expression;
							final IHasCommandExecutor service = DefaultMetaKeys
									.getService(r, IHasCommandExecutor.class);
							if (service != null) {
								return service;
							}
						}
						return null;
					}
				});
	}

	@SuppressWarnings("unchecked")
	private Object calc() {
		Object value2 = expression.getValue();
		if (value2 instanceof IRealm && value2 != pm.getParent()) {
			pm.setParent((IRealm<?>) value2);

			rvalue = null;
			return pm;
		}
		Iterable<Object> collection = ValueUtils.toCollection(value2);
		if (collection instanceof Collection) {
			if (rvalue != null) {
				HashDelta buildFrom = HashDelta.buildFrom(rvalue.getContents(),
						(Collection) collection);
				rvalue.applyDelta(buildFrom);
				return pm;
			}
			OrderedRealm r = new OrderedRealm((Collection) collection);
			LyfecycleUtils.markShortLyfeCycle(r, this);
			initRealm(r);
			return pm;
		}
		OrderedRealm r = new OrderedRealm();
		r.add(collection);
		initRealm(r);
		return pm;
	}

	private void initRealm(OrderedRealm r) {
		rvalue = r;
		pm.setParent(rvalue);
	}

	
	@Override
	public void dispose() {
		pm.dispose();
		expression.disposeExpression();
		expression.removeValueListener(this);
	}

	
	@Override
	public void valueChanged(Object oldValue, Object newValue) {
		setNewValue(calc());
	}
}
