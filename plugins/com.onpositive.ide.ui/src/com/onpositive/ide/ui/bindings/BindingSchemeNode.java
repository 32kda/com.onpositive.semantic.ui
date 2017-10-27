package com.onpositive.ide.ui.bindings;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;
import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.CommonPropertyProvider;
import com.onpositive.semantic.model.api.property.IProperty;

@SuppressWarnings("restriction")
public class BindingSchemeNode {

	String id = null;
	String className;
	Boolean modelExtension = null;

	public boolean isModelExtension() {
		if (modelExtension == null) {
			if (parent != null) {
				return parent.isModelExtension();
			}
			return true;
		}
		return modelExtension;
	}

	public void setModelExtension(boolean modelExtension) {
		this.modelExtension = modelExtension;
	}

	IType type;
	IDocumentElementNode documentElementNode;
	IJavaProject project;
	IJavaElement javaElement;
	boolean initialized;
	final BindingSchemeNode parent;

	HashMap<String, BindingSchemeNode> childrenMap;

	BindingSchemeNode getParentScheme(String string) {

		int p = string.indexOf('.');
		if (p < 0)
			return this;

		String childId = string.substring(0, p);

		BindingSchemeNode childNode = childrenMap.get(childId);

		if (childNode == null) {
			if (childId.equals("$") || childId.equals("parent")) {
				childNode = parent;
			}
			if (childId.equals("this")) {
				childNode = this;
			}
			if (childNode == null) {
				return null;
			}
		}

		if (!childNode.initialized)
			childNode.init();

		String nextString = string.substring(p + 1);
		return childNode.getParentScheme(nextString);
	}

	BindingSchemeNode getNode(String string) {

		for (BindingSchemeNode n : childrenMap.values()) {
			if (n==null || n.documentElementNode==null){
				continue;
			}
			if (n.documentElementNode.getXMLTagName() == string)
				return n;
		}
		return null;
	}

	public String[] getChildBindings() {
		Object[] tmpArr = childrenMap.keySet().toArray();
		int l = tmpArr.length;
		String[] result = new String[l];
		for (int i = 0; i < l; i++)
			result[i] = (String) tmpArr[i];

		return result;
	}

	public BindingSchemeNode(IDocumentElementNode _element,
			IJavaProject _project, BindingSchemeNode p) {
		initialized = false;
		documentElementNode = _element;
		project = _project;
		this.parent = p;
		DomainEditingModelObject m = (DomainEditingModelObject) documentElementNode;
		final String nameSpaceString = documentElementNode != null ? m
				.getNamespace() : null;
		NamespaceModel resolveNamespace = NamespacesModel.getInstance()
				.resolveNamespace(nameSpaceString);
		ElementModel resolvedElement = resolveNamespace != null ? resolveNamespace
				.resolveElement(m.getLocalName()) : null;

		if (resolvedElement != null) {

			boolean isBinding = false;
			HashSet<ElementModel> allSuperElements = resolvedElement
					.getAllSuperElements();
			for (ElementModel ma : allSuperElements) {
				// String n = ma.getName();
				if (ma.getName().equals("abstractBinding"))
					isBinding = true;
			}

			if (isBinding) {
				IDocumentAttributeNode documentAttribute = m
						.getDocumentAttribute("id");
				if (documentAttribute != null)
					id = documentAttribute.getAttributeValue();

				documentAttribute = m.getDocumentAttribute("class");
				if (documentAttribute != null) {
					className = documentAttribute.getAttributeValue();

				}
				if (m.getLocalName().equals("model")) {
					id = "";
				}
				documentAttribute = m.getDocumentAttribute("modelExtension");
				if (documentAttribute != null) {
					modelExtension = Boolean.parseBoolean(documentAttribute
							.getAttributeValue());
				}
			}
		}
	}

	public BindingSchemeNode(String id, String className,
			IDocumentElementNode element, IJavaProject project,
			IJavaElement javaElement, BindingSchemeNode p) {
		this.initialized = false;
		this.id = id;
		this.className = className;
		this.documentElementNode = element;
		this.project = project;
		this.javaElement = javaElement;
		this.parent = p;
		if (element != null) {
			IDocumentAttributeNode documentAttribute = element
					.getDocumentAttribute("modelExtension");
			if (documentAttribute != null) {
				modelExtension = Boolean.parseBoolean(documentAttribute
						.getAttributeValue());
			}
		}
	}

