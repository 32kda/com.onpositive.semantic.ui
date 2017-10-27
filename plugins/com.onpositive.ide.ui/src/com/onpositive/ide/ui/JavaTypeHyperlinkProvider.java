package com.onpositive.ide.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeHyperlinkProvider;

public class JavaTypeHyperlinkProvider implements ITypeHyperlinkProvider {

	public JavaTypeHyperlinkProvider() {
		// TODO Auto-generated constructor stub
	}

	
	public IHyperlink[] calculateHyperlinks(String attributeName,
			DomainEditingModelObject findElement, ITextViewer viewer,
			final int offset, String startString, final int lengthCompletion,
			String fullString, String typeSpecialization) {
		final IEditorPart activeEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (activeEditor != null) {
			final IEditorInput editorInput = activeEditor.getEditorInput();
			IProject pr = null;
			if ((editorInput != null)
					&& (editorInput instanceof IFileEditorInput)) {
				final IFileEditorInput fl = (IFileEditorInput) editorInput;
				pr = fl.getFile().getProject();
				try {
					final IType findType = JavaCore.create(pr).findType(fullString);
					if (findType!=null){
						return new IHyperlink[]{
							new IHyperlink() {
								
								public void open() {
									try {
										JavaUI.openInEditor(findType);
									} catch (PartInitException e) {
										e.printStackTrace();
									} catch (JavaModelException e) {
										e.printStackTrace();
									}
								}
								
								public String getTypeLabel() {
									return findType.getFullyQualifiedName();
								}
								
								public String getHyperlinkText() {
									// TODO Auto-generated method stub
									return findType.getFullyQualifiedName();
								}
								
								public IRegion getHyperlinkRegion() {									
									return new Region(offset,lengthCompletion);
								}
							}
								
						};
					}
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
