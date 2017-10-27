package com.onpositive.ide.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainAttributeNode;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.HyperlinkProviderRegistry;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeHyperlinkProvider;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ModelVisitor;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;

public class DLFHyperlinkDetector extends AbstractHyperlinkDetector {

	public DLFHyperlinkDetector() {		
	}

	
	public IHyperlink[] detectHyperlinks(final ITextViewer textViewer,
			final IRegion region, boolean canShowMultipleHyperlinks) {
		final ArrayList<IHyperlink>values=new ArrayList<IHyperlink>();
		DomainEditingModel dm = new DomainEditingModel(textViewer.getDocument(), false);
		try {
			dm.load();
		} catch (CoreException e) {			
		}
		if (dm.isValid()){
			dm.getRoot().traverse(new ModelVisitor() {
				
				public void visitAttribute(DomainAttributeNode na) {
					int start=na.getValueOffset();
					int length=na.getValueLength();
					if (region.getOffset()>=start&&region.getOffset()+region.getLength()<=start+length){
						NamespaceModel resolveNamespace;
						DomainEditingModelObject parentNode = (DomainEditingModelObject)na.getEnclosingElement();
						final String namespace = parentNode.getNamespace();
						resolveNamespace = NamespacesModel.getInstance()
								.resolveNamespace(namespace);

						ElementModel parentElement = null;

						parentElement = resolveNamespace != null ? resolveNamespace
								.resolveElement(parentNode.getLocalName()) : null;
						if (parentElement != null) {
							final HashSet<AttributeModel> allProperties = parentElement
									.getAllProperties();
							for (final AttributeModel m : allProperties) {
								if (m.getName().equals(na.getAttributeName())) {
									final String type = m.getType();
									final ITypeHyperlinkProvider completionProvider = HyperlinkProviderRegistry.getCompletionProvider(type);									
									if (completionProvider != null) 
									{
										IHyperlink[] calculateHyperlinks = completionProvider.calculateHyperlinks(
												na.getAttributeName(), parentNode,textViewer,
												 start, na.getAttributeValue().substring(0, region.getOffset()-start),
												length, na.getAttributeValue(), m.getTypeSpecialization());
										if (calculateHyperlinks!=null){
										values.addAll(Arrays.asList(calculateHyperlinks));
										}
									}
								}
							}
						}
					}
				}
				
				public void exitNode(DomainEditingModelObject domainEditingModelObject) {
					
				}
				
				public void enterNode(DomainEditingModelObject domainEditingModelObject) {
					
				}
			});
		}
		if (!values.isEmpty()){
			return values.toArray(new IHyperlink[values.size()]);
		}
		return null;
	}

}
