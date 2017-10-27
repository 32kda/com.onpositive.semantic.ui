package com.onpositive.commons.platform.configuration.empty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.onpositive.core.runtime.IConfigurationElement;
import com.onpositive.core.runtime.IExtensionRegistry;

public class ManualExtensionRegistry implements IExtensionRegistry {

	private static final IConfigurationElement[] I_CONFIGURATION_ELEMENTS = new IConfigurationElement[0];
	protected HashMap<String, ArrayList<IConfigurationElement>> elements = new HashMap<String, ArrayList<IConfigurationElement>>();

	
	public IConfigurationElement[] getConfigurationElementsFor(String point) {
		ArrayList<IConfigurationElement> arrayList = elements.get(point);
		if (arrayList != null) {
			return arrayList
					.toArray(new IConfigurationElement[arrayList.size()]);
		}
		return I_CONFIGURATION_ELEMENTS;
	}

	SAXParser parser;

	public void appendDocument(InputStream strem) {
		if (parser == null) {
			try {
				parser = SAXParserFactory.newInstance().newSAXParser();
			} catch (Exception e) {
				throw new IllegalStateException();
			}
		}
		try {
			parser.parse(strem, new SaxExtensionParser(elements));
		} catch (SAXException e) {
			throw new IllegalStateException();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}
}
