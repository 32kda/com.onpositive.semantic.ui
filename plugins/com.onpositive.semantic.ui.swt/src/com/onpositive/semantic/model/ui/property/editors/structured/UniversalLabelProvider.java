/**
 * 
 */
package com.onpositive.semantic.model.ui.property.editors.structured;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.WeakHashMap;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.common.ui.roles.TooltipManager;
import com.onpositive.semantic.common.ui.roles.TooltipObject;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.roles.DecorationContext;
import com.onpositive.semantic.model.api.roles.IImageDescriptorDecorator;
import com.onpositive.semantic.model.api.roles.IObjectDecorator;
import com.onpositive.semantic.model.api.roles.IRichLabelProvider;
import com.onpositive.semantic.model.api.roles.IRichTextDecorator;
import com.onpositive.semantic.model.api.roles.IStyledTextLabelProvider;
import com.onpositive.semantic.model.api.roles.ITextDecorator;
import com.onpositive.semantic.model.api.roles.ImageManager;
import com.onpositive.semantic.model.api.roles.ImageObject;
import com.onpositive.semantic.model.api.roles.LabelManager;
import com.onpositive.semantic.model.api.roles.ObjectDecoratorDescriptor;
import com.onpositive.semantic.model.api.roles.ObjectDecoratorManager;
import com.onpositive.semantic.model.api.roles.StyledString;
import com.onpositive.semantic.model.ui.generic.IRowStyleProvider;
import com.onpositive.viewer.extension.coloring.IItemPaintParticipant;
import com.onpositive.viewer.extension.coloring.IOwnerDrawLabelProvider;
import com.onpositive.viewer.extension.coloring.OwnerDrawSupport;

