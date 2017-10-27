package com.onpositive.semantic.model.ui.roles;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.commons.platform.configuration.IAbstractConfiguration;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.IResourceLink;
import com.onpositive.core.runtime.Bundle;
import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.IResourceFinder;
import com.onpositive.core.runtime.Platform;
import com.onpositive.semantic.model.api.access.IClassResolver;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.realm.IDisposable;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.ICommitListener;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.ui.generic.widgets.ISelectorElement;

public class WidgetRegistry extends AbstractRoleMap<WidgetObject> {

	public static final class EditBinding extends Binding {
		private final ICommitListener doOnCommit;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EditBinding(Object object, ICommitListener doOnCommit) {
			super(object);
			this.doOnCommit = doOnCommit;
		}

		@Override
		public void dispose() {
			if (doOnCommit instanceof IDisposable){
				IDisposable m=(IDisposable) doOnCommit;
				m.dispose();
			}
			super.dispose();
		}
	}

	public static final class AddChildBinding extends Binding {
		private final Object pObject;
		private final IProperty prop;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AddChildBinding(Object object, Object pObject, IProperty prop) {
			super(object);
			this.pObject = pObject;
			this.prop = prop;
		}

		public void commit() {
			this.commit(this.getValue());
		}

		public void innerCommit(Object value) {
			PropertyAccess.addValue(prop, pObject, object);
			fireCommit(null);
		}
	}

	public static final class AddBinding extends Binding {
		private final ISelectorElement<Object> sel;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AddBinding(Object object, ISelectorElement<Object> sel) {
			super(object);
			this.sel = sel;
		}

		public void commit() {
			this.commit(this.getValue());
		}

		@SuppressWarnings("unchecked")
		public void innerCommit(Object value) {

			if (value == null) {
				return;
			}
			if (value instanceof Collection) {
				sel.addValues((Collection<Object>) value);
			} else {
				// FIXME what if selector was disposed or changed input
				sel.addValue(value);
			}
			fireCommit(null);
		}
	}

	public static final class AutoBinding extends Binding {
		private final ISelectorElement<?> va;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public AutoBinding(Class<?> c, ISelectorElement<?> va) {
			super(c);
			this.va = va;
		}

		public void commit() {
			this.commit(this.getValue());
		}

		@SuppressWarnings("unchecked")
		public void commit(Object value) {
			if (value == null) {
				return;
			}
			if (value instanceof Collection) {
				va.addValues((Collection<Object>) value);
			} else {
				va.addValue(value);
			}
			fireCommit(null);
		}
	}

	public static final class AutomatedObject extends WidgetObject {
		private final String dlfPath;
//		private final URL resolvedResource;
		private final IClassResolver service;

		public AutomatedObject(IConfigurationElement element,
				String dlfPath, IClassResolver service) {
			super(element);
			this.dlfPath = dlfPath;
//			this.resolvedResource = resolveResource;
			this.service = service;
		}

		@Override
		public Object evaluate(Binding bnd) {
			Bundle bundle = Platform.getBundle("com.onpositive.semantic.model.ui.generic");
			try{
				InputStream openStream = null;
				try {
					openStream = service.openResourceStream(dlfPath);
				} catch (Throwable e1) {
				}
				
				if (openStream == null) {
					final IResourceFinder finder = Platform.getFinder();
					final Object found = finder.find(Object.class, dlfPath);
					
					if (found instanceof IResourceLink) {
						final IResourceLink resourceLink = (IResourceLink) found;
						openStream = resourceLink.openStream();
					}
				}
				
				IAbstractConfiguration preferences = bundle.getPreferences();
				try {
					Object evaluate = DOMEvaluator.getInstance().evaluate(
							openStream,bnd,new ClassLoader() {
								public java.lang.Class<?> loadClass(String name) throws ClassNotFoundException {
									return service.resolveClass(name);
									
								};
								protected URL findResource(String name) {
									final URL foundResource = service.resolveResource(name);
									return foundResource;
								};
							},dlfPath,preferences);
					return evaluate;
					
				} finally {
					openStream.close();
				}
			}catch (Throwable e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}

		@Override
		public String getResource() {
			return dlfPath;
		}
	}

	private final static class FakeConfigurationElement implements
			IConfigurationElement,Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final IHasMeta meta;

		private FakeConfigurationElement(IHasMeta meta) {
			this.meta = meta;
		}

