package com.onpositive.semantic.model.api.query;

public interface IQueryPreprocessorProvider {

	IQueryPreProcessor[] getPreprocessors(Query q);
}
