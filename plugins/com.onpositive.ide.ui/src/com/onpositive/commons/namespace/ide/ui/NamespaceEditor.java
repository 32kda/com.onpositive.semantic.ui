package com.onpositive.commons.namespace.ide.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.RootElement;
import com.onpositive.commons.elements.SWTEventListener;
import com.onpositive.commons.namespace.ide.ui.completion.JavaContentAssistConfiguration;
import com.onpositive.commons.xml.language.DOMEvaluator;
import com.onpositive.semantic.language.model.DocumentationContributionModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.IEditorWithNamespace;
import com.onpositive.semantic.language.model.IResourceLoader;
import com.onpositive.semantic.language.model.NameSpaceContributionModel;
import com.onpositive.semantic.language.model.NamespacesModel;
import com.onpositive.semantic.language.model.ProjectContibutionModel;
import com.onpositive.semantic.model.api.changes.IValueListener;
import com.onpositive.semantic.model.api.status.CodeAndMessage;
import com.onpositive.semantic.model.api.validation.IValidationContext;
import com.onpositive.semantic.model.api.validation.ValidatorAdapter;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.binding.IBinding;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;
import com.onpositive.semantic.model.ui.generic.widgets.IPropertyEditor;
import com.onpositive.semantic.model.ui.property.editors.CompositeEditor;
import com.onpositive.semantic.model.ui.property.editors.IAllowsRegisterCommands;
import com.onpositive.semantic.model.ui.property.editors.structured.AbstractEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.columns.EditableListEnumeratedValueSelector;

public class NamespaceEditor extends EditorPart implements IEditorWithNamespace{

	private StructuredSelection selection;

	private static final DocumentBuilder DOCUMENT_BUILDER;

	static {
		try {
			DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			DOCUMENT_BUILDER.setErrorHandler(new ErrorHandler() {

				public void error(SAXParseException exception)
						throws SAXException {

				}

				public void fatalError(SAXParseException exception)
						throws SAXException {

				}

				public void warning(SAXParseException exception)
						throws SAXException {

				}

			});
		} catch (final ParserConfigurationException e) {
			throw new LinkageError();
		}
	}

	private NameSpaceContributionModel model;

	public NamespaceEditor() {

	}

	@Override
	public void dispose() {
		super.dispose();
		NamespacesModel.getInstance().removeModelListener(project,
				projectModelListener);
	}

	public void doSave(IProgressMonitor monitor) {
		final Document dl = DOCUMENT_BUILDER.newDocument();
		final Element createElement = dl.createElement("namespace");
		this.model.store(createElement);
		dl.appendChild(createElement);

		try {
			final IFileEditorInput fl = (IFileEditorInput) this
					.getEditorInput();
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			final ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
			final StreamResult streamResult1 = new StreamResult(
					byteArrayOutputStream1);
			final StreamResult streamResult = new StreamResult(
					byteArrayOutputStream);
			final Transformer serializer = TransformerFactory.newInstance()
					.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			serializer.transform(new DOMSource(dl), streamResult);
			final Document documentation = DOCUMENT_BUILDER.newDocument();
			this.model.getDocumentation().store(documentation);
			serializer.transform(new DOMSource(documentation), streamResult1);
			this.version = 0;
			try {
				fl.getFile().setContents(
						new ByteArrayInputStream(
								byteArrayOutputStream.toByteArray()), true,
						true, monitor);
				final ByteArrayInputStream source = new ByteArrayInputStream(
						byteArrayOutputStream1.toByteArray());
				if (!this.docFile.exists()) {
					this.docFile.create(source, true, monitor);
				} else {
					this.docFile.setContents(source, true, true, monitor);
				}
				this.firePropertyChange(IEditorPart.PROP_DIRTY);
			} catch (final CoreException e) {
				Activator.log(e);
			}

		} catch (final TransformerConfigurationException e) {
			Activator.log(e);
		} catch (final TransformerException e) {
			Activator.log(e);
		} catch (final TransformerFactoryConfigurationError e) {
			Activator.log(e);
		}

	}

	public void doSaveAs() {

	}

	IProject project;

	Runnable projectModelListener = new Runnable() {

		public void run() {
			ProjectContibutionModel projectModel = NamespacesModel
					.getInstance().getProjectModel(project);
			if (projectModel != null) {
				ArrayList<NameSpaceContributionModel> namespaces = projectModel
						.getNamespaces();
				for (NameSpaceContributionModel m : namespaces) {
					IFile resource = m.getResource();
					if (file.equals(resource)) {
						model.setUrlExt(m.getDeclUrl());
					}
				}
			}
		}
	};

