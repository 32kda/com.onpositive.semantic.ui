package com.onpositive.semantic.model.ui.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.PrettyFormat;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.ElementDialog;
import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.command.SimpleOneArgCommand;
import com.onpositive.semantic.model.api.property.DefaultPropertyMetadata;
import com.onpositive.semantic.model.api.property.IOperation;
import com.onpositive.semantic.model.api.property.IOperationProvider;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyMetaData;
import com.onpositive.semantic.model.api.property.IPropertyProvider;
import com.onpositive.semantic.model.api.property.PropertyProviderLookup;
import com.onpositive.semantic.model.api.property.adapters.IRealmProvider;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.java.JavaPropertyProvider;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.core.IKnowsMenuItems;
import com.onpositive.semantic.model.realm.FilterAdapter;
import com.onpositive.semantic.model.realm.IFilter;
import com.onpositive.semantic.model.realm.IRealm;
import com.onpositive.semantic.model.realm.IValidator;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.property.PropertyStatistics.StatEntry;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.EditorFactory;

public final class PropertyBasedMenuContributor {

	private static final int MIN_VALUES_FOR_CUSTOMIZE = 20;



	private PropertyBasedMenuContributor() {

	}

	private static final class SimpleOperationAction extends Action {
		private final IOperation<? extends Object> a;
		private final List<? extends Object> asList;

		private SimpleOperationAction(String text,
				IOperation<? extends Object> a, List<? extends Object> asList) {
			super(text);
			this.a = a;
			this.asList = asList;
		}

		public void run() {
			
			a.executeOn((Collection) asList, new HashMap<String, Object>());
		}
	}

	public static abstract class CommandHelper {

		abstract void onRun0(final Object[] objeect, final IProperty property2,
				final IFilter flt, final Object s);

		abstract void generateCommandFromDialog(final Object[] objeect,
				final IProperty property2, final IFilter flt, final Binding b,
				final ElementDialog elementDialog);

		abstract void onRun2(final Object[] array, final IProperty p,
				final boolean multivalue, final Object o);
	}

