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
 * Copied from JDT UI: org.eclipse.jdt.internal.ui.viewsupport.ColoredString.
 * Will be removed again when made API. https://bugs.eclipse.org/bugs/show_bug.cgi?id=196128
 *******************************************************************************/
package com.onpositive.semantic.model.ui.richtext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("unchecked")
public class StyledString implements Comparable<StyledString>,Serializable {

	public static class Style implements Serializable{
		private final String fForegroundColorName;
		private final String fBackgroundColorName;
		private int fontStyle;
		private boolean underline;
		private boolean strikeout;
		

		public Style(String foregroundColorName, String backgroundColorName) {
			this.fForegroundColorName = foregroundColorName;
			this.fBackgroundColorName = backgroundColorName;
		}

		public String getForegroundColorName() {
			return this.fForegroundColorName;
		}

		public String getBackgroundColorName() {
			return this.fBackgroundColorName;
		}

		public boolean isUnderline() {
			return this.underline;
		}

		public void setUnderline(boolean underline) {
			this.underline = underline;
		}

		public boolean isStrikeout() {
			return this.strikeout;
		}

		public void setStrikeout(boolean strikeout) {
			this.strikeout = strikeout;
		}

		public int getFontStyle() {
			return this.fontStyle;
		}

		public void setFontStyle(int fontStyle) {
			this.fontStyle = fontStyle;
		}

		public void setStrikeThrough(boolean b) {
			this.strikeout=b;
		}
	}

	public static final Style DEFAULT_STYLE = null;

	private final StringBuilder fBuffer;

	private ArrayList fRanges;

	public StyledString() {
		this.fBuffer = new StringBuilder();
		this.fRanges = null;
	}

	public StyledString(String text) {
		this(text, StyledString.DEFAULT_STYLE);
	}

	public StyledString(String text, Style style) {
		this();
		this.append(text, style);
	}
	
	public void trimToLength(int length){
		fBuffer.setLength(length);
	}

	public String getString() {
		return this.fBuffer.toString();
	}

	public int length() {
		return this.fBuffer.length();
	}

	public Iterator getRanges() {
		if (!this.hasRanges()) {
			return Collections.EMPTY_LIST.iterator();
		}
		return this.getRangesList().iterator();
	}

	public StyledString append(String text) {
		return this.append(text, DEFAULT_STYLE);
	}

	public StyledString append(char ch) {
		return this.append(String.valueOf(ch), DEFAULT_STYLE);
	}

	public StyledString append(StyledString string) {
		final int offset = this.fBuffer.length();
		this.fBuffer.append(string.getString());
		for (final Iterator iterator = string.getRanges(); iterator.hasNext();) {
			final StyleRange curr = (StyleRange) iterator.next();
			this.addRange(new StyleRange(offset + curr.offset, curr.length,
					curr.style));
		}
		return this;
	}

	public StyledString append(String text, Style style) {
		if (text==null){
			text="null";
		}
		if (text.length() == 0) {
			return this;
		}

		final int offset = this.fBuffer.length();
		this.fBuffer.append(text);
		if (style != null) {
			final int nRanges = this.getNumberOfRanges();
			if (nRanges > 0) {
				final StyleRange last = this.getRange(nRanges - 1);
				if ((last.offset + last.length == offset)
						&& style.equals(last.style)) {
					last.length += text.length();
					return this;
				}
			}
			this.addRange(new StyleRange(offset, text.length(), style));
		}
		return this;
	}

	public void colorize(int offset, int length, Style style) {
		if ((offset < 0) || (offset + length > this.fBuffer.length())) {
			throw new IllegalArgumentException(
					"Invalid offset (" + offset + ") or length (" + length + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		}

		int insertPos = 0;
		final int nRanges = this.getNumberOfRanges();
		for (int i = 0; i < nRanges; i++) {
			final StyleRange curr = this.getRange(i);
			if (curr.offset + curr.length <= offset) {
				insertPos = i + 1;
			}
		}
		if (insertPos < nRanges) {
			int size = this.fRanges.size();
			ArrayList<StyleRange>q=new ArrayList<StyleRange>();
			for (int a=0;a<size;a++){
				StyleRange object = (StyleRange) this.fRanges.get(a);
				if (object.offset+object.length<offset){
					q.add(object);
					continue;
				}
				if (object.offset>offset+length){
					StyleRange r1=new StyleRange(offset,length,style);					
					q.add(r1);
					q.add(object);
					continue;
				}
				if (offset-object.offset>0){
					StyleRange r1=new StyleRange(object.offset,offset-object.offset, object.style);
					q.add(r1);
				}
				if (offset<object.offset+object.length){
					StyleRange r1=new StyleRange(offset,Math.min(object.offset+object.length-offset,length), style);					
					q.add(r1);
				}
				if (object.offset+object.length>offset+length){
					StyleRange r1=new StyleRange(offset+length,object.offset+object.length,object.style);
					q.add(r1);
				}
				
			}
			this.fRanges=q;
			return;
		}
		this.addRange(insertPos, new StyleRange(offset, length, style));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.fBuffer.toString();
	}

	private boolean hasRanges() {
		return (this.fRanges != null) && !this.fRanges.isEmpty();
	}

	private int getNumberOfRanges() {
		return this.fRanges == null ? 0 : this.fRanges.size();
	}

	private StyleRange getRange(int index) {
		if (this.fRanges != null) {
			return (StyleRange) this.fRanges.get(index);
		}
		throw new IndexOutOfBoundsException();
	}

	private void addRange(StyleRange range) {
		this.getRangesList().add(range);
	}

	private void addRange(int index, StyleRange range) {
		this.getRangesList().add(index, range);
	}

	private List getRangesList() {
		if (this.fRanges == null) {
			this.fRanges = new ArrayList(2);
		}
		return this.fRanges;
	}

	public static class StyleRange {
		public int offset;
		public int length;
		public Style style;

		public StyleRange(int offset, int length, Style style) {
			this.offset = offset;
			this.length = length;
			this.style = style;
		}
	}

	public int compareTo(StyledString o) {
		return this.fBuffer.toString().compareTo(o.fBuffer.toString());
	}

	public void append(String text, String string) {
		this.append(text,new Style(string,null));
	}
}
