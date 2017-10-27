package com.onpositive.semantic.model.ui.property.editors;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.PrettyFormat;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.ElementDialog;
import com.onpositive.semantic.model.api.property.DefaultPropertyMetadata;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.realm.IConverter;
import com.onpositive.semantic.model.realm.IFactory;
import com.onpositive.semantic.model.realm.Realm;

public class EditorFactory {

	
	public static AbstractUIElement<?>createEditor(Binding b,IProperty p){
		return getEditor(b,p);		
	}
	
	protected static OneLineTextElement<String> getEditor(
			final Binding b, IProperty p) {
		final IProperty property2=p;
		DefaultPropertyMetadata propertyMetaData2=(DefaultPropertyMetadata) property2.getPropertyMetaData(); 
		Object object = propertyMetaData2.get(DefaultPropertyMetadata.CONTENT_TYPE_MODIFIER);
		if ((((Class)property2.getSubjectClass())==Date.class)||(object!=null&&object.equals(DefaultPropertyMetadata.CONTENT_TYPE_VALUE_DATE))){
			Realm<String> realm = new Realm<String>("Today","Tomorrow","This week","This month");
			b.setConverter(new IConverter<Object, Object>() {

				public Object from(Object source) {
					return source;
				}

				public Object to(Object source) {
					return source;
				}
			});
			GregorianCalendar m=new GregorianCalendar();
			Map<String, Integer> displayNames = m.getDisplayNames(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG,Locale.getDefault());
			for (String s:displayNames.keySet()){
				realm.add(s);
			}
			b.setRealm(realm);
			b.setAdapter(ITextLabelProvider.class, new ITextLabelProvider() {
				
				public String getText(Object object) {
					return PrettyFormat.format(object, true);
				}
				
				public String getDescription(Object object) {
					return null;
				}
			});
			b.setFactory(new IFactory() {
				
				public Object getValue(Object context) {
					Container m=new Container();
					//m.add(new ButtonSelector());
					m.setLayoutManager(new OneElementOnLineLayouter());
					final DateTimeEditor d=new DateTimeEditor();
					d.setBinding(b);
					d.setAllowClear(false);
					d.getLayoutHints().setGridy(true);
					m.add(d);
					
					d.setStyle(SWT.CALENDAR);
					final ElementDialog dlg=new ElementDialog(b, m, "Choose date", "Choose date value");
					dlg.setCloseOnMouseOut(true);
					SWTEventListener<DateTime> eventListener = new SWTEventListener<DateTime>() {
						
						public void handleEvent(AbstractUIElement<DateTime> element, Event event) {
							b.setValue(d.getValue(), null);
							dlg.close();				
						}
					};
					d.addListener(SWT.MouseDoubleClick, eventListener);
					//d.addListener(SWT.Selection, eventListener);
					//d.addListener(SWT.DefaultSelection,eventListener);
					dlg.open();
					return null;
				}
				
				public String getName() {
					return "...";
				}
				
				public String getDescription() {
					return "";
				}
			});
		}
		OneLineTextElement<String> editor = new OneLineTextElement<String>(b){
			
			
			@Override
			protected void doCommit(IBinding binding2) {
				String text2 = getText();
				if (text2.trim().length()==0){
					commitToBinding(null);
					return;
				}
				Date parseDate = PrettyFormat.parseDate(text2);
				commitToBinding(parseDate);
			}
		};
		return editor;
	}
}
