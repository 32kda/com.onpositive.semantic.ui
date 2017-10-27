package com.onpositive.semantic.model.api.property.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.onpositive.semantic.model.api.property.java.annotations.meta.MetaProperty;
import com.onpositive.semantic.model.api.undo.UndoMetaUtils;

@Retention(RetentionPolicy.RUNTIME)
@MetaProperty(key=UndoMetaUtils.UNDO_ALLOWED,boolValue=false)
public @interface NoUndo {

}
