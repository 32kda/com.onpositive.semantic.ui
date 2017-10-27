package com.onpositive.ide.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeHyperlinkProvider;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;
import com.onpositive.ide.ui.bindings.BindingSchemeNode;
import com.onpositive.ide.ui.bindings.BindingSchemeTree;

public class BindingHyperlinkProvider implements ITypeHyperlinkProvider {

	public BindingHyperlinkProvider() {
		// TODO Auto-generated constructor stub
	}

	
	public IHyperlink[] calculateHyperlinks(String attributeName,
											DomainEditingModelObject findElement,
											final ITextViewer viewer,
											final int offset,
											String startString,
											final int lengthCompletion,
											String fullString,
											String typeSpecialization)
	{
		final IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor != null) {
			final IEditorInput editorInput = activeEditor.getEditorInput();
			IProject pr = null;
			if ((editorInput != null)
					&& (editorInput instanceof IFileEditorInput)) {
				final IFileEditorInput fl = (IFileEditorInput) editorInput;
				pr = fl.getFile().getProject();
				
				final IJavaProject jProject = JavaCore.create(pr) ;
				
				BindingSchemeTree tree = new BindingSchemeTree(findElement.getRoot(), jProject) ;
				tree.adjustTo(findElement);
				BindingSchemeNode scheme = tree.getScheme(fullString) ;
				
				if( scheme != null )
				{
					final IJavaElement javaElement = scheme.getJavaElement() ;
					if( javaElement != null ){
						if( javaElement != null )
							return new IHyperlink[]{
								new IHyperlink() {
									
									public void open() {
										try {
											JavaUI.openInEditor( javaElement );
										} catch (JavaModelException e) {
											e.printStackTrace();
										} catch (PartInitException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									
									public String getTypeLabel() {
										return javaElement.getElementName();
									}
									
									public String getHyperlinkText() {
										// TODO Auto-generated method stub
										return javaElement.getElementName();
									}
									
									public IRegion getHyperlinkRegion() {									
										return new Region(offset,lengthCompletion);
									}
								}									
							};							
					}
					else{
						final IDocumentElementNode documentElementNode = scheme.getDocumentElementNode() ;
						if( documentElementNode != null )
							return new IHyperlink[]{
								new IHyperlink() {
									
									public void open() {
											((SourceViewer)viewer).setSelection(new TextSelection( documentElementNode.getOffset(),documentElementNode.getLength()),true) ;
									}
									
									public String getTypeLabel() {
										return "sadasd";//javaElement.getElementName();
									}
									
									public String getHyperlinkText() {
										// TODO Auto-generated method stub
										return "sadasd";//javaElement.getElementName();
									}
									
									public IRegion getHyperlinkRegion() {									
										return new Region(offset,lengthCompletion);
									}
								}									
							};						
					}					
				}
				else{
					if( fullString.startsWith( "{") && fullString.endsWith( "}" ) && startString.length() > 0 )
					{
						return (new ExpressionHyperlinkProvider()).calculateHyperlinks(
								attributeName, findElement, viewer, offset+1,
								startString.substring(1), lengthCompletion-2,
								fullString.substring(1, fullString.length()-1), typeSpecialization) ;												
					}					
				}
			}
		}
		return null;
	}

}
