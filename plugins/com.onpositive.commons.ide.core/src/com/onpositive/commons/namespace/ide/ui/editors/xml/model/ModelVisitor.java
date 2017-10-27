package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

public interface ModelVisitor {

	void enterNode(DomainEditingModelObject domainEditingModelObject);

	void visitAttribute(DomainAttributeNode na);

	void exitNode(DomainEditingModelObject domainEditingModelObject);

}
