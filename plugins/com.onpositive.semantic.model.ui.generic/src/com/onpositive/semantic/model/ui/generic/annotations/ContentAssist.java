package com.onpositive.semantic.model.ui.generic.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.meta.DefaultMetaKeys;
import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.property.java.annotations.meta.ProvidesService;
import com.onpositive.semantic.model.ui.generic.IContentAssistConfiguration;

@Retention(RetentionPolicy.RUNTIME)
/**
 * allows to supply content assist configuration for a property
 */
public @interface ContentAssist {

	@MetaProperty(key = DefaultMetaKeys.CONTENT_ASSIST_CONFIG_KEY, createInstance = true)
	@ProvidesService(IContentAssistConfiguration.class)
	Class<? extends IContentAssistConfiguration> value();
}
