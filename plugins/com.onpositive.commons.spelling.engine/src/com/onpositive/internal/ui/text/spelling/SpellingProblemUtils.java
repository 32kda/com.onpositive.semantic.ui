package com.onpositive.internal.ui.text.spelling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.spelling.SpellingAnnotation;

public class SpellingProblemUtils {

	/**
	 * Removes all spelling problems that are reported
	 * for the given <code>word</code> in the active editor.
	 * 
	 * @param sourceViewer the source viewer
	 * @param word the word for which to remove the problems or <code>null</code> to remove all
	 * @since 3.4
	 */
	public static void removeAll(ISourceViewer sourceViewer, String word) {
		Assert.isNotNull(sourceViewer);
		
		IAnnotationModel model= sourceViewer.getAnnotationModel();
		if (model == null)
			return;
		
		IDocument document= sourceViewer.getDocument();
		if (document == null)
			return;
		
		boolean supportsBatchReplace= (model instanceof IAnnotationModelExtension);
		List toBeRemovedAnnotations= new ArrayList();
		Iterator iter= model.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation= (Annotation) iter.next();
			if (SpellingAnnotation.TYPE.equals(annotation.getType())) {
				boolean doRemove= word == null;
				if (word == null)
					doRemove= true;
				else {
					String annotationWord= null;
					Position pos= model.getPosition(annotation);
					try {
						annotationWord= document.get(pos.getOffset(), pos.getLength());
					} catch (BadLocationException e) {
						continue;
					}
					doRemove= word.equals(annotationWord);
				}
				if (doRemove) {
					if (supportsBatchReplace)
						toBeRemovedAnnotations.add(annotation);
					else
						model.removeAnnotation(annotation);
				}
			}
		}
		
		if (supportsBatchReplace && !toBeRemovedAnnotations.isEmpty()) {
			Annotation[] annotationArray= (Annotation[])toBeRemovedAnnotations.toArray(new Annotation[toBeRemovedAnnotations.size()]);
			((IAnnotationModelExtension)model).replaceAnnotations(annotationArray, null);
		}
	}
}