	@SuppressWarnings("unchecked")
	public static void contribute(IMenuManager manager, ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			final Object[] array = sselection.toArray();

			HashSet<IProperty> commonProps = null;
			HashSet<IOperation> commonOps = new HashSet<IOperation>();
			for (Object currentSelection : array) {
				IPropertyProvider propertyProvider = PropertyProviderLookup
						.getPropertyProvider(currentSelection);
				if (propertyProvider instanceof IOperationProvider) {
					IOperationProvider p = (IOperationProvider) propertyProvider;
					Collection<IOperation<? extends Object>> genericOperations = p
							.getGenericOperations(currentSelection);
					commonOps.addAll(genericOperations);
				}
				if (commonProps == null) {
					Iterable<IProperty> properties = propertyProvider
							.getProperties(currentSelection);
					if (properties == null) {
						continue;
					}
					HashSet<IProperty> ourSet = new HashSet<IProperty>();
					for (IProperty m : properties) {
						ourSet.add(m);
					}
					commonProps = ourSet;
				} else {
					ArrayList<IProperty> toRemove = new ArrayList<IProperty>();
					for (IProperty p : commonProps) {
						IProperty property = propertyProvider.getProperty(currentSelection, p
								.getId());
						if (property == null) {
							toRemove.add(property);
						}
					}
					commonProps.removeAll(toRemove);
				}
			}

			if (commonProps != null) {
				commonProps.remove(null);
				manager.add(new Separator());
				ArrayList<IProperty> cc = new ArrayList<IProperty>(commonProps);
				Collections.sort(cc, new Comparator<IProperty>() {

					public int compare(IProperty arg0, IProperty arg1) {
						if (arg0.getPropertyMetaData().getAdapter(
								IPropertyMenuCustomizer.class) != null) {
							if (arg1.getPropertyMetaData().getAdapter(
									IPropertyMenuCustomizer.class) == null) {
								return -1;
							}
						}
						if (arg1.getPropertyMetaData().getAdapter(
								IPropertyMenuCustomizer.class) != null) {
							if (arg0.getPropertyMetaData().getAdapter(
									IPropertyMenuCustomizer.class) == null) {
								return 1;
							}
						}
						return arg0.getPropertyMetaData().getName().compareTo(
								arg1.getPropertyMetaData().getName());
					}
				});
				final DRun d = new DRun();
				for (final IProperty p : cc) {
					IPropertyMetaData propertyMetaData2 = p
							.getPropertyMetaData();
					if (p.getPropertyProvider() instanceof JavaPropertyProvider)
						continue;
					DefaultPropertyMetadata propertyMetaData = (DefaultPropertyMetadata) propertyMetaData2;
					Collection<IOperation<? extends Object>> assotiatedOperations = propertyMetaData
							.getAssotiatedOperations();
					final List<? extends Object> asList = Arrays.asList(array);
					for (final IOperation<? extends Object> operation : assotiatedOperations) {
						if (operation.isEnabledFor((Collection) asList)) {
							IProperty[] requiredProperties = operation
									.getRequiredProperties(array);
							if (requiredProperties == null
									|| requiredProperties.length == 0) {
								Action action = new SimpleOperationAction(operation
										.getName(), operation, asList);
								manager.add(action);
							} else {
								if (requiredProperties.length == 1) {

									CommandHelper m = new CommandHelper() {

										
										void onRun0(Object[] objeect,
												IProperty property2,
												IFilter flt, Object s) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), s);
											operation.executeOn((Collection) asList,
													map);
										}

										
										void generateCommandFromDialog(
												Object[] objeect,
												IProperty property2,
												IFilter flt, Binding b,
												ElementDialog elementDialog) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), b.getValue());
											operation.executeOn((Collection) asList,
													map);
											elementDialog.close();
										}

										
										void onRun2(Object[] array,
												IProperty p,
												boolean multivalue, Object o) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), o);
											operation.executeOn((Collection) asList,
													map);
										}
									};
									contributProp(
											manager,
											array,
											m,
											requiredProperties[0],
											(DefaultPropertyMetadata) requiredProperties[0]
													.getPropertyMetaData(), operation
													.getName());
								}
							}
						}
					}
				}
				manager.add(new Separator());
				for (final IProperty p : cc) {
					if (p.getPropertyProvider() instanceof JavaPropertyProvider)
						continue;
					DefaultPropertyMetadata propertyMetaData = (DefaultPropertyMetadata) p
							.getPropertyMetaData();
					contributProp(manager, array, d, p, propertyMetaData,
							propertyMetaData.getName());
				}
			}
			if (commonOps.size() > 0) {
				manager.add(new Separator());
				for (IOperation z : commonOps) {
					List<Object> asList = Arrays.asList(array);
					if (z.isEnabledFor(asList)) {
						SimpleOperationAction action = new SimpleOperationAction(
								z.getName(), z, asList);
						Object image = z.getImage();
						if (image != null) {
							action.setImageDescriptor((ImageDescriptor) image);
						}
						manager.add(action);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void contributeForOneProperty(IMenuManager manager,
			ISelection selection, String pId) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			final Object[] array = sselection.toArray();

			HashSet<IProperty> commonProps = null;
			for (Object o : array) {
				IPropertyProvider propertyProvider = PropertyProviderLookup
						.getPropertyProvider(o);
				if (commonProps == null) {
					Iterable<IProperty> properties = propertyProvider
							.getProperties(o);
					if (properties == null) {
						continue;
					}
					HashSet<IProperty> ourSet = new HashSet<IProperty>();
					for (IProperty m : properties) {
						if (m.getId().equals(pId)) {
							ourSet.add(m);
						}
					}
					commonProps = ourSet;
				} else {
					ArrayList<IProperty> toRemove = new ArrayList<IProperty>();
					for (IProperty p : commonProps) {
						IProperty property = propertyProvider.getProperty(o, p
								.getId());
						if (property == null) {
							toRemove.add(property);
						}
					}
					commonProps.removeAll(toRemove);
				}
			}

			if (commonProps != null) {

				manager.add(new Separator());
				ArrayList<IProperty> cc = new ArrayList<IProperty>(commonProps);
				Collections.sort(cc, new Comparator<IProperty>() {

					public int compare(IProperty arg0, IProperty arg1) {
						if (arg0.getPropertyMetaData().getAdapter(
								IPropertyMenuCustomizer.class) != null) {
							if (arg1.getPropertyMetaData().getAdapter(
									IPropertyMenuCustomizer.class) == null) {
								return -1;
							}
						}
						if (arg1.getPropertyMetaData().getAdapter(
								IPropertyMenuCustomizer.class) != null) {
							if (arg0.getPropertyMetaData().getAdapter(
									IPropertyMenuCustomizer.class) == null) {
								return 1;
							}
						}
						return arg0.getPropertyMetaData().getName().compareTo(
								arg1.getPropertyMetaData().getName());
					}
				});
				for (final IProperty p : cc) {
					DefaultPropertyMetadata propertyMetaData = (DefaultPropertyMetadata) p
							.getPropertyMetaData();
					Collection<IOperation<? extends Object>> assotiatedOperations = propertyMetaData
							.getAssotiatedOperations();
					final List<? extends Object> asList = Arrays.asList(array);
					for (final IOperation<? extends Object> a : assotiatedOperations) {
						if (a.isEnabledFor((Collection) asList)) {
							IProperty[] requiredProperties = a
									.getRequiredProperties(array);
							if (requiredProperties == null
									|| requiredProperties.length == 0) {
								Action action = new SimpleOperationAction(a
										.getName(), a, asList);
								manager.add(action);
							} else {
								if (requiredProperties.length == 1) {

									CommandHelper m = new CommandHelper() {

										
										void onRun0(Object[] objeect,
												IProperty property2,
												IFilter flt, Object s) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), s);
											a.executeOn((Collection) asList,
													map);
										}

										
										void generateCommandFromDialog(
												Object[] objeect,
												IProperty property2,
												IFilter flt, Binding b,
												ElementDialog elementDialog) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), b.getValue());
											a.executeOn((Collection) asList,
													map);
											elementDialog.close();
										}

										
										void onRun2(Object[] array,
												IProperty p,
												boolean multivalue, Object o) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), o);
											a.executeOn((Collection) asList,
													map);
										}
									};
									contributProp(
											manager,
											array,
											m,
											requiredProperties[0],
											(DefaultPropertyMetadata) requiredProperties[0]
													.getPropertyMetaData(), a
													.getName());
								}
							}
						}
					}
				}
