package com.onpositive.semantic.model.api.expressions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.FixedTargetCommandFactory;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.ICommandExecutor;
import com.onpositive.semantic.model.api.command.ICommandFactory;
import com.onpositive.semantic.model.api.command.IHasCommandExecutor;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.IMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IContextDependingProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;

public class BasicLookup implements IExpressionEnvironment, IClassResolver {

	private static final long serialVersionUID = 1L;
	private static BaseMeta defaultReadOnlyMeta = new BaseMeta();
	{
		defaultReadOnlyMeta.putMeta(DefaultMetaKeys.READ_ONLY_KEY, true);
	}

	private final class Expr extends ConstantExpression implements
			IEditableExpression<Object> {
		IProperty property;
		private Object parent;
		private FixedTargetCommandFactory ts;
		protected BaseMeta m;

		private Expr(final IProperty object, Object pv, Object result) {
			super(result);
			this.parent = pv;
			this.property = object;

			if (property != null) {
				m = new BaseMeta();
				ts = new FixedTargetCommandFactory() {
					@Override
					public IHasCommandExecutor getExecutor() {
						return new IHasCommandExecutor() {

							
							@Override
							public ICommandFactory getCommandFactory() {
								return ts;
							}

							
							@Override
							public ICommandExecutor getCommandExecutor() {
								final IHasCommandExecutor service = DefaultMetaKeys
										.getService(object,
												IHasCommandExecutor.class);
								return new ICommandExecutor() {

									
									@Override
									public void execute(ICommand cmd) {
										if (service != null) {
											ICommandExecutor commandExecutor = service
													.getCommandExecutor();
											if (commandExecutor == null) {
												throw new IllegalStateException(
														"comman executor is null "
																+ cmd);
											}
											cmd = transform(service, cmd);
											commandExecutor.execute(cmd);
											changed();
										}
									}

									protected ICommand transform(
											final IHasCommandExecutor service,
											ICommand cmd) {
										if (cmd instanceof CompositeCommand) {
											CompositeCommand cm1 = new CompositeCommand();
											cm1.setParentMeta(cmd.getMeta());
											for (ICommand z : (CompositeCommand) cmd) {
												cm1.addCommand(transform(
														service, z));
											}
											return cm1;
										}
										SimpleOneArgCommand m = (SimpleOneArgCommand) cmd;
										m = new SimpleOneArgCommand(
												m.getTarget(), m.getValue(),
												m.getKind(), service);
										return m;
									}

								};
							};
						};
					}
				};
				ts.setTarget(parent);

				m.setParentMeta(MetaAccess.getMeta(object).getMeta());
				m.setDefaultMeta(ts);
			} else {
				m = defaultReadOnlyMeta;
			}

		}

		
		@Override
		public IMeta getMeta() {
			return m;
		}

		
		@Override
		public void setValue(Object value) {
			PropertyAccess.setValue(property, parent, value);
			changed();
		}

		
		@Override
		public boolean isReadOnly() {
			return PropertyAccess.isReadonly(property, parent);
		}
	}

	protected void changed() {

	}

	private Object value;
	private HashMap<String, Object> extras;
	private IHasMeta pMeta;
	private IClassResolver resolver;

	public BasicLookup(IHasMeta pmeta, Object classExpressionValue) {
		this.value = classExpressionValue;
		this.pMeta = pmeta;
	}

	public void registerChild(String name, Object value) {
		if (extras == null) {
			extras = new HashMap<String, Object>();
		}
		extras.put(name, value);
	}

	@Override
	
	public IEditableExpression<?> getBinding(String path) {
		int id = path.indexOf('.');
		Object result = value;
		id = path.indexOf('.');
		if (id == -1) {
			id = path.length();
		}
		IProperty pr = null;
		Object pv = null;
		Object oldObject = result;
		while (id != -1) {
			if (result instanceof IExpressionEnvironment) {
				IExpressionEnvironment m = (IExpressionEnvironment) result;
				IListenableExpression<?> binding = m.getBinding(path);
				if (binding != null) {
					if (!(binding instanceof IEditableExpression<?>)){
						binding=new EditableWrapper(binding);
					}
					return (IEditableExpression<?>) binding;
				}
			}
			String pId = path.substring(0, id);

			Object vl = lookup(pId);
			Object value2 = null;
			if (vl != null) {
				value2 = vl;
			} else {
				IProperty old = pr;
				pr = PropertyAccess.getProperty(result, pId);

				if (pr == null) {
					try { //String with dot can also mean classname. Try to load it
						Class<?> clazz = Class.forName(path);
						return new Expr(null, null, clazz);
					} catch (ClassNotFoundException e) {
						return new Expr(null, null, null);
					}
					
				}
				if (pr instanceof IContextDependingProperty && old != null) {
					IContextDependingProperty m = (IContextDependingProperty) pr;
					value2 = m.getValue(old, oldObject, result);
				} else {
					value2 = pr.getValue(result);
				}
				
				// //here.
				// if (value2 == null) {
				//
				// return new Expr(pr,result,null);
				// }
			}
			pv = result;
			oldObject = result;
			result = value2;
			if (id != path.length()) {
				path = path.substring(id + 1);
				id = path.indexOf('.');
				if (id == -1) {
					if (path.length() > 0) {
						id = path.length();
					}
				}
			} else {
				break;
			}
		}
		if (result == this) {
			result = null;
		}
		return new Expr(pr, pv, result);
	}

	protected Object lookup(String pId) {
		if (extras != null && extras.containsKey(pId)) {
			Object object = extras.get(pId);
			if (object == null) {
				return this;
			}
			return object;
		}
		if (pId.equals("this")) {
			if (value == null) {
				return this;
			}
			return value;
		}
		return null;
	}

	
	@Override
	public IClassResolver getClassResolver() {
		return this;
	}

	
	@Override
	public Class<?> resolveClass(String className) {
		if (resolver == null && pMeta != null) {
			resolver = DefaultMetaKeys.getService(pMeta, IClassResolver.class);
		}
		if (resolver != null) {
			Class<?> resolveClass = resolver.resolveClass(className);
			if (resolveClass != null) {
				return resolveClass;
			}
		}
		if (value == null) {
			try {
				return ClassLoader.getSystemClassLoader().loadClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		try {
			ClassLoader classLoader = value.getClass().getClassLoader();
			if (classLoader == null) {
				return ClassLoader.getSystemClassLoader().loadClass(className);
			}
			return classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public URL resolveResource(String className) {
		if (resolver == null && pMeta != null) {
			resolver = DefaultMetaKeys.getService(pMeta, IClassResolver.class);
		}
		if (resolver != null) {
			Class<?> resolveClass = resolver.resolveClass(className);
			if (resolveClass != null) {
				return resolveClass.getResource(className);
			}
		}
		return value.getClass().getResource(className);
	}
	
	@Override
	public InputStream openResourceStream(String path) throws IOException {
		return resolveResource(path).openStream();
	}

}
