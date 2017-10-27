package com.onpositive.businessdroids.model.groups;

import java.util.List;

import com.onpositive.businessdroids.model.TableModel;
import com.onpositive.businessdroids.ui.dataview.Group;
import com.onpositive.businessdroids.ui.dataview.persistence.ISaveable;

public interface IGroupingCalculator extends ISaveable {
	public List<Group> calculateGroups(TableModel tableModel);

}