/*				manager.add(new Separator());
				for (final IProperty p : cc) {
					DefaultPropertyMetadata propertyMetaData = (DefaultPropertyMetadata) p
							.getPropertyMetaData();
					contributProp(manager, array, d, p, propertyMetaData,
							propertyMetaData.getName());
				}*/
			}
/*			if (commonOps.size() > 0) {
				manager.add(new Separator());
				for (IOperation z : commonOps) {
					List<Object> asList = Arrays.asList(array);
					if (z.isEnabledFor(asList)) {
						SimpleOperationAction action = new SimpleOperationAction(
								z.getName(), z, asList);
						Object image = z.getImage();
						if (image != null) {
							action.setImageDescriptor((ImageDescriptor) image);
						}
						manager.add(action);
					}
				}
			}*/
		}
	}
	@SuppressWarnings("unchecked")
	public static void contributeForOnePropertyAndOneOperation(IMenuManager manager,
			ISelection selection, String pId, String operationId) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			IStructuredSelection sselection = (IStructuredSelection) selection;
			final Object[] array = sselection.toArray();

			HashSet<IProperty> commonProps = null;
			for (Object o : array) {
				IPropertyProvider propertyProvider = PropertyProviderLookup
						.getPropertyProvider(o);
				if (commonProps == null) {
					Iterable<IProperty> properties = propertyProvider
							.getProperties(o);
					if (properties == null) {
						continue;
					}
					HashSet<IProperty> ourSet = new HashSet<IProperty>();
					for (IProperty m : properties) {
						if (m.getId().equals(pId)) {
							ourSet.add(m);
						}
					}
					commonProps = ourSet;
				} else {
					ArrayList<IProperty> toRemove = new ArrayList<IProperty>();
					for (IProperty p : commonProps) {
						IProperty property = propertyProvider.getProperty(o, p
								.getId());
						if (property == null) {
							toRemove.add(property);
						}
					}
					commonProps.removeAll(toRemove);
				}
			}

			if (commonProps != null) {

				manager.add(new Separator());
				ArrayList<IProperty> cc = new ArrayList<IProperty>(commonProps);
				Collections.sort(cc, new Comparator<IProperty>() {

					public int compare(IProperty arg0, IProperty arg1) {
						if (arg0.getPropertyMetaData().getAdapter(
								IPropertyMenuCustomizer.class) != null) {
							if (arg1.getPropertyMetaData().getAdapter(
									IPropertyMenuCustomizer.class) == null) {
								return -1;
							}
						}
						if (arg1.getPropertyMetaData().getAdapter(
								IPropertyMenuCustomizer.class) != null) {
							if (arg0.getPropertyMetaData().getAdapter(
									IPropertyMenuCustomizer.class) == null) {
								return 1;
							}
						}
						return arg0.getPropertyMetaData().getName().compareTo(
								arg1.getPropertyMetaData().getName());
					}
				});
				for (final IProperty p : cc) {
					DefaultPropertyMetadata propertyMetaData = (DefaultPropertyMetadata) p
							.getPropertyMetaData();
					Collection<IOperation<? extends Object>> assotiatedOperations = propertyMetaData
							.getAssotiatedOperations();
					final List<? extends Object> asList = Arrays.asList(array);
					for (final IOperation<? extends Object> a : assotiatedOperations) {
						if(!a.getName().equals(operationId)){
							continue;
						}
						if (a.isEnabledFor((Collection) asList)) {
							IProperty[] requiredProperties = a
									.getRequiredProperties(array);
							if (requiredProperties == null
									|| requiredProperties.length == 0) {
								Action action = new SimpleOperationAction(a
										.getName(), a, asList);
								manager.add(action);
							} else {
								if (requiredProperties.length == 1) {

									CommandHelper m = new CommandHelper() {

										
										void onRun0(Object[] objeect,
												IProperty property2,
												IFilter flt, Object s) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), s);
											a.executeOn((Collection) asList,
													map);
										}

										
										void generateCommandFromDialog(
												Object[] objeect,
												IProperty property2,
												IFilter flt, Binding b,
												ElementDialog elementDialog) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), b.getValue());
											a.executeOn((Collection) asList,
													map);
											elementDialog.close();
										}

										
										void onRun2(Object[] array,
												IProperty p,
												boolean multivalue, Object o) {
											HashMap<String, Object> map = new HashMap<String, Object>();
											map.put(p.getId(), o);
											a.executeOn((Collection) asList,
													map);
										}
									};
									contributProp(
											manager,
											array,
											m,
											requiredProperties[0],
											(DefaultPropertyMetadata) requiredProperties[0]
													.getPropertyMetaData(), a
													.getName());
								}
							}
						}
					}
				}
			}
		}
	}
	
	
	private static void contributProp(IMenuManager manager,
			final Object[] array, final CommandHelper d, final IProperty p,
			DefaultPropertyMetadata propertyMetaData, String title) {
		if (propertyMetaData.get(DefaultPropertyMetadata.INPLACE_EDIT) != null||propertyMetaData.isCustom()) {
			{
				if (!propertyMetaData.isReadOnly()){
				final String description = "Please specify "
						+ title.toLowerCase() + ":";
				PropertyBasedMenuContributor.contibuteCustomizeProperty(
						manager, array, p, title, description,
						new FilterAdapter(), d);
				}
				return;
			}
		}
		if (propertyMetaData.isFixedBound() && (!propertyMetaData.isReadOnly()||(!(d  instanceof DRun)))) {
			fixedProperty(title, manager, array, d, p, propertyMetaData);
		}
	}

	@SuppressWarnings("unchecked")
	private static void fixedProperty(String title, IMenuManager manager,
			final Object[] array, final CommandHelper d, final IProperty p,
			DefaultPropertyMetadata propertyMetaData) {
		IRealmProvider adapter = p.getPropertyMetaData().getAdapter(
				IRealmProvider.class);
		ITextLabelProvider tlp = propertyMetaData.getAdapter(
				ITextLabelProvider.class);
		if (adapter != null) {
			HashSet<Object> values = null;
			HashSet<Object> values1 = new HashSet<Object>();
			for (Object o : array) {
				Binding binding = new Binding(o);
				IRealm realm = adapter.getRealm(binding);
				if (realm == null) {
					return;
				}
				if (values == null) {
					values = new HashSet<Object>(realm.getContents());
				} else {
					values.retainAll(realm.getContents());
				}
				values1.addAll(p.getValues(o));
				binding.dispose();
			}
			if (values != null && values.size() > MIN_VALUES_FOR_CUSTOMIZE) {
				final String description = "Please specify "
						+ title.toLowerCase() + ":";
				PropertyBasedMenuContributor.contibuteCustomizeProperty(
						manager, array, p, title, description,
						new FilterAdapter(), d);
				return;
			}
			if (values != null) {
				if (values.size() <= 1) {
					return;
				}
				final boolean multivalue = p.getPropertyMetaData()
						.isMultivalue();
				IPropertyMenuCustomizer customizer = p.getPropertyMetaData()
						.getAdapter(IPropertyMenuCustomizer.class);
				if (customizer != null) {
					customizer.customizeMenu(manager, p, array, values1);
				} else {
					MenuManager submanager = new MenuManager(title);
					for (final Object o : values) {
						Action action = new Action(tlp==null? LabelManager.getInstance()
								.getText(o, "", ""):tlp.getText(o),
								multivalue ? Action.AS_CHECK_BOX
										: Action.AS_RADIO_BUTTON) {

							
							public void run() {
								if (isChecked()) {
									d.onRun2(array, p, multivalue, o);
								}
							}

						};
						action.setChecked(values1.contains(o));
						submanager.add(action);
					}
					manager.add(submanager);
				}
			}
		}
	}

	public static void contibuteCustomizeProperty(IMenuManager manager,
			final Object[] objeect, final IProperty property2,
			final String title, final String description, final IFilter flt,
			final CommandHelper helper) {
		final IPropertyMetaData propertyMetaData = property2.getPropertyMetaData();
		IKnowsMenuItems<String> itemsAdapter = propertyMetaData.getAdapter(IKnowsMenuItems.class);
		IRealmProvider<?> adapter = propertyMetaData.getAdapter(
				IRealmProvider.class);
		ITextLabelProvider textLabelProvider = propertyMetaData.getAdapter(
				ITextLabelProvider.class);
		if (itemsAdapter != null)
		{
			String[] menuItems = itemsAdapter.getMenuItems(objeect[0],title);
			if (menuItems != null)
			{
				MenuManager assign = new MenuManager(title);
				manager.add(assign);
				
				boolean multivalue=propertyMetaData.isMultivalue();
				for (final String str : menuItems) {
					final String s=textLabelProvider==null?str:textLabelProvider.getText(str);					
					if (s.trim().length() > 0) {
						Action action = new Action(s,multivalue ? Action.AS_CHECK_BOX
								: Action.AS_RADIO_BUTTON) {
							public void run() {
								helper.onRun0(objeect, property2, flt, s);
							}
						};
						assign.add(action);
					}
				}
				return;
			}
			
		}
		if (adapter != null) {

			MenuManager assign = new MenuManager(title);
			manager.add(assign);
			HashSet<Object> values0 = null;
			HashSet<Object> values1 = new HashSet<Object>();
			for (Object o : objeect) {
				Binding binding = new Binding(o);
				IRealm realm = adapter.getRealm(binding);
				if (realm == null) {
					continue;
				}
				if (values0 == null) {
					values0 = new HashSet<Object>(realm.getContents());
				} else {
					if (realm.size()<1000){
						values0.retainAll(realm.getContents());
					}
				}
				values1.addAll(property2.getValues(o));
				binding.dispose();
			}
			if (values0 != null) {
				final HashSet<Object> values = new HashSet<Object>();
				for (Object o : objeect) {
					Object value = property2.getValue(o);
					if (value == null) {
						continue;
					}
					if (value instanceof Collection) {
						Collection<?> c = (Collection<?>) value;
						for (Object f : c) {
							values.add(f);
						}
					} else {
						values.add(value);
					}
				}
				values0 = checkAllowedValues(values0, values, Collections
						.singleton(property2));
				ArrayList<String> str = new ArrayList(values0);
				try{
				Collections.sort(str);
				}catch (Exception e) {
					Collections.sort(str,new Comparator<Object>() {

						public int compare(Object o1, Object o2) {
							return PrettyFormat.format(o1, false).compareTo(PrettyFormat.format(o2, false));
						}
					});
				}
				final Realm realm = new Realm(values0);
				boolean multivalue=propertyMetaData.isMultivalue();
				for (final Object o : str) {
					final String s=textLabelProvider==null?o.toString():textLabelProvider.getText(o);					
					if (s.trim().length() > 0) {
						Action action = new Action(s,multivalue ? Action.AS_CHECK_BOX
								: Action.AS_RADIO_BUTTON) {
							public void run() {
								helper.onRun0(objeect, property2, flt, o);
							}
						};
						assign.add(action);
						action.setChecked(values.contains(s));
					}
				}
				assign.add(new Separator());
				assign.add(new Action("Other...") {
					public void run() {
						Object next = values.isEmpty() ? "" : values.iterator()
								.next();
						DefaultPropertyMetadata propertyMetaData2 = (DefaultPropertyMetadata) property2.getPropertyMetaData();
						boolean multivalue = propertyMetaData2.isMultivalue();
						if (multivalue) {
							StringBuilder bld = new StringBuilder();
							for (Object s : values) {
								bld.append(PrettyFormat.format(s, false));
								bld.append(',');
							}
							if (bld.length() != 0) {
								bld.deleteCharAt(bld.length() - 1);
							}
							next = bld.toString();
						}
						final Binding b = new Binding(next);
						b.setRealm(realm);
						IContentAssistConfiguration adapter2 = propertyMetaData.getAdapter(IContentAssistConfiguration.class);
						if (adapter2!=null){
							b.setAdapter(IContentAssistConfiguration.class, adapter2);
						}
						IValidator<?> validator = propertyMetaData.getValidator(next);
						if (validator!=null){
							b.addValidator(validator);
						}
						b.setMaxCardinality(multivalue ? Integer.MAX_VALUE : 0);
						b.setAutoCommit(true);
						b.setName(description);
						Container c = new Container();
						c.setLayoutManager(new OneElementOnLineLayouter());
						AbstractUIElement<?> editor = EditorFactory.createEditor(b,property2);												
						c.add(editor);
						Container c1 = new Container();
						c.add(c1);
						ButtonSelector element = new ButtonSelector();
						element.setText("Ok");

						c1.add(element);
						ButtonSelector element2 = new ButtonSelector();
						c1.add(element2);
						element2.setText("Cancel");
						DisposeBindingListener.linkBindingLifeCycle(b, c);
						final ElementDialog elementDialog = new ElementDialog(
								b, c, title, "");

						element.addListener(SWT.Selection,
								new SWTEventListener<Button>() {

									public void handleEvent(
											AbstractUIElement<Button> element,
											Event event) {

										helper.generateCommandFromDialog(objeect,
												property2, flt, b,
												elementDialog);
									}
								});
						element2.addListener(SWT.Selection,
								new SWTEventListener<Button>() {

									public void handleEvent(
											AbstractUIElement<Button> element,
											Event event) {
										elementDialog.close();
									}
								});
						elementDialog.open();
					}

				});
			}
		}
	}

	private static HashSet<Object> checkAllowedValues(HashSet values0,
			HashSet values1, Set<IProperty> cc) {
		PropertyStatistics cs = new PropertyStatistics(values0);
		if (values0.size() > 15) {
			for (IProperty m : cc) {
				IPropertyStatistics adapter = m.getPropertyMetaData()
						.getAdapter(IPropertyStatistics.class);
				if (adapter != null) {
					PropertyStatistics p = adapter.getStatistics();
					for (StatEntry e : p.stat()) {
						cs.merge(e);
					}
				}
			}
			return cs.getBest(20, values1);
		}
		return values0;
	}

	

	static class DRun extends CommandHelper {

		void onRun0(final Object[] objeect, final IProperty property2,
				final IFilter flt, final Object s) {
			CompositeCommand m = new CompositeCommand();
			for (Object c : objeect) {
				Object doc = (Object) c;
				if (flt.accept(c)) {
					IProperty property = PropertyProviderLookup.getProperty(c,
							property2.getId());
					if (property != null) {
						SimpleOneArgCommand createSetValueCommand1 = (SimpleOneArgCommand) property
								.getCommandFactory().createSetValueCommand(
										property, c, s);
						createSetValueCommand1.setProperty(property);
						m.addCommand(createSetValueCommand1);
					}
				}
			}
			property2.getCommandExecutor().execute(m);
		}

		void generateCommandFromDialog(final Object[] objeect,
				final IProperty property2, final IFilter flt, final Binding b,
				final ElementDialog elementDialog) {
			CompositeCommand m = new CompositeCommand();
			for (Object c : objeect) {
				if (flt.accept(c)) {
					SimpleOneArgCommand createSetValueCommand1 = (SimpleOneArgCommand) property2
							.getCommandFactory().createSetValueCommand(
									property2, c, b.getValue());
					createSetValueCommand1.setProperty(property2);
					m.addCommand(createSetValueCommand1);
				}
			}
			elementDialog.close();
			property2.getCommandExecutor().execute(m);
		}

		void onRun2(final Object[] array, final IProperty p,
				final boolean multivalue, final Object o) {
			CompositeCommand mm = new CompositeCommand();
			for (Object obj : array) {
				IProperty property = PropertyProviderLookup.getProperty(obj, p
						.getId());
				if (property != null) {
					ICommand createSetValueCommand = multivalue ? property
							.getCommandFactory().createAddValueCommand(
									property, obj, o) : property
							.getCommandFactory().createSetValueCommand(
									property, obj, o);
					if (createSetValueCommand instanceof SimpleOneArgCommand) {
						SimpleOneArgCommand m = (SimpleOneArgCommand) createSetValueCommand;
						m.setProperty(property);
					}
					mm.addCommand(createSetValueCommand);
				}
			}
			p.getCommandExecutor().execute(mm);
		}
	}
}
