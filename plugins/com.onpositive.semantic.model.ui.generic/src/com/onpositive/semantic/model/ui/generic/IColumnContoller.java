package com.onpositive.semantic.model.ui.generic;

public interface IColumnContoller {

	void setText(String caption);

	void setTooltipText(String description);

	void setMovable(boolean movable);

	void setResizable(boolean resizable);

	void setImage(String image);

	int getWidth();

	void setLayoutData(ColumnLayoutData layoutData);

	void initEditing(Column lmn);
}
