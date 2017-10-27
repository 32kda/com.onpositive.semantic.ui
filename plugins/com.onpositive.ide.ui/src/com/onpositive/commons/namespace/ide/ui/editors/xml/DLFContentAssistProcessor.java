/**
 * 
 */
package com.onpositive.commons.namespace.ide.ui.editors.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextPresentationListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.xml.core.internal.parser.XMLTokenizer;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.onpositive.commons.Activator;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModel;
import com.onpositive.commons.namespace.ide.ui.editors.xml.model.DomainEditingModelObject;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentAttributeNode;
import com.onpositive.commons.namespace.ide.ui.internal.core.text.IDocumentElementNode;
import com.onpositive.semantic.language.model.AttributeModel;
import com.onpositive.semantic.language.model.ElementModel;
import com.onpositive.semantic.language.model.NamespaceModel;
import com.onpositive.semantic.language.model.NamespacesModel;

@SuppressWarnings({ "restriction", "unused" })
final class DLFContentAssistProcessor implements IContentAssistProcessor {
	IDocument document;
	DomainEditingModel model;

	final static int COMPLETE_TAG_NAME = 0;
	final static int COMPLETE_ATTR_NAME = 1;
	final static int COMPLETE_ATTR_VALUE = 2;
	final static int COMPLETE_CHILD_NAME = 3;

