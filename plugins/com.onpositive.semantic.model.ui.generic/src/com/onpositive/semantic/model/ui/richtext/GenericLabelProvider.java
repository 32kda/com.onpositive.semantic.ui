package com.onpositive.semantic.model.ui.richtext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.onpositive.semantic.model.api.decoration.DecorationContext;
import com.onpositive.semantic.model.api.decoration.IObjectDecorator;
import com.onpositive.semantic.model.api.decoration.ITextDecorator;
import com.onpositive.semantic.model.api.labels.ITextLabelProvider;
import com.onpositive.semantic.model.api.labels.LabelAccess;
import com.onpositive.semantic.model.api.meta.BaseMeta;
import com.onpositive.semantic.model.api.meta.MetaAccess;
import com.onpositive.semantic.model.ui.generic.widgets.IListElement;
import com.onpositive.semantic.model.ui.roles.IImageDescriptorDecorator;
import com.onpositive.semantic.model.ui.roles.ImageDescriptor;
import com.onpositive.semantic.model.ui.roles.ImageManager;
import com.onpositive.semantic.model.ui.roles.LabelProvider;
import com.onpositive.semantic.model.ui.roles.ObjectDecoratorDescriptor;
import com.onpositive.semantic.model.ui.roles.ObjectDecoratorManager;

public class GenericLabelProvider extends BaseMeta{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final static Object getPresentation(Object element) {
		return LabelAccess.getPresentationObject(element);
	}
	DecorationContext context = new DecorationContext();
	
	private ITextLabelProvider labelProvider;
	
	public ITextLabelProvider getLabelProvider() {
		return labelProvider;
	}

	public void setLabelProvider(ITextLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public ImageDescriptor getActualImage(Object element,IListElement<?>ls,boolean returnDefault) {

		element = getPresentation(element);
		final String rowRole = ls!=null?ls.getElementRole():null;

		final String theme2 = ls!=null?ls.getTheme():null;
		com.onpositive.semantic.model.ui.roles.ImageDescriptor imageDescriptor = ImageManager.getInstance().getImageDescriptor(
				element, rowRole, theme2);
		if (imageDescriptor==null&&returnDefault){
			imageDescriptor = ls!=null?ls.getDefaultImageDescriptor():null;			
		}
		if (imageDescriptor == null) {			
			return null;
		}
		
		this.context.object = element;
		this.context.role = rowRole;
		this.context.theme = theme2;
		if (ls != null) {
			this.context.binding = ls
					.getBinding();
		}
		final ArrayList<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(element, rowRole, theme2);

		for (int a = 0; a < decorators.size(); a++) {
			final ObjectDecoratorDescriptor d = decorators.get(a);
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof IImageDescriptorDecorator) {
				if (true) {
					final IImageDescriptorDecorator ds = (IImageDescriptorDecorator) decorator;
					imageDescriptor = ds
							.decorate(this.context, imageDescriptor);
				}
			}
		}
		if (ls != null) {
			final Collection<IObjectDecorator<?>> decorators2 = ls.getDecorators();

			if (decorators2 != null) {
				for (final IObjectDecorator d : decorators2) {
					if (d instanceof IImageDescriptorDecorator) {
						final IImageDescriptorDecorator ds = (IImageDescriptorDecorator) d;
						imageDescriptor = ds.decorate(this.context,
								imageDescriptor);
					}
				}
			}
		}		
		return imageDescriptor;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public StyledString getRichTextLabel(Object object,IListElement<?>ls) {
		if (object instanceof Collection) {
			final Collection<Object> cm = (Collection<Object>) object;
			final StyledString sm = new StyledString();
			int a = 0;
			final int i = cm.size() - 1;
			final ArrayList<StyledString> rs = new ArrayList<StyledString>();
			for (final Object o : cm) {
				final StyledString richTextLabel = this.getRichTextLabel(o,ls);
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
		if ((this.labelProvider != null)
				&& !(this.labelProvider instanceof LabelProvider)) {
			if (this.labelProvider instanceof IRichLabelProvider) {
				styledString = ((IRichLabelProvider) this.labelProvider)
						.getRichTextLabel(object);
			}
			styledString = new StyledString(this.labelProvider.getText(
					ls==null?MetaAccess.getMeta(object): ls.getMeta(),
							ls==null?null:ls.getParentObject(), object));
		} else {
			styledString = RichLabelAccess.getLabel(
					ls!=null?ls.getMeta():null,
					ls!=null?ls.getParentObject():null, object);

		}
		final String rowRole = getRole(ls);
		final String theme2 = getTheme(ls);
		this.context.object = object;
		this.context.role = rowRole;
		this.context.theme = theme2;
		if (ls != null) {
			this.context.binding = ls
					.getBinding();
		}
		final Collection<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(object, rowRole, theme2);
		for (final ObjectDecoratorDescriptor d : decorators) {
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof IRichTextDecorator) {
				if (true) {
					final IRichTextDecorator ds = (IRichTextDecorator) decorator;
					styledString = ds.decorate(this.context, styledString);
				}
			}
		}
		final Collection<IObjectDecorator> decorators2 = ls != null ? ls.getDecorators()
				: new HashSet();
		if (decorators2 != null) {
			for (final IObjectDecorator d : decorators2) {
				if (d instanceof IRichTextDecorator) {
					{
						final IRichTextDecorator ds = (IRichTextDecorator) d;
						styledString = ds.decorate(this.context, styledString);
					}
				}
			}
		}
		return styledString;
	}
	
	public String getText(Object element,IListElement<?>ls) {
		String string;
		final Object object = getPresentation(element);
		if ((this.labelProvider != null)
				&& !(this.labelProvider instanceof LabelProvider)) {
			string = this.labelProvider.getText(
					ls.getMeta(),
					ls.getParentObject(), object);

		} else {
			string = LabelAccess.getLabel(
					ls.getMeta(),
					ls.getParentObject(), object);
		}
		final String rowRole = getRole(ls);
		this.context.object = element;
		this.context.role = rowRole;

		final String theme2 = getTheme(ls);
		this.context.theme = theme2;
		if (ls != null) {
			this.context.binding = ls
					.getBinding();
		}
		final Collection<ObjectDecoratorDescriptor> decorators = ObjectDecoratorManager
				.getInstance().getElements(object, rowRole, theme2);
		for (final ObjectDecoratorDescriptor d : decorators) {
			final IObjectDecorator decorator = d.getDecorator();
			if (decorator instanceof ITextDecorator) {
				if (true) {
					final ITextDecorator ds = (ITextDecorator) decorator;
					string = ds.decorate(this.context, string);
				}
			}
		}
		final Collection<IObjectDecorator<?>> decorators2 = (ls == null ?null: ls.getDecorators());
		if (decorators2 != null) {
			for (final IObjectDecorator d : decorators2) {
				if (d instanceof ITextDecorator) {
					{
						final ITextDecorator ds = (ITextDecorator) d;
						string = ds.decorate(this.context, string);
					}
				}

			}
		}
		return string;
	}


	protected String getTheme(IListElement<?> ls) {
		if (ls==null){
			return null;
		}
		return ls.getTheme();
	}

	protected String getRole(IListElement<?> ls) {
		if (ls==null){
			return null;
		}
		return ls.getElementRole();
	}
}
