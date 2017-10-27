package com.onpositive.semantic.model.ui.property.editors.structured.celleditor;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import com.onpositive.commons.PrettyFormat;
import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.commons.ui.dialogs.ElementDialog;
import com.onpositive.semantic.model.api.property.IProperty;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.ui.property.editors.DateTimeEditor;

public class DateCellEditor extends TextCellEditorWithButton{

	public DateCellEditor(Composite control, Object parent, IProperty property) {
		super(control, parent, property);
	}
	
	protected void installContentAssist(Text text){
		Realm<String> realm = new Realm<String>("Today","Tomorrow","This week","This month");
		GregorianCalendar m=new GregorianCalendar();
		Map<String, Integer> displayNames = m.getDisplayNames(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.LONG,Locale.getDefault());
		for (String s:displayNames.keySet()){
			realm.add(s);
		}
		bnd.setRealm(realm);
		
		super.installContentAssist(text);
	}
	
	@Override
	protected void onButton() {
		Container m=new Container();
		//m.add(new ButtonSelector());
		m.setLayoutManager(new OneElementOnLineLayouter());
		final DateTimeEditor d=new DateTimeEditor();
		d.setBinding(getBinding());
		d.setAllowClear(false);
		d.getLayoutHints().setGridy(true);
		m.add(d);
		
		d.setStyle(SWT.CALENDAR);
		final ElementDialog dlg=new ElementDialog(getBinding(), m, "Choose date", "Choose date value");
		dlg.setCloseOnMouseOut(true);
		SWTEventListener<DateTime> eventListener = new SWTEventListener<DateTime>() {
			
			public void handleEvent(AbstractUIElement<DateTime> element, Event event) {
				doSetValue(d.getValue());
				dlg.close();				
			}
		};
		d.addListener(SWT.MouseDoubleClick, eventListener);
		//d.addListener(SWT.Selection, eventListener);
		//d.addListener(SWT.DefaultSelection,eventListener);
		dlg.open();
		super.onButton();
	}

	@Override
	protected void doSetValue(Object value) {
		if (value instanceof Date){
			bnd.setAdapter(ITextLabelProvider.class,new ITextLabelProvider() {
				
				public String getText(Object object) {
					return DateFormat.getDateTimeInstance().format(object);
				}
				
				public String getDescription(Object object) {
					return null;
				}
			});
			
			super.doSetValue(value);
		}
		else{
			super.doSetValue(value);
		}
	}
	
	@Override
	protected Object doGetValue1() {
		try{
		Date parse = DateFormat.getDateInstance(DateFormat.MEDIUM).parse(text.getText());
		return parse;
		}catch (Exception e) {
		}
		String tl = text.getText();
		Date dd=convert(tl);
		if (dd!=null){
			return dd;
		}
		if (tl.trim().length()==0){
			return null;
		}
		return initValue;
	}

	public static Date convert(String tl) {		
		return PrettyFormat.parseDate(tl);
	}
}
