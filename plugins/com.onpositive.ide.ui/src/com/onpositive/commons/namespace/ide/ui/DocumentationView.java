package com.onpositive.commons.namespace.ide.ui;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.commons.elements.Container;
import com.onpositive.semantic.language.model.ModelElement;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.FormEditor;
import com.onpositive.semantic.model.ui.property.editors.ICanInstallToolbar;
import com.onpositive.semantic.ui.workbench.elements.XMLView;

@SuppressWarnings("restriction")
public class DocumentationView extends XMLView {

	private FormEditor form;
	private ISelectionListener listener;

	public DocumentationView() {
		super("documentationView.xml");
	}

	public void dispose() {
		this.getSite().getPage().removeSelectionListener(this.listener);
		if (lastImage!=null){
			lastImage.dispose();			
		}
		super.dispose();
	}

	private ModelElement model;
	private Binding bnd;
	private IAdaptable location;

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		bnd = getBinding().binding("model");
		this.listener = new ISelectionListener() {

			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if (selection instanceof StructuredSelection) {
					final StructuredSelection sel = (StructuredSelection) selection;
					Object firstElement = sel.getFirstElement();
					if (firstElement instanceof ITreeNode<?>){
						firstElement=((ITreeNode<?>)firstElement).getElement();
					}
					if (firstElement instanceof ModelElement) {
						DocumentationView.this.model = (ModelElement) firstElement;
						bnd.setValue(DocumentationView.this.model, null);
						DocumentationView.this.form
								.setCaption("Documentation on "
										+ DocumentationView.this.model
												.getName());
						DocumentationView.this.form.getControl().setImage(
								SWTImageManager.getImage(
										DocumentationView.this.model, null,
										null));
						
						if (model != null) {
							File location2 = model.getDocumentationContribution().getLocation();
							if (location2==null){
								return;
							}
							IFile[] findFilesForLocation = ResourcesPlugin.getWorkspace()
									.getRoot().findFilesForLocation(
											new Path(location2.getAbsolutePath()));
							if (findFilesForLocation != null
									&& findFilesForLocation.length == 1) {
								location = findFilesForLocation[0];
							} else {
								location = null;
							}
						} else {
							location =null;
						}
						updateImage();
						return;
					} else {
						bnd.setValue(null, null);
					}
				}
				DocumentationView.this.setNull();
			}

		};
		this.getSite().getPage().addSelectionListener(this.listener);
		this.form = (FormEditor) ((Container) this.uiRoot);		
		this.form.getControl().getBody().setLayout(new FillLayout());
//		((RichTextViewer) ((RichTextEditorWrapper)getElement("richtext")).getSourceViewer()).addRichDocumentListener(
//			new IRichDocumentListener()
//			{
//
//				
//				
//				
//				@SuppressWarnings("unchecked")
//				public void documentChanged(DocumentEvent event,
//						RichDocumentChange change)
//				{
//					for (Iterator iterator = change.getDelta().getAdded().iterator(); iterator
//							.hasNext();)
//					{
//						BasePartition partition= (BasePartition) iterator.next();
//						if (partition instanceof ImagePartition)
//						{
//							String cachedFileName = CachingTools.saveImgToDisk("E:\\tmp\\photo",(ImagePartition) partition);
//							((ImagePartition)partition).setImageFileName(cachedFileName);
//						}
//					} 
//				}
//
//				public void documentAboutToBeChanged(
//						com.onpositive.richtext.model.meta.DocumentEvent event) {
//					
//				}
//
//				public void documentChanged(
//						com.onpositive.richtext.model.meta.DocumentEvent event,
//						RichDocumentChange change) {
//					
//				}
//			
//			}
//		);
		// this.form.setBinding(bnd);
		DisposeBindingListener.linkBindingLifeCycle(bnd, this.form);		
	}
	

	
	
	public void browse() {
		if (location==null){
			return;
		}
		final IProject project = ((IResource) location).getProject();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				Display.getCurrent().getActiveShell(),
				new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setInput(project);
		Object lpath = getBinding().getBinding("model.DocumentationContribution.Icon").getValue();
		IResource resource = null;
		try{
		resource=(lpath!=null&&lpath.toString().length()>0)?project.getFile(new Path((String) lpath)):null;
		}catch (Exception e) {
		}
		if (resource != null)
			dialog.setInitialSelection(resource);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IProject)
					return ((IProject) element).equals(project);
				return true;
			}
		});
		dialog.setAllowMultiple(false);
		dialog.setTitle(PDEUIMessages.ResourceAttributeCellEditor_title);
		dialog.setMessage(PDEUIMessages.ResourceAttributeCellEditor_message);
		dialog.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection != null
						&& selection.length > 0
						&& (selection[0] instanceof IFile || selection[0] instanceof IContainer))
					return new Status(IStatus.OK, PDEPlugin.getPluginId(),
							IStatus.OK, "", null); //$NON-NLS-1$

				return new Status(IStatus.ERROR, PDEPlugin.getPluginId(),
						IStatus.ERROR, "", null); //$NON-NLS-1$
			}
		});
		if (dialog.open() == Window.OK) {
			IResource res = (IResource) dialog.getFirstResult();
			IPath path = res.getProjectRelativePath();
			if (res instanceof IContainer)
				path = path.addTrailingSeparator();
			String value = path.toString();
			getBinding().binding("model.DocumentationContribution.Icon").setValue(value,null);
			updateImage();
		}
	}

	String lastImagePath;
	Image lastImage;
	
	private void updateImage() {
		if (location==null){
			return;
		}
		final IProject project = ((IResource) location).getProject();		
		Object lpath = getBinding().getBinding("model.DocumentationContribution.Icon").getValue();
		
		
		IResource resource = null;
		try{
		resource=(lpath!=null&&lpath.toString().length()>0)?project.getFile(new Path((String) lpath)):null;
		}catch (Exception e) {
		}
		if (resource != null){
			if (!resource.getFullPath().toOSString().equals(lastImagePath)){
				lastImagePath=resource.getFullPath().toOSString();
				if (lastImage!=null){
					lastImage.dispose();
				}
				try{
					lastImage=new Image(Display.getCurrent(),((IFile)resource).getContents(true));
					form.getControl().setImage(lastImage);
				}
				catch (Exception e) {
				}
			}
			return;
		}
		form.getControl().setImage(SWTImageManager.getImage(model, null, null));
		if (lastImage!=null){
			lastImage.dispose();			
		}
		lastImagePath=null;
		lastImage=null;
	}

	protected void setNull() {
		this.form.setCaption("Documentation");
		this.form.getControl().setImage(null);
	}
}