		@Override
		public String getName() {
			return DefaultMetaKeys.getStringValue(meta, DefaultMetaKeys.NAME_KEY);
		}

		@Override
		public String getContributorId() {
			return DefaultMetaKeys.getStringValue(meta, DefaultMetaKeys.NAME_KEY);
		}

		@Override
		public IConfigurationElement[] getChildren() {
			return new IConfigurationElement[0] ;
		}

		@Override
		public String getAttribute(String name) {
			return null;
		}

		@Override
		public Object createExecutableExtension(String primaryObjectProperty) {
			return null;
		}
	}

	private static final class OkListener implements ICommitListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		boolean isOk;

		public void commitPerformed(ICommand command) {
			isOk = true;
		}
	}

	private static WidgetRegistry instance;

	private WidgetRegistry() {
		super("com.onpositive.semantic.model.widgetRegistry",
				WidgetObject.class);
	}

	public static WidgetRegistry getInstance() {
		if (instance == null) {
			instance = new WidgetRegistry();
		}
		return instance;
	}

	public void showWidget(Binding bnd) {
		final Object object = bnd.getObject();
		final WidgetObject obj = this.getWidgetObject(object, bnd.getRole(),
				bnd.getTheme());
		if (obj != null)
			obj.show(bnd, (String) null);
		else
			System.err.println("Can't find suitable edirtor for "
					+ object.toString());
	}
	
	public static void show(Object object){
		Binding bnd=new Binding(object);
		getInstance().showWidget(bnd);
		//FIXME lifecycle
	}

	// @SuppressWarnings("unchecked")
	// public void showAddToNewObjectRealmWidget(IObjectRealm<?> realm,
	// String type, String theme, Object undoContext) {
	// final NewRealmMemberBinding bnd = new NewRealmMemberBinding(realm, type);
	// bnd.setUndoContext(undoContext);
	// this.checkLoad();
	// final Object object = bnd.getObject();
	// if (object == null) {
	// return;
	// }
	// final Class<?> class1 = object.getClass();
	// final Set<Object> types = new HashSet<Object>();
	// // if (realm instanceof ObjectdRealm<?>) {
	// // ObjectdRealm<?> t = (ObjectdRealm<?>) realm;
	// // types.add((type != null && type.length() > 0) ? realm.getType(type)
	// // : t.getType());
	// // }
	// final RoleKey ks = new RoleKey(this.getName(class1), bnd.getRole(),
	// theme, this.getTypes(types));
	// final WidgetObject er = this.getObject(class1, ks, types);
	// er.show(bnd);
	// }

	public WidgetObject getWidgetObject(Object object, String role, String theme) {
		this.checkLoad();
		if (object == null) {
			return null;
		}

		final Class<?> class1 = object.getClass();
		Set<? extends Object> types = null;
		// if (object instanceof ObjectdObject) {
		// final ObjectdObject ta = (ObjectdObject) object;
		// types = ta.getTypes();
		// }
		final RoleKey ks = new RoleKey(this.getName(class1), role, theme,
				this.getTypes(types));
		WidgetObject object2 = this.getObject(class1, ks, types);
		return getFromMeta(object, object2);
	}

	protected WidgetObject getFromMeta(Object object, WidgetObject object2) {
		if (object2 == null) {
			final IHasMeta meta = MetaAccess.getMeta(object);
			final String dlfPath = DefaultMetaKeys.getStringValue(meta,
					DefaultMetaKeys.DISPLAY_KEY);
			final IClassResolver service = DefaultMetaKeys.getService(meta,
					IClassResolver.class);
			if (dlfPath==null){
				return null;
			}
			
			if (dlfPath != null) {
				WidgetObject q = new AutomatedObject(new FakeConfigurationElement(meta), dlfPath,
						service);
				return q;
			}
		}
		return object2;
	}

	public void addFromRealm(final ISelectorElement<?> va, String themeId,
			ICommitListener commmitListener, Object undoContext) {
		final IRealm<?> ra = va.getRealm();
		final HashSet<Object> oo = new HashSet<Object>(ra.getContents());
		oo.removeAll(va.getCurrentValue());
		final Realm<Object> rs = new Realm<Object>(oo);
		final Binding bs = new AutoBinding(Object.class, va);
		bs.setUndoContext(undoContext);
		bs.setAdapter(ITextLabelProvider.class, new LabelProvider());
		bs.setAutoCommit(false);
		bs.addCommitListener(commmitListener);
		// bs.setMaxCardinality(Integer.MAX_VALUE);
		final WidgetObject er = this.get(themeId);
		bs.setRealm(rs);

		er.show(bs, (String) null);
		return;
	}

	public Object showNewObjectWidget(final ISelectorElement<Object> sel,
			Class<?> objectClass, String typeId, String themeId, Object object,
			ICommitListener listener, Object undoContext,boolean autoCommit) {
		this.checkLoad();
		if (object == null) {
			return null;
		}
		final Class<?> class1 = objectClass;
		Set<? extends Object> types = null;
		final RoleKey ks = new RoleKey(this.getName(class1), "new", themeId,
				this.getTypes(types));
		WidgetObject object2 = this.getObject(class1, ks, types);
		if (object2==null){
			object2=getFromMeta(class1, object2);
		}
		final Binding bs = new AddBinding(object, sel);
		bs.setUndoContext(bs); //TODO what is the right context for this dialog?
		bs.addCommitListener(listener);
		bs.setAutoCommit(autoCommit);
		if (object2 == null) {
			Platform.log(new IllegalArgumentException(
					"Unable to find suitable widget for :"
							+ objectClass.getName()));
			return null;
		}
		object2.show(bs, "new");
		return object2;
	}
	public Object showNewChildObjectWidget(final ISelectorElement<Object> sel,
			Class<?> objectClass, String typeId, String themeId, Object object,
			ICommitListener listener, Object undoContext,final Object pObject,final IProperty prop,boolean autocommit) {
		this.checkLoad();
		if (object == null) {
			return null;
		}
		final Class<?> class1 = objectClass;
		Set<? extends Object> types = null;
		final RoleKey ks = new RoleKey(this.getName(class1), "new", themeId,
				this.getTypes(types));
		WidgetObject object2 = this.getObject(class1, ks, types);
		if (object2==null){
			object2=getFromMeta(class1, object2);
		}
		final Binding bs = new AddChildBinding(object, pObject, prop);
		bs.setAutoCommit(autocommit);
		bs.setUndoContext(undoContext);
		bs.addCommitListener(listener);
		if (object2 == null) {
			Platform.log(new IllegalArgumentException(
					"Unable to find suitable widget for :"
							+ objectClass.getName()));
			return null;
		}
		object2.show(bs, "new");
		return object2;
	}

	public void showEditObjectWidget(Object firstElement, String role,
			String theme, String wiString, final ICommitListener doOnCommit,
			Object undoContext,boolean autoCommit) {
		final Binding bnd = new EditBinding(firstElement, doOnCommit);
		if (doOnCommit != null) {
			bnd.addCommitListener(doOnCommit);
		}
		
		this.checkLoad();
		bnd.setRole(role);
		
		bnd.setTheme(theme);
		bnd.setAutoCommit(autoCommit);
		if (autoCommit)
			bnd.setUndoContext(bnd);
		else
			bnd.setUndoContext(undoContext);
		final Object object = bnd.getObject();
		if (object == null) {
			return;
		}
		final Class<?> class1 = object.getClass();

		final Set<Object> types = new HashSet<Object>();

		final RoleKey ks = new RoleKey(this.getName(class1), role, theme,
				this.getTypes(types));
		if (wiString != null) {
			WidgetObject widgetObject = get(wiString);

			widgetObject.show(bnd, role);
			return;
		}
		WidgetObject er = this.getObject(class1, ks, types);
		if (er==null){
			er=getFromMeta(object, er);
		}
		if (er != null) {
			bnd.putMeta(DefaultMetaKeys.ROOT_UNDO_CONTEXT, undoContext);
			er.show(bnd, role);
			return;
		}
		if (firstElement instanceof IClusterizationPoint<?>) {
			IClusterizationPoint<?> p = (IClusterizationPoint<?>) firstElement;
			Object primaryValue = p.getPrimaryValue();
			showEditObjectWidget(primaryValue, role, theme, wiString,
					doOnCommit, undoContext, autoCommit);
			return;
		}
		bnd.dispose();
	}

	public static boolean createObject(Object initial) {
		Binding bdn = new Binding(initial);
		bdn.setAutoCommit(true);
		OkListener l = new OkListener();
		bdn.addCommitListener(l);
		getInstance().showWidget(bdn);
		return l.isOk;
	}
}