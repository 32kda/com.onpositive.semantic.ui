package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.access.ClassLoaderResolver;
import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.expressions.ExpressionAccess;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.property.DynamicProperty;
import com.onpositive.semantic.model.api.property.ExpressionValueProperty;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.validation.ValidationAccess;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBindable;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.tree.AbstractClusterizationPointProvider;
import com.onpositive.semantic.model.tree.IPresentationFactory;
import com.onpositive.semantic.model.tree.PropertyValueHierarchicalPointProvider;
import com.onpositive.semantic.model.tree.PropertyValuePointProvider;
import com.onpositive.semantic.model.ui.actions.Action;
import com.onpositive.semantic.model.ui.actions.IAction;
import com.onpositive.semantic.model.ui.generic.ICompositeElement;
import com.onpositive.semantic.model.ui.generic.IElementListener;
import com.onpositive.semantic.model.ui.generic.INodeLabelProvider;
import com.onpositive.semantic.model.ui.generic.widgets.ICanDrop;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.generic.widgets.IUIElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.ActionsElementHandler.ActionsSetting;

public class GroupByHandler extends AbstractActionElementHandler {

	private final class MO implements Observer ,Serializable{
		private final String groupBy;
		private final BindedAction bindedAction;

		private MO(String groupBy, BindedAction bindedAction) {
			this.groupBy = groupBy;
			this.bindedAction = bindedAction;
		}

		public void update(Observable o, Object arg) {
			if (arg != null) {
				bindedAction.setChecked(arg.equals(groupBy));
			} else {
				bindedAction.setChecked(groupBy.length() == 0);
			}
		}
	}

	private final class GroupAction extends ActionBinding implements ICanDrop{
		private final HashMap<Class, Object> extraAdapters;
		private final IPresentationFactory pfactory;
		private final String assotiatedBinding;
		private final String lock;
		private final IUIElement<?> control;
		private final Context context;
		private final String groupBy;
		private final boolean isPropogate;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private IListenableExpression<Object> parse;
		private IValueListener<Object> exp;
		private IElementListener disposeBindingListener;
		private IProperty property2;

		private GroupAction(HashMap<Class, Object> extraAdapters,
				IPresentationFactory pfactory, String assotiatedBinding,
				String lock, IUIElement<?> control, Context context,
				String groupBy, boolean isPropogate) {
			this.extraAdapters = extraAdapters;
			this.pfactory = pfactory;
			this.assotiatedBinding = assotiatedBinding;
			this.lock = lock;
			this.control = control;
			this.context = context;
			this.groupBy = groupBy;
			this.isPropogate = isPropogate;
		}