	DLFContentAssistProcessor(final ISourceViewer sourceViewer) {
		final StructuredTextViewer tv = (StructuredTextViewer) sourceViewer;
		tv.addTextPresentationListener(new ITextPresentationListener() {

			ExpressionStyleRangeProvider provider = new ExpressionStyleRangeProvider();

			public void applyTextPresentation(TextPresentation textPresentation) {
				if (document==null||model==null){
					document = sourceViewer.getDocument();
					loadModel(sourceViewer.getSelectedRange());
				}
				IDOMModel iDOMModel = (IDOMModel) StructuredModelManager
						.getModelManager().getModelForRead(
								(IStructuredDocument) tv.getDocument());
				Node node = iDOMModel.getDocument();

				processNode(node, textPresentation, model.getRoot());

				iDOMModel.releaseFromRead();
			}

			void processNode(Node node, TextPresentation textPresentation,
					Object domainEditingModelObject) {
				if (node instanceof IDOMElement) {
					IDOMElement el = (IDOMElement) node;
					NamedNodeMap attributes = el.getAttributes();
					IDocumentAttributeNode[] nodeAttributes =domainEditingModelObject!=null?  ((DomainEditingModelObject) domainEditingModelObject)
							.getNodeAttributes():new IDocumentAttributeNode[0];
					for (int a = 0; a < attributes.getLength(); a++) {
						IDocumentAttributeNode mn = null;
						if (nodeAttributes.length > a) {
							mn = nodeAttributes[a];
						}
						processNode(attributes.item(a), textPresentation, mn);
					}
				}
				if (node instanceof Attr) {
					String type=null;
					if (domainEditingModelObject instanceof IDocumentAttributeNode) {
						IDocumentAttributeNode m = (IDocumentAttributeNode) domainEditingModelObject;
						DomainEditingModelObject pel = (DomainEditingModelObject) m
								.getEnclosingElement();
						final String namespace = pel.getNamespace();
						final String tag = pel.getLocalName();

						final NamespacesModel instance = NamespacesModel
								.getInstance();
						final NamespaceModel resolveNamespace = instance
								.resolveNamespace(namespace);

						if (resolveNamespace != null) {
							// return;

							final ElementModel resolveElement = resolveNamespace
									.resolveElement(tag);
							if (resolveElement!=null){
								AttributeModel property = resolveElement.resolveProperty(node.getNodeName());
								if (property!=null){
									type = property.getType();
								}
							}
						}
					}
					String attrValue = ((Attr) node).getValue();
					int offset = ((IDOMAttr) node).getValueRegionStartOffset() + 1;
					StyleRange[] newPaintings = provider.computeStyleRanges(
							offset, attrValue,type);
					ArrayList<StyleRange>fR=new ArrayList<StyleRange>();
					for (StyleRange c:newPaintings){
						if (c.start+c.length>textPresentation.getExtent().getOffset()
								&&c.start<textPresentation.getExtent().getOffset()+textPresentation.getExtent().getLength()){
							fR.add(c);
						}
					}
					newPaintings=fR.toArray(new StyleRange[fR.size()]);
					if (newPaintings != null && newPaintings.length > 0)
						textPresentation.mergeStyleRanges(newPaintings);
				} else {
					IDocumentElementNode[] childNodes =domainEditingModelObject!=null?  ((DomainEditingModelObject) domainEditingModelObject)
							.getChildNodes():new IDocumentElementNode[0];
					NodeList nodeList = node.getChildNodes();
					int l = nodeList.getLength();
					int k=0;
					for (int i = 0; i < l; i++) {
						IDocumentElementNode el = null;
						Node item = nodeList.item(i);
						if (k < childNodes.length) {
							el = childNodes[k];
						}
						if (item instanceof Element){
							k++;
						}
						if (node instanceof org.w3c.dom.Document){
							el=(IDocumentElementNode) domainEditingModelObject;
						}					
						
						processNode(item, textPresentation, el);
					}
				}
			}
		});
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		this.document = viewer.getDocument();
		this.loadModel(viewer.getSelectedRange());

		if (this.model.getRoot() == null) {
			this.loadModel(viewer.getSelectedRange());
			if (this.model.getRoot() == null) {
				try {
					final String text = viewer.getDocument().get(0, offset);
					final CompletionInfo determineCompletionType = this
							.determineCompletionType(text, offset);
					if (determineCompletionType != null) {
						ITextRegion region = determineCompletionType.region;
						if (region == null
								&& determineCompletionType.previousRegions
										.size() > 0)
							region = determineCompletionType.previousRegions
									.peek();
						if (region != null) {
							final String type = region.getType();
							if (type.equals(DOMRegionContext.XML_TAG_OPEN)
									|| type.equals(DOMRegionContext.XML_TAG_NAME)) {
								return new ElementNameProposalComputer(
										determineCompletionType.isInTagDeclaration)
										.computeProposals(null, null, viewer,
												offset, text.substring(region
														.getStart(), Math.min(
														region.getTextEnd(),
														offset)), "");
							}
						} else {
							return computeIncorrect(null, viewer, offset,
									determineCompletionType);
						}
					} else {
						return null;
					}
				} catch (final Exception e) {
					Activator.log(e);
				}
			}
		}
		if (this.model.getRoot() == null) {
			return null;
		}
		final DomainEditingModelObject findElement = this.model.getRoot()
				.findElement(offset);
		try {
			final int offset2 = findElement.getOffset();
			final String text = viewer.getDocument().get(offset2,
					findElement.getLength());
			final CompletionInfo determineCompletionType = this
					.determineCompletionType(text, offset - offset2);
			if (determineCompletionType.isInTagDeclaration) {
				final ITextRegion region = determineCompletionType.region;
				if (region != null) {
					String type = region.getType();
					if (type.equals(DOMRegionContext.XML_TAG_NAME)) {
						String tagName = text.substring(region.getStart(),
								region.getTextEnd());
						ElementNameProposalComputer computer = new ElementNameProposalComputer(
								determineCompletionType.isInTagDeclaration);
						computer.setNeedToCloseTag(false);
						return computer.computeProposals(
								this.model,
								findElement,
								viewer,
								offset,
								text.substring(
										region.getStart(),
										Math.min(region.getTextEnd(), offset
												- offset2)), tagName);
					}
					if (type.equals(DOMRegionContext.XML_TAG_OPEN)) {
						return computeIncorrect(this.model, viewer, offset,
								determineCompletionType);
					}
					if (type.equals(DOMRegionContext.XML_CONTENT)
							&& determineCompletionType.previousRegions.peek()
									.getType()
									.equals(DOMRegionContext.XML_TAG_OPEN)) {
						return new ElementNameProposalComputer(// TODO пока
																// затычка, т.е.
																// предполагается,
																// что xml не
																// валидный и мы
																// вызвали комп.
																// после '<' без
																// ничего за ней
								determineCompletionType.isInTagDeclaration)
								.computeProposals(this.model, findElement,
										viewer, offset, "", findElement, "");

					}
					if (type.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
						String fullString = "";
						if (offset - offset2 > region.getStart())
							fullString = computeFullString(region.getStart(),
									region.getTextEnd(), text);
						return computeStandartAttributeName(
								findElement,
								viewer,
								offset,
								text,
								text.substring(
										region.getStart(),
										Math.min(region.getTextEnd(), offset
												- offset2)), fullString);
					}
					if (type.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)
							&& determineCompletionType.previousRegions.size() > 0) {
						ITextRegion prevRegion = determineCompletionType.previousRegions
								.peek();
						if (!prevRegion.getType().equals(
								DOMRegionContext.XML_TAG_ATTRIBUTE_NAME))
							return null;
						return computeStandartAttributeName(findElement,
								viewer, offset, text, text.substring(
										prevRegion.getStart(),
										prevRegion.getTextEnd()),
								text.substring(prevRegion.getStart(),
										prevRegion.getTextEnd()));
					}

					if (type.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {

						ITextRegion pop = determineCompletionType.previousRegions
								.pop();
						pop = determineCompletionType.previousRegions.pop();
						final String name = viewer.getDocument().get(
								pop.getStart() + offset2, pop.getLength());
						final AttributeValueComputer pr = new AttributeValueComputer(
								name);
						return pr.computeProposals(
								this.model,
								findElement,
								viewer,
								offset,
								text.substring(
										region.getStart(),
										Math.min(region.getTextEnd(), offset
												- offset2)),
								region.getLength(),
								text.substring(region.getStart(),
										region.getTextEnd()));
					}
					if (type.equals(DOMRegionContext.XML_TAG_CLOSE)
							|| type.equals(DOMRegionContext.XML_EMPTY_TAG_CLOSE)) {
						if (offset - offset2 > region.getStart())
							return null;
						ITextRegion prevRegion = determineCompletionType.previousRegions
								.peek();
						if (offset2 + prevRegion.getStart()
								+ prevRegion.getTextEnd() - 1 == offset
								&& prevRegion.getType().equals(
										DOMRegionContext.XML_TAG_NAME)) // If
																		// true,
																		// we
																		// stay
																		// at
																		// tag
																		// name
																		// end,
																		// and
																		// must
																		// complete
																		// tag
																		// name
						{
							String startStr = text.substring(
									prevRegion.getStart(),
									prevRegion.getTextEnd());
							return new ElementNameProposalComputer(
									determineCompletionType.isInTagDeclaration)
									.computeProposals(this.model, findElement,
											viewer, offset, startStr,
											findElement, startStr);
						}
						String startStr = "", fullStr = "";
						final AttributeProposalComputer pr = new AttributeProposalComputer( // Else
																							// -
																							// we
																							// select
																							// new
																							// attribute
								false);
						if (prevRegion.getType().equals(
								DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
							fullStr = text.substring(prevRegion.getStart(),
									prevRegion.getTextEnd());
							startStr = text.substring(
									prevRegion.getStart(),
									Math.min(prevRegion.getTextEnd(), offset
											- offset2));
						}
						return pr.computeProposals(this.model, findElement,
								viewer, offset, startStr, fullStr);
					}
				} else {
					if (!determineCompletionType.previousRegions.isEmpty()) {

						final ITextRegion peek = determineCompletionType.previousRegions
								.peek();
						if (peek.getType().equals(
								DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)) {
							System.out.println("Attribute value");
						} else if (peek.getType().equals(
								DOMRegionContext.XML_TAG_NAME)
								&& offset > 0
								&& !Character.isWhitespace(document.get(
										offset - 1, 1).charAt(0))) {

							ElementNameProposalComputer computer = new ElementNameProposalComputer(
									determineCompletionType.isInTagDeclaration);
							computer.setNeedToCloseTag(!checkForTagClosePresence(
									document, offset));
							return computer.computeProposals(
									this.model,
									findElement,
									viewer,
									offset,
									text.substring(
											peek.getStart(),
											Math.min(peek.getTextEnd(), offset
													- offset2)),
									findElement, // TODO Added experimentally
									text.substring(peek.getStart(),
											peek.getTextEnd()));
						}
						if (peek.getType().equals(
								DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
							final AttributeProposalComputer pr = new AttributeProposalComputer(
									false);
							return pr.computeProposals(
									this.model,
									findElement,
									viewer,
									offset,
									text.substring(
											peek.getStart(),
											Math.min(peek.getTextEnd(), offset
													- offset2)),
									findElement, // TODO Added experimentally
									text.substring(peek.getStart(),
											peek.getTextEnd()));
						}
					}
					final AttributeProposalComputer pr = new AttributeProposalComputer(
							false);
					return pr.computeProposals(this.model, findElement, viewer,
							offset, "", "");

				}
			}
			if (determineCompletionType != null) {
				final String type = determineCompletionType.getType();
				String peekType = "";
				if (determineCompletionType.previousRegions.size() > 1)
					determineCompletionType.previousRegions.peek().getType();
				if (type != null) {
					if (type.equals(DOMRegionContext.XML_CONTENT)) {
						this.doTagCompletion();
					} else if (type
							.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
						System.out.println("Attribute value");
					} else if (type
							.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME)) {
						System.out.println("Attribute name");
					} else if (type
							.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_EQUALS)
							|| type.equals(DOMRegionContext.XML_END_TAG_OPEN)
							|| type.equals(DOMRegionContext.XML_TAG_CLOSE)
							|| (type.equals(DOMRegionContext.XML_TAG_NAME) && peekType
									.equals(DOMRegionContext.XML_END_TAG_OPEN))) {
						return null;
					}
				}
				ElementNameProposalComputer computer = new ElementNameProposalComputer(
						determineCompletionType.isInTagDeclaration);
				computer.setAddAtBeginningStr("<");
				return computer.computeProposals(this.model, findElement,
						viewer, offset, "", findElement, "");
			}
		} catch (final BadLocationException e) {
			Activator.log(e);
		}
		// System.out.println(findElement.getLocalName());
		return null;
	}

