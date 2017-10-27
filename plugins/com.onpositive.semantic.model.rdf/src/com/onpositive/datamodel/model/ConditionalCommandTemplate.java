package com.onpositive.datamodel.model;

import java.util.Map;

import org.eclipse.core.runtime.Assert;

import com.onpositive.datamodel.impl.IDataStoreRealm;
import com.onpositive.semantic.model.api.command.ICommand;

public final class ConditionalCommandTemplate implements ICommandTemplate {

	private ICommandTemplate result;
	private ITemplateCondition condition;

	public ConditionalCommandTemplate(ICommandTemplate result, ITemplateCondition condition) {
		super();
		this.result = result;
		this.condition = condition;
		Assert.isNotNull(result);
		Assert.isNotNull(condition);
	}

	public final ICommandTemplate getResult() {
		return result;
	}
	
	public final void setResult(ICommandTemplate result) {
		this.result = result;
	}

	public ICommand toCommand(IDataStoreRealm realm,
			Map<String, Object> variables) {
		if (condition.isConditionMet(realm, variables)) {
			return result.toCommand(realm, variables);
		}
		return null;
	}


}