		@SuppressWarnings("unchecked")
		public void doAction() {
			final IListElement<Object> ls = (IListElement<Object>) control;
			if (groupBy.length() == 0) {
				ls.setAsTree(false);
				clearTr(control, ls);
				ls.setClusterizationPointProviders(null);
				getPropertyId(control).setValue(null);
				ls.setAsTree(false);
				ls.setClusterizationPointProviders(null);
				ls.removeDropSupportParticipant(this);
			} else {
				
				Object value2 = getPropertyId(control).getValue();
				if (value2 != null && value2.equals(groupBy)) {
					ls.setAsTree(false);
					clearTr(control, ls);
					ls.setClusterizationPointProviders(null);
					getPropertyId(control).setValue(null);
					ls.removeDropSupportParticipant(this);
					if (parse!=null){
						parse.removeValueListener(exp);
						parse.disposeExpression();
						parse=null;
						control.getParent().removeElementListener(disposeBindingListener);
						
						exp=null;
					}
					return;
				}
				clearTr(control, ls);
				if (assotiatedBinding != null
						&& assotiatedBinding.length() != 0) {
					IBinding binding2 = ls.getBinding().binding(
							assotiatedBinding);
					IRealm<?> ra = binding2.getRealm();
					if (ra != null) {
						ChangableValue realmId = getRealmId(control);
						realmId.setValue(ls.getBinding().getRealm());
						((Binding) ls.getBinding()).setRealm(ra);
					}
				}
				
				property2 = null;
				if (ExpressionAccess.isExpression(groupBy)) {
					if (groupBy.charAt(0) == '{') {
						if (parse == null) {
							parse = (IListenableExpression<Object>) ExpressionAccess
									.parse(groupBy.substring(1,
											groupBy.length() - 1),
											ls.getBinding());
							exp = new IValueListener<Object>() {

								@Override
								public void valueChanged(Object oldValue,
										Object newValue) {
									doAction();
									doAction();
								}
							};
							parse.addValueListener(exp);
							disposeBindingListener = new IElementListener() {
								
								@Override
								public void hierarchyChanged(IUIElement<?> element) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void elementRemoved(ICompositeElement<?, ?> parent,
										IUIElement<?> element) {
									// TODO Auto-generated method stub
									
								}
								
								@Override
								public void elementDisposed(IUIElement<?> element) {
									parse.disposeExpression();
									control.getParent().addElementListener(disposeBindingListener);
									parse=null;
									exp=null;
								}
								
								@Override
								public void elementCreated(IUIElement<?> element) {
									
								}
								
								@Override
								public void elementAdded(ICompositeElement<?, ?> parent,
										IUIElement<?> element) {
									
								}
								
								@Override
								public void bindingChanged(IUIElement<?> element, IBinding newBinding,
										IBinding oldBinding) {
									
								}

								@Override
								public void elementVisibilityChanged(
										IUIElement<?> element) {
									
								}
							};
							control.getParent().addElementListener(disposeBindingListener);
							parse.addValueListener(exp);
						}
						String s = "" + parse.getValue();
						property2 = new DynamicProperty(s);
					} else {
						IClassResolver cll = new ClassLoaderResolver(
								context.getClassLoader());
						IBinding binding2 = ls.getBinding();
						ExpressionValueProperty expressionValueProperty = new ExpressionValueProperty(
								groupBy, cll);
						expressionValueProperty.setParentContext(binding2);
						property2 = expressionValueProperty;
					}
				} else {

					property2 = new DynamicProperty(groupBy.intern());
				}
				if (!isPropogate) {
					final PropertyValuePointProvider<Object> propertyValuePointProvider = new PropertyValuePointProvider<Object>(
							property2);
					propertyValuePointProvider
							.setPresentationFactory(pfactory);
					for (final Class c : extraAdapters.keySet()) {
						propertyValuePointProvider.registerAdapter(c,
								extraAdapters.get(c));
					}
					final AbstractClusterizationPointProvider<Object>[] ns = new AbstractClusterizationPointProvider[] { propertyValuePointProvider };
					ls.setClusterizationPointProviders(lock, ns);
					ls.setAsTree(true);
				} else {
					final PropertyValueHierarchicalPointProvider<Object> propertyValuePointProvider = new PropertyValueHierarchicalPointProvider<Object>(
							property2);
					propertyValuePointProvider
							.setPresentationFactory(pfactory);
					for (final Class c : extraAdapters.keySet()) {
						propertyValuePointProvider.registerAdapter(c,
								extraAdapters.get(c));
					}
					final AbstractClusterizationPointProvider<Object>[] ns = new AbstractClusterizationPointProvider[] { propertyValuePointProvider };
					ls.setClusterizationPointProviders(lock, ns);
					ls.setAsTree(true);
				}
				getPropertyId(control).setValue(groupBy);
				ls.addDropSupportParticipant(this);
			}
		}

		private void clearTr(final IUIElement<?> control,
				final IListElement<?> ls) {
			ChangableValue realmId = getRealmId(control);
			IRealm<?> tr = (IRealm<?>) realmId.getValue();
			if (tr != null) {
				((Binding) ls.getBinding()).setRealm(tr);
				realmId.setValue(null);
			}
		}

		@Override
		public boolean canDrop(Object target, Object[] array) {
			target=LabelAccess.getPresentationObject(target);
			if (array.length==0){
				return false;
			}
			for (Object o:array){
				o=LabelAccess.getPresentationObject(o);
				IProperty property2=this.property2;
				if (property2 instanceof DynamicProperty){
					property2=PropertyAccess.getProperty(o, ((DynamicProperty)property2).getId());
				}	
				if (property2==null){
					return false;
				}
				ICommand createSetValueCommand = PropertyAccess.createSetValueCommand(o, target,property2);
				if (createSetValueCommand==null){
					return false;
				}
				if (ValidationAccess.validate(createSetValueCommand).isError()){
					return false;
				}
			}
			return true;
		}

