package com.onpositive.commons.namespace.ide.ui.editors.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.IncrementalHelper;
import org.eclipse.wst.validation.ReporterHelper;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IProjectValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;

import com.onpositive.commons.Activator;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainValidatorVisitor;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.IProblemReporter;

@SuppressWarnings("restriction")
public class DomainValidator extends AbstractNestedValidator implements IValidator, ISourceValidator {

	private IDocument document;

	public void cleanup(IReporter arg0) {
	}

	public void connect(IDocument arg0) {
		this.document = arg0;
	}

	public void disconnect(IDocument arg0) {
		this.document = null;
	}

	public synchronized void validate(IRegion arg0, IValidationContext arg1,
			final IReporter arg2) {
		IProject prj=null;
		if (arg1 instanceof IncrementalHelper){
			IncrementalHelper l=(IncrementalHelper) arg1;
			prj=l.getProject();
		}
		if (arg1 instanceof IProjectValidationContext){
			IProjectValidationContext p=(IProjectValidationContext) arg1;
			prj=p.getProject();
		}
		
		final DomainEditingModel ma = new DomainEditingModel(this.document, true);
		arg2.removeAllMessages(this);
		final IProblemReporter pb = new IProblemReporter() {

			public void accept(int severity, int start, int end, String message) {
				final org.eclipse.wst.validation.internal.core.Message message2 = new LocalizedMessage(
						IMessage.HIGH_SEVERITY, message);
				message2.setSeverity(IMessage.HIGH_SEVERITY);
				message2.setOffset(start);
				message2.setLength(end - start);
				try {
					message2.setLineNo(DomainValidator.this.document.getLineOfOffset(start));
				} catch (final BadLocationException e) {
					Activator.log(e);
				}
				arg2.addMessage(DomainValidator.this, message2);
			}

		};

		try {
			ma.load();
			if (!ma.isValid()) {
				return;
			}
			ma.getRoot().traverse(new DomainValidatorVisitor(pb, prj));
		} catch (final CoreException e) {
			throw new RuntimeException();
		}
	}

	
	public void validate(IValidationContext arg0, IReporter arg1)
			throws ValidationException {
		validate(null, arg0, arg1);
	}

	@Override
	public synchronized ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context) {
		if (inputstream==null){
			try {
				inputstream=new URI(uri).toURL().openStream();
			} catch (MalformedURLException e) {
			
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		BufferedReader r=new BufferedReader(new InputStreamReader(inputstream));
		try{
		StringBuilder bld=new StringBuilder();
		while (true){
			int read;
			try {
				read = r.read();
				if (read==-1){
					break;
				}
				bld.append((char)read);
			} catch (IOException e) {
				break;
			}
			
		}
		
		Document document2 = new Document(bld.toString());
		this.document=document2;
		try {
			ReporterHelper arg1 = new ReporterHelper(new NullProgressMonitor());
			
			IProject project = context instanceof IProjectValidationContext?((IProjectValidationContext) context).getProject():null;
			if (project==null){
				if (uri!=null){
					try {
						IFile[] findFilesForLocationURI = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(uri));
						if (findFilesForLocationURI!=null&&findFilesForLocationURI.length>0){
							project=findFilesForLocationURI[0].getProject();
						}
					} catch (URISyntaxException e) {
						
					}
				}
			}
			validate(new IncrementalHelper(document, project), arg1);
			List<IMessage> messages = arg1.getMessages();
			ValidationInfo info=new ValidationInfo(uri);
			for (IMessage m:messages){
				if (m.getSeverity()==IMessage.HIGH_SEVERITY){
					info.addError(m.getText(), m.getLineNumber()+1,m.getOffset(), 
							uri, m.getId(), m.getParams());
				}
				else{
					info.addWarning(m.getText(), m.getLineNumber()+1,m.getOffset(), 
							uri, m.getId(), m.getParams());
				}
			}
			return info;
		} catch (ValidationException e) {
			
		}
		}finally{
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
