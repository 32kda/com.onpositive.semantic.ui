package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.wst.sse.core.internal.document.IDocumentCharsetDetector;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.AbstractModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.IModelLoader;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentCharsetDetector;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelhandler.XMLModelLoader;
import org.eclipse.wst.xml.core.internal.parser.XMLSourceParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

@SuppressWarnings("restriction")
public class ModelHandlerForDLF extends AbstractModelHandler implements IModelHandler{

	@SuppressWarnings("restriction")
	public ModelHandlerForDLF(){
		setId("com.onpositive.commons.namespace.ide.ui.dlfmodelhandler");
		setAssociatedContentTypeId("com.onpositive.commons.namespace.ide.ui.dlf");
	}
	
	@SuppressWarnings("restriction")
	
	public IDocumentCharsetDetector getEncodingDetector() {
		return new XMLDocumentCharsetDetector();
	}

	@SuppressWarnings("restriction")
	
	public IModelLoader getModelLoader() {
		return new XMLModelLoader(){
			
			protected void addHTMLishTag(XMLSourceParser parser, String tagname) {
				BlockMarker bm = new BlockMarker(tagname, null, DOMRegionContext.BLOCK_TEXT, false);
				parser.addBlockMarker(bm);
			}
			
			@SuppressWarnings("restriction")
			public IDocumentLoader getDocumentLoader() {
				if (documentLoaderInstance == null) {
					documentLoaderInstance =new XMLDocumentLoader(){
						
						/*
						 * @see IModelLoader#getParser()
						 */
						public RegionParser getParser() {
							XMLSourceParser parser = new XMLSourceParser();
							// for the "static HTML" case, we need to initialize
							// Blocktags here.
							addHTMLishTag(parser, "script"); //$NON-NLS-1$
							//addHTMLishTag(parser, "style"); //$NON-NLS-1$
							return parser;
						}
						
						public IDocumentPartitioner getDefaultDocumentPartitioner() {
							return new StructuredTextPartitionerForHTML();
						}
						
					};
				}
				return documentLoaderInstance;
			}
			
		};
	}

	@SuppressWarnings("restriction")
	
	public IDocumentLoader getDocumentLoader() {
		return new XMLDocumentLoader(){
			
			protected void addHTMLishTag(XMLSourceParser parser, String tagname) {
				BlockMarker bm = new BlockMarker(tagname, null, DOMRegionContext.BLOCK_TEXT, false);
				parser.addBlockMarker(bm);
			}
			
			/*
			 * @see IModelLoader#getParser()
			 */
			public RegionParser getParser() {
				XMLSourceParser parser = new XMLSourceParser();
				// for the "static HTML" case, we need to initialize
				// Blocktags here.
				addHTMLishTag(parser, "script"); //$NON-NLS-1$
				//addHTMLishTag(parser, "style"); //$NON-NLS-1$
				return parser;
			}
			
			public IDocumentPartitioner getDefaultDocumentPartitioner() {
				return new StructuredTextPartitionerForHTML();
			}
			
		};
	}
}
