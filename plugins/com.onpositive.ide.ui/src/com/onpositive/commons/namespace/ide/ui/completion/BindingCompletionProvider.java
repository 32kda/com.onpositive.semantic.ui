package com.onpositive.commons.namespace.ide.ui.completion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.Region;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.CompletionProviderRegistry;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.ITypeCompletionProvider;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;
import com.onpositive.ide.ui.bindings.BindingSchemeNode;
import com.onpositive.ide.ui.bindings.BindingSchemeTree;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;

public class BindingCompletionProvider implements ITypeCompletionProvider {

	public BindingCompletionProvider() {

	}
	public void fillProposals( String attributeName, DomainEditingModelObject findElement, ITextViewer viewer,
							   int offset, String startString, int lengthCompletion, ArrayList<ICompletionProposal> result,
							   String fullString, boolean addBraces,String typeSpec )
	{
		DomainEditingModelObject root = findElement.getRoot();

		BindingSchemeTree tree = new BindingSchemeTree( root, null ) ;
		
		DomainEditingModelObject a = (DomainEditingModelObject) findElement.getParentNode();
		String ms = "";
		
		while (a != null) {
			IDocumentAttributeNode documentAttribute = a
					.getDocumentAttribute("bindTo");
			if (documentAttribute != null) {
				String attributeValue = documentAttribute.getAttributeValue();
				if (!attributeValue.equals("this")) {
					ms = attributeValue + "." + ms;
				}
			}
			a = (DomainEditingModelObject) a.getParentNode();
		}
		if (pBinding!=null){
			if (ms.length()==0){
				ms="this";
			}
			ms=ms+"."+pBinding+".";
		}
		BindingSchemeNode parentNode = tree.getParentScheme( ms+startString ) ;
		if( parentNode == null )
			return ;
		
		String res = startString ;
		{			
			int p = res.indexOf('.') ;
			for( ; p > 0 ; )
			{
				res = res.substring( p+1 ) ;
				p = res.indexOf('.') ;				
			}
		}
		
		String[] childBindings = parentNode.getChildBindings() ;
		java.util.Arrays.sort( childBindings ) ;
				 
		for( String s : childBindings )
		{			
			if( s != null && s.startsWith( res ) )
			{
				CompletionProposal cp = new CompletionProposal( 
										s, offset - res.length() , res.length(), s.length(),
										JavaUI.getSharedImages().getImage(ISharedImages.IMG_FIELD_PUBLIC),
										s, null, null );
				result.add(cp);				
			}
		} 		
	}
	String pBinding;
	public void shift(String pBinding) {
		this.pBinding=pBinding;
	}

//	public void fillProposals2(String attributeName,
//			DomainEditingModelObject findElement, ITextViewer viewer,
//			int offset, String startString, int lengthCompletion,
//			ArrayList<ICompletionProposal> result, String fullString,
//			boolean addBraces,String typeSpec) {
//		DomainEditingModelObject root = findElement.getRoot();
//		HashSet<String> str = new HashSet<String>();
//		Stack<String> ids = new Stack<String>();
//		traverse(root, str, ids);
//		DomainEditingModelObject a = (DomainEditingModelObject) findElement
//				.getParentNode();
//		String ms = "";
//		while (a != null) {
//			IDocumentAttributeNode documentAttribute = a
//					.getDocumentAttribute("bindTo");
//			if (documentAttribute != null) {
//				String attributeValue = documentAttribute.getAttributeValue();
//				if (!attributeValue.equals("this")) {
//					ms = attributeValue + "." + ms;
//				}
//			}
//			a = (DomainEditingModelObject) a.getParentNode();
//		}
//		for (String s : str) {
//			if (s.startsWith(ms + fullString)) {
//				s = s.substring(ms.length());
//				if (s.length() == 0) {
//					continue;
//				}
//				if (s.indexOf('.')!=-1){
//					continue;
//				}
//				CompletionProposal e = new CompletionProposal(s, offset
//						- fullString.length(), fullString.length(), s.length(),
//						JavaUI.getSharedImages().getImage(
//								ISharedImages.IMG_FIELD_PUBLIC), s, null, null);
//
//				result.add(e);
//			}
//		}
//	}
//
//	private void traverse(IDocumentElementNode findElement,
//			HashSet<String> str, Stack<String> ids) {
//		if (!(findElement instanceof DomainEditingModelObject)) {
//			return;
//		}
//		DomainEditingModelObject m = (DomainEditingModelObject) findElement;
//		NamespaceModel resolveNamespace;
//		final String namespace = findElement != null ? m.getNamespace() : null;
//		resolveNamespace = NamespacesModel.getInstance().resolveNamespace(
//				namespace);
//
//		ElementModel parentElement = null;
//
//		parentElement = resolveNamespace != null ? resolveNamespace
//				.resolveElement(m.getLocalName()) : null;
//		String strA = null;
//		if (parentElement != null) {
//			final HashSet<AttributeModel> allProperties = parentElement
//					.getAllProperties();
//			HashSet<ElementModel> allSuperElements = parentElement
//					.getAllSuperElements();
//			boolean isBinding = false;
//			for (ElementModel ma : allSuperElements) {
//				if (ma.getName().equals("abstractBinding")) {
//					isBinding = true;
//				}
//			}
//			for (final AttributeModel mq : allProperties) {
//				if (mq.getType().equals("binding")) {
//					IDocumentAttributeNode documentAttribute = m
//							.getDocumentAttribute(mq.getName());
//					if (documentAttribute != null) {
//						String attributeValue = documentAttribute
//								.getAttributeValue();
//						strA = ids.isEmpty() ? attributeValue : ids.peek()
//								+ "." + attributeValue;
//						//str.add(strA);
//					}
//				}
//			}
//			if (isBinding) {
//				IDocumentAttributeNode documentAttribute = m
//						.getDocumentAttribute("id");
//				if (documentAttribute != null) {
//					String attributeValue = documentAttribute
//							.getAttributeValue();
//					strA = ids.isEmpty() ? attributeValue : ids.peek() + "."
//							+ attributeValue;
//					str.add(strA);
//				}
//				documentAttribute = m.getDocumentAttribute("class");
//
//				if (documentAttribute != null) {
//					// It is type
//					final IEditorPart activeEditor = PlatformUI.getWorkbench()
//							.getActiveWorkbenchWindow().getActivePage()
//							.getActiveEditor();
//					if (activeEditor != null) {
//						final IEditorInput editorInput = activeEditor
//								.getEditorInput();
//						IProject pr = null;
//						if ((editorInput != null)
//								&& (editorInput instanceof IFileEditorInput)) {
//							final IFileEditorInput fl = (IFileEditorInput) editorInput;
//							pr = fl.getFile().getProject();
//						}
//
//						IJavaProject create = JavaCore.create(pr);
//						if (create.exists()) {
//							try {
//								String ll = documentAttribute
//										.getAttributeValue();
//								qq1(str, strA, create, ll,0);
//							} catch (JavaModelException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//			}
//		}
//		IDocumentElementNode[] childNodes = findElement.getChildNodes();
//
//		for (IDocumentElementNode ma : childNodes) {
//			if (strA != null) {
//				ids.push(strA);
//			}
//			traverse(ma, str, ids);
//			if (strA != null) {
//				if (!ids.isEmpty()) {
//					ids.pop();
//				}
//			}
//		}
//	}
//
//	protected void qq1(HashSet<String> str, String strA, IJavaProject create,
//			String ll,int level) throws JavaModelException {
//		if (level>3){
//			return;
//		}
//		IType findType = processType(str, strA, create,
//				ll,level);
//		qq(str, strA, create, findType,level);
//	}
//
//	protected void qq(HashSet<String> str, String strA, IJavaProject create,
//			IType findType, int level) throws JavaModelException {
//		if (findType != null) {
//			Region ee = new Region();
//			ee.add(create);
//			String superclassTypeSignature = findType
//					.getSuperclassTypeSignature();
//			while (superclassTypeSignature != null) {
//				String resolvedTypeName = JavaModelUtil
//						.getResolvedTypeName(
//								superclassTypeSignature,
//								findType);
//				if (resolvedTypeName != null) {
//					IType findType2 = create.findType(resolvedTypeName);
//					if (findType2!=null){
//					pm(str, strA, findType2,level);
//					superclassTypeSignature=findType2.getSuperclassTypeSignature();
//					}
//					else{
//						break;
//					}
//				}
//			}
//		}
//	}
//
//	private IType processType(HashSet<String> str, String strA,
//			IJavaProject create, String ll,int level) throws JavaModelException {
//		if (ll == null) {
//			return null;
//		}
//		IType findType = create.findType(ll);
//		if (findType != null) {
//			pm(str, strA, findType,level);
//		}
//		return findType;
//	}
//
//	protected void pm(HashSet<String> str, String strA, IType findType, int level)
//			throws JavaModelException {
//		if (findType==null){
//			return;
//		}
//		if (findType.getFullyQualifiedName().equals("java.lang.Object")) {
//			return;
//		}
//		IMethod[] methods = findType.getMethods();
//		String s = strA == null ? "" : strA + ".";
//		for (IMethod ma : methods) {
//			if (ma.getParameterNames().length!=0){
//				continue;
//			}
//			String elementName = ma.getElementName();
//			if (elementName.startsWith("get")) {
//				elementName = elementName.substring(3);
//			} else if (elementName.startsWith("set")) {
//				elementName = elementName.substring(3);
//			} else if (elementName.startsWith("is")) {
//				elementName = elementName.substring(2);
//			}
//			str.add(s + elementName);
//			String resolvedTypeName = JavaModelUtil.getResolvedTypeName(
//					ma.getReturnType(), findType);
//			IJavaProject javaProject = findType.getJavaProject();
//			qq1(str, s+elementName, javaProject, resolvedTypeName, level+1);
//			
//		}
//		IField[] f = findType.getFields();
//		for (IField ma : f) {
//			str.add(s + ma.getElementName());
//			String resolvedTypeName = JavaModelUtil.getResolvedTypeName(
//					ma.getTypeSignature(), findType);
//			IJavaProject javaProject = findType.getJavaProject();			
//			qq1(str, s+ma.getElementName(), javaProject, resolvedTypeName,level+1);			
//		}
//	}
//
}
