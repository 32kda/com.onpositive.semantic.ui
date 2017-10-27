package com.onpositive.semantic.model.ui.property.editors.structured.columns;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;

import com.onpositive.commons.SWTImageManager;
import com.onpositive.semantic.model.api.property.adapters.ITextLabelProvider;
import com.onpositive.semantic.model.api.roles.IImageDescriptorProvider;
import com.onpositive.semantic.model.api.roles.IStyledTextLabelProvider;
import com.onpositive.semantic.model.api.roles.StyledString;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.semantic.model.ui.generic.Column;
import com.onpositive.semantic.model.ui.generic.INodeLabelProvider;
import com.onpositive.semantic.model.ui.property.editors.structured.UniversalLabelProvider;
import com.onpositive.viewer.extension.coloring.IItemPaintParticipant;
import com.onpositive.viewer.extension.coloring.IOwnerDrawTableProvider;
import com.onpositive.viewer.extension.coloring.IOwnerSupport;
import com.onpositive.viewer.extension.coloring.ITableRichLabelProvider;
import com.onpositive.viewer.extension.coloring.OwnerDrawSupport;

public class TableLabelProvider extends UniversalLabelProvider<Object>
		implements ITableRichLabelProvider, IOwnerDrawTableProvider,
		IOwnerSupport {

	private WeakHashMap<Object, WeakReference<String>> cacheMap;
	private WeakHashMap<Object, Image> imageMap;

	private final ArrayList<WeakHashMap<Object, WeakReference<String>>> caches = new ArrayList<WeakHashMap<Object, WeakReference<String>>>();
	private final ArrayList<WeakHashMap<Object, Image>> imageCaches = new ArrayList<WeakHashMap<Object, Image>>();

	private final TableEnumeratedValueSelector owner;
	private String role;
	private String theme;

	public void clearCache() {
		for (final WeakHashMap<Object, WeakReference<String>> w : this.caches) {
			w.clear();

		}
		for (final WeakHashMap<Object, Image> w : this.imageCaches) {
			w.clear();
		}
	}

	public void clearCache(Object object) {
		for (final WeakHashMap<Object, WeakReference<String>> w : this.caches) {
			w.remove(object);

		}
		for (final WeakHashMap<Object, Image> w : this.imageCaches) {
			w.remove(object);
		}
	}

	TableLabelProvider(
			TableEnumeratedValueSelector abstractEnumeratedValueSelector,
			ITextLabelProvider pr) {
		super(abstractEnumeratedValueSelector, pr);
		this.owner = abstractEnumeratedValueSelector;
	}

	protected void cache(Object element, String string) {
		if (this.cacheMap != null) {
			this.cacheMap.put(element, new WeakReference<String>(string));
		}
	}

	protected Image getCachedImage(Object element) {
		final Image image = this.imageMap.get(element);
		return image;
	}

	protected void cacheImage(Object element, Image actualImage) {
		this.imageMap.put(element, actualImage);
	}

	protected WeakReference<String> getFromCache(Object element) {
		if (this.cacheMap != null) {
			return this.cacheMap.get(element);
		}
		return null;
	}

	public void update(ViewerCell cell) {
		final int ci = cell.getColumnIndex();
		int size = this.caches.size();
		while (ci >= size) {
			this.caches.add(new WeakHashMap<Object, WeakReference<String>>());
			this.imageCaches.add(new WeakHashMap<Object, Image>());
			size++;
		}
		this.cacheMap = this.caches.get(ci);
		this.imageMap = this.imageCaches.get(ci);
		if (ci >= this.owner.getColumnCount()) {
			return;
		}
		final Column column = this.owner.getColumn(ci);

		this.role = column.getRole();
		this.theme = column.getTheme();

		final Object item = cell.getElement();

		final Object element = column.getElement(item);
		// if (column.isTextFromBase()) {
		// cell.setText(super.getText(item));
		// } else {
		// cell.setText(super.getText(element));
		// }
		Image im = null;
		if (owner.isImageOnFirstColumn() && ci == 0) {
			im = this.getImage(item);
		} else if (column.hasImage()) {

			if (!column.isImageFromBase()) {
				IImageDescriptorProvider imageProvider = column.getImageProvider();				
				if (item instanceof ITreeNode<?>) {
					final ITreeNode node = (ITreeNode<?>) item;
					final INodeLabelProvider adapter = node
							.getAdapter(INodeLabelProvider.class);
					if (adapter != null) {
						final Image image = SWTImageManager.getImage(adapter.getImage(node, element,
								this.role, this.theme, column));
						if (image != null) {
							im = image;
						} else {
							if (imageProvider != null) {
								im = SWTImageManager
										.getImage(imageProvider
												.getImageDescriptor(element));
							} else {
								im = this.getImage(element);
							}
						}
					} else {
						if (imageProvider != null) {
							im = SWTImageManager.getImage(imageProvider
									.getImageDescriptor(element));
						} else {
							im = this.getImage(element);
						}
					}
				} else {
					if (imageProvider != null) {
						im = SWTImageManager.getImage(imageProvider
								.getImageDescriptor(element));
					} else {
						im = this.getImage(element);
					}
				}
			} else {
				IImageDescriptorProvider imDescriptorProvider=column.getImageProvider();
				if (imDescriptorProvider != null) {
					im = SWTImageManager.getImage(imDescriptorProvider
							.getImageDescriptor(element));
				} else {
					im = this.getImage(item);
				}
			}
		}
		cell.setImage(im);
		cell.setBackground(this.getBackground(item));
		cell.setForeground(this.getForeground(item));
		cell.setFont(this.getFont(item));
	}

	public String getText(Object element) {
		final StringBuilder bld = new StringBuilder();
		for (int a = 0; a < this.owner.getColumnCount(); a++) {
			final Column column = this.owner.getColumn(a);
			final Object item = column.getElement(element);
			bld.append(super.getText(item));
			bld.append(' ');
		}
		return bld.toString();
	}

	protected String getRole() {
		return this.role;
	}

	protected String getTheme() {
		return this.theme;
	}

	public StyledString getRichTextLabel(Object object, int index) {
		final int ci = index;
		if (ci >= this.owner.getColumnCount()) {
			return null;
		}
		final Column column = this.owner.getColumn(ci);
		if (!column.hasText()) {
			return null;
		}

		ITextLabelProvider labelProvider = column.getLabelProvider();
		if (labelProvider != null) {
			if (labelProvider instanceof IStyledTextLabelProvider) {
				IStyledTextLabelProvider ta = (IStyledTextLabelProvider) labelProvider;
				return ta.getRichTextLabel(object);
			}
			return new StyledString(labelProvider.getText(object));
		}
		final Object element = column.getElement(object);
		if (object instanceof ITreeNode<?>) {
			final ITreeNode<?> node = (ITreeNode<?>) object;
			final INodeLabelProvider adapter = node
					.getAdapter(INodeLabelProvider.class);
			if (adapter != null) {
				final StyledString richText = adapter.getRichText(node,
						element, this.role, this.theme, column);
				if (richText != null) {
					return richText;
				}
			}
		}
		this.role = column.getRole();
		this.theme = column.getTheme();
		if (column.isTextFromBase()) {
			final StyledString richTextLabel = super.getRichTextLabel(object);
			return richTextLabel;
		}
		final StyledString richTextLabel = super.getRichTextLabel(element);
		return richTextLabel;
	}

	public IItemPaintParticipant getParticipant(Object object, int index) {
		final int ci = index;
		if (ci >= this.owner.getColumnCount()) {
			return null;
		}
		final Column column = this.owner.getColumn(ci);
		this.role = column.getRole();
		this.theme = column.getTheme();
		final Object element = column.getElement(object);
		return this.getParticipant(element);
	}

	public boolean doPaint(OwnerDrawSupport support, Event event) {
		int index = event.index;
		final int ci = index;
		if (ci >= this.owner.getColumnCount()) {
			return false;
		}
		final Column column = this.owner.getColumn(ci);
		
		IColumnRenderer renderer = (IColumnRenderer) column.getRenderer();
		if (renderer != null) {
			renderer.drawColumn(support, event);
			return true;
		}
		return false;
	}
}
