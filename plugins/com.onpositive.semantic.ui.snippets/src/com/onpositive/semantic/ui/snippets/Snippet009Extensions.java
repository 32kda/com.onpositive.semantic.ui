package com.onpositive.semantic.ui.snippets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.osgi.framework.Bundle;

import com.onpositive.commons.elements.AbstractUIElement;
import com.onpositive.commons.elements.Container;
import com.onpositive.commons.ui.appearance.OneElementOnLineLayouter;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.property.adapters.TextProviderAdapter;
import com.onpositive.semantic.model.binding.Binding;
import com.onpositive.semantic.model.realm.Realm;
import com.onpositive.semantic.model.tree.GroupingPointProvider;
import com.onpositive.semantic.model.tree.IClusterizationPointProvider;
import com.onpositive.semantic.model.ui.generic.DisposeBindingListener;
import com.onpositive.semantic.model.ui.property.editors.ButtonSelector;
import com.onpositive.semantic.model.ui.property.editors.IMayHaveCustomTooltipCreator;
import com.onpositive.semantic.model.ui.property.editors.structured.FilterControl;
import com.onpositive.semantic.model.ui.property.editors.structured.ListEnumeratedValueSelector;

public class Snippet009Extensions extends AbstractSnippet {

	private final class ExtensionViewerTextProvider extends TextProviderAdapter {

		
		public String getText(Object object) {
			if (object instanceof String) {
				return object.toString();
			}
			if (object instanceof IContributor) {
				final IContributor pa = (IContributor) object;
				return pa.getName();
			}
			final IConfigurationElement e = (IConfigurationElement) object;
			final String attribute = e.getAttribute("id"); //$NON-NLS-1$
			final String name = e.getName();
			final String uniqueIdentifier = name
					+ " " + (attribute != null ? attribute : ""); //$NON-NLS-1$ //$NON-NLS-2$
			return uniqueIdentifier;
		}

		
		public String getDescription(Object object) {
			if (object instanceof String) {
				return "<b>Extension Point:</b>" + object.toString(); //$NON-NLS-1$
			}
			if (object instanceof IContributor) {
				final IContributor pa = (IContributor) object;
				return "<b>Bundle:</b>" + pa.getName(); //$NON-NLS-1$
			}
			final IConfigurationElement e = (IConfigurationElement) object;
			String string = "<p><b>" + this.getText(object) + "</b></p><p>" + "<b>Defined in:</b>" + e.getContributor().getName() + "</p><p vspace=\"false\"><b>Point:</b>" + e.getDeclaringExtension().getExtensionPointUniqueIdentifier() + "</p>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			string += "<p><b>Attributes:</b></p>"; //$NON-NLS-1$
			for (final String s : e.getAttributeNames()) {
				string += "<p vspace=\"false\"><b>" + s + "</b>=\"" + e.getAttribute(s) + "\"</p>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			return string;
		}
	}

	private final class PluginProvider extends
			GroupingPointProvider<IConfigurationElement> {

		
		public Set<? extends Object> getGroup(IConfigurationElement o) {
			return Collections.singleton(o.getContributor());
		}

		
		public Object getPresentationObject(Object o) {
			return o;
		}
	}

	private final class ExtensionPointProvider extends
			GroupingPointProvider<IConfigurationElement> {

		
		public Set<String> getGroup(IConfigurationElement o) {
			return Collections.singleton(o.getDeclaringExtension()
					.getExtensionPointUniqueIdentifier());
		}

		
		public Object getPresentationObject(Object o) {
			return o;
		}
	}

