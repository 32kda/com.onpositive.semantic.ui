package com.onpositive.semantic.ui.swt;

import java.util.HashMap;

import org.w3c.dom.Element;

import com.onpositive.semantic.model.ui.generic.widgets.IUIElementFactory;
import com.onpositive.semantic.model.ui.generic.widgets.handlers.UIElementHandler.UIHandlerStruct;

public class SWTUIEngine implements IUIElementFactory {

	public SWTUIEngine() {
		ClassLoader loader=this.getClass().getClassLoader();
		this.names
				.put("tab-folder", new UIHandlerStruct(loader,"com.onpositive.commons.elements.TabFolderElement")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("ctab-folder", new UIHandlerStruct(loader,"com.onpositive.commons.elements.CTabFolderElement")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("container", new UIHandlerStruct(loader,"com.onpositive.commons.elements.Container")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("properties", new UIHandlerStruct(loader,"com.onpositive.semantic.ui.layouts.SemanticLayoutComposite")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("vc", new UIHandlerStruct(loader,"com.onpositive.commons.elements.VContainer")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("browser", new UIHandlerStruct(loader,"com.onpositive.commons.elements.BrowserElement")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("hc", new UIHandlerStruct(loader,"com.onpositive.commons.elements.HContainer")); //$NON-NLS-1$ //$NON-NLS-2$		
		this.names
				.put("string", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.OneLineTextElement")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("text", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.ViewerTextElement")); //$NON-NLS-1$ //$NON-NLS-2$		
		this.names
				.put("combo", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.structured.ComboEnumeratedValueSelector")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("table", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.structured.columns.TableEnumeratedValueSelector")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("list", "com.onpositive.semantic.ui.xml.ListSelectionHandler"); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("spinner", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.SpinnerEditor")); //$NON-NLS-1$ //$NON-NLS-2$		
		this.names
				.put("button", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.ButtonSelector")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("colorSelector", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.ColorSelectorWrapper")); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("form", "com.onpositive.semantic.ui.xml.FormEditorHandlerStruct"); //$NON-NLS-1$ //$NON-NLS-2$
		this.names
				.put("label", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.FormTextElement")); //$NON-NLS-1$//$NON-NLS-2$
		this.names
				.put("commit", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.Commit")); //$NON-NLS-1$//$NON-NLS-2$
		this.names
				.put("close-shell", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.CloseShell")); //$NON-NLS-1$//$NON-NLS-2$
		this.names
				.put("composite-action", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.CompositeAction")); //$NON-NLS-1$//$NON-NLS-2$
		this.names
				.put("composite-editor", new UIHandlerStruct(loader,"com.onpositive.semantic.model.ui.property.editors.CompositeEditor")); //$NON-NLS-1$//$NON-NLS-2$
		this.names
				.put("toolbar", new UIHandlerStruct(loader,"com.onpositive.commons.elements.ToolbarElement")); //$NON-NLS-1$//$NON-NLS-2$
		this.names.put("splitter",
				"com.onpositive.semantic.ui.xml.SashHandlerStruct");
		this.names.put("binded-action",
				"com.onpositive.semantic.ui.xml.BindedActionStruct");
	}

	HashMap<String, Object> names = new HashMap<String, Object>();

	@SuppressWarnings("rawtypes")
	public Object newUIElement(Class class1,
			String localName, Element element, Object parentContext) {
		Object object = names.get(localName);
		if (object instanceof String){
			try{
			object=(UIHandlerStruct) Class.forName(object.toString()).newInstance();
			}catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		UIHandlerStruct uiHandlerStruct = (UIHandlerStruct) object;
		if (uiHandlerStruct != null) {
			return  (uiHandlerStruct.newInstance(element,
					parentContext));
		}
		return null;
	}

}
