package com.onpositive.semantic.ui.core;

import java.io.Serializable;

import com.onpositive.commons.xml.language.CustomAttributeHandler;
import com.onpositive.commons.xml.language.HandlesAttributeDirectly;


public class GenericLayoutHints implements Serializable{

	private Boolean grabHorizontal;
	private Boolean grabVertical;

	private Point span;
	private Point indent;
	private Point minimumSize;
	private Point hint;

	private Integer alignmentHorizontal;
	private Integer alignmentVertical;
	private Boolean gridy;

	public Boolean getGrabHorizontal() {
		Boolean grabHorizontal2 = this.grabHorizontal;
		if (grabHorizontal2==null){
			return false;
		}
		return grabHorizontal2;
	}

	@HandlesAttributeDirectly("grabHorizontal")
	public void setGrabHorizontal(Boolean grabHorizontal) {
		this.grabHorizontal = grabHorizontal;
	}

	public Boolean getGrabVertical() {
		Boolean grabVertical2 = this.grabVertical;
		if (grabVertical==null){
			return false;
		}
		return grabVertical2;
	}

	@HandlesAttributeDirectly("grabVertical")
	public void setGrabVertical(Boolean grabVertical) {

		this.grabVertical = grabVertical;
	}

	public Point getSpan() {
		return this.span;
	}

	@HandlesAttributeDirectly("span")
	public void setSpan(Point span) {
		this.span = span;
	}

	public Point getIndent() {
		return this.indent;
	}

	@HandlesAttributeDirectly("indent")
	public void setIndent(Point indent) {
		this.indent = indent;
	}

	public Integer getAlignmentHorizontal() {
		return this.alignmentHorizontal;
	}

	/**
	 * alignmentHorizontal specifies how controls will be positioned horizontally within a cell. 
	 * Alignment constants are the same, as for SWT GridLayout
	 * The default value is BEGINNING. Possible values are: 
	 * <li>SWT.BEGINNING (or SWT.LEFT): Position the control at the left of the cell 
	 * <li>SWT.CENTER: Position the control in the horizontal center of the cell 
	 * <li>SWT.END (or SWT.RIGHT): Position the control at the right of the cell 
	 * <li>SWT.FILL: Resize the control to fill the cell horizontally 
	 * @param alignmentHorizontal alignment constant to use
	 */
	@CustomAttributeHandler(value="hAlign",handler=AlignmentAttributeHandler.class)
	public void setAlignmentHorizontal(Integer alignmentHorizontal) {
		
		this.alignmentHorizontal = alignmentHorizontal;
		
	}

	public Integer getAlignmentVertical() {
		return this.alignmentVertical;
	}

	@CustomAttributeHandler(value="vAlign",handler=AlignmentAttributeHandler.class)
	public void setAlignmentVertical(Integer alignmentVertical) {
		this.alignmentVertical = alignmentVertical;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.alignmentHorizontal == null) ? 0 : this.alignmentHorizontal
						.hashCode());
		result = prime
				* result
				+ ((this.alignmentVertical == null) ? 0 : this.alignmentVertical
						.hashCode());
		result = prime * result
				+ ((this.grabHorizontal == null) ? 0 : this.grabHorizontal.hashCode());
		result = prime * result
				+ ((this.grabVertical == null) ? 0 : this.grabVertical.hashCode());
		result = prime * result + ((this.indent == null) ? 0 : this.indent.hashCode());
		result = prime * result + ((this.span == null) ? 0 : this.span.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final GenericLayoutHints other = (GenericLayoutHints) obj;
		if (this.alignmentHorizontal == null) {
			if (other.alignmentHorizontal != null) {
				return false;
			}
		} else if (!this.alignmentHorizontal.equals(other.alignmentHorizontal)) {
			return false;
		}
		if (this.alignmentVertical == null) {
			if (other.alignmentVertical != null) {
				return false;
			}
		} else if (!this.alignmentVertical.equals(other.alignmentVertical)) {
			return false;
		}
		if (this.grabHorizontal == null) {
			if (other.grabHorizontal != null) {
				return false;
			}
		} else if (!this.grabHorizontal.equals(other.grabHorizontal)) {
			return false;
		}
		if (this.grabVertical == null) {
			if (other.grabVertical != null) {
				return false;
			}
		} else if (!this.grabVertical.equals(other.grabVertical)) {
			return false;
		}
		if (this.indent == null) {
			if (other.indent != null) {
				return false;
			}
		} else if (!this.indent.equals(other.indent)) {
			return false;
		}
		if (this.span == null) {
			if (other.span != null) {
				return false;
			}
		} else if (!this.span.equals(other.span)) {
			return false;
		}
		return true;
	}



	public Point getMinimumSize() {
		return this.minimumSize;
	}

	@HandlesAttributeDirectly("minimumSize")
	public void setMinimumSize(Point minimumSize) {
		this.minimumSize = minimumSize;
	}

	public Point getHint() {
		return this.hint;
	}

	@HandlesAttributeDirectly("hint")
	public void setHint(Point hint) {
		this.hint = hint;
	}

	public Boolean getGridy() {
		return this.gridy;
	}

	public void setGridy(Boolean gridy) {
		this.gridy = gridy;
	}
	
	public boolean grabHorizontal() {
		return grabHorizontal != null && grabHorizontal;
	}

	public boolean grabVertical() {
		return grabVertical != null && grabVertical;
	}
	
}
