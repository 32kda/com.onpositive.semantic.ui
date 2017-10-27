package com.onpositive.semantic.model.binding;

import java.io.Serializable;
import java.util.Collection;

import com.onpositive.commons.xml.language.IHasAdapters;
import com.onpositive.semantic.model.api.expressions.IEditableExpression;
import com.onpositive.semantic.model.api.expressions.IExpressionEnvironment;
import com.onpositive.semantic.model.api.expressions.IListenableExpression;
import com.onpositive.semantic.model.api.labels.ILabelLookup;
import com.onpositive.semantic.model.api.labels.NotFoundException;
import com.onpositive.semantic.model.api.property.IFunction;
import com.onpositive.semantic.model.api.realm.IRealm;
import com.onpositive.semantic.model.api.status.IHasStatus;
import com.onpositive.semantic.model.api.validation.IValidationContext;

public interface IBinding extends IHasStatus, IListenableExpression<Object>, IEditableExpression<Object>,
		IExpressionEnvironment,IHasAdapters,IValidationContext,Serializable {

	String getId();

	String getName();

	String getDescription();

	void removeBindingChangeListener(IBindingChangeListener<?> listener);

	void addBindingChangeListener(IBindingChangeListener<?> listener);
	
	public void addCommitListener(ICommitListener l) ;

	public void removeCommitListener(ICommitListener l) ;

	void setValue(Object value, IBindingChangeListener<?> ignoreListener);

	Object getObject(); 

	Object getUnconvertedValue(); //XXX: What does it mean? Get some uncorverted value? What means "unconverted"?

	Class<?> getSubjectClass();

	boolean isDirty();

	boolean isReadOnly();

	boolean isRequired();
	
	boolean allowsMultiValues();

	boolean isUnique();

	void dispose();

	IRealm<Object> getRealm();

	Object lookupByLabel(String label) throws NotFoundException;

	Collection<Object> lookupByLabels(Collection<String> labels)
			throws NotFoundException;

	ILabelLookup getLabelLookup();

	IFunction getElementFactory();

	<T> T getAdapter(Class<T> class1);

	IBinding getParent();

	String getRole();

	String externalizeString(String value); //Why binding is responsible for this?

	String getWhyBindingIsDisabled();

	public void commit();

	void actionPerformed(Object eObject, Object extras);

	IBinding getRoot();

	boolean isStatic();

	void notifyPossibleChange();

	public Object getUndoContext();

	void setUndoContext(Object undoContext);

	void refresh();
	
	public IListenableExpression<Object> getBinding(String path);

	IBinding binding(String string);

	boolean isWorkingCopiesEnabled();

	void setWorkingCopiesEnabled(boolean m);
}