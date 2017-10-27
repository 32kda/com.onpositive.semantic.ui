package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import org.w3c.dom.Element;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.Context;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.expressions.BasicLookup;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.property.ValueUtils;
import com.onpositive.semantic.model.api.query.Query;
import com.onpositive.semantic.model.api.realm.AbstractFilter;
import com.onpositive.semantic.model.api.realm.FilteringRealm;
import com.onpositive.semantic.model.api.realm.IDescribableToQuery;
import com.onpositive.semantic.model.api.realm.IFilter;
import com.onpositive.semantic.model.api.realm.IFiltrable;
import com.onpositive.semantic.model.api.realm.IOwned;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.expressions.impl.BinaryExpression;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;
import com.onpositive.semantic.ui.core.IConfigurable;

public class ToggleFilterHandler extends AbstractActionElementHandler {

	private final static class ExpressionBasedFilter extends AbstractFilter
			implements IOwned, IDescribableToQuery {
		private String expression;

		public ExpressionBasedFilter(String expression) {
			super();
			this.expression = expression;
		}

		IBinding nm;

		public boolean accept(Object element) {
			if (element instanceof ITreeNode<?>) {
				ITreeNode<?> t = (ITreeNode<?>) element;
				Object element2 = t.getElement();

				if (accept(element2)) {
					return true;
				}
				ITreeNode<?>[] children = t.getChildren();
				if (children != null) {
					for (ITreeNode<?> r : children) {
						if (accept(r)) {
							return true;
						}
					}
				}
				return false;
			}
			Object calculate = ExpressionAccess.calculate(expression, nm,
					element);
			return ValueUtils.toBoolean(calculate);
		}

		public void setOwner(Object owner) {
			if (owner instanceof IListElement) {
				this.selector = (IListElement<?>) owner;
			}
			if (owner instanceof IBindable) {
				IBindable b = (IBindable) owner;
				this.nm = b.getBinding();
			} else {
				this.nm = null;
			}
		}

		IListElement<?> selector;

		@Override
		public boolean adapt(Query query) {
			if (nm.getValue() instanceof IRealm<?>) {
				BinaryExpression parse = (BinaryExpression) ExpressionAccess
						.parse("this filterBy [" + expression + "]", nm);
				Object value2 = parse.getValue();
				if (value2 instanceof FilteringRealm) {
					FilteringRealm value = (FilteringRealm) value2;

					return value.adapt(query);
				}
			} else {
				if (selector != null) {
					BasicLookup ll=new BasicLookup(nm, selector.getRealm());
					ll.registerChild("$", nm.getParent());
					ll.registerChild("$$", nm.getRoot());
					BinaryExpression parse = (BinaryExpression) ExpressionAccess
							.parse("this filterBy [" + expression + "]",ll);
					Object value2 = parse.getValue();
					if (value2 instanceof FilteringRealm) {
						FilteringRealm value = (FilteringRealm) value2;

						return value.adapt(query);
					}
				}
				return false;
				
			}
			return false;
		}
	}

	private final class FilterBinding extends ActionBinding {

		private final Context loader;
		private final IFiltrable fl;
		String className;
		private IFilter filter;
		boolean filterEnabled;
		private String expression;

		private FilterBinding(Element element, Context loader, IFiltrable fl) {
			this.loader = loader;
			this.fl = fl;
			this.className = element.getAttribute("class");
			this.expression = element.getAttribute("expression");
		}

		void toggleFilter() {
			if (this.filter == null) {
				if (expression != null && expression.length() > 0) {
					filter = new ExpressionBasedFilter(expression);
					this.fl.getFilters().add(filter);
					this.filterEnabled = true;
					return;
				}
				try {
					final Class<?> loadClass = this.loader
							.loadClass(this.className);
					try {
						this.filter = (IFilter) loadClass.newInstance();
					} catch (final InstantiationException e) {
						Platform.log(e);
					} catch (final IllegalAccessException e) {
						Platform.log(e);
					}
				} catch (final ClassNotFoundException e) {
					Platform.log(e);
				}
			}
			this.fl.getFilters().add(filter);
			this.filterEnabled = true;
		}

		public void doAction() {
			if (!this.filterEnabled) {
				this.toggleFilter();
				this.filterEnabled = true;
			} else {
				this.fl.getFilters().remove(this.filter);
				this.filterEnabled = false;
			}
		}
	}

	public ToggleFilterHandler() {
	}

	@SuppressWarnings("unchecked")
	protected Action contribute(ActionsSetting parentContext, Context context,
			final Element element) {
		final IUIElement<?> control = parentContext.getControl();
		final ClassLoader loader = context.getClassLoader();
		final IPropertyEditor<IUIElement<?>> c = control
				.getService(IPropertyEditor.class);
		final Binding bnd = (Binding) c.getBinding();

		if (bnd == null)
			return null;

		final IFiltrable fl = (IFiltrable) control;
		final FilterBinding binding = new FilterBinding(element, context, fl);
		String attribute = element.getAttribute("value");

		bnd.setBinding(element.getAttribute("id"), binding);
		final BindedAction bindedAction = new BindedAction(binding,
				IAction.AS_CHECK_BOX);
		if (attribute != null && attribute.length() > 0) {
			if (Boolean.parseBoolean(attribute)) {
				binding.toggleFilter();
				bindedAction.setChecked(true);
			}
		}
		control.addConfigurationPart(new IConfigurable() {

			public void loadConfiguration(IAbstractConfiguration configuration) {
				final boolean booleanAttribute = configuration
						.getBooleanAttribute(binding.className);
				if (booleanAttribute) {
					binding.toggleFilter();
					bindedAction.setChecked(true);
				}
			}

			public void storeConfiguration(IAbstractConfiguration configuration) {
				configuration.setBooleanAttribute(binding.className,
						binding.filterEnabled);
			}

		});
		handleAction(element, context, bindedAction, parentContext.getControl());
		// parentContext.addAction(bindedAction, element);
		return bindedAction;
	}

	// protected void contribute(IProvidesToolbarManager parentContext,
	// Context context, Element element) {
	// throw new UnsupportedOperationException("Works only with actions tag");
	// }
}