	boolean groupByPlugin;
	boolean groupByExtension;
	boolean noGrouping = true;

	
	protected AbstractUIElement<?> createContent() {
		final Binding bnd = new Binding(this);
		final Binding bsa = new Binding(Bundle.class);
		final IExtensionPoint[] bundleGroupProviders = Platform
				.getExtensionRegistry().getExtensionPoints();
		final ArrayList<IConfigurationElement> s = new ArrayList<IConfigurationElement>();
		for (final IExtensionPoint b : bundleGroupProviders) {
			final IExtension[] extensions = b.getExtensions();
			for (final IExtension e : extensions) {
				s.addAll(Arrays.asList(e.getConfigurationElements()));
			}
		}
		bsa.setAdapter(ITextLabelProvider.class,
				new ExtensionViewerTextProvider());
		bsa.setRealm(new Realm<IConfigurationElement>(s));
		bsa.setMaxCardinality(1);
		final Container el = new Container();
		final OneElementOnLineLayouter el2 = new OneElementOnLineLayouter();
		el.setLayoutManager(el2);
		final FilterControl filterControl = new FilterControl();
		this.eviewer = new ListEnumeratedValueSelector<IConfigurationElement>(bsa);
		filterControl.setSelector(this.eviewer);
		this.eviewer.setLayoutData(new GridData(500, 500));
		el.add(filterControl);
		final Container buttons = new Container();
		buttons.setLayout(new GridLayout(3, false));
		final ButtonSelector selector = new ButtonSelector(bnd
				.getBinding("GroupByPlugin"), SWT.RADIO); //$NON-NLS-1$
		final ButtonSelector selector1 = new ButtonSelector(bnd
				.getBinding("GroupByExtension"), SWT.RADIO); //$NON-NLS-1$
		final ButtonSelector selector2 = new ButtonSelector(bnd
				.getBinding("NoGrouping"), SWT.RADIO); //$NON-NLS-1$
		this.eviewer.setElementRole("extensions"); //$NON-NLS-1$
		el.add(this.eviewer);
		selector.setCaption("Group by Plugin"); //$NON-NLS-1$
		selector1.setCaption("Group by Extension Point"); //$NON-NLS-1$
		selector2.setCaption("Does not group elements"); //$NON-NLS-1$
		buttons.add(selector);
		buttons.add(selector1);
		buttons.add(selector2);
		el.add(buttons);
		buttons
				.setLayoutData(GridDataFactory.swtDefaults().span(2, 1)
						.create());
		el2.excludeFromLayout(buttons);
		DisposeBindingListener.linkBindingLifeCycle(bsa, el);
		return el;
	}

	PluginProvider pprovider = new PluginProvider();
	ExtensionPointProvider eprovider = new ExtensionPointProvider();

	private ListEnumeratedValueSelector<IConfigurationElement> eviewer;

	@SuppressWarnings("unused")
	private void handleGrouping(IMayHaveCustomTooltipCreator<?> sl,
			ButtonSelector selector, ButtonSelector selector1) {

	}

	
	protected String getDescription() {
		return "This snippets shows Extensions that are known to platform"; //$NON-NLS-1$
	}

	
	protected String getName() {
		return "Filtering Extensions"; //$NON-NLS-1$
	}

	public boolean isGroupByPlugin() {
		return this.groupByPlugin;
	}

	public void setGroupByPlugin(boolean groupByPlugin) {
		this.groupByPlugin = groupByPlugin;
		this.refreshViewerGrouping();
	}

	public boolean isGroupByExtension() {
		return this.groupByExtension;
	}

	public void setGroupByExtension(boolean groupByExtension) {
		this.groupByExtension = groupByExtension;
		this.refreshViewerGrouping();
	}

	public boolean isNoGrouping() {
		return this.noGrouping;
	}

	public void setNoGrouping(boolean noGrouping) {
		this.noGrouping = noGrouping;
		this.refreshViewerGrouping();
	}

	private void refreshViewerGrouping() {
		if (this.groupByExtension) {			
			this.eviewer.setClusterizationPointProviders(null, new IClusterizationPointProvider[]{this.eprovider});
		} else if (this.groupByPlugin) {
			this.eviewer.setClusterizationPointProviders(null, new IClusterizationPointProvider[]{this.eprovider});
		} else {
			this.eviewer.setClusterizationPointProviders(null);
		}
		this.eviewer.setAsTree(this.groupByExtension || this.groupByPlugin);
	}

	
	public String getGroup() {
		return "Java"; //$NON-NLS-1$
	}
}