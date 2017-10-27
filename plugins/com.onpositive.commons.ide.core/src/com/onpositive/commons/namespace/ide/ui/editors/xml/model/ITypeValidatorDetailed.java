package com.onpositive.commons.namespace.ide.ui.editors.xml.model;

import org.eclipse.core.resources.IProject;

public interface ITypeValidatorDetailed {
	
	static class ErrorInfo{
		
		String message ;
		int offset ;
		int length ;
		
		public final String getMessage() {
			return message;
		}
		public final void setMessage(String message) {
			this.message = message;
		}
		public final int getOffset() {
			return offset;
		}
		public final void setOffset(int offset) {
			this.offset = offset;
		}
		public final int getLength() {
			return length;
		}
		public final void setLength(int length) {
			this.length = length;
		}

	}
	
	public ErrorInfo[] getErrors( IProject project,String value, DomainEditingModelObject element, String typeSpecialization ) ;

}



