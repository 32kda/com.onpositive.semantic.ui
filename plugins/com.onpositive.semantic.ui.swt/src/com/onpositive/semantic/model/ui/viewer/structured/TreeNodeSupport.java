package com.onpositive.semantic.model.ui.viewer.structured;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;

import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;
import com.onpositive.semantic.model.ui.property.editors.structured.RealmLazyTreeContentProvider;

public class TreeNodeSupport {

	public static IContentProvider configure(final AbstractTreeViewer viewer,ListEnumeratedValueSelector<?>selector) {
		// viewer.setSorter(new TreeNodeComparator());
		IContentProvider provider = null;
		if (viewer instanceof CheckboxTreeViewer) {
			provider = new TreeNodeContentProvider();
			viewer.setContentProvider(provider);
		} else {
			provider = new RealmLazyTreeContentProvider(selector);
			viewer.setContentProvider(provider);
		}
		viewer.addTreeListener(new ITreeViewerListener() {

			public void treeCollapsed(TreeExpansionEvent event) {

			}

			public void treeExpanded(TreeExpansionEvent event) {
				final Object element = event.getElement();
				final IContentProvider contentProvider2 = event.getTreeViewer()
						.getContentProvider();
				if (contentProvider2 instanceof ILazyTreeContentProvider) {
					return;
				}
				final ITreeContentProvider contentProvider = (ITreeContentProvider) contentProvider2;
				final Object[] children = contentProvider.getChildren(element);
				if (children.length == 1) {
					if (contentProvider.hasChildren(children[0])) {
						viewer.expandToLevel(children[0], 0);
					}
				}
			}

		});
		return provider;
	}

//	public static TreeViewer createViewer(Composite cm) {
//		final TreeViewer viewer = new TreeViewer(cm) {
//
//			protected void triggerEditorActivationEvent(
//					ColumnViewerEditorActivationEvent event) {
//				final ViewerCell cell = (ViewerCell) event.getSource();
//				final Object element = cell.getElement();
//				final IStructuredSelection selection = (IStructuredSelection) this.getSelection();
//				if (!selection.toList().contains(element)) {
//					return;
//				}
//				if (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC) {
//					super.triggerEditorActivationEvent(event);
//				}
//				if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
//					super.triggerEditorActivationEvent(event);
//				}
//				if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
//					event.eventType = ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION;
//					super.triggerEditorActivationEvent(event);
//				}
//			}
//		};
//		viewer.addOpenListener(new IOpenListener() {
//
//			public void open(OpenEvent event) {
//				final IStructuredSelection selection = (IStructuredSelection) event
//						.getSelection();
//				final Object firstElement = selection.getFirstElement();
//				if (viewer.getCellModifier().canModify(firstElement, null)) {
//					viewer.editElement(firstElement, 0);
//				}
//			}
//		});
//		configure(viewer);
//		return viewer;
//	}

}
