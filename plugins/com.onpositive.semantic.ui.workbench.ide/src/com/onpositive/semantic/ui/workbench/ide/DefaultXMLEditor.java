package com.onpositive.semantic.ui.workbench.ide;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.semantic.model.api.wc.WorkingCopyAccess;
import com.onpositive.semantic.model.api.xml.SimpleXMLAccess;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.ui.roles.WidgetRegistry;
import com.onpositive.semantic.ui.workbench.elements.XMLEditorPart;

public class DefaultXMLEditor extends XMLEditorPart {

	protected Class<?>classToEdit;
	private Object workingCopy;
	
	
	public DefaultXMLEditor(Class<?> classToEdit) {
		super();
		this.classToEdit = classToEdit;
	}

	@Override
	protected String getUIDefinitionPath() {
		return null;
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		bnd.commit();
		String write = SimpleXMLAccess.write(bnd.getObject(), true);
		IFileEditorInput f = (IFileEditorInput) getEditorInput();
		try {
			f.getFile().setContents(new ByteArrayInputStream(write.getBytes("UTF-8")), true,true,monitor);
			//f.getFile().setCharset("UTF-8", monitor);
		} catch (UnsupportedEncodingException e) {
			
		} catch (CoreException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error",e.getMessage());
		}
		workingCopy=WorkingCopyAccess.getWorkingCopy(bnd.getObject());
		super.doSave(monitor);
		
		
	}
	
	@Override
	public boolean isDirty() {
		return super.isDirty();
	}

	@Override
	public void initObject(IEditorInput input) {
		IFileEditorInput f = (IFileEditorInput) input;
		try {
			IFile file = f.getFile();
			InputStream contents = file.getContents();
			String charset = f.getFile().getCharset();
			try {
				InputStreamReader rs = new InputStreamReader(contents, charset);
				Object read = SimpleXMLAccess.read(rs,
						classToEdit);
				if (read==null){
					try {
						read=classToEdit.newInstance();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				workingCopy = WorkingCopyAccess.getWorkingCopy(read);
				bnd = new DirtyListeningBinding(read);
				bnd.setWorkingCopiesEnabled(true);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} catch (CoreException e) {
			IFile file = f.getFile();
			try {
				file.refreshLocal(1, new NullProgressMonitor());
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		super.initObject(input);
	}

	protected AbstractUIElement<?> createRoot(final Binding bnd)
			throws Exception {
		return (AbstractUIElement<?>) WidgetRegistry.getInstance()
				.getWidgetObject(bnd.getObject(), null, null).createWidget(bnd);
	}
	
	@Override
	protected boolean recalcDirty() {
		return !WorkingCopyAccess.isSame(workingCopy, bnd.getObject());
	}
}
