package com.onpositive.semantic.model.ui.viewer.structured;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreePathViewerSorter;
import org.eclipse.jface.viewers.Viewer;

import com.onpositive.semantic.model.tree.ITreeNode;

public class TreeNodeComparator extends TreePathViewerSorter {

	@SuppressWarnings("unchecked")
	public void sort(final Viewer viewer, final TreePath parentPath,
			Object[] elements) {
		ITreeNode treeNode = null;
		if (parentPath == null) {
			treeNode = (ITreeNode) viewer.getInput();
		} else {
			treeNode = ((ITreeNode) parentPath.getLastSegment());
		}
		final Comparator comparator2 = treeNode.getComparator();
		if (comparator2 != null) {
			Arrays.sort(elements, comparator2);
		} else {
			Arrays.sort(elements);
		}
	}

}
