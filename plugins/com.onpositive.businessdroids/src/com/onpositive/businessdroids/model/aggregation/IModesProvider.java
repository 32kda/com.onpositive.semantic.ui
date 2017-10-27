package com.onpositive.businessdroids.model.aggregation;

import java.util.Map;

import com.onpositive.businessdroids.ui.dataview.persistence.ISaveable;


public interface IModesProvider extends ISaveable {

	void setMode(int mode);

	int getMode();

	Map<String, Integer> getSupportedModes();
}
