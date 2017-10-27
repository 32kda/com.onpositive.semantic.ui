package com.onpositive.semantic.editactions;

import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.IStructuredSelection;
import com.onpositive.semantic.model.ui.generic.widgets.IAddNewAction;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.StructuredSelection;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class DefaultAddNewAction extends OwnedAction implements IAddNewAction{

	protected String typeId;

	protected String themeId;

	protected Class<?> objectClass;

	protected boolean createChild;

	public void setWidgetId(String themeId) {
		this.themeId = themeId;
	}

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getWidgetId() {
		return this.themeId;
	}
	
	public Class<?> getObjectClass() {
		return this.objectClass;
	}

	public void setObjectClass(Class<?> objectClass) {
		this.objectClass = objectClass;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DefaultAddNewAction(int style,
			IListElement<?> sl) {
		super(ISelectorElement.ADD_ACTION, style, sl);
	}

	
	public boolean isActuallyEnabled() {
		if (createChild) {
			if (this.owner != null) {
				if (owner.getViewerSelection().size() != 1) {
					return false;
				}
				Object firstElement = owner.getViewerSelection()
						.getFirstElement();
				IProperty property = PropertyAccess
						.getProperty(firstElement,
								(owner)
										.getProperty());
				if (property != null) {
					return !PropertyAccess.isReadonly(property,
							firstElement);
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (createChild ? 1231 : 1237);
		result = prime * result
				+ ((objectClass == null) ? 0 : objectClass.hashCode());
		result = prime * result + ((themeId == null) ? 0 : themeId.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		return result;
	}
	
	protected Object preCreate(){
		return null;
	}
	@SuppressWarnings("unchecked")
	public void internalRun() {
		Object expandedTreePaths = preCreate();
		try {
			if (createChild) {
				Object firstElement = owner.getViewerSelection()
						.getFirstElement();
				IProperty property = PropertyAccess
						.getProperty(firstElement,
								( owner)
										.getProperty());
				if (property != null) {
					Class<?> objectClass2 = this.objectClass;
					if (objectClass2 == null) {
						objectClass2 = DefaultMetaKeys
								.getSubjectClass(property);
					}
					try {
						final Object object = objectClass2.newInstance();
						PropertyAccess.setParentSilent(object,firstElement);							
						WidgetRegistry.getInstance()
								.showNewChildObjectWidget(
										(ISelectorElement) this.owner,
										objectClass2,
										this.typeId,
										this.themeId,
										object,
										new CommmitListener(
												owner
														
														.getViewerSelection()),
										owner.getUndoContext(),
										firstElement, property, true);
						treeExpand(firstElement);
						return;
					} catch (Exception e) {
						Platform.log(e);
					}
				}
			}
			if (!this.owner.isValueAsSelection()) {
				try {
					Class<?> objectClass2 = this.objectClass;
					if (objectClass2 == null) {
						objectClass2 = DefaultMetaKeys
								.getSubjectClass(owner.getRealm());
					}
					final Object object = objectClass2.newInstance();
					IBinding binding = owner.getBinding();
					if (binding!=null&& binding.getParent()!=null){
						PropertyAccess.setParentSilent(object,binding.getParent().getValue());
					}
					WidgetRegistry.getInstance().showNewObjectWidget(
							(ISelectorElement) this.owner,
							objectClass2,
							this.typeId,
							this.themeId,
							object,
							new CommmitListener((IStructuredSelection) owner
									.getViewerSelection()),
							owner.getUndoContext(), true);
				} catch (final InstantiationException e) {
					Platform.log(e);
				} catch (final IllegalAccessException e) {
					Platform.log(e);
				}
			} else {
				// FIXME
				// if (realm instanceof IObjectRealm<?>) {
				// WidgetRegistry.getInstance().showAddToNewObjectRealmWidget(
				// (IObjectRealm<?>) realm, this.typeId, this.themeId,
				// owner.getUndoContext());
				// } else {
				// throw new UnsupportedOperationException();
				// }
			}
		} finally {
			postCreate(expandedTreePaths, (IListElement) owner);
		}
	}

	protected void treeExpand(Object firstElement) {
		
	}

	protected void postCreate(Object expandedTreePaths, IListElement owner) {
		
	}

	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultAddNewAction other = (DefaultAddNewAction) obj;
		if (createChild != other.createChild)
			return false;
		if (objectClass == null) {
			if (other.objectClass != null)
				return false;
		} else if (!objectClass.equals(other.objectClass))
			return false;
		if (themeId == null) {
			if (other.themeId != null)
				return false;
		} else if (!themeId.equals(other.themeId))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		return true;
	}

	public void setCreateChild(boolean b) {
		this.createChild = true;
	}
}
