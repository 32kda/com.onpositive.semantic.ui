/**
 * 
 */
package com.onpositive.commons.contentassist;

import org.eclipse.jface.fieldassist.IContentProposal;

public final class BasicContentProposal implements IContentProposal,
		Comparable<IContentProposal> {
	private final String ds;
	private final String text2;
	private final String replace;
	private final int cpos;

	private Object element;

	public BasicContentProposal(String replace, String caption,
			String description, int cpos, Object element) {
		this.ds = description;
		this.text2 = caption;
		this.replace = replace;
		this.cpos = cpos;
		this.setElement(element);
	}

	public String getContent() {
		return this.replace;
	}

	public int getCursorPosition() {
		return this.cpos;
	}

	public String getDescription() {
		return this.ds;
	}

	public String getLabel() {
		return this.text2;
	}

	public int compareTo(IContentProposal o) {
		if (o instanceof BasicContentProposal){
			BasicContentProposal other=(BasicContentProposal) o;
			Object o1=other.getElement();
			if (this.element!=null&&o1!=null){
				if (this.element instanceof Comparable<?>){
					if (o1 instanceof Comparable<?>){
						try{
							return ((Comparable)this.element).compareTo(o1);
						}catch (Exception e) {
							
						}
					}
				}
			}
		}
		return this.getLabel().compareToIgnoreCase(o.getLabel());
	}

	public void setElement(Object element) {
		this.element = element;
	}

	public Object getElement() {
		return this.element;
	}

}