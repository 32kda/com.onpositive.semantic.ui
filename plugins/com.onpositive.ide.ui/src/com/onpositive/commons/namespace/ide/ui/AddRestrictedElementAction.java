package com.onpositive.commons.namespace.ide.ui;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.HorizontalLayouter;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.ElementDialog;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.model.api.realm.Realm;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.actions.IBindedActionDelegate;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class AddRestrictedElementAction implements IBindedActionDelegate{

	
	public AddRestrictedElementAction(){
		
	}
	
	public void run(IPropertyEditor<?> bindind) {
		IBinding binding = bindind.getBinding();
		ElementModel mdl=(ElementModel) binding.getObject();
		final AbstractEnumeratedValueSelector vl= (AbstractEnumeratedValueSelector) bindind.getUIElement();
		HashSet<ElementModel> superElements = mdl.getSuperElements();
		HashSet<AttributeModel>ml=new HashSet<AttributeModel>();
		for (ElementModel m:superElements){
			ml.addAll(m.getProperties());
		}
		ml.removeAll(vl.getCurrentValue());
		Binding bs=new Binding("");
		bs.setName("Attributes to restrict");
		bs.setDescription("Check attributes which can not be used in current context");
		bs.setRealm(new Realm<AttributeModel>(ml));
		Container c=new Container();
		c.setLayoutData(GridDataFactory.fillDefaults().hint(400,200).create());
		c.setLayoutManager(new OneElementOnLineLayouter());
		final ListEnumeratedValueSelector<AttributeModel>sel=new ListEnumeratedValueSelector<AttributeModel>();
		sel.setAsCheckBox(true);
		Container buttons=new Container();
		ButtonSelector element = new ButtonSelector();
		buttons.add(element);
		buttons.setLayoutManager(new HorizontalLayouter());
		element.setCaption("Ok");
		ButtonSelector element1 = new ButtonSelector();
		element1.setCaption("Cancel");
		buttons.add(element1);
		sel.setBinding(bs);
		//sel.getLayoutHints().setHint(new Point(200,200));
		c.add(sel);
		c.add(buttons);
		final ElementDialog dl=new ElementDialog(bs,c,bs.getName(),bs.getDescription());
		element1.addListener(SWT.Selection, new SWTEventListener<Button>() {
			
			
			public void handleEvent(AbstractUIElement<Button> element, Event event) {
				dl.close();
			}
		});
		element.addListener(SWT.Selection, new SWTEventListener<Button>() {
			
			
			public void handleEvent(AbstractUIElement<Button> element, Event event) {
				Collection<Object> currentValue = sel.getCurrentValue();
				vl.addValues(currentValue);
				dl.close();
			}
		});
		dl.open();
	}

	
	public boolean isEnabled(Object value) {
		return true;
	}

}
