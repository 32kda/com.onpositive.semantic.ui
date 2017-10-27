package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import java.util.Date;
import java.util.Set;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import com.onpositive.commons.Activator;
import com.onpositive.core.runtime.CoreException;
import com.onpositive.semantic.model.api.property.DefaultPropertyMetadata;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.IPropertyMetaData;
import com.onpositive.semantic.model.api.roles.AbstractRoleMap;
import com.onpositive.semantic.model.api.roles.RoleObject;
import com.onpositive.semantic.model.realm.IType;
import com.onpositive.semantic.model.realm.ITypedObject;

public final class CellEditorRegistry extends AbstractRoleMap<RoleObject> {

	private CellEditorRegistry() {
		super("com.onpositive.semantic.model.cellEditor", RoleObject.class);
	}

	private static CellEditorRegistry instance;

	public static CellEditorRegistry getInstance() {
		if (instance == null) {
			instance = new CellEditorRegistry();
		}
		return instance;
	}

	public static CellEditor createEditor(Object parentObject, Object object,
			final Viewer parent, IProperty property,
			String role, String theme) {
		final CellEditor internalGet = getInstance().internalGet(parentObject,
				object, role, theme, parent, property);
		return internalGet;
	}

	private CellEditor internalGet(Object parentObject, Object object,
			String role, String theme, Viewer parent,
			IProperty property) {
		final Class<?> clazz = object != null ? object.getClass() : property
				.getSubjectClass();
		Set<? extends IType> types = null;
		if (object instanceof ITypedObject) {
			types = ((ITypedObject) object).getTypes();
		}
		final RoleKey ks = new RoleKey(this.getName(clazz), role, theme, this
				.getTypes(types));
		final RoleObject object2 = getInstance().getObject(clazz, ks, types);
		if (object2 != null) {
			ICellEditorFactory factory;
			try {
				factory = (ICellEditorFactory) object2.getObject();
				return factory.createEditor(parentObject, object, parent,
						property);
			} catch (final CoreException e) {
				Activator.log(e);
				return null;
			}
		}
		IPropertyMetaData propertyMetaData = property.getPropertyMetaData();
		final CellEditorFactory adapter = propertyMetaData!=null?propertyMetaData.getAdapter(CellEditorFactory.class):null;
		if (adapter != null) {
			try {
				final ICellEditorFactory newInstance = adapter.value()
						.newInstance();
				return newInstance.createEditor(parentObject, object, parent,
						property);
			} catch (final InstantiationException e) {
				Activator.log(e);
				return null;
			} catch (final IllegalAccessException e) {
				Activator.log(e);
				return null;
			}
		}
		if (object instanceof Boolean) {
			return new CheckBoxCellEditor((Composite) parent.getControl());
		}
		if (propertyMetaData instanceof DefaultPropertyMetadata){
			DefaultPropertyMetadata mq=(DefaultPropertyMetadata) propertyMetaData;
			Object object3 = mq.get(DefaultPropertyMetadata.CONTENT_TYPE_MODIFIER);
			if (object3!=null&&object3.equals(DefaultPropertyMetadata.CONTENT_TYPE_VALUE_DATE)){
				return new DateCellEditor((Composite) parent.getControl(), parentObject, property);
			}
		}
		if (((Class)property.getSubjectClass())==Date.class){
			return new DateCellEditor((Composite) parent.getControl(), parentObject, property);
		}
		return new TextCellEditorWithDecoration(
				(Composite) parent.getControl(), parentObject, property);
	}
}