	private IFile file;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.setSite(site);
		super.setInput(input);
		NamespacesModel instance = NamespacesModel.getInstance();

		if (input instanceof IFileEditorInput) {
			final IFileEditorInput fl = (IFileEditorInput) input;
			InputStream contents;

			instance.addModelListener(fl.getFile().getProject(),
					projectModelListener);
			try {
				this.project = fl.getFile().getProject();
				this.file = fl.getFile();
				contents = fl.getFile().getContents(true);
				try {
					final Document parse = DOCUMENT_BUILDER.parse(contents);
					this.model = new NameSpaceContributionModel(
							parse.getDocumentElement());

				} catch (final SAXException e) {
					this.model = new NameSpaceContributionModel();
				} catch (final IOException e) {
					Activator.log(e);
				}
				try {
					contents.close();
				} catch (final IOException e) {
					Activator.log(e);
				}
			} catch (final CoreException e) {
				this.model = new NameSpaceContributionModel();
			}
			final IContainer parent = ((IFileEditorInput) input).getFile()
					.getParent();
			this.docFile = parent.getFile(new Path(fl.getFile().getName()
					+ "-doc"));
			if (!docFile.exists()) {
				IProject project2 = parent.getProject();
				IProject project3 = project2.getWorkspace().getRoot()
						.getProject(project2.getName() + ".ide");
				if (project3.exists()) {
					this.docFile = project3.getFile(fl.getFile().getName()
							+ "-doc");
				}
			}
			try {
				if (this.docFile.exists()) {
					contents = this.docFile.getContents(true);
					try {
						final Document parse = DOCUMENT_BUILDER.parse(contents);
						final DocumentationContributionModel ma = new DocumentationContributionModel(
								this.model, parse);
						this.model.setDocumentation(ma);
						ma.setLoader(new IResourceLoader() {

							public URL getResource(String name) {
								try {
									return docFile.getProject()
											.getFile(new Path(name))
											.getRawLocationURI().toURL();
								} catch (MalformedURLException e) {
									return null;
								}
							}
						});
					} finally {
						contents.close();
					}
				} else {
					final DocumentationContributionModel ma = new DocumentationContributionModel(
							this.model);
					this.model.setDocumentation(ma);
				}
			} catch (final Exception e) {
				final DocumentationContributionModel ma = new DocumentationContributionModel(
						this.model);
				this.model.setDocumentation(ma);
			}
			this.model.getDocumentation().setLocation(
					docFile.getLocation().toFile());
			projectModelListener.run();
		}
	}

	int version = 0;

	public boolean isDirty() {
		final boolean b = this.version != 0;
		return b;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public void createPartControl(Composite parent) {
		final RootElement element = new RootElement(parent);
		final IActionBars actionBars = this.getEditorSite().getActionBars();
		final IHandlerService service = (IHandlerService) actionBars
				.getServiceLocator().getService(IHandlerService.class);
		ObjectUndoContext context = new ObjectUndoContext(this);
		final UndoActionHandler undoActionHandler = new UndoActionHandler(
				this.getEditorSite(), context);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				undoActionHandler);
		final RedoActionHandler redoActionHandler = new RedoActionHandler(
				this.getEditorSite(), context);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				redoActionHandler);
		element.addService(IAllowsRegisterCommands.class,
				new IAllowsRegisterCommands() {

					public Object activateHandler(String str,
							final IHandler handler) {
						final ActionHandler mn = (ActionHandler) handler;
						final String id = mn.getAction().getId();
						if (id != null) {
							actionBars.setGlobalActionHandler(id,
									mn.getAction());
							actionBars.updateActionBars();
						}
						return service.activateHandler(str, handler);
					}

					public void deactivateHandler(Object object) {
						final IHandlerActivation ac = (IHandlerActivation) object;
						service.deactivateHandler(ac);
						final ActionHandler mn = (ActionHandler) ac
								.getHandler();
						final String id = mn.getAction().getId();
						if (id != null) {
							actionBars.setGlobalActionHandler(id, null);
							actionBars.updateActionBars();
						}
					}

				});

		try {
			Binding rootBinding = new Binding(this.model);
			element.setBinding(rootBinding);
			final IPropertyEditor<?> evaluateLocalPluginResource = (IPropertyEditor<?>) DOMEvaluator
					.getInstance().evaluateLocalPluginResource(
							NamespaceEditor.class, "namespaceEditor.dlf",
							rootBinding);
			evaluateLocalPluginResource.getBinding().setUndoContext(this);
			final AbstractUIElement<? extends Control> element2 = (AbstractUIElement<? extends Control>) evaluateLocalPluginResource
					.getUIElement();
			final CompositeEditor ed = (CompositeEditor) element2;

			element.add(ed);
			final EditableListEnumeratedValueSelector vs = (EditableListEnumeratedValueSelector) ed
					.getElement("elements");
			final IBinding binding2 = evaluateLocalPluginResource.getBinding();
			final Binding binding = (Binding) binding2
					.getBinding("currentElement.Handler");
			binding.addValidator(new ValidatorAdapter<String>() {

				public CodeAndMessage isValid(IValidationContext context,
						String object) {
					final Object object2 = context.getObject();
					if (object2 instanceof ElementModel) {
						final ElementModel ms = (ElementModel) object2;
						if (ms == null) {
							return CodeAndMessage.OK_MESSAGE;
						}
						// if (ms.isAbstract()) {
						// if ((object == null)
						// || (object.trim().length() != 0)) {
						// return CodeAndMessage
						// .errorMessage("Handler class should not be specified for abstract elements");
						// }
						// return CodeAndMessage.OK_MESSAGE;
						// }
						// if ((object == null) || (object.trim().length() ==
						// 0)) {
						// return CodeAndMessage
						// .errorMessage("Handler class should be specified for all not abstract elements");
						// }

					}
					return CodeAndMessage.OK_MESSAGE;
				}

			});
			final JavaContentAssistConfiguration object = new JavaContentAssistConfiguration();
			object.setProject(this.project);
			binding.setAdapter(IContentAssistConfiguration.class, object);
			vs.addListener(SWT.Selection, new SWTEventListener<Control>() {

				public void handleEvent(AbstractUIElement<Control> element,
						Event event) {
					final StructuredSelection selection = (StructuredSelection) vs
							.getViewer().getSelection();
					NamespaceEditor.this.selection = selection;
					NamespaceEditor.this.selectionChanged();
				}
			});
			final AbstractEnumeratedValueSelector<?> vs2 = (AbstractEnumeratedValueSelector<?>) ed
					.getElement("properties");
			vs2.addListener(SWT.Selection, new SWTEventListener<Control>() {

				public void handleEvent(AbstractUIElement<Control> element,
						Event event) {
					final StructuredSelection selection = (StructuredSelection) vs2
							.getViewer().getSelection();
					NamespaceEditor.this.selection = selection;
					NamespaceEditor.this.selectionChanged();
				}
			});
			final IValueListener<NameSpaceContributionModel> valueListener = new IValueListener<NameSpaceContributionModel>() {

				public void valueChanged(NameSpaceContributionModel oldValue,
						NameSpaceContributionModel newValue) {
					NamespaceEditor.this.version = 1;
					NamespaceEditor.this
							.firePropertyChange(IEditorPart.PROP_DIRTY);
					NamespaceEditor.this.selectionChanged();
				}

			};
			this.model.addValueListener(valueListener);
		} catch (final Exception e) {
			throw new RuntimeException(e);
			// Activator.log(e);
		}

		this.provider = new ISelectionProvider() {

			public void addSelectionChangedListener(
					ISelectionChangedListener listener) {
				NamespaceEditor.this.ls.add(listener);
			}

			public ISelection getSelection() {
				return NamespaceEditor.this.selection;
			}

			public void removeSelectionChangedListener(
					ISelectionChangedListener listener) {
				NamespaceEditor.this.ls.remove(listener);
			}

			public void setSelection(ISelection selection) {

			}

		};

		Display.getCurrent().asyncExec(new Runnable() {

			public void run() {
				final IWorkbenchPage activePage = NamespaceEditor.this
						.getSite().getPage();
				try {
					activePage
							.showView("com.onpositive.commons.namespace.ide.ui.documentation");
				} catch (final PartInitException e) {
					Activator.log(e);
				}
			}

		});

		this.getSite().setSelectionProvider(this.provider);
	}

	ArrayList<ISelectionChangedListener> ls = new ArrayList<ISelectionChangedListener>();

	private ISelectionProvider provider;

	private IFile docFile;

	protected void selectionChanged() {

		for (final ISelectionChangedListener l : this.ls) {
			if (this.selection == null) {
				this.selection = new StructuredSelection();
			}
			l.selectionChanged(new SelectionChangedEvent(this.provider,
					this.selection));
		}
	}

	public void setFocus() {

	}

	public NameSpaceContributionModel model() {
		return model;
	}
}