	/**
	 * Used to compute full string for attribute (means not only attr name, but
	 * a value too)
	 * 
	 * @param regionStart
	 *            start of detected name value region
	 * @param regionEnd
	 *            end of dected attr name region
	 * @param text
	 *            full tag text, where attr was detected
	 * @return full string fo attr, like name="value"
	 */
	private String computeFullString(int regionStart, int regionEnd, String text) {
		String baseStr = text.substring(regionStart, regionEnd);
		String rest = text.substring(regionEnd); // Rest of line, normally must
													// contain '="value"'
		int i = 0;
		while (Character.isWhitespace(rest.charAt(i)))
			i++;
		if (rest.charAt(i) != '=')
			return baseStr;
		i++;
		while (Character.isWhitespace(rest.charAt(i)))
			i++;
		if (rest.charAt(i) != '"')
			return baseStr;
		i++;
		for (int j = i; j < rest.length(); j++) {
			if (rest.charAt(j) == '"' && rest.charAt(j - 1) != '\\')
				return baseStr + rest.substring(0, j + 1);
		}
		return baseStr;
	}

	private ICompletionProposal[] computeStandartAttributeName(
			DomainEditingModelObject findElement, ITextViewer viewer,
			int offset, String text, String startString, String fullString) {
		final AttributeProposalComputer pr = new AttributeProposalComputer(
				false);
		return pr.computeProposals(this.model, findElement, viewer, offset,
				startString, fullString);
	}

