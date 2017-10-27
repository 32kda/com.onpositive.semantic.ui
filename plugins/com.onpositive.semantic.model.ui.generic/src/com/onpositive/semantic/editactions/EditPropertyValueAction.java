package com.onpositive.semantic.editactions;

import com.onpositive.semantic.model.api.command.CompositeCommand;
import com.onpositive.semantic.model.api.command.ICommand;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.PropertyAccess;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;
import com.onpositive.semantic.model.binding.AbstractBinding;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.roles.WidgetObject;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;

public class EditPropertyValueAction extends PropertyAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3975148437144189840L;

	public EditPropertyValueAction(IBinding c, IProperty property, Object[] baseObjects,
			int style) {
		super(c,property, baseObjects, style);
		setText("Edit " + property.getId());
	}
	
	public EditPropertyValueAction(IBinding c, IProperty property, Object[] baseObjects,
			int style, String text) {
		super(c,property, baseObjects, style);
		setText(text);
	}

	
	@Override
	public void run() {
		Object value = getCommonValue();
		editValue(value);
	}

	protected Object editValue(Object value) {
		if (String.class.equals(PropertyAccess.getSubjectClass(property)) || property.getMeta().getService(ILabelLookup.class) != null) {
			String editorId = "com.onpositive.semantic.editactions.simpleedior";
			if (value != null && value instanceof String && ((String)value).length() > 100)
				editorId = "com.onpositive.semantic.editactions.texteditor";
			WidgetObject widgetObject = WidgetRegistry.getInstance().get(editorId);
			Binding bnd = new Binding(baseObjects[0],property,value){
				/**
				 * Serial version UID
				 */
				private static final long serialVersionUID = 1191350062816233103L;

				@Override
				public ICommand createCommand(Object value) {
					if (baseObjects.length == 1)
						return super.createCommand(value);
					CompositeCommand compositeCommand = new CompositeCommand();
					for (Object base: baseObjects) {
						compositeCommand.addCommand(createCommandForValue(value,base));
					}
					UndoMetaUtils.setUndoContext(compositeCommand, this.getUndoContext());
					return compositeCommand;
				}
				@Override
				public void commit() {
					super.commit();
					AbstractBinding abstractBinding = (AbstractBinding)EditPropertyValueAction.this.binding;
					if (abstractBinding!=null){
					abstractBinding.onChildChanged();
					}
				}
			};
			String caption = DefaultMetaKeys.getCaption(property);
			if (caption == null)
				caption = property.getId();
			bnd.setName(caption);
			bnd.setDescription(caption);
			widgetObject.show(bnd,"");
		}
		return value;
	}

}
