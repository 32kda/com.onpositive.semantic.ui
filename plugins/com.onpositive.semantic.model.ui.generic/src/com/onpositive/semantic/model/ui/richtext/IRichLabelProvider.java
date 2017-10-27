/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 * Copied from JDT UI: org.eclipse.jdt.internal.ui.viewsupport.IRichLabelProvider.
 * Will be removed again when made API. https://bugs.eclipse.org/bugs/show_bug.cgi?id=196128
 *******************************************************************************/
package com.onpositive.semantic.model.ui.richtext;

import com.onpositive.semantic.model.api.labels.ITextLabelProvider;




public interface IRichLabelProvider extends ITextLabelProvider{

	StyledString getRichTextLabel(Object object);

}
