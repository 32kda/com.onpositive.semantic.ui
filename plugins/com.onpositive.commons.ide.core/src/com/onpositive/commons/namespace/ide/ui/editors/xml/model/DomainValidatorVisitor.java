package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeValidatorDetailed.ErrorInfo;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.DocumentTextNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentRange;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;

public class DomainValidatorVisitor implements ModelVisitor {

	private final IProblemReporter reporter;
	IProject project;

	public DomainValidatorVisitor(IProblemReporter reporter, IProject project) {
		super();
		this.reporter = reporter;
		this.project = project;
	}

	public void enterNode(DomainEditingModelObject domainEditingModelObject) {
		final String namespace = domainEditingModelObject.getNamespace() ;
		final String tag = domainEditingModelObject.getLocalName() ;

		final NamespacesModel instance = NamespacesModel.getInstance();
		final NamespaceModel resolveNamespace = instance
				.resolveNamespace(namespace);
		if (resolveNamespace == null) {
			this.reportUnknown(domainEditingModelObject, "Namespace "
					+ namespace + " is unknown");
			return;
		}
		final ElementModel resolveElement = resolveNamespace
				.resolveElement(tag);
		if (resolveElement == null) {
			this.reportUnknown(domainEditingModelObject, "Element " + tag
					+ "is not a member of namespace " + namespace);
			return;
		} else {
			final DomainEditingModelObject parentNode = (DomainEditingModelObject) domainEditingModelObject
					.getParentNode();
			if (parentNode != null) {
				final ElementModel ma = instance.resolveElement(
						parentNode.getNamespace(), parentNode.getLocalName());
				if (ma != null) {
					if (!ma.isAllowedChild(resolveElement)) {
						this.report(IStatus.WARNING, domainEditingModelObject,
								resolveElement.getName()
										+ " is not allowed child of element "
										+ ma.getName());
					}
				}
			}

			final IDocumentAttributeNode[] nodeAttributes = domainEditingModelObject
					.getNodeAttributes();
			final HashSet<AttributeModel> allProperties = resolveElement
					.getAllProperties();
			HashSet<AttributeModel> restrictedAttributes = resolveElement
					.getRestrictedAttributes();
			final HashSet<String> required = new HashSet<String>();
			for (final AttributeModel m : allProperties) {
				if (m.isRequired()) {
					required.add(m.getName());
				}
			}
			for (final IDocumentAttributeNode currentAttribute : nodeAttributes) {
				final DomainAttributeNode md = (DomainAttributeNode) currentAttribute;
				final String nm = md.getAttributeName();
				
				required.remove(nm);
				final String localName = md.getLocalName();
				if (nm.equals("xmlns") || nm.startsWith("xmlns:")) {
					continue;
				}

				AttributeModel f = null;
				for (final AttributeModel m : allProperties) {
					if (m.getName().equals(localName)) {
						f = m;
						break;
					}
				}
				if (restrictedAttributes.contains(f)) {
					this.reportUnknown((IDocumentRange) currentAttribute,
							"Usage of attribute " + localName
									+ " is restricted for " + tag);
				}
				if (f == null) {
					this.reportUnknown((IDocumentRange) currentAttribute,
							"Attribute " + localName
									+ " is not defined as a valid property of "
									+ tag);
				} else {
					String str = null;
					ITypeValidator validator = TypeValidatorRegistry
							.getTypeValidator(f.getType());
					
					if (validator != null){
						if( validator instanceof ITypeValidatorDetailed ){							

							ErrorInfo[] errorsArray = ((ITypeValidatorDetailed)validator).getErrors(project,
									  currentAttribute.getAttributeValue(),
									  domainEditingModelObject, f.getTypeSpecialization() );

							if( errorsArray != null && errorsArray.length > 0 )
							{
								int rangeOffset = currentAttribute.getValueOffset() ;
								DocumentTextNode errorNode = new DocumentTextNode() ; 
								for( ErrorInfo ei : errorsArray  )
								{
									if( ei == null || ei.getMessage() == null )	continue ;
									
									errorNode.setOffset( ei.getOffset() + rangeOffset ) ;
									errorNode.setLength( ei.getLength() ) ;
									this.reportUnknown( (IDocumentRange)errorNode, ei.getMessage() );								
								}
							}
						}
						else{
							str = validator.validate(project,
									 currentAttribute.getAttributeValue(),
									 domainEditingModelObject, f.getTypeSpecialization() );
		
							if (str != null)
								this.reportUnknown((IDocumentRange) currentAttribute,
										str);
						}
						
							
					}

				}
			}
			if (!required.isEmpty()) {
				final ArrayList<String> missedRequired = new ArrayList<String>(
						required);
				Collections.sort(missedRequired);
				for (final String s : missedRequired) {
					this.reportUnknown(domainEditingModelObject,
							"Required attribute " + s + " are not defined in "
									+ tag);
				}
			}
		}
	}

	private void reportUnknown(IDocumentRange range, String name) {
		this.reporter.accept(IStatus.ERROR, range.getOffset(),
				range.getOffset() + range.getLength(), name);
	}

	public void exitNode(DomainEditingModelObject domainEditingModelObject) {

	}

	public void visitAttribute(DomainAttributeNode na) {
	}

	void report(int severity, IDocumentRange range, String message) {
		this.reporter.accept(severity, range.getOffset(), range.getOffset()
				+ range.getLength(), message);
	}
}
