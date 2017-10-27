package com.onpositive.commons.namespace.ide.ui.editors.xml;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.xml.core.internal.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

public class DLFStructuredTextViewerConfiguration extends
		StructuredTextViewerConfigurationXML {

	private String[] fConfiguredContentTypes;
	
	
	protected IContentAssistProcessor[] getContentAssistProcessors(
			final ISourceViewer sourceViewer, String partitionType) {
		final IContentAssistProcessor processor = new DLFContentAssistProcessor(
				sourceViewer);
		return new IContentAssistProcessor[] { processor };
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (fConfiguredContentTypes == null) {
			String[] xmlTypes = StructuredTextPartitionerForXML.getConfiguredContentTypes();
			String[] htmlTypes = new String[]{"org.eclipse.wst.html.SCRIPT"};
			fConfiguredContentTypes = new String[2 + xmlTypes.length + htmlTypes.length];

			fConfiguredContentTypes[0] = IStructuredPartitions.DEFAULT_PARTITION;
			fConfiguredContentTypes[1] = IStructuredPartitions.UNKNOWN_PARTITION;

			int index = 0;
			System.arraycopy(xmlTypes, 0, fConfiguredContentTypes, index += 2, xmlTypes.length);
			System.arraycopy(htmlTypes, 0, fConfiguredContentTypes, index += xmlTypes.length, htmlTypes.length);
		}

		return fConfiguredContentTypes;
	}
}