	void init() {
		if (initialized)
			return;

		childrenMap = new HashMap<String, BindingSchemeNode>();
		Iterable<IProperty> properties = CommonPropertyProvider.INSTANCE
				.getProperties(this);
		for (IProperty p : properties) {
			Class<?> subjectClass = DefaultMetaKeys.getSubjectClass(p);
			BindingSchemeNode bindingSchemeNode = new BindingSchemeNode(id,
					subjectClass.getName(), null, project, null, this);
			childrenMap.put(p.getId(), bindingSchemeNode);
		}
		if (className != null) {
			// It is type

			if (project == null) {

				final IEditorPart activeEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();

				if (activeEditor != null) {
					final IEditorInput editorInput = activeEditor
							.getEditorInput();
					IProject pr = null;
					if ((editorInput != null)
							&& (editorInput instanceof IFileEditorInput)) {
						final IFileEditorInput fl = (IFileEditorInput) editorInput;
						pr = fl.getFile().getProject();
					}
					project = JavaCore.create(pr);
				}
			}
			if (project.exists()) {
				try {
					processAllFields(project, className);

				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}

		if (documentElementNode != null) {
			IDocumentElementNode[] childNodes = documentElementNode
					.getChildNodes();

			for (IDocumentElementNode ma : childNodes) {
				BindingSchemeNode childNode = new BindingSchemeNode(ma,
						project, this);
				if (childNode.id != null)// means that childNode is binding
				{
					BindingSchemeNode bindingSchemeNode = childrenMap
							.get(childNode.id);
					if (bindingSchemeNode != null) {
						if (childNode.javaElement == null) {
							childNode.javaElement = bindingSchemeNode.javaElement;
						}
					}
					childrenMap.put(childNode.id, childNode);
				}
			}
		}
		initialized = true;
	}

	protected void processAllFields(IJavaProject project, String className)
			throws JavaModelException {

		IType type = className != null ? project.findType(className) : null;

		processMember(type);
		processSuperClass(project, type);
	}

	protected void processSuperClass(IJavaProject project, IType type)
			throws JavaModelException {
		if (type != null) {

			String superclassTypeSignature = type.getSuperclassTypeSignature();

			while (superclassTypeSignature != null) {

				String resolvedTypeName = JavaModelUtil.getResolvedTypeName(
						superclassTypeSignature, type);
				if (resolvedTypeName != null) {

					IType superClassType = project.findType(resolvedTypeName);
					if (superClassType != null) {
						processMember(superClassType);
						superclassTypeSignature = superClassType
								.getSuperclassTypeSignature();
					} else
						break;
				}
				else{
					break;
				}
			}
		}
	}

	protected void processMember(IType type) throws JavaModelException {

		if (type == null) {
			return;
		}
		if (type.getFullyQualifiedName().equals("java.lang.Object")) {
			return;
		}
		IMethod[] methods = type.getMethods();

		IJavaProject javaProject = type.getJavaProject();

		for (IMethod ma : methods) {
			if (ma.getParameterNames().length != 0) {
				continue;
			}
			String elementName = ma.getElementName();
			if (elementName.startsWith("get")) {
				elementName = elementName.substring(3);
			} else if (elementName.startsWith("set")) {
				elementName = elementName.substring(3);
			} else if (elementName.startsWith("is")) {
				elementName = elementName.substring(2);
			}

			String returnType = ma.getReturnType();
			returnType = cleanSig(returnType);
			String resolvedClassName = JavaModelUtil.getResolvedTypeName(
					returnType, type);

			childrenMap.put(elementName, new BindingSchemeNode(elementName,
					resolvedClassName, null, javaProject, ma, this));
		}

		IField[] f = type.getFields();
		for (IField ma : f) {
			String typeSignature = ma.getTypeSignature();

			typeSignature = cleanSig(typeSignature);
			String resolvedTypeName = JavaModelUtil.getResolvedTypeName(
					typeSignature, type);

			String id = ma.getElementName();
			childrenMap.put(id, new BindingSchemeNode(id, resolvedTypeName,
					null, javaProject, ma, this));
		}
	}

	protected String cleanSig(String typeSignature) {
		if (Signature.getArrayCount(typeSignature) > 0) {
			typeSignature = new String(Signature.getElementType(typeSignature
					.toCharArray()));
		}

		if (typeSignature.contains("Set") || typeSignature.contains("List")
				|| typeSignature.contains("Vector")
				|| typeSignature.contains("Collection")
				|| typeSignature.contains("Stack")) {
			try {
				String[] typeArguments = Signature
						.getTypeArguments(typeSignature);
				if (typeArguments != null && typeArguments.length > 0) {
					typeSignature = typeArguments[0];
				}
			} catch (Exception e) {
				try {
					String[] typeParameterBounds = Signature
							.getTypeParameterBounds(typeSignature);
					if (typeParameterBounds != null
							&& typeParameterBounds.length > 0) {
						typeSignature = typeParameterBounds[0];
					}
				} catch (Exception ex) {
					// TODO: handle exception
				}
			}
			// re
		}
		if (typeSignature.startsWith("+")) {
			typeSignature = typeSignature.substring(1);
		}
		return typeSignature;
	}

	public void deleteChild(BindingSchemeNode childNode) {
		childrenMap.remove(childNode.id);
	}

	public void merge(BindingSchemeNode anotherNode) {

		childrenMap.putAll(anotherNode.childrenMap);
		this.modelExtension = anotherNode.modelExtension;
	}

	public IJavaElement getJavaElement() {
		return javaElement;
	}

	public IDocumentElementNode getDocumentElementNode() {
		return documentElementNode;
	}
}