		@Override
		public void drop(Object target, Object[] array) {
			// TODO Auto-generated method stub
			IBindable m=(IBindable) control;
			AbstractBinding c=null;
			if (m.getBinding()!=null){
				c=(AbstractBinding) m.getBinding();
			}
			
			target=LabelAccess.getPresentationObject(target);
			CompositeCommand cm=new CompositeCommand();
			for (Object o:array){
				o=LabelAccess.getPresentationObject(o);
				
				IProperty property2=this.property2;
				if (property2 instanceof DynamicProperty){
					property2=PropertyAccess.getProperty(o, ((DynamicProperty)property2).getId());
				}
				ICommand createSetValueCommand = PropertyAccess.createSetValueCommand( o,target,property2);
				if (createSetValueCommand==null){
					return ;
				}
				if (ValidationAccess.validate(createSetValueCommand).isError()){
					return ;
				}
				cm.addCommand(createSetValueCommand);
			}
			cm.getCommandExecutor().execute(cm);
			
			if (c!=null){
				c.onChildChanged();
			}
		}
	}

	public GroupByHandler() {
	}

	public static ChangableValue getPropertyId(IUIElement<?> c) {
		Object data = c.getData("com.onpositive.semantic.ui.xml.groupBy");
		if (data == null) {
			ChangableValue vc = new ChangableValue();
			c.setData("com.onpositive.semantic.ui.xml.groupBy", vc);
			return vc;
		}
		return (ChangableValue) data;
	}

	public static ChangableValue getRealmId(IUIElement<?> c) {
		Object data = c.getData("com.onpositive.semantic.ui.xml.realmId");
		if (data == null) {
			ChangableValue vc = new ChangableValue();
			c.setData("com.onpositive.semantic.ui.xml.realmId", vc);
			return vc;
		}
		return (ChangableValue) data;
	}

	protected Action contribute(ActionsSetting parentContext,
			final Context context, Element element) {
		final IUIElement<?> control = parentContext.getControl();
		final IPropertyEditor<IUIElement> c = control
				.getService(IPropertyEditor.class);
		final Binding bnd = (Binding) c.getBinding();

		if (bnd == null)
			return null;

		final NodeList childNodes = element.getChildNodes();
		final HashMap<Class, Object> extraAdapters = new HashMap<Class, Object>();
		if (childNodes!=null){
		for (int a = 0; a < childNodes.getLength(); a++) {
			final Node item = childNodes.item(a);
			if (item instanceof Element) {
				final Element el = (Element) item;
				final String attribute = el.getAttribute("class");
				if (attribute.length() > 0) {
					try {
						final Class<?> loadClass = context.getClassLoader()
								.loadClass(attribute);
						final Object newInstance = loadClass.newInstance();
						extraAdapters
								.put(INodeLabelProvider.class, newInstance);
					} catch (final Exception e) {
						Platform.log(e);
					}
				}
			}
		}
		}
		final String groupBy = element.getAttribute("propertyId");
		final String propogate = element.getAttribute("propogate");
		final String assotiatedBinding = element
				.getAttribute("useRealmFromBinding");
		final String presentationFactory = element
				.getAttribute("presentationFactory");
		String attribute = element.getAttribute("lockFirstColumnTo");
		final String lock = attribute.length() == 0 ? null : attribute;

		final IPresentationFactory pfactory = (presentationFactory != null && presentationFactory
				.length() > 0) ? (IPresentationFactory) context
				.newInstance(presentationFactory) : null;

		final boolean isPropogate = propogate.length() == 0 ? false : Boolean
				.parseBoolean(propogate);
		final ActionBinding binding = new GroupAction(extraAdapters, pfactory, assotiatedBinding, lock,
				control, context, groupBy, isPropogate);
		bnd.setBinding(element.getAttribute("id"), binding);
		final BindedAction bindedAction = new BindedAction(binding,
				IAction.AS_CHECK_BOX) ;
		getPropertyId(control).addObserver(new MO(groupBy, bindedAction));
		if (groupBy.length() == 0) {
			bindedAction.setChecked(getPropertyId(control).getValue() == null);
		}
		handleAction(element, context, bindedAction, parentContext.getControl());
		// parentContext.addAction(bindedAction, element);
		return bindedAction;
	}

	// protected void contribute(IProvidesToolbarManager parentContext,
	// Context context, Element element) {
	// throw new UnsupportedOperationException("Works only with actions tag");
	// }

}
