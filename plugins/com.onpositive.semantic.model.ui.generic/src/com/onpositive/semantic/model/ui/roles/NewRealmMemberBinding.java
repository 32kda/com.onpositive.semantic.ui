package com.onpositive.semantic.model.ui.roles;

import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;

public class NewRealmMemberBinding extends Binding {
	//FIXME
	public NewRealmMemberBinding(AbstractBinding abstractBinding, String id,
			Object object) {
		super(abstractBinding, id, object);
		// TODO Auto-generated constructor stub
	}

	public NewRealmMemberBinding(AbstractBinding abstractBinding, String id) {
		super(abstractBinding, id);
		// TODO Auto-generated constructor stub
	}

	public NewRealmMemberBinding(Class<?> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public NewRealmMemberBinding(Object base, IProperty property, Object value) {
		super(base, property, value);
		// TODO Auto-generated constructor stub
	}

	public NewRealmMemberBinding(Object object) {
		super(object);
		// TODO Auto-generated constructor stub
	}

//	private final IObjectRealm<IIdentifiableObject> owner;
//	private IType type;
//
//	@SuppressWarnings("unchecked")
//	public NewRealmMemberBinding(IObjectRealm<?> realm, String type) {
//		super(realm.newObject());
//		this.setAutoCommit(false);
//		setRealm(realm);
//		this.setRole("new");
//		if ((type.length() == 0) || (type == null)) {
//			if (realm instanceof ITypedRealm<?>) {
//				final ITypedRealm<?> tp = (ITypedRealm<?>) realm;
//				this.type = tp.getType();
//			}
//		} else {
//			this.type = realm.getType(type);
//			if (this.type == null) {
//				throw new IllegalArgumentException();
//			}
//		}
//		
//		this.owner = (IObjectRealm<IIdentifiableObject>) realm;
//	}
//
//	public void addCommitCommand(CompositeCommand cmd) {
//		super.addCommitCommand(cmd);
//		cmd.addCommand(this.owner
//				.getObjectAdditionCommand((IIdentifiableObject) this.getValue()));
//		if (type!=null){
//			cmd.addCommand(this.owner
//					.createSetTypeCommand(this.getValue(),type));	
//		}
//		
//	}
}
