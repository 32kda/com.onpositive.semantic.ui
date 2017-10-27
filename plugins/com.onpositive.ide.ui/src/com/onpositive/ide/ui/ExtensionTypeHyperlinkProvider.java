package com.onpositive.ide.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDEExtensionRegistry;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainAttributeNode;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeHyperlinkProvider;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ModelVisitor;

public class ExtensionTypeHyperlinkProvider implements ITypeHyperlinkProvider {

	public ExtensionTypeHyperlinkProvider() {
		// TODO Auto-generated constructor stub
	}

	public IHyperlink[] calculateHyperlinks(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			final int offset, final String startString, final int lengthCompletion,
			final String fullString, String typeSpec) {
		final ArrayList<IHyperlink> values = new ArrayList<IHyperlink>();
		if (typeSpec != null && typeSpec.indexOf('/') != -1) {
			String trim = typeSpec.trim();
			int p = trim.indexOf('/');
			String extension = trim.substring(0, p);
			String elem = trim.substring(p + 1);

			PDEExtensionRegistry extensionsRegistry = PDECore.getDefault()
					.getExtensionsRegistry();
			IExtension[] findExtensions = extensionsRegistry.findExtensions(
					extension, false);
			for (IExtension e : findExtensions) {
				IConfigurationElement[] configurationElements = e
						.getConfigurationElements();
				for (IConfigurationElement el : configurationElements) {
					String attribute = el.getAttribute("id");
					if (el.getName().equals(elem)) {
						if (attribute != null && attribute.equals(startString)) {
							IPluginModelBase dd = PDECore.getDefault()
									.getModelManager()
									.findModel(el.getContributor().getName());
							IResource underlyingResource = dd
									.getUnderlyingResource();
							if (underlyingResource != null) {
								IProject pr = (IProject) underlyingResource.getProject();
								final IFile file = pr.getFile("plugin.xml");
								if (file.exists()) {
									
									InputStream contents;
									try {
										contents = file.getContents();
										BufferedReader b=new BufferedReader(new InputStreamReader(contents));
										StringBuilder bld=new StringBuilder();
										while (true){
											try{
											int z=b.read();
											if (z==-1){
												break;
											}
											bld.append((char)z);
											}catch (Exception ex) {
												try {
													b.close();
												} catch (IOException e1) {
													e1.printStackTrace();
												}
												break;
											}
										}
										DomainEditingModel m = new DomainEditingModel(
												new Document(bld.toString()), true);
										try {
											m.reload(m.getDocument());
											//m.load(contents, true);
											if (m.isValid()) {
												m.getRoot().traverse(
														new ModelVisitor() {

															public void visitAttribute(
																	final DomainAttributeNode na) {
																if (na.getAttributeName()
																		.equals("id")) {
																	if (na.getAttributeValue()
																			.equals(startString)) {
																		values.add(new IHyperlink() {

																			public void open() {
																				FileEditorInput fl=new FileEditorInput(file);
																				IEditorDescriptor findEditor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor("plugin.xml");
																				try {
																					IEditorPart d = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(fl, findEditor.getId());
//																					if (d instanceof ManifestEditor){
//																						ManifestEditor te=(ManifestEditor) d;
//																						
//																					}
																				} catch (PartInitException e) {
																					// TODO Auto-generated catch block
																					e.printStackTrace();
																				}
																			}

																			
																			public String getTypeLabel() {
																				return fullString;
																			}

																			
																			public String getHyperlinkText() {
																				
																				return fullString;
																			}

																			
																			public IRegion getHyperlinkRegion() {
																				return new Region(offset,lengthCompletion);
																			}
																		});
																	}
																}
															}
															public void exitNode(
																	DomainEditingModelObject domainEditingModelObject) {

															}

															public void enterNode(
																	DomainEditingModelObject domainEditingModelObject) {
																// TODO
																// Auto-generated
																// method stub

															}
														});
											}
										} finally {
											try {
												contents.close();
											} catch (IOException e1) {
												// TODO Auto-generated catch
												// block
												e1.printStackTrace();
											}
										}
									} catch (CoreException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}

							}
						}
					}
				}
			}
		}
		if (!values.isEmpty()) {
			return values.toArray(new IHyperlink[values.size()]);
		}
		return null;
	}

}
