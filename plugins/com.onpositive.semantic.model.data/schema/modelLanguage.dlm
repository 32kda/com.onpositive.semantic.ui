<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<namespace description="" name="DataModel" url="http://www.jetface.org/datamodel">
    <element allowsChilds="propsetting" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="annotatedElement" name="property">
        <property description="" name="range" type="string"/>
        <property description="" name="domain" type="string"/>
        <property description="" name="maxCardinality" type="integerOrStar"/>
        <property description="" name="minCardinality" type="integer"/>
        <property description="" name="defaultValue" type="string"/>
        <property description="" name="groupValueProvider" type="java"/>
        <property description="" name="unique" type="boolean"/>
        <property description="" name="storageType" type="string"/>
    </element>
    <element allowsChilds="classsetting" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="annotatedElement" name="class">
        <property description="" name="superClasses" type="string"/>
        <property description="" name="broadCastChanges" type="boolean"/>
    </element>
    <element allowsChilds="" class="" description="" extends="" isAbstract="true" name="annotatedElement">
        <property description="" name="caption" required="true" translatable="true" type="string"/>
        <property description="" name="description" translatable="true" type="string"/>
        <property description="" name="id" required="true" type="string"/>
    </element>
    <element allowsChilds="annotatedElement" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="" name="model">
        <property description="" name="namespace" type="string"/>
    </element>
    <element allowsChilds="" class="" description="" extends="" isAbstract="true" name="classsetting"/>
    <element allowsChilds="" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="classsetting" isAbstract="true" name="regexp-constraint">
        <property description="" name="pattern" required="true" type="regexp"/>
        <property description="" name="description" translatable="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="classsetting" name="range-constraint">
        <property description="" name="description" translatable="true" type="string"/>
        <property description="" name="min" type="number"/>
        <property description="" name="max" type="number"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="classsetting" name="realm-provider">
        <property description="" name="class" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="property" name="calculatableProperty">
        <property description="" name="class" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="classsetting" name="instanceListener">
        <property description="" name="class" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.model.data.DataModelEvaluator" description="" extends="property" name="inverseProperty">
        <property description="" name="of" required="true" type="string"/>
    </element>
</namespace>
