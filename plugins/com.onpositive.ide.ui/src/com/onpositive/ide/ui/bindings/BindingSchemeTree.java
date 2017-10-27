package com.onpositive.ide.ui.bindings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.jdt.core.IJavaProject;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;

public class BindingSchemeTree {
	
	private static final int MAX_CACHE_SIZE = 20;

	protected static HashMap<DomainEditingModelObject, BindingSchemeNode> rootNodes = new HashMap<DomainEditingModelObject, BindingSchemeNode>();

	BindingSchemeNode rootNode;

	public BindingSchemeTree(DomainEditingModelObject element,
			IJavaProject project) {
		rootNode = rootNodes.get(element);
		if (rootNode == null) {
			rootNode = new BindingSchemeNode(element, project, null);
			rootNode.init();
			BindingSchemeNode modelNode = rootNode.getNode("model");
			if (modelNode != null) {
				try {
					modelNode.init();
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
				rootNode.deleteChild(modelNode);
				rootNode.merge(modelNode);
			}
			if (rootNodes.size() > MAX_CACHE_SIZE)
				rootNodes.clear();
			rootNodes.put(element, rootNode);
		}
	}

	public BindingSchemeNode getParentScheme(String string) {

		return rootNode.getParentScheme(string);
	}

	public BindingSchemeNode getScheme(String string) {

		return rootNode.getParentScheme(string + '.');
	}

	public void adjustTo(DomainEditingModelObject element) {
		IDocumentElementNode parentNode = element.getParentNode();
		ArrayList<String> bld = new ArrayList<String>();
		while (parentNode != null) {
			String xmlAttributeValue = parentNode
					.getXMLAttributeValue("bindTo");
			if (xmlAttributeValue != null && xmlAttributeValue.length() > 0) {
				if (!xmlAttributeValue.equals("this")) {
					bld.add(xmlAttributeValue);

				}
			}
			parentNode = parentNode.getParentNode();
		}
		if (bld.size() > 0) {
			Collections.reverse(bld);
			StringBuilder dd = new StringBuilder();
			for (String s : bld) {
				dd.append(s);
				dd.append('.');
			}
			dd.deleteCharAt(dd.length() - 1);
			BindingSchemeNode parentScheme = getScheme(dd.toString());
			if (parentScheme != null) {
				rootNode = parentScheme;
			}
		}
	}

}
