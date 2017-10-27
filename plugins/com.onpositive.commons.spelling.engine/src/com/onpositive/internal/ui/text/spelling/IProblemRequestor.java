package com.onpositive.internal.ui.text.spelling;

public interface IProblemRequestor {

	public void beginReporting();

	public void endReporting();

	public void acceptProblem(CoreSpellingProblem problem);

}
