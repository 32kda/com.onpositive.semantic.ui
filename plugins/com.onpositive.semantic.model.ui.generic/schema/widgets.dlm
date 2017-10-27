<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<namespace description="This namespace contains useful widgets" name="widgets" url="http://jetface.org/JetFace1.0/widgets">
    <element allowsChilds="wizardPage" class="com.onpositive.semantic.ui.xml.WizardElementHandler" description="Allows to create wizard" extends="dialog" name="wizard">
        <property description="should implement IWizardListener interface" name="listener" type="java"/>
        <property description="" name="helpContext" type="string"/>
        <property description="" name="dialogSettingsId" type="string"/>
    </element>
    <element allowsChilds="http://jetface.org/JetFace1.0/uielement" class="com.onpositive.semantic.ui.xml.WizardPageHandler" description="Allows to create wizard page in wizard" extends="" name="wizardPage">
        <property description="" name="title" required="true" type="string"/>
        <property description="" name="message" required="true" type="string"/>
        <property description="should implement IWizardPageListener interface" name="listener" type="java"/>
        <property description="" name="helpContext" type="string"/>
    </element>
    <element allowsChilds="http://jetface.org/JetFace1.0/uielement" class="com.onpositive.semantic.ui.xml.DialogElementHandler" description="Allows to create titled dialog" extends="" name="dialog">
        <property description="" name="title" type="string"/>
        <property description="" name="image" type="imageref"/>
        <property description="" name="message" type="string"/>
        <property description="" name="resizable" type="boolean"/>
        <property description="" name="dialogSettingsId" type="string"/>
        <property description="" name="helpContext" type="string"/>
    </element>
    <element allowsChilds="http://jetface.org/JetFace1.0/uielement" class="com.onpositive.semantic.ui.xml.PopupDialogElementHandler" description="Allows to create Popup Dialog" extends="" name="popupDialog">
        <property description="" name="title" type="string"/>
        <property description="" name="description" type="string"/>
    </element>
    <element allowsChilds="http://jetface.org/JetFace1.0/form" class="com.onpositive.semantic.ui.xml.FormDialogElementHandler" description="Allows to create Form Dialog" extends="" name="formDialog">
        <property description="" name="title" type="string"/>
        <property description="" name="image" type="imageref"/>
        <property description="" name="description" type="string"/>
        <property description="" name="flags" type="string"/>
    </element>
</namespace>
