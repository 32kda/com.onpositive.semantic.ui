package com.onpositive.businessdroids.model.groups;

import com.onpositive.businessdroids.model.IField;

public interface IFieldGroupingCalculator extends IGroupingCalculator, IHasId {
	public IField getGroupField();
	public void setGroupField(IField field);
}