public class UniversalLabelProvider<T> extends ColumnLabelProvider implements
		IRichLabelProvider, IOwnerDrawLabelProvider{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.CellLabelProvider#update(org.eclipse.jface.
	 * viewers.ViewerCell)
	 */
	public void update(ViewerCell cell) {

		cell.getItem().setData(OwnerDrawSupport.NEEDS_REFRESH, this);
		final Object element = cell.getElement();
		cell.setText(this.getText(element));
		// cell.setImage(getImage(element));
		cell.setBackground(this.getBackground(element));
		cell.setForeground(this.getForeground(element));
		cell.setFont(this.getFont(element));
	}

	/**
	 * 
	 */
	protected final AbstractEnumeratedValueSelector<T> abstractEnumeratedValueSelector;

	public String getToolTipText(Object element) {
		element = getPresentation(element);
		if ((this.labelProvider != null) && !LabelManager.isDefault(this.labelProvider)) {
			return this.labelProvider.getDescription(element);
		}
		return LabelManager.getInstance().getDescription(element,
				this.getRole(), this.getTheme());
	}

	public final static Object getPresentation(Object element) {		
		return LabelManager.getPresentation(element);
	}

	public boolean useNativeToolTip(Object object) {
		object = getPresentation(object);
		final TooltipObject tooltipObject = TooltipManager.getInstance()
				.getTooltipObject(object, this.getRole(), this.getTheme());
		return tooltipObject != null ? tooltipObject.useNativeTooltip() : false;
	}

	public int getToolTipDisplayDelayTime(Object object) {
		object = getPresentation(object);
		final TooltipObject tooltipObject = TooltipManager.getInstance()
				.getTooltipObject(object, this.getRole(), this.getTheme());
		return tooltipObject != null ? tooltipObject.getTooltipVisibilityTime()
				: 0;
	}

	public int getToolTipTimeDisplayed(Object object) {
		object = getPresentation(object);
		final TooltipObject tooltipObject = TooltipManager.getInstance()
				.getTooltipObject(object, this.getRole(), this.getTheme());
		return tooltipObject != null ? tooltipObject.getTooltipVisibilityTime()
				: 5000;
	}

	private final ITextLabelProvider labelProvider;

	public UniversalLabelProvider(
			AbstractEnumeratedValueSelector<T> abstractEnumeratedValueSelector,
			ITextLabelProvider pr) {
		this.abstractEnumeratedValueSelector = abstractEnumeratedValueSelector;
		this.labelProvider = pr;
	}

	public UniversalLabelProvider() {
		labelProvider = null;
		this.abstractEnumeratedValueSelector = null;
	}

	private WeakHashMap<Object, WeakReference<String>> cache = new WeakHashMap<Object, WeakReference<String>>();
	private final WeakHashMap<Object, Image> cacheImage = new WeakHashMap<Object, Image>();

	public void clearCache(Object object) {
		this.cache.remove(object);
		this.cacheImage.remove(object);
	}

	public void clearCache() {
		this.cache = new WeakHashMap<Object, WeakReference<String>>();
		this.cacheImage.clear();
	}

	public String getText(Object element) {

		final WeakReference<String> weakReference = this.getFromCache(element);
		if (weakReference != null) {
			final String string = weakReference.get();
			if (string != null) {
				return string;
			}
		}
		if (true) {
			return this.internalCountText(element);
		}
		return this.getRichTextLabel(element).getString();
	}

	protected WeakReference<String> getFromCache(Object element) {
		return this.cache.get(element);
	}

	private String internalCountText(Object element) {
		if (element instanceof Collection) {
			final StringBuilder bld = new StringBuilder();
			final Collection<?> cm = (Collection<?>) element;
			if (cm.size() > 0) {
				final ArrayList<String> sm = new ArrayList<String>();
				for (final Object o : cm) {
					sm.add(this.internalCountText(o));
				}
				Collections.sort(sm);
				for (final Object o : sm) {
					bld.append(o);
					bld.append(',');
				}
				return bld.substring(0, bld.length() - 1);
			}
			return "";
		}
		String string;
		final Object object = getPresentation(element);
		if ((this.labelProvider != null) && !LabelManager.isDefault(this.labelProvider)) {
			string = this.labelProvider.getText(object);

		} else {
			string = LabelManager.getInstance().getText(object, this.getRole(),
					this.getTheme());
		}
		final String rowRole = this.getRole();
		this.context.object = element;
		this.context.role = rowRole;

		final String theme2 = this.getTheme();
		this.context.theme = theme2;
		if (abstractEnumeratedValueSelector != null) {
			this.context.binding = this.abstractEnumeratedValueSelector
					.getBinding();
		}
		final Collection<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(object, rowRole, theme2);
		for (final ObjectDecoratorDescriptor d : decorators) {
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof ITextDecorator) {
				if (this.isEnabled(d)) {
					final ITextDecorator ds = (ITextDecorator) decorator;
					string = ds.decorateText(this.context, string);
				}
			}
		}
		final HashSet<IObjectDecorator> decorators2 = this.abstractEnumeratedValueSelector == null ? new HashSet()
				: abstractEnumeratedValueSelector.decorators;
		if (decorators2 != null) {
			for (final IObjectDecorator d : decorators2) {
				if (d instanceof ITextDecorator) {
					{
						final ITextDecorator ds = (ITextDecorator) d;
						string = ds.decorateText(this.context, string);
					}
				}

			}
		}
		this.cache(element, string);
		return string;
	}

	boolean useCache;

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean cache) {
		this.useCache = cache;
		if (!useCache) {
			this.cache.clear();
		}
	}

	protected void cache(Object element, String string) {
		if (useCache) {
			final WeakReference<String> s = new WeakReference<String>(string);
			this.cache.put(element, s);
		}
	}

	public Image getImage(Object element) {
		final Image image = this.getCachedImage(element);
		if (image != null) {
			return image;
		}
		final Image actualImage = this.getActualImage(element);
		this.cacheImage(element, actualImage);
		return actualImage;
	}

	protected Image getCachedImage(Object element) {
		final Image image = this.cacheImage.get(element);
		return image;
	}

	protected void cacheImage(Object element, Image actualImage) {
		this.cacheImage.put(element, actualImage);
	}

	public Image getActualImage(Object element) {

		element = getPresentation(element);
		final String rowRole = this.getRole();

		final String theme2 = this.getTheme();
		com.onpositive.semantic.model.api.roles.ImageDescriptor imageDescriptor;
		ImageObject imageObject = ImageManager.getInstance().getImageObject(
				element, rowRole, theme2);
		if (imageObject == null) {
			return null;
		}
		imageDescriptor = imageObject.getImageDescriptor(element);
		this.context.object = element;
		this.context.role = rowRole;
		this.context.theme = theme2;
		if (abstractEnumeratedValueSelector != null) {
			this.context.binding = this.abstractEnumeratedValueSelector
					.getBinding();
		}
		final ArrayList<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(element, rowRole, theme2);

		for (int a = 0; a < decorators.size(); a++) {
			final ObjectDecoratorDescriptor d = decorators.get(a);
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof IImageDescriptorDecorator) {
				if (this.isEnabled(d)) {
					final IImageDescriptorDecorator ds = (IImageDescriptorDecorator) decorator;
					imageDescriptor = ds.decorateImageDescriptor(
							imageDescriptor, this.context);
				}
			}
		}
		if (this.abstractEnumeratedValueSelector != null) {
			final HashSet<IObjectDecorator> decorators2 = this.abstractEnumeratedValueSelector.decorators;

			if (decorators2 != null) {
				for (final IObjectDecorator d : decorators2) {
					if (d instanceof IImageDescriptorDecorator) {
						final IImageDescriptorDecorator ds = (IImageDescriptorDecorator) d;
						imageDescriptor = ds.decorateImageDescriptor(
								imageDescriptor, this.context);
					}
				}
			}
		}
		return SWTImageManager.getImage(imageDescriptor);
	}

	private boolean isEnabled(ObjectDecoratorDescriptor d) {
		return true;
	}

	DecorationContext context = new DecorationContext();
	private IRowStyleProvider rowStyleProvider;

	@SuppressWarnings("unchecked")
	public StyledString getRichTextLabel(Object object) {
		if (object instanceof Collection) {
			final Collection<Object> cm = (Collection<Object>) object;
			final StyledString sm = new StyledString();
			int a = 0;
			final int i = cm.size() - 1;
			final ArrayList<StyledString> rs = new ArrayList<StyledString>();
			for (final Object o : cm) {
				final StyledString richTextLabel = this.getRichTextLabel(o);
				rs.add(richTextLabel);
			}
			Collections.sort(rs);
			for (final StyledString richTextLabel : rs) {
				sm.append(richTextLabel);
				if (a != i) {
					sm.append(',');
				}
				a++;
			}
			return sm;
		}
		object = getPresentation(object);

		StyledString styledString = null;
		if ((this.labelProvider != null) && !LabelManager.isDefault(this.labelProvider)) {
			if (this.labelProvider instanceof IRichLabelProvider) {
				styledString = ((IRichLabelProvider) this.labelProvider)
						.getRichTextLabel(object);
			} else if (this.labelProvider instanceof IStyledTextLabelProvider) {
				styledString = ((IStyledTextLabelProvider) this.labelProvider)
						.getRichTextLabel(object);
			}
			styledString = new StyledString(this.labelProvider.getText(object));
		} else {
			final String elementRole = this.getRole();
			styledString = LabelManager.getInstance().getRichTextLabel(object,
					elementRole, this.getTheme());

		}
		final String rowRole = this.getRole();
		final String theme2 = this.getTheme();
		this.context.object = object;
		this.context.role = rowRole;
		this.context.theme = theme2;
		if (abstractEnumeratedValueSelector != null) {
			this.context.binding = this.abstractEnumeratedValueSelector
					.getBinding();
		}
		final Collection<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(object, rowRole, theme2);
		for (final ObjectDecoratorDescriptor d : decorators) {
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof IRichTextDecorator) {
				if (this.isEnabled(d)) {
					final IRichTextDecorator ds = (IRichTextDecorator) decorator;
					styledString = ds.decorateRichText(this.context,
							styledString);
				}
			}
		}
		final HashSet<IObjectDecorator> decorators2 = this.abstractEnumeratedValueSelector != null ? abstractEnumeratedValueSelector.decorators
				: new HashSet();
		if (decorators2 != null) {
			for (final IObjectDecorator d : decorators2) {
				if (d instanceof IRichTextDecorator) {
					{
						final IRichTextDecorator ds = (IRichTextDecorator) d;
						styledString = ds.decorateRichText(this.context,
								styledString);
					}
				}
			}
		}
		return styledString;
	}

	protected String getRole() {
		if (abstractEnumeratedValueSelector == null) {
			return "";
		}
		return this.abstractEnumeratedValueSelector.getElementRole();
	}

	protected String getTheme() {
		if (abstractEnumeratedValueSelector == null) {
			return "";
		}
		return this.abstractEnumeratedValueSelector.getTheme();
	}

	public Font getFont(Object element) {
		if (rowStyleProvider != null) {
			return JFaceResources.getFont(rowStyleProvider.getFont(element));
		}
		return super.getFont(element);
	}

	public Color getBackground(Object element) {
		if (rowStyleProvider != null) {
			return JFaceResources.getColorRegistry().get(rowStyleProvider.getBackground(element));
		}
		return super.getBackground(element);
	}

	public IItemPaintParticipant getParticipant(Object object) {
		final String rowRole = this.getRole();
		final String theme2 = this.getTheme();
		object = getPresentation(object);
		final Collection<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(object, rowRole, theme2);
		IItemPaintParticipant result = null;
		for (final ObjectDecoratorDescriptor d : decorators) {
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof IItemPaintParticipant) {
				if (this.isEnabled(d)) {
					if (result == null) {
						result = (IItemPaintParticipant) decorator;
					} else {
						final IItemPaintParticipant prev = result;
						final IItemPaintParticipant ps = new IItemPaintParticipant() {

							public void paint(Event event) {
								prev.paint(event);
								((IItemPaintParticipant) decorator)
										.paint(event);
							}

						};
						result = ps;
					}
				}
			}
		}
		return result;
	}

	public void setRowStyleProvider(IRowStyleProvider rowStyleProvider) {
		this.rowStyleProvider = rowStyleProvider;
	}

	
}