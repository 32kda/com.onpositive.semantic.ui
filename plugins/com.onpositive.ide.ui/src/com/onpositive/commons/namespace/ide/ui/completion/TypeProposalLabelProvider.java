/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.onpositive.commons.namespace.ide.ui.completion;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.onpositive.commons.ISWTDescriptor;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.meta.IHasMeta;
import com.onpositive.semantic.model.ui.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;

/**
 * TypeProposalLabelProvider
 * 
 */
public class TypeProposalLabelProvider extends LabelProvider implements ITextLabelProvider,IImageDescriptorProvider {

	/**
	 * 
	 */
	public TypeProposalLabelProvider() {
		// NO-OP
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof TypeContentProposal) {
			return ((TypeContentProposal) element).getImage();
		}
		return null;
	}

	public ImageDescriptor getImageDescriptor(final Object object) {
		return new ISWTDescriptor() {
			
			public org.eclipse.jface.resource.ImageDescriptor getDescripror() {
				return org.eclipse.jface.resource.ImageDescriptor.createFromImage(getImage(object));
			}
		};
	}

	public String getDescription(Object object) {
		return "";
	}

	public String getText(IHasMeta meta, Object parent, Object object) {
		return getText(object);
	}

}
