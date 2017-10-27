package com.onpositive.semantic.model.ui.generic;

import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.richtext.StyledString;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;

public interface INodeLabelProvider {

	public ImageDescriptor getImage(ITreeNode node, Object element, String role,
			String theme, Column column);

	public StyledString getRichText(ITreeNode node, Object element,
			String role, String theme, Column column);

}
