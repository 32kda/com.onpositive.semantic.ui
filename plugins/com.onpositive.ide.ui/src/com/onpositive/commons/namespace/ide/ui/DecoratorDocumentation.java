package com.onpositive.commons.namespace.ide.ui;

import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import com.onpositive.semantic.language.model.DocumentationContribution;
import com.onpositive.semantic.language.model.ModelElement;
import com.onpositive.semantic.model.api.decoration.DecorationContext;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;
import com.onpositive.semantic.model.tree.IClusterizationPoint;
import com.onpositive.semantic.model.tree.ITreeNode;
import com.onpositive.viewer.extension.coloring.IItemPaintParticipant;

public class DecoratorDocumentation implements IObjectDecorator,
		IItemPaintParticipant {

	static Image createFromImage = FieldDecorationRegistry.getDefault()
			.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();

	public DecoratorDocumentation() {
	}

	
	public void paint(Event event) {
		Object data2 = event.item.getData();
		if (data2 instanceof ITreeNode<?>) {
			ITreeNode<?> nm = (ITreeNode<?>) data2;
			data2 = nm.getElement();
			if (data2 instanceof IClusterizationPoint<?>) {
				IClusterizationPoint<?> p = (IClusterizationPoint<?>) data2;
				data2 = p.getPrimaryValue();
			}
		}
		if (data2 instanceof ModelElement) {
			final ModelElement data = (ModelElement) data2;
			final DocumentationContribution documentationContribution = data
					.getDocumentationContribution();
			if ((documentationContribution == null)
					|| documentationContribution.isEmpty()||data.getDescription()==null||data.getDescription().trim().length()==0) {
				if (event.item instanceof TableItem) {
					final TableItem ti = (TableItem) event.item;
					final Rectangle imageBounds = ti
							.getImageBounds(event.index);
					event.gc.drawImage(createFromImage, imageBounds.x + 1,
							imageBounds.y + 1);
				} else if (event.item instanceof TreeItem) {
					final TreeItem ti = (TreeItem) event.item;
					final Rectangle imageBounds = ti
							.getImageBounds(event.index);
					event.gc.drawImage(createFromImage, imageBounds.x + 1,
							imageBounds.y + 1);
				}
			}
		}
	}


	public Object decorate(DecorationContext parameterObject, Object text) {
		return null;
	}

}
