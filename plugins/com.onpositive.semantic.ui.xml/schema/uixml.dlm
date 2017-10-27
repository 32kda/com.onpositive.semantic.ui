<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<namespace description="This namespace contains core jetface elements" name="jetface" url="http://jetface.org/JetFace1.0/">
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="abstract super element for all UI controls" extends="" group="controls" isAbstract="true" name="uielement">
        <property description="Vertical alignment of the element (is taken in account by auto layouts) valid values are: fill,top,bottom,center" name="vAlign" type="enum" typeSpecialization="top,bottom,center,fill"/>
        <property description="Horizontal alignment of the element (is taken in account by auto layouts)" name="hAlign" type="enum" typeSpecialization="left,center,right,fill"/>
        <property description="if set to true component will grab horizontal space if it is placed in container with automatic layout management" name="grabHorizontal" type="boolean"/>
        <property description="if set to true component will grab vertical space if it is placed in container with automatic layout management" name="grabVertical" type="boolean"/>
        <property description="allows to setup how many cells container will take in container with automatic layout " name="span" type="point"/>
        <property description="allows to setup indentation(x indentation, y indentation) of a component in the container will take in container with automatic layout " name="indent" type="point"/>
        <property description="Desired size for the component(width,height)" name="hint" type="point"/>
        <property description="Minimum size for the component(width,height)" name="minimumSize" type="point"/>
        <property description="text to show in the component(may be interpreted differently)" name="text" translatable="true" type="string"/>
        <property description="label for the component(may be interpreted differently depending of component implementation)" name="caption" translatable="true" type="string"/>
        <property description="background attribute allows to specify background color from JFace color registry that will be used for the element" name="background" type="color"/>
        <property description="background image allows to specify background image that will be used for the element value should be identifier of the image declared using 'com.onpositive.semantic.ui.images' extension point" name="background-image" type="image"/>
        <property description="font allows to specify font that will be used for the element(expect key that may be mapped to font in JFace font registry)" name="font" type="font"/>
        <property description="theme allows to specify theme that will be used for the element" name="theme" type="string"/>
        <property description="role allows to specify role that will be used for the element" name="role" type="string"/>
        <property description="tooltip text that should be shown when user hovers the element" name="tooltip-text" type="string"/>
        <property description="foreground allows to specify foreground color from JFace color registry that will be used for the element" name="foreground" type="color"/>
        <property description="id attribute allows to refer on it from external context or from same 'dlf' file" name="id" type="string"/>
        <property description="allows to specify expression that controls the should be given element enabled or not" name="enablement" type="string"/>
        <property description="Controls conditions when element is visible (not visible elements are removed from layout and did not consume space)" group="com.onpositive.ide.ui.layout" name="visibility" type="expression"/>
    </element>
    <element allowsChilds="true" class="" description="abstract super elements of all elements that may be binded to some binding in the data model" extends="uielement" group="controls" isAbstract="true" name="editor">
        <property description="Allows to connect editor with data model" name="bindTo" type="binding"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="viewer element is abstract super element of all viewers. " extends="editor" group="controls" isAbstract="true" name="viewer">
        <property description="if 'false' specified as a value of this element viewer will not attempt to maintain selection during viewer refresh" name="elementRole" type="string"/>
        <property description="if 'false' specified as a value of this element viewer will not attempt to maintain selection during viewer refresh" name="persist-selection" type="boolean"/>
    </element>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="basic container for UI controls" extends="uielement" group="controls" name="container">
        <property description="" name="margin" type="rectangle"/>
        <property description="" name="hasBorder" type="boolean"/>
    </element>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.StackElementHandler" description="Stack container shows first enabled child, it listens to element enablement and is able to adapt to enablement changes" extends="container" group="controls" name="stack"/>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="simple text field, supports validation and content assist " extends="editor" group="controls" name="string">
        <property description="" name="showhoverOnError" type="boolean"/>
        <property description="" name="contentAssistRole" type="string"/>
        <property description="" name="installRequiredDecoration" type="boolean"/>
        <property description="allows to specify that this string field should be shown as read only" name="readonly" type="boolean"/>
        <property description="string that contains characters that may separate values" name="separatorCharacters" type="string"/>
        <property description="if true label provider is used for null" name="useLabelsForNull" type="boolean"/>
        <property description="Used to specify button and classname for it's handler class. Will create button to the right jf the string. Handler class must implement IFactory interface." name="buttonSelector" type="java"/>
    </element>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to add combo editor to the parent container" extends="viewer" group="controls" name="combo">
        <property description="string that contains characters that may separate values" name="separatorCharacters" type="string"/>
        <property description="if true label provider is used for null" name="useLabelsForNull" type="boolean"/>
        <property description="" name="selectDefault" type="boolean"/>
    </element>
    <element allowsChilds="setting,decorator,tooltip-creator,contentProvider,interceptor" class="com.onpositive.semantic.ui.xml.ListSelectionHandler" description="allows to insert a list control to the parent container" extends="viewer" group="controls" name="list">
        <property description="allows to bind selection to given binding" name="bindSelectionTo" type="bingingReference"/>
        <property description="controls if this list should have a border around it" name="hasBorder" type="boolean"/>
        <property description="if set to true viewer will be represented as checkable tree or as checkable list" name="asCheckBox" type="boolean"/>
        <property description="if set to true viewer will be represented as 'tree', if set to 'false' it will be represented as list" name="asTree" type="boolean"/>
        <property description="if set to true viewer items will have width exactly equal to width of viewer" name="fitHorizontal" type="boolean"/>
        <property description="" name="linkSelectionWith" type="binding"/>
        <property description="" name="useSelectionAsValue" type="boolean"/>
        <property description="" name="enableDirectEdit" type="boolean"/>
        <property description="" name="directEditProperty" type="string"/>
        <property description="" name="rowStyleProvider" type="java"/>
        <property description="" name="isOrdered" type="boolean"/>
        <property description="" name="openOnDoubleClick" type="boolean"/>
        <property description="" name="labelProvider" type="java"/>
        <property description="Binding to method to execute on open" name="onOpen" type="binding"/>
    </element>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="'vc' element allows to define container with children that will be situated on a one per line basis" extends="container" group="controls" name="vc"/>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="'hc' element allows to define container with children that will be situated on one line'hc' element allows to define container with children that will be situated on a one per line basis" extends="container" group="controls" name="hc"/>
    <element allowsChilds="uielement,model,setting,headControl" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to create form" extends="container,editor" group="controls" name="form">
        <property description="" name="showValueInTitle" type="boolean"/>
    </element>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to add spinner editor to parent container" extends="editor" group="controls" name="spinner"/>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Description is not specified" extends="editor" group="controls" name="button">
        <property description="" name="image" type="image"/>
    </element>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to insert text label to the parent control" extends="editor" group="controls" name="label">
        <property description="" name="richContent" type="boolean"/>
        <property description="" name="hyperLinkListener" type="java"/>
    </element>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to describe used data model " extends="abstractBinding" group="Model" name="model"/>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Binding element allows to create point for data binding " extends="abstractBinding" group="Model" name="binding">
        <property description="" name="caption" translatable="true" type="string"/>
        <property description="" name="undo-context" type="string"/>
        <property description="" name="addParentToScope" type="boolean"/>
        <property description="" name="path" type="string"/>
        <property description="" name="id" required="true" type="string"/>
        <property description="" name="required" type="boolean"/>
        <property description="" name="autoCommit" type="boolean"/>
        <property description="" name="description" translatable="true" type="string"/>
        <property description="" name="minCardinality" type="integer"/>
        <property description="" name="maxCardinality" type="integer"/>
        <property description="" name="readonly" type="boolean"/>
        <property description="if this attribute is has false value this binding will not produce undoable commands" name="enableUndo" type="boolean"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to specify a realm for the given binding" extends="bindingMember" group="Model" name="realm">
        <property description="" name="class" required="true" type="java" typeSpecialization="com.onpositive.semantic.model.api.property.adapters.IRealmProvider"/>
    </element>
    <element allowsChilds="" class="" description="abstract super elements for all elements that may be placed in the binding" extends="" group="Model" isAbstract="true" name="bindingMember"/>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to commit data in the parent binding" extends="abstractBinding" group="Actions" name="commit"/>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.DeleteElementsUIFactory" description="allows to delete selected data objects" extends="abstractBinding" group="Actions" name="delete-selected">
        <property description="" name="targetId" required="true" type="id"/>
        <property description="" name="confirmTitle" translatable="true" type="string"/>
        <property description="" name="confirmDescription" translatable="true" type="string"/>
    </element>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.AddElementsUIFactory" description="allows to add new object to the parent viewer" extends="abstractBinding" group="Actions" name="add-new">
        <property description="" name="targetId" required="true" type="id"/>
        <property description="" name="targetType" type="type"/>
        <property description="" name="theme" type="string"/>
    </element>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to execute sequence of actions" extends="abstractBinding" group="Actions" name="composite-action"/>
    <element allowsChilds="bindingMember" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to close shell were this action is located" extends="abstractBinding" group="Actions" name="close-shell"/>
    <element allowsChilds="" class="" description="abstract super element of all binding elements" extends="bindingMember" group="Model" isAbstract="true" name="abstractBinding">
        <property description="" name="caption" type="string"/>
        <property description="" name="autoCommit" type="boolean"/>
        <property description="" name="registerListeners" type="boolean"/>
        <property description="" name="commitOnErrors" type="boolean"/>
        <property description="" name="enablement" type="expression"/>
        <property description="" name="id" type="identifier"/>
        <property description="" name="class" type="java"/>
        <property description="" name="modelExtension" type="boolean"/>
    </element>
    <element allowsChilds="" class="" description="Allows to specify numeric range for a value" extends="bindingMember" group="Model" name="range">
        <property description="" name="min" type="double"/>
        <property description="" name="max" type="double"/>
        <property description="" name="digits" type="integer"/>
        <property description="" name="increment" type="double"/>
        <property description="" name="pageIncrement" type="double"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Marks that this element property should have unique value" extends="bindingMember" group="Model" name="unique"/>
    <element allowsChilds="uielement,model,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="declares complex editor " extends="container,editor" group="controls" name="composite-editor"/>
    <element allowsChilds="contributionElement,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="this element allows to insert toolbar control in the current container" extends="uielement" group="controls" name="toolbar"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to contribute element" extends="contributionElement" group="Actions" name="binded-action">
        <property description="Allows to connect action with it is real executor" name="bindTo" type="binding"/>
        <property description="allows to set style for action may be 'check'|'radio'|'drop-down' or none" name="style" type="actionStyle"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.DelegatedActionHandler" description="allows to contribute action to current action contribution context. Action &amp;quot;class&amp;quot; attribute must have class, implementing com.onpositive.semantic.model.ui.property.editors.IBindedActionDelegate" extends="binded-action" group="Actions" name="action">
        <property description="" name="class" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="" description="abstract super element of all contribution elements" extends="" group="Actions" isAbstract="true" name="contributionElement">
        <property description="" name="image" type="image"/>
        <property description="" name="disabled-image" type="image"/>
        <property description="" name="hover-image" type="image"/>
        <property description="" name="caption" translatable="true" type="string"/>
        <property description="" name="toMenu" type="boolean"/>
        <property description="" name="toToolbar" type="boolean"/>
        <property description="" name="exportAs" type="string"/>
        <property description="" name="id" type="string"/>
        <property description="" group="events" name="definitionId" type="string"/>
    </element>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Splitter allows to create hozizontally or vertically oriented split pane" extends="container" group="controls" name="splitter">
        <property description="" name="horizontal" type="boolean"/>
        <property description="allows to specify weights for child elements in form like 20,20" name="weights" type="weights"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to setup custom text label provider on a element" extends="bindingMember" group="Model" name="text-label">
        <property description="additional value that is passed to a provider" name="value" translatable="true" type="value"/>
        <property description="class of the provider to use" name="provider" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to specify separator characters for  the parent binding" extends="bindingMember" group="Model" name="separator-characters"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to connect binding to mapper that will map text to object of required class" extends="bindingMember" group="Model" name="label-lookup">
        <property description="" name="value" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to connect binding to factory provider which knows how to produce new objects" extends="bindingMember" group="Model" name="factory-provider">
        <property description="" name="value" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to specify content assist for the binding" extends="bindingMember" group="Model" name="content-assist">
        <property description="" name="value" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="'validator' element allows to install custom value validator  on the binding " extends="bindingMember" group="Model" name="validator">
        <property description="name of the validator class. (class should implement com.onpositive.semantic.model.realm.IValidator)" name="value" required="true" type="java"/>
    </element>
    <element allowsChilds="uielement,model,setting" class="com.onpositive.semantic.ui.xml.SectionHandler" description="this complex editor allows to create section container" extends="container,editor" group="controls" name="section">
        <property description="" name="decorateTitle" type="boolean"/>
        <property description="" name="expanded" type="boolean"/>
        <property description="" name="expandable" type="boolean"/>
        <property description="If set to true presented object label is displayed in title, default value is 'true'" group="controls" name="showValueInTitle" type="boolean"/>
    </element>
    <element allowsChilds="contributionElement" class="com.onpositive.semantic.ui.xml.ToolbarManagerHandler" description="it is needed to investigate what it is for" extends="setting" group="Settings" name="toolbar-manager"/>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Allows user to create tab folder" extends="container" group="controls" name="tab-folder"/>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.CheckboxHandler" description="Allows user to add check box editor to the parent container" extends="editor" group="controls" name="checkbox">
        <property description="allows to specify that this check box should be not click able" name="readonly" type="boolean"/>
    </element>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.CheckboxHandler" description="Allows user to create radio button" extends="editor" group="controls" name="radio"/>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.GroupHandler" description="This element allows to create SWT Group" extends="container" group="controls" name="group"/>
    <element allowsChilds="uielement,setting" class="com.onpositive.semantic.ui.xml.GroupHandler" description="This element allows to create horizontally oriented SWT Group" extends="container" group="controls" name="hgroup"/>
    <element allowsChilds="contributionElement" class="com.onpositive.semantic.ui.xml.PopupHandler" description="Allows to specify popup menu for element" extends="setting" group="Settings" name="popup-menu"/>
    <element allowsChilds="" class="" description="abstract super element for settings" extends="" group="Settings" isAbstract="true" name="setting"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.FilterUIFactory" description="allows to add simple text filter to parent container" extends="uielement" group="controls" name="pattern-filter">
        <property description="" name="targetId" required="true" type="id"/>
        <property description="controls should filter decorate occurences" name="markOccurences" type="boolean"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.DecoratorHandler" description="allows to add decorator to the parent viewer" extends="" group="Settings" name="decorator">
        <property description="class of decorator to add" name="value" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.TooltipCreatorHandler" description="allows to setup custom tooltip creator" extends="" group="Settings" name="tooltip-creator">
        <property description="class of tooltip creator that should be used, must implement 'IInformationalControlContentProducer' interface" name="value" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.RegexpValidatorHandler" description="allows to add validator based on validation of text representation of the data against given regular expression" extends="bindingMember" group="Model" name="regexp-validator">
        <property description="regexp to be validated" name="regexp" required="true" type="regexp"/>
        <property description="description of validation error" name="message" required="true" translatable="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="this control will be described later" extends="container,editor" group="controls" name="properties"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="will be described later" extends="abstractBinding" group="Settings" name="data-source">
        <property description="" name="bindingProvider" required="true" type="extension" typeSpecialization="com.onpositive.semantic.model.bindingProvider/bindingProvider"/>
        <property description="" name="dataUrl" type=""/>
        <property description="" name="id" required="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to specify external realm for the parent binding" extends="bindingMember" group="Model" name="external-realm" restrictedAttrs="class">
        <property description="" name="provider" required="true" type="extension" typeSpecialization="com.onpositive.semantic.model.realmProvider/realmProvider"/>
        <property description="" name="url" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.CommandHandler" description="allows to declare handler for a command (eclipse commands)" extends="setting" group="Settings" name="command-handler">
        <property description="" name="command" required="true" translatable="true" type="commandId"/>
        <property description="" name="bindTo" required="true" translatable="true" type="binding"/>
    </element>
    <element allowsChilds="contributionElement" class="com.onpositive.semantic.ui.xml.ActionsElementHandler" description="allows to specify a group of actions that should be contributed to parent action context" extends="setting" group="Model" name="actions">
        <property description="" name="toToolbar" type="boolean"/>
        <property description="" name="toMenu" type="boolean"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.AddFromRealmHandler" description="allows to add action which allows addition of elements from realm to the parent viewer" extends="contributionElement" group="Actions" name="add-fromRealm">
        <property description="" name="widgetId" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.AddToRealmHandler" description="allows to add new data object to parent viewer" extends="contributionElement" group="Actions" name="create-member">
        <property description="" name="targetType" type="type"/>
        <property description="" name="objectClass" type="java"/>
        <property description="" name="themeId" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.OpenHandler" description="allows to start editing of the selected element" extends="contributionElement" group="Actions" name="edit-selection">
        <property description="" name="directEdit" type="boolean"/>
        <property description="" name="theme" type="string"/>
        <property description="" name="role" type="string"/>
        <property description="" name="widgetId" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.RemoveSelectedHandler" description="allows to remove selected elements from the parent viewer" extends="contributionElement" group="Actions" name="remove-selection">
        <property description="" name="confirmTitle" translatable="true" type="string"/>
        <property description="" name="confirmDescription" translatable="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UndoRedoHandler" description="This block allows contribute 'undo' and 'redo' actions to the current context" extends="contributionElement" group="Actions" name="undoredo-block" restrictedAttrs="definitionId"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.SelectAllHandler" description="this action allows to select all elements in the parent viewer" extends="contributionElement" group="Actions" name="selectAll"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.GroupByHandler" description="allows to remove grouping from the parent viewer" extends="contributionElement" group="Actions" name="doNotGroup"/>
    <element allowsChilds="labelProvider" class="com.onpositive.semantic.ui.xml.GroupByHandler" description="allows to create group by action in the parent action context (should be used only inside list or table)" extends="contributionElement" group="Actions" name="groupBy">
        <property description="" name="propertyId" required="true" type="string"/>
        <property description="" name="propogate" type="boolean"/>
        <property description="" name="presentationFactory" type="java"/>
        <property description="" name="lockFirstColumnTo" type="string"/>
        <property description="" name="useRealmFromBinding" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.DeSelectAllHandler" description="allows to deselect all elements in the parent viewer" extends="contributionElement" group="Actions" name="deselectAll"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.ToggleFilterHandler" description="allows to specify boolean filter action, parent control must implement IFiltrable interface" extends="contributionElement" group="Actions" name="toggle-filter">
        <property description="class of filter to create, class must implement com.onpositive.semantic.model.realm.IFilter interface" name="class" required="true" type="class"/>
        <property description="" name="value" type="boolean"/>
    </element>
    <element allowsChilds="setting,decorator,tooltip-creator,columns,contentProvider,interceptor" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="table element allows to insert table control in the parent container" extends="list" group="controls" name="table">
        <property description="if 'imageOnFirstColumn' attribute is specified to true table will always have a image on first column and this image will be calculated basing on row objects" name="imageOnFirstColumn" type="boolean"/>
        <property description="'linesVisible' allows to control visibility of  lines that separate cells in the table" name="linesVisible" type="boolean"/>
        <property description="'headerVisible' attribute allows to control visibility of the table header" name="headerVisible" type="boolean"/>
    </element>
    <element allowsChilds="column" class="com.onpositive.semantic.ui.xml.ColumnsElementHandler" description="column that are represented in the parent table should be described inside this element" extends="" group="Settings" name="columns"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.ColumnsElementHandler" description="specifies column in the table" extends="" group="Settings" name="column">
        <property description="" name="caption" translatable="true" type="string"/>
        <property description="" name="id" type="id"/>
        <property description="" name="icon" type="icon"/>
        <property description="" name="movable" type="boolean"/>
        <property description="" name="imageFromBase" type="boolean"/>
        <property description="" name="textFromBase" type="boolean"/>
        <property description="" name="resizable" type="boolean"/>
        <property description="" name="description" translatable="true" type="string"/>
        <property description="" name="resizeWeight" type="int"/>
        <property description="" name="initialWidth" type="int"/>
        <property description="" name="role" type="string"/>
        <property description="" name="theme" type="role"/>
        <property description="allows to setup cell editor factory for a column" name="cellEditorFactory" type="java"/>
        <property description="" name="hasImage" type="boolean"/>
        <property description="" name="imageProvider" type="java" typeSpecialization="com.onpositive.semantic.model.api.roles.IImageDescriptorProvider"/>
        <property description="" name="hasText" type="boolean"/>
        <property description="" name="textLabelProvider" type="java" typeSpecialization="com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.LabelProviderHandler" description="allows to specify custom label provider for the parent viewer" extends="" group="Settings" name="labelProvider">
        <property description="" name="class" required="true" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to create control which may be used to represent text and supports configuration using org.eclipse.jface.text.source.SourceViewerConfiguration objects" extends="editor" group="controls" name="text">
        <property description="if 'multiline' attribute is specified to true' control will allow user to type multiple lines" name="multiline" type="boolean"/>
        <property description="this attribute should point to class implementing IViewerConfigurator interface and allows to setup custom configuration on the text viewer" name="sourceViewerConfigurator" type="java"/>
        <property description="if wrap text attribute is specified to  'true' control will wrap text" name="wrapText" type="boolean"/>
        <property description="" name="readonly" type="boolean"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.ContentProviderHandler" description="allows to specify custom content provider for the parent viewer" extends="" group="Settings" name="contentProvider">
        <property description="" name="class" type="java"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.DateTimeHandler" description="allows to add date time control to the parent container" extends="editor" group="controls" name="date">
        <property description="" name="size" required="true" type="string"/>
        <property description="" name="type" required="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.LinkHandler" description="allows to add link control to the parent container" extends="uielement" group="controls" name="link">
        <property description="" name="url" required="true" type="string"/>
        <property description="" name="hyperLinkListener" required="true" type="java" typeSpecialization="com.onpositive.semantic.model.ui.generic.IHyperlinkListener"/>
    </element>
    <element allowsChilds="uielement" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="allows to create CTabFolder control" extends="container" group="controls" name="ctab-folder"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.PasswordElementHandler" description="allows to add password editor to the parent container" extends="string" group="controls" name="password"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.ObjectValueHandler" description="allows to specify binding with a given value" extends="binding" group="Model" name="objectValue">
        <property description="" name="class" required="true" type="java"/>
        <property description="" name="id" required="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Allows to insert browser control to the parent container" extends="uielement" group="controls" name="browser"/>
    <element allowsChilds="uielement" class="com.onpositive.semantic.ui.xml.HeadControlHandler" description="allows to specify container that should be placed inside head control of parent form" extends="" group="controls" name="headControl"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.ObjectContributionHandler" description="allows to contribute selection specific actions to the current action context " extends="contributionElement" group="Actions" isAbstract="true" name="objectContribution"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.MoveHandler" description="allows to add move up action to the parent viewer " extends="contributionElement" group="Actions" name="moveUp"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.MoveHandler" description="allows to add moveDown action to the parent viewer " extends="contributionElement" group="Actions" name="moveDown"/>
    <element allowsChilds="stringValue" class="com.onpositive.semantic.ui.xml.StringRealmHandler" description="allows to specify realm which contains string members for the given binding" extends="bindingMember" group="Model" name="realmDefinition"/>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.StringValueHandler" description="Allows to add string value to the realm" extends="" group="Model" name="stringMember">
        <property description="value to add" name="value" required="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.InterceptorHandler" description="allows to add action interceptor to the parent viewer" extends="" group="Settings" name="interceptor">
        <property description="" name="kind" required="true" type="string"/>
        <property description="" name="class" required="true" type="java"/>
    </element>
    <element allowsChilds="actions" class="com.onpositive.semantic.ui.xml.TreeSelectionHandler" description="Tree element allows to show a tree to the user" extends="list" group="controls" name="tree">
        <property description="property attribute should point on id of the property which will be used to determine children of the given element" name="property" required="true" type="string"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.DefaultValueHandler" description="allows to specify default value for the parent binding" extends="bindingMember" group="Model" name="defaultValue">
        <property description="" name="value" required="true" type="string"/>
        <property description="" name="type" type="java"/>
    </element>
    <element allowsChilds="setting" class="com.onpositive.semantic.ui.xml.UIElementHandler" description="Used for creating special color select button" extends="editor" group="controls" name="colorSelector"/>
    <element allowsChilds="uielement" class="com.onpositive.semantic.ui.xml.ScrollableHandler" description="Allows to create scrollable container" extends="container" group="controls" name="scrollable">
        <property description="if true scroll bars are visible despite of content preferred size" name="alwaysShowScrollBars" type="boolean"/>
    </element>
    <element allowsChilds="" class="com.onpositive.semantic.ui.xml.SeparatorHandler" description="Allows to add separator element to the parent container" extends="uielement" group="controls" name="separator">
        <property description="" name="vertical" required="true" type="boolean"/>
    </element>
</namespace>
