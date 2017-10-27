package com.onpositive.commons.namespace.ide.ui.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.internal.corext.dom.HierarchicalASTVisitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;

@SuppressWarnings("restriction")
public class JavaCompletionIntegration implements
		IJavaCompletionProposalComputer {

	public List computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor arg1) {
		if (context instanceof JavaContentAssistInvocationContext) {
			JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
			return internalComputeCompletionProposals(
					context.getInvocationOffset(), javaContext);
		}
		return Collections.EMPTY_LIST;
	}

	private List internalComputeCompletionProposals(final int invocationOffset,
			final JavaContentAssistInvocationContext javaContext) {
		ASTParser newParser = ASTParser.newParser(AST.JLS3);
		newParser.setSource(javaContext.getCompilationUnit());
		CompilationUnit parseCompilationUnit = (CompilationUnit) newParser
				.createAST(new NullProgressMonitor());
		final ArrayList<ICompletionProposal> ps = new ArrayList<ICompletionProposal>();
		parseCompilationUnit.accept(new HierarchicalASTVisitor() {
			@Override
			public boolean visit(StringLiteral node) {
				if (node.getStartPosition() <= invocationOffset
						&& node.getStartPosition() + node.getLength() > invocationOffset) {
					if (node.getParent().getParent() instanceof Annotation) {
						Annotation new_name = (Annotation) node.getParent()
								.getParent();
						ExpressionComplitionProvider p = new ExpressionComplitionProvider();
						int _offset = javaContext.getInvocationOffset();
						ArrayList<ICompletionProposal> _result = ps;
						int _lengthCompletion = 0;
						String _attributeName = "";
						DomainEditingModel dm = null;
						try {
							String initialContent = "<composite-editor xmlns=\"http://jetface.org/JetFace1.0/\">"
									+ "<model class=\""
									+ javaContext.getCompilationUnit()
											.getTypes()[0]
											.getFullyQualifiedName()
									+ "\"></model></composite-editor>";
							dm = new DomainEditingModel(new Document(
									initialContent), true);
							dm.load();
						} catch (JavaModelException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						DomainEditingModelObject _findElement = dm.getRoot();
						String _typeSpec = "";
						if (new_name.getTypeName().getFullyQualifiedName()
								.equals("Validator")) {
							p.fillProposals(
									_attributeName,
									_findElement,
									javaContext.getViewer(),
									_offset,
									'{' + node.getLiteralValue().substring(
											0,
											invocationOffset
													- node.getStartPosition()
													- 1), _lengthCompletion,
									_result,
									'{' + node.getLiteralValue() + '}', false,
									_typeSpec,null);
						} else {
							p.fillProposals(
									_attributeName,
									_findElement,
									javaContext.getViewer(),
									_offset,
									node.getLiteralValue().substring(
											0,
											invocationOffset
													- node.getStartPosition()
													- 1), _lengthCompletion,
									_result,
									node.getLiteralValue(), false,
									_typeSpec,null);
						}
						// System.out.println(new_name);
					}
				}
				return super.visit(node);
			}
		});
		return ps;
	}

	public List computeContextInformation(ContentAssistInvocationContext arg0,
			IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public void sessionEnded() {

	}

	public void sessionStarted() {

	}

}
