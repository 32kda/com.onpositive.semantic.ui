package com.onpositive.semantic.ui.realm.fastviewer;

import java.util.List;

public interface ITreeNode {

	List<?> getChildren();

	Object getNodeObject();

	boolean hasChildren();
}
