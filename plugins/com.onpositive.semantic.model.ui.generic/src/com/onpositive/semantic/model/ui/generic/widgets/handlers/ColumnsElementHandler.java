package com.onpositive.semantic.model.ui.generic.widgets.handlers;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.xml.language.Context;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.commons.xml.language.GeneralElementHandler;
import com.onpositive.commons.xml.language.IElementHandler;
import com.onpositive.core.runtime.Bundle;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.ColumnLayoutData;
import com.onpositive.semantic.model.ui.generic.widgets.ITableElement;

public class ColumnsElementHandler implements IElementHandler{

	public ColumnsElementHandler() {
	}

	public Object handleElement(Element element, Object parentContext,
			Context context) {
		final ITableElement<?> pContext = (ITableElement<?>) parentContext;
		final NodeList nlist = element.getChildNodes();
		final int length = nlist.getLength();
		final ArrayList<Column> columns = new ArrayList<Column>();
		for (int a = 0; a < length; a++) {
			final Node n = nlist.item(a);
			if (n instanceof Element) {
				final Element el = (Element) n;
				if (el.getLocalName().equals("column")) {
					final Column column = (Column) DOMEvaluator.getInstance().evaluate( el, null, context );
					String attribute = el.getAttribute("initialWidth");
					int initialWidth = 0;
					int growth = 1;
					if (attribute.length() > 0) {
						initialWidth = Integer.parseInt(attribute);
					}
					attribute = el.getAttribute("resizeWeight");
					if (attribute.length() > 0) {
						growth = Integer.parseInt(attribute);
					}
					attribute = el.getAttribute("cellEditorFactory");
					if (attribute.length() > 0) {
						final Object factory = context.newInstance(attribute);
						column.setCellEditorFactory(factory);
					}
					if (growth == 0) {
						column.setLayoutData(new ColumnLayoutData(initialWidth,
								false));
					}
					column.setLayoutData(new ColumnLayoutData(growth, initialWidth,
							true));
					columns.add(column);
				}
			}
		}
		pContext.setColumns(columns);
		return null;
	}

}
