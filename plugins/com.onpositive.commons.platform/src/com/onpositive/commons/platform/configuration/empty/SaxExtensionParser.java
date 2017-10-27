package com.onpositive.commons.platform.configuration.empty;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.onpositive.core.runtime.IConfigurationElement;

public class SaxExtensionParser extends DefaultHandler{

	private HashMap<String, ArrayList<IConfigurationElement>> map;

	public SaxExtensionParser(
			HashMap<String, ArrayList<IConfigurationElement>> elements) {
		this.map=elements;
	}
	String cContributor;
	String cExtension;
	ConfigurationElement currentElement;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("plugin")){
			cContributor="";
			return;
		}
		if (qName.equals("extension")){
			cExtension=attributes.getValue("point");
			return;
		}
		ConfigurationElement el=new ConfigurationElement(cExtension, currentElement);
		el.setName(qName);
		if (currentElement!=null){
			currentElement.children.add(el);			
		}
		else{
			ArrayList<IConfigurationElement> arrayList = map.get(cExtension);
			if (arrayList==null){
				arrayList=new ArrayList<IConfigurationElement>();
				map.put(cExtension, arrayList);
			}
			arrayList.add(el);
		}
		this.currentElement=el;
		int length = attributes.getLength();
		for (int a=0;a<length;a++){
			String localName2 = attributes.getLocalName(a);
			String value = attributes.getValue(a);
			el.add(localName2,value);
		}
		super.startElement(uri, localName, qName, attributes);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("plugin")){
			return;
		}
		if (localName.equals("extension")){
			return;
		}
		if (currentElement!=null){
			currentElement=currentElement.parent;
		}
		super.endElement(uri, localName, qName);
	}
	
}