	private ICompletionProposal[] computeIncorrect(DomainEditingModel model2,
			ITextViewer viewer, int offset,
			CompletionInfo determineCompletionType) throws BadLocationException {
		IDocument document = viewer.getDocument();
		ElementNameProposalComputer computer = new ElementNameProposalComputer(
				determineCompletionType.isInTagDeclaration);
		String[] res = tokenizeString(document.get(0, offset));

		String startStr = "", fullStr = "";
		if (res.length > 0) {
			if (!res[res.length - 1].equals("<")) {
				startStr = fullStr = res[res.length - 1];
				if (res.length < 2 || !res[res.length - 2].equals("<"))
					computer.setAddAtBeginningStr("<");
			}
		} else
			computer.setAddAtBeginningStr("<");
		return computer.computeProposals(this.model, null, viewer, offset,
				startStr, fullStr);
	}

	private String[] tokenizeString(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, "<>/", true);
		String[] res = new String[tokenizer.countTokens()];
		for (int i = 0; i < res.length; i++) {
			res[i] = tokenizer.nextToken();
		}
		return res;
	}

	private boolean checkForTagClosePresence(IDocument document, int offset) {
		for (int i = offset; i < document.getLength() - 1; i++) {
			try {
				if (document.get(i, 1).equals(">"))
					return true;
				if (document.get(i, 1).equals("<"))
					return false;
			} catch (BadLocationException e) {
			}
		}
		return false;
	}

	private void doTagCompletion() {
		System.out.println("Tag completion");
	}

	static class CompletionInfo {
		Stack<ITextRegion> previousRegions = new Stack<ITextRegion>();
		boolean isInTagDeclaration;
		ITextRegion region;

		public String getType() {
			// TODO Auto-generated method stub
			if (region != null)
				return region.getType();
			return null;
		}
	}

	private CompletionInfo determineCompletionType(String text, int offset) {
		if (offset < 0)
			offset = 0;
		final XMLTokenizer tk = new XMLTokenizer();
		tk.reset(new StringReader(text));
		final CompletionInfo info = new CompletionInfo();
		while (true) {
			ITextRegion nextToken;
			try {
				nextToken = tk.getNextToken();
				if (nextToken == null) {
					break;
				} else {
					final String type = nextToken.getType();
					if (type.equals(DOMRegionContext.XML_TAG_OPEN)) {
						info.isInTagDeclaration = true;
					} else if (type.equals(DOMRegionContext.XML_TAG_CLOSE)
							|| type.equals(DOMRegionContext.XML_EMPTY_TAG_CLOSE)) {
						if (nextToken.getEnd() <= offset) {
							info.isInTagDeclaration = false;
						}
					}
					if ((nextToken.getStart() <= offset)
							&& (nextToken.getTextEnd() > offset)) {
						info.region = nextToken;
						return info;
					}
					if (nextToken.getStart() > offset) {
						return info;
					}

				}
				info.previousRegions.add(nextToken);
			} catch (final IOException e) {
				break;
			}
		}
		return info;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	private void loadModel(Point point) {
		if (this.document == null) {
			return;
		}
		DomainEditingModel dm = new DomainEditingModel(this.document, true);
		try {
			dm.load();
			if (dm.isValid()) {
				this.model = dm;
			} else {
				final String initialContent = this.document.get();
				StringBuffer bs = new StringBuffer(initialContent);

				try {
					if ((point.y == 0) && (point.x > 0)) {
						for (int a = point.x - 1; a >= 0; a--) {
							final char charAt = bs.charAt(a);
							if (Character.isWhitespace(charAt)) {
								break;
							} else {
								bs.setCharAt(a, ' ');
							}
						}
						final Document da = new Document(bs.toString());
						dm = new DomainEditingModel(da, true);
						dm.load();
						if (dm.isValid()) {
							this.model = dm;
							return;
						} else {
							bs = new StringBuffer(initialContent);
						}
						IRegion lineInformationOfOffset;

						lineInformationOfOffset = this.document
								.getLineInformationOfOffset(point.x);

						final int end = lineInformationOfOffset.getOffset()
								+ lineInformationOfOffset.getLength();
						for (int a = lineInformationOfOffset.getOffset(); a < end; a++) {
							bs.setCharAt(a, ' ');
						}
					}
					final Document da = new Document(bs.toString());
					dm = new DomainEditingModel(da, true);
					dm.load();
					this.model = dm;
				} catch (final BadLocationException e) {
					// Activator.log(e);
				}
			}
		} catch (final CoreException e) {
			throw new RuntimeException();
		}
	}
}