/**
 * 
 */
package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public final class ActivationStrategy extends
		ColumnViewerEditorActivationStrategy {
	private IStructuredSelection structuredSelection;
	private final ColumnViewer viewer;

	public ActivationStrategy(final ColumnViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		viewer.getControl().addListener(SWT.MouseDown, new Listener() {

			public void handleEvent(Event event) {
				ActivationStrategy.this.structuredSelection = ((IStructuredSelection) viewer
						.getSelection());
			}

		});
		;
	}

	protected boolean isEditorActivationEvent(
			ColumnViewerEditorActivationEvent event) {
		if (this.structuredSelection == null) {
			this.structuredSelection = ((IStructuredSelection) this.viewer
					.getSelection());
		}
		final boolean singleSelect = this.structuredSelection.size() == 1;
		final boolean isLeftMouseSelect = (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION)
				&& (((MouseEvent) event.sourceEvent).button == 1);
		final boolean isDoubleMouseSelect = (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
				&& (((MouseEvent) event.sourceEvent).button == 1);
		if (isDoubleMouseSelect) {
			return true;
		}
//		if (isLeftMouseSelect) {
//			if (singleSelect) {
//				final Object sl = this.structuredSelection.getFirstElement();
//				final ViewerCell cl = (ViewerCell) event.getSource();
//				final Object element = cl.getElement();
//				if (element != sl) {
//					System.out.println("Wrong element");
//					return false;
//				}
//			}
//		}
//		this.structuredSelection = null;
		return 
						 (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC) || (event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL);
		//return false;
	}